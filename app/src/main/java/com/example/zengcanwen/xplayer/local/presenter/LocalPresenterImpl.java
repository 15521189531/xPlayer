package com.example.zengcanwen.xplayer.local.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.zengcanwen.xplayer.Bean.LocalVideoFileBean;
import com.example.zengcanwen.xplayer.local.model.LocalListener;
import com.example.zengcanwen.xplayer.local.model.LocalModel;
import com.example.zengcanwen.xplayer.local.model.LocalModelImpl;
import com.example.zengcanwen.xplayer.local.view.LocalView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.example.zengcanwen.xplayer.local.model.LocalModelImpl.FILEFINDFINISH;


/**
 * 本地列表Fragment中presenter层的实现类
 * Created by zengcanwen on 2018/1/25.
 */

public class LocalPresenterImpl implements LocalPresenter {

    private LocalView mLocalView;
    private LocalModelImpl mLocalmodel;
    private MyHandler myHandler;
    private static final int CONFIGURAIONCHANGE = 102;

    public LocalPresenterImpl(LocalView mLocalView, LocalListener mLocalListener, Context context) {
        this.mLocalView = mLocalView;
        myHandler = new MyHandler(this);
        mLocalmodel = new LocalModelImpl(context, myHandler, mLocalListener);
        init();
    }

    @Override
    public void clickItemP(int positin) {
        mLocalmodel.clickFile(positin);
    }

    @Override
    public void deleteItemP(int position) {
        ArrayList<LocalVideoFileBean> fileList = mLocalmodel.deleteFile(position);
        mLocalView.notifyAdapter(fileList);
    }

    @Override
    public void init() {
        mLocalView.init();
        mLocalmodel.init() ;
        mLocalView.showLoading();
        mLocalmodel.findFile();
}

    @Override
    public void filterFile() {
        ArrayList<LocalVideoFileBean> fileList = mLocalmodel.filterFile();
        for(LocalVideoFileBean l : fileList){
            Log.i("aaaaa" , l.getmName()) ;
        }
        mLocalView.setAdapter(fileList);
    }

    @Override
    public void configurationChanged() {
        //解决横屏后MyListView的错位,延时更新
        myHandler.sendMessageDelayed(myHandler.obtainMessage(CONFIGURAIONCHANGE), 500);
    }


    private static class MyHandler extends Handler {
        WeakReference<LocalPresenterImpl> mWeakReference;

        public MyHandler(LocalPresenterImpl localPresenterImpl) {
            mWeakReference = new WeakReference<LocalPresenterImpl>(localPresenterImpl);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FILEFINDFINISH:      //扫描结束后发出的处理
                    mWeakReference.get().filterFile();
                    mWeakReference.get().mLocalView.closeLoading();
                    break;

                case CONFIGURAIONCHANGE:        //解决横屏后MyListView的错位
                    mWeakReference.get().mLocalView.setAdapterChange();
            }
        }
    }
}
