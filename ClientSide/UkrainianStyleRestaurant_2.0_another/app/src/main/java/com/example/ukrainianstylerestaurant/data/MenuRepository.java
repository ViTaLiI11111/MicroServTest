package com.example.ukrainianstylerestaurant.data;

import com.example.ukrainianstylerestaurant.model.Category;
import com.example.ukrainianstylerestaurant.model.Course;
import com.example.ukrainianstylerestaurant.net.MenuApi;
import com.example.ukrainianstylerestaurant.net.RetrofitClient;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;

public class MenuRepository {
    private final MenuApi api = RetrofitClient.get().create(MenuApi.class);

    public List<Category> getCategories() throws IOException {
        Response<List<Category>> r = api.getCategories().execute();
        if (!r.isSuccessful()) throw new IOException("HTTP " + r.code());
        return r.body();
    }

    public List<Course> getDishes() throws IOException {
        Response<List<Course>> r = api.getDishes().execute();
        if (!r.isSuccessful()) throw new IOException("HTTP " + r.code());
        return r.body();
    }
}
