package com.example.ukrainianstylerestaurant.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ukrainianstylerestaurant.R;
import com.example.ukrainianstylerestaurant.model.OrderItemResponse;
import com.example.ukrainianstylerestaurant.model.OrderResponse;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<OrderResponse> orders;
    private final OnReorderListener listener;

    public interface OnReorderListener {
        void onReorder(OrderResponse order);
    }

    public HistoryAdapter(List<OrderResponse> orders, OnReorderListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_order, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderResponse order = orders.get(position);

        // Дата
        String dateStr = order.createdAt;
        try {
            if (dateStr != null && dateStr.length() >= 10) {
                dateStr = dateStr.substring(0, 10) + " " + dateStr.substring(11, 16);
            }
        } catch (Exception e) { /* ignore */ }

        holder.date.setText(dateStr);
        holder.type.setText(order.type);
        holder.total.setText(order.total + " грн");

        // Список страв
        StringBuilder sb = new StringBuilder();
        if (order.items != null) {
            for (OrderItemResponse item : order.items) {
                sb.append(item.dishTitle).append(" x").append(item.qty).append(", ");
            }
        }
        String summary = sb.toString();
        if (summary.length() > 2) summary = summary.substring(0, summary.length() - 2);
        holder.summary.setText(summary);

        // Клік на повтор
        holder.reorderBtn.setOnClickListener(v -> listener.onReorder(order));
    }

    @Override
    public int getItemCount() { return orders != null ? orders.size() : 0; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date, type, summary, total;
        Button reorderBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.tv_history_date);
            type = itemView.findViewById(R.id.tv_history_type);
            summary = itemView.findViewById(R.id.tv_history_summary);
            total = itemView.findViewById(R.id.tv_history_total);
            reorderBtn = itemView.findViewById(R.id.btn_reorder);
        }
    }
}