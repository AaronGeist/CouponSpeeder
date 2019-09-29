package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;

public class VideoReader extends BaseLearner {

    private int videoReadTimeInSeconds = 190;  //视频观看秒数

    public VideoReader(AccessibilityService service) {
        super(service);
    }

    @Override
    void processEntry() {
        // 只要停在那里等着就可以了
        try {
            Thread.sleep(videoReadTimeInSeconds * 1000);
        } catch (InterruptedException e) {
            Log.d(TAG, "");
        }
    }

    @Override
    int getRequiredEntryCnt() {
        return 6;
    }

}
