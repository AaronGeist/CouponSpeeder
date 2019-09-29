package com.shakazxx.couponspeeder.service;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import com.shakazxx.couponspeeder.core.party.ArticleReader;
import com.shakazxx.couponspeeder.core.party.VideoReader;

import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;

public class MyAccessibilityService extends AccessibilityService {

    private static final String TAG = MyAccessibilityService.class.getSimpleName();

    private ArticleReader articleReader;
    private VideoReader videoReader;

    /**************************
     * 启动应用   adb shell am start -n cn.xuexi.android/com.alibaba.android.rimet.biz.SplashActivity
     * 应用关闭   adb shell am force-stop cn.xuexi.android
     **************************/


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        articleReader = new ArticleReader(this);
        videoReader = new VideoReader(this);
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
                processArticle();
                processVideo();
                break;
            default:
                break;
        }
    }

    private void processArticle() {
        if (articleReader.findEntrance("学习")) {
            if (articleReader.findEntrance("要闻")) {
                articleReader.loop("“学习强国”学习平台", "欢迎发表你的观点");
            }
        }
    }

    private void processVideo() {
        if (videoReader.findEntrance("视听学习")) {
            videoReader.loop("央视网", "欢迎发表你的观点");
        }
    }


    @Override
    public void onInterrupt() {

    }
}