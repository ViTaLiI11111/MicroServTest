using System.ComponentModel.DataAnnotations;

namespace OrderDispatch.Domain.Entities;

public enum OrderType
{
    DineIn,
    Delivery
}

public class Order
{
    public Guid Id { get; set; } = Guid.NewGuid();

    public OrderType Type { get; set; } = OrderType.DineIn;

    public int TableNo { get; set; }

    public string? ClientName { get; set; }

    public string? DeliveryAddress { get; set; }
    public string? ClientPhone { get; set; }

    public string Status { get; set; } = "new";
    public decimal Total { get; set; }
    public DateTimeOffset CreatedAt { get; set; } = DateTimeOffset.UtcNow;
    public DateTimeOffset UpdatedAt { get; set; } = DateTimeOffset.UtcNow;
    public bool IsPaid { get; set; } = false;
    public DateTimeOffset? PaidAt { get; set; }

    // --- НОВЕ ПОЛЕ ---
    // ID офіціанта, який обслуговує цей столик (може бути null, якщо ще ніхто не взяв)
    public int? WaiterId { get; set; }
    // -----------------

    public List<OrderItem> Items { get; set; } = new();
}