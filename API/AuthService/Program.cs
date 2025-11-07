using Microsoft.EntityFrameworkCore;
using AuthService.Data;

var builder = WebApplication.CreateBuilder(args);

// 1. Додаємо контролери
builder.Services.AddControllers();

// 2. Налаштовуємо DbContext для Postgres
var connectionString = builder.Configuration.GetConnectionString("AuthDb");
builder.Services.AddDbContext<AuthDbContext>(options =>
    options.UseNpgsql(connectionString)
);

// (Опціонально) Додаємо Swagger/OpenAPI
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();


var app = builder.Build();

// (Опціонально) Swagger UI
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

// (Опціонально) Автоматично застосовуємо міграції при старті
// Це зручно для Docker, щоб база сама оновлювалась
try
{
    using (var scope = app.Services.CreateScope())
    {
        var dbContext = scope.ServiceProvider.GetRequiredService<AuthDbContext>();
        dbContext.Database.Migrate();
    }
}
catch (Exception ex)
{
    // Логуємо помилку, якщо не вдалося підключитись до БД при старті
    var logger = app.Services.GetRequiredService<ILogger<Program>>();
    logger.LogError(ex, "An error occurred while migrating the database.");
}


app.UseHttpsRedirection();
app.UseAuthorization();
app.MapControllers();
app.Run();