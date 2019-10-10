package com.shakazxx.couponspeeder.core.Score;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.CommonUtil;
import com.shakazxx.couponspeeder.core.util.GestureUtil;

public class CmbScoreFetcher extends BaseScoreFetcher {

    public CmbScoreFetcher(AccessibilityService service) {
        super(service);
    }

    public boolean fetch() {
        if (!enable) {
            return false;
        }

        if (!CommonUtil.click(CommonUtil.findFirstNodeByText(accessibilityService, null, "我的"), 2000)) {
            return false;
        }

        if (!loginIfNeeded()) {
            // 登录失败
            return false;
        }

        if (!CommonUtil.click(CommonUtil.findFirstNodeByText(accessibilityService, null, "积分"), 3000)) {
            return false;
        }

        if (!CommonUtil.click(CommonUtil.findFirstNodeByText(accessibilityService, null, "签到"), 8000)) {
            return false;
        }

        if (CommonUtil.click(CommonUtil.findFirstNodeByText(accessibilityService, null, "签到领积分"), 0)
                || (CommonUtil.findFirstNodeByText(accessibilityService, null, "今日已签到") != null)) {
            accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
            enable = false;
            return true;
        }

        return false;
    }

    protected boolean loginIfNeeded() {
        AccessibilityNodeInfo btn = CommonUtil.findFirstNodeByText(accessibilityService, null, "登录");
        if (btn == null) {
            return true;
        }

        GestureUtil.click(accessibilityService, 50, 1800, 500);
        GestureUtil.click(accessibilityService, 50, 1400, 500);
        GestureUtil.click(accessibilityService, 120, 1400, 500);
        GestureUtil.click(accessibilityService, 240, 1400, 500);
        GestureUtil.click(accessibilityService, 350, 1400, 500);
        GestureUtil.click(accessibilityService, 120, 1500, 500);
        GestureUtil.click(accessibilityService, 250, 1500, 500);
        GestureUtil.click(accessibilityService, 320, 1500, 500);

        GestureUtil.click(accessibilityService, 450, 1500, 500);

        return CommonUtil.click(btn, 8000);
    }
}
