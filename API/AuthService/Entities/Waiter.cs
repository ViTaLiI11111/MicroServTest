using System.ComponentModel.DataAnnotations;

namespace AuthService.Entities
{
    public class Waiter
    {
        public int Id { get; set; }

        [Required]
        [MaxLength(100)]
        public string Username { get; set; }

        [Required]
        public string PasswordHash { get; set; }

        public string? FullName { get; set; }
    }
}