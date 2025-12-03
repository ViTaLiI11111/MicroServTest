using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using OrderDispatch.Application.Menu;
using OrderDispatch.Application.Delivery;
using OrderDispatch.Application.Interfaces; // <--- Інтерфейси тут
using OrderDispatch.Domain.Entities;
using OrderDispatch.Infrastructure;
using OrderDispatch.Api.Orders; // Для DTO (CreateOrderRequest і т.д.)

namespace OrderDispatch.Api.Controllers;

[ApiController]
[Route("orders")]
public class OrdersController : ControllerBase
{
    private readonly AppDbContext _db;
    private readonly IMenuClient _menu;
    private readonly IDeliveryClient _delivery;

    // --- НОВІ ЗАЛЕЖНОСТІ ДЛЯ ПУШІВ ---
    private readonly IAuthClient _auth;
    private readonly INotificationService _notifier;

    public OrdersController(
        AppDbContext db,
        IMenuClient menu,
        IDeliveryClient delivery,
        IAuthClient auth,
        INotificationService notifier)
    {
        _db = db;
        _menu = menu;
        _delivery = delivery;
        _auth = auth;
        _notifier = notifier;
    }

    // --- 1. СПИСОК ЗАМОВЛЕНЬ ---
    [HttpGet]
    public async Task<ActionResult<IEnumerable<OrderResponse>>> List(
        [FromQuery] string? type,
        [FromQuery] int? waiterId,
        [FromQuery] bool? onlyFree,
        [FromQuery] bool? activeOnly,
        [FromQuery] string? clientName,
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

        // 2. Фільтр по клієнту (Історія)
        if (!string.IsNullOrEmpty(clientName))
        {
            query = query.Where(x => x.ClientName == clientName);
        }
        else
        {
            // 3. Фільтр актуальності (якщо не історія)
            if (activeOnly == true || (string.IsNullOrEmpty(type) && waiterId == null))
            {
                var yesterday = DateTimeOffset.UtcNow.AddHours(-24);
                query = query.Where(x => x.CreatedAt >= yesterday);
            }
        }

        var orders = await query.ToListAsync(ct);
        return Ok(orders.Select(ToDto));
    }

    // --- 2. ОТРИМАННЯ ОДНОГО ---
    [HttpGet("{id:guid}")]
    public async Task<ActionResult<OrderResponse>> Get(Guid id, CancellationToken ct)
    {
        var o = await _db.Orders
            .Include(x => x.Items)
            .FirstOrDefaultAsync(x => x.Id == id, ct);

        if (o is null) return NotFound();
        return Ok(ToDto(o));
    }

    // --- 3. ОПЛАТА ---
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

    // --- 4. СТВОРЕННЯ (ТУТ Є ПУШІ) ---
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

        // --- ЛОГІКА ПОВІДОМЛЕНЬ ---
        try
        {
            // 1. Сповіщаємо Кухарів
            var cookTokens = await _auth.GetTokensByRoleAsync("Cook");
            await _notifier.SendToMultipleTokensAsync(cookTokens, "Нове замовлення!",
                $"Тип: {(order.Type == OrderType.DineIn ? "Зал" : "Доставка")}. Стіл/Інфо: {order.TableNo}");

            // 2. Сповіщаємо персонал або клієнта
            if (order.Type == OrderType.DineIn)
            {
                var waiterTokens = await _auth.GetTokensByRoleAsync("Waiter");
                await _notifier.SendToMultipleTokensAsync(waiterTokens, "Новий столик", $"Стіл №{order.TableNo} чекає.");
            }
            else
            {
                // Сповіщення клієнту (якщо є ім'я)
                if (!string.IsNullOrEmpty(order.ClientName))
                {
                    // Важливо: ми припускаємо, що ClientName == Username клієнта
                    var token = await _auth.GetTokenAsync(order.ClientName, "Client");
                    if (token != null)
                    {
                        await _notifier.SendToTokenAsync(token, "Замовлення прийнято!", "Ваше замовлення відправлено на кухню.");
                    }
                }
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"Notification Error: {ex.Message}");
            // Не зупиняємо роботу, якщо пуш не пішов
        }

