using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using OrderDispatch.Application.Menu;
using OrderDispatch.Application.Delivery; // <-- 1. Додано для доставки
using OrderDispatch.Domain.Entities;
using OrderDispatch.Infrastructure;

namespace OrderDispatch.Api.Orders;

[ApiController]
[Route("orders")]
public class OrdersController : ControllerBase
{
    private readonly AppDbContext _db;
    private readonly IMenuClient _menu;
    private readonly IDeliveryClient _delivery; // <-- 2. Нове поле

    // 3. Оновлений конструктор
    public OrdersController(AppDbContext db, IMenuClient menu, IDeliveryClient delivery)
    {
        _db = db;
        _menu = menu;
        _delivery = delivery;
    }

    [HttpPost]
    public async Task<ActionResult<OrderResponse>> Create([FromBody] CreateOrderRequest req, CancellationToken ct)
    {
        if (req.Items is null || req.Items.Count == 0)
            return BadRequest("Items are required.");

        // 4. Валідація для доставки
        if (req.Type == OrderType.Delivery)
        {
            if (string.IsNullOrEmpty(req.Address) || string.IsNullOrEmpty(req.Phone))
            {
                return BadRequest("Delivery Address and Phone are required for delivery orders.");
            }
        }

        // 5. Мапинг нових полів
        var order = new Order
        {
            TableNo = req.TableNo,
            Status = "new",
            Type = req.Type,               // <-- Тип (DineIn/Delivery)
            DeliveryAddress = req.Address, // <-- Адреса
            ClientPhone = req.Phone        // <-- Телефон
        };

        foreach (var i in req.Items)
        {
            var dish = await _menu.GetDishAsync(i.DishId, ct);
            if (dish is null) return BadRequest($"Dish {i.DishId} not found in MenuService");

            var item = new OrderItem
            {
                DishId = dish.Id,
                DishTitle = dish.Title,
                Price = dish.Price,
                Qty = i.Qty
            };

            order.Items.Add(item);
            order.Total += item.Price * item.Qty;
        }

        _db.Orders.Add(order);
        await _db.SaveChangesAsync(ct);

        // 6. Інтеграція: Надсилаємо запит у DeliveryService, якщо це доставка
        if (order.Type == OrderType.Delivery)
        {
            // Ми не блокуємо відповідь клієнту, якщо сервіс доставки лежить,
            // але в реальному проді тут варто додати обробку помилок або черги (RabbitMQ)
            var deliverySuccess = await _delivery.CreateDeliveryRequestAsync(
                order.Id,
                order.DeliveryAddress!,
                order.ClientPhone!,
                ct);

            if (!deliverySuccess)
            {
                // Тут можна залогувати помилку: "Не вдалося створити доставку автоматично"
                // Для MVP просто продовжуємо
            }
        }

        return Ok(ToDto(order));
    }

    [HttpGet("{id:guid}")]
    public async Task<ActionResult<OrderResponse>> Get(Guid id, CancellationToken ct)
    {
        var o = await _db.Orders.Include(x => x.Items).FirstOrDefaultAsync(x => x.Id == id, ct);
        return o is null ? NotFound() : Ok(ToDto(o));
    }

    [HttpGet]
    public async Task<ActionResult<IEnumerable<OrderResponse>>> List(CancellationToken ct)
    {
        var orders = await _db.Orders
            .AsNoTracking()
            .OrderByDescending(x => x.CreatedAt)
            .Include(x => x.Items)
            .ToListAsync(ct);

        return Ok(orders.Select(ToDto));
    }

    [HttpPost("{id:guid}/status")]
    public async Task<IActionResult> SetStatus(Guid id, [FromBody] StatusRequest req, CancellationToken ct)
    {
        var o = await _db.Orders.FirstOrDefaultAsync(x => x.Id == id, ct);
        if (o is null) return NotFound();

        o.Status = req.Status;
        o.UpdatedAt = DateTimeOffset.UtcNow;
        await _db.SaveChangesAsync(ct);
        return NoContent();
    }

    // 7. Оновлений маппер ToDto (додані Type, Address, Phone)
    static OrderResponse ToDto(Order o) => new(
        o.Id,
        o.TableNo,
        o.Status,
        o.Type.ToString(),      // <-- Передаємо тип як рядок
        o.Total,
        o.CreatedAt,
        o.DeliveryAddress,      // <-- Адреса (може бути null)
        o.ClientPhone,          // <-- Телефон (може бути null)
        o.Items.Select(i => new OrderItemResponse(i.Id, i.DishId, i.DishTitle, i.Qty, i.Price)).ToList()
    );

    public record StatusRequest(string Status);
}