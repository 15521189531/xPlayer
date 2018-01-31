package com.example.zengcanwen.xplayer.main.presenter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.example.zengcanwen.xplayer.Bean.EventBusBean;
import com.example.zengcanwen.xplayer.Bean.VideoSaveBean;
import com.example.zengcanwen.xplayer.local.useful.SearchFile;
import com.example.zengcanwen.xplayer.main.model.MainModelImpl;
import com.example.zengcanwen.xplayer.main.view.MainView;

import java.lang.ref.WeakReference;

import static com.example.zengcanwen.xplayer.Bean.VideoSaveBean.ONLINE_TAG;

/**
 * 播放界面Activity的pressenter层实现类
 * Created by zengcanwen on 2018/1/30.
 */

public class MainPresenterImpl implements MainPresenter {
    public static final int SYNCSEEKBAR = 101;
    public static final int SHOWBUTTON = 102;

    private MainView mMainView;
    private MainModelImpl mMainModel;
    private MyHandler mMyHandler;

    public MainPresenterImpl(Activity mActivity, MainView mMainView) {
        mMainModel = new MainModelImpl(mActivity);
        this.mMainView = mMainView;
        init();
    }

    @Override
    public void init() {
        mMainModel.init();
        mMainView.init();
        initVideoView();
        mMainModel.addLocalFragment();
        mMyHandler = new MyHandler(this);
    }

    @Override
    public void sendMessage(Message message) {
        mMyHandler.sendMessage(message);
    }

    @Override
    public void updataVideoView(int seekTo, String time, int progress) {
        mMainView.updataVideoView(seekTo, time, progress);
    }

    @Override
    public void sendMessageDelayed(Message message, long time) {
        mMyHandler.sendMessageDelayed(message, time);
    }

    @Override
    public void localTvClick() {
        mMainModel.addLocalFragment();
    }

    @Override
    public void onlineTvClick() {
        mMainModel.addOnlineFragment();
    }

    @Override
    public void eventHandler(EventBusBean eventBusBean) {
        int tag = eventBusBean.getTag();
        String path = eventBusBean.getPath();
        String name = eventBusBean.getName();
        if (tag == ONLINE_TAG && mMainModel.isOnlineVideoHad(name)) {
            path = SearchFile.FILEPATHBASE + name;
        }
        mMainView.eventBusHandler(tag, path);
        mMainModel.saveVideoData(eventBusBean);
    }

    @Override
    public void initVideoView() {
        VideoSaveBean videoSaveBean = mMainModel.initVideoData();
        mMainView.initVideoView(videoSaveBean);
    }

    @Override
    public void pause() {
        mMainView.saveSeekTime();
    }

    @Override
    public void destory() {
        mMainModel.destory();
    }

    private static class MyHandler extends Handler {
        WeakReference<MainPresenterImpl> weakReference;

        private MyHandler(MainPresenterImpl mainPresenter) {
            weakReference = new WeakReference<>(mainPresenter);
        }

        @Override
        public void handleMessage(Message msg) {
            MainPresenterImpl mainPresenter = weakReference.get();
            if (null == mainPresenter) return;
            switch (msg.what) {
                case SYNCSEEKBAR:
                    mainPresenter.mMainView.syncSeekBar();
                    sendMessage(this.obtainMessage(SYNCSEEKBAR));
                    break;

                case SHOWBUTTON:
                    mainPresenter.mMainView.showAllButton(msg);
                    break;
            }
        }
    }
}
