using OrderDispatch.Domain.Entities;

namespace OrderDispatch.Api.Orders;

public record CreateOrderRequest(
    int TableNo,
    List<CreateOrderItem> Items, // <--- Використовує клас нижче
    OrderType Type = OrderType.DineIn,
    string? Address = null,
    string? Phone = null
);

public record CreateOrderItem(int DishId, int Qty);