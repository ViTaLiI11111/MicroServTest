using StackExchange.Redis;

namespace MenuService.Services
{
    public class RedisCacheService : ICacheService
    {
        private readonly IDatabase _db;

        public RedisCacheService(StackExchange.Redis.IConnectionMultiplexer redis)
        {
            _db = redis.GetDatabase();
        }

        public Task<string?> GetAsync(string key)
        {
            return _db.StringGetAsync(key).ContinueWith(t =>
            {
                var val = t.Result;
                return val.HasValue ? (string?)val.ToString() : null;
            });
        }

        public Task SetAsync(string key, string value, TimeSpan? ttl = null)
        {
            return _db.StringSetAsync(key, value, ttl);
        }

        public Task RemoveAsync(string key)
        {
            return _db.KeyDeleteAsync(key);
        }
    }
}
