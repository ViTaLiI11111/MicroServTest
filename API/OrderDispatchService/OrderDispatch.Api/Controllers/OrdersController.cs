using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using OrderDispatch.Application.Menu;
using OrderDispatch.Domain.Entities;
using OrderDispatch.Infrastructure;

namespace OrderDispatch.Api.Orders;

[ApiController]
[Route("orders")]
public class OrdersController : ControllerBase
{
    private readonly AppDbContext _db;
    private readonly IMenuClient _menu;

    public OrdersController(AppDbContext db, IMenuClient menu) { _db = db; _menu = menu; }

    [HttpPost]
    public async Task<ActionResult<OrderResponse>> Create([FromBody] CreateOrderRequest req, CancellationToken ct)
    {
        if (req.Items is null || req.Items.Count == 0)
            return BadRequest("Items are required.");

        var order = new Order { TableNo = req.TableNo, Status = "new" };

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

    static OrderResponse ToDto(Order o) => new(
        o.Id, o.TableNo, o.Status, o.Total, o.CreatedAt,
        o.Items.Select(i => new OrderItemResponse(i.Id, i.DishId, i.DishTitle, i.Qty, i.Price)).ToList());

    public record StatusRequest(string Status);
}
