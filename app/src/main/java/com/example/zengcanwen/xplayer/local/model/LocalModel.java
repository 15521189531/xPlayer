package com.example.zengcanwen.xplayer.local.model;

import com.example.zengcanwen.xplayer.Bean.LocalVideoFileBean;

import java.util.ArrayList;

/**
 * 本地列表Fragment中model层的接口
 * Created by zengcanwen on 2018/1/25.
 */

public interface LocalModel {

    void init() ;

    void findFile();

    ArrayList<LocalVideoFileBean> filterFile();

    ArrayList<LocalVideoFileBean> deleteFile(int position);

    void clickFile(int position);


}
