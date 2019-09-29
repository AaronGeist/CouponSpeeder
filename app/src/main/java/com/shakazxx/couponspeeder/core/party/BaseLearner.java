package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.GestureUtil;

import java.util.ArrayList;
import java.util.List;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK;

public abstract class BaseLearner {

    protected final String TAG = getClass().getSimpleName();

    // local variables
    private boolean enable = true;

    // service
    protected AccessibilityService accessibilityService;

    // constructor
    public BaseLearner(AccessibilityService service) {
        accessibilityService = service;
    }

    public boolean findEntrance(String keyword) {
        if (!enable) {
            return false;
        }

        boolean result = false;
        AccessibilityNodeInfo root = accessibilityService.getRootInActiveWindow();
        if (root == null) {
            return false;
        }

        List<AccessibilityNodeInfo> entrances = root.findAccessibilityNodeInfosByText(keyword);
        for (AccessibilityNodeInfo entrance : entrances) {
            if ((entrance.getClassName().toString().contains("TextView") && entrance.getText().toString().equalsIgnoreCase(keyword)) ||
                    (entrance.getClassName().toString().contains("FrameLayout") && entrance.getContentDescription() != null
                            && entrance.getContentDescription().toString().equalsIgnoreCase(keyword))) {

                // 往上找，找到可以点击的
                while (entrance != null && !entrance.isClickable()) {
                    entrance = entrance.getParent();
                }

                if (entrance != null) {
                    entrance.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    sleep(1000);
                    Log.d(TAG, "发现入口");
                    result = true;
                    break;
                }
            }

        }

        return result;
    }

    // return true if has finished loop entries
    public void loop(String keyword, String innerKeyword) {
        if (!enable) {
            return;
        }

        int totalCnt = 0;
        List<String> readTitles = new ArrayList<>();
        while (totalCnt < getRequiredEntryCnt()) {
            AccessibilityNodeInfo root = accessibilityService.getRootInActiveWindow();
            List<AccessibilityNodeInfo> entries = root.findAccessibilityNodeInfosByText(keyword);
            if (entries.size() > 0) {
                for (AccessibilityNodeInfo entry : entries) {
                    AccessibilityNodeInfo btn = entry.getParent();
                    String title = btn.getChild(0).getText().toString();
                    if (!readTitles.contains(title)) {
                        // 发现不重复项
                        Log.d(TAG, "当前标题: " + title);
                        readTitles.add(title);
                        sleep(1000);
                        if (btn.isClickable()) {
                            btn.performAction(AccessibilityNodeInfo.ACTION_CLICK);

                            AccessibilityNodeInfo newRoot = accessibilityService.getRootInActiveWindow();
                            List<AccessibilityNodeInfo> inners = newRoot.findAccessibilityNodeInfosByText(innerKeyword);
                            if (inners.size() > 0) {
                                Log.d(TAG, "进入单项");
                                if (processEntry(title)) {
                                    totalCnt++;
                                }
                                accessibilityService.performGlobalAction(GLOBAL_ACTION_BACK);
                                sleep(1000);

                                if (totalCnt >= getRequiredEntryCnt()) {
                                    break;
                                }
                            }
                        } else {
                            Log.e(TAG, "无法点击进入: " + title);
                        }
                    }
                }
            }

            if (totalCnt < getRequiredEntryCnt()) {
                GestureUtil.scrollDown(accessibilityService, 500);
                sleep(3000);
            }
        }

        //学习结束
        enable = false;
    }

    protected void sleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 进入单项后如何处理
    abstract boolean processEntry(String title);

    // 需要学习的数量
    abstract int getRequiredEntryCnt();


}
