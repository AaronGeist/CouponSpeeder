package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;

public class ScoreReader extends BaseLearner  {

    protected Bundle bundle;


    // constructor
    public ScoreReader(AccessibilityService service, Bundle bundle) {
        super(service, bundle);

        if (bundle == null) {
            bundle = new Bundle();
        }
    }

    public void getScore() {

    }

    @Override
    boolean processEntry(String title) {
        return false;
    }

    @Override
    int getRequiredEntryCnt() {
        return 0;
    }

    @Override
    int expectScoreIncr() {
        return 0;
    }
}
