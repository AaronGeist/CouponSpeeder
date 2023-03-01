package com.shakazxx.couponspeeder.core.party;

import static com.shakazxx.couponspeeder.core.util.CommonUtil.sleep;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.CommonUtil;

import java.util.HashMap;
import java.util.Map;

public class ScoreReader extends BaseLearner {

    public static final String ALL_DONE_KEY = "all_done";

    private static final String TRG_SCORE = "trgScore";
    private static final String BUNDLE_KEY = "bundleKey";
    private static final String TYPE = "type";
    private static final String MAX_NUM = "maxNum";

    Map<String, Map<String, Object>> scoreConfig = new HashMap<>();

    String logs = "";

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
        innerConfig.put(TRG_SCORE, 5);
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
        logs = "";
        int cnt = 0;
        AccessibilityNodeInfo rootNode = null;
        for (int i = 0; i < 10; i++) {
            rootNode = CommonUtil.findFirstNodeByText(accessibilityService, "积分规则", 30000, 1000).getParent().getChild(2);
            cnt = rootNode.getChildCount();

            // 没有加载到，等待
            if (cnt == 0) {
                Log.d(TAG, "等待积分加载");
                sleep(1000);
            } else {
                break;
            }
        }
        boolean isAllDone = true;
        for (int i = 0; i < cnt; i++) {
            String type = (String) rootNode.getChild(i).getChild(0).getText();
            int currScore = Integer.valueOf((String) rootNode.getChild(i).getChild(3).getChild(0).getText());

            if (scoreConfig.containsKey(type)) {
                Map<String, Object> settings = scoreConfig.get(type);
                int trgScore = (int) settings.get(TRG_SCORE);
                boolean isReachTargetScore = currScore >= trgScore;
                String log = String.format("%s达标: %s [%d/%d]", (isReachTargetScore ? "已" : "未"), type, currScore, trgScore);
                Log.d(TAG, log);

                logs += log + "\n";

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

        // 直接跳过读文章
        if (bundle.getInt("article_num") == 0) {
            bundle.putBoolean("enable_article", false);
        }

        // 直接跳过看视频
        if (bundle.getInt("video_num") == 0 && bundle.getInt("video_minute") == 0) {
            bundle.putBoolean("enable_video", false);

        }

        bundle.putBoolean(ALL_DONE_KEY, isAllDone);
        Log.d(TAG, "全部达标=" + isAllDone);

        // go back to home page
        CommonUtil.globalBack(accessibilityService, 1000);
        CommonUtil.globalBack(accessibilityService, 1000);
    }

    public String printLog() {
        return logs;
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
