using System;
using System.Collections.Generic;

namespace OrderDispatch.Domain.Entities;

public class Order
{
    public Guid Id { get; set; } = Guid.NewGuid();
    public int TableNo { get; set; }
    public string Status { get; set; } = "new"; // new|inprogress|done|paid
    public decimal Total { get; set; }
    public DateTimeOffset CreatedAt { get; set; } = DateTimeOffset.UtcNow;
    public DateTimeOffset UpdatedAt { get; set; } = DateTimeOffset.UtcNow;

    public List<OrderItem> Items { get; set; } = new();
}
