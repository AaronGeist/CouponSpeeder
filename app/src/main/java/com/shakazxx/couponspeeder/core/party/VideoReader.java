package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.GestureUtil;

import java.util.List;
import java.util.Random;

import static com.shakazxx.couponspeeder.core.util.CommonUtil.sleep;

public class VideoReader extends BaseLearner {

    private int videoReadTimeInSecondsLeft = 180 * getRequiredEntryCnt();  //视频观看秒数

    private Random r = new Random();

    public VideoReader(AccessibilityService service) {
        super(service);
    }

    @Override
    boolean processEntry(String title) {
        // 第一个很长，跳过
        if (title.contains(":")) {
            return false;
        }

        long startTime = System.currentTimeMillis();
        long endTime;
        boolean videoEnd = false;
        while (true) {
            endTime = System.currentTimeMillis();
            // 累计时间到了，不看了
            if (endTime - startTime > videoReadTimeInSecondsLeft * 1000) {
                break;
            }

            // 视频结束了
            AccessibilityNodeInfo root = accessibilityService.getRootInActiveWindow();
            List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByText("重新播放");
            if (nodes.size() > 0) {
                videoEnd = true;
                break;
            }

            sleep(1000);
        }

        if (!videoEnd) {
            // 视频还没结束，快进
            while (true) {
                AccessibilityNodeInfo root = accessibilityService.getRootInActiveWindow();
                List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByText("重新播放");
                if (nodes.size() > 0) {
                    break;
                }

                // 没结束？快进
                int x = r.nextInt(10) + 200;
                int y = r.nextInt(50) + 400;
                GestureUtil.scrollRight(accessibilityService, x, y, 400);

                sleep(r.nextInt(1000) + 1000);
            }
        }

        endTime = System.currentTimeMillis();
        videoReadTimeInSecondsLeft -= (endTime - startTime) / 1000;

        return true;
    }

    @Override
    int getRequiredEntryCnt() {
        return 6;
    }

}
