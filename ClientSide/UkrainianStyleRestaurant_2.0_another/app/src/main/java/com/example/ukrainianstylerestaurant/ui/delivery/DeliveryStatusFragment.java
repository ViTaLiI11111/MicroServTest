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
import com.example.ukrainianstylerestaurant.model.DeliveryStatusResponse;
import com.example.ukrainianstylerestaurant.net.DeliveryApi;
import com.example.ukrainianstylerestaurant.net.DeliveryRetrofitClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Response;

public class DeliveryStatusFragment extends Fragment {

    private TextView tvBody, tvCourierName, tvEta, tvStatusHeader;
    private LinearLayout layoutCourierInfo;
    private Button btnRefresh;

    private ExecutorService executorService;
    private Handler mainHandler;

    // --- ДЛЯ АВТО-ОНОВЛЕННЯ ---
    private boolean isTracking = false;
    private final int UPDATE_INTERVAL = 5000; // 5 секунд
    private final Runnable statusChecker = new Runnable() {
        @Override
        public void run() {
            if (isTracking) {
                loadStatus(false); // false = не показувати спіннер/блокування кнопок
                mainHandler.postDelayed(this, UPDATE_INTERVAL);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery_status, container, false);

        // Ініціалізація View
        tvStatusHeader = view.findViewById(R.id.tv_status_header);
        tvBody = view.findViewById(R.id.tv_status_body);
        tvCourierName = view.findViewById(R.id.tv_courier_name);
        tvEta = view.findViewById(R.id.tv_eta);
        layoutCourierInfo = view.findViewById(R.id.layout_courier_info);
        btnRefresh = view.findViewById(R.id.btn_refresh_status);

        // Ініціалізація потоків
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        // Завантаження при старті
        loadStatus(true); // true = показати індикацію завантаження перший раз

        btnRefresh.setOnClickListener(v -> loadStatus(true));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Починаємо авто-оновлення, коли екран активний
        isTracking = true;
        statusChecker.run();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Зупиняємо, коли згорнули
        isTracking = false;
        mainHandler.removeCallbacks(statusChecker);
    }

    private void loadStatus(boolean showLoading) {
        String activeOrderId = LocalStorage.getActiveOrderId(requireContext());

        if (activeOrderId == null) {
            updateUI(null);
            return;
        }

        if (showLoading) {
            btnRefresh.setEnabled(false);
            btnRefresh.setText("Оновлення...");
        }

        executorService.execute(() -> {
            try {
                DeliveryApi api = DeliveryRetrofitClient.get().create(DeliveryApi.class);
                Response<DeliveryStatusResponse> response = api.getDeliveryStatus(activeOrderId).execute();

                mainHandler.post(() -> {
                    if (showLoading) {
                        btnRefresh.setEnabled(true);
                        btnRefresh.setText("Оновити статус");
                    }

                    if (response.isSuccessful() && response.body() != null) {
                        updateUI(response.body());
                    } else {
                        // Якщо 404 - значить замовлення ще не створилось у базі доставки
                        tvBody.setText("Обробка замовлення...");
                        layoutCourierInfo.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mainHandler.post(() -> {
                    if (showLoading) {
                        btnRefresh.setEnabled(true);
                        btnRefresh.setText("Оновити статус");
                        Toast.makeText(requireContext(), "Помилка з'єднання", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void updateUI(DeliveryStatusResponse data) {
        if (data == null) {
            tvBody.setText("Немає активних замовлень");
            layoutCourierInfo.setVisibility(View.GONE);
            return;
        }

        // ВІДЛАДКА: Показуємо тост, який саме статус прийшов
        // Toast.makeText(requireContext(), "Debug: Status Code = " + data.status, Toast.LENGTH_SHORT).show();

        // 0=Created, 1=Assigned, 2=PickedUp, 3=Delivered
        switch (data.status) {
            case 0: // Created
                tvStatusHeader.setText("Шукаємо кур'єра");
                tvBody.setText("Замовлення #" + LocalStorage.getActiveOrderId(requireContext()) + "\nприйнято!");
                layoutCourierInfo.setVisibility(View.GONE);
                tvEta.setText("~45 хв");
                break;

            case 1: // Assigned
                tvStatusHeader.setText("Кур'єр знайдений!");
                tvBody.setText("Кур'єр прямує до ресторану.");
                layoutCourierInfo.setVisibility(View.VISIBLE);

                // Якщо є ID кур'єра, показуємо його (поки просто ID)
                String courierInfo = (data.courierId != null) ? "ID #" + data.courierId : "Призначено";
                tvCourierName.setText(courierInfo);
                tvEta.setText("~35 хв");
                break;

            case 2: // PickedUp
                tvStatusHeader.setText("Кур'єр в дорозі");
                tvBody.setText("Їжа вже їде до вас!");
                layoutCourierInfo.setVisibility(View.VISIBLE);
                tvCourierName.setText("ID #" + data.courierId);
                tvEta.setText("~15 хв");
                break;

            case 3: // Delivered
                tvStatusHeader.setText("Доставлено!");
                tvBody.setText("Смачного! Дякуємо за замовлення.");
                layoutCourierInfo.setVisibility(View.GONE);
                tvEta.setText("0 хв");

                // Зупиняємо трекінг, бо вже все
                isTracking = false;
                LocalStorage.clearActiveOrder(requireContext());
                break;
        }
    }
}