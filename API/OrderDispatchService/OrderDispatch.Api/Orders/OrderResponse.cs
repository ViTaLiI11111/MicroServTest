using System;
using System.Collections.Generic;

namespace OrderDispatch.Api.Orders;

public record OrderResponse(
    Guid Id,
    int TableNo,
    string Status,
    string Type,
    decimal Total,
    DateTimeOffset CreatedAt,
    string? DeliveryAddress,
    string? ClientPhone,
    string? ClientName, // <--- Важливе нове поле
    List<OrderItemResponse> Items
);

public record OrderItemResponse(int Id, int DishId, string DishTitle, int Qty, decimal Price);