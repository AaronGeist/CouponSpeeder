package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.base.BaseAction;
import com.shakazxx.couponspeeder.core.util.CommonUtil;
import com.shakazxx.couponspeeder.core.util.GestureUtil;

import java.util.ArrayList;
import java.util.List;

import static com.shakazxx.couponspeeder.core.util.CommonUtil.sleep;

public abstract class BaseLearner extends BaseAction {

    protected final String TAG = getClass().getSimpleName();

    // do not scroll down forever, if no item found, return
    private static final int MAX_SCROLL_DOWN_CNT = 100;

    // 预期每种类型增加的分数
    public static final int EXPECT_SCORE_INCR = 12;

    // local variables
    protected boolean enable = true;

    protected Bundle bundle;

    // application is not running, then pending
    protected boolean pending = false;

    protected HistoryRecord historyRecord = new HistoryRecord();

    // 最初的分数
    private int initScore = -1;

    // constructor
    public BaseLearner(AccessibilityService service, Bundle bundle) {
        super(service);
        this.bundle = bundle;
    }

    public boolean findEntrance(String keyword) {
        if (!enable) {
            return false;
        }

        boolean result = false;
        List<AccessibilityNodeInfo> entrances = CommonUtil.findAllByText(accessibilityService, null, keyword);
        for (AccessibilityNodeInfo entrance : entrances) {
            if ((entrance.getClassName().toString().contains("TextView") && entrance.getText().toString().equalsIgnoreCase(keyword)) ||
                    (entrance.getClassName().toString().contains("FrameLayout") && entrance.getContentDescription() != null
                            && entrance.getContentDescription().toString().equalsIgnoreCase(keyword))) {

                result = CommonUtil.click(entrance, 1000);
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

    // return true if has finished processSingle entries
    public void processSingle(String keyword) {
        if (!enable) {
            return;
        }

        List<String> processedTitles = historyRecord.readData();

        int currentCnt = 0;
        int currScrollCnt = 0;
        List<String> readTitles = new ArrayList<>();
        while (currentCnt < getRequiredEntryCnt() && currScrollCnt < MAX_SCROLL_DOWN_CNT && !pending) {
            List<AccessibilityNodeInfo> entries = CommonUtil.findAllByText(accessibilityService, null, keyword);
            for (AccessibilityNodeInfo entry : entries) {

                try {
                    // 屏幕外的部分不要
                    Rect rect = new Rect();
                    entry.getBoundsInScreen(rect);
                    if (rect.left < 0 || rect.right < 0) {
                        continue;
                    }

                    AccessibilityNodeInfo btn = entry.getParent();
                    String title = btn.getChild(0).getText().toString().trim();
                    if (processedTitles.contains(title)) {
                        Log.d(TAG, "已经读过/看过：" + title);
                        continue;
                    }

                    if (!readTitles.contains(title)) {
                        // 发现不重复项
                        readTitles.add(title);
                        if (CommonUtil.click(btn, 1000)) {
                            Log.d(TAG, "进入单项");
                            if (processEntry(title)) {
                                // 记录数据
                                historyRecord.writeData(title);
                                currentCnt++;
                            }
                            CommonUtil.globalBack(accessibilityService, 1000);

                            int currentScore = getScore();
                            if (initScore == -1) {
                                initScore = currentScore;
                            }

                            // 分数增长达到目标，可以提前结束
                            if (currentScore - initScore >= EXPECT_SCORE_INCR) {
                                break;
                            }

                            if (currentCnt >= getRequiredEntryCnt()) {
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }

            if (currentCnt < getRequiredEntryCnt()) {
                GestureUtil.scrollDown(accessibilityService, 500);
                currScrollCnt++;
                sleep(800);
            }
        }

        //学习结束
        enable = false;
    }

    private int getScore() {
        AccessibilityNodeInfo nodeInfo = CommonUtil.findFirstNodeByText(accessibilityService, null, "积分");
        if (nodeInfo == null) {
            return -1;
        }

        if (nodeInfo.getParent() != null && nodeInfo.getParent().getChildCount() >= 2) {
            nodeInfo = nodeInfo.getParent().getChild(1);
            return Integer.valueOf(nodeInfo.getText().toString());
        } else {
            return -1;
        }
    }

    public void stop() {
        this.pending = true;
    }

    // 进入单项后如何处理
    abstract boolean processEntry(String title);

    // 需要学习的数量
    abstract int getRequiredEntryCnt();


}
