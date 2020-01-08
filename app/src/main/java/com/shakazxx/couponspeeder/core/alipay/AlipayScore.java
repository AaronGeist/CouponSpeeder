package com.shakazxx.couponspeeder.core.alipay;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.base.BaseAction;
import com.shakazxx.couponspeeder.core.util.CommonUtil;
import com.shakazxx.couponspeeder.core.util.GestureUtil;

import java.util.BitSet;
import java.util.List;

public class AlipayScore extends BaseAction {

    private BitSet enable = new BitSet(2);

    public AlipayScore(AccessibilityService accessibilityService) {
        super(accessibilityService);
    }

    public void cmbScore() {
        // both has finished
        if (enable.cardinality() == 2) {
            return;
        }

        if (!loginIfNeeded()) {
            return;
        }

        if (enter()) {
            sign4Score();
            guess50();
        }
    }

    private boolean loginIfNeeded() {
        AccessibilityNodeInfo node = CommonUtil.findFirstByViewId(accessibilityService, null, "com.ali.user.mobile.security.ui:id/userAccountImage", 5000, 1000);

        // 没有登录头像，说明已经登录了
        if (node == null) {
            return true;
        }

        if (!CommonUtil.click(node, 1000)) {
            return false;
        }

        AccessibilityNodeInfo switcher = CommonUtil.findFirstNodeByText(accessibilityService, null, "密码登录");
        if (!CommonUtil.click(switcher, 1000)) {
            return false;
        }

        AccessibilityNodeInfo pwd = CommonUtil.findFirstNodeByText(accessibilityService, null, "请输入登录密码");
        if (pwd == null) {
            return false;
        }

        CommonUtil.inputText(pwd, "1234qwerasdf");

        AccessibilityNodeInfo btn = CommonUtil.findFirstByViewId(accessibilityService, null, "com.ali.user.mobile.security.ui:id/loginButton");

        // 多等一会儿
        return CommonUtil.click(btn, 5000);
    }

    private boolean enter() {
        AccessibilityNodeInfo tab = CommonUtil.findFirstNodeByText(accessibilityService, "朋友", 10000, 1000);

        if (!CommonUtil.click(tab, 3000)) {
            return false;
        }

        AccessibilityNodeInfo node = CommonUtil.findFirstNodeByText(accessibilityService, null, "招商银行信用卡");

        Rect rect = new Rect();
        node.getBoundsInScreen(rect);
        GestureUtil.click(accessibilityService, rect.left + 20, rect.top + 20, 3000);

        return true;
    }

    private boolean sign4Score() {
        if (enable.get(0)) {
            return true;
        }

        GestureUtil.click(accessibilityService, getWidth() - 50, getHeight() - 20, 3000);

        AccessibilityNodeInfo item = CommonUtil.findFirstNodeByText(accessibilityService, null, "打卡领积分");
        if (!CommonUtil.click(item, 10000)) {
            return false;
        }

        enable.set(0);

        return true;
    }

    private boolean guess50() {
        if (enable.get(1)) {
            return true;
        }

        GestureUtil.click(accessibilityService, 20, getHeight() - 20, 3000);

        int maxCnt = 3;
        int cnt = 0;
        while (cnt < maxCnt) {
            cnt++;
            List<AccessibilityNodeInfo> nodes = CommonUtil.findAllByViewId(accessibilityService, null, "com.alipay.mobile.pubsvc:id/input_edit");
            if (nodes.size() == 0) {
                return false;
            }

            AccessibilityNodeInfo input = nodes.get(0);
            input.performAction(AccessibilityNodeInfo.ACTION_FOCUS);

            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, "秋日补贴");
            input.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

            nodes = CommonUtil.findAllByViewId(accessibilityService, null, "com.alipay.mobile.pubsvc:id/sendBtn");
            if (nodes.size() == 0) {
                return false;
            }

            int waitTime = r.nextInt(30000) + 30000;
            if (cnt >= maxCnt) {
                waitTime = 1000;
            }
            CommonUtil.click(nodes.get(0), waitTime);
        }

        enable.set(1);
        return true;
    }
}
