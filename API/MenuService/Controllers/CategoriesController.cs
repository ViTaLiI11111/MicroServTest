using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using MenuService.Data;
using MenuService.DTOs;
using MenuService.Models;

namespace MenuService.Controllers;

[ApiController]
[Route("api/[controller]")]
public sealed class CategoriesController : ControllerBase
{
    private readonly AppDbContext _db;

    public CategoriesController(AppDbContext db) => _db = db;

    [HttpGet]
    public async Task<ActionResult<IEnumerable<CategoryDto>>> GetAll()
    {
        var data = await _db.Categories
            .OrderBy(x => x.Id)
            .Select(x => new CategoryDto { Id = x.Id, Title = x.Title })
            .ToListAsync();

        return Ok(data);
    }

    [HttpGet("{id:int}")]
    public async Task<ActionResult<CategoryDto>> GetById(int id)
    {
        var cat = await _db.Categories.FindAsync(id);
        if (cat is null) return NotFound();

        return Ok(new CategoryDto { Id = cat.Id, Title = cat.Title });
    }

    [HttpPost]
    public async Task<ActionResult<CategoryDto>> Create([FromBody] CreateCategoryRequest req)
    {
        if (!ModelState.IsValid) return BadRequest(ModelState);

        var exists = await _db.Categories.AnyAsync(c => c.Id == req.Id);
        if (exists) return Conflict($"Category with id {req.Id} already exists.");

        var entity = new Category { Id = req.Id, Title = req.Title };
        _db.Categories.Add(entity);
        await _db.SaveChangesAsync();

        var dto = new CategoryDto { Id = entity.Id, Title = entity.Title };
        return CreatedAtAction(nameof(GetById), new { id = dto.Id }, dto);
    }

    [HttpPut("{id:int}")]
    public async Task<ActionResult<CategoryDto>> Update(int id, [FromBody] CreateCategoryRequest req)
    {
        if (!ModelState.IsValid) return BadRequest(ModelState);
        if (id != req.Id) return BadRequest("Id in route must match Id in body.");

        var entity = await _db.Categories.FindAsync(id);
        if (entity is null) return NotFound();

        entity.Title = req.Title;
        await _db.SaveChangesAsync();

        return Ok(new CategoryDto { Id = entity.Id, Title = entity.Title });
    }

    [HttpDelete("{id:int}")]
    public async Task<IActionResult> Delete(int id)
    {
        var entity = await _db.Categories.FindAsync(id);
        if (entity is null) return NotFound();

        _db.Categories.Remove(entity);
        await _db.SaveChangesAsync();
        return NoContent();
    }
}
