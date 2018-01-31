package com.example.zengcanwen.xplayer.online.view;

import android.graphics.Bitmap;
import android.view.View;
import com.example.zengcanwen.xplayer.Bean.NetVideosDataBean;
import java.util.ArrayList;

/**
 * 在线视频Fragment的view层接口
 * Created by zengcanwen on 2018/1/26.
 */

public interface OnlineView {

    void init();

    void showLoading();

    void closeLoading();

    void setBitmap(Bitmap bitmap, int position);

    boolean isFragmentFinish();

    void setAdapter(ArrayList<NetVideosDataBean> adapterList);

    void setProgress(String status, int progress, int position);

    int getProgressBarTag(View view);

    NetVideosDataBean getAdapterItemBean(int position);
}
