package com.example.zengcanwen.xplayer.Util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * 等待框实现类
 * Created by zengcanwen on 2017/12/21.
 */

public class LoadingUtil {
    private ProgressDialog progressDialog;
    private Context context;

    public LoadingUtil(Context context) {
        this.context = context;
    }

    //展示等待对话框
    public void showProgressDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("正在加载信息");
        progressDialog.setMessage("等待中...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    //取消等待对话框
    public void cancelProgressDialog() {
        progressDialog.cancel();
        progressDialog = null;
    }
}
