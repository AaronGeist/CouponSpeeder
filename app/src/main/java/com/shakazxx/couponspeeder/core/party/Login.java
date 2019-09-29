package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.GestureUtil;

import java.util.List;

public class Login {

    private AccessibilityService accessibilityService;

    public Login(AccessibilityService service) {
        accessibilityService = service;
    }

    public boolean process() {
        AccessibilityNodeInfo root = accessibilityService.getRootInActiveWindow();
        if (root == null) {
            return false;
        }

        List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByText("密码");
        if (nodes.size() == 0) {
            return true;
        }

        AccessibilityNodeInfo input = nodes.get(0).getParent().getChild(4);
        input.performAction(AccessibilityNodeInfo.ACTION_FOCUS);

        Bundle arguments = new Bundle();
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "");
        input.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

        nodes = root.findAccessibilityNodeInfosByText("登录");
        if (nodes.size() > 0) {
            AccessibilityNodeInfo btn = nodes.get(0);
            if (btn.isClickable()) {
                btn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                sleep(1000);

                WindowManager wm = (WindowManager) accessibilityService.getSystemService(Context.WINDOW_SERVICE);
                DisplayMetrics dm = new DisplayMetrics();
                wm.getDefaultDisplay().getMetrics(dm);

                GestureUtil.click(accessibilityService, dm.widthPixels - 10, dm.heightPixels - 10);
                sleep(1000);

                return true;
            }
        }

        return false;
    }

    protected void sleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
