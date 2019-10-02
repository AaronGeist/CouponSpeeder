package com.shakazxx.couponspeeder.core.Score;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.CommonUtil;

import java.util.List;

public abstract class BaseScoreFetcher {


    protected boolean enable = true;

    protected AccessibilityService accessibilityService;

    public BaseScoreFetcher(AccessibilityService service) {
        accessibilityService = service;
    }

    public boolean fetch(String entryText) {
        if (!enable) {
            return false;
        }

        AccessibilityNodeInfo root = accessibilityService.getRootInActiveWindow();
        if (root == null) {
            return false;
        }

        List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByText(entryText);
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
            if (!CommonUtil.click(node, 2000)) {
                return false;
            }

            root = accessibilityService.getRootInActiveWindow();
            if (root == null) {
                return false;
            }

            node = CommonUtil.findFirstNodeByText(root, "签到领积分");
            if (CommonUtil.click(node, 0)) {
                enable = false;
                return true;
            }
        }

        return false;
    }

    abstract protected boolean loginIfNeeded();
}
