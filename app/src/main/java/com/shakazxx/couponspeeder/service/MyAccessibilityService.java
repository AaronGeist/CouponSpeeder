package com.shakazxx.couponspeeder.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.shakazxx.couponspeeder.core.Score.CmbScoreFetcher;
import com.shakazxx.couponspeeder.core.Score.SpdccScoreFetcher;
import com.shakazxx.couponspeeder.core.party.PartyLearner;

import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;

public class MyAccessibilityService extends AccessibilityService {

    private static final String TAG = MyAccessibilityService.class.getSimpleName();

    private PartyLearner partyLearner;
    private CmbScoreFetcher cmbScoreFetcher;
    private SpdccScoreFetcher spdccScoreFetcher;

    /**************************
     * 启动应用   adb shell am start -n cn.xuexi.android/com.alibaba.android.rimet.biz.SplashActivity
     * 应用关闭   adb shell am force-stop cn.xuexi.android
     **************************/


    @Override

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        partyLearner = new PartyLearner(this);
        cmbScoreFetcher = new CmbScoreFetcher(this);
        spdccScoreFetcher = new SpdccScoreFetcher(this);

        lightScreen();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent) {
            return super.onStartCommand(intent, flags, startId);
        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    protected void onServiceConnected() {
        Log.d(TAG, "onServiceConnected");
        super.onServiceConnected();

        AccessibilityServiceInfo serviceInfo = this.getServiceInfo();
        serviceInfo.flags = serviceInfo.flags
                | AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY
                | AccessibilityServiceInfo.FLAG_REQUEST_ACCESSIBILITY_BUTTON
                | AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE;
        setServiceInfo(serviceInfo);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "onAccessibilityEvent");

        int eventType = event.getEventType();
        String packageName = event.getPackageName().toString();
        String className = event.getClassName().toString();
        Log.d(TAG, String.format("pkg=%s, cls=%s, event=%s", packageName, className, eventType));

        switch (eventType) {
            case TYPE_WINDOW_CONTENT_CHANGED:
                if (packageName.equalsIgnoreCase("cn.xuexi.android")) {
                    partyLearner.learn();
                }

                if (packageName.equalsIgnoreCase("cmb.pb")) {
                    cmbScoreFetcher.fetch();
                }

                if (packageName.equalsIgnoreCase("com.spdbccc.app")) {
                    spdccScoreFetcher.fetch();
                }

                break;
            default:
                break;
        }
    }


    // 点亮亮屏
    private void lightScreen() {
        PowerManager mPowerManager = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock mWakeLock = mPowerManager.newWakeLock
                (PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, TAG);
        mWakeLock.acquire(10 * 1000);
    }

    @Override
    public void onInterrupt() {

    }
}