using System.ComponentModel.DataAnnotations;

namespace DeliveryService.Entities
{
    public class Delivery
    {
        public int Id { get; set; }

        [Required]
        public Guid OrderId { get; set; } // ID замовлення з OrderDispatchService

        public int? CourierId { get; set; } // ID кур'єра (null, поки не взяв)

        public DeliveryStatus Status { get; set; } = DeliveryStatus.Created;

        [Required]
        public string ClientAddress { get; set; } // Адреса клієнта

        public string? ClientPhone { get; set; } // Телефон для зв'язку

        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
        public DateTime? DeliveredAt { get; set; }
    }
}