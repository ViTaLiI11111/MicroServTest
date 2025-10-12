namespace USRest_Admin
{
    public class CategoryDto
    {
        public int Id { get; set; }
        public string Title { get; set; }
    }

    public class DishDto
    {
        public int Id { get; set; }
        public int CategoryId { get; set; }
        public string Title { get; set; }
        public decimal Price { get; set; }          // EF: decimal
        public string Pepper { get; set; }
        public string ImageBase64 { get; set; }
        public string Color { get; set; }
    }

    // для створення/оновлення (можеш і DishDto використовувати, але я відділив)
    public class CreateDishRequest
    {
        public int CategoryId { get; set; }
        public string Title { get; set; }
        public decimal Price { get; set; }
        public string Pepper { get; set; }
        public string ImageBase64 { get; set; }
        public string Color { get; set; }
    }

    public class CreateCategoryRequest
    {
        public string Title { get; set; }
    }
}
