package com.example.zengcanwen.xplayer.Util;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by zengcanwen on 2017/12/18.
 * 实现断点续传的HttpUtil，优化
 */

public class HttpUtil2 {
    private static OkHttpClient okHttpClient ;
    private static HttpUtil2 httpUtil2 ;
    private final static int CONNECT_TIMEOUT  = 60;
    private final static int WRITE_TIMEOUT  = 60;
    private final static int READ_TIMEOUT  = 60;

    //初始化okhttpClient
    private HttpUtil2(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT , TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT , TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT , TimeUnit.SECONDS);
        okHttpClient = builder.build() ;
    }

    //双重锁单例模式
    public static HttpUtil2 getInstance(){
        if(httpUtil2 == null){
            synchronized (HttpUtil2.class){
                if(httpUtil2 == null){
                    httpUtil2 = new HttpUtil2() ;
                }
            }
        }

        return httpUtil2 ;
    }

    public void downFileByRanger(String url , long startIndex , Callback callback){
        Request request = new Request.Builder().url(url)
                .header("RANGE", "bytes=" + startIndex + "-").build();
        doAsync(request , callback);
    }


    //实现异步请求
    private void doAsync(Request request , Callback callback){
        //创建请求会话
        Call call = okHttpClient.newCall(request) ;
        //异步执行会话请求
        call.enqueue(callback);
    }
}
