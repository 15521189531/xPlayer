package com.example.zengcanwen.xplayer.online.presenter;

import android.view.View;
import com.example.zengcanwen.xplayer.Bean.EventBusServiceBean;

/**
 * 在线视频Fragment的presenter层接口
 * Created by zengcanwen on 2018/1/26.
 */

public interface OnlinePresenter {
    void init();

    void updataPic(String url, int position);

    void initProgress(int position);

    void listViewrecycle(View view);

    void listViewScroll(int firstItemVisiable, int lastItemVisiable);

    void progressClick(int position);

    void lLayoutClick(int position);

    boolean eventBusHandler(EventBusServiceBean eventBusServiceBean);

    boolean destory();


}
