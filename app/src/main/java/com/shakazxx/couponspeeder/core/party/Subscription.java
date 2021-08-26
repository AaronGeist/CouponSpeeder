package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.CommonUtil;
import com.shakazxx.couponspeeder.core.util.GestureUtil;

public class Subscription extends BaseLearner {

    public Subscription(AccessibilityService service, Bundle bundle) {
        super(service, bundle);
    }

    @Override
    public void processSingle(String keyword) {
        if (!enable) {
            return;
        }

        AccessibilityNodeInfo node = CommonUtil.findFirstNodeByClassName(accessibilityService, null, "android.view.View", "强国号");
        if (node != null) {
            CommonUtil.click(node, 1000);
        }
        GestureUtil.click(accessibilityService, 600, 550, 5000);
        CommonUtil.globalBack(accessibilityService, 1000);

        enable = false;
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
