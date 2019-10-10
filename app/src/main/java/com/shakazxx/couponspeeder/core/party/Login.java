package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.base.BaseAction;
import com.shakazxx.couponspeeder.core.util.CommonUtil;
import com.shakazxx.couponspeeder.core.util.GestureUtil;

public class Login extends BaseAction {


    public Login(AccessibilityService service) {
        super(service);
    }

    public boolean process() {
        AccessibilityNodeInfo node = CommonUtil.findFirstNodeByText(accessibilityService, null, "密码");
        if (node == null) {
            return true;
        }

        AccessibilityNodeInfo input = node.getParent().getChild(4);

        CommonUtil.inputText(input, "xcl880809");

        AccessibilityNodeInfo btn = CommonUtil.findFirstNodeByText(accessibilityService, null, "登录");

        if (CommonUtil.click(btn, 3000)) {
            GestureUtil.click(accessibilityService, getWidth() - 20, getHeight() - 20, 1000);
            showToast("登录成功");
            return true;
        }

        return false;
    }
}
