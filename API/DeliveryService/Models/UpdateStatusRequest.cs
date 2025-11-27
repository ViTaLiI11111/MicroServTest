using DeliveryService.Entities;

namespace DeliveryService.Models
{
    public class UpdateStatusRequest
    {
        public int CourierId { get; set; } // Хто міняє статус
        public DeliveryStatus NewStatus { get; set; }
    }
}