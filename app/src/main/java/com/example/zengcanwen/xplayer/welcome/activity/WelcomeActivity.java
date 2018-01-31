package com.example.zengcanwen.xplayer.welcome.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.zengcanwen.xplayer.R;
import com.example.zengcanwen.xplayer.main.activity.MainActivity;
import com.example.zengcanwen.xplayer.welcome.permission.PermissionBase;

import java.lang.ref.WeakReference;

/**
 * 欢迎界面
 * Created by zengcanwen on 2017/11/27.
 */

public class WelcomeActivity extends AppCompatActivity {

    private PermissionBase mPermissionBase; //权限
    private Handler myHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);

        //使用handler延时实现淡入淡出效果
        myHandler = new MyHandler(this);

        //动态获取权限
        mPermissionBase = new PermissionBase(this, myHandler);
        mPermissionBase.premissionMain();

    }


    private static class MyHandler extends Handler {
        private WeakReference<WelcomeActivity> mWeakReference;

        private MyHandler(WelcomeActivity welcomeActivity) {
            mWeakReference = new WeakReference<>(welcomeActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            WelcomeActivity welcomeActivity = mWeakReference.get();
            switch (msg.what) {
                case 101:
                    //实现跳转并实现动画
                    Intent intent = new Intent(welcomeActivity, MainActivity.class);
                    welcomeActivity.startActivity(intent);
                    welcomeActivity.finish();
                    int VERSION = Integer.parseInt(Build.VERSION.SDK);
                    if (VERSION >= 5) {
                        welcomeActivity.overridePendingTransition(R.anim.entry_anim, R.anim.quit_anim);
                    }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionBase.premissionRequest(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionBase.premissionResultRequest(requestCode, resultCode, data);
    }
}
