package com.example.zengcanwen.xplayer.local.presenter;

/**
 * 本地列表Fragment中presenter层的接口
 * Created by zengcanwen on 2018/1/25.
 */

public interface LocalPresenter {

    void clickItemP(int position);

    void deleteItemP(int position);

    void init();

    void filterFile();

    void configurationChanged();


}
