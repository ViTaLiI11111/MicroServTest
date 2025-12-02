using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using AuthService.Data;
using AuthService.Entities;
using AuthService.Models;

namespace AuthService.Controllers
{
    [ApiController]
    [Route("api/cook")]
    public class CookAuthController : ControllerBase
    {
        private readonly AuthDbContext _context;

        public CookAuthController(AuthDbContext context)
        {
            _context = context;
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register(RegisterRequest request)
        {
            if (await _context.Cooks.AnyAsync(c => c.Username == request.Username))
            {
                return BadRequest("Username already taken");
            }

            // Перевіряємо, чи вказано цех
            if (request.StationId == null)
            {
                return BadRequest("StationId is required for cooks");
            }

            string passwordHash = BCrypt.Net.BCrypt.HashPassword(request.Password);

            var cook = new Cook
            {
                Username = request.Username,
                PasswordHash = passwordHash,
                FullName = request.FullName,
                StationId = request.StationId.Value // Зберігаємо ID цеху
            };

            _context.Cooks.Add(cook);
            await _context.SaveChangesAsync();

            return Ok(new { Message = "Cook registered successfully" });
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login(LoginRequest request)
        {
            var cook = await _context.Cooks
                .FirstOrDefaultAsync(c => c.Username == request.Username);

            if (cook == null || !BCrypt.Net.BCrypt.Verify(request.Password, cook.PasswordHash))
            {
                return Unauthorized("Invalid credentials");
            }

            var response = new LoginResponse
            {
                UserId = cook.Id,
                Username = cook.Username,
                StationId = cook.StationId // <--- Повертаємо ID цеху
            };

            return Ok(response);
        }
    }
}