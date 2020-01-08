package com.shakazxx.couponspeeder.core.util;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK;

public class CommonUtil {

    public static void sleep(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static List<AccessibilityNodeInfo> findAllByViewId(AccessibilityService service, AccessibilityNodeInfo root, String viewId) {
        if (root == null) {
            root = service.getRootInActiveWindow();
        }
        if (root == null) {
            return new ArrayList<>();
        }

        return root.findAccessibilityNodeInfosByViewId(viewId);
    }

    public static List<AccessibilityNodeInfo> findAllByText(AccessibilityService service, AccessibilityNodeInfo root, String text) {
        if (root == null) {
            root = service.getRootInActiveWindow();
        }
        if (root == null) {
            return new ArrayList<>();
        }

        return root.findAccessibilityNodeInfosByText(text);
    }

    public static AccessibilityNodeInfo findFirstByViewId(AccessibilityService service, AccessibilityNodeInfo root, String viewId, long timeout, long checkInterval) {
        if (root == null) {
            root = service.getRootInActiveWindow();
        }
        if (root == null) {
            return null;
        }

        long startTime = System.currentTimeMillis();
        long endTime = 0;

        while (endTime < startTime + timeout) {
            List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByViewId(viewId);
            if (nodes.size() == 0) {
                sleep(checkInterval);
                endTime = System.currentTimeMillis();
                continue;
            }

            return nodes.get(0);
        }

        return null;
    }

    public static AccessibilityNodeInfo findFirstByViewId(AccessibilityService service, AccessibilityNodeInfo root, String viewId) {
        if (root == null) {
            root = service.getRootInActiveWindow();
        }
        if (root == null) {
            return null;
        }

        List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByViewId(viewId);
        if (nodes.size() == 0) {
            return null;
        }

        return nodes.get(0);
    }

    public static AccessibilityNodeInfo findFirstNodeByClassName(AccessibilityService service, AccessibilityNodeInfo root, String nodeClassName) {
        if (root == null) {
            root = service.getRootInActiveWindow();
        }

        if (root == null) {
            return null;
        }

        if (root.getClassName().toString().equalsIgnoreCase(nodeClassName)) {
            return root;
        }

        int maxIndex = root.getChildCount();
        for (int i = 0; i < maxIndex; i++) {
            AccessibilityNodeInfo node = findFirstNodeByClassName(service, root.getChild(i), nodeClassName);
            if (node != null)
                return node;
        }

        return null;
    }

    public static void inputText(AccessibilityNodeInfo inputNode, String text) {
        if (inputNode == null) {
            return;
        }

        inputNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);

        Bundle arguments = new Bundle();
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
        inputNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
    }

    public static AccessibilityNodeInfo findFirstNodeByText(AccessibilityService service, String text, long timeout, long checkInterval) {

        long startTime = System.currentTimeMillis();
        long endTime = 0;

        while (endTime < startTime + timeout) {
            AccessibilityNodeInfo root = service.getRootInActiveWindow();

            if (root == null) {
                sleep(checkInterval);
                endTime = System.currentTimeMillis();
                continue;
            }

            if (root.getText() != null && text.equalsIgnoreCase(root.getText().toString())) {
                return root;
            }

            int maxIndex = root.getChildCount();
            for (int i = 0; i < maxIndex; i++) {
                AccessibilityNodeInfo child = root.getChild(i);
                if (child == null) {
                    continue;
                }

                AccessibilityNodeInfo node = findFirstNodeByText(service, child, text);
                if (node != null) {
                    return node;
                }
            }

            sleep(checkInterval);
            endTime = System.currentTimeMillis();
        }

        return null;
    }

    public static AccessibilityNodeInfo findFirstNodeByText(AccessibilityService service, AccessibilityNodeInfo root, String text) {
        if (root == null) {
            root = service.getRootInActiveWindow();
        }

        if (root == null) {
            return null;
        }

        if (root.getText() != null && text.equalsIgnoreCase(root.getText().toString())) {
            return root;
        }

        int maxIndex = root.getChildCount();
        for (int i = 0; i < maxIndex; i++) {
            AccessibilityNodeInfo child = root.getChild(i);
            if (child == null) {
                continue;
            }

            AccessibilityNodeInfo node = findFirstNodeByText(service, child, text);
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

    public static void globalBack(AccessibilityService service, int delayTime) {
        service.performGlobalAction(GLOBAL_ACTION_BACK);
        sleep(delayTime);
    }
}
