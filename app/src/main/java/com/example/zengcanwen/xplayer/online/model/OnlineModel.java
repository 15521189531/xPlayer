package com.example.zengcanwen.xplayer.online.model;

import com.example.zengcanwen.xplayer.Bean.EventBusServiceBean;
import com.example.zengcanwen.xplayer.Bean.NetVideosDataBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 在线视频Fragment的model层接口
 * Created by zengcanwen on 2018/1/26.
 */

public interface OnlineModel {

    void init();

    void searchOnlineFile();

    void downloadFile(NetVideosDataBean netVideosDataBean, int position);

    void pauseFile(NetVideosDataBean netVideosDataBean);

    void clickLlayout(int position);

    void downingCallback(EventBusServiceBean eventBusServiceBean, int position);

    void pauseCallback(int position);

    void finishCallback(int position);

    boolean stopEBandService();

    void getDataFormHM();

    void setCache();

    boolean isdownFinish(int position);

    boolean isdowning(int position);

    void updatePic(String url, final int position);

    void listViewScroll(int firstVisibleItem, int visibleItemCount);

    void listViewRecycle(int position);

    boolean isHind(int position);

    HashMap<Integer, Integer> getProgressHM();

    ArrayList<NetVideosDataBean> getDataList();

}
