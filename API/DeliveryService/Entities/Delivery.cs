using System.ComponentModel.DataAnnotations;

namespace DeliveryService.Entities
{
    public class Delivery
    {
        public int Id { get; set; }
        public Guid OrderId { get; set; }
        public int? CourierId { get; set; }
        public DeliveryStatus Status { get; set; } = DeliveryStatus.Created;

        [Required]
        public string ClientAddress { get; set; } = string.Empty;

        public string? ClientPhone { get; set; }

        public string? ClientName { get; set; } // <--- НОВЕ ПОЛЕ

        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
        public DateTime? DeliveredAt { get; set; }
    }
}