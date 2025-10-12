using System.Collections.Generic;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MenuService.Data;      // <- підстав свій namespace з AppDbContext
using MenuService.Models;    // <- підстав namespace де Category

namespace MenuService.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class CategoriesController : ControllerBase
    {
        private readonly AppDbContext _db;
        public CategoriesController(AppDbContext db) => _db = db;

        // GET /api/Categories
        [HttpGet]
        public async Task<ActionResult<IEnumerable<Category>>> GetAll()
        {
            var list = await _db.Categories.ToListAsync();
            return Ok(list);
        }

        // GET /api/Categories/{id}
        [HttpGet("{id:int}")]
        public async Task<ActionResult<Category>> GetById(int id)
        {
            var item = await _db.Categories.FindAsync(id);
            if (item == null) return NotFound();
            return Ok(item);
        }

        // POST /api/Categories
        [HttpPost]
        public async Task<ActionResult<Category>> Create([FromBody] Category category)
        {
            _db.Categories.Add(category);
            await _db.SaveChangesAsync();
            return CreatedAtAction(nameof(GetById), new { id = category.Id }, category);
        }

        // DELETE /api/Categories/{id}
        [HttpDelete("{id:int}")]
        public async Task<IActionResult> Delete(int id)
        {
            var item = await _db.Categories.FindAsync(id);
            if (item == null) return NotFound();
            _db.Categories.Remove(item);
            await _db.SaveChangesAsync();
            return NoContent();
        }
    }
}
