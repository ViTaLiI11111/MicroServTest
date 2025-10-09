namespace MenuService.Services
{
    public interface ICacheService
    {
        Task<string?> GetAsync(string key);
        Task SetAsync(string key, string value, TimeSpan? ttl = null);
        Task RemoveAsync(string key);
    }
}
