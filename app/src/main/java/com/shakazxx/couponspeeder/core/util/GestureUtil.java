package com.shakazxx.couponspeeder.core.util;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.graphics.Path;

public class GestureUtil {

    public static void scrollDown(AccessibilityService service, int distance) {
        GestureDescription.Builder builder = new GestureDescription.Builder();

        Path path = new Path();
        int y = 1200;
        int x = 360;

        path.moveTo(x, y);
        path.lineTo(x, y -= distance);

        GestureDescription gestureDescription = builder
                .addStroke(new GestureDescription.StrokeDescription(path, 200L, 800L, false))
                .build();

        service.dispatchGesture(gestureDescription, null, null);
    }
}
