package com.example.zengcanwen.xplayer.main.presenter;

import android.os.Message;

import com.example.zengcanwen.xplayer.Bean.EventBusBean;

/**
 * 播放界面Activity的presneter层接口
 * Created by zengcanwen on 2018/1/30.
 */

public interface MainPresenter {

    void init();

    void eventHandler(EventBusBean eventBusBean);

    void localTvClick();

    void onlineTvClick();

    void initVideoView();

    void sendMessage(Message message);

    void sendMessageDelayed(Message message, long time);

    void updataVideoView(int seekTo, String time, int progress);

    void destory();

    void pause();
}
