using System.Threading;
using System.Threading.Tasks;

namespace OrderDispatch.Application.Delivery;

public interface IDeliveryClient
{
    // Оновлена сигнатура: додали isPaid та total
    Task<bool> CreateDeliveryRequestAsync(
        Guid orderId,
        string address,
        string phone,
        string clientName,
        bool isPaid,
        decimal total,
        CancellationToken ct = default);

    Task<bool> MarkOrderAsReadyAsync(Guid orderId, CancellationToken ct = default);

    // Новий метод
    Task<bool> MarkOrderAsPaidAsync(Guid orderId, CancellationToken ct = default);
}