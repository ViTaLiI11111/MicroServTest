using OrderDispatch.Domain.Entities;

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
    string? ClientName,

    // --- НОВІ ПОЛЯ ---
    bool IsPaid,
    DateTimeOffset? PaidAt,
    // -----------------

    List<OrderItemResponse> Items
);

public record OrderItemResponse(
    int Id,
    int DishId,
    string DishTitle,
    int Qty,
    decimal Price,
    int StationId,          // <--- Нове
    string Status           // <--- Нове (Enum як рядок)
);

public record UpdateItemStatusRequest(OrderItemStatus Status); // DTO для зміни статусу