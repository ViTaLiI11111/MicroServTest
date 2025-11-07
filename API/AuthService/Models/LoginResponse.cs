namespace AuthService.Models
{
    // Успішна відповідь при логіні
    public class LoginResponse
    {
        public int UserId { get; set; }
        public string Username { get; set; }
    }
}