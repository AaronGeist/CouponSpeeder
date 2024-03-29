package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.AnswerUtil;
import com.shakazxx.couponspeeder.core.util.CommonUtil;
import com.shakazxx.couponspeeder.core.util.FileUtil;
import com.shakazxx.couponspeeder.core.util.GestureUtil;
import com.shakazxx.couponspeeder.core.util.TessUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Quiz extends BaseLearner {

    AnswerUtil answerUtil = new AnswerUtil();
    List<String> prefix = Arrays.asList("a", "b", "c", "d", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    boolean singleQuizEnable;
    boolean twoPersonQuizEnable;
    boolean fourPersonQuizEnable;

    public Quiz(AccessibilityService service, Bundle bundle) {
        super(service, bundle);
    }

    @Override
    public void processSingle(String keyword) {
        if (!enable) {
            return;
        }

        singleQuiz();
        twoPersonQuizV2();

        // 四人赛要执行两次
        fourPersonQuizV2();
        fourPersonQuizV2();

        CommonUtil.globalBack(accessibilityService, 1000);
        CommonUtil.globalBack(accessibilityService, 1000);

        enable = false;
    }

    @Override
    void loadConfiguration() {
        singleQuizEnable = bundle.getBoolean("enable_single_quiz", true);
        twoPersonQuizEnable = bundle.getBoolean("enable_two_person_quiz", true);
        fourPersonQuizEnable = bundle.getBoolean("enable_four_person_quiz", true);

        enable = singleQuizEnable || twoPersonQuizEnable || fourPersonQuizEnable;
    }

    private String takeScreenshot() {
        GestureUtil.miuiScreenShot(accessibilityService);
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        String name = new SimpleDateFormat("yyyy-MM-dd-HH").format(calendar.getTime());
        Log.d(TAG, "takeScreenshot: time " + name);

        String filePath = null;

        File dir = new File(FileUtil.getRootPath() + "/DCIM/Screenshots/");
        for (int i = 0; i < 100; i++) {
            CommonUtil.sleep(100);
            File[] files = dir.listFiles();
            for (File file : files) {
                Log.d(TAG, "takeScreenshot: file: " + file.getName());
                if (file.getName().contains("cn.xuexi.android") && file.getName().contains(name) && !file.getName().contains(".Screenshot")) {
                    filePath = file.getAbsolutePath();
                    return filePath;
                }
            }
        }

        return filePath;
    }

    private void twoPersonQuizV2() {
        if (!twoPersonQuizEnable) {
            return;
        }


        // go to inner page and start
        GestureUtil.click(accessibilityService, getWidth() - 100, getHeight() - 800, 2000);
        GestureUtil.click(accessibilityService, getWidth() - 300, getHeight() / 2, 10000);

        Random r = new Random();
        for (int i = 0; i < 20; i++) {
            // 随机点了，y 轴大概范围是 800 - 1500
            int x = getWidth() / 2 + r.nextInt(100);
            int y = 800 + r.nextInt(700);
            GestureUtil.click(accessibilityService, x, y, 1000);
            Log.d(TAG, "Random click: " + x + "-" + y);
        }

        // start quit
        Log.d(TAG, "twoPersonQuiz: quit");

        // 等5分钟，足够对手完成了吧
        for (int i = 0; i < 60; i++) {
            AccessibilityNodeInfo node = CommonUtil.findFirstNodeByText(accessibilityService, null, "继续挑战");
            if (node != null) {
                Log.d(TAG, "终于结束了");
                break;
            } else {
                Log.d(TAG, "还没结束吗？");
                CommonUtil.sleep(5000);
            }
        }
        CommonUtil.globalBack(accessibilityService, 3000);

        // 这里开始都找不到text，只能按位置点
        CommonUtil.globalBack(accessibilityService, 1000);
        GestureUtil.click(accessibilityService, 300, getHeight() / 2 + 200, 1000); // click quit button
    }

    private void twoPersonQuiz() {
        if (!twoPersonQuizEnable) {
            return;
        }

        // go to inner page and start
        GestureUtil.click(accessibilityService, getWidth() - 100, getHeight() - 800, 2000);
        GestureUtil.click(accessibilityService, getWidth() - 300, getHeight() / 2, 10000);

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
            List<String> answerData = answerUtil.find(keywords);

            AccessibilityNodeInfo answerNode = null;

            if (answerData != null) {
                String answer = answerData.get(1);
                Log.d(TAG, "answer: " + answer);

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

    private void fourPersonQuizV2() {
        if (!fourPersonQuizEnable) {
            return;
        }

        // go to inner page and start
        GestureUtil.click(accessibilityService, 300, getHeight() - 800, 1000);
        for (int i = 0; i < 10; i++) {
            AccessibilityNodeInfo node = CommonUtil.findFirstNodeByText(accessibilityService, null, "开始比赛");
            if (node != null) {
                CommonUtil.click(node, 13000);
                break;
            } else {
                CommonUtil.sleep(1000);
            }
        }

        Random r = new Random();
        for (int i = 0; i < 20; i++) {
            // 随机点了，y轴大概范围是 800 - 1500
            int x = getWidth() / 2 + r.nextInt(100);
            int y = 800 + r.nextInt(700);
            GestureUtil.click(accessibilityService, x, y, 1000);
            Log.d(TAG, "Random click: " + x + "-" + y);
        }

        // start quit
        Log.d(TAG, "fourPersonQuiz: quit");

        // 等2分钟，足够对手完成了吧
        for (int i = 0; i < 60; i++) {
            AccessibilityNodeInfo node = CommonUtil.findFirstNodeByText(accessibilityService, null, "继续挑战");
            if (node != null) {
                Log.d(TAG, "终于结束了！");
                break;
            } else {
                Log.d(TAG, "还没结束吗？");
                CommonUtil.sleep(2000);

            }
        }
        CommonUtil.globalBack(accessibilityService, 3000);
        CommonUtil.globalBack(accessibilityService, 3000);
    }

    private void fourPersonQuiz() {
        if (!twoPersonQuizEnable) {
            return;
        }

        TessUtil.init();
        cleanupSnapshotFolder();

        // go to inner page and start
        GestureUtil.click(accessibilityService, 300, getHeight() - 800, 1000);
//        GestureUtil.click(accessibilityService, 300, getHeight() - 300, 13000);

        for (int i = 0; i < 20; i++) {
            GestureUtil.click(accessibilityService, 300, getHeight() - 300, 13000);

            String imgPath = takeScreenshot();
            Log.d(TAG, "fourPersonQuiz: image " + imgPath);
            assert imgPath != null;
            Map<String, Rect> ocrTextResult = TessUtil.recognition(imgPath);

            // normalize text
            Map<String, Rect> normOcrTextResult = new HashMap<>();
            for (String text : ocrTextResult.keySet()) {
                Rect rect = ocrTextResult.get(text);
                if (text.trim().length() <= 1) {
                    continue;
                }

                text = normalizeText(text);
                normOcrTextResult.put(text, rect);
            }
            FileUtil.remove(imgPath);

            List<String> keywords = new ArrayList<>(normOcrTextResult.keySet());
            String question = CommonUtil.getLongest(keywords);
            Log.d(TAG, "fourPersonQuiz: question:" + question);

            for (String kw : keywords) {
                Log.d(TAG, "keyword: " + kw);
            }
            String answer = null;
            List<String> answerData = answerUtil.find(keywords);
            if (answerData != null) {
                answer = answerData.get(1);
            }

            Rect rect = null;
            if (answer != null) {
                answer = normalizeText(answer);
                Log.d(TAG, "normalized answer: " + answer);

                for (String ocrText : ocrTextResult.keySet()) {
                    if (ocrText.contains(answer) && !ocrText.equals(question)) {
                        // calculate pos
                        rect = ocrTextResult.get(ocrText);

                    }
                }
            }

            if (rect == null) {
                Log.d(TAG, "fourPersonQuiz: no answer, random pick");
                for (String ocrText : ocrTextResult.keySet()) {
                    if (!ocrText.startsWith("出题") && !ocrText.equals(question)) {
                        // calculate pos
                        rect = ocrTextResult.get(ocrText);
                    }
                }
            }

            Log.d(TAG, rect.left + "-" + rect.right + "-" + rect.top + "-" + rect.bottom);
            int x = (rect.left + rect.right) / 2;
            int y = 500 + (rect.top + rect.bottom) / 2;
            Log.d(TAG, "fourPersonQuiz: click: " + x + " - " + y);
//                        GestureUtil.click(accessibilityService, x, y, 10);

            CommonUtil.sleep(3000);

            CommonUtil.globalBack(accessibilityService, 2000);
            GestureUtil.click(accessibilityService, 300, getHeight() / 2 + 100, 1000); // click quit button
        }
        TessUtil.close();

        CommonUtil.globalBack(accessibilityService, 1000);
        CommonUtil.click(CommonUtil.findFirstNodeByText(accessibilityService, null, "退出"), 1000);
    }

    private void singleQuiz() {
        if (!singleQuizEnable) {
            return;
        }

        GestureUtil.click(accessibilityService, getWidth() - 100, getHeight() - 300, 5000);

        // enable retry
        for (int i = 0; i < 40; i++) {
            if (doSingleQuiz()) {
                break;
            }
        }

        CommonUtil.globalBack(accessibilityService, 1000);
    }

    private boolean doSingleQuiz() {
        if (!singleQuizEnable) {
            return true;
        }

        // 进入一个总题库
        GestureUtil.click(accessibilityService, getWidth() / 2, 360, 5000);

        // retry to avoid entering page too fast
        for (int j = 0; j < 10; j++) {
            Map<String, AccessibilityNodeInfo> allTextNodes = CommonUtil.findAllText(accessibilityService, null);
            if (allTextNodes != null &&  allTextNodes.size() > 0) {
                break;
            } else {
                CommonUtil.sleep(1000);
            }
        }

        String lastQuestion = "";
        for (int i = 0; i < 6; i++) {
            Map<String, AccessibilityNodeInfo> allTextNodes = null;

            for (int j = 0; j < 10; j++) {
                Log.d(TAG, "singleQuiz: try find new quiz, tried " + j + " time now");
                CommonUtil.sleep(1000);
                allTextNodes = CommonUtil.findAllText(accessibilityService, null);
                String question = CommonUtil.getLongest(new ArrayList<>(allTextNodes.keySet()));
                if (!question.equals(lastQuestion)) {
                    Log.d(TAG, "singleQuiz: find new quiz, question=" + question);
                    lastQuestion = question;
                    break;
                }
            }

            List<String> keywords = new ArrayList<>(allTextNodes.keySet()).stream().map(item -> item.replaceAll("[\\s`\\\\~!@#$%^&*()+={}':;,\\[\\].<>/?！￥…（）—《》【】‘；：”“’。，、？]", "")).filter(item -> item.length() >= 1).collect(Collectors.toList());

            for (String kw : keywords) {
                Log.d(TAG, "keyword: " + kw);
            }

            List<String> answerData = answerUtil.find(keywords);

            AccessibilityNodeInfo answerNode = null;

            if (answerData != null) {
                String question = answerData.get(0);
                String answer = answerData.get(1);
                Log.d(TAG, "answer: " + answer);

                // 找到答案了
                if (i < 5) {
                    // 选一个正确的
                    answerNode = allTextNodes.get(answer);
                } else {
                    // 选一个错误的
                    List<String> questions = new ArrayList<>(Arrays.asList(question.split("\\|")));
                    questions.remove(0);
                    questions.remove(answer);
                    String wrongAnswer = questions.get(0);
                    Log.d(TAG, "now 6th round, choose a wrong answer: " + answer);
                    answerNode = allTextNodes.get(wrongAnswer);
                }

                if (answerNode != null) {
                    CommonUtil.click(answerNode, 100);
                    CommonUtil.sleep(2000);
                } else {
                    // Cannot find answer, maybe there's no answer for current quiz, might need to retry
                    Log.d(TAG, "singleQuiz: cannot find answer, quit and retry");
                    // quit
                    CommonUtil.globalBack(accessibilityService, 1000);
                    CommonUtil.click(CommonUtil.findFirstNodeByText(accessibilityService, null, "退出"), 1000);
                    return false;
                }
            } else {
                // 没找到答案
                if (i < 5) {
                    // 退出重来
                    Log.d(TAG, "singleQuiz: cannot find answer, quit and retry");
                    // quit
                    CommonUtil.globalBack(accessibilityService, 1000);
                    CommonUtil.click(CommonUtil.findFirstNodeByText(accessibilityService, null, "退出"), 1000);
                    return false;
                } else {
                    // 等时间结束
                }
            }
        }

        // wait for close
        for (int i = 0; i < 10; i++) {
            Log.d(TAG, "waiting for close, " + i);
            if (CommonUtil.findFirstNodeByText(accessibilityService, null, "挑战结束") != null) {
                CommonUtil.globalBack(accessibilityService, 1000);
                CommonUtil.globalBack(accessibilityService, 1000);
                break;
            }
            CommonUtil.sleep(5000);
        }

        return true;
    }

    private String normalizeText(String text) {
        text = text.replaceAll("[ `\\\\~!@#$%^&*()+={}':;,\\[\\].<>/?！￥…（）—《》【】‘；：”“’。，、？]", "");

        // remove prefix and space
        String firstChar = text.substring(0, 1).toLowerCase(Locale.ROOT);
        if (prefix.contains(firstChar)) {
            text = text.substring(1).trim();
        }
        text = text.trim();

        return text;
    }

    private void cleanupSnapshotFolder() {
        File dir = new File(FileUtil.getRootPath() + "/DCIM/Screenshots/");

        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.getName().contains("cn.xuexi.android")) {
                file.delete();
            }
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
