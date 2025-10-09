using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MenuService.Data;
using MenuService.DTOs;
using MenuService.Models;
using MenuService.Services;
using Newtonsoft.Json;

namespace MenuService.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class DishesController : ControllerBase
    {
        private readonly AppDbContext _db;
        private readonly ICacheService _cache;
        private const string CacheKey_All = "dishes:all";

        public DishesController(AppDbContext db, ICacheService cache)
        {
            _db = db;
            _cache = cache;
        }

        [HttpGet]
        public async Task<ActionResult<IEnumerable<DishDto>>> GetAll()
        {
            // 1) спробувати з кешу
            var cached = await _cache.GetAsync(CacheKey_All);
            if (!string.IsNullOrEmpty(cached))
                return Ok(JsonConvert.DeserializeObject<List<DishDto>>(cached));

            // 2) з БД
            var items = await _db.Dishes.AsNoTracking().ToListAsync();
            var dto = items.Select(d => new DishDto
            {
                Id = d.Id,
                Title = d.Title,
                Price = d.Price,
                Pepper = d.Pepper,
                Color = d.Color,
                CategoryId = d.CategoryId,
                ImageBase64 = d.ImageBase64
            }).ToList();

            // 3) покласти в кеш на 5 хв
            await _cache.SetAsync(CacheKey_All, JsonConvert.SerializeObject(dto), TimeSpan.FromMinutes(5));
            return Ok(dto);
        }

        [HttpGet("{id:int}")]
        public async Task<ActionResult<DishDto>> GetById(int id)
        {
            var d = await _db.Dishes.AsNoTracking().FirstOrDefaultAsync(x => x.Id == id);
            if (d == null) return NotFound();

            return Ok(new DishDto
            {
                Id = d.Id,
                Title = d.Title,
                Price = d.Price,
                Pepper = d.Pepper,
                Color = d.Color,
                CategoryId = d.CategoryId,
                ImageBase64 = d.ImageBase64
            });
        }

        [HttpPost]
        public async Task<ActionResult<DishDto>> Create([FromBody] CreateDishRequest req)
        {
            var entity = new Dish
            {
                Title = req.Title,
                Price = req.Price,
                Pepper = req.Pepper,
                Color = req.Color,
                CategoryId = req.CategoryId,
                ImageBase64 = req.ImageBase64
            };

            _db.Dishes.Add(entity);
            await _db.SaveChangesAsync();

            // інвалідуємо кеш списку
            await _cache.RemoveAsync(CacheKey_All);

            return CreatedAtAction(nameof(GetById), new { id = entity.Id }, new DishDto
            {
                Id = entity.Id,
                Title = entity.Title,
                Price = entity.Price,
                Pepper = entity.Pepper,
                Color = entity.Color,
                CategoryId = entity.CategoryId,
                ImageBase64 = entity.ImageBase64
            });
        }

        [HttpPut("{id:int}")]
        public async Task<IActionResult> Update(int id, [FromBody] UpdateDishRequest req)
        {
            var entity = await _db.Dishes.FindAsync(id);
            if (entity == null) return NotFound();

            entity.Title = req.Title;
            entity.Price = req.Price;
            entity.Pepper = req.Pepper;
            entity.Color = req.Color;
            entity.CategoryId = req.CategoryId;
            entity.ImageBase64 = req.ImageBase64;

            await _db.SaveChangesAsync();
            await _cache.RemoveAsync(CacheKey_All);
            return NoContent();
        }

        [HttpDelete("{id:int}")]
        public async Task<IActionResult> Delete(int id)
        {
            var entity = await _db.Dishes.FindAsync(id);
            if (entity == null) return NotFound();

            _db.Dishes.Remove(entity);
            await _db.SaveChangesAsync();
            await _cache.RemoveAsync(CacheKey_All);
            return NoContent();
        }
    }
}
