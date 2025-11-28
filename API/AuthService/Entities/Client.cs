using System.ComponentModel.DataAnnotations;

namespace AuthService.Entities
{
    public class Client
    {
        public int Id { get; set; }

        [Required]
        [MaxLength(100)]
        public string Username { get; set; }

        [Required]
        public string PasswordHash { get; set; }

        // --- ОСЬ ЦЬОГО РЯДКА НЕ ВИСТАЧАЄ ---
        public string? FullName { get; set; }
        // -----------------------------------

        [EmailAddress]
        public string? Email { get; set; }

        [Phone]
        public string? Phone { get; set; }
    }
}