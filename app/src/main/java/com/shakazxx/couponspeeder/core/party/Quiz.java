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

        singleQuiz();
        twoPersonQuiz();
//        fourPersonQuiz();

        CommonUtil.globalBack(accessibilityService, 1000);
        CommonUtil.globalBack(accessibilityService, 1000);

        enable = false;
    }

    private void twoPersonQuiz() {
        // go to inner page and start
        GestureUtil.click(accessibilityService, getWidth() - 100, getHeight() - 800, 2000);
        GestureUtil.click(accessibilityService, getWidth() - 300, getHeight() / 2, 8000);

        String lastQuestion = "";
        for (int i = 0; i < 10; i++) {
            Map<String, AccessibilityNodeInfo> allTextNodes = null;

            // wait for full quiz, which should always has "A blablabla" and "B blablabla"
            boolean notFound = true;
            for (int j = 0; j < 10 && notFound; j++) {
                Log.d(TAG, "twoPersonQuiz: waiting for question, try " + j + " time");
                CommonUtil.sleep(1000);
                allTextNodes = CommonUtil.findAllText(accessibilityService, null);
                String question = CommonUtil.getLongest(new ArrayList<>(allTextNodes.keySet()));
                // wait until new question comes
                if (!question.equals(lastQuestion)) {
                    // wait until answer list shows
                    for (String text : allTextNodes.keySet()) {
                        if (text.contains("A. ")) {
                            Log.d(TAG, "twoPersonQuiz: find new question: " + question + " -> " + text);
                            lastQuestion = question;
                            notFound = false;
                            break;
                        }
                    }
                }
            }

            List<String> keywords = new ArrayList<>(allTextNodes.keySet()).stream().map(item -> item.replaceAll("[`\\\\~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%…&*（）——+|{}《》【】‘；：”“’。，、？]", "")).filter(item -> item.length() > 4).collect(Collectors.toList());

            // remove index
            keywords = keywords.stream().map(item -> {
                int length = item.length();
                if (length > 2) {
                    int pos = item.indexOf(' ');
                    return item.substring(pos + 1, length);
                } else {
                    return item;
                }
            }).collect(Collectors.toList());
            for (String kw : keywords) {
                Log.d(TAG, "keyword: " + kw);
            }
            String answer = answerUtil.find(keywords);
            Log.d(TAG, "answer: " + answer);

            AccessibilityNodeInfo answerNode = null;

            if (answer != null) {
                List<String> texts = allTextNodes.keySet().stream().filter(key -> key.contains(answer)).collect(Collectors.toList());
                for (String text : texts) {
                    answerNode = allTextNodes.get(text);
                    // might get title, so need to be clickable
                    if (answerNode.isClickable()) {
                        break;
                    }
                }
            }

            if (answerNode == null) {
                if (keywords.stream().filter(item -> item.contains("正确数总题数")).collect(Collectors.toList()).size() > 0) {
                    Log.d(TAG, "twoPersonQuiz: finished!");
                    break;
                }

                Log.d(TAG, "twoPersonQuiz: no answer is found, choose a random one");
                for (String text : allTextNodes.keySet()) {
                    if (text.length() > 2 && !text.equals(lastQuestion)) {
                        answerNode = allTextNodes.get(text);
                    }
                }
            }

            if (answerNode != null) {
                CommonUtil.click(answerNode, 100);
            } else {
                Log.d(TAG, "twoPersonQuiz: something wrong here");
            }

            CommonUtil.sleep(2000);
        }

        // start quit
        Log.d(TAG, "twoPersonQuiz: quit");
        CommonUtil.globalBack(accessibilityService, 3000);

        for (int i = 0; i < 10; i++) {
            AccessibilityNodeInfo node = CommonUtil.findFirstNodeByText(accessibilityService, null, "随机匹配");
            if (node != null) {
                break;
            } else {
                CommonUtil.sleep(1000);
            }
        }
        CommonUtil.globalBack(accessibilityService, 3000);
        GestureUtil.click(accessibilityService, 300, getHeight() / 2 + 100, 1000); // click quit button
    }

    private void fourPersonQuiz() {
        // go to inner page and start
        GestureUtil.click(accessibilityService, 300, getHeight() - 800, 1000);
        GestureUtil.click(accessibilityService, 300, getHeight() - 300, 8000);

        String lastQuestion = "";
        for (int i = 0; i < 10; i++) {
            Map<String, AccessibilityNodeInfo> allTextNodes = null;

            // wait for full quiz, which should always has "A blablabla" and "B blablabla"
            boolean notFound = true;
            for (int j = 0; j < 15 && notFound; j++) {
                GestureUtil.click(accessibilityService, 100, getHeight() - 10, 10);
                Log.d(TAG, "fourPersonQuiz: try " + j + " time");
                CommonUtil.sleep(1000);
                allTextNodes = CommonUtil.findAllText(accessibilityService, null);
                String question = CommonUtil.getLongest(new ArrayList<>(allTextNodes.keySet()));
                Log.d(TAG, "fourPersonQuiz: question " + question);
                if (!question.equals(lastQuestion)) {
                    for (String text : allTextNodes.keySet()) {
                        Log.d(TAG, "fourPersonQuiz: text " + text);
                        if (text.contains("A. ")) {
                            Log.d(TAG, "fourPersonQuiz: new quiz " + question + " -> " + text);
                            lastQuestion = question;
                            notFound = false;
                            break;
                        }
                    }
                }
            }

            Log.d(TAG, ">>>>>>>> start new quiz: " + allTextNodes.size());

            List<String> keywords = new ArrayList<>(allTextNodes.keySet()).stream().map(item -> item.replaceAll("[`\\\\~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%…&*（）——+|{}《》【】‘；：”“’。，、？]", "")).filter(item -> item.length() > 4).collect(Collectors.toList());

            // remove index
            keywords = keywords.stream().map(item -> {
                int length = item.length();
                if (length > 2) {
                    int pos = item.indexOf(' ');
                    return item.substring(pos + 1, length);
                } else {
                    return item;
                }
            }).collect(Collectors.toList());
            for (String kw : keywords) {
                Log.d(TAG, "keyword: " + kw);
            }
            String answer = answerUtil.find(keywords);
            Log.d(TAG, "answer: " + answer);

            AccessibilityNodeInfo answerNode = null;

            if (answer != null) {
                List<String> texts = allTextNodes.keySet().stream().filter(key -> key.contains(answer)).collect(Collectors.toList());
                for (String text : texts) {
                    answerNode = allTextNodes.get(text);
                    // might get title, so need to be clickable
                    if (answerNode.isClickable()) {
                        break;
                    }
                }
            }

            if (answerNode != null) {
                CommonUtil.click(answerNode, 100);
            } else {
                GestureUtil.click(accessibilityService, getWidth() / 2, getHeight() / 2, 1000);

                // game ends
                if (keywords.stream().filter(item -> item.contains("正确数总题数")).collect(Collectors.toList()).size() > 0) {
                    break;
                }
            }

            CommonUtil.sleep(3000);
        }

        CommonUtil.globalBack(accessibilityService, 1000);
        CommonUtil.click(CommonUtil.findFirstNodeByText(accessibilityService, null, "退出"), 1000);
    }

    private void singleQuiz() {
        GestureUtil.click(accessibilityService, getWidth() - 100, getHeight() - 300, 5000);

        // retry to avoid entering page too fast
        for (int j = 0; j < 10; j++) {
            Map<String, AccessibilityNodeInfo> allTextNodes = CommonUtil.findAllText(accessibilityService, null);
            if (allTextNodes.size() > 0) {
                break;
            } else {
                CommonUtil.sleep(1000);
            }
        }

        String lastQuestion = "";
        for (int i = 0; i < 6; i++) {
            Map<String, AccessibilityNodeInfo> allTextNodes = null;

            for (int j = 0; j < 10; j++) {
                Log.d(TAG, "singleQuiz: try " + j + " time");
                CommonUtil.sleep(1000);
                allTextNodes = CommonUtil.findAllText(accessibilityService, null);
                String question = CommonUtil.getLongest(new ArrayList<>(allTextNodes.keySet()));
                if (!question.equals(lastQuestion)) {
                    Log.d(TAG, "singleQuiz: new quiz " + question);
                    lastQuestion = question;
                    break;
                }
            }

            Log.d(TAG, ">>>>>>>> findText: " + allTextNodes.size());

            List<String> keywords = new ArrayList<>(allTextNodes.keySet()).stream().map(item -> item.replaceAll("[`\\\\~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%…&*（）——+|{}《》【】‘；：”“’。，、？]", "")).filter(item -> item.length() > 0).collect(Collectors.toList());

            for (String kw : keywords) {
                Log.d(TAG, "keyword: " + kw);
            }
            String answer = answerUtil.find(keywords);
            Log.d(TAG, "answer: " + answer);

            AccessibilityNodeInfo answerNode = null;
            if (i == 5) {
                // choose wrong answer
                for (String text : allTextNodes.keySet()) {
                    if (!answer.equals(text) && !"\uE6F8".equals(text) && !text.contains("出题") && !text.contains("推荐") && !text.equals("")) {
                        answerNode = allTextNodes.get(text);
                        Log.d(TAG, "singleQuiz: choose wrong answer" + text);
                        break;
                    }
                }
            } else {
                answerNode = allTextNodes.get(answer);
            }

            if (answerNode != null) {
                CommonUtil.click(answerNode, 100);
                CommonUtil.sleep(2000);
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
