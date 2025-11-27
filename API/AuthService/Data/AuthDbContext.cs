using Microsoft.EntityFrameworkCore;
using AuthService.Entities;

namespace AuthService.Data
{
    public class AuthDbContext : DbContext
    {
        public AuthDbContext(DbContextOptions<AuthDbContext> options) : base(options)
        {
        }

        public DbSet<Client> Clients { get; set; }
        public DbSet<Waiter> Waiters { get; set; }
        public DbSet<Courier> Couriers { get; set; } // <--- ДОДАНО

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Client>()
                .HasIndex(c => c.Username)
                .IsUnique();

            modelBuilder.Entity<Waiter>()
                .HasIndex(w => w.Username)
                .IsUnique();

            // <--- ДОДАНО: Унікальний логін для кур'єра
            modelBuilder.Entity<Courier>()
                .HasIndex(c => c.Username)
                .IsUnique();
        }
    }
}