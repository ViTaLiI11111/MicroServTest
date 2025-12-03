namespace OrderDispatch.Application.Interfaces;

public interface INotificationService
{
    Task SendToTokenAsync(string token, string title, string body);
    Task SendToMultipleTokensAsync(List<string> tokens, string title, string body);
}