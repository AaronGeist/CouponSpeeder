package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.CommonUtil;
import com.shakazxx.couponspeeder.core.util.GestureUtil;

import java.util.List;

import static com.shakazxx.couponspeeder.core.util.CommonUtil.sleep;

public class ArticleReader extends BaseLearner {

    private final String TAG = getClass().getSimpleName();

    public static final int DEFAULT_TIME_IN_SECOND = 130;  //等待时间  130
    public static final int DEFAULT_READ_ARTICLE_NUM = 8;
    public static final int REQUIRED_SHARE_CNT = 4; //分享次数
    public static final int REQUIRED_COMMENT_CNT = 2; // 评论次数
    public static final int MAX_SCROLL_DOWN_CNT = 30; // 下滑最大次数

    private int articleNum;
    private int articleTime;
    private int shareCnt = 0;
    private int commentCnt = 0;

    public ArticleReader(AccessibilityService service, Bundle bundle) {
        super(service, bundle);

        if (bundle == null) {
            bundle = new Bundle();
        }

        articleNum = bundle.getInt("article_num", DEFAULT_READ_ARTICLE_NUM);
        articleTime = bundle.getInt("article_time", DEFAULT_TIME_IN_SECOND);
        enable = bundle.getBoolean("enable_article", true);

        Log.d(TAG, "article_num: " + articleNum);
        Log.d(TAG, "article_time: " + articleTime);
    }

    @Override
    boolean processEntry(String title) {
        preProcessHook();

        boolean bottom = false;
        long startTime = System.currentTimeMillis();
        long endTime;

        int scrollDownCnt = 0;
        while (!pending) {
            endTime = System.currentTimeMillis();
            // 累计时间到了，不看了
            if (endTime - startTime > articleTime * 1000) {
                Log.d(TAG, "阅读时间满足条件");
                break;
            }

            if (!bottom) {
                GestureUtil.scrollDown(accessibilityService, 500, 1500, r.nextInt(200) + 1000);
                scrollDownCnt++;
                Log.d(TAG, "下滑+1");

                if ((scrollDownCnt > MAX_SCROLL_DOWN_CNT) ||
                        (CommonUtil.findFirstNodeByText(accessibilityService, null, "已显示全部观点") != null)) {
                    Log.d(TAG, "到底了");

                    bottom = true;
                }
            }

            Log.d(TAG, "等待时间：" + (endTime - startTime) / 1000);

            sleep(r.nextInt(200) + 500);
        }

        return true;
    }

    private void preProcessHook() {
        List<AccessibilityNodeInfo> elements = CommonUtil.findAllByText(accessibilityService, null, "欢迎发表你的观点");
        if (elements.size() > 0) {
            // 收藏
            GestureUtil.click(accessibilityService, getWidth() - 200, getHeight(), 1000);

            // 分享
            if (shareCnt < REQUIRED_SHARE_CNT) {
                shareCnt++;
                GestureUtil.click(accessibilityService, getWidth() - 100, getHeight(), 1000);
                AccessibilityNodeInfo newRoot = accessibilityService.getRootInActiveWindow();
                if (newRoot == null) {
                    return;
                }
                List<AccessibilityNodeInfo> newElems = newRoot.findAccessibilityNodeInfosByText("分享到学习强国");
                if (newElems.size() > 0) {
                    CommonUtil.click(newElems.get(0), 1000);
                    CommonUtil.globalBack(accessibilityService, 2000);
                }
            }

            if (commentCnt < REQUIRED_COMMENT_CNT) {
                commentCnt++;
                GestureUtil.click(accessibilityService, 200, getHeight() , 1000);
                AccessibilityNodeInfo node = CommonUtil.findFirstNodeByText(accessibilityService, null, "好观点将会被优先展示");
                if (node != null) {
                    CommonUtil.inputText(node, CommentDict.pick());
                    CommonUtil.click(CommonUtil.findFirstNodeByText(accessibilityService, null, "发布"), 1000);
                }
            }
        }
    }

    @Override
    int getRequiredEntryCnt() {
        return articleNum;
    }

    @Override
    int expectScoreIncr() {
        return 16;
    }
}
