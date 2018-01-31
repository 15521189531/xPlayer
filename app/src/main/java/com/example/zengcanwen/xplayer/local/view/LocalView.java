package com.example.zengcanwen.xplayer.local.view;

import com.example.zengcanwen.xplayer.Bean.LocalVideoFileBean;

import java.util.ArrayList;

/**
 * /**
 * 本地列表Fragment中view层的接口
 * Created by zengcanwen on 2018/1/25.
 */

public interface LocalView {

    void showLoading();

    void closeLoading();

    void setAdapter(ArrayList<LocalVideoFileBean> mFileArrayList);

    void notifyAdapter(ArrayList<LocalVideoFileBean> mFileArrayList);

    void setAdapterChange();

    void init();

}
