package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.CommonUtil;
import com.shakazxx.couponspeeder.core.util.GestureUtil;

import java.util.List;

import static com.shakazxx.couponspeeder.core.util.CommonUtil.sleep;

public class ArticleReader extends BaseLearner {

    private static final int REQUIRED_TIME_IN_SECOND = 130;  //等待时间  130
    private static final int REQUIRED_SHARE_CNT = 4; //分享次数
    private static final int MAX_SCROLL_DOWN_CNT = 15; // 下滑最大次数

    private int shareCnt = 0;

    public ArticleReader(AccessibilityService service) {
        super(service);
    }

    @Override
    boolean processEntry(String title) {
        preProcessHook();

        boolean bottom = false;
        long startTime = System.currentTimeMillis();
        long endTime;

        int scrollDownCnt = 0;
        while (!pending) {
            endTime = System.currentTimeMillis();
            // 累计时间到了，不看了
            if (endTime - startTime > REQUIRED_TIME_IN_SECOND * 1000) {
                Log.d(TAG, "阅读时间满足条件");
                break;
            }

            if (!bottom) {
                GestureUtil.scrollDown(accessibilityService, r.nextInt(200) + 800);
                scrollDownCnt++;
                Log.d(TAG, "下滑+1");

                if ((scrollDownCnt > MAX_SCROLL_DOWN_CNT) ||
                        (CommonUtil.findFirstNodeByText(accessibilityService, null, "已显示全部观点") != null)) {
                    Log.d(TAG, "到底了");

                    bottom = true;
                }
            }

            Log.d(TAG, "等待时间：" + (endTime - startTime) / 1000);

            sleep(1000);
        }

        return true;
    }

    private void preProcessHook() {
        AccessibilityNodeInfo root = accessibilityService.getRootInActiveWindow();
        if (root == null) {
            return;
        }
        List<AccessibilityNodeInfo> elements = root.findAccessibilityNodeInfosByText("欢迎发表你的观点");
        if (elements.size() > 0) {
            // 收藏
            GestureUtil.click(accessibilityService, getWidth() - 300, getHeight() - 50, 1000);

            // 分享
            if (shareCnt < REQUIRED_SHARE_CNT) {
                shareCnt++;
                GestureUtil.click(accessibilityService, getWidth() - 100, getHeight() - 50, 1000);
                AccessibilityNodeInfo newRoot = accessibilityService.getRootInActiveWindow();
                if (newRoot == null) {
                    return;
                }
                List<AccessibilityNodeInfo> newElems = newRoot.findAccessibilityNodeInfosByText("分享到学习强国");
                if (newElems.size() > 0) {
                    CommonUtil.click(newElems.get(0), 1000);
                    CommonUtil.globalBack(accessibilityService, 2000);
                }
            }
        }
    }

    @Override
    int getRequiredEntryCnt() {
        return 10;
    }
}
