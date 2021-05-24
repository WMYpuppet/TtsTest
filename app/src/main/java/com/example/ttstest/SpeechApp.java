package com.example.ttstest;

import android.app.Application;

import com.iflytek.cloud.SpeechUtility;

/**
 * 作者：Created by Administrator on 2020/11/24.
 * 邮箱：
 */
public class SpeechApp extends Application {
    @Override
    public void onCreate() {
        SpeechUtility.createUtility(SpeechApp.this, "appid=" + getString(R.string.app_id));
        super.onCreate();
    }
}
