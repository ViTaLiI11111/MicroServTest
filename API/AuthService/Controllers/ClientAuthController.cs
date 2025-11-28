using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using AuthService.Data;
using AuthService.Entities;
using AuthService.Models;

namespace AuthService.Controllers
{
    [ApiController]
    [Route("api/client")]
    public class ClientAuthController : ControllerBase
    {
        private readonly AuthDbContext _context;

        public ClientAuthController(AuthDbContext context)
        {
            _context = context;
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register(RegisterRequest request)
        {
            // Перевіряємо, чи юзер вже існує
            if (await _context.Clients.AnyAsync(c => c.Username == request.Username))
            {
                return BadRequest("Username already taken");
            }

            // Хешуємо пароль
            string passwordHash = BCrypt.Net.BCrypt.HashPassword(request.Password);

            var client = new Client
            {
                Username = request.Username,
                PasswordHash = passwordHash,
                Email = request.Email,
                Phone = request.Phone
            };

            _context.Clients.Add(client);
            await _context.SaveChangesAsync();

            return Ok(new { Message = "Client registered successfully" });
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login(LoginRequest request)
        {
            var client = await _context.Clients
                .FirstOrDefaultAsync(c => c.Username == request.Username);

            // Перевіряємо юзера і чи збігається пароль
            if (client == null || !BCrypt.Net.BCrypt.Verify(request.Password, client.PasswordHash))
            {
                return Unauthorized("Invalid credentials");
            }

            var response = new LoginResponse
            {
                UserId = client.Id,
                Username = client.Username
            };

            return Ok(response);
        }

        [HttpPut("profile")]
        public async Task<IActionResult> UpdateProfile([FromBody] UpdateClientRequest request)
        {
            var client = await _context.Clients.FindAsync(request.Id);
            if (client == null) return NotFound("Client not found");

            // Оновлюємо ім'я в базі
            client.FullName = request.FullName; // <--- Ось тут відбувається магія

            client.Email = request.Email;
            client.Phone = request.Phone;

            await _context.SaveChangesAsync();
            return Ok(new { Message = "Profile updated successfully" });
        }
    }
}