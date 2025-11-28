package com.example.ukrainianstylerestaurant.ui.order;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.ukrainianstylerestaurant.LocalStorage;
import com.example.ukrainianstylerestaurant.R;
import com.example.ukrainianstylerestaurant.data.OrdersRepository;
import com.example.ukrainianstylerestaurant.model.Course;
import com.example.ukrainianstylerestaurant.model.CreateOrderRequest;
import com.example.ukrainianstylerestaurant.model.Order;
import com.example.ukrainianstylerestaurant.model.OrderItemRequest;
import com.example.ukrainianstylerestaurant.ui.home.HomeFragment;

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

        executorService = Executors.newSingleThreadExecutor();
        mainThreadHandler = new Handler(Looper.getMainLooper());

        ordersList = view.findViewById(R.id.orders_list);
        sumArea = view.findViewById(R.id.sum_area);
        btnBuy = view.findViewById(R.id.button);
        btnClearCart = view.findViewById(R.id.button2);

        loadOrderData();

        btnBuy.setOnClickListener(v -> showOrderTypeDialog());
        btnClearCart.setOnClickListener(v -> toClearCart());
    }

    private void loadOrderData() {
        List<String> coursesOrder = new ArrayList<>();
        List<String> coursesSum = new ArrayList<>();
        float sum = 0;

        // Беремо дані зі статичного списку в HomeFragment (кеш меню)
        for (Course c : HomeFragment.fullCoursesList) {
            if (Order.items_id.contains(c.getId())) {
                coursesOrder.add(c.getTitle());
                coursesOrder.add(c.getPrice());
                try {
                    sum += Float.parseFloat(c.getPrice());
                } catch (NumberFormatException e) { /* ігноруємо */ }
            }
        }

        coursesSum.add(String.valueOf(sum));

        if (getContext() != null) {
            ordersList.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, coursesOrder));
            sumArea.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, coursesSum));
        }
    }

    // --- ЛОГИКА ЗАМОВЛЕННЯ ---

    private void showOrderTypeDialog() {
        if (Order.items_id.isEmpty()) {
            Toast.makeText(requireContext(), "Кошик пустий", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- НОВА ПЕРЕВІРКА: Чи є ім'я? ---
        String clientName = LocalStorage.getClientName(requireContext());
        if (clientName.isEmpty()) {
            Toast.makeText(requireContext(), "Будь ласка, вкажіть ваше ім'я в профілі!", Toast.LENGTH_LONG).show();
            // Перекидаємо в профіль
            Navigation.findNavController(requireView()).navigate(R.id.nav_profile);
            return; // Зупиняємо замовлення
        }
        // ----------------------------------

        String[] options = {"🍽️ У закладі (на столик)", "🛵 Доставка додому"};

        new AlertDialog.Builder(requireContext())
                .setTitle("Оберіть спосіб отримання")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Варіант 1: У закладі
                        processOrder(0);
                    } else {
                        // Варіант 2: Доставка
                        String address = LocalStorage.getClientAddress(requireContext());
                        String phone = LocalStorage.getClientPhone(requireContext());

                        // Перевіряємо ще й адресу та телефон
                        if (address.isEmpty() || phone.isEmpty()) {
                            Toast.makeText(requireContext(), "Для доставки вкажіть адресу та телефон!", Toast.LENGTH_LONG).show();
                            Navigation.findNavController(requireView()).navigate(R.id.nav_profile);
                        } else {
                            processOrder(1);
                        }
                    }
                })
                .show();
    }

    private void processOrder(int type) {
        executorService.execute(() -> {
            try {
                // 1. Збираємо список товарів
                List<OrderItemRequest> items = new ArrayList<>();
                for (Integer dishId : Order.items_id) {
                    items.add(new OrderItemRequest(dishId, 1, null));
                }

                // 2. ОТРИМУЄМО ІМ'Я КЛІЄНТА
                String clientName = LocalStorage.getClientName(requireContext());
                // Якщо ім'я не вказано в профілі, беремо логін або "Гість"
                if (clientName == null || clientName.isEmpty()) {
                    clientName = LocalStorage.getUsername(requireContext());
                }

                CreateOrderRequest req;

                // 3. Формуємо запит (з урахуванням імені)
                if (type == 0) {
                    // DINE IN (У закладі)
                    int tableNo = LocalStorage.getTableNumber(requireContext());
                    req = new CreateOrderRequest(tableNo, items, clientName);
                } else {
                    // DELIVERY (Доставка)
                    String address = LocalStorage.getClientAddress(requireContext());
                    String phone = LocalStorage.getClientPhone(requireContext());
                    req = new CreateOrderRequest(items, address, phone, clientName);
                }

                // 4. Відправляємо на сервер
                OrdersRepository repo = new OrdersRepository();
                com.example.ukrainianstylerestaurant.model.OrderResponse response = repo.createOrder(req);

                // 5. Обробляємо відповідь
                mainThreadHandler.post(() -> {
                    if (response != null) {
                        String msg = (type == 0) ? "Замовлення передано на кухню!" : "Заявку на доставку створено!";
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();

                        if (response.id != null) {
                            LocalStorage.saveActiveOrderId(requireContext(), response.id);
                        }

                        Order.items_id.clear();
                        loadOrderData(); // Метод, який оновлює список (має бути у вашому класі)
                    } else {
                        Toast.makeText(requireContext(), "Помилка сервера при створенні замовлення", Toast.LENGTH_LONG).show();
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

    public void toClearCart() {
        if (!Order.items_id.isEmpty()) {
            Order.items_id.clear();
            Toast.makeText(requireContext(), "Кошик очищено!", Toast.LENGTH_LONG).show();
            loadOrderData();
        } else {
            Toast.makeText(requireContext(), "Кошик пустий!", Toast.LENGTH_LONG).show();
        }
    }
}