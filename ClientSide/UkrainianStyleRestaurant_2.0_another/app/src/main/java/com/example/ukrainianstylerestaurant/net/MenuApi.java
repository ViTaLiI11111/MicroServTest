package com.example.ukrainianstylerestaurant.net;

import com.example.ukrainianstylerestaurant.model.Category;
import com.example.ukrainianstylerestaurant.model.Course;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

// На бекенді: Category { id, title }, Dish { id, title, price, pepper, color, categoryId, imageBase64 }
public interface MenuApi {
    @GET("api/Categories")
    Call<List<Category>> getCategories();

    @GET("api/Dishes")
    Call<List<Course>> getDishes();

    @GET("api/Dishes/{id}")
    Call<Course> getDish(@Path("id") int id);
}
