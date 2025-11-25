package com.example.ukrainianstylerestaurant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ukrainianstylerestaurant.R;
import com.example.ukrainianstylerestaurant.model.Category;
import com.example.ukrainianstylerestaurant.ui.home.HomeFragment;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    Context context;
    List<Category> categories;
    HomeFragment homeFragment;

    public CategoryAdapter(Context context, List<Category> categories, HomeFragment homeFragment) {
        this.context = context;
        this.categories = categories;
        this.homeFragment = homeFragment;
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

        holder.itemView.setOnClickListener(view -> homeFragment
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