        return Ok(ToDto(order));
    }

    // --- 5. ЗМІНА СТАТУСУ СТРАВИ (ТУТ Є ПУШІ) ---
    // --- 5. ЗМІНА СТАТУСУ СТРАВИ (Виправлено для Клієнта) ---
    [HttpPatch("items/{itemId:int}/status")]
    public async Task<IActionResult> UpdateItemStatus(int itemId, [FromBody] UpdateItemStatusRequest req, CancellationToken ct)
    {
        var item = await _db.OrderItems.Include(i => i.Order).FirstOrDefaultAsync(i => i.Id == itemId, ct);
        if (item is null) return NotFound();

        // Оновлюємо статус
        item.Status = req.Status;

        var parentOrder = item.Order!;
        var allItemsCount = await _db.OrderItems.CountAsync(i => i.OrderId == parentOrder.Id, ct);
        var readyItemsCount = await _db.OrderItems.CountAsync(i => i.OrderId == parentOrder.Id && i.Status == OrderItemStatus.Ready, ct);

        // Перевіряємо, чи ВСЕ замовлення готове (враховуємо поточний item, який ми щойно змінили в пам'яті, але в базі він ще може бути старим до SaveChanges, тому логіка readyItemsCount + 1 вірна лише якщо поточний статус Ready)
        // Але надійніше перевірити після req.Status:

        bool isCurrentItemBecomingReady = req.Status == OrderItemStatus.Ready;
        // Якщо в базі 4 items, 3 ready, і ми зараз робимо 4-й ready -> то readyItemsCount (з бази) = 3. 3+1 = 4. 
        bool isNowFullyReady = isCurrentItemBecomingReady && (readyItemsCount + 1 == allItemsCount);

        if (isNowFullyReady)
        {
            parentOrder.Status = "ready";

            // ==========================================
            // 1. ЛОГІКА ДЛЯ ДОСТАВКИ (Deliveries API + Couriers)
            // ==========================================
            if (parentOrder.Type == OrderType.Delivery)
            {
                await _delivery.MarkOrderAsReadyAsync(parentOrder.Id, ct);

                // PUSH: Кур'єрам (щоб забрали)
                try
                {
                    var courierTokens = await _auth.GetTokensByRoleAsync("Courier");
                    await _notifier.SendToMultipleTokensAsync(courierTokens, "Доставка готова!", "Заберіть замовлення з кухні.");
                }
                catch { }
            }
            // ==========================================
            // 2. ЛОГІКА ДЛЯ ЗАЛУ (Waiters)
            // ==========================================
            else
            {
                // PUSH: Офіціантам (щоб віднесли)
                try
                {
                    var waiterTokens = await _auth.GetTokensByRoleAsync("Waiter");
                    await _notifier.SendToMultipleTokensAsync(waiterTokens, "Готово до видачі!", $"Стіл №{parentOrder.TableNo} готовий.");
                }
                catch { }
            }

            // ==========================================
            // 3. НОВА ЛОГІКА: СПОВІЩЕННЯ КЛІЄНТА
            // ==========================================
            if (!string.IsNullOrEmpty(parentOrder.ClientName))
            {
                try
                {
                    // Отримуємо токен саме цього клієнта
                    var clientToken = await _auth.GetTokenAsync(parentOrder.ClientName, "Client");

                    if (clientToken != null)
                    {
                        string title = "Замовлення готове! 😋";
                        string body = parentOrder.Type == OrderType.DineIn
                            ? "Ваші страви готові! Офіціант вже несе їх вам."
                            : "Кухня приготувала ваше замовлення! Шукаємо кур'єра.";

                        await _notifier.SendToTokenAsync(clientToken, title, body);
                    }
                }
                catch (Exception ex)
                {
                    Console.WriteLine($"Error sending push to client: {ex.Message}");
                }
            }
        }

        await _db.SaveChangesAsync(ct);
        return NoContent();
    }

    // --- 6. ASSIGN ---
    [HttpPost("{id:guid}/assign")]
    public async Task<IActionResult> Assign(Guid id, [FromQuery] int waiterId, CancellationToken ct)
    {
        var order = await _db.Orders.FirstOrDefaultAsync(x => x.Id == id, ct);
        if (order is null) return NotFound();

        if (order.WaiterId != null && order.WaiterId != waiterId)
            return BadRequest("This order is already taken.");

        order.WaiterId = waiterId;
        if (order.Status == "new") order.Status = "inprogress";

        await _db.SaveChangesAsync(ct);
        return Ok(ToDto(order));
    }

    // --- 7. COMPLETE ---
    [HttpPost("{id:guid}/complete")]
    public async Task<IActionResult> Complete(Guid id, CancellationToken ct)
    {
        var order = await _db.Orders.FirstOrDefaultAsync(x => x.Id == id, ct);
        if (order is null) return NotFound();
        if (!order.IsPaid) return BadRequest("Cannot complete unpaid order.");

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
        o.WaiterId,
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