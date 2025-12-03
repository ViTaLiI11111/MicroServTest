namespace OrderDispatch.Application.Interfaces;

public interface IAuthClient
{
    // Отримати токен конкретного юзера (напр. клієнта "Vitaliy")
    Task<string?> GetTokenAsync(string username, string role);

    // Отримати токени всіх юзерів ролі (напр. "Cook")
    Task<List<string>> GetTokensByRoleAsync(string role);
}