using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

namespace USRest_Admin
{
    public class MenuApiClient
    {
        private readonly HttpClient _http;
        private readonly JsonSerializerOptions _json = new JsonSerializerOptions
        {
            PropertyNameCaseInsensitive = true
        };

        public MenuApiClient(string baseUrl)
        {
            _http = new HttpClient { BaseAddress = new Uri(baseUrl) };
            _http.Timeout = TimeSpan.FromSeconds(30);
        }

        // ---------- Categories ----------
        public Task<List<CategoryDto>> GetCategoriesAsync()
            => GetAsync<List<CategoryDto>>("/api/Categories");

        public Task<CategoryDto> GetCategoryAsync(int id)
            => GetAsync<CategoryDto>("/api/Categories/" + id);

        public Task<CategoryDto> CreateCategoryAsync(string title)
            => PostAsync<CreateCategoryRequest, CategoryDto>("/api/Categories",
                new CreateCategoryRequest { Title = title });

        public Task DeleteCategoryAsync(int id)
            => DeleteAsync("/api/Categories/" + id);

        // ---------- Dishes ----------
        public Task<List<DishDto>> GetDishesAsync()
            => GetAsync<List<DishDto>>("/api/Dishes");

        public Task<DishDto> GetDishAsync(int id)
            => GetAsync<DishDto>("/api/Dishes/" + id);

        public Task<DishDto> CreateDishAsync(CreateDishRequest body)
            => PostAsync<CreateDishRequest, DishDto>("/api/Dishes", body);

        public async Task<DishDto> UpdateDishAsync(int id, CreateDishRequest req)
            => await PutAsync<CreateDishRequest, DishDto>($"/api/Dishes/{id}", req);

        public Task DeleteDishAsync(int id)
            => DeleteAsync("/api/Dishes/" + id);

        // ---------- helpers (C# 7.3 friendly) ----------
        private async Task<T> GetAsync<T>(string url)
        {
            using (var r = await _http.GetAsync(url).ConfigureAwait(false))
            {
                r.EnsureSuccessStatusCode();
                var json = await r.Content.ReadAsStringAsync().ConfigureAwait(false);
                var obj = JsonSerializer.Deserialize<T>(json, _json);
                if (obj == null) throw new InvalidOperationException("Empty or invalid JSON.");
                return obj;
            }
        }

        private async Task<TOut> PostAsync<TIn, TOut>(string url, TIn body)
        {
            var json = JsonSerializer.Serialize(body);
            using (var content = new StringContent(json, Encoding.UTF8, "application/json"))
            using (var r = await _http.PostAsync(url, content).ConfigureAwait(false))
            {
                r.EnsureSuccessStatusCode();
                var resp = await r.Content.ReadAsStringAsync().ConfigureAwait(false);
                var obj = JsonSerializer.Deserialize<TOut>(resp, _json);
                if (obj == null) throw new InvalidOperationException("Empty or invalid JSON.");
                return obj;
            }
        }

        private async Task<TOut> PutAsync<TIn, TOut>(string url, TIn body)
        {
            var json = JsonSerializer.Serialize(body);
            using (var r = await _http.PutAsync(
                url,
                new StringContent(json, Encoding.UTF8, "application/json")))
            {
                r.EnsureSuccessStatusCode();
                var resp = await r.Content.ReadAsStringAsync();
                return JsonSerializer.Deserialize<TOut>(resp, _json);
            }
        }

        private async Task DeleteAsync(string url)
        {
            using (var r = await _http.DeleteAsync(url).ConfigureAwait(false))
            {
                r.EnsureSuccessStatusCode();
            }
        }
    }
}
