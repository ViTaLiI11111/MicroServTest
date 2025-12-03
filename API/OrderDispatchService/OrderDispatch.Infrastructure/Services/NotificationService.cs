using FirebaseAdmin;
using FirebaseAdmin.Messaging;
using Microsoft.Extensions.Logging;
using OrderDispatch.Application.Interfaces;

namespace OrderDispatch.Infrastructure.Services;

public class NotificationService : INotificationService
{
    private readonly ILogger<NotificationService> _logger;

    public NotificationService(ILogger<NotificationService> logger)
    {
        _logger = logger;
    }

    public async Task SendToTokenAsync(string token, string title, string body)
    {
        await SendSingleMessage(token, title, body);
    }

    public async Task SendToMultipleTokensAsync(List<string> tokens, string title, string body)
    {
        if (tokens == null || tokens.Count == 0) return;

        // Чистимо та видаляємо дублікати
        var uniqueTokens = tokens
            .Where(t => !string.IsNullOrWhiteSpace(t))
            .Select(t => t.Trim())
            .Distinct()
            .ToList();

        if (uniqueTokens.Count == 0) return;

        _logger.LogInformation($"[PUSH] Preparing to send to {uniqueTokens.Count} tokens via V1 API loop.");

        // В V1 API немає нативного "Multicast" у чистому вигляді, тому відправляємо паралельно або в циклі.
        // Для надійності та кращого контролю помилок 404/InvalidToken — проходимо циклом.

        var tasks = uniqueTokens.Select(token => SendSingleMessage(token, title, body));
        await Task.WhenAll(tasks);
    }

    private async Task SendSingleMessage(string token, string title, string body)
    {
        try
        {
            var message = new Message()
            {
                Token = token,
                Notification = new Notification()
                {
                    Title = title,
                    Body = body
                },
                // Android config для пріоритету та звуку
                Android = new AndroidConfig()
                {
                    Priority = Priority.High,
                    Notification = new AndroidNotification()
                    {
                        Sound = "default",
                        ChannelId = "OrderUpdates" // Має співпадати з каналом у клієнтському додатку
                    }
                }
            };

            // SendAsync ЗАВЖДИ використовує V1 endpoint: projects/{id}/messages:send
            string response = await FirebaseMessaging.DefaultInstance.SendAsync(message);
            _logger.LogInformation($"[PUSH SUCCESS] Sent to {token.Substring(0, 6)}... MsgID: {response}");
        }
        catch (FirebaseMessagingException fex)
        {
            // Обробка специфічних помилок Firebase
            if (fex.ErrorCode == ErrorCode.NotFound || fex.MessagingErrorCode == MessagingErrorCode.Unregistered)
            {
                _logger.LogWarning($"[PUSH TOKEN INVALID] Token is stale or app uninstalled: {token.Substring(0, 6)}...");
                // Тут можна було б видалити токен з БД, якщо б був доступ до репозиторію
            }
            else
            {
                _logger.LogError($"[PUSH ERROR] Firebase Error for {token.Substring(0, 6)}...: {fex.Message}");
            }
        }
        catch (Exception ex)
        {
            _logger.LogError($"[PUSH EXCEPTION] General error for {token.Substring(0, 6)}...: {ex.Message}");
        }
    }
}