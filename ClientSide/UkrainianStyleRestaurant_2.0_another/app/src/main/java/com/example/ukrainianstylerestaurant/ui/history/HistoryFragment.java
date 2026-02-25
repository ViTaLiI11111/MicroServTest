package com.example.ukrainianstylerestaurant.ui.history;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ukrainianstylerestaurant.LocalStorage;
import com.example.ukrainianstylerestaurant.R;
import com.example.ukrainianstylerestaurant.adapter.HistoryAdapter;
import com.example.ukrainianstylerestaurant.data.OrdersRepository;
import com.example.ukrainianstylerestaurant.model.CreateOrderRequest;
import com.example.ukrainianstylerestaurant.model.OrderItemRequest;
import com.example.ukrainianstylerestaurant.model.OrderItemResponse;
import com.example.ukrainianstylerestaurant.model.OrderResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private ExecutorService executorService;
    private Handler mainHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.history_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        loadHistory();
    }

    private void loadHistory() {
        String clientName = LocalStorage.getClientName(requireContext());
        if (clientName.isEmpty()) clientName = LocalStorage.getUsername(requireContext());

        String finalClientName = clientName;

        executorService.execute(() -> {
            try {
                OrdersRepository repo = new OrdersRepository();
                List<OrderResponse> history = repo.getClientHistory(finalClientName);

                mainHandler.post(() -> {
                    if (history != null && !history.isEmpty()) {
                        HistoryAdapter adapter = new HistoryAdapter(history, this::onReorderClick);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(getContext(), "Історія пуста", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> Toast.makeText(getContext(), "Помилка завантаження: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void onReorderClick(OrderResponse oldOrder) {
        String[] options = {"💳 Оплатити зараз (Картка)", "💵 Оплата при отриманні"};

        new AlertDialog.Builder(requireContext())
                .setTitle("Створити це замовлення знову?")
                .setItems(options, (dialog, which) -> {
                    boolean payImmediately = (which == 0);
                    processReorder(oldOrder, payImmediately);
                })
                .setNegativeButton("Скасувати", null)
                .show();
    }

    private void processReorder(OrderResponse oldOrder, boolean payImmediately) {
        Toast.makeText(getContext(), "Створення...", Toast.LENGTH_SHORT).show();

        executorService.execute(() -> {
            try {
                List<OrderItemRequest> newItems = new ArrayList<>();
                if (oldOrder.items != null) {
                    for (OrderItemResponse oldItem : oldOrder.items) {
                        newItems.add(new OrderItemRequest(oldItem.dishId, oldItem.qty, null));
                    }
                }

                String address = LocalStorage.getClientAddress(requireContext());
                String phone = LocalStorage.getClientPhone(requireContext());
                String name = LocalStorage.getClientName(requireContext());
                if (name.isEmpty()) name = LocalStorage.getUsername(requireContext());

                CreateOrderRequest req;

                if ("DineIn".equalsIgnoreCase(oldOrder.type)) {
                    int tableNo = LocalStorage.getTableNumber(requireContext());
                    req = new CreateOrderRequest(tableNo, newItems, name);
                } else {
                    req = new CreateOrderRequest(newItems, address, phone, name);
                }

                OrdersRepository repo = new OrdersRepository();
                OrderResponse response = repo.createOrder(req);

                if (response != null && response.id != null) {
                    LocalStorage.saveActiveOrderId(requireContext(), response.id);

                    boolean paid = false;
                    if (payImmediately) {
                        paid = repo.payOrder(response.id);
                    }

                    boolean finalPaid = paid;

                    mainHandler.post(() -> {
                        String msg = "Замовлення створено!" + (finalPaid ? " (Оплачено)" : "");
                        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

                        loadHistory();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> Toast.makeText(getContext(), "Помилка: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
}