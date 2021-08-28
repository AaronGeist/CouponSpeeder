package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.os.Bundle;

import com.shakazxx.couponspeeder.core.util.FileUtil;
import com.shakazxx.couponspeeder.core.util.TessUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PartyStudent {

    private ArticleReader articleReader;
    private VideoReader videoReader;
    private LocalChannel localChannel;
    private Quiz quiz;
    private Subscription subscription;
    private Login login;

    public PartyStudent(AccessibilityService service, Bundle bundle) {
        login = new Login(service);

        articleReader = new ArticleReader(service, bundle);
        videoReader = new VideoReader(service, bundle);
        localChannel = new LocalChannel(service, bundle);
        quiz = new Quiz(service, bundle);
        subscription = new Subscription(service, bundle);
    }

    public void learn() {
        login();
//        subscribe();
        readArticle();
        watchVideo();
        localChannel();
        challengeQuiz();
    }

    public void stop() {
        articleReader.stop();
        videoReader.stop();
        localChannel.stop();
        quiz.stop();
    }

    private void login() {
        login.process();
    }

    private void readArticle() {
        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("yyyy").format(new Date());
        if (articleReader.findEntrance("工作")) {
            if (articleReader.findEntrance("推荐")) {
                articleReader.processSingle(date);
            }
        }
    }

    private void challengeQuiz() {
        if (quiz.findEntrance("我的")) {
            if (quiz.findEntrance("我要答题")) {
                quiz.processSingle("");
            }
        }
    }

    private void subscribe() {
        if (subscription.findEntrance("我的")) {
            if (subscription.findEntrance("订阅")) {
                subscription.processSingle("");
            }
        }
    }

    private void watchVideo() {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("yyyy").format(calendar.getTime());
        if (videoReader.findEntrance("电视台")) {
            if (videoReader.findEntrance("联播频道")) {
                videoReader.processSingle(date);
            }
        }
    }

    private void localChannel() {
        if (localChannel.findEntrance("工作")) {
            if (localChannel.findEntrance("上海")) {
                localChannel.processSingle("");
            }
        }
    }
}
