using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using OrderDispatch.Application.Menu;
using OrderDispatch.Application.Delivery;
using OrderDispatch.Domain.Entities;
using OrderDispatch.Infrastructure;

namespace OrderDispatch.Api.Orders;

[ApiController]
[Route("orders")]
public class OrdersController : ControllerBase
{
    private readonly AppDbContext _db;
    private readonly IMenuClient _menu;
    private readonly IDeliveryClient _delivery;

    public OrdersController(AppDbContext db, IMenuClient menu, IDeliveryClient delivery)
    {
        _db = db;
        _menu = menu;
        _delivery = delivery;
    }

    // --- 1. СПИСОК ЗАМОВЛЕНЬ (ОНОВЛЕНО) ---
    [HttpGet]
    public async Task<ActionResult<IEnumerable<OrderResponse>>> List(
        [FromQuery] string? type,
        [FromQuery] int? waiterId,
        [FromQuery] bool? onlyFree,
        [FromQuery] bool? activeOnly, // <--- НОВИЙ ПАРАМЕТР
        CancellationToken ct)
    {
        var query = _db.Orders
            .AsNoTracking()
            .OrderByDescending(x => x.CreatedAt)
            .Include(x => x.Items)
            .AsQueryable();

        // 1. Фільтр типу
        if (!string.IsNullOrEmpty(type) && Enum.TryParse<OrderType>(type, true, out var orderType))
        {
            query = query.Where(x => x.Type == orderType);

            if (orderType == OrderType.DineIn)
            {
                if (onlyFree == true) query = query.Where(x => x.WaiterId == null);
                else if (waiterId.HasValue) query = query.Where(x => x.WaiterId == waiterId.Value);
            }
        }

        // 2. --- ФІЛЬТР АКТУАЛЬНОСТІ (ДЛЯ КУХАРЯ І ВСІХ ІНШИХ) ---
        // Якщо activeOnly=true, або якщо параметри взагалі не передані (випадок Кухаря),
        // то ми не хочемо бачити замовлення за минулий місяць.
        // Давай показувати тільки ті, що створені за останні 24 години, АБО ще не мають статусу "completed".

        // Логіка: (Створено недавно) АБО (Ще не завершено)
        // Це гарантує, що старі "висяки" не прийдуть, якщо вони закриті.
        // А якщо вони "висять" відкритими з минулого року - ну, тоді треба їх закрити адміном або скриптом.

        // Для простоти, давай фільтрувати за датою для всіх запитів без фільтрів.
        if (activeOnly == true || (string.IsNullOrEmpty(type) && waiterId == null))
        {
            // Беремо замовлення тільки за останні 24 години
            var yesterday = DateTimeOffset.UtcNow.AddHours(-24);
            query = query.Where(x => x.CreatedAt >= yesterday);
        }

        var orders = await query.ToListAsync(ct);
        return Ok(orders.Select(ToDto));
    }

    // --- 2. GET ONE ---
    [HttpGet("{id:guid}")]
    public async Task<ActionResult<OrderResponse>> Get(Guid id, CancellationToken ct)
    {
        var o = await _db.Orders
            .Include(x => x.Items)
            .FirstOrDefaultAsync(x => x.Id == id, ct);

        if (o is null) return NotFound();
        return Ok(ToDto(o));
    }

    // --- 3. PAY ---
    [HttpPost("{id:guid}/pay")]
    public async Task<IActionResult> Pay(Guid id, CancellationToken ct)
    {
        var order = await _db.Orders.FirstOrDefaultAsync(x => x.Id == id, ct);
        if (order is null) return NotFound();
        if (order.IsPaid) return BadRequest("Order is already paid.");

        order.IsPaid = true;
        order.PaidAt = DateTimeOffset.UtcNow;

        await _db.SaveChangesAsync(ct);

        if (order.Type == OrderType.Delivery)
        {
            await _delivery.MarkOrderAsPaidAsync(order.Id, ct);
        }
        return NoContent();
    }

