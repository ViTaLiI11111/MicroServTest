package com.example.ukrainianstylerestaurant;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;


public class AboutUs extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
    }

    public void goToContacts(View view){
        Intent intent = new Intent(this, Contacts.class);
        startActivity(intent);
    }
    public void mainPage(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}