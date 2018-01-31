package com.example.zengcanwen.xplayer.main.view;

        import android.os.Message;

        import com.example.zengcanwen.xplayer.Bean.VideoSaveBean;

/**
 * 播放界面Activity的view层接口
 * Created by zengcanwen on 2018/1/30.
 */

public interface MainView {

    void init();

    void updataVideoView(int seekTo, String time, int progress);

    boolean syncSeekBar();

    void showAllButton(Message msg);

    void saveSeekTime();

    void eventBusHandler(int tag, String path);

    void initVideoView(VideoSaveBean videoSaveBean);


}
