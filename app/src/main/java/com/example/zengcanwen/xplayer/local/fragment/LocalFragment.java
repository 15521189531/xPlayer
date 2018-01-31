package com.example.zengcanwen.xplayer.local.fragment;

import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zengcanwen.xplayer.local.adapter.MyListViewAdapter;
import com.example.zengcanwen.xplayer.Bean.LocalVideoFileBean;
import com.example.zengcanwen.xplayer.R;
import com.example.zengcanwen.xplayer.Util.LoadingUtil;
import com.example.zengcanwen.xplayer.local.customview.MyListView;
import com.example.zengcanwen.xplayer.local.model.LocalListener;
import com.example.zengcanwen.xplayer.local.presenter.LocalPresenterImpl;
import com.example.zengcanwen.xplayer.local.view.LocalView;

import java.util.ArrayList;

/**
 * 本地视频播放列表Fragment
 * Created by zengcanwen on 2018/1/25.
 */

public class LocalFragment extends Fragment implements LocalView, LocalListener, MyListViewAdapter.MyClickListener {

    private MyListView mMyListView;
    private MyListViewAdapter mMyListViewAdapter;
    private LoadingUtil mLoadingUtil;
    private LocalPresenterImpl mLocalPresenter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.local_fragment_layout , container , false) ;
        mMyListView = (MyListView)view.findViewById(R.id.local_fragment_mlv) ;
        mLocalPresenter = new LocalPresenterImpl(this, this, getActivity());
        return view ;
    }

    @Override
    public void init() {
        mMyListViewAdapter = new MyListViewAdapter(getActivity(), mMyListView);
        mLoadingUtil = new LoadingUtil(getActivity());
        mMyListViewAdapter.setMyClickListener(this);
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
    public void setAdapter(ArrayList<LocalVideoFileBean> fileList) {
        Log.i("bbbbb" , fileList.size() + "") ;
        mMyListViewAdapter.setArrayList(fileList);
        mMyListView.setAdapter(mMyListViewAdapter);
        Log.i("aaaa" , "-------------------------------------") ;
    }

    @Override
    public void notifyAdapter(ArrayList<LocalVideoFileBean> fileList) {
        mMyListViewAdapter.setArrayList(fileList);
        mMyListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public LocalVideoFileBean getLocalVideoFileBean(int position) {
        LocalVideoFileBean localVideoFileBean = (LocalVideoFileBean) mMyListViewAdapter.getItem(position);
        return localVideoFileBean;
    }


    @Override
    public void contentClickListener(int position) {
        mLocalPresenter.clickItemP(position);
    }

    @Override
    public void cancelClickListener(int position) {

    }

    @Override
    public void deleteClickListener(int position) {
        mLocalPresenter.deleteItemP(position);
    }

    @Override
    public void setAdapterChange() {
        mMyListView.setAdapter(mMyListViewAdapter);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mLocalPresenter.configurationChanged();
    }

}
