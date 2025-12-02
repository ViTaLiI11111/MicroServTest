using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using DeliveryService.Data;
using DeliveryService.Entities;
using DeliveryService.Models;

namespace DeliveryService.Controllers
{
    [ApiController]
    [Route("api/deliveries")]
    public class DeliveryController : ControllerBase
    {
        private readonly DeliveryDbContext _context;

        public DeliveryController(DeliveryDbContext context)
        {
            _context = context;
        }

        // 1. Створення доставки (Викликає OrderDispatchService)
        [HttpPost]
        public async Task<IActionResult> CreateDelivery(CreateDeliveryRequest request)
        {
            if (await _context.Deliveries.AnyAsync(d => d.OrderId == request.OrderId))
            {
                return BadRequest("Delivery for this order already exists");
            }

            var delivery = new Delivery
            {
                OrderId = request.OrderId,
                ClientAddress = request.Address,
                ClientPhone = request.Phone,
                ClientName = request.ClientName,
                Status = DeliveryStatus.Created,

                // --- Зберігаємо нові поля ---
                IsPaid = request.IsPaid,
                Total = request.Total
            };

            _context.Deliveries.Add(delivery);
            await _context.SaveChangesAsync();

            return Ok(new { Message = "Delivery request created", DeliveryId = delivery.Id });
        }

        // 2. Отримання доступних замовлень (Для Кур'єра)
        [HttpGet("available")]
        public async Task<IActionResult> GetAvailableDeliveries()
        {
            var list = await _context.Deliveries
                .Where(d => d.Status == DeliveryStatus.Created && d.CourierId == null)
                .ToListAsync();
            return Ok(list);
        }

        // 3. Кур'єр бере замовлення в роботу
        [HttpPost("{id}/take")]
        public async Task<IActionResult> TakeDelivery(int id, [FromQuery] int courierId)
        {
            var delivery = await _context.Deliveries.FindAsync(id);
            if (delivery == null) return NotFound();

            if (delivery.CourierId != null)
                return BadRequest("Already taken by another courier");

            delivery.CourierId = courierId;
            delivery.Status = DeliveryStatus.Assigned;

            await _context.SaveChangesAsync();
            return Ok(new { Message = "You took the order!" });
        }

        // 4. Оновлення статусу (PickedUp -> Delivered)
        [HttpPut("{id}/status")]
        public async Task<IActionResult> UpdateStatus(int id, UpdateStatusRequest request)
        {
            var delivery = await _context.Deliveries.FindAsync(id);
            if (delivery == null) return NotFound();

            // Перевірка, чи це той самий кур'єр
            if (delivery.CourierId != request.CourierId)
                return Unauthorized("This isn't your order");

            delivery.Status = request.NewStatus;

            if (request.NewStatus == DeliveryStatus.Delivered)
            {
                delivery.DeliveredAt = DateTime.UtcNow;
            }

            await _context.SaveChangesAsync();
            return Ok(new { Message = $"Status updated to {request.NewStatus}" });
        }

        // 5. Отримати мої активні замовлення (Для Кур'єра)
        [HttpGet("my/{courierId}")]
        public async Task<IActionResult> GetMyDeliveries(int courierId)
        {
            var list = await _context.Deliveries
                .Where(d => d.CourierId == courierId)
                // ПРИБРАЛИ: && d.Status != DeliveryStatus.Delivered
                // ДОДАЛИ: Сортування, щоб недоставлені були зверху, а потім за датою
                .OrderBy(d => d.Status == DeliveryStatus.Delivered) // false < true, тому активні будуть вище
                .ThenByDescending(d => d.CreatedAt)
                .ToListAsync();

            return Ok(list);
        }

        [HttpGet("track/{orderId}")]
        public async Task<IActionResult> GetDeliveryStatus(Guid orderId)
        {
            var delivery = await _context.Deliveries
                .FirstOrDefaultAsync(d => d.OrderId == orderId);

            if (delivery == null)
            {
                return NotFound(new { Message = "Delivery not found for this order" });
            }

            var response = new DeliveryStatusResponse
            {
                Id = delivery.Id,
                Status = delivery.Status,
                CourierId = delivery.CourierId,
                DeliveredAt = delivery.DeliveredAt
            };

            return Ok(response);
        }

        [HttpPatch("order/{orderId:guid}/ready")]
        public async Task<IActionResult> MarkAsReady(Guid orderId)
        {
            var delivery = await _context.Deliveries.FirstOrDefaultAsync(d => d.OrderId == orderId);

            if (delivery == null) return NotFound();

            delivery.IsReadyForPickup = true;
            await _context.SaveChangesAsync();

            return NoContent();
        }

        // --- 6. НОВИЙ МЕТОД: Позначити як оплачене ---
        [HttpPatch("order/{orderId:guid}/pay")]
        public async Task<IActionResult> MarkAsPaid(Guid orderId)
        {
            var delivery = await _context.Deliveries.FirstOrDefaultAsync(d => d.OrderId == orderId);

            if (delivery == null) return NotFound();

            delivery.IsPaid = true;
            await _context.SaveChangesAsync();

            return NoContent();
        }
    }
}