package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.util.Log;

import com.shakazxx.couponspeeder.core.util.CommonUtil;
import com.shakazxx.couponspeeder.core.util.GestureUtil;

import static com.shakazxx.couponspeeder.core.util.CommonUtil.sleep;

public class VideoReader extends BaseLearner {

    private final String TAG = getClass().getSimpleName();

    public static final int DEFAULT_WATCH_CNT = 8;
    public static final int DEFAULT_OVERALL_TIME = 60 * 6;

    private int videoReadTimeInSecondsLeft;  //视频观看秒数  180
    private int videoNum;
    private static final int MAX_WAIT_CNT = 30;

    public VideoReader(AccessibilityService service, Bundle bundle) {
        super(service, bundle);

        if (bundle == null) {
            bundle = new Bundle();
        }

        videoNum = bundle.getInt("video_num", DEFAULT_WATCH_CNT);
        videoReadTimeInSecondsLeft = bundle.getInt("video_time", DEFAULT_OVERALL_TIME);
        enable = bundle.getBoolean("enable_video", true);

        Log.d(TAG, "video_num: " + videoNum);
        Log.d(TAG, "video_time: " + videoReadTimeInSecondsLeft);
    }

    @Override
    boolean processEntry(String title) {
        Log.d(TAG, "Processing, " + videoReadTimeInSecondsLeft + " seconds left");
        long startTime = System.currentTimeMillis();
        long endTime;
        boolean isVideoEnd = false;
        while (!pending) {
            endTime = System.currentTimeMillis();
            // 累计时间到了，不看了
            if (endTime - startTime > videoReadTimeInSecondsLeft * 1000) {
                Log.d(TAG, "Congrs, Times up!");
                break;
            }

            Log.d(TAG, (endTime - startTime) / 1000 + "ms elapsed...not finished yet");
            // 视频结束了
            if (CommonUtil.findFirstNodeByText(accessibilityService, null, "重新播放") != null) {
                isVideoEnd = true;
                break;
            }

            sleep(1000);
            GestureUtil.click(accessibilityService, getWidth() / 2, getHeight() / 2, 10);
        }

        // 视频还没结束，快进
        if (!isVideoEnd) {
            // 点开进度条
            Log.d(TAG, "快进！");
            GestureUtil.click(accessibilityService, getWidth() / 2, 300, 500);
            // 直接点到最后
            GestureUtil.click(accessibilityService, getWidth() - 270, 675, 500);

            // 2秒检测一次，等待自动放完，最多等60秒
            int waitCnt = 0;
            while (!pending && waitCnt <= MAX_WAIT_CNT) {
                if (CommonUtil.findFirstNodeByText(accessibilityService, null, "重新播放") != null) {
                    isVideoEnd = true;
                    break;
                }

                Log.d(TAG, "耐心等待");
                sleep(2000);
                waitCnt++;
            }
        }

        endTime = System.currentTimeMillis();
        videoReadTimeInSecondsLeft -= (endTime - startTime) / 1000;

        return isVideoEnd;
    }

    @Override
    int getRequiredEntryCnt() {
        return videoNum;
    }

    @Override
    int expectScoreIncr() {
        return 12;
    }

}
