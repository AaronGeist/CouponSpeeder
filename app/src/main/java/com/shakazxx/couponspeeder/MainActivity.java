package com.shakazxx.couponspeeder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.shakazxx.couponspeeder.service.MyAccessibilityService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent mIntent = new Intent(this, MyAccessibilityService.class);
        startService(mIntent);
    }
}
