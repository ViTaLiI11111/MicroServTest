namespace MenuService.DTOs;

public sealed class CreateDishRequest
{
    public int Id { get; set; }
    public string Title { get; set; } = "";
    public decimal Price { get; set; }
    public string Pepper { get; set; } = "";
    public string Color { get; set; } = "#ffffff";
    public int CategoryId { get; set; }
    public string? ImageBase64 { get; set; }
}
