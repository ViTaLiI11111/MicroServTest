package com.example.ukrainianstylerestaurant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ukrainianstylerestaurant.MainActivity;
import com.example.ukrainianstylerestaurant.R;
import com.example.ukrainianstylerestaurant.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    Context context;
    List<Category> categories;
    MainActivity mainActivity;

    public CategoryAdapter(Context context, List<Category> categories, MainActivity mainActivity) {
        this.context = context;
        this.categories = categories;
        this.mainActivity = mainActivity;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View categoryItems = LayoutInflater.from(context).inflate(R.layout.category_item, parent, false);
        return new CategoryViewHolder(categoryItems);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        holder.categoryTitle.setText(categories.get(holder.getAdapterPosition()).getTitle());

        holder.itemView.setOnClickListener(view -> mainActivity
                .showCoursesByCategory(categories
                        .get(holder.getAdapterPosition())
                        .getId()));
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static final class CategoryViewHolder extends RecyclerView.ViewHolder {

        TextView categoryTitle;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.CategoryTitle);
        }
    }
}
