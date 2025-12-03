using System.Net.Http.Json;
using System.Text.Json;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.Logging; // <--- Додай
using OrderDispatch.Application.Interfaces;

namespace OrderDispatch.Infrastructure.Services;

public class AuthClient : IAuthClient
{
    private readonly HttpClient _http;
    private readonly string _baseUrl;
    private readonly ILogger<AuthClient> _logger; // <--- Додай логер

    public AuthClient(HttpClient http, IConfiguration cfg, ILogger<AuthClient> logger)
    {
        _http = http;
        _baseUrl = cfg["AuthService:BaseUrl"]?.TrimEnd('/') ?? "http://authservice";
        _logger = logger;
    }

    public async Task<List<string>> GetTokensByRoleAsync(string role)
    {
        var url = $"{_baseUrl}/api/notifications/tokens/role?role={role}";
        try
        {
            _logger.LogInformation($"[AuthClient] Requesting tokens for role: {role} from {url}");

            var response = await _http.GetAsync(url);

            if (!response.IsSuccessStatusCode)
            {
                var errorContent = await response.Content.ReadAsStringAsync();
                _logger.LogError($"[AuthClient] Error {response.StatusCode}: {errorContent}");
                return new List<string>();
            }

            var list = await response.Content.ReadFromJsonAsync<List<string>>();

            if (list == null || !list.Any())
            {
                _logger.LogWarning($"[AuthClient] Received empty list for role {role}");
                return new List<string>();
            }

            _logger.LogInformation($"[AuthClient] Received {list.Count} tokens for {role}. First: {list.First().Substring(0, 5)}...");

            return list
                .Where(t => !string.IsNullOrWhiteSpace(t))
                .Select(t => t.Trim())
                .ToList();
        }
        catch (Exception ex)
        {
            _logger.LogError($"[AuthClient] Exception: {ex.Message}");
            return new List<string>();
        }
    }

    // Те саме зроби і для GetTokenAsync (одиночного), щоб бачити помилки
    public async Task<string?> GetTokenAsync(string username, string role)
    {
        var url = $"{_baseUrl}/api/notifications/token?username={username}&role={role}";
        try
        {
            var response = await _http.GetAsync(url);
            if (!response.IsSuccessStatusCode)
            {
                _logger.LogError($"[AuthClient] Single token error: {response.StatusCode}");
                return null;
            }

            var json = await response.Content.ReadAsStringAsync();
            // Логуємо JSON, щоб перевірити регістр літер (Token vs token)
            _logger.LogInformation($"[AuthClient] JSON Response: {json}");

            using var doc = JsonDocument.Parse(json);

            // ВАЖЛИВО: JSON за замовчуванням camelCase ("token"), а в C# класі може бути "Token"
            // Спробуємо отримати властивість незалежно від регістру
            JsonElement tokenProp;

            // Шукаємо "token" або "Token"
            if (doc.RootElement.TryGetProperty("token", out tokenProp) ||
                doc.RootElement.TryGetProperty("Token", out tokenProp) ||
                doc.RootElement.TryGetProperty("fcmToken", out tokenProp))
            {
                var t = tokenProp.GetString();
                return string.IsNullOrWhiteSpace(t) ? null : t.Trim();
            }

            _logger.LogWarning("[AuthClient] Token property not found in JSON");
            return null;
        }
        catch (Exception ex)
        {
            _logger.LogError($"[AuthClient] Exception: {ex.Message}");
            return null;
        }
    }
}