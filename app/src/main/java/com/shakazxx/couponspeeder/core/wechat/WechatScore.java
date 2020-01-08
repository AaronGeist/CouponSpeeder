package com.shakazxx.couponspeeder.core.wechat;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.base.BaseAction;
import com.shakazxx.couponspeeder.core.util.CommonUtil;
import com.shakazxx.couponspeeder.core.util.GestureUtil;

import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.shakazxx.couponspeeder.core.util.CommonUtil.sleep;

public class WechatScore extends BaseAction {

    private BitSet enable = new BitSet(2);

    private String password;

    public WechatScore(AccessibilityService service, Bundle bundle) {
        super(service);

        if (bundle == null) {
            bundle = new Bundle();
        }

        password = bundle.getString("password");
    }

    public void cmbScore() {
        // both has finished
        if (enable.cardinality() == 1) {
            return;
        }

        if (!loginIfNeeded()) {
            return;
        }

        signScore();
//        programScore();
    }

    public void pbcc() {
        if (!loginIfNeeded()) {
            return;
        }

        exchangeMiles();
    }

    private boolean loginIfNeeded() {
        AccessibilityNodeInfo pwd = CommonUtil.findFirstNodeByText(accessibilityService, null, "请填写微信密码");
        if (pwd == null) {
            return true;
        }

        CommonUtil.inputText(pwd, password);

        // 点击登陆按钮
        GestureUtil.click(accessibilityService, 600, 1100, 5000);
        // 多等一会儿
        return true;
    }

    private boolean signScore() {
        if (enable.get(0)) {
            return true;
        }


        if (CommonUtil.findFirstNodeByText(accessibilityService, "微信", 10000, 1000) == null) {
            return false;
        }

        // 招行信用卡入口
        GestureUtil.click(accessibilityService, 600, 300, 1000);

        AccessibilityNodeInfo item = CommonUtil.findFirstNodeByText(accessibilityService, null, "领积分");
        if (item == null) {
            // title always change, let click fixed position
            GestureUtil.click(accessibilityService, getWidth() - 10, getHeight() - 10, 5000);
        } else {
            CommonUtil.click(item, 5000);
        }

        item = CommonUtil.findFirstNodeByText(accessibilityService, null, "签到有好礼");
        if (item == null) {
            GestureUtil.click(accessibilityService, getWidth() - 100, getHeight() - 600, 5000);
        } else {
            if (!CommonUtil.click(item, 5000)) {
                // go back to home page
                goHomePage();
                return false;
            }
        }

        GestureUtil.click(accessibilityService, 500, 300, 1000);
        enable.set(0);

        // go back to home page
        goHomePage();

        return true;
    }

    private boolean programScore() {
        if (enable.get(1)) {
            return true;
        }

        GestureUtil.scrollDown(accessibilityService, -600);
        sleep(1000);

        List<AccessibilityNodeInfo> nodes = CommonUtil.findAllByViewId(accessibilityService, null, "com.tencent.mm:id/ct");
        if (nodes.size() == 0) {
            return false;
        }

        boolean find = false;
        for (AccessibilityNodeInfo node : nodes) {
            if (node.getText() != null && "招行信用卡".equalsIgnoreCase(node.getText().toString())) {
                CommonUtil.click(node, 10000);
                find = true;
                break;
            }
        }

        if (!find) {
            return false;
        }

        GestureUtil.click(accessibilityService, 500, 1300, 10000);
        GestureUtil.click(accessibilityService, 500, 600, 1000);

        enable.set(1);

        goHomePage();

        return true;
    }

    private boolean exchangeMiles() {

        // 看下时间是不是10点不到些
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour != 9) {
            return false;
        }

        int minute = calendar.get(Calendar.MINUTE);
        if (minute != 59) {
            return false;
        }

        List<AccessibilityNodeInfo> nodes = CommonUtil.findAllByViewId(accessibilityService, null, "com.tencent.mm:id/b4o");
        if (nodes.size() == 0) {
            return false;
        }

        boolean find = false;
        for (AccessibilityNodeInfo node : nodes) {
            if (node.getText() != null && "浦发银行信用卡".equalsIgnoreCase(node.getText().toString())) {
                CommonUtil.click(node, 5000);
                find = true;
                break;
            }
        }

        if (!find) {
            return false;
        }


        AccessibilityNodeInfo item = CommonUtil.findFirstNodeByText(accessibilityService, null, "小浦\uD83C\uDF81福利");
        CommonUtil.click(item, 5000);

        item = CommonUtil.findFirstNodeByText(accessibilityService, null, "消费红包·全能积分");
        if (!CommonUtil.click(item, 5000)) {
            return false;
        }

        GestureUtil.click(accessibilityService, 100, 1300, 3000);
        GestureUtil.click(accessibilityService, 10, 300, 3000);

        // 等到10点整，再点
        while (true) {
            calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            if (hour != 10) {
                sleep(100);
                continue;
            }
            GestureUtil.click(accessibilityService, 300, 1000, 3000);
            break;
        }

        // go back to home page
        goHomePage();

        return true;
    }

    private void goHomePage() {
        while (true) {
            if (CommonUtil.findFirstNodeByText(accessibilityService, null, "通讯录") != null) {
                return;
            }

            CommonUtil.globalBack(accessibilityService, 1000);
        }
    }
}
