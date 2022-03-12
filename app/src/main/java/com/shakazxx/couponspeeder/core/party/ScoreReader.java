package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.CommonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreReader extends BaseLearner {

    protected Bundle bundle;

    Map<String, Integer> targetScore = new HashMap<>();
    Map<String, String> scoreSetting = new HashMap<>();


    // constructor
    public ScoreReader(AccessibilityService service, Bundle bundle) {
        super(service, bundle);

        if (bundle == null) {
            bundle = new Bundle();
        }

        targetScore.put("我要选读文章", 12);
        targetScore.put("视听学习", 6);
        targetScore.put("视听学习时长", 6);
        targetScore.put("本地频道", 1);
        targetScore.put("分享", 1);
        targetScore.put("发表观点", 1);
        targetScore.put("挑战答题", 6);
        targetScore.put("双人对战", 1);
        targetScore.put("四人赛", 2);

        scoreSetting.put("我要选读文章", "enable_article");
        scoreSetting.put("视听学习", "enable_video");
        scoreSetting.put("本地频道", "enable_tv");
        scoreSetting.put("挑战答题", "enable_single_quiz");
        scoreSetting.put("双人对战", "enable_two_person_quiz");
        scoreSetting.put("四人赛", "enable_four_person_quiz");
    }

    @Override
    boolean processEntry(String title) {
        return false;
    }

    @Override
    public void processSingle(String keyword) {
        Pattern pattern = Pattern.compile("已获([0-9]{1,2})分/每日上限([0-9]{1,2})分");

        AccessibilityNodeInfo rootNode = CommonUtil.findFirstNodeByText(accessibilityService, null, "积分规则").getParent().getChild(2);
        int cnt = rootNode.getChildCount();
        for (int i = 0; i < cnt; i++) {
            String type = (String) rootNode.getChild(i).getChild(0).getChild(0).getText();
            String text = (String) rootNode.getChild(i).getChild(2).getText();
            if (text != null) {
                Log.d(TAG, "text " + text);
                Matcher m = pattern.matcher(text);
                if (m.matches()) {
                    int current = Integer.valueOf(m.group(1));
                    int target = Integer.valueOf(m.group(2));
                    Log.d(TAG, "type is " + type + " " + current + ":" + target);
                    if (targetScore.containsKey(type) && targetScore.get(type) > current) {
                        if (scoreSetting.containsKey(type)) {
                            bundle.putBoolean(scoreSetting.get(type), true);
                        }
                    }
                }
            }
        }
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
