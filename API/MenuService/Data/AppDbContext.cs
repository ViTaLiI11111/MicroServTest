using Microsoft.EntityFrameworkCore;
using MenuService.Models;

namespace MenuService.Data
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options) { }

        public DbSet<Dish> Dishes => Set<Dish>();
        public DbSet<Category> Categories => Set<Category>();

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Category>()
                .HasMany<Dish>()
                .WithOne(d => d.Category)
                .HasForeignKey(d => d.CategoryId)
                .OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<Dish>()
                .Property(x => x.Price)
                .HasColumnType("numeric(12,2)"); // ціна з копійками

            base.OnModelCreating(modelBuilder);
        }
    }
}
