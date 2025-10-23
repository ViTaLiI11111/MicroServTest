using System;
using System.Collections.Generic;

namespace OrderDispatch.Api.Orders;

public record OrderResponse(
    Guid Id, int TableNo, string Status, decimal Total, DateTimeOffset CreatedAt,
    List<OrderItemResponse> Items);

public record OrderItemResponse(int Id, int DishId, string DishTitle, int Qty, decimal Price);
