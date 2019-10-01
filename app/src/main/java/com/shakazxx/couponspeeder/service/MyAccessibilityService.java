package com.shakazxx.couponspeeder.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.party.ArticleReader;
import com.shakazxx.couponspeeder.core.party.Login;
import com.shakazxx.couponspeeder.core.party.Quiz;
import com.shakazxx.couponspeeder.core.party.VideoReader;
import com.shakazxx.couponspeeder.core.util.GestureUtil;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;

public class MyAccessibilityService extends AccessibilityService {

    private static final String TAG = MyAccessibilityService.class.getSimpleName();

    private ArticleReader articleReader;
    private VideoReader videoReader;
    private Login login;
    private Quiz quiz;

    private boolean first = true;

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
        quiz = new Quiz(this);

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
                processLogin();
                processArticle();
                processVideo();
//                processQuiz();
                break;
            default:
                break;
        }
    }

    private void processLogin() {
        login.process();
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

    private void processQuiz() {
        if (quiz.findEntry("我的")) {
            if (quiz.findEntry("我要答题")) {
                if (quiz.findEntry("挑战答题")) {
                    Log.d(TAG, "");
                }
            }
        }

        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root != null) {
            List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByViewId("cn.xuexi.android:id/webview_frame");
            if (nodes.size() > 0 && first) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                first = false;
                GestureUtil.miuiScreenShot(this);
                FileInputStream inputStream;
                String path = Environment.getExternalStorageDirectory().toString() + "/DCIM/Screenshots";
                String SCREENSHOTS_IMAGE_BUCKET_ID = getBucketId(path);

                String[] projection = {MediaStore.Images.Media.DATA,
                        MediaStore.Images.Media.DATE_MODIFIED};
                //
                String selection = MediaStore.Images.Media.BUCKET_ID + " = ?";
                //
                String[] selectionArgsForScreenshots = {SCREENSHOTS_IMAGE_BUCKET_ID};

                //检查Screenshots文件夹
                Pair<Long, String> screenshotsPair = null;
                //查询并排序
                Cursor cursor = this.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        selection,
                        selectionArgsForScreenshots,
                        MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC");

                if (cursor.moveToFirst()) {
                    screenshotsPair = new Pair(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)),
                            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
                }
                if (!cursor.isClosed()) {
                    cursor.close();
                }

                try {
                    inputStream = openFileInput(screenshotsPair.second);
                    byte temp[] = new byte[1024];
                    StringBuilder sb = new StringBuilder("");
                    int len = 0;
                    while ((len = inputStream.read(temp)) > 0) {
                        sb.append(new String(temp, 0, len));
                    }
                    Log.d("msg", "readSaveFile: \n" + sb.toString());
                    inputStream.close();
                } catch (Exception e) {

                }
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

    private static String getBucketId(String path) {
        return String.valueOf(path.toLowerCase().hashCode());
    }

    @Override
    public void onInterrupt() {

    }
}