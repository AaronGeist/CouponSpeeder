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

    public static void scrollRight(AccessibilityService service, int x, int y, int distance) {
        GestureDescription.Builder builder = new GestureDescription.Builder();

        Path path = new Path();

        path.moveTo(x, y);
        path.lineTo(x += distance, y);

        GestureDescription gestureDescription = builder
                .addStroke(new GestureDescription.StrokeDescription(path, 200L, 800L, false))
                .build();

        service.dispatchGesture(gestureDescription, null, null);
    }

    public static void scrollLeft(AccessibilityService service, int x, int y, int distance) {
        GestureDescription.Builder builder = new GestureDescription.Builder();

        Path path = new Path();

        path.moveTo(x, y);
        path.lineTo(x -= distance, y);

        GestureDescription gestureDescription = builder
                .addStroke(new GestureDescription.StrokeDescription(path, 200L, 800L, false))
                .build();

        service.dispatchGesture(gestureDescription, null, null);
    }

    public static void click(AccessibilityService service, int x, int y) {
        GestureDescription.Builder builder = new GestureDescription.Builder();

        Path path = new Path();

        path.moveTo(x, y);

        GestureDescription gestureDescription = builder
                .addStroke(new GestureDescription.StrokeDescription(path, 1L, 1L, false))
                .build();

        service.dispatchGesture(gestureDescription, null, null);
    }

    public static void miuiScreenShot(AccessibilityService service) {
        int y1 = 800;
        int y2 = 1200;
        int x1 = 200;
        int x2 = 400;
        int x3 = 600;
        Path path1 = new Path();
        path1.moveTo(x1, y1);
        path1.lineTo(x1, y2);
        Path path2 = new Path();
        path2.moveTo(x2, y1);
        path2.lineTo(x2, y2);
        Path path3 = new Path();
        path3.moveTo(x3, y1);
        path3.lineTo(x3, y2);
        GestureDescription.Builder builder = new GestureDescription.Builder();
        GestureDescription gestureDescription = builder
                .addStroke(new GestureDescription.StrokeDescription(path1, 0, 1000))
                .addStroke(new GestureDescription.StrokeDescription(path2, 0, 1000))
                .addStroke(new GestureDescription.StrokeDescription(path3, 0, 1000))
                .build();
        service.dispatchGesture(gestureDescription, null, null);

    }
}
