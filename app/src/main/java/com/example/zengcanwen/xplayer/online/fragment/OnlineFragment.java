package com.example.zengcanwen.xplayer.online.fragment;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.zengcanwen.xplayer.Bean.EventBusServiceBean;
import com.example.zengcanwen.xplayer.Bean.NetVideosDataBean;
import com.example.zengcanwen.xplayer.R;
import com.example.zengcanwen.xplayer.Util.LoadingUtil;
import com.example.zengcanwen.xplayer.online.adapter.MyOnlineLvAdapter;
import com.example.zengcanwen.xplayer.online.customView.MyProgressBarView;
import com.example.zengcanwen.xplayer.online.presenter.OnlinePresenterImpl;
import com.example.zengcanwen.xplayer.online.view.OnlineView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * 在线视频列表的Fragment
 * Created by zengcanwen on 2018/1/26.
 */

public class OnlineFragment extends Fragment implements OnlineView, MyOnlineLvAdapter.MyViewListener {

    private MyOnlineLvAdapter mMyOnlineLvAdapter;
    private ListView mOnlineListView;
    private boolean isFragmentFinish = false;
    private OnlinePresenterImpl mOnlinePresenter;
    private LoadingUtil mLoadingUtil;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.net_fragment_layout, container, false);
        EventBus.getDefault().register(this);
        mOnlineListView = view.findViewById(R.id.net_fragment_lv);
        mOnlinePresenter = new OnlinePresenterImpl(getActivity(), this);
        return view;
    }

    @Override
    public void init() {
        mLoadingUtil = new LoadingUtil(getActivity());
        mMyOnlineLvAdapter = new MyOnlineLvAdapter(getActivity()) {
            @Override
            public void updataPic(String url, int position) {
                mOnlinePresenter.updataPic(url, position);
            }

            @Override
            public void initProgress(int position) {
                mOnlinePresenter.initProgress(position);
            }
        };

        mOnlineListView.setRecyclerListener(new AbsListView.RecyclerListener() {
            @Override
            public void onMovedToScrapHeap(View view) {
                mOnlinePresenter.listViewrecycle(view);
            }
        });

        mOnlineListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(mOnlinePresenter != null){
                    mOnlinePresenter.listViewScroll(firstVisibleItem, visibleItemCount);
                }
            }
        });

        mMyOnlineLvAdapter.setMyClickListener(this);
    }


    @Override
    public void progressClick(int position) {
        mOnlinePresenter.progressClick(position);
    }

    @Override
    public void LlClick(int position) {
        mOnlinePresenter.lLayoutClick(position);
    }

    //处理下载视频的回调
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    public void eventHandler(EventBusServiceBean eventBusServiceBean) {
       boolean isEnd = mOnlinePresenter.eventBusHandler(eventBusServiceBean);
       if (isEnd) EventBus.getDefault().unregister(this);
    }

    @Override
    public void showLoading() {
        mLoadingUtil.showProgressDialog();
    }

    @Override
    public void closeLoading() {
        mLoadingUtil.cancelProgressDialog();
    }

    @Override
    public void setBitmap(Bitmap bitmap, int position) {
        ImageView imageView = mMyOnlineLvAdapter.getImageView(position);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    public boolean isFragmentFinish() {
        return isFragmentFinish;
    }

    @Override
    public void setAdapter(ArrayList<NetVideosDataBean> adapterList) {
        mMyOnlineLvAdapter.setArrayList(adapterList);
        mOnlineListView.setAdapter(mMyOnlineLvAdapter);
    }

    @Override
    public void setProgress(String status, int progress, int position) {
        MyProgressBarView myProgressBarView = mMyOnlineLvAdapter.getMyProgressBarView(position);
        if (null != status) {
            myProgressBarView.setTextStr(status);
        }
        if (progress != -1) {
            myProgressBarView.setProgress(progress);
        }
    }

    @Override
    public int getProgressBarTag(View view) {
        MyProgressBarView myProgressBarView =  view.findViewById(R.id.my_progress_bar_view);
        return (Integer) myProgressBarView.getTag();
    }

    @Override
    public NetVideosDataBean getAdapterItemBean(int position) {
        return (NetVideosDataBean) mMyOnlineLvAdapter.getItem(position);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isFragmentFinish = true ;
        boolean isEnd = mOnlinePresenter.destory();
        if(isEnd) EventBus.getDefault().unregister(this);
    }
}
