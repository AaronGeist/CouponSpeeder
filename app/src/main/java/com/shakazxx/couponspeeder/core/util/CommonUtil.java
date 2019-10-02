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

    public static AccessibilityNodeInfo findFirstNodeByClassName(AccessibilityNodeInfo root, String nodeClassName) {
        if (root == null) {
            return null;
        }

        if (root.getClassName().toString().equalsIgnoreCase(nodeClassName)) {
            return root;
        }

        int maxIndex = root.getChildCount();
        for (int i = 0; i < maxIndex; i++) {
            AccessibilityNodeInfo node = findFirstNodeByClassName(root.getChild(i), nodeClassName);
            if (node != null)
                return node;
        }

        return null;
    }


    public static AccessibilityNodeInfo findFirstNodeByText(AccessibilityNodeInfo root, String text) {
        if (root == null) {
            return null;
        }

        if (root.getText() != null && text.equalsIgnoreCase(root.getText().toString())) {
            return root;
        }

        int maxIndex = root.getChildCount();
        for (int i = 0; i < maxIndex; i++) {
            AccessibilityNodeInfo node = findFirstNodeByText(root.getChild(i), text);
            if (node != null) {
                return node;
            }
        }

        return null;
    }

    public static boolean click(AccessibilityNodeInfo node, int delayTime) {
        if (node == null) {
            return false;
        }

        if (node.isClickable()) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            sleep(delayTime);
            return true;
        } else {
            return click(node.getParent(), delayTime);
        }
    }
}
