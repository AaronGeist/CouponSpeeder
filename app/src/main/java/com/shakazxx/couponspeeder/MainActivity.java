package com.shakazxx.couponspeeder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.shakazxx.couponspeeder.service.MyAccessibilityService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TimePicker.OnTimeChangedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private boolean isSet = false;
    private Button btnStart;
    private Button btnSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBtn();
        startService();

        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_CONTACTS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }

    }

    private void initBtn() {
        btnSettings = findViewById(R.id.setting_btn);
        btnStart = findViewById(R.id.start_btn);

        btnSettings.setOnClickListener(this);
        btnStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_btn:
                Intent mIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                this.startActivity(mIntent);
                break;
            case R.id.start_btn:
                Intent intent = new Intent();
                intent.setPackage("com.eg.android.AlipayGphone");
                intent.setClassName("com.eg.android.AlipayGphone", "com.eg.android.AlipayGphone.AlipayLogin");
                this.startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void startService() {
        Intent mIntent = new Intent(this, MyAccessibilityService.class);
        startService(mIntent);
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
        if (!isSet) {
            isSet = true;
        }
    }
}
