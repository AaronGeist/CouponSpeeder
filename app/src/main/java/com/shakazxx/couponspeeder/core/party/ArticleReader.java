package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.CommonUtil;
import com.shakazxx.couponspeeder.core.util.GestureUtil;

import java.util.List;

public class ArticleReader extends BaseLearner {

    private int articleScrollDownTimes = 130;  //下滑次数

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

        for (int i = 0; i < articleScrollDownTimes; i++) {
            GestureUtil.scrollDown(accessibilityService, r.nextInt(200) + 800);
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
        if (root == null) {
            return;
        }
        List<AccessibilityNodeInfo> elements = root.findAccessibilityNodeInfosByText("欢迎发表你的观点");
        if (elements.size() > 0) {
            AccessibilityNodeInfo element = elements.get(0);
            AccessibilityNodeInfo parent = element.getParent();

            int childCnt = parent.getChildCount();
            // 收藏
            if (childCnt > 6 && markCnt < maxMarkCnt) {
                markCnt++;
                AccessibilityNodeInfo markBtn = parent.getChild(6);
                CommonUtil.click(markBtn, 1000);
            }

            // 分享
            if (childCnt > 7 && shareCnt < maxShareCnt) {
                shareCnt++;
                AccessibilityNodeInfo shareBtn = parent.getChild(7);
                CommonUtil.click(shareBtn, 3000);
                AccessibilityNodeInfo newRoot = accessibilityService.getRootInActiveWindow();
                if (newRoot == null) {
                    return;
                }
                List<AccessibilityNodeInfo> newElems = newRoot.findAccessibilityNodeInfosByText("分享到学习强国");
                if (newElems.size() > 0) {
                    CommonUtil.click(newElems.get(0), 3000);
                    CommonUtil.globalBack(accessibilityService, 3000);
                }
            }
        }
    }

    @Override
    int getRequiredEntryCnt() {
        return 8;
    }
}
