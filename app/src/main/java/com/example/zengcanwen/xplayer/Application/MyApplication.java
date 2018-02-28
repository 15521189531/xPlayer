package com.example.zengcanwen.xplayer.Application;

import android.app.Application;
import android.content.Context;

import com.avos.avoscloud.AVOSCloud;

/**
 * 暴露ApplicationContext
 * Created by zengcanwen on 2017/12/29.
 */

public class MyApplication extends Application {

    //解决SPUtil的内存泄漏
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this,"dMbj94bqd6ImGRcEIK04oi4U-gzGzoHsz","RFOvtdcMfC8tepr0d95VlM2M");
        AVOSCloud.setDebugLogEnabled(true);
    }
}
