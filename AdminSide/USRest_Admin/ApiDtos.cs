using System;

namespace USRest_Admin
{
    // -------------------- Categories --------------------
    public sealed class CategoryDto
    {
        public int Id { get; set; }
        public string Title { get; set; } = string.Empty;
    }

    public sealed class CreateCategoryRequest
    {
        public int Id { get; set; }            // ручне введення Id
        public string Title { get; set; } = string.Empty;
    }

    // ---------------------- Dishes ----------------------
    public sealed class DishDto
    {
        public int Id { get; set; }
        public string Title { get; set; } = string.Empty;

        // у БД numeric(12,2) → тут decimal
        public decimal Price { get; set; }

        public string Pepper { get; set; } = string.Empty;
        public string Color { get; set; } = "#ffffff";

        public int CategoryId { get; set; }

        // base64 png/jpg
        public string ImageBase64 { get; set; } = null;
        public int StationId { get; set; }
    }

    public sealed class CreateDishRequest
    {
        public int Id { get; set; }                 // дозволяємо ручне Id
        public string Title { get; set; } = string.Empty;
        public decimal Price { get; set; }
        public string Pepper { get; set; } = string.Empty;
        public string Color { get; set; } = "#ffffff";
        public int CategoryId { get; set; }
        public string ImageBase64 { get; set; } = null;
        public int StationId { get; set; }
    }
}
