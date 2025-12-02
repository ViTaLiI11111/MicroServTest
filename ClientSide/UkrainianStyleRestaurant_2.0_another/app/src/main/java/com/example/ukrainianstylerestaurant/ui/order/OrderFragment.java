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
import com.example.ukrainianstylerestaurant.model.OrderResponse;
import com.example.ukrainianstylerestaurant.ui.home.HomeFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map; // Додано імпорт
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

        btnBuy.setOnClickListener(v -> startCheckoutProcess());
        btnClearCart.setOnClickListener(v -> toClearCart());
    }

    private void loadOrderData() {
        List<String> coursesOrder = new ArrayList<>();
        List<String> coursesSum = new ArrayList<>();
        float totalSum = 0;

        // Проходимось по всіх доступних стравах
        for (Course c : HomeFragment.fullCoursesList) {
            // Перевіряємо, чи є ID цієї страви в нашій Map
            if (Order.itemsMap.containsKey(c.getId())) {

                int qty = Order.itemsMap.get(c.getId()); // Отримуємо кількість

                // Формуємо рядок: "Борщ x2"
                coursesOrder.add(c.getTitle() + " x" + qty);

                // Ціна за одну порцію
                coursesOrder.add(c.getPrice());

                try {
                    float price = Float.parseFloat(c.getPrice());
                    totalSum += price * qty; // Додаємо до суми (ціна * кількість)
                } catch (NumberFormatException e) {
                    /* ігноруємо */
                }
            }
        }

        coursesSum.add(String.valueOf(totalSum));

        if (getContext() != null) {
            ordersList.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, coursesOrder));
            sumArea.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, coursesSum));
        }
    }

    private void startCheckoutProcess() {
        if (Order.itemsMap.isEmpty()) {
            Toast.makeText(requireContext(), "Кошик пустий", Toast.LENGTH_SHORT).show();
            return;
        }

        String clientName = LocalStorage.getClientName(requireContext());
        if (clientName.isEmpty()) {
            Toast.makeText(requireContext(), "Будь ласка, вкажіть ваше ім'я в профілі!", Toast.LENGTH_LONG).show();
            Navigation.findNavController(requireView()).navigate(R.id.nav_profile);
            return;
        }

        showDeliveryDialog();
    }

    private void showDeliveryDialog() {
        String[] options = {"🍽️ У закладі (на столик)", "🛵 Доставка додому"};

        new AlertDialog.Builder(requireContext())
                .setTitle("Оберіть спосіб отримання")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showPaymentDialog(0);
                    } else {
                        String address = LocalStorage.getClientAddress(requireContext());
                        String phone = LocalStorage.getClientPhone(requireContext());

                        if (address.isEmpty() || phone.isEmpty()) {
                            Toast.makeText(requireContext(), "Для доставки вкажіть адресу та телефон!", Toast.LENGTH_LONG).show();
                            Navigation.findNavController(requireView()).navigate(R.id.nav_profile);
                        } else {
                            showPaymentDialog(1);
                        }
                    }
                })
                .show();
    }

    private void showPaymentDialog(int orderType) {
        String[] options = {"💳 Оплатити зараз (Картка)", "💵 Оплата при отриманні"};

        new AlertDialog.Builder(requireContext())
                .setTitle("Спосіб оплати")
                .setItems(options, (dialog, which) -> {
                    boolean payImmediately = (which == 0);
                    processOrder(orderType, payImmediately);
                })
                .show();
    }

    private void processOrder(int type, boolean payImmediately) {
        Toast.makeText(requireContext(), "Обробка замовлення...", Toast.LENGTH_SHORT).show();

        executorService.execute(() -> {
            try {
                // 1. Збираємо дані з Map
                List<OrderItemRequest> items = new ArrayList<>();

                // Проходимо по кожному запису в Map (ID -> Quantity)
                for (Map.Entry<Integer, Integer> entry : Order.itemsMap.entrySet()) {
                    int dishId = entry.getKey();
                    int qty = entry.getValue();

                    // Додаємо в список для відправки на сервер
                    items.add(new OrderItemRequest(dishId, qty, null));
                }

                String clientName = LocalStorage.getClientName(requireContext());
                if (clientName.isEmpty()) clientName = LocalStorage.getUsername(requireContext());

                CreateOrderRequest req;

                if (type == 0) {
                    int tableNo = LocalStorage.getTableNumber(requireContext());
                    req = new CreateOrderRequest(tableNo, items, clientName);
                } else {
                    String address = LocalStorage.getClientAddress(requireContext());
                    String phone = LocalStorage.getClientPhone(requireContext());
                    req = new CreateOrderRequest(items, address, phone, clientName);
                }

                OrdersRepository repo = new OrdersRepository();
                OrderResponse response = repo.createOrder(req);

                if (response != null && response.id != null) {
                    LocalStorage.saveActiveOrderId(requireContext(), response.id);

                    boolean paymentSuccess = false;
                    if (payImmediately) {
                        paymentSuccess = repo.payOrder(response.id);
                    }

                    boolean finalPaymentSuccess = paymentSuccess;

                    mainThreadHandler.post(() -> {
                        String msg = (type == 0) ? "Замовлення передано на кухню!" : "Заявку на доставку створено!";

                        if (payImmediately) {
                            if (finalPaymentSuccess) {
                                msg += "\n✅ Оплачено успішно!";
                            } else {
                                msg += "\n⚠️ Помилка оплати. Спробуйте пізніше.";
                            }
                        } else {
                            msg += "\nОплата при отриманні.";
                        }

                        Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG).show();

                        // Очищаємо кошик
                        Order.itemsMap.clear();
                        loadOrderData();
                    });

                } else {
                    mainThreadHandler.post(() ->
                            Toast.makeText(requireContext(), "Помилка сервера при створенні замовлення", Toast.LENGTH_LONG).show()
                    );
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                mainThreadHandler.post(() ->
                        Toast.makeText(requireContext(), "Мережева помилка: " + ex.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        });
    }

    public void toClearCart() {
        if (!Order.itemsMap.isEmpty()) {
            Order.itemsMap.clear();
            Toast.makeText(requireContext(), "Кошик очищено!", Toast.LENGTH_LONG).show();
            loadOrderData();
        } else {
            Toast.makeText(requireContext(), "Кошик пустий!", Toast.LENGTH_LONG).show();
        }
    }
}