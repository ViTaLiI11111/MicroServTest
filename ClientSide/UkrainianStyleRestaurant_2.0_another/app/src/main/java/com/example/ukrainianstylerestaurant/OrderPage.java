package com.example.ukrainianstylerestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ukrainianstylerestaurant.R;
import com.example.ukrainianstylerestaurant.model.Course;
import com.example.ukrainianstylerestaurant.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_page);

        ListView orders_list = findViewById(R.id.orders_list);
        ListView sum_area = findViewById(R.id.sum_area);

        List<String> coursesOrder = new ArrayList<>();
        List<String> coursesSum = new ArrayList<>();
        int sum = 0;


        for (Course c : MainActivity.fullCoursesList) {
            if (Order.items_id.contains(c.getId())) {
                coursesOrder.add(c.getTitle());
                coursesOrder.add(c.getPrice());

                int n = Integer.parseInt(c.getPrice());
                sum += n;
            }
        }


        String string_sum = Integer.toString(sum);
        coursesSum.add(string_sum);

        orders_list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, coursesOrder));
        sum_area.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, coursesSum));
    }
    public void aboutUs(View view){
        Intent intent = new Intent(this, AboutUs.class);
        startActivity(intent);
    }

    public void goToContacts(View view){
        Intent intent = new Intent(this, Contacts.class);
        startActivity(intent);
    }

    public void mainPage(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void toBuy(View view){
        if(Order.items_id.isEmpty()){
            Toast.makeText(this,"Замовте страву, будь ласка.", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this,"Замовлення виконано!", Toast.LENGTH_LONG).show();
        }
    }

    public void toClearCart(View view){
        if(!Order.items_id.isEmpty()) {
            Order.items_id.clear();
            Toast.makeText(this, "Кошик очищено!", Toast.LENGTH_LONG).show();
            recreate();
        }
        else{
            Toast.makeText(this, "Кошик пустий!", Toast.LENGTH_LONG).show();
        }
    }

}
