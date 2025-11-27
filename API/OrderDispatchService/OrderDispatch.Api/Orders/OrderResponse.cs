using System;
using System.Collections.Generic;

namespace OrderDispatch.Api.Orders;

public record OrderResponse(
    Guid Id,
    int TableNo,
    string Status,
    string Type,            // <--- Нове
    decimal Total,
    DateTimeOffset CreatedAt,
    string? DeliveryAddress, // <--- Нове
    string? ClientPhone,     // <--- Нове
    List<OrderItemResponse> Items
);

public record OrderItemResponse(int Id, int DishId, string DishTitle, int Qty, decimal Price);
