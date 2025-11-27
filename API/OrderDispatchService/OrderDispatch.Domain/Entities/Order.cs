namespace OrderDispatch.Domain.Entities;

public enum OrderType
{
    DineIn,   // В закладі
    Delivery  // Доставка
}

public class Order
{
    public Guid Id { get; set; } = Guid.NewGuid();

    public OrderType Type { get; set; } = OrderType.DineIn; // <--- Нове поле

    public int TableNo { get; set; }

    // Інформація для доставки (може бути null, якщо це DineIn)
    public string? DeliveryAddress { get; set; } // <--- Нове поле
    public string? ClientPhone { get; set; }     // <--- Нове поле

    public string Status { get; set; } = "new";
    public decimal Total { get; set; }
    public DateTimeOffset CreatedAt { get; set; } = DateTimeOffset.UtcNow;
    public DateTimeOffset UpdatedAt { get; set; } = DateTimeOffset.UtcNow;

    public List<OrderItem> Items { get; set; } = new();
}