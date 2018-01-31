package com.example.zengcanwen.xplayer.online.download;

import android.content.Context;

import com.example.zengcanwen.xplayer.Bean.NetVideosDataBean;

import java.util.HashMap;

/**
 *  * 实现断点续传的管理器
 * Created by zengcanwen on 2017/12/18.
 */

public class DownManager {
    private HashMap<String, DownloadTask> hashMap;
    private static DownManager downManager;
    private Context context;

    //单例模式
    public static DownManager getInstance(Context context) {
        if (downManager == null) {
            synchronized (DownManager.class) {
                if (downManager == null) {
                    downManager = new DownManager(context);
                }
            }
        }
        return downManager;
    }

    //管理器初始化
    private DownManager(Context context) {
        hashMap = new HashMap<>();
        this.context = context;
    }

    //添加下载文件到管理器
    public void addFile(NetVideosDataBean netVideosDataBean, DownListener d) {
        if (hashMap.containsKey(netVideosDataBean.getVideo_url())) {
            return;
        } else {
            hashMap.put(netVideosDataBean.getVideo_url(), new DownloadTask(netVideosDataBean, d, context));
        }
    }


    //暂停
    public void pause(String url) {
        if (hashMap.containsKey(url)) {
            hashMap.get(url).pause();
        }
    }

    //下载
    public void start(String url, int position) {
        if (hashMap.containsKey(url)) {
            hashMap.get(url).start(position);
        }
    }


}
