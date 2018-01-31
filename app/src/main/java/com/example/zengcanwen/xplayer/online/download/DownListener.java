package com.example.zengcanwen.xplayer.online.download;

/**
 * 下载功能接口
 * Created by zengcanwen on 2017/12/18.
 */

public interface DownListener {

    void Success() ;

    void Downing(int progress ) ;

    void pause() ;

}
