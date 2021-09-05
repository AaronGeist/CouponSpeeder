package com.shakazxx.couponspeeder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.shakazxx.couponspeeder.core.party.ArticleReader;
import com.shakazxx.couponspeeder.core.party.HistoryRecord;
import com.shakazxx.couponspeeder.core.party.VideoReader;
import com.shakazxx.couponspeeder.core.util.FileUtil;
import com.shakazxx.couponspeeder.service.MyAccessibilityService;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TimePicker.OnTimeChangedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String CONFIG_FILE_PATH = FileUtil.getRootPath() + "/Download/partyStudy.cfg";

    private boolean isSet = false;
    private Button btnSave;
    private Button btnStartPartyStudy;
    private Button btnSettings;
    private Button btnReset;
    private Button btnClean;

    private TextView tvArticleNum;
    private TextView tvArticleTime;
    private TextView tvVideoNum;
    private TextView tvVideoTime;
    private TextView tvKeepTitleNum;
    private TextView tvAlipayCmbToken;
    private TextView tvPassword;

    private Switch swArticle;
    private Switch swVideo;
    private Switch swSingleQuiz;
    private Switch swTwoPersonQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        grantPermission();
        initBtn();
        initCfg();
        startService();
    }

    private void grantPermission() {
        // 读写权限检查 & 等待
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        if (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }

        while (PackageManager.PERMISSION_GRANTED !=
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void initBtn() {
        btnSettings = findViewById(R.id.setting_btn);
        btnSave = findViewById(R.id.saveBtn);
        btnStartPartyStudy = findViewById(R.id.start_party_btn);
        btnReset = findViewById(R.id.reset_btn);
        btnClean = findViewById(R.id.clean_btn);

        tvArticleNum = findViewById(R.id.article_num_tv);
        tvArticleTime = findViewById(R.id.article_time_tv);
        tvVideoNum = findViewById(R.id.video_num_tv);
        tvVideoTime = findViewById(R.id.video_time_tv);
        tvKeepTitleNum = findViewById(R.id.keep_title_tv);
        tvAlipayCmbToken = findViewById(R.id.alipay_cmb_token_tv);
        tvPassword = findViewById(R.id.password_tv);


        swArticle = findViewById(R.id.switchArticle);
        swVideo = findViewById(R.id.switchVideo);
        swSingleQuiz = findViewById(R.id.switchSingleQuiz);
        swTwoPersonQuiz = findViewById(R.id.switchTwoPersonQuiz);

        btnSettings.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnStartPartyStudy.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        btnClean.setOnClickListener(this);
    }

    private void initCfg() {
        try {
            String json = FileUtil.readAll(CONFIG_FILE_PATH);
            if ("".equals(json) || json == null) {
                tvArticleNum.setText("0");
                tvArticleTime.setText("0");
                tvVideoNum.setText("0");
                tvVideoTime.setText("0");
                swArticle.setChecked(true);
                swVideo.setChecked(true);
                swSingleQuiz.setChecked(true);
                swTwoPersonQuiz.setChecked(true);

                tvKeepTitleNum.setText("1");
                tvAlipayCmbToken.setText("aaa");
                tvPassword.setText("aaa");
            } else {
                JSONObject jsonObject = new JSONObject(json);
                tvArticleNum.setText(jsonObject.getString("article_num"));
                tvArticleTime.setText(jsonObject.getString("article_time"));
                tvVideoNum.setText(jsonObject.getString("video_num"));
                tvVideoTime.setText(jsonObject.getString("video_time"));
                swArticle.setChecked(jsonObject.getBoolean("enable_article"));
                swVideo.setChecked(jsonObject.getBoolean("enable_video"));
                swSingleQuiz.setChecked(jsonObject.getBoolean("enable_single_quiz"));
                swTwoPersonQuiz.setChecked(jsonObject.getBoolean("enable_two_person_quiz"));

                tvKeepTitleNum.setText(String.valueOf(HistoryRecord.readData().size()));
                tvAlipayCmbToken.setText(jsonObject.getString("alipay_cmb_token"));
                tvPassword.setText(jsonObject.getString("password"));
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    private Intent createIntent() {
        Intent intent = new Intent(this, MyAccessibilityService.class);
        intent.putExtra("article_num", Integer.valueOf(tvArticleNum.getText().toString()));
        intent.putExtra("article_time", Integer.valueOf(tvArticleTime.getText().toString()));
        intent.putExtra("video_num", Integer.valueOf(tvVideoNum.getText().toString()));
        intent.putExtra("enable_article", swArticle.isChecked());
        intent.putExtra("enable_video", swSingleQuiz.isChecked());
        intent.putExtra("enable_single_quiz", swTwoPersonQuiz.isChecked());
        intent.putExtra("enable_two_person_quiz", swVideo.isChecked());
        intent.putExtra("alipay_cmb_token", tvAlipayCmbToken.getText().toString());
        intent.putExtra("password", tvPassword.getText().toString());

        return intent;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_btn:
                Intent mIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                this.startActivity(mIntent);
                break;
            case R.id.start_party_btn:
                startActivity("cn.xuexi.android", "com.alibaba.android.rimet.biz.SplashActivity");
                break;
            case R.id.saveBtn:
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("article_num", Integer.valueOf(tvArticleNum.getText().toString()));
                    jsonObject.put("article_time", Integer.valueOf(tvArticleTime.getText().toString()));
                    jsonObject.put("video_num", Integer.valueOf(tvVideoNum.getText().toString()));
                    jsonObject.put("video_time", Integer.valueOf(tvVideoTime.getText().toString()));
                    jsonObject.put("enable_article", swArticle.isChecked());
                    jsonObject.put("enable_video", swVideo.isChecked());
                    jsonObject.put("enable_single_quiz", swSingleQuiz.isChecked());
                    jsonObject.put("enable_two_person_quiz", swTwoPersonQuiz.isChecked());
                    jsonObject.put("alipay_cmb_token", tvAlipayCmbToken.getText().toString());
                    jsonObject.put("password", tvPassword.getText().toString());


                    FileUtil.writeLine(CONFIG_FILE_PATH, jsonObject.toString(), false);
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }

                startService(createIntent());

                break;
            case R.id.reset_btn:
                tvArticleNum.setText(String.valueOf(ArticleReader.DEFAULT_READ_ARTICLE_NUM));
                tvArticleTime.setText(String.valueOf(ArticleReader.DEFAULT_TIME_IN_SECOND));
                tvVideoNum.setText(String.valueOf(VideoReader.DEFAULT_WATCH_CNT));
                tvVideoTime.setText(String.valueOf(VideoReader.DEFAULT_OVERALL_TIME));
                swArticle.setChecked(true);
                swVideo.setChecked(true);
                swSingleQuiz.setChecked(true);
                swTwoPersonQuiz.setChecked(true);
                break;
            case R.id.clean_btn:
                if (tvKeepTitleNum.getText() != null && !tvKeepTitleNum.getText().toString().equals("")) {
                    HistoryRecord.cleanup(Integer.valueOf(tvKeepTitleNum.getText().toString()));
                }
                break;
            default:
                break;
        }
     }

    private void startService() {
        startService(createIntent());
    }

    private void startActivity(String packageName, String className) {
        Intent intent = new Intent();
        intent.setPackage(packageName);
        intent.setClassName(packageName, className);
        this.startActivity(intent);
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
        if (!isSet) {
            isSet = true;
        }
    }
}
