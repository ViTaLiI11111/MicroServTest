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

    // --- НОВЕ ПОЛЕ ---
    public string? ClientName { get; set; }

    public string? DeliveryAddress { get; set; }
    public string? ClientPhone { get; set; }

    public string Status { get; set; } = "new";
    public decimal Total { get; set; }
    public DateTimeOffset CreatedAt { get; set; } = DateTimeOffset.UtcNow;
    public DateTimeOffset UpdatedAt { get; set; } = DateTimeOffset.UtcNow;

    public List<OrderItem> Items { get; set; } = new();
}