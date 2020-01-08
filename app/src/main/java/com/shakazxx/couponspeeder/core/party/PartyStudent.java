package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PartyStudent {

    private ArticleReader articleReader;
    private VideoReader videoReader;
    private Login login;

    public PartyStudent(AccessibilityService service) {
        login = new Login(service);
        articleReader = new ArticleReader(service);
        videoReader = new VideoReader(service);
    }

    public void learn() {
        login();
        readArticle();
        watchVideo();
    }

    public void stop() {
        articleReader.stop();
        videoReader.stop();
    }

    private void login() {
        login.process();
    }

    private void readArticle() {
        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("yyyy").format(new Date());
        if (articleReader.findEntrance("学习")) {
            if (articleReader.findEntrance("推荐")) {
                articleReader.processSingle(date);
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
}
