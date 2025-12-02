package com.example.ukrainianstylerestaurant.ui.delivery;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ukrainianstylerestaurant.LocalStorage;
import com.example.ukrainianstylerestaurant.R;
import com.example.ukrainianstylerestaurant.data.OrdersRepository;
import com.example.ukrainianstylerestaurant.model.DeliveryStatusResponse;
import com.example.ukrainianstylerestaurant.model.OrderResponse;
import com.example.ukrainianstylerestaurant.net.DeliveryApi;
import com.example.ukrainianstylerestaurant.net.DeliveryRetrofitClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class DeliveryStatusFragment extends Fragment {

    private TextView tvBody, tvStatusHeader, tvPaymentStatus;
    private LinearLayout layoutCourierInfo, layoutPaymentInfo;
    private Button btnRefresh, btnPay;

    private ExecutorService executorService;
    private Handler mainHandler;

    private boolean isTracking = false;
    // Оновлюємо кожні 3 секунди, щоб швидше бачити зміни
    private final int UPDATE_INTERVAL = 3000;

    private final Runnable statusChecker = new Runnable() {
        @Override
        public void run() {
            if (isTracking) {
                loadAllData(false);
                mainHandler.postDelayed(this, UPDATE_INTERVAL);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery_status, container, false);

        tvStatusHeader = view.findViewById(R.id.tv_status_header);
        tvBody = view.findViewById(R.id.tv_status_body);
        layoutCourierInfo = view.findViewById(R.id.layout_courier_info);

        layoutPaymentInfo = view.findViewById(R.id.layout_payment_info);
        tvPaymentStatus = view.findViewById(R.id.tv_payment_status);
        btnPay = view.findViewById(R.id.btn_pay);
        btnRefresh = view.findViewById(R.id.btn_refresh_status);

        executorService = Executors.newFixedThreadPool(2);
        mainHandler = new Handler(Looper.getMainLooper());

        btnRefresh.setOnClickListener(v -> loadAllData(true));
        btnPay.setOnClickListener(v -> performPayment());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        isTracking = true;
        loadAllData(true);
        statusChecker.run();
    }

    @Override
    public void onPause() {
        super.onPause();
        isTracking = false;
        mainHandler.removeCallbacks(statusChecker);
    }

    private void loadAllData(boolean showLoading) {
        String activeOrderId = LocalStorage.getActiveOrderId(requireContext());
        if (activeOrderId == null) {
            showEmptyState();
            return;
        }

        if (showLoading) {
            btnRefresh.setText("Оновлення...");
            btnRefresh.setEnabled(false);
        }

        executorService.execute(() -> {
            try {
                OrdersRepository repo = new OrdersRepository();
                // 1. Отримуємо актуальні дані про замовлення (в т.ч. isPaid)
                OrderResponse order = repo.getOrder(activeOrderId);

                mainHandler.post(() -> {
                    btnRefresh.setEnabled(true);
                    btnRefresh.setText("Оновити статус");

                    if (order != null) {
                        // Оновлюємо UI оплати
                        updatePaymentUI(order);

                        if ("Delivery".equalsIgnoreCase(order.type)) {
                            loadDeliveryDetails(activeOrderId);
                        } else {
                            showDineInStatus(order);
                        }
                    } else {
                        tvStatusHeader.setText("Помилка");
                        tvBody.setText("Не вдалося отримати дані");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> {
                    if(showLoading) {
                        btnRefresh.setEnabled(true);
                        btnRefresh.setText("Оновити статус");
                    }
                });
            }
        });
    }

    private void loadDeliveryDetails(String orderId) {
        executorService.execute(() -> {
            try {
                DeliveryApi api = DeliveryRetrofitClient.get().create(DeliveryApi.class);
                Response<DeliveryStatusResponse> response = api.getDeliveryStatus(orderId).execute();

                mainHandler.post(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        updateDeliveryUI(response.body());
                    } else {
                        tvStatusHeader.setText("Обробка");
                        tvBody.setText("Менеджер підтверджує доставку...");
                        layoutCourierInfo.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void updatePaymentUI(OrderResponse order) {
        layoutPaymentInfo.setVisibility(View.VISIBLE);

        if (order.isPaid) {
            tvPaymentStatus.setText("✅ ОПЛАЧЕНО");
            tvPaymentStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            btnPay.setVisibility(View.GONE); // Ховаємо кнопку, якщо вже оплачено
        } else {
            tvPaymentStatus.setText("💵 НЕ ОПЛАЧЕНО");
            tvPaymentStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            btnPay.setVisibility(View.VISIBLE); // Показуємо кнопку
            btnPay.setText("Сплатити " + order.total + " грн");
        }
    }

    private void performPayment() {
        String activeOrderId = LocalStorage.getActiveOrderId(requireContext());
        if (activeOrderId == null) return;

        btnPay.setEnabled(false);
        btnPay.setText("Обробка...");

        executorService.execute(() -> {
            try {
                OrdersRepository repo = new OrdersRepository();
                boolean success = repo.payOrder(activeOrderId);

                mainHandler.post(() -> {
                    if (success) {
                        Toast.makeText(requireContext(), "Оплата успішна!", Toast.LENGTH_LONG).show();
                        loadAllData(false); // Одразу оновлюємо екран
                    } else {
                        btnPay.setEnabled(true);
                        btnPay.setText("Спробувати ще раз");
                        Toast.makeText(requireContext(), "Помилка оплати", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> {
                    btnPay.setEnabled(true);
                    btnPay.setText("Спробувати ще раз");
                    Toast.makeText(requireContext(), "Помилка мережі", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showDineInStatus(OrderResponse order) {
        layoutCourierInfo.setVisibility(View.GONE);
        tvStatusHeader.setText("Статус: " + order.status);
        tvBody.setText("Смачного!");
    }

    private void updateDeliveryUI(DeliveryStatusResponse data) {
        switch (data.status) {
            case 0:
                tvStatusHeader.setText("Шукаємо кур'єра");
                tvBody.setText("Замовлення готується.");
                break;
            case 1:
                tvStatusHeader.setText("Кур'єр прямує до ресторану");
                tvBody.setText("Скоро забере ваше замовлення.");
                break;
            case 2:
                tvStatusHeader.setText("Кур'єр в дорозі");
                tvBody.setText("Очікуйте дзвінка!");
                break;
            case 3:
                tvStatusHeader.setText("Доставлено!");
                tvBody.setText("Дякуємо за замовлення.");
                isTracking = false;
                LocalStorage.clearActiveOrder(requireContext());
                break;
        }
    }

    private void showEmptyState() {
        tvStatusHeader.setText("Пусто");
        tvBody.setText("Зробіть замовлення");
        layoutCourierInfo.setVisibility(View.GONE);
        layoutPaymentInfo.setVisibility(View.GONE);
    }
}