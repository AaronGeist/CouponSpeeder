package com.shakazxx.couponspeeder.core.util;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnswerUtil {


    private Map<String, String> answers = new HashMap<>();
    private List<String> answerKeys = new ArrayList<>();

    public AnswerUtil() {
        try {
            String data = FileUtil.readAll(FileUtil.getRootPath() + "/Download/quiz.txt");

            JSONObject jsonObject = new JSONObject(data);
            Iterator<String> it = jsonObject.keys();
            while (it.hasNext()) {
                String key = it.next();
                String value = (String) jsonObject.get(key);
                key = key.replaceAll("[\\s`\\\\~!@#$%^&*()+={}':;,\\[\\].<>/?！￥…（）—《》【】‘；：”“’。，、？]", "");
                answers.put(key, value);
                answerKeys.add(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List<String> find(List<String> keywords) {
        List<String> candidates= new ArrayList<>(answerKeys);
        for (final String keyword : keywords) {
            final StringBuilder sb = new StringBuilder();
            if (keyword.length() > 10) {
                // 如果题目很长，可以截断
                sb.append(keyword.substring(0, 10));
            } else {
                // 短的一般是答案，答案前面带竖线
                sb.append("|").append(keyword);
            }

            final String searchValue = sb.toString();
            List<String> tempRes = candidates.stream().filter(item -> item.contains(searchValue)).collect(Collectors.toList());
            if (tempRes.size() == 0) {
                // 可能是来源，脏数据可以忽略
                continue;
            }
            if (tempRes.size() == 1) {
                return Arrays.asList(tempRes.get(0), answers.get(tempRes.get(0)));
            }
            candidates = tempRes;
        }

        // 有可能题库有重复，如果答案一样，就随便返回一个
        if (candidates.size() <= 3) {
            boolean isSameAnswer = true;
            String answer = answers.get(candidates.get(0));
            for (int i = 1; i < candidates.size(); i++) {
                if (!answers.get(candidates.get(i)).equalsIgnoreCase(answer)) {
                    isSameAnswer = false;
                    break;
                }
            }

            if (isSameAnswer) {
                return Arrays.asList(candidates.get(0), answer);
            }
        }

        Log.d("AnswerUtil", "Still have multiple candidates: ");
        for (String res : candidates) {
            Log.d("AnswerUtil", res);
        }

        return null;
    }

}

