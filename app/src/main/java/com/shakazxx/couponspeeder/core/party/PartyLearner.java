package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PartyLearner {

    private ArticleReader articleReader;
    private VideoReader videoReader;
    private Login login;


    public PartyLearner(AccessibilityService service) {
        login = new Login(service);
        articleReader = new ArticleReader(service);
        videoReader = new VideoReader(service);
    }

    public void learn() {
        login();
        readArticle();
        watchVideo();
    }

    private void login() {
        login.process();
    }

    private void readArticle() {
        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        if (articleReader.findEntrance("学习")) {
            if (articleReader.findEntrance("推荐")) {
                articleReader.loop(date, "欢迎发表你的观点");
            }
        }
    }

    private void watchVideo() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        @SuppressLint("SimpleDateFormat")
        String date = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
        if (videoReader.findEntrance("视听学习")) {
            if (videoReader.findEntrance("联播频道")) {
                videoReader.loop(date, "欢迎发表你的观点");
            }
        }
    }
}
