namespace OrderDispatch.Application.Auth;

public interface IAuthClient
{
    // Отримати токен конкретного користувача (наприклад, Клієнта по імені)
    Task<string?> GetTokenAsync(string username, string role);

    // Отримати токени всіх користувачів певної ролі (наприклад, всі Офіціанти)
    Task<List<string>> GetTokensByRoleAsync(string role);
}