namespace OrderDispatch.Application.Delivery;

public interface IDeliveryClient
{
    // Ми передаємо ID замовлення (Guid), адресу та телефон
    Task<bool> CreateDeliveryRequestAsync(Guid orderId, string address, string phone, CancellationToken ct = default);
}