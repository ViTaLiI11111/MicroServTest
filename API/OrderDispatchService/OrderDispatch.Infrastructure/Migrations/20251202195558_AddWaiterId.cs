using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace OrderDispatch.Infrastructure.Migrations
{
    /// <inheritdoc />
    public partial class AddWaiterId : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "WaiterId",
                table: "Orders",
                type: "integer",
                nullable: true);
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "WaiterId",
                table: "Orders");
        }
    }
}
