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

    // --- 1. ОТРИМАННЯ СПИСКУ ЗАМОВЛЕНЬ (Оновлений метод) ---
    [HttpGet]
    public async Task<ActionResult<IEnumerable<OrderResponse>>> List([FromQuery] string? type, CancellationToken ct)
    {
        // 1. Починаємо формувати запит
        var query = _db.Orders
            .AsNoTracking()
            .OrderByDescending(x => x.CreatedAt)
            .Include(x => x.Items)
            .AsQueryable();

        // 2. Якщо клієнт передав параметр "type" (наприклад, "DineIn"), додаємо фільтр
        if (!string.IsNullOrEmpty(type) && Enum.TryParse<OrderType>(type, true, out var orderType))
        {
            query = query.Where(x => x.Type == orderType);
        }

        // 3. Виконуємо запит
        var orders = await query.ToListAsync(ct);

        return Ok(orders.Select(ToDto));
    }

    // --- 2. ОТРИМАННЯ ДЕТАЛЕЙ ОДНОГО ЗАМОВЛЕННЯ ---
    [HttpGet("{id:guid}")]
    public async Task<ActionResult<OrderResponse>> Get(Guid id, CancellationToken ct)
    {
        var o = await _db.Orders
            .Include(x => x.Items)
            .FirstOrDefaultAsync(x => x.Id == id, ct);

        if (o is null) return NotFound();

        return Ok(ToDto(o));
    }

    // --- 3. ОПЛАТА ЗАМОВЛЕННЯ ---
    [HttpPost("{id:guid}/pay")]
    public async Task<IActionResult> Pay(Guid id, CancellationToken ct)
    {
        var order = await _db.Orders.FirstOrDefaultAsync(x => x.Id == id, ct);

        if (order is null) return NotFound();

        if (order.IsPaid) return BadRequest("Order is already paid.");

        order.IsPaid = true;
        order.PaidAt = DateTimeOffset.UtcNow;

        await _db.SaveChangesAsync(ct);

        // Якщо це доставка -> повідомляємо сервіс доставки
        if (order.Type == OrderType.Delivery)
        {
            await _delivery.MarkOrderAsPaidAsync(order.Id, ct);
        }

        return NoContent();
    }

    // --- 4. СТВОРЕННЯ ЗАМОВЛЕННЯ ---
    [HttpPost]
    public async Task<ActionResult<OrderResponse>> Create([FromBody] CreateOrderRequest req, CancellationToken ct)
    {
        if (req.Items is null || req.Items.Count == 0)
            return BadRequest("Items are required.");

        if (req.Type == OrderType.Delivery)
        {
            if (string.IsNullOrEmpty(req.Address) || string.IsNullOrEmpty(req.Phone))
                return BadRequest("Address and Phone are required for Delivery orders.");
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

    // --- 5. ЗМІНА СТАТУСУ СТРАВИ ---
    [HttpPatch("items/{itemId:int}/status")]
    public async Task<IActionResult> UpdateItemStatus(int itemId, [FromBody] UpdateItemStatusRequest req, CancellationToken ct)
    {
        var item = await _db.OrderItems.Include(i => i.Order).FirstOrDefaultAsync(i => i.Id == itemId, ct);
        if (item is null) return NotFound();

        item.Status = req.Status;

        var parentOrder = item.Order;
        var allItemsCount = await _db.OrderItems.CountAsync(i => i.OrderId == parentOrder.Id, ct);
        var readyItemsCount = await _db.OrderItems.CountAsync(i => i.OrderId == parentOrder.Id && i.Status == OrderItemStatus.Ready, ct);

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