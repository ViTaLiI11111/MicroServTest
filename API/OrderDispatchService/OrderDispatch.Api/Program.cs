using Microsoft.EntityFrameworkCore;
using OrderDispatch.Infrastructure;
using OrderDispatch.Application.Menu;
using OrderDispatch.Infrastructure.Menu;
using OrderDispatch.Application.Delivery;
using OrderDispatch.Infrastructure.Delivery;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddDbContext<AppDbContext>(opt =>
    opt.UseNpgsql(builder.Configuration.GetConnectionString("DefaultConnection")));

builder.Services.AddHttpClient<IMenuClient, MenuClient>();

builder.Services.AddHttpClient<IDeliveryClient, DeliveryClient>();

var app = builder.Build();

app.UseSwagger();
app.UseSwaggerUI(c =>
{
    c.SwaggerEndpoint("/swagger/v1/swagger.json", "OrderDispatch API v1");
    c.RoutePrefix = "swagger"; // отже URL: /swagger
});
using (var scope = app.Services.CreateScope())
{
    var db = scope.ServiceProvider.GetRequiredService<AppDbContext>();
    db.Database.Migrate();   // створює таблиці в ТОЙ БД, що в конекшні цього сервісу
}

app.MapControllers();
app.Run();
