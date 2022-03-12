package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.base.BaseAction;
import com.shakazxx.couponspeeder.core.util.CommonUtil;
import com.shakazxx.couponspeeder.core.util.GestureUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.shakazxx.couponspeeder.core.util.CommonUtil.sleep;

public abstract class BaseLearner extends BaseAction {

    protected final String TAG = getClass().getSimpleName();

    // do not scroll down forever, if no item found, return
    private static final int MAX_SCROLL_DOWN_CNT = 100;

    // local variables
    protected boolean enable = true;

    protected Bundle bundle;

    // application is not running, then pending
    protected boolean pending = false;

    protected HistoryRecord historyRecord = new HistoryRecord();

    // 最初的分数
    private int initScore = -1;

    private static final Pattern timePattern = Pattern.compile("[0-9]{2}:[0-9]{2}");

    private static final String VIDEO_CLASS_NAME = "android.widget.FrameLayout";

    // constructor
    public BaseLearner(AccessibilityService service, Bundle bundle) {
        super(service);
        this.bundle = bundle;
    }

    public boolean findEntrance(String keyword) {
        if (!enable) {
            return false;
        }

        boolean result = false;
        List<AccessibilityNodeInfo> entrances = CommonUtil.findAllByText(accessibilityService, null, keyword);
        for (AccessibilityNodeInfo entrance : entrances) {
            if ((entrance.getClassName().toString().contains("TextView") && entrance.getText().toString().equalsIgnoreCase(keyword)) ||
                    (entrance.getClassName().toString().contains("FrameLayout") && entrance.getContentDescription() != null
                            && entrance.getContentDescription().toString().equalsIgnoreCase(keyword))) {

                result = CommonUtil.click(entrance, 1000);
                // 往上找，找到可以点击的
                while (entrance != null && !entrance.isClickable()) {
                    entrance = entrance.getParent();
                }

                if (entrance != null) {
                    entrance.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    sleep(1000);
                    Log.d(TAG, "发现入口");
                    result = true;
                    break;
                }
            }

        }

        return result;
    }

    // return true if has finished processSingle entries
    public void processSingle(String keyword) {
        if (!enable) {
            return;
        }

        List<String> processedTitles = historyRecord.readData();

        int currentCnt = 0;
        int currScrollCnt = 0;
        List<String> readTitles = new ArrayList<>();

        int entryHash4PreviousScreen = 0;
        int sameEntryCnt = 0;
        while (currentCnt < getRequiredEntryCnt() && currScrollCnt < MAX_SCROLL_DOWN_CNT && !pending) {
            List<AccessibilityNodeInfo> entries = CommonUtil.findAllByText(accessibilityService, null, keyword);

            int currentEntryHash = entries.hashCode();
            if (currentEntryHash == entryHash4PreviousScreen) {
                // 下拉后没有新的内容，继续下拉
                sameEntryCnt++;
                if (sameEntryCnt >= 10) {
                    // 连续10次下拉都是一样的东西，加大下拉距离，加载新的
                    GestureUtil.scrollDown(accessibilityService, getHeight() / 2);
                    sleep(2000); // 更长距离，需要更多等候时间
                    sameEntryCnt = 0;
                    Log.d(TAG, "Same content, scroll down directly and more distance(" + getHeight() / 2 + ")");
                } else {
                    GestureUtil.scrollDown(accessibilityService, 400);
                    sleep(1000);
                    Log.d(TAG, "Same content, scroll down directly");
                }
                currScrollCnt++;
                Log.d(TAG, "Scrolling Status: " + currScrollCnt + "/" + MAX_SCROLL_DOWN_CNT);
                continue;
            }

            entryHash4PreviousScreen = currentEntryHash;

            for (AccessibilityNodeInfo entry : entries) {
                try {
                    // 屏幕外的部分不要
                    Rect rect = new Rect();
                    entry.getBoundsInScreen(rect);
                    if (rect.left < 0 || rect.right < 0) {
                        continue;
                    }

                    AccessibilityNodeInfo btn = entry.getParent();
                    String title = btn.getChild(0).getText().toString().trim();
                    String potentialTime = "";
                    String childClassName = "";
                    if (btn.getChildCount() > 3) {
                        // 如果是视频，下列属性会是特殊的值
                        potentialTime = btn.getChild(3).getText().toString().trim();
                        childClassName = btn.getChild(1).getClassName().toString();
                    }

                    boolean containsVideo = false;
                    if (timePattern.matcher(title).find()) {
                        // 标题拿到的是视频的时长
                        title = btn.getChild(1).getText().toString().trim();
                        Log.d(TAG, "修正标题：" + title);

                        containsVideo = true;
                    }

                    if (VIDEO_CLASS_NAME.equals(childClassName) || timePattern.matcher(potentialTime).find()) {
                        containsVideo = true;
                    }

                    if (processedTitles.contains(title) || readTitles.contains(title)) {
                        // 已经看过，跳过
                        continue;
                    }

                    // 读文章的时候不要看视频
                    if (containsVideo && skipVideo()) {
                        Log.d(TAG, "跳过视频：" + title);
                        continue;
                    }

                    // 发现不重复项
                    readTitles.add(title);
                    Log.d(TAG, ">>>>>>>>> 发现新内容：" + title + " >>>>>>>>>>>>>");

                    if (CommonUtil.click(btn, 1000)) {
                        if (processEntry(title)) {
                            // 记录数据
                            historyRecord.writeData(title);
                            currentCnt++;
                        }
                        CommonUtil.globalBack(accessibilityService, 1000);

                        int currentScore = getScore();
                        if (initScore == -1) {
                            initScore = currentScore;
                        }

                        Log.d(TAG, String.format("状态：新增积分=%d，新增计数=%d", (currentScore - initScore), currentCnt));
                        // 分数增长达到目标，可以提前结束
                        if (currentScore - initScore >= expectScoreIncr()) {
                            break;
                        }

                        if (currentCnt >= getRequiredEntryCnt()) {
                            break;
                        }
                    }

                } catch (Exception e) {
                }
            }
        }

        //学习结束
        enable = false;
    }

    private int getScore() {
        AccessibilityNodeInfo nodeInfo = CommonUtil.findFirstNodeByText(accessibilityService, null, "积分");
        if (nodeInfo == null) {
            return -1;
        }

        if (nodeInfo.getParent() != null && nodeInfo.getParent().getChildCount() >= 2) {
            nodeInfo = nodeInfo.getParent().getChild(1);
            return Integer.valueOf(nodeInfo.getText().toString());
        } else {
            return -1;
        }
    }

    public void stop() {
        this.pending = true;
    }

    public void disable() {
        this.enable = false;
    }

    // 进入单项后如何处理
    abstract boolean processEntry(String title);

    // 需要学习的数量
    abstract int getRequiredEntryCnt();

    // 预期每种类型增加的分数
    abstract int expectScoreIncr();

    // 是否要看视频，比如读文章就不看
    protected boolean skipVideo() {
        return false;
    }


}
