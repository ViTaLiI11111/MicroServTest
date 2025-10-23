using System.Net.Http;
using System.Net.Http.Json;
using Microsoft.Extensions.Configuration;
using OrderDispatch.Application.Menu;

namespace OrderDispatch.Infrastructure.Menu;

public class MenuClient : IMenuClient
{
    private readonly HttpClient _http;
    private readonly string _base;

    public MenuClient(HttpClient http, IConfiguration cfg)
    {
        _http = http;
        _base = cfg.GetValue<string>("MenuService:BaseUrl")?.TrimEnd('/')
                ?? "http://menusvc"; // ім'я сервісу у docker-compose
    }

    public async Task<DishDto?> GetDishAsync(int dishId, CancellationToken ct = default)
        => await _http.GetFromJsonAsync<DishDto>($"{_base}/api/dishes/{dishId}", ct);
}
