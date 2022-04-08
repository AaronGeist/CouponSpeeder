package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.CommonUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScoreReader extends BaseLearner {

    public static final String ALL_DONE_KEY = "all_done";

    private static final String TRG_SCORE = "trgScore";
    private static final String BUNDLE_KEY = "bundleKey";
    private static final String TYPE = "type";
    private static final String MAX_NUM = "maxNum";

    Map<String, Map<String, Object>> scoreConfig = new HashMap<>();

    // constructor
    public ScoreReader(AccessibilityService service, Bundle bundle) {
        super(service, bundle);

        Map<String, Object> innerConfig = new HashMap<>();
        innerConfig.put(TRG_SCORE, 1);
        innerConfig.put(BUNDLE_KEY, "");
        innerConfig.put(TYPE, "NA");
        scoreConfig.put("登录", innerConfig);

        innerConfig = new HashMap<>();
        innerConfig.put(TRG_SCORE, 12);
        innerConfig.put(BUNDLE_KEY, "article_num");
        innerConfig.put(MAX_NUM, 6);
        innerConfig.put(TYPE, "number");
        scoreConfig.put("我要选读文章", innerConfig);

        innerConfig = new HashMap<>();
        innerConfig.put(TRG_SCORE, 6);
        innerConfig.put(BUNDLE_KEY, "video_num");
        innerConfig.put(MAX_NUM, 6);
        innerConfig.put(TYPE, "number");
        scoreConfig.put("视听学习", innerConfig);

        innerConfig = new HashMap<>();
        innerConfig.put(TRG_SCORE, 6);
        innerConfig.put(BUNDLE_KEY, "video_minute");
        innerConfig.put(MAX_NUM, 6);
        innerConfig.put(TYPE, "number");
        scoreConfig.put("视听学习时长", innerConfig);

        innerConfig = new HashMap<>();
        innerConfig.put(TRG_SCORE, 6);
        innerConfig.put(BUNDLE_KEY, "enable_single_quiz");
        innerConfig.put(TYPE, "toggle");
        scoreConfig.put("挑战答题", innerConfig);

        innerConfig = new HashMap<>();
        innerConfig.put(TRG_SCORE, 2);
        innerConfig.put(BUNDLE_KEY, "enable_four_person_quiz");
        innerConfig.put(TYPE, "toggle");
        scoreConfig.put("四人赛", innerConfig);

        innerConfig = new HashMap<>();
        innerConfig.put(TRG_SCORE, 1);
        innerConfig.put(BUNDLE_KEY, "enable_two_person_quiz");
        innerConfig.put(TYPE, "toggle");
        scoreConfig.put("双人对战", innerConfig);

        innerConfig = new HashMap<>();
        innerConfig.put(TRG_SCORE, 1);
        innerConfig.put(BUNDLE_KEY, "article_share_num");
        innerConfig.put(TYPE, "number");
        scoreConfig.put("分享", innerConfig);

        innerConfig = new HashMap<>();
        innerConfig.put(TRG_SCORE, 1);
        innerConfig.put(BUNDLE_KEY, "article_comment_num");
        innerConfig.put(TYPE, "number");
        scoreConfig.put("发表观点", innerConfig);

        innerConfig = new HashMap<>();
        innerConfig.put(TRG_SCORE, 1);
        innerConfig.put(BUNDLE_KEY, "enable_tv");
        innerConfig.put(TYPE, "toggle");
        scoreConfig.put("本地频道", innerConfig);
    }

    @Override
    boolean processEntry(String title) {
        return false;
    }

    @Override
    public void processSingle(String keyword) {
        Pattern pattern = Pattern.compile("已获([0-9]{1,2})分/每日上限([0-9]{1,2})分");

        AccessibilityNodeInfo rootNode = CommonUtil.findFirstNodeByText(accessibilityService, "积分规则", 30000, 1000).getParent().getChild(2);
        int cnt = rootNode.getChildCount();
        boolean isAllDone = true;
        for (int i = 0; i < cnt; i++) {
            String type = (String) rootNode.getChild(i).getChild(0).getChild(0).getText();
            String text = (String) rootNode.getChild(i).getChild(2).getText();
            if (text != null) {
                Matcher m = pattern.matcher(text);
                if (m.matches()) {
                    int currScore = Integer.valueOf(m.group(1));
                    if (scoreConfig.containsKey(type)) {
                        Map<String, Object> settings = scoreConfig.get(type);
                        int trgScore = (int) settings.get(TRG_SCORE);
                        boolean isReachTargetScore = currScore >= trgScore;
                        Log.d(TAG, String.format("%s达标: %s [%d/%d]", (isReachTargetScore ? "已" : "未"), type, currScore, trgScore));

                        if ("number".equalsIgnoreCase((String) settings.get(TYPE))) {
                            // 分值类
                            String bundleKey = (String) settings.get(BUNDLE_KEY);
                            if (isReachTargetScore) {
                                bundle.putInt(bundleKey, 0);
                            } else if (bundle.containsKey(bundleKey)) {
                                if (settings.containsKey(MAX_NUM)) {
                                    bundle.putInt(bundleKey, Math.min((Integer) settings.get(MAX_NUM), trgScore - currScore));
                                } else {
                                    // 使用默认值
                                    bundle.remove(bundleKey);
                                }
                            }
                        } else {
                            // 开关类
                            bundle.putBoolean((String) settings.get(BUNDLE_KEY), !isReachTargetScore);
                        }

                        if (!isReachTargetScore) {
                            isAllDone = false;
                        }
                    }
                }
            }
        }

        bundle.putBoolean(ALL_DONE_KEY, isAllDone);
        Log.d(TAG, "全部达标=" + isAllDone);

        // go back to home page
        CommonUtil.globalBack(accessibilityService, 1000);
        CommonUtil.globalBack(accessibilityService, 1000);
    }

    @Override
    void loadConfiguration() {

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
