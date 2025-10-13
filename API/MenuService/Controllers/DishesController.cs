namespace MenuService.Controllers;

using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MenuService.Data;
using MenuService.Models;
using MenuService.DTOs;

[ApiController]
[Route("api/[controller]")]
public class DishesController : ControllerBase
{
    private readonly AppDbContext _db;
    public DishesController(AppDbContext db) => _db = db;

    [HttpGet] // GET /api/Dishes
    public async Task<ActionResult<IEnumerable<DishDto>>> GetAll()
        => await _db.Dishes
            .Select(d => new DishDto
            {
                Id = d.Id,
                Title = d.Title,
                Price = d.Price,
                Pepper = d.Pepper,
                Color = d.Color,
                CategoryId = d.CategoryId,
                ImageBase64 = d.ImageBase64
            })
            .ToListAsync();

    [HttpGet("{id:int}")] // GET /api/Dishes/5
    public async Task<ActionResult<DishDto>> GetById(int id)
    {
        var d = await _db.Dishes.FindAsync(id);
        if (d is null) return NotFound();
        return new DishDto
        {
            Id = d.Id,
            Title = d.Title,
            Price = d.Price,
            Pepper = d.Pepper,
            Color = d.Color,
            CategoryId = d.CategoryId,
            ImageBase64 = d.ImageBase64
        };
    }

    [HttpPost] // POST /api/Dishes
    public async Task<ActionResult<DishDto>> Create([FromBody] CreateDishRequest req)
    {
        // унікальність Id, якщо дозволяєш ручне введення
        if (await _db.Dishes.AnyAsync(x => x.Id == req.Id))
            return Conflict($"Dish with Id {req.Id} already exists.");

        if (!await _db.Categories.AnyAsync(c => c.Id == req.CategoryId))
            return BadRequest($"Category {req.CategoryId} not found.");

        var entity = new Dish
        {
            Id = req.Id, // якщо ручний Id; якщо авто — не заповнюй
            Title = req.Title,
            Price = req.Price,
            Pepper = req.Pepper,
            Color = req.Color,
            CategoryId = req.CategoryId,
            ImageBase64 = req.ImageBase64
        };

        _db.Dishes.Add(entity);
        await _db.SaveChangesAsync();

        var dto = new DishDto
        {
            Id = entity.Id,
            Title = entity.Title,
            Price = entity.Price,
            Pepper = entity.Pepper,
            Color = entity.Color,
            CategoryId = entity.CategoryId,
            ImageBase64 = entity.ImageBase64
        };

        return CreatedAtAction(nameof(GetById), new { id = entity.Id }, dto);
    }

    [HttpPut("{id:int}")] // PUT /api/Dishes/5
    public async Task<ActionResult<DishDto>> Update(int id, [FromBody] CreateDishRequest req)
    {
        var d = await _db.Dishes.FindAsync(id);
        if (d is null) return NotFound();

        if (!await _db.Categories.AnyAsync(c => c.Id == req.CategoryId))
            return BadRequest($"Category {req.CategoryId} not found.");

        d.Title = req.Title;
        d.Price = req.Price;
        d.Pepper = req.Pepper;
        d.Color = req.Color;
        d.CategoryId = req.CategoryId;
        d.ImageBase64 = req.ImageBase64;

        await _db.SaveChangesAsync();

        return new DishDto
        {
            Id = d.Id,
            Title = d.Title,
            Price = d.Price,
            Pepper = d.Pepper,
            Color = d.Color,
            CategoryId = d.CategoryId,
            ImageBase64 = d.ImageBase64
        };
    }

    [HttpDelete("{id:int}")] // DELETE /api/Dishes/5
    public async Task<IActionResult> Delete(int id)
    {
        var d = await _db.Dishes.FindAsync(id);
        if (d is null) return NotFound();

        _db.Dishes.Remove(d);
        await _db.SaveChangesAsync();
        return NoContent();
    }
}
