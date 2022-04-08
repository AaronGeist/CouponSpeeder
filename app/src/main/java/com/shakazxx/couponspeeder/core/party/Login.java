package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.base.BaseAction;
import com.shakazxx.couponspeeder.core.util.CommonUtil;
import com.shakazxx.couponspeeder.core.util.GestureUtil;

public class Login extends BaseAction {


    public Login(AccessibilityService service) {
        super(service);
    }

    /**
     * 等待 10s, 如果界面上有 "我的" 则表示登陆了
     *
     * @return 是否登陆
     */
    private boolean isLogin() {
        Log.d(this.getClass().getSimpleName(), "开始检查登陆状态");
        AccessibilityNodeInfo node = CommonUtil.findFirstNodeByText(accessibilityService, "我的", 5 * 1000, 1000);
        boolean result = (node != null);
        Log.d(this.getClass().getSimpleName(), "登陆状态: " + (result ? "是" : "否"));

        return result;
    }

    public boolean process() {
        if (isLogin()) {
            return true;
        }

        AccessibilityNodeInfo node = CommonUtil.findFirstNodeByText(accessibilityService, null, "密码");
        if (node == null) {
            Log.d("", "没有发现登陆界面，登陆失败");
            return false;
        }

        AccessibilityNodeInfo input = node.getParent().getChild(4);

        CommonUtil.inputText(input, "xcl880809");

        AccessibilityNodeInfo btn = CommonUtil.findFirstNodeByText(accessibilityService, null, "登录");

        if (CommonUtil.click(btn, 3000)) {
            GestureUtil.click(accessibilityService, getWidth() - 20, getHeight() - 20, 1000);
            showToast("登录成功");
            return true;
        }

        Log.d("", "未知原因，登陆失败");

        return false;
    }
}
