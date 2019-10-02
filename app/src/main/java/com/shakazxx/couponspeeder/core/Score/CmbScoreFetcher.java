package com.shakazxx.couponspeeder.core.Score;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.CommonUtil;
import com.shakazxx.couponspeeder.core.util.GestureUtil;

import java.util.List;

public class CmbScoreFetcher extends BaseScoreFetcher {

    public CmbScoreFetcher(AccessibilityService service) {
        super(service);
    }

    public boolean fetch() {
        if (!enable) {
            return false;
        }

        AccessibilityNodeInfo root = accessibilityService.getRootInActiveWindow();
        if (root == null) {
            return false;
        }

        List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByText("我的积分按钮");
        if (nodes.size() > 0) {
            CommonUtil.click(nodes.get(0), 2000);

            if (!loginIfNeeded()) {
                // 登录失败
                return false;
            }

            root = accessibilityService.getRootInActiveWindow();
            if (root == null) {
                return false;
            }

            AccessibilityNodeInfo node = CommonUtil.findFirstNodeByText(root, "签到");
            if (!CommonUtil.click(node, 8000)) {
                return false;
            }

            root = accessibilityService.getRootInActiveWindow();
            if (root == null) {
                return false;
            }

            node = CommonUtil.findFirstNodeByText(root, "签到领积分");
            if (CommonUtil.click(node, 0)) {
                accessibilityService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                enable = false;
                return true;
            }
        }

        return false;
    }

    protected boolean loginIfNeeded() {
        AccessibilityNodeInfo root = accessibilityService.getRootInActiveWindow();
        if (root == null) {
            return false;
        }

        AccessibilityNodeInfo btn = CommonUtil.findFirstNodeByText(root, "登录");
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
