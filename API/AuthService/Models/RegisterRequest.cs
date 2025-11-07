using System.ComponentModel.DataAnnotations;

namespace AuthService.Models
{
    // Ця DTO підійде для реєстрації і клієнта, і офіціанта
    public class RegisterRequest
    {
        [Required]
        public string Username { get; set; }

        [Required]
        [MinLength(6)]
        public string Password { get; set; }

        public string? Email { get; set; } // Використовується тільки для клієнта
        public string? Phone { get; set; } // Використовується тільки для клієнта
        public string? FullName { get; set; } // Використовується тільки для офіціанта
    }
}