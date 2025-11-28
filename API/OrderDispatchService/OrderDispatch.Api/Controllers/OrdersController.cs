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

    [HttpGet] // <--- ВАЖЛИВО: Цей атрибут робить метод доступним для GET запиту
    public async Task<ActionResult<IEnumerable<OrderResponse>>> List(CancellationToken ct)
    {
        var orders = await _db.Orders
            .AsNoTracking()
            .OrderByDescending(x => x.CreatedAt)
            .Include(x => x.Items)
            .ToListAsync(ct);

        return Ok(orders.Select(ToDto));
    }

    // --- ОСЬ ЦЬОГО МЕТОДУ НЕ ВИСТАЧАЄ ---
    [HttpGet("{id:guid}")] // Важливо: вказуємо, що чекаємо ID в URL
    public async Task<ActionResult<OrderResponse>> Get(Guid id, CancellationToken ct)
    {
        var o = await _db.Orders
            .Include(x => x.Items)
            .FirstOrDefaultAsync(x => x.Id == id, ct);

        if (o is null) return NotFound();

        return Ok(ToDto(o));
    }
    // ------------------------------------

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
            ClientName = req.ClientName // <--- Зберігаємо ім'я
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
                Qty = i.Qty
            };
            order.Items.Add(item);
            order.Total += item.Price * item.Qty;
        }

        _db.Orders.Add(order);
        await _db.SaveChangesAsync(ct);

        // Якщо доставка -> відправляємо запит у DeliveryService
        if (order.Type == OrderType.Delivery)
        {
            await _delivery.CreateDeliveryRequestAsync(
                order.Id,
                order.DeliveryAddress!,
                order.ClientPhone!,
                order.ClientName ?? "Unknown", // <--- Передаємо ім'я
                ct);
        }

        return Ok(ToDto(order));
    }

    // Інші методи (Get, List, SetStatus) залишаються без змін,
    // крім оновлення ToDto (див. нижче)

    static OrderResponse ToDto(Order o) => new(
        o.Id,
        o.TableNo,
        o.Status,
        o.Type.ToString(),
        o.Total,
        o.CreatedAt,
        o.DeliveryAddress,
        o.ClientPhone,
        o.ClientName, // <--- Додано в DTO
        o.Items.Select(i => new OrderItemResponse(i.Id, i.DishId, i.DishTitle, i.Qty, i.Price)).ToList()
    );

    public record StatusRequest(string Status);
}