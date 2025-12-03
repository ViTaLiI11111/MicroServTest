using FirebaseAdmin;
using Google.Apis.Auth.OAuth2;
using Microsoft.EntityFrameworkCore;
using OrderDispatch.Infrastructure;
using OrderDispatch.Application.Menu;
using OrderDispatch.Infrastructure.Menu;
using OrderDispatch.Application.Delivery;
using OrderDispatch.Infrastructure.Delivery;
// Нові імпорти
using OrderDispatch.Application.Interfaces;
using OrderDispatch.Infrastructure.Services;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddDbContext<AppDbContext>(opt =>
    opt.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

// Реєстрація старих клієнтів
builder.Services.AddHttpClient<IMenuClient, MenuClient>();
builder.Services.AddHttpClient<IDeliveryClient, DeliveryClient>();

// --- РЕЄСТРАЦІЯ НОВИХ СЕРВІСІВ ---

// 1. AuthClient
builder.Services.AddHttpClient<IAuthClient, AuthClient>();

// 2. NotificationService
builder.Services.AddSingleton<INotificationService, NotificationService>();

// 3. Ініціалізація Firebase (Один раз при старті)
var firebaseConfigPath = Path.Combine(builder.Environment.ContentRootPath, "firebase-service-account.json");

if (File.Exists(firebaseConfigPath))
{
    // Читаємо файл вручну, щоб отримати ProjectId для логування та перевірки
    var jsonContent = File.ReadAllText(firebaseConfigPath);
    using var jsonDoc = System.Text.Json.JsonDocument.Parse(jsonContent);

    // Спробуємо дістати project_id
    string? projectId = null;
    if (jsonDoc.RootElement.TryGetProperty("project_id", out var pidProp))
    {
        projectId = pidProp.GetString();
    }

    if (FirebaseApp.DefaultInstance == null)
    {
        FirebaseApp.Create(new AppOptions()
        {
            Credential = GoogleCredential.FromFile(firebaseConfigPath),
            ProjectId = projectId // <--- ЯВНО ВКАЗУЄМО PROJECT ID
        });

        Console.WriteLine($"[Firebase] Initialized. Project ID: {projectId}");
    }
}
else
{
    Console.WriteLine($"[Firebase] WARNING: Config file not found at {firebaseConfigPath}");
}

// --------------------------------

var app = builder.Build();

app.UseSwagger();
app.UseSwaggerUI(c =>
{
    c.SwaggerEndpoint("/swagger/v1/swagger.json", "OrderDispatch API v1");
    c.RoutePrefix = "swagger";
});

using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    db.Database.Migrate();
}

app.MapControllers();
app.Run();