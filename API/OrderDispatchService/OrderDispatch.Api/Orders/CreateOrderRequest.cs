namespace OrderDispatch.Api.Orders;

public record CreateOrderRequest(int TableNo, List<CreateOrderItem> Items);
public record CreateOrderItem(int DishId, int Qty);
