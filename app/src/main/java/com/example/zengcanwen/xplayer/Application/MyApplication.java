package com.example.zengcanwen.xplayer.Application;

import android.app.Application;
import android.content.Context;

/**
 * Created by zengcanwen on 2017/12/29.
 */

public class MyApplication extends Application {

    //解决SPUtil的内存泄漏
    private static Context context  ;
    public static Context getContext(){
        return context ;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext() ;
    }
}
