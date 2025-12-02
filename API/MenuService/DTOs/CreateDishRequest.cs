namespace MenuService.DTOs;

public class CreateDishRequest
{
    public int Id { get; set; }
    public string Title { get; set; }
    public decimal Price { get; set; }
    public string Pepper { get; set; }
    public string Color { get; set; }
    public int CategoryId { get; set; }
    public string? ImageBase64 { get; set; }

    // --- НОВЕ ПОЛЕ ---
    public int StationId { get; set; } // 1=Гарячий, 2=Холодний, 3=Гриль, 4=Піца
}