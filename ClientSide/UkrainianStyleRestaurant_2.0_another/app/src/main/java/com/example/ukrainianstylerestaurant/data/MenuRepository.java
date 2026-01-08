package com.example.ukrainianstylerestaurant.data;

import com.example.ukrainianstylerestaurant.model.Category;
import com.example.ukrainianstylerestaurant.model.Course;
import com.example.ukrainianstylerestaurant.net.MenuApi;
import com.example.ukrainianstylerestaurant.net.RetrofitClient;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MenuRepository {
    private final MenuApi api;

    public MenuRepository(){
        this.api = RetrofitClient.get().create(MenuApi.class);
    }

    public MenuRepository(MenuApi api){
        this.api = api;
    }

    public List<Category> getCategories() throws IOException {
        Call<List<Category>> call = api.getCategories();
        Response<List<Category>> r = call.execute();
        if (!r.isSuccessful()) throw new IOException("HTTP " + r.code());
        return r.body();
    }

    public List<Course> getDishes() throws IOException {
        Call<List<Course>> call = api.getDishes();
        Response<List<Course>> r = call.execute();
        if (!r.isSuccessful()) throw new IOException("HTTP " + r.code());
        return r.body();
    }
}
