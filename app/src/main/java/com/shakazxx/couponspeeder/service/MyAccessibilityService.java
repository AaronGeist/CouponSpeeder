package com.shakazxx.couponspeeder.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.shakazxx.couponspeeder.core.Score.CmbScoreFetcher;
import com.shakazxx.couponspeeder.core.Score.SpdccScoreFetcher;
import com.shakazxx.couponspeeder.core.alipay.AlipayScore;
import com.shakazxx.couponspeeder.core.party.PartyStudent;
import com.shakazxx.couponspeeder.core.wechat.WechatScore;

import java.util.List;

import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED;

public class MyAccessibilityService extends AccessibilityService {

    private static final String TAG = MyAccessibilityService.class.getSimpleName();

    private Bundle bundle;

    private PartyStudent partyStudent;
    private CmbScoreFetcher cmbScoreFetcher;
    private SpdccScoreFetcher spdccScoreFetcher;
    private WechatScore wechatScore;
    private AlipayScore alipayScore;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");



        lightScreen();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null == intent) {
            return super.onStartCommand(intent, flags, startId);
        }

        this.bundle = intent.getExtras();

        partyStudent = new PartyStudent(this, bundle);
        cmbScoreFetcher = new CmbScoreFetcher(this);
        spdccScoreFetcher = new SpdccScoreFetcher(this);
        wechatScore = new WechatScore(this);
        alipayScore = new AlipayScore(this);

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
            case TYPE_WINDOW_STATE_CHANGED:
                if (packageName.equalsIgnoreCase("cn.xuexi.android")) {
                    partyStudent.learn();
                    return;
                }

                if (packageName.equalsIgnoreCase("cmb.pb")) {
                    cmbScoreFetcher.fetch();
                    return;
                }

                if (packageName.equalsIgnoreCase("com.spdbccc.app")) {
                    spdccScoreFetcher.fetch();
                    return;
                }

                if (packageName.equalsIgnoreCase("com.tencent.mm")) {
                    wechatScore.pbcc();
                    wechatScore.cmbScore();
                    return;
                }

                if (packageName.equalsIgnoreCase("com.eg.android.AlipayGphone")) {
                    alipayScore.cmbScore();
                    return;
                }

                partyStudent.stop();

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

    private boolean isFront(String packageName) {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(packageName)) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("后台", appProcess.processName);
                    return false;
                } else {
                    Log.i("前台", appProcess.processName);
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void onInterrupt() {

    }
}