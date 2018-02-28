package com.example.zengcanwen.xplayer.welcome.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.example.zengcanwen.xplayer.R;
import com.example.zengcanwen.xplayer.main.activity.MainActivity;
import com.example.zengcanwen.xplayer.welcome.customtrailview.BezierTrailAnimationView;
import com.example.zengcanwen.xplayer.welcome.permission.PermissionBase;

import java.lang.ref.WeakReference;

/**
 * 欢迎界面
 * Created by zengcanwen on 2017/11/27.
 */

public class WelcomeActivity extends AppCompatActivity {

    private PermissionBase mPermissionBase; //权限
    private Handler myHandler;
    private FrameLayout mainFl;
    private LinearLayout mainLl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);
        mainFl = findViewById(R.id.main_fl);
        mainLl = findViewById(R.id.main_ll);

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
            final WelcomeActivity welcomeActivity = mWeakReference.get();
            if (welcomeActivity == null) return;
            switch (msg.what) {
                case 101:
                    //开启轨迹动画，在动画结束时展示欢迎图标
                    BezierTrailAnimationView bezierTrailAnimationView = new BezierTrailAnimationView(welcomeActivity.mainFl, welcomeActivity);
                    bezierTrailAnimationView.start();
                    bezierTrailAnimationView.setOnEndAnimation(new BezierTrailAnimationView.OnEndAnimation() {
                        @Override
                        public void endAnimation() {
                            welcomeActivity.mainLl.setVisibility(View.VISIBLE);
                            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(welcomeActivity.mainLl, "alpha", 0.0f, 1.0f);
                            objectAnimator.setDuration(1000);
                            objectAnimator.start();
                            objectAnimator.addListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    sendEmptyMessageDelayed(102, 1000);
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            });
                        }
                    });
                    break;

                case 102:

                    //实现淡入淡出跳转
                    Intent intent = new Intent(welcomeActivity, MainActivity.class);
                    welcomeActivity.startActivity(intent);
                    welcomeActivity.finish();
                    int VERSION = Integer.parseInt(Build.VERSION.SDK);
                    if (VERSION >= 5) {
                        welcomeActivity.overridePendingTransition(R.anim.entry_anim, R.anim.quit_anim);
                    }
                    break;
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
