namespace DeliveryService.Models
{
    public class CreateDeliveryRequest
    {
        public Guid OrderId { get; set; }
        public string Address { get; set; }
        public string Phone { get; set; }
    }
}