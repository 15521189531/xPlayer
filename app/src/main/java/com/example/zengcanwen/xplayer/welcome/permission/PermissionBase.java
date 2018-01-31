package com.example.zengcanwen.xplayer.welcome.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * 权限实现类
 * Created by zengcanwen on 2017/12/12.
 */

public class PermissionBase {
    private String[] premission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Activity activity;
    private Handler mHandler;

    public PermissionBase(Activity activity, Handler mHandler) {
        this.activity = activity;
        this.mHandler = mHandler;
    }

    //判断是否具有权限，如果没有则去申请
    public void premissionMain() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int i = ContextCompat.checkSelfPermission(activity, premission[0]);
            if (i != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, premission, 321);
            } else {
                mHandler.sendMessageDelayed(mHandler.obtainMessage(101), 800);
            }
        } else {
            mHandler.sendMessageDelayed(mHandler.obtainMessage(101), 800);
        }
    }


    //申请权限回调
    public void premissionRequest(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    boolean b = activity.shouldShowRequestPermissionRationale(premission[0]);
                    if (!b) {
                        goToAppSeting();
                    } else {
                        activity.finish();
                    }
                } else {
                    Toast.makeText(activity, "获取权限成功", Toast.LENGTH_SHORT).show();
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(101), 800);

                }
            }
        }
    }


    //跳转到当前应用的权限设置界面
    private void goToAppSeting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri rui = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(rui);
        activity.startActivityForResult(intent, 123);
    }

    //权限申请结果回调
    public void premissionResultRequest(int requestCode, int resultCode, Intent data) {
        if (requestCode == 123) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int i = ContextCompat.checkSelfPermission(activity, premission[0]);
                if (i != PackageManager.PERMISSION_GRANTED) {
                    goToAppSeting();
                } else {
                    Toast.makeText(activity, "权限获取成功", Toast.LENGTH_SHORT).show();
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(101), 800);

                }
            }
        }
    }
}
