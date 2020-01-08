package com.shakazxx.couponspeeder.core.Score;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.CommonUtil;
import com.shakazxx.couponspeeder.core.util.GestureUtil;

import static com.shakazxx.couponspeeder.core.util.CommonUtil.sleep;

public class SpdccScoreFetcher extends BaseScoreFetcher {

    boolean isLogin = false;

    public SpdccScoreFetcher(AccessibilityService service) {
        super(service);
    }

    public void fetch() {
        if (!enable) {
            return;
        }

        if (loginIfNeeded()) {
            fetch("登录");
        }
    }

    @Override
    public boolean fetch(String text) {
        GestureUtil.click(accessibilityService, getWidth() - 10, 300, 10000);
        GestureUtil.click(accessibilityService, getWidth() / 2, 500, 3000);
        CommonUtil.globalBack(accessibilityService, 1000);

        enable = false;
        return true;
    }

    @Override
    protected boolean loginIfNeeded() {
        if (!enable) {
            return false;
        }

        if (isLogin) {
            return true;
        }

        sleep(20000);
        GestureUtil.click(accessibilityService, getWidth() - 100, getHeight() - 30, 2000);

        AccessibilityNodeInfo loginBtn = CommonUtil.findFirstNodeByText(accessibilityService, null, "登录 / 注册");
        if (loginBtn == null) {
            isLogin = true;
            return true;
        }

        if (!CommonUtil.click(loginBtn, 5000)) {
            return false;
        }

        GestureUtil.click(accessibilityService, 400, 1700, 10000);

        isLogin = CommonUtil.findFirstNodeByText(accessibilityService, null, "登录 / 注册") == null;
        return isLogin;
    }
}
