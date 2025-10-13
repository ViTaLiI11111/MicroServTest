using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MenuService.Models
{
    public class Dish
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.None)]   // Власний Id (не identity)
        public int Id { get; set; }

        [Required] public string Title { get; set; } = default!;
        [Required] public decimal Price { get; set; }
        public string Pepper { get; set; } = "";
        public string Color { get; set; } = "#ffffff";

        [Required] public int CategoryId { get; set; }
        public Category Category { get; set; } = default!;

        public string? ImageBase64 { get; set; }
    }
}
