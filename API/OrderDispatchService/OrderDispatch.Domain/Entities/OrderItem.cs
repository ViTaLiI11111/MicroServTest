namespace OrderDispatch.Domain.Entities;

public class OrderItem
{
    public int Id { get; set; }
    public Guid OrderId { get; set; }

    public int DishId { get; set; }
    public string DishTitle { get; set; } = "";
    public decimal Price { get; set; }
    public int Qty { get; set; }

    // --- НОВІ ПОЛЯ ---
    public int StationId { get; set; } // На який планшет це відправити (1, 2...)
    public OrderItemStatus Status { get; set; } = OrderItemStatus.Pending;
    // -----------------

    public Order? Order { get; set; }
}