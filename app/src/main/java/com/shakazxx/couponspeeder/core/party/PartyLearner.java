package com.shakazxx.couponspeeder.core.party;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.accessibility.AccessibilityNodeInfo;

import com.shakazxx.couponspeeder.core.util.GestureUtil;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PartyLearner {

    private ArticleReader articleReader;
    private VideoReader videoReader;
    private Login login;
    private Quiz quiz;


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
            if (articleReader.findEntrance("联播频道")) {
                videoReader.loop(date, "欢迎发表你的观点");
            }
        }
    }
}
