package com.shakazxx.couponspeeder.service;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.shakazxx.couponspeeder.core.party.ArticleReader;
import com.shakazxx.couponspeeder.core.party.Login;
import com.shakazxx.couponspeeder.core.party.VideoReader;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;

public class MyAccessibilityService extends AccessibilityService {

    private static final String TAG = MyAccessibilityService.class.getSimpleName();

    private ArticleReader articleReader;
    private VideoReader videoReader;
    private Login login;

    /**************************
     * 启动应用   adb shell am start -n cn.xuexi.android/com.alibaba.android.rimet.biz.SplashActivity
     * 应用关闭   adb shell am force-stop cn.xuexi.android
     **************************/


    @Override

    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        login = new Login(this);
        articleReader = new ArticleReader(this);
        videoReader = new VideoReader(this);

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
                login.process();
                processArticle();
                processVideo();
                break;
            default:
                break;
        }
    }

    private void processArticle() {
        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (articleReader.findEntrance("学习")) {
            if (articleReader.findEntrance("推荐")) {
                articleReader.loop(date, "欢迎发表你的观点");
            }
        }
    }

    private void processVideo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        if (videoReader.findEntrance("视听学习")) {
            if (articleReader.findEntrance("联播频道")) {
                videoReader.loop(date, "欢迎发表你的观点");
            }
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