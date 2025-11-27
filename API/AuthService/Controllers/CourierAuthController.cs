using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using AuthService.Data;
using AuthService.Entities;
using AuthService.Models;

namespace AuthService.Controllers
{
    [ApiController]
    [Route("api/courier")]
    public class CourierAuthController : ControllerBase
    {
        private readonly AuthDbContext _context;

        public CourierAuthController(AuthDbContext context)
        {
            _context = context;
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register(RegisterRequest request)
        {
            // Перевірка на унікальність
            if (await _context.Couriers.AnyAsync(c => c.Username == request.Username))
            {
                return BadRequest("Username already taken");
            }

            // Перевірка на наявність телефону (для кур'єра це важливо)
            if (string.IsNullOrEmpty(request.Phone))
            {
                return BadRequest("Phone number is required for couriers");
            }

            string passwordHash = BCrypt.Net.BCrypt.HashPassword(request.Password);

            var courier = new Courier
            {
                Username = request.Username,
                PasswordHash = passwordHash,
                FullName = request.FullName,
                Phone = request.Phone
            };

            _context.Couriers.Add(courier);
            await _context.SaveChangesAsync();

            return Ok(new { Message = "Courier registered successfully" });
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login(LoginRequest request)
        {
            var courier = await _context.Couriers
                .FirstOrDefaultAsync(c => c.Username == request.Username);

            if (courier == null || !BCrypt.Net.BCrypt.Verify(request.Password, courier.PasswordHash))
            {
                return Unauthorized("Invalid credentials");
            }

            var response = new LoginResponse
            {
                UserId = courier.Id,
                Username = courier.Username
            };

            return Ok(response);
        }
    }
}