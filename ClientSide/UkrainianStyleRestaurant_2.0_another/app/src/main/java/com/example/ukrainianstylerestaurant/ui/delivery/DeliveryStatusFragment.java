package com.example.ukrainianstylerestaurant.ui.delivery;

import android.graphics.Typeface;
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
import com.example.ukrainianstylerestaurant.model.OrderItemResponse;
import com.example.ukrainianstylerestaurant.model.OrderResponse;
import com.example.ukrainianstylerestaurant.net.DeliveryApi;
import com.example.ukrainianstylerestaurant.net.DeliveryRetrofitClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class DeliveryStatusFragment extends Fragment {

    private TextView tvBody, tvStatusHeader, tvPaymentStatus;
    private LinearLayout layoutCourierInfo, layoutPaymentInfo, layoutItems; // <--- Додали layoutItems
    private Button btnRefresh, btnPay;

    private ExecutorService executorService;
    private Handler mainHandler;

    private boolean isTracking = false;
    private final int UPDATE_INTERVAL = 6000;
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

        layoutItems = view.findViewById(R.id.layout_order_items); // <--- Знаходимо
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
                OrderResponse order = repo.getOrder(activeOrderId);

                mainHandler.post(() -> {
                    btnRefresh.setEnabled(true);
                    btnRefresh.setText("Оновити статус");

                    if (order != null) {
                        updatePaymentUI(order);

                        // --- НОВЕ: Заповнюємо список страв ---
                        fillOrderItems(order);
                        // ------------------------------------

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
                        Toast.makeText(requireContext(), "Помилка з'єднання", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // --- НОВИЙ МЕТОД ---
    private void fillOrderItems(OrderResponse order) {
        layoutItems.removeAllViews(); // Очищаємо перед оновленням

        // 1. Отримуємо правильні кольори з ресурсів, які залежать від теми
        // Використовуємо ContextCompat для сумісності, або getResources().getColor()
        int primaryColor = androidx.core.content.ContextCompat.getColor(getContext(), R.color.primary_text);
        int secondaryColor = androidx.core.content.ContextCompat.getColor(getContext(), R.color.secondary_text);

        if (order.items != null) {
            for (OrderItemResponse item : order.items) {
                TextView tv = new TextView(getContext());
                // Формат: "Борщ x2 — 200.0 грн"
                double sum = item.price * item.qty;
                String text = item.dishTitle + " x" + item.qty + " — " + sum + " грн";
                tv.setText(text);
                tv.setTextSize(16);

                // ЗМІНА ТУТ: Замість Color.BLACK ставимо сірий (або основний) колір з теми
                tv.setTextColor(secondaryColor);

                tv.setPadding(0, 4, 0, 4);
                // Додаємо шрифт Montserrat (опціонально, якщо хочеш красу)
                // tv.setTypeface(ResourcesCompat.getFont(getContext(), R.font.montserrat_light));
                layoutItems.addView(tv);
            }

            // Разом
            TextView totalTv = new TextView(getContext());
            totalTv.setText("Всього до сплати: " + order.total + " грн");
            totalTv.setTextSize(18);

            // ЗМІНА ТУТ: Основний колір (чорний вдень, білий вночі)
            totalTv.setTextColor(primaryColor);

            totalTv.setTypeface(null, Typeface.BOLD);
            totalTv.setPadding(0, 16, 0, 0);
            layoutItems.addView(totalTv);
        }
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

    private void showDineInStatus(OrderResponse order) {
        layoutCourierInfo.setVisibility(View.GONE);
        tvStatusHeader.setText("Статус: " + order.status);
        tvBody.setText("Смачного!");
    }

    private void updateDeliveryUI(DeliveryStatusResponse data) {
        switch (data.status) {
            case 0:
                tvStatusHeader.setText("Шукаємо кур'єра");
                tvBody.setText("Замовлення прийнято.");
                layoutCourierInfo.setVisibility(View.GONE);
                break;
            case 1:
                tvStatusHeader.setText("Кур'єр знайдений!");
                tvBody.setText("Кур'єр прямує до ресторану.");
                layoutCourierInfo.setVisibility(View.VISIBLE);
                break;
            case 2:
                tvStatusHeader.setText("Кур'єр в дорозі");
                tvBody.setText("Замовлення їде до вас!");
                layoutCourierInfo.setVisibility(View.VISIBLE);
                break;
            case 3:
                tvStatusHeader.setText("Доставлено!");
                tvBody.setText("Смачного! Дякуємо за замовлення.");
                layoutCourierInfo.setVisibility(View.GONE);
                isTracking = false;
                LocalStorage.clearActiveOrder(requireContext());
                break;
        }
    }

    private void updatePaymentUI(OrderResponse order) {
        layoutPaymentInfo.setVisibility(View.VISIBLE);
        if (order.isPaid) {
            tvPaymentStatus.setText("✅ ЗАМОВЛЕННЯ ОПЛАЧЕНО");
            tvPaymentStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            btnPay.setVisibility(View.GONE);
        } else {
            tvPaymentStatus.setText("💵 НЕ ОПЛАЧЕНО");
            tvPaymentStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            btnPay.setVisibility(View.VISIBLE);
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
                        loadAllData(false);
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

    private void showEmptyState() {
        tvStatusHeader.setText("Немає активних замовлень");
        tvBody.setText("Зробіть замовлення в меню");
        layoutItems.removeAllViews(); // Очищуємо список
        layoutCourierInfo.setVisibility(View.GONE);
        layoutPaymentInfo.setVisibility(View.GONE);
        btnRefresh.setEnabled(true);
    }
}