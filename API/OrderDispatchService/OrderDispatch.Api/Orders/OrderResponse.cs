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
    bool IsPaid,
    DateTimeOffset? PaidAt,

    // --- НОВЕ ПОЛЕ ---
    int? WaiterId,

    List<OrderItemResponse> Items
);

public record OrderItemResponse(
    int Id,
    int DishId,
    string DishTitle,
    int Qty,
    decimal Price,
    int StationId,
    string Status
);

public record UpdateItemStatusRequest(Domain.Entities.OrderItemStatus Status);