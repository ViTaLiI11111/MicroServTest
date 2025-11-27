using Microsoft.EntityFrameworkCore;
using OrderDispatch.Domain.Entities;

namespace OrderDispatch.Infrastructure;

public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

    public DbSet<Order> Orders => Set<Order>();
    public DbSet<OrderItem> OrderItems => Set<OrderItem>();

    protected override void OnModelCreating(ModelBuilder b)
    {
        b.Entity<Order>(e =>
        {
            e.HasKey(x => x.Id);
            e.Property(x => x.Status).HasMaxLength(32).IsRequired();
            e.Property(x => x.Total).HasColumnType("numeric(12,2)");
            e.Property(x => x.CreatedAt).IsRequired();
            e.Property(x => x.UpdatedAt).IsRequired();
            e.HasMany(x => x.Items)
                .WithOne(i => i.Order!)
                .HasForeignKey(i => i.OrderId)
                .OnDelete(DeleteBehavior.Cascade);

            e.Property(x => x.Type).HasConversion<string>(); // Зберігаємо enum як текст
            e.Property(x => x.DeliveryAddress).HasMaxLength(500).IsRequired(false);
            e.Property(x => x.ClientPhone).HasMaxLength(50).IsRequired(false);
        });

        b.Entity<OrderItem>(e =>
        {
            e.HasKey(x => x.Id);
            e.Property(x => x.DishTitle).HasMaxLength(200).IsRequired();
            e.Property(x => x.Price).HasColumnType("numeric(12,2)");
        });
    }
}
