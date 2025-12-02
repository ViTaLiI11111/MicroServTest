namespace OrderDispatch.Domain.Entities;

public enum OrderItemStatus
{
    Pending, // Чекає (в черзі)
    Cooking, // Кухар натиснув "Почав"
    Ready    // Кухар натиснув "Готово"
}