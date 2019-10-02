package com.shakazxx.couponspeeder.core.Score;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.CommonUtil;

import java.util.List;

public class SpdccScoreFetcher extends BaseScoreFetcher {

    public SpdccScoreFetcher(AccessibilityService service) {
        super(service);
    }

    public void fetch() {
        if (loginIfNeeded()) {
            fetch("登录");
        }
    }

    @Override
    protected boolean loginIfNeeded() {
        if (!enable) {
            return false;
        }

        AccessibilityNodeInfo root = accessibilityService.getRootInActiveWindow();
        if (root == null) {
            return false;
        }

        List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByText("登录");
        if (nodes.size() == 0) {
            return true;
        }

        CommonUtil.click(nodes.get(0), 2000);


        Path path = new Path();
        path.moveTo(250, 900);
        path.lineTo(250, 1500);
        final GestureDescription.StrokeDescription sd = new GestureDescription.StrokeDescription(path, 0, 5000, true);
        Path path2 = new Path();
        path2.moveTo(250, 1500);
        path2.lineTo(550, 1500);
        final GestureDescription.StrokeDescription sd2 = sd.continueStroke(path2, 0, 5000, false);
        accessibilityService.dispatchGesture(new GestureDescription.Builder().addStroke(sd).addStroke(sd2).build(), null, null);


//        GestureUtil.drawLine(accessibilityService, paths);

        return true;
    }
}
