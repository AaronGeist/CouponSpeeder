package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

import static android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_TAKE_SCREENSHOT;

public class VideoReader extends BaseLearner {

    private int videoReadTimeInSeconds = 185;  //视频观看秒数

    public VideoReader(AccessibilityService service) {
        super(service);
    }

    @Override
    boolean processEntry(String title) {
        // 第一个很长，跳过
        if (title.contains(":")) {
            return false;
        }

        int cnt = 0;
        while (true) {
            AccessibilityNodeInfo root = accessibilityService.getRootInActiveWindow();
            List<AccessibilityNodeInfo> nodes = root.findAccessibilityNodeInfosByText("重新播放");
            if (nodes.size() > 0) {
                break;
            }

            sleep(1000);
            cnt++;

            // 时间太长了，跳过
            if (cnt > videoReadTimeInSeconds) {
                return false;
            }
        }

        // 看完了，时间不够，继续等
        while (cnt < videoReadTimeInSeconds) {
            sleep(1000);
            cnt++;
        }

        return true;
    }

    @Override
    int getRequiredEntryCnt() {
        return 6;
    }

}
