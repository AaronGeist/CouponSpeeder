package com.shakazxx.couponspeeder.core.util;

import android.view.accessibility.AccessibilityNodeInfo;

public class CommonUtil {

    public static void sleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean find(AccessibilityNodeInfo root, String nodeClassName) {
        if (root == null || root.getChildCount() == 0) {
            return false;
        }

        if (root.getClassName().toString().equalsIgnoreCase(nodeClassName)) {
            return true;
        }

        int maxIndex = root.getChildCount();
        for (int i = 0; i < maxIndex; i++) {
            if (find(root.getChild(i), nodeClassName)) {
                return true;
            }
        }

        return false;
    }
}
