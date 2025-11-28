namespace DeliveryService.Models
{
    public class CreateDeliveryRequest
    {
        public Guid OrderId { get; set; }
        public string Address { get; set; } = string.Empty;
        public string Phone { get; set; } = string.Empty;
        public string ClientName { get; set; } = string.Empty; // <--- НОВЕ ПОЛЕ
    }
}