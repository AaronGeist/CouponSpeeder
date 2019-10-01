package com.shakazxx.couponspeeder;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

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
    }

    private void initBtn() {
        btnSettings = findViewById(R.id.setting_btn);
        btnStart = findViewById(R.id.start_btn);

        btnSettings.setOnClickListener(this);
        btnStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: ");
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
