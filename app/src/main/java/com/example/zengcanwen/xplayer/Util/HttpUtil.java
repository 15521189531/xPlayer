package com.example.zengcanwen.xplayer.Util;

import android.os.Environment;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by zengcanwen on 2017/12/6.
 */

public class HttpUtil {
    //使用单例
    private static HttpUtil httpUtil;

    private OkHttpClient okHttpClient;

    private  Call call ;

    private HttpUtil() {
        okHttpClient = new OkHttpClient();
    }

    //双重锁
    public static HttpUtil getInstance() {
        if (httpUtil == null) {
            synchronized (HttpUtil.class) {
                if (httpUtil == null) {
                    httpUtil = new HttpUtil();
                }
            }
        }
        return httpUtil;
    }

    //相应回调
    public interface OkhttpResponse {
        void onError(Call call, IOException e);
        void onResponse(Call call, Response response);
    }


    //使用get方式请求内容
    public void getHttp(String inputurl, Map<String, String> parMap, final OkhttpResponse okhttpResponse) {
        if (!(inputurl.contains("http") || inputurl.contains("https"))) {
            return;
        }
        StringBuilder urlStr = new StringBuilder(inputurl + "?");
        //遍历Map
        if (parMap != null && parMap.size() != 0) {
            Iterator<Map.Entry<String, String>> iterator = parMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                urlStr.append(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        String url = urlStr.substring(0, urlStr.length() - 1);
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                okhttpResponse.onError(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                okhttpResponse.onResponse(call, response);
            }
        });
    }

    //post请求
    public void postHttp(String inputUrl, Map<String, String> map, final OkhttpResponse okhttpResponse) {
        if (!(inputUrl.contains("http") || inputUrl.contains("https"))) {
            return;
        }
        FormBody.Builder formBody = new FormBody.Builder();
        //遍历Map
        if (map != null && map.size() != 0) {
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                formBody.add(entry.getKey(), entry.getValue());
            }
        }
        Request request = new Request.Builder().url(inputUrl).post(formBody.build()).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                okhttpResponse.onError(call, e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                okhttpResponse.onResponse(call, response);
            }
        });
    }
}
