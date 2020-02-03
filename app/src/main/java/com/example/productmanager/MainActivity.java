package com.example.productmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;

import com.example.productmanager.firebase.FireManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Window window = getWindow();
        //window.setStatusBarColor(Color.parseColor("#FAFAFA"));
    }
}
