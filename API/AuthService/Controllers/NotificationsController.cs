using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using AuthService.Data;
using AuthService.Entities;
using AuthService.Models;

namespace AuthService.Controllers
{
    [ApiController]
    [Route("api/notifications")]
    public class NotificationsController : ControllerBase
    {
        private readonly AuthDbContext _context;

        public NotificationsController(AuthDbContext context)
        {
            _context = context;
        }

        // 1. Мобільний додаток надсилає сюди свій токен при старті/логіні
        [HttpPost("token")]
        public async Task<IActionResult> SaveToken([FromBody] SaveTokenRequest request)
        {
            // Шукаємо, чи є вже запис для цього юзера
            var existingToken = await _context.UserTokens
                .FirstOrDefaultAsync(t => t.Username == request.Username && t.Role == request.Role);

            if (existingToken != null)
            {
                // Оновлюємо токен
                existingToken.FcmToken = request.Token;
                existingToken.LastUpdated = DateTime.UtcNow;
            }
            else
            {
                // Створюємо новий
                var newToken = new UserToken
                {
                    Username = request.Username,
                    Role = request.Role,
                    FcmToken = request.Token
                };
                _context.UserTokens.Add(newToken);
            }

            await _context.SaveChangesAsync();
            return Ok(new { Message = "Token saved" });
        }

        // 2. Цей метод буде викликати OrderDispatchService, щоб дізнатися кому слати
        [HttpGet("token")]
        public async Task<IActionResult> GetToken([FromQuery] string username, [FromQuery] string role)
        {
            var tokenEntity = await _context.UserTokens
                .FirstOrDefaultAsync(t => t.Username == username && t.Role == role);

            if (tokenEntity == null)
            {
                return NotFound("Token not found");
            }

            return Ok(new { Token = tokenEntity.FcmToken });
        }

        // 3. Метод для отримання всіх токенів певної ролі (наприклад, всім Кухарям)
        [HttpGet("tokens/role")]
        public async Task<IActionResult> GetTokensByRole([FromQuery] string role)
        {
            var tokens = await _context.UserTokens
                .Where(t => t.Role == role)
                .Select(t => t.FcmToken)
                .ToListAsync();

            return Ok(tokens);
        }
    }
}