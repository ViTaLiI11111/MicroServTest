using System.ComponentModel.DataAnnotations;

namespace AuthService.Entities
{
    public class Cook
    {
        public int Id { get; set; }

        [Required]
        [MaxLength(100)]
        public string Username { get; set; }

        [Required]
        public string PasswordHash { get; set; }

        public string? FullName { get; set; }

        [Required]
        public int StationId { get; set; } // Обов'язкове поле для кухаря!
    }
}