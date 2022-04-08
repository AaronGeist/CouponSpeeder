package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;

import com.shakazxx.couponspeeder.core.util.CommonUtil;
import com.shakazxx.couponspeeder.core.util.GestureUtil;

public class LocalChannel extends BaseLearner {

    public LocalChannel(AccessibilityService service, Bundle bundle) {
        super(service, bundle);
    }

    @Override
    public void processSingle(String keyword) {
        if (!enable) {
            return;
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        GestureUtil.click(accessibilityService, 600, 550, 5000);
        CommonUtil.globalBack(accessibilityService, 1000);

        enable = false;
    }

    @Override
    void loadConfiguration() {
        enable = bundle.getBoolean("enable_tv", true);
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
