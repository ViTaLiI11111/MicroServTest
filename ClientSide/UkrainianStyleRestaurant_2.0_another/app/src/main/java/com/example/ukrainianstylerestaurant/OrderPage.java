package com.example.ukrainianstylerestaurant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ukrainianstylerestaurant.model.Course;
import com.example.ukrainianstylerestaurant.model.Order;
import com.example.ukrainianstylerestaurant.data.OrdersRepository;
import com.example.ukrainianstylerestaurant.model.CreateOrderRequest;
import com.example.ukrainianstylerestaurant.model.OrderItemRequest;

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
        float sum = 0;


        for (Course c : MainActivity.fullCoursesList) {
            if (Order.items_id.contains(c.getId())) {
                coursesOrder.add(c.getTitle());
                coursesOrder.add(c.getPrice());

                float n = Float.parseFloat(c.getPrice());
                sum += n;
            }
        }


        String string_sum = Float.toString(sum);
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
            return;
        }


        List<OrderItemRequest> items = new ArrayList<>();
        for (Integer dishId : Order.items_id) {
            items.add(new OrderItemRequest(dishId, 1, null)); // qty=1, notes=null
        }

        CreateOrderRequest req = new CreateOrderRequest(1, items);

        new Thread(() -> {
            try {
                OrdersRepository repo = new OrdersRepository();
                boolean ok = repo.createOrder(req);

                runOnUiThread(() -> {
                    if (ok) {
                        Toast.makeText(this, "Замовлення відправлено офіціанту!", Toast.LENGTH_LONG).show();
                        Order.items_id.clear();
                        recreate();
                    } else {
                        Toast.makeText(this, "Помилка серверу при створенні замовлення", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Мережева помилка: " + ex.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
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
