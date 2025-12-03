using System.ComponentModel.DataAnnotations;

namespace AuthService.Models
{
    public class SaveTokenRequest
    {
        [Required]
        public string Username { get; set; }

        [Required]
        public string Role { get; set; } // Client, Waiter, Courier, Cook

        [Required]
        public string Token { get; set; }
    }
}