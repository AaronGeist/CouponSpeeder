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
    public static final int DEFAULT_OVERALL_TIME = 180 * 6 + 30;

    private int videoReadTimeInSecondsLeft;  //视频观看秒数  180
    private int videoNum;
    private static final int MAX_SCROLL_CNT = 30;

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
        long startTime = System.currentTimeMillis();
        long endTime;
        boolean videoEnd = false;
        while (!pending) {
            endTime = System.currentTimeMillis();
            // 累计时间到了，不看了
            if (endTime - startTime > videoReadTimeInSecondsLeft * 1000) {
                break;
            }

            // 视频结束了
            if (CommonUtil.findFirstNodeByText(accessibilityService, null, "重新播放") != null) {
                videoEnd = true;
                break;
            }

            sleep(1000);
            GestureUtil.click(accessibilityService, getWidth() / 2, getHeight() / 2, 10);
        }

        int scrollCnt = 0;
        if (!videoEnd) {
            // 视频还没结束，快进
            while (!pending) {
                if (CommonUtil.findFirstNodeByText(accessibilityService, null, "重新播放") != null) {
                    break;
                }

                // 没结束？快进
                int x = r.nextInt(10) + 200;
                int y = r.nextInt(50) + 400;
                GestureUtil.scrollRight(accessibilityService, x, y, 400);

                scrollCnt++;
                if (scrollCnt > MAX_SCROLL_CNT) {
                    // 超过最大快进次数，可能不是视频而是文章，直接返回
                    return false;
                }

                sleep(r.nextInt(1000) + 1000);
            }

            // 回退一下自动播放到结尾
            GestureUtil.scrollLeft(accessibilityService, 1000, 500, 50);
            sleep(5000);

            // 等待自动放完
            while (!pending) {
                if (CommonUtil.findFirstNodeByText(accessibilityService, null, "重新播放") != null) {
                    break;
                }

                sleep(r.nextInt(1000) + 1000);
            }
        }

        endTime = System.currentTimeMillis();
        videoReadTimeInSecondsLeft -= (endTime - startTime) / 1000;

        return true;
    }

    @Override
    int getRequiredEntryCnt() {
        return videoNum;
    }

}
