package com.shakazxx.couponspeeder.core.base;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Random;

public class BaseAction {

    protected AccessibilityService accessibilityService;
    protected Random r = new Random();
    protected DisplayMetrics dm;

    public BaseAction(AccessibilityService accessibilityService) {
        this.accessibilityService = accessibilityService;
        WindowManager wm = (WindowManager) accessibilityService.getSystemService(Context.WINDOW_SERVICE);
        dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
    }

    protected void showToast(String msg) {
        Toast.makeText(accessibilityService.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    protected int getWidth() {
        return dm.widthPixels;
    }

    protected int getHeight() {
        return dm.heightPixels;
    }
}
