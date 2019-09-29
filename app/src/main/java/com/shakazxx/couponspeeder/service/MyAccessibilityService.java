package com.shakazxx.couponspeeder.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

import static android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;

public class MyAccessibilityService extends AccessibilityService {

    private static final String TAG = MyAccessibilityService.class.getSimpleName();

    private boolean isEntered = false;
    private int currentIndex = 0;
    private int totalCount = 0;
    private int maxTotalCount = 6;
    private int articleScrollDownTimes = 120;  //下滑次数
    private boolean isFinish = false;
    private List<String> readTitles = new ArrayList<>();

    private boolean enableArticle = false;
    private boolean enableVideo = true;
    private boolean isVideoEntered = false;
    private int currentVideoIndex = 0;
    private int totalVideoCount = 0;
    private int maxTotalVideoCount = 6;
    private int videoHoldTime = 180;  // 观看时间，秒
    private boolean isVideoFinish = false;
    private List<String> readVideos = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
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

        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }

        switch (eventType) {
            case TYPE_WINDOW_CONTENT_CHANGED:
                processArticle(nodeInfo);
                processVideo(nodeInfo);
                break;
            default:
                break;
        }
    }

    private void processArticle(AccessibilityNodeInfo rootNode) {
        if (!enableArticle) {
            return;
        }

        List<AccessibilityNodeInfo> info = rootNode.findAccessibilityNodeInfosByText("要闻");
        if (info.size() > 0 && !isEntered) {
            AccessibilityNodeInfo info1 = info.get(0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            info1.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Log.d(TAG, "进入要闻");
            isEntered = true;

            return;
        }

        List<AccessibilityNodeInfo> entries = rootNode.findAccessibilityNodeInfosByText("“学习强国”学习平台");
        if (entries.size() > 0 && !isFinish) {
            try {
                Log.d(TAG, String.format("文章数量:%s", entries.size()));
                Log.d(TAG, String.format("当前第%s篇", currentIndex + 1));

                int maxIndex = entries.size();

                if (currentIndex > maxIndex - 1 && !isFinish) {
                    if (totalCount < maxTotalCount) {
                        currentIndex = 0;
                        scrollDown(500);
                        return;
                    } else {
                        Log.d(TAG, "今天文章学习结束了");
                        isFinish = true;
                        enableVideo = true;
                        return;
                    }
                }

                AccessibilityNodeInfo btn = entries.get(currentIndex).getParent();
                currentIndex++;
                if (btn != null) {
                    String title = btn.getChild(0).getText().toString();
                    if (!readTitles.contains(title)) {
                        Log.d(TAG, title);
                        readTitles.add(title);
                        totalCount++;
                        Thread.sleep(1000);
                        if (btn.isClickable()) {
                            btn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            readArticle();
                        } else {
                            Log.e(TAG, "无法点击进入文章");
                        }
                    } else {
                        scrollDown(500);
                        return;
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, e.getMessage(), e);
            }
        }
    }

    private void readArticle() {
        AccessibilityNodeInfo newRoot = getRootInActiveWindow();
        List<AccessibilityNodeInfo> inners = newRoot.findAccessibilityNodeInfosByText("欢迎发表你的观点");
        if (inners.size() > 0 && !isFinish) {
            Log.d(TAG, "进入文章");
            int stepLength = 1000;

            for (int i = 0; i < articleScrollDownTimes; i++) {
                scrollDown(stepLength);
                Log.d(TAG, ">>>>> down " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }

            this.performGlobalAction(GLOBAL_ACTION_BACK);
        }
    }

    private void processVideo(AccessibilityNodeInfo rootNode) {
        if (!enableVideo) {
            return;
        }

        List<AccessibilityNodeInfo> info = rootNode.findAccessibilityNodeInfosByText("视听学习");
        if (info.size() > 0 && !isVideoEntered) {
            for (AccessibilityNodeInfo node : info) {
                if (node.getClassName().equals("android.widget.FrameLayout")) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    Log.d(TAG, "进入视频");
                    isVideoEntered = true;

                    return;
                }
            }
        }

        List<AccessibilityNodeInfo> entries = rootNode.findAccessibilityNodeInfosByText("央视网");
        if (entries.size() > 0 && !isVideoFinish) {
            try {
                Log.d(TAG, String.format("文章数量:%s", entries.size()));
                Log.d(TAG, String.format("当前第%s篇", currentVideoIndex + 1));

                int maxIndex = entries.size();

                if (currentVideoIndex > maxIndex - 1 && !isVideoFinish) {
                    if (totalVideoCount < maxTotalVideoCount) {
                        currentVideoIndex = 0;
                        scrollDown(1000);
                        return;
                    } else {
                        Log.d(TAG, "今天视频学习结束了");
                        isVideoFinish = true;
                        return;
                    }
                }

                AccessibilityNodeInfo btn = entries.get(currentVideoIndex).getParent();
                currentVideoIndex++;
                if (btn != null) {
                    String title = btn.getChild(0).getText().toString();
                    if (!readVideos.contains(title)) {
                        Log.d(TAG, title);
                        readVideos.add(title);
                        totalVideoCount++;
                        Thread.sleep(1000);
                        if (btn.isClickable()) {
                            btn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            watchVideo();
                        } else {
                            Log.e(TAG, "无法进入视频文章");
                        }
                    } else {
                        scrollDown(500);
                        return;
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, e.getMessage(), e);
            }

            return;
        } else if (entries.size() == 0 && !isVideoFinish) {
            scrollDown(500);
            return;
        }
    }

    private void watchVideo() {
        AccessibilityNodeInfo newRoot = getRootInActiveWindow();
        List<AccessibilityNodeInfo> inners = newRoot.findAccessibilityNodeInfosByText("欢迎发表你的观点");
        if (inners.size() > 0 && !isVideoFinish) {
            try {
                Thread.sleep(videoHoldTime * 1000);
            } catch (InterruptedException e) {
            }

            this.performGlobalAction(GLOBAL_ACTION_BACK);
        }
    }


    @Override
    public void onInterrupt() {

    }

    private void scrollDown(int distance) {
        GestureDescription.Builder builder = new GestureDescription.Builder();

        Path path = new Path();
        int y = 1200;
        int x = 360;

        path.moveTo(x, y);
        path.lineTo(x, y -= distance);

        GestureDescription gestureDescription = builder
                .addStroke(new GestureDescription.StrokeDescription(path, 200L, 800L, false))
                .build();

        dispatchGesture(gestureDescription, null, null);
    }
}