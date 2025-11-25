package com.example.ukrainianstylerestaurant.ui.order;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ukrainianstylerestaurant.LocalStorage;
import com.example.ukrainianstylerestaurant.R;
import com.example.ukrainianstylerestaurant.model.Course;
import com.example.ukrainianstylerestaurant.model.Order;
import com.example.ukrainianstylerestaurant.data.OrdersRepository;
import com.example.ukrainianstylerestaurant.model.CreateOrderRequest;
import com.example.ukrainianstylerestaurant.model.OrderItemRequest;
import com.example.ukrainianstylerestaurant.ui.home.HomeFragment; // Щоб отримати fullCoursesList

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderFragment extends Fragment {

    private ListView ordersList;
    private ListView sumArea;
    private Button btnBuy;
    private Button btnClearCart;

    private ExecutorService executorService;
    private Handler mainThreadHandler;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ініціалізація
        executorService = Executors.newSingleThreadExecutor();
        mainThreadHandler = new Handler(Looper.getMainLooper());

        // Пошук View (без посилань на бічну панель)
        ordersList = view.findViewById(R.id.orders_list);
        sumArea = view.findViewById(R.id.sum_area);
        btnBuy = view.findViewById(R.id.button); // toBuy
        btnClearCart = view.findViewById(R.id.button2); // toClearCart

        // Завантаження та відображення даних
        loadOrderData();

        // Встановлення обробників кліків
        btnBuy.setOnClickListener(v -> toBuy(v));
        btnClearCart.setOnClickListener(v -> toClearCart(v));

        // ВАЖЛИВО: Видаліть виклики onClicks для навігаційних кнопок:
        // aboutUs(), goToContacts(), mainPage() більше не потрібні,
        // оскільки навігація відбувається через Navigation Drawer.
    }

    private void loadOrderData() {
        List<String> coursesOrder = new ArrayList<>();
        List<String> coursesSum = new ArrayList<>();
        float sum = 0;

        // Використовуємо статичний список курсів з HomeFragment
        for (Course c : HomeFragment.fullCoursesList) {
            if (Order.items_id.contains(c.getId())) {
                coursesOrder.add(c.getTitle());
                // Якщо ціна не String, а число, потрібно змінити.
                coursesOrder.add(c.getPrice());

                try {
                    float n = Float.parseFloat(c.getPrice());
                    sum += n;
                } catch (NumberFormatException e) {
                    // Обробка помилки, якщо ціна не є числом
                }
            }
        }

        String stringSum = Float.toString(sum);
        coursesSum.add(stringSum);

        // Використовуємо requireContext()
        ordersList.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, coursesOrder));
        sumArea.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, coursesSum));
    }

    // --- Обробники кліків (Перенесені з OrderPage.java) ---

    public void toBuy(View view) {
        if (Order.items_id.isEmpty()) {
            Toast.makeText(requireContext(), "Замовте страву, будь ласка.", Toast.LENGTH_LONG).show();
            return;
        }

        executorService.execute(() -> {
            try {
                // Отримуємо дані
                int tableNo = LocalStorage.getTableNumber(requireContext());
                List<OrderItemRequest> items = new ArrayList<>();
                for (Integer dishId : Order.items_id) {
                    items.add(new OrderItemRequest(dishId, 1, null));
                }
                CreateOrderRequest req = new CreateOrderRequest(tableNo, items);

                // Виконуємо запит
                OrdersRepository repo = new OrdersRepository();
                boolean ok = repo.createOrder(req);

                mainThreadHandler.post(() -> {
                    if (ok) {
                        Toast.makeText(requireContext(), "Замовлення відправлено офіціанту!", Toast.LENGTH_LONG).show();
                        Order.items_id.clear();
                        // У Fragment для оновлення використовуємо навігацію або перевантаження
                        // Для простоти використовуємо NavController.navigate() до поточного Fragment
                        // або просто викликаємо loadOrderData().
                        loadOrderData();
                    } else {
                        Toast.makeText(requireContext(), "Помилка серверу при створенні замовлення", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                mainThreadHandler.post(() ->
                        Toast.makeText(requireContext(), "Мережева помилка: " + ex.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        });
    }

    public void toClearCart(View view) {
        if (!Order.items_id.isEmpty()) {
            Order.items_id.clear();
            Toast.makeText(requireContext(), "Кошик очищено!", Toast.LENGTH_LONG).show();
            loadOrderData(); // Оновлюємо список
        } else {
            Toast.makeText(requireContext(), "Кошик пустий!", Toast.LENGTH_LONG).show();
        }
    }
}