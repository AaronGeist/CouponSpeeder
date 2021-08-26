package com.shakazxx.couponspeeder.core.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
                key = key.replaceAll("[`\\\\~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%…&*（）——+|{}《》【】‘；：”“’。，、？]", "");
                answers.put(key, value);
                answerKeys.add(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String find(List<String> keywords) {
        String res = null;

        List<String> results= new ArrayList<>(answerKeys);
        for (final String keyword : keywords) {
            List<String> tempRes = results.stream().filter(item -> item.contains(keyword)).collect(Collectors.toList());
            if (tempRes.size() == 0) {
                continue;
            }
            if (tempRes.size() == 1) {
                return answers.get(tempRes.get(0));
            }
            results = tempRes;
        }

        return res;
    }
}

