using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;

namespace USRest_Admin
{
    public sealed class MenuApiClient : IDisposable
    {
        private readonly HttpClient _http;
        private readonly JsonSerializerSettings _json;

        public MenuApiClient(string baseUrl)
        {
            _http = new HttpClient { BaseAddress = new Uri(baseUrl) };
            _json = new JsonSerializerSettings
            {
                ContractResolver = new CamelCasePropertyNamesContractResolver(),
                NullValueHandling = NullValueHandling.Ignore
            };
        }

        // =================== Categories ===================

        public Task<List<CategoryDto>> GetCategoriesAsync()
            => GetAsync<List<CategoryDto>>("/api/Categories");

        public Task<CategoryDto> GetCategoryAsync(int id)
            => GetAsync<CategoryDto>("/api/Categories/" + id);

        public Task<CategoryDto> CreateCategoryAsync(CreateCategoryRequest body)
            => PostAsync<CreateCategoryRequest, CategoryDto>("/api/Categories", body);

        public Task<CategoryDto> UpdateCategoryAsync(int id, CreateCategoryRequest body)
            => PutAsync<CreateCategoryRequest, CategoryDto>("/api/Categories/" + id, body);

        public Task DeleteCategoryAsync(int id)
            => DeleteAsync("/api/Categories/" + id);

        // ===================== Dishes ======================

        public Task<List<DishDto>> GetDishesAsync()
            => GetAsync<List<DishDto>>("/api/Dishes");

        public Task<DishDto> GetDishAsync(int id)
            => GetAsync<DishDto>("/api/Dishes/" + id);

        public Task<DishDto> CreateDishAsync(CreateDishRequest body)
            => PostAsync<CreateDishRequest, DishDto>("/api/Dishes", body);

        public Task<DishDto> UpdateDishAsync(int id, CreateDishRequest body)
            => PutAsync<CreateDishRequest, DishDto>("/api/Dishes/" + id, body);

        public Task DeleteDishAsync(int id)
            => DeleteAsync("/api/Dishes/" + id);

        // =============== HTTP helpers (C# 7.3) ===============

        private async Task<T> GetAsync<T>(string url)
        {
            using (var r = await _http.GetAsync(url).ConfigureAwait(false))
            {
                if ((int)r.StatusCode == 404) return default(T);
                r.EnsureSuccessStatusCode();
                var json = await r.Content.ReadAsStringAsync().ConfigureAwait(false);
                return JsonConvert.DeserializeObject<T>(json, _json);
            }
        }

        private async Task<TOut> PostAsync<TIn, TOut>(string url, TIn body)
        {
            var json = JsonConvert.SerializeObject(body, _json);
            using (var r = await _http.PostAsync(url, new StringContent(json, Encoding.UTF8, "application/json")).ConfigureAwait(false))
            {
                r.EnsureSuccessStatusCode();
                var resp = await r.Content.ReadAsStringAsync().ConfigureAwait(false);
                if (string.IsNullOrWhiteSpace(resp)) return default(TOut);
                return JsonConvert.DeserializeObject<TOut>(resp, _json);
            }
        }

        private async Task<TOut> PutAsync<TIn, TOut>(string url, TIn body)
        {
            var json = JsonConvert.SerializeObject(body, _json);
            using (var r = await _http.PutAsync(url, new StringContent(json, Encoding.UTF8, "application/json")).ConfigureAwait(false))
            {
                r.EnsureSuccessStatusCode();
                var resp = await r.Content.ReadAsStringAsync().ConfigureAwait(false);
                if (string.IsNullOrWhiteSpace(resp)) return default(TOut);
                return JsonConvert.DeserializeObject<TOut>(resp, _json);
            }
        }

        private async Task DeleteAsync(string url)
        {
            using (var r = await _http.DeleteAsync(url).ConfigureAwait(false))
            {
                r.EnsureSuccessStatusCode();
            }
        }

        public void Dispose() { _http.Dispose(); }
    }
}
