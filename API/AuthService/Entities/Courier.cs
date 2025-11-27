using System.ComponentModel.DataAnnotations;

namespace AuthService.Entities
{
    public class Courier
    {
        public int Id { get; set; }

        [Required]
        [MaxLength(100)]
        public string Username { get; set; }

        [Required]
        public string PasswordHash { get; set; }

        [Required] // Для кур'єра телефон важливий
        [Phone]
        public string Phone { get; set; }

        public string? FullName { get; set; }
    }
}