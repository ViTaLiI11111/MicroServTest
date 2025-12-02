using System.Net.Http.Json;
using Microsoft.Extensions.Configuration;
using OrderDispatch.Application.Delivery;

namespace OrderDispatch.Infrastructure.Delivery;

public class DeliveryClient : IDeliveryClient
{
    private readonly HttpClient _http;
    private readonly string _baseUrl;

    public DeliveryClient(HttpClient http, IConfiguration cfg)
    {
        _http = http;
        // ЗМІНА ТУТ: Використовуємо квадратні дужки замість GetValue
        var url = cfg["DeliveryService:BaseUrl"];

        _baseUrl = url?.TrimEnd('/') ?? "http://deliveryservice";
    }

    public async Task<bool> CreateDeliveryRequestAsync(Guid orderId, string address, string phone, string clientName, bool isPaid, decimal total, CancellationToken ct = default)
    {
        try
        {
            var request = new
            {
                OrderId = orderId, // Важливо: DeliveryService має очікувати Guid або string, а не int!
                Address = address,
                Phone = phone,
                ClientName = clientName,
                IsPaid = isPaid, // <--- Передаємо статус
                Total = total
            };

            var response = await _http.PostAsJsonAsync($"{_baseUrl}/api/deliveries", request, ct);
            return response.IsSuccessStatusCode;
        }
        catch
        {
            // Логування помилки тут було б доречним
            return false;
        }
    }

    public async Task<bool> MarkOrderAsReadyAsync(Guid orderId, CancellationToken ct = default)
    {
        try
        {
            // Формуємо URL: http://deliveryservice/api/deliveries/order/{guid}/ready
            var url = $"{_baseUrl}/api/deliveries/order/{orderId}/ready";

            // Створюємо PATCH запит
            // Використовуємо HttpRequestMessage, бо це найнадійніший спосіб зробити PATCH без тіла
            var request = new HttpRequestMessage(HttpMethod.Patch, url);

            var response = await _http.SendAsync(request, ct);

            return response.IsSuccessStatusCode;
        }
        catch (Exception)
        {
            // Тут добре було б додати логування (logger.LogError...)
            return false;
        }
    }

    public async Task<bool> MarkOrderAsPaidAsync(Guid orderId, CancellationToken ct = default)
    {
        try
        {
            // Викликаємо ендпоінт, який ми створили в Етапі 1
            var url = $"{_baseUrl}/api/deliveries/order/{orderId}/pay";
            var request = new HttpRequestMessage(HttpMethod.Patch, url);
            var response = await _http.SendAsync(request, ct);
            return response.IsSuccessStatusCode;
        }
        catch
        {
            return false;
        }
    }
}