    // --- 4. CREATE ---
    [HttpPost]
    public async Task<ActionResult<OrderResponse>> Create([FromBody] CreateOrderRequest req, CancellationToken ct)
    {
        if (req.Items is null || req.Items.Count == 0)
            return BadRequest("Items are required.");

        if (req.Type == OrderType.Delivery)
        {
            if (string.IsNullOrEmpty(req.Address) || string.IsNullOrEmpty(req.Phone))
                return BadRequest("Address/Phone required for Delivery.");
        }

        var order = new Order
        {
            TableNo = req.TableNo,
            Status = "new",
            Type = req.Type,
            DeliveryAddress = req.Address,
            ClientPhone = req.Phone,
            ClientName = req.ClientName
        };

        foreach (var i in req.Items)
        {
            var dish = await _menu.GetDishAsync(i.DishId, ct);
            if (dish is null) return BadRequest($"Dish {i.DishId} not found");

            var item = new OrderItem
            {
                DishId = dish.Id,
                DishTitle = dish.Title,
                Price = dish.Price,
                Qty = i.Qty,
                StationId = dish.StationId,
                Status = OrderItemStatus.Pending
            };
            order.Items.Add(item);
            order.Total += item.Price * item.Qty;
        }

        _db.Orders.Add(order);
        await _db.SaveChangesAsync(ct);

        if (order.Type == OrderType.Delivery)
        {
            await _delivery.CreateDeliveryRequestAsync(
                order.Id,
                order.DeliveryAddress!,
                order.ClientPhone!,
                order.ClientName ?? "Unknown",
                order.IsPaid,
                order.Total,
                ct);
        }

        return Ok(ToDto(order));
    }

    // --- 5. UPDATE ITEM STATUS ---
    [HttpPatch("items/{itemId:int}/status")]
    public async Task<IActionResult> UpdateItemStatus(int itemId, [FromBody] UpdateItemStatusRequest req, CancellationToken ct)
    {
        var item = await _db.OrderItems.Include(i => i.Order).FirstOrDefaultAsync(i => i.Id == itemId, ct);
        if (item is null) return NotFound();

        item.Status = req.Status;

        var parentOrder = item.Order!;
        var allItemsCount = await _db.OrderItems.CountAsync(i => i.OrderId == parentOrder.Id, ct);
        var readyItemsCount = await _db.OrderItems.CountAsync(i => i.OrderId == parentOrder.Id && i.Status == OrderItemStatus.Ready, ct);

        // Враховуємо поточну зміну (вона ще не в базі як Ready, якщо ми її тільки ставимо)
        bool isNowFullyReady = (req.Status == OrderItemStatus.Ready) && (readyItemsCount + 1 == allItemsCount);

        if (isNowFullyReady)
        {
            parentOrder.Status = "ready";
            if (parentOrder.Type == OrderType.Delivery)
            {
                await _delivery.MarkOrderAsReadyAsync(parentOrder.Id, ct);
            }
        }

        await _db.SaveChangesAsync(ct);
        return NoContent();
    }

    // --- 6. НОВИЙ МЕТОД: ASSIGN (Взяти замовлення) ---
    [HttpPost("{id:guid}/assign")]
    public async Task<IActionResult> Assign(Guid id, [FromQuery] int waiterId, CancellationToken ct)
    {
        var order = await _db.Orders.FirstOrDefaultAsync(x => x.Id == id, ct);
        if (order is null) return NotFound();

        // Перевіряємо, чи столик ще вільний
        if (order.WaiterId != null && order.WaiterId != waiterId)
        {
            return BadRequest("This order is already taken.");
        }

        order.WaiterId = waiterId;

        // Якщо статус був "new", міняємо на "inprogress", щоб показати що над ним працюють
        if (order.Status == "new")
        {
            order.Status = "inprogress";
        }

        await _db.SaveChangesAsync(ct);
        return Ok(ToDto(order));
    }

    // --- 7. НОВИЙ МЕТОД: COMPLETE (Завершити) ---
    [HttpPost("{id:guid}/complete")]
    public async Task<IActionResult> Complete(Guid id, CancellationToken ct)
    {
        var order = await _db.Orders.FirstOrDefaultAsync(x => x.Id == id, ct);
        if (order is null) return NotFound();

        if (!order.IsPaid)
        {
            return BadRequest("Cannot complete unpaid order.");
        }

        order.Status = "completed";
        await _db.SaveChangesAsync(ct);
        return NoContent();
    }

    static OrderResponse ToDto(Order o) => new(
        o.Id,
        o.TableNo,
        o.Status,
        o.Type.ToString(),
        o.Total,
        o.CreatedAt,
        o.DeliveryAddress,
        o.ClientPhone,
        o.ClientName,
        o.IsPaid,
        o.PaidAt,
        o.WaiterId, // <--- Мапимо нове поле
        o.Items.Select(i => new OrderItemResponse(
            i.Id,
            i.DishId,
            i.DishTitle,
            i.Qty,
            i.Price,
            i.StationId,
            i.Status.ToString()
        )).ToList()
    );
}