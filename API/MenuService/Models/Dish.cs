namespace MenuService.Models
{
    public class Dish
    {
        public int Id { get; set; }
        public string Title { get; set; } = default!;
        public decimal Price { get; set; } // було string — робимо число
        public string? Pepper { get; set; } // гострота як текст або рівень
        public string? Color { get; set; }  // "#RRGGBB"
        public int CategoryId { get; set; }
        public Category? Category { get; set; }

        // Примітка: краще зберігати URL картинки, а не base64.
        public string? ImageBase64 { get; set; }
    }
}
