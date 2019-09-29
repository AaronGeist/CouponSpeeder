package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.GestureUtil;

import java.util.List;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK;

public class ArticleReader extends BaseLearner {

    private int articleScrollDownTimes = 125;  //下滑次数

    private int maxMarkCnt = 2;
    private int markCnt = 0;

    private int maxShareCnt = 2;
    private int shareCnt = 0;

    public ArticleReader(AccessibilityService service) {
        super(service);
    }

    @Override
    boolean processEntry(String title) {
        postProcessHook();

        int scrollLength = 1000;

        for (int i = 0; i < articleScrollDownTimes; i++) {
            GestureUtil.scrollDown(accessibilityService, scrollLength);
            Log.d(TAG, ">>>>> down " + i);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }

        return true;
    }

    private void postProcessHook() {
        AccessibilityNodeInfo root = accessibilityService.getRootInActiveWindow();
        List<AccessibilityNodeInfo> elements = root.findAccessibilityNodeInfosByText("欢迎发表你的观点");
        if (elements.size() > 0) {
            AccessibilityNodeInfo element = elements.get(0);
            AccessibilityNodeInfo parent = element.getParent();

            int childCnt = parent.getChildCount();
            // 收藏
            if (childCnt > 6 && markCnt < maxMarkCnt) {
                markCnt++;
                AccessibilityNodeInfo markBtn = parent.getChild(6);
                markBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                sleep(1000);
            }

            // 分享
            if (childCnt > 7 && shareCnt < maxShareCnt) {
                shareCnt++;
                AccessibilityNodeInfo shareBtn = parent.getChild(7);
                shareBtn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                sleep(3000);
                AccessibilityNodeInfo newRoot = accessibilityService.getRootInActiveWindow();
                List<AccessibilityNodeInfo> newElems = newRoot.findAccessibilityNodeInfosByText("分享到学习强国");
                if (newElems.size() > 0) {
                    newElems.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    sleep(3000);
                    accessibilityService.performGlobalAction(GLOBAL_ACTION_BACK);
                    sleep(3000);
                }
            }
//            int cnt = parent.getChildCount();
//            for (int i = 0; i < cnt; i++) {
//                if (i == 1 || i == 3) {
//                    continue;
//                }
//                AccessibilityNodeInfo child = parent.getChild(i);
//                if (child.getClassName().toString().contains("ImageView") && child.isClickable()) {
//                    child.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                }
//            }
        }
    }

    // i=1 回退
    // i=3 分享
    // i=6 收藏
    // i=7 分享
    @Override
    int getRequiredEntryCnt() {
        return 6;
    }
}
