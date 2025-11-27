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

    public async Task<bool> CreateDeliveryRequestAsync(Guid orderId, string address, string phone, CancellationToken ct = default)
    {
        try
        {
            var request = new
            {
                OrderId = orderId, // Важливо: DeliveryService має очікувати Guid або string, а не int!
                Address = address,
                Phone = phone
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
}