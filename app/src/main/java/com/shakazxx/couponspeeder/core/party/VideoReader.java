package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.util.Log;

import com.shakazxx.couponspeeder.core.util.CommonUtil;
import com.shakazxx.couponspeeder.core.util.GestureUtil;

import static com.shakazxx.couponspeeder.core.util.CommonUtil.sleep;

public class VideoReader extends BaseLearner {

    private final String TAG = getClass().getSimpleName();

    public static final int DEFAULT_WATCH_CNT = 6;
    public static final int DEFAULT_OVERALL_MINUTES = 6;

    private int videoReadTimeInSecondsLeft;  //视频观看秒数  180
    private int trgVideoNum;
    private int trgVideoMin;

    private static final int MAX_WAIT_CNT = 30;

    public VideoReader(AccessibilityService service, Bundle bundle) {
        super(service, bundle);
    }

    @Override
    void loadConfiguration() {
        trgVideoNum = bundle.getInt("video_num", DEFAULT_WATCH_CNT);
        trgVideoMin = bundle.getInt("video_minute", DEFAULT_OVERALL_MINUTES);
        videoReadTimeInSecondsLeft = trgVideoMin * 80; // 额外多看一些时间

        enable = bundle.getBoolean("enable_video", true);
        Log.d(TAG, String.format("trgVideoNum %s\ntrgVideoMin %s\nvideoReadTimeInSecondsLeft %s\nenable %s", trgVideoNum, trgVideoMin, videoReadTimeInSecondsLeft, enable));
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
            if (endTime - startTime > videoReadTimeInSecondsLeft * 1000L) {
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
            int waitCnt = 0;

            // 如果waitCnt太少，表示视频观看时间太少，不计数，需要重新拉长时间看
            int round = 0;
            int step = 50;
            while (waitCnt <= 2 && round <= 5) {
                waitCnt = 0;

                if (round >= 4) {
                    step = 100;
                }

                // 点开进度条
                Log.d(TAG, "快进！round " + (round + 1));
                GestureUtil.click(accessibilityService, getWidth() / 2, 300, 500);
                // 直接点到最后
                GestureUtil.click(accessibilityService, 690 - round * step, 630, 500);

                // 2秒检测一次，等待自动放完，最多等60秒
                while (!pending && waitCnt <= MAX_WAIT_CNT) {
                    if (CommonUtil.findFirstNodeByText(accessibilityService, null, "重新播放") != null) {
                        isVideoEnd = true;
                        break;
                    }

                    Log.d(TAG, "耐心等待");
                    sleep(2000);
                    waitCnt++;
                }
                round++;
            }

            // 如果结束太快，重新看一下
        }

        endTime = System.currentTimeMillis();
        videoReadTimeInSecondsLeft -= (endTime - startTime) / 1000;

        return isVideoEnd;
    }

    @Override
    int getRequiredEntryCnt() {
        return trgVideoNum;
    }

    @Override
    int expectScoreIncr() {
        return 12;
    }

}
