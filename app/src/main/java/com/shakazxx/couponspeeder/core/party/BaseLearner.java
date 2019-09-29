package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.GestureUtil;

import java.util.ArrayList;
import java.util.List;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK;

public class BaseLearner {

    private final String TAG = getClass().getSimpleName();

    private boolean isEntered = false;
    private int currentIndex = 0;
    private int totalCount = 0;
    private int maxTotalCount = 6;
    private int articleScrollDownTimes = 120;  //下滑次数
    private boolean isFinish = false;
    private List<String> readTitles = new ArrayList<>();
    private boolean enable = false;

    private long previousTs = 0;

    private AccessibilityService accessibilityService;

    public BaseLearner(AccessibilityService service) {
        accessibilityService = service;
    }

    public boolean findEntrance(AccessibilityNodeInfo root, String keyword) {
        if (!enable) {
            return false;
        }

        List<AccessibilityNodeInfo> info = root.findAccessibilityNodeInfosByText("要闻");
        if (info.size() > 0) {
            AccessibilityNodeInfo info1 = info.get(0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            info1.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Log.d(TAG, "进入文章");

            return true;
        } else {
            return false;
        }
    }

    private void process(AccessibilityNodeInfo rootNode) {
        if (!enable) {
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
            Log.d(TAG, "进入文章");
            isEntered = true;

            return;
        }

        List<AccessibilityNodeInfo> entries = rootNode.findAccessibilityNodeInfosByText("“学习强国”学习平台");
        if (entries.size() > 0 && !isFinish) {
            try {
                Log.d(TAG, String.format("发现总数量:%s", entries.size()));
                Log.d(TAG, String.format("当前第%s", currentIndex + 1));

                int maxIndex = entries.size();

                if (currentIndex > maxIndex - 1 && !isFinish) {
                    if (totalCount < maxTotalCount) {
                        currentIndex = 0;
                        GestureUtil.scrollDown(accessibilityService, 500);
                        return;
                    } else {
                        Log.d(TAG, "今天文章学习结束了");
                        isFinish = true;
//                        enableVideo = true;
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
                        GestureUtil.scrollDown(accessibilityService, 500);
                        return;
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, e.getMessage(), e);
            }
        }
    }

    private void readArticle() {
        AccessibilityNodeInfo newRoot = accessibilityService.getRootInActiveWindow();
        List<AccessibilityNodeInfo> inners = newRoot.findAccessibilityNodeInfosByText("欢迎发表你的观点");
        if (inners.size() > 0 && !isFinish) {
            Log.d(TAG, "进入文章");
            int stepLength = 1000;

            for (int i = 0; i < articleScrollDownTimes; i++) {
                GestureUtil.scrollDown(accessibilityService, stepLength);
                Log.d(TAG, ">>>>> down " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
            }

            accessibilityService.performGlobalAction(GLOBAL_ACTION_BACK);
        }
    }


}
