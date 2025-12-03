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
        public DbSet<Courier> Couriers { get; set; }
        public DbSet<Cook> Cooks { get; set; }

        // --- НОВА ТАБЛИЦЯ ---
        public DbSet<UserToken> UserTokens { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<Client>().HasIndex(c => c.Username).IsUnique();
            modelBuilder.Entity<Waiter>().HasIndex(w => w.Username).IsUnique();
            modelBuilder.Entity<Courier>().HasIndex(c => c.Username).IsUnique();
            modelBuilder.Entity<Cook>().HasIndex(c => c.Username).IsUnique();
        }
    }
}