using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace MenuService.Models
{
    public class Category
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.None)]   // Власний Id
        public int Id { get; set; }

        [Required] public string Title { get; set; } = default!;

        public ICollection<Dish> Dishes { get; set; } = new List<Dish>();
    }
}
