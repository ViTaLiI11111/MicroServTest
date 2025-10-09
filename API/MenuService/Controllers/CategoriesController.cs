using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MenuService.Data;
using MenuService.DTOs;
using MenuService.Models;

namespace MenuService.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class CategoriesController : ControllerBase
    {
        private readonly AppDbContext _db;
        public CategoriesController(AppDbContext db) => _db = db;

        [HttpGet]
        public async Task<IEnumerable<CategoryDto>> GetAll()
        {
            return await _db.Categories
                .AsNoTracking()
                .Select(c => new CategoryDto { Id = c.Id, Title = c.Title })
                .ToListAsync();
        }

        [HttpPost]
        public async Task<ActionResult<CategoryDto>> Create([FromBody] CategoryDto req)
        {
            var entity = new Category { Title = req.Title };
            _db.Categories.Add(entity);
            await _db.SaveChangesAsync();

            return CreatedAtAction(nameof(GetAll), new { id = entity.Id },
                new CategoryDto { Id = entity.Id, Title = entity.Title });
        }
    }
}
