using System.ComponentModel.DataAnnotations;

namespace AuthService.Entities
{
    public class UserToken
    {
        public int Id { get; set; }

        [Required]
        public string Username { get; set; } // Логін користувача (унікальний для клієнта/кур'єра/кухаря)

        [Required]
        public string Role { get; set; } // "Client", "Waiter", "Courier", "Cook"

        [Required]
        public string FcmToken { get; set; } // Токен від Firebase

        public DateTime LastUpdated { get; set; } = DateTime.UtcNow;
    }
}