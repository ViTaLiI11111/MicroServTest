using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using AuthService.Data;
using AuthService.Entities;
using AuthService.Models;

namespace AuthService.Controllers
{
    [ApiController]
    [Route("api/waiter")]
    public class WaiterAuthController : ControllerBase
    {
        private readonly AuthDbContext _context;

        public WaiterAuthController(AuthDbContext context)
        {
            _context = context;
        }

        [HttpPost("register")]
        public async Task<IActionResult> Register(RegisterRequest request)
        {
            if (await _context.Waiters.AnyAsync(w => w.Username == request.Username))
            {
                return BadRequest("Username already taken");
            }

            string passwordHash = BCrypt.Net.BCrypt.HashPassword(request.Password);

            var waiter = new Waiter
            {
                Username = request.Username,
                PasswordHash = passwordHash,
                FullName = request.FullName
            };

            _context.Waiters.Add(waiter);
            await _context.SaveChangesAsync();

            return Ok(new { Message = "Waiter registered successfully" });
        }

        [HttpPost("login")]
        public async Task<IActionResult> Login(LoginRequest request)
        {
            var waiter = await _context.Waiters
                .FirstOrDefaultAsync(w => w.Username == request.Username);

            if (waiter == null || !BCrypt.Net.BCrypt.Verify(request.Password, waiter.PasswordHash))
            {
                return Unauthorized("Invalid credentials");
            }

            var response = new LoginResponse
            {
                UserId = waiter.Id,
                Username = waiter.Username
            };

            return Ok(response);
        }
    }
}