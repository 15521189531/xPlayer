package com.example.zengcanwen.xplayer.main.model;

import com.example.zengcanwen.xplayer.Bean.EventBusBean;
import com.example.zengcanwen.xplayer.Bean.VideoSaveBean;

/**
 * 播放界面Activity的model层接口
 * Created by zengcanwen on 2018/1/30.
 */

public interface Mainmodel {

    void init();

    VideoSaveBean initVideoData();

    void saveVideoData(EventBusBean eventBusBean);

    void addLocalFragment();

    void addOnlineFragment();

    void destory();

    boolean isOnlineVideoHad(String name);
}
