package com.shakazxx.couponspeeder.core.party;

import static java.lang.Thread.sleep;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PartyStudent {

    private final ArticleReader articleReader;
    private final VideoReader videoReader;
    private final LocalChannel localChannel;
    private final Quiz quiz;
    private final Subscription subscription;
    private final Login login;
    private final ScoreReader scoreReader;
    private final Bundle bundle;
    private boolean enable = true;
    private boolean isAllDone = false;
    private String resultLog = "";

    private final static int MAX_RETRY_TIMES = 10;

    public PartyStudent(AccessibilityService service, Bundle bundle) {
        this.bundle = bundle;
        login = new Login(service);
        articleReader = new ArticleReader(service, bundle);
        videoReader = new VideoReader(service, bundle);
        localChannel = new LocalChannel(service, bundle);
        quiz = new Quiz(service, bundle);
        subscription = new Subscription(service, bundle);
        scoreReader = new ScoreReader(service, bundle);
    }

    public void learn() {
        if (!enable) {
            return;
        }

        if (!login()) {
            return;
        }

        // 如果分数不够，重新增量执行
        for (int i = 0; i < MAX_RETRY_TIMES; i++) {
            // wait until score is loaded
            while (!getScore()) {
                try {
                    sleep(2000);
                    Log.d(this.getClass().getSimpleName(), "Wait for score");
                } catch (Exception e) {
                    //
                }
            }

            if (bundle.getBoolean(ScoreReader.ALL_DONE_KEY)) {
                Log.d(this.getClass().getSimpleName(), "Congratulations! Mission completed!");
                enable = false;
                isAllDone = true;
                return;
            }

            readArticle();
            watchVideo();
            localChannel();
            challengeQuiz();
            // subscribe();
        }

        // fail to complete mission, send message
        return;
    }

    public void stop() {
        articleReader.stop();
        videoReader.stop();
        localChannel.stop();
        quiz.stop();
    }

    public boolean isAllDone() {
        return isAllDone;
    }

    public String getResultLog() {
        return resultLog;
    }

    private boolean login() {
        return login.process();
    }

    private boolean getScore() {
        if (scoreReader.findEntrance("我的")) {
            if (scoreReader.findEntrance("学习积分", 220, 800)) {
                scoreReader.processSingle("");
                resultLog = scoreReader.printLog();
                return true;
            }
        }
        return false;
    }

    private void readArticle() {
        articleReader.loadConfiguration();

        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("yyyy").format(new Date());
        if (articleReader.findEntrance("工作")) {
            if (articleReader.findEntrance("推荐")) {
                articleReader.processSingle(date.substring(0, 3));
            }
        }
    }

    private void challengeQuiz() {
        quiz.loadConfiguration();
        if (quiz.findEntrance("我的")) {
            if (quiz.findEntrance("我要答题", 550, 800)) {
                quiz.processSingle("");
            }
        }
    }

    private void subscribe() {
        subscription.loadConfiguration();
        if (subscription.findEntrance("我的")) {
            if (subscription.findEntrance("订阅")) {
                subscription.processSingle("");
            }
        }
    }

    private void watchVideo() {
        videoReader.loadConfiguration();

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("yyyy").format(calendar.getTime());
        if (videoReader.findEntrance("电视台")) {
            if (videoReader.findEntrance("联播频道")) {
                videoReader.processSingle(date.substring(0, 3));
            }
        }
    }

    private void localChannel() {
        localChannel.loadConfiguration();
        if (localChannel.findEntrance("工作")) {
            if (localChannel.findEntrance("上海")) {
                localChannel.processSingle("");
            }
        }
    }
}
