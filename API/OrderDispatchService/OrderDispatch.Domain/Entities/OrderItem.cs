namespace OrderDispatch.Domain.Entities;

public class OrderItem
{
    public int Id { get; set; }
    public Guid OrderId { get; set; }

    public int DishId { get; set; }          // посилання на MenuService.Dishes.Id
    public string DishTitle { get; set; } = ""; // кешуємо назву, щоб не тягнути кожного разу
    public decimal Price { get; set; }       // ціна з MenuService (на момент замовлення)
    public int Qty { get; set; }

    public Order? Order { get; set; }
}
