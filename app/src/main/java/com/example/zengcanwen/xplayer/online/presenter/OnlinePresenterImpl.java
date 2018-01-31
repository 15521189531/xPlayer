package com.example.zengcanwen.xplayer.online.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.zengcanwen.xplayer.Bean.EventBusServiceBean;
import com.example.zengcanwen.xplayer.Bean.NetVideosDataBean;
import com.example.zengcanwen.xplayer.online.model.OnlineModelImpl;
import com.example.zengcanwen.xplayer.online.view.OnlineView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.zengcanwen.xplayer.Bean.EventBusServiceBean.DOWNING;
import static com.example.zengcanwen.xplayer.Bean.EventBusServiceBean.FINISH;
import static com.example.zengcanwen.xplayer.Bean.EventBusServiceBean.PAUSE;
import static com.example.zengcanwen.xplayer.online.model.OnlineModelImpl.SEARCHFILEONLINEFAIL;
import static com.example.zengcanwen.xplayer.online.model.OnlineModelImpl.SEARCHFILEONLINESUCCESS;
import static com.example.zengcanwen.xplayer.online.model.OnlineModelImpl.SETBITMAP;

/**
 * 在线视频Fragment的model层实现类
 * Created by zengcanwen on 2018/1/26.
 */

public class OnlinePresenterImpl implements OnlinePresenter {
    private OnlineModelImpl mOnlineModel;
    private OnlineView mOnlineView;
    private Context mContext;

    public OnlinePresenterImpl(Context mContext, OnlineView mOnlineView) {
        this.mContext = mContext;
        this.mOnlineView = mOnlineView;
        MyHandler myHandler = new MyHandler(this);
        mOnlineModel = new OnlineModelImpl(mContext, myHandler);
        init();
    }

    @Override
    public void init() {
        mOnlineModel.init();
        mOnlineModel.setCache();
        mOnlineModel.searchOnlineFile();
        mOnlineView.init();
        mOnlineView.showLoading();
    }

    @Override
    public void updataPic(String url, int position) {
        mOnlineModel.updatePic(url, position);
    }

    @Override
    public void initProgress(int position) {
        HashMap<Integer, Integer> progressHM = mOnlineModel.getProgressHM();
        if (progressHM == null) return;
        Integer progress = progressHM.get(position);
        if (progress == null || progress == 0) {
            mOnlineView.setProgress("", 0, position);
        } else if (progress == 100) {
            mOnlineView.setProgress("下载完成", 100, position);
        } else {
            mOnlineView.setProgress("暂停", progress, position);
        }
    }

    @Override
    public void listViewrecycle(View view) {
        int position = mOnlineView.getProgressBarTag(view);
        mOnlineModel.listViewRecycle(position);
    }

    @Override
    public void listViewScroll(int firstItemVisiable, int visibleItemCount) {
        mOnlineModel.listViewScroll(firstItemVisiable, visibleItemCount);
    }

    @Override
    public void progressClick(int position) {
        Boolean isDownFinish = mOnlineModel.isdownFinish(position);
        Log.i("ddddd", isDownFinish + "");
        if (isDownFinish) {
            Toast.makeText(mContext, "下载已经完成", Toast.LENGTH_SHORT).show();
            return;
        }

        NetVideosDataBean netVideosDataBean = mOnlineView.getAdapterItemBean(position);
        Boolean isDowning = mOnlineModel.isdowning(position);
        if (isDowning) {
            mOnlineModel.pauseFile(netVideosDataBean);
        } else {
            mOnlineModel.downloadFile(netVideosDataBean, position);
        }
    }

    @Override
    public void lLayoutClick(int position) {
        mOnlineModel.clickLlayout(position);
    }

    @Override
    public boolean eventBusHandler(EventBusServiceBean eventBusServiceBean) {
        int TAG = eventBusServiceBean.getTag();
        int position = eventBusServiceBean.getPosition();
        Boolean isHind = mOnlineModel.isHind(position);
        if (TAG == DOWNING) {
            mOnlineModel.downingCallback(eventBusServiceBean, position);
            int progress = eventBusServiceBean.getProgress();
            if (!isHind) {
                mOnlineView.setProgress("正在下载", progress, position);
            }
        } else if (TAG == PAUSE) {
            mOnlineModel.pauseCallback(position);
            if (!isHind) {
                mOnlineView.setProgress("暂停", -1, position);
            }
        } else if (TAG == FINISH) {
            mOnlineModel.finishCallback(position);
            if (!isHind) {
                mOnlineView.setProgress("下载完成", 100, position);
            }

            boolean isFragmentFinish = mOnlineView.isFragmentFinish();
            if (isFragmentFinish) {
                return mOnlineModel.stopEBandService();
            }
        }

        return false;
    }

    @Override
    public boolean destory() {
        return mOnlineModel.stopEBandService();
    }

    private static class MyHandler extends Handler {
        private WeakReference<OnlinePresenterImpl> weakReference;

        private MyHandler(OnlinePresenterImpl onlinePresenterImpl) {
            weakReference = new WeakReference<>(onlinePresenterImpl);
        }

        @Override
        public void handleMessage(Message msg) {
            OnlinePresenterImpl onlinePresenterImpl = weakReference.get();
            if (onlinePresenterImpl == null) return;
            switch (msg.what) {
                case SEARCHFILEONLINESUCCESS:
                    onlinePresenterImpl.mOnlineModel.getDataFormHM();
                    onlinePresenterImpl.mOnlineView.closeLoading();
                    ArrayList<NetVideosDataBean> adapterList = onlinePresenterImpl.mOnlineModel.getDataList();
                    onlinePresenterImpl.mOnlineView.setAdapter(adapterList);
                    break;

                case SEARCHFILEONLINEFAIL:
                    onlinePresenterImpl.mOnlineView.closeLoading();
                    Toast.makeText(onlinePresenterImpl.mContext, "加载数据失败", Toast.LENGTH_SHORT).show();
                    break;

                case SETBITMAP:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    int position = msg.arg1;
                    onlinePresenterImpl.mOnlineView.setBitmap(bitmap, position);
                    break;
            }
        }
    }
}
