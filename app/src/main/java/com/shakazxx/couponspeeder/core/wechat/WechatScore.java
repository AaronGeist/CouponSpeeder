package com.shakazxx.couponspeeder.core.wechat;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.base.BaseAction;
import com.shakazxx.couponspeeder.core.party.CommentDict;
import com.shakazxx.couponspeeder.core.util.CommonUtil;
import com.shakazxx.couponspeeder.core.util.GestureUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.shakazxx.couponspeeder.core.util.CommonUtil.sleep;

public class WechatScore extends BaseAction {

    private BitSet enable = new BitSet(2);

    private String password;

    private boolean enableCovid9Report;
    private boolean enablePartyReport;

    public WechatScore(AccessibilityService service, Bundle bundle) {
        super(service);

        if (bundle == null) {
            bundle = new Bundle();
        }

        enableCovid9Report = bundle.getBoolean("covid9_report", false);
        enablePartyReport = bundle.getBoolean("party_report", false);

        password = bundle.getString("password");
    }

    public void sendMessage(String name, String msg, boolean isGroupChat) {
        boolean isOnChatPage;
        // 个人和群组有不同的进入方式
        if (!isGroupChat) {
            isOnChatPage = enterInto("通讯录") && enterInto(name) && enterInto("发消息");
        } else {
            isOnChatPage = enterInto("通讯录") && enterInto("群聊") && enterInto(name);
        }

        if (isOnChatPage) {
            AccessibilityNodeInfo inputNode = CommonUtil.findFirstNodeByClassName(accessibilityService, null, "android.widget.EditText");
            CommonUtil.click(inputNode, 1000);

            CommonUtil.inputText(inputNode, msg);
            if (!CommonUtil.click(CommonUtil.findFirstNodeByText(accessibilityService, "发送", 2000, 500), 1500)) {
                // 打开应用后只有第一次可以找到发送按钮，后续只能根据按钮位置点击
                GestureUtil.click(accessibilityService, 990, 1230, 1500);
            }
        }
        // 回到首页，恢复状态
        goHomePage();
    }

    public void dailyReport(boolean isTodayPartyStudyDone, String resultLog) {
        if (!loginIfNeeded()) {
            return;
        }
        covid9Report();

        partyStudyReport(isTodayPartyStudyDone, resultLog);
    }

    private void covid9Report() {
        if (!enableCovid9Report) {
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("M月d日");
        String today = sdf.format(new Date());
        String msg = "周沐唯体温36.7";
//        sendMessage("哥", msg, false);
        sendMessage("#2021#中一班", msg, true);
//        sendMessage("机器人测试", msg, true);
        sendMessage("哥", today + "幼儿园防疫消息已发", false);

        enableCovid9Report = false;
    }

    private void partyStudyReport(boolean isTodayPartyStudyDone, String resultLog) {
        if (!enablePartyReport) {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("M月d日");
        String today = sdf.format(new Date());
        String msg = String.format("[庆祝][庆祝]%s学习强国完成[烟花][烟花]\n%s", today, resultLog);
        if (!isTodayPartyStudyDone) {
            msg = String.format("[叹气][叹气]%s学习强国未完成[衰][衰]\n%s", today, resultLog);
        }
        sendMessage("姐", msg, false);
        sendMessage("哥", msg, false);

        enablePartyReport = false;
    }

    private boolean enterInto(String entryName) {
        AccessibilityNodeInfo node = CommonUtil.findFirstNodeByText(accessibilityService, entryName, 10000, 1000);
        if (node == null) {
            return false;
        }

        if (!CommonUtil.click(node, 1500)) {
            Rect rect = new Rect();
            node.getBoundsInScreen(rect);
            GestureUtil.click(accessibilityService, (rect.left + rect.right) / 2, (rect.top + rect.bottom) / 2, 1500);
        }
        return true;
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
