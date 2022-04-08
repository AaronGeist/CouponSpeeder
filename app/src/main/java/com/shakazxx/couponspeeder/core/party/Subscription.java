package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.CommonUtil;
import com.shakazxx.couponspeeder.core.util.FileUtil;
import com.shakazxx.couponspeeder.core.util.GestureUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Subscription extends BaseLearner {

    public Subscription(AccessibilityService service, Bundle bundle) {
        super(service, bundle);
    }

    private List<String> entries = Arrays.asList("推荐", "上新", "主要央媒", "行业媒体", "机关企事业", "党刊", "高校", "地方媒体", "社会机构");

    @Override
    public void processSingle(String keyword) {
        if (!enable) {
            return;
        }

        Set<String> subList = loadSubscriptionList();

        subList.add("强国号");
        subList.add("地方平台");

        sub(subList);

        // go back
        CommonUtil.globalBack(accessibilityService, 1000);
        CommonUtil.globalBack(accessibilityService, 1000);
        CommonUtil.globalBack(accessibilityService, 1000);

        enable = false;
    }

    @Override
    void loadConfiguration() {

    }

    private void sub(Set<String> subList) {
        findEntrance("添加");

        Set<String> titles = new HashSet<>();
        for (String entry : entries) {
            AccessibilityNodeInfo node = CommonUtil.findFirstNodeByDesc(accessibilityService, null, entry);
            if (node != null) {
                CommonUtil.click(node, 1000);
            }

            int lastSize = 0;
            int cnt = 0;
            while (true) {
                Map<String, AccessibilityNodeInfo> res = CommonUtil.findAllDesc(accessibilityService, null);
                Set<String> title_res = res.keySet();
                // find title not subscribed yet
                for (String title : title_res) {
                    String[] titleItems = title.split("\n");
                    if (titleItems.length > 1) {
                        String realTitle = titleItems[0];
                        if (!subList.contains(realTitle)) {
                            Log.d(TAG, "sub: new one " + realTitle);
                            subList.add(realTitle);
                        }
                    }
                }

                titles.addAll(title_res);
                if (lastSize == titles.size()) {
                    cnt += 1;
                    Log.d(TAG, "loadSub: cnt++");
                    if (cnt > 5) {
                        Log.d(TAG, "loadSub: stop");
                        break;
                    }
                } else {
                    Log.d(TAG, "loadSub: now " + titles.size());
                    lastSize = titles.size();
                    cnt = 0;
                }

                GestureUtil.scrollDown(accessibilityService, 500);
                CommonUtil.sleep(1000);
            }

            Log.d(TAG, "sub: to go next");
        }
    }

    private Set<String> loadSubscriptionList() {
        Set<String> titles = new HashSet<>();

        int lastSize = 0;
        int cnt = 0;
        while (true) {
            Map<String, AccessibilityNodeInfo> res = CommonUtil.findAllText(accessibilityService, null);
            Set<String> title_res = res.keySet();
            titles.addAll(title_res);
            if (lastSize == titles.size()) {
                cnt += 1;
                Log.d(TAG, "loadSub: cnt++");
                if (cnt > 5) {
                    Log.d(TAG, "loadSub: stop");
                    break;
                }
            } else {
                Log.d(TAG, "loadSub: now " + titles.size());
                lastSize = titles.size();
                cnt = 0;
            }

            GestureUtil.scrollDown(accessibilityService, 500);
            CommonUtil.sleep(1000);
        }

        Log.d(TAG, "loadSub: write now " + titles.size());

//        StringBuilder data = new StringBuilder();
//        for (String title : titles) {
//            if (title.contains(",") || title.contains("，") || title.contains("。") || title.length() > 10) {
//                Log.d(TAG, "loadSub: ---- " + title);
//                continue;
//            } else {
//                Log.d(TAG, "loadSub: ++++ " + title);
//                data.append(title).append("|");
//            }
//        }

//        FileUtil.writeLine(FileUtil.getRootPath() + "/Download/partyStudySubs.txt", data.toString(), false);
        return titles;
    }

    private boolean isTitle(String title) {
        return !(title.contains(",") || title.contains("，") || title.contains("。") || title.length() > 10);
    }

    @Override
    boolean processEntry(String title) {
        return false;
    }

    @Override
    int getRequiredEntryCnt() {
        return 1;
    }

    @Override
    int expectScoreIncr() {
        return 1;
    }
}
