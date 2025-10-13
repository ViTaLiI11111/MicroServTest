using Microsoft.EntityFrameworkCore;
using MenuService.Models;

namespace MenuService.Data
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

        // DbSet-и, щоб _db.Dishes / _db.Categories існували
        public DbSet<Dish> Dishes => Set<Dish>();
        public DbSet<Category> Categories => Set<Category>();

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // Dish
            modelBuilder.Entity<Dish>(e =>
            {
                e.HasKey(x => x.Id);
                e.Property(x => x.Id).ValueGeneratedNever();              // власний Id
                e.Property(x => x.Title).IsRequired();
                e.Property(x => x.Price).HasColumnType("numeric(12,2)");
                e.HasOne(x => x.Category)
                 .WithMany(c => c.Dishes)
                 .HasForeignKey(x => x.CategoryId)
                 .OnDelete(DeleteBehavior.Restrict);
            });

            // Category (якщо теж хочеш власний Id)
            modelBuilder.Entity<Category>(e =>
            {
                e.HasKey(x => x.Id);
                e.Property(x => x.Id).ValueGeneratedNever();
                e.Property(x => x.Title).IsRequired();
            });
        }
    }
}
