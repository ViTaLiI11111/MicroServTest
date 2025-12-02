using System.Threading;
using System.Threading.Tasks;

namespace OrderDispatch.Application.Menu;

public interface IMenuClient
{
    Task<DishDto?> GetDishAsync(int dishId, CancellationToken ct = default);
}

public record DishDto(int Id, string Title, decimal Price, int StationId);
