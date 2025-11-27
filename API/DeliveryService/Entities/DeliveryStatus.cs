namespace DeliveryService.Entities
{
    public enum DeliveryStatus
    {
        Created,    // Замовлення готове, шукаємо кур'єра
        Assigned,   // Кур'єр взяв замовлення
        PickedUp,   // Кур'єр забрав їжу з ресторану
        Delivered,  // Клієнт отримав їжу
        Cancelled   // Щось пішло не так
    }
}