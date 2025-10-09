namespace MenuService.DTOs
{
    public class DishDto
    {
        public int Id { get; set; }
        public string Title { get; set; } = default!;
        public decimal Price { get; set; }
        public string? Pepper { get; set; }
        public string? Color { get; set; }
        public int CategoryId { get; set; }
        public string? ImageBase64 { get; set; }
    }
}
