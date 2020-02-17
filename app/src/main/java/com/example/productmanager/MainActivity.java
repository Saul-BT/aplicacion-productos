package com.example.productmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.productmanager.model.User;

public class MainActivity extends AppCompatActivity {

    public static User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Window window = getWindow();
        //window.setStatusBarColor(Color.parseColor("#FAFAFA"));
    }
}
