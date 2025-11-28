using DeliveryService.Entities;

namespace DeliveryService.Models
{
    public class DeliveryStatusResponse
    {
        public int Id { get; set; }
        public DeliveryStatus Status { get; set; }
        public int? CourierId { get; set; }
        public DateTime? DeliveredAt { get; set; }
    }
}