using System.ComponentModel.DataAnnotations;

namespace MenuService.DTOs;

public sealed class CreateCategoryRequest
{
    [Range(1, int.MaxValue, ErrorMessage = "Id must be >= 1")]
    public int Id { get; set; }

    [Required, MaxLength(100)]
    public string Title { get; set; } = string.Empty;
}
