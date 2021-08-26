package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.AnswerUtil;
import com.shakazxx.couponspeeder.core.util.CommonUtil;
import com.shakazxx.couponspeeder.core.util.GestureUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Quiz extends BaseLearner {

    AnswerUtil answerUtil = new AnswerUtil();

    public Quiz(AccessibilityService service, Bundle bundle) {
        super(service, bundle);
    }

    @Override
    public void processSingle(String keyword) {
        if (!enable) {
            return;
        }

        GestureUtil.click(accessibilityService, getWidth() - 100, getHeight() - 300, 5000);

        for (int i = 0; i < 5; i++) {
            Map<String, AccessibilityNodeInfo> allTextNodes = new HashMap<>();

            // retry to avoid entering page too fast
            for (int j = 0; j < 10; j++) {
                allTextNodes = CommonUtil.findAllText(accessibilityService, null);
                if (allTextNodes.size() > 0) {
                    break;
                } else {
                    CommonUtil.sleep(1000);
                }
            }
            Log.d(TAG, ">>>>>>>> findText: " + allTextNodes.size());

            List<String> keywords = new ArrayList<>(allTextNodes.keySet()).stream().map(item -> item.replaceAll("[`\\\\~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%…&*（）——+|{}《》【】‘；：”“’。，、？]", "")).filter(item -> item.length() > 0).collect(Collectors.toList());

            for (String kw : keywords) {
                Log.d(TAG, "keyword: " + kw);
            }
            String answer = answerUtil.find(keywords);
            Log.d(TAG, "answer: " + answer);

            AccessibilityNodeInfo answerNode = allTextNodes.get(answer);

            if (answerNode != null) {
                CommonUtil.click(answerNode, 100);
                CommonUtil.sleep(5000);
            }
        }

        // wait for close
        for (int i = 0; i < 10; i++) {
            if (CommonUtil.findFirstNodeByText(accessibilityService, null, "挑战结束") != null) {
                CommonUtil.globalBack(accessibilityService, 1000);
                CommonUtil.globalBack(accessibilityService, 1000);
                break;
            }
            CommonUtil.sleep(5000);
        }

        enable = false;
    }

    @Override
    boolean processEntry(String title) {
        return false;
    }

    @Override
    int getRequiredEntryCnt() {
        return 1;
    }

    @Override
    int expectScoreIncr() {
        return 1;
    }
}
