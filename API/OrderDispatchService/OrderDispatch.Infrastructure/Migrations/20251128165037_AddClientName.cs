using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace OrderDispatch.Infrastructure.Migrations
{
    /// <inheritdoc />
    public partial class AddClientName : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "ClientName",
                table: "Orders",
                type: "text",
                nullable: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "ClientName",
                table: "Orders");
        }
    }
}
