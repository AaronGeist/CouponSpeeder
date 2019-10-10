package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
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

    // local variables
    private boolean enable = true;

    // constructor
    public BaseLearner(AccessibilityService service) {
        super(service);
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

        int currentCnt = 0;
        List<String> readTitles = new ArrayList<>();
        while (currentCnt < getRequiredEntryCnt()) {
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
                    String title = btn.getChild(0).getText().toString();
                    if (!readTitles.contains(title)) {
                        // 发现不重复项
                        showToast("当前标题: " + title);
                        readTitles.add(title);
                        if (CommonUtil.click(btn, 1000)) {
                            Log.d(TAG, "进入单项");
                            if (processEntry(title)) {
                                currentCnt++;
                            }
                            CommonUtil.globalBack(accessibilityService, 1000);

                            if (currentCnt >= getRequiredEntryCnt()) {
                                break;
                            }
                        }
                    } else {
                        showToast("无法点击进入: " + title);
                    }
                } catch (Exception e) {
                }
            }

            if (currentCnt < getRequiredEntryCnt()) {
                GestureUtil.scrollDown(accessibilityService, 500);
                sleep(3000);
            }
        }

        //学习结束
        enable = false;
    }

    // 进入单项后如何处理
    abstract boolean processEntry(String title);

    // 需要学习的数量
    abstract int getRequiredEntryCnt();


}
