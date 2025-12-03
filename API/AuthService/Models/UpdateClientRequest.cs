namespace AuthService.Models
{
    public class UpdateClientRequest
    {
        public int Id { get; set; }
        public string? FullName { get; set; } // <--- Це поле відповідає за "Vitaliy"
        public string? Email { get; set; }
        public string? Phone { get; set; }
        public string? Address { get; set; }
    }
}