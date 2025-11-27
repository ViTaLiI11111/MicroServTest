using Microsoft.EntityFrameworkCore;
using DeliveryService.Entities;

namespace DeliveryService.Data
{
    public class DeliveryDbContext : DbContext
    {
        public DeliveryDbContext(DbContextOptions<DeliveryDbContext> options) : base(options) { }

        public DbSet<Delivery> Deliveries { get; set; }
    }
}