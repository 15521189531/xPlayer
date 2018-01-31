package com.example.zengcanwen.xplayer.local.model;


import android.content.Context;
import android.os.Handler;

import com.example.zengcanwen.xplayer.Bean.EventBusBean;
import com.example.zengcanwen.xplayer.Bean.LocalVideoFileBean;
import com.example.zengcanwen.xplayer.Util.SPUtil;
import com.example.zengcanwen.xplayer.local.useful.SearchFile;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.example.zengcanwen.xplayer.Bean.VideoSaveBean.LOCAL_TAG;

/**
 * 本地列表Fragment中model层的实现类
 * Created by zengcanwen on 2018/1/25.
 */

public class LocalModelImpl implements LocalModel {

    private Context mContext;
    private Handler mHandler;
    private ArrayList<LocalVideoFileBean> mFileArrayList;
    private SearchFile mFileUtil;
    private SPUtil mSPUtil;
    private LocalListener mLocalListener;
    public static final int FILEFINDFINISH = 101;

    public LocalModelImpl(Context mContext, Handler mHandler, LocalListener mLocalListener) {
        this.mContext = mContext;
        this.mHandler = mHandler;
        this.mLocalListener = mLocalListener;
    }

    @Override
    public void init() {
        mFileArrayList = new ArrayList<>();
        mFileUtil = new SearchFile(mContext);
        mSPUtil = SPUtil.getInstance();
    }


    @Override
    public void findFile() {
        MyThread myThread = new MyThread(this);
        myThread.start();
    }

    @Override
    public ArrayList<LocalVideoFileBean> filterFile() {

        int delfileNumber = mSPUtil.getInt("filenumber");  //获取删除文件的数量
        ArrayList<String> fileNameArr = new ArrayList<>();

        for (LocalVideoFileBean localVideoFileBean : mFileArrayList) {
            fileNameArr.add(localVideoFileBean.getmName());
        }

        if (delfileNumber != -1) {
            for (int i = 1; i <= delfileNumber; i++) {
                String fileName = mSPUtil.getString("deleteFileName" + i);
                if (fileNameArr.contains(fileName)) {
                    int index = fileNameArr.indexOf(fileName);
                    fileNameArr.remove(index);
                    mFileArrayList.remove(index);
                }
            }
        }

        return mFileArrayList;
    }

    @Override
    public ArrayList<LocalVideoFileBean> deleteFile(int position) {
        //在Sp中保存删除的文件，可以在扫描完成后的ArrayList中进行去除
        int delfileNumber = mSPUtil.getInt("filenumber");     //获取删除文件的数量
        if (delfileNumber == -1) {
            delfileNumber = 1;
        } else {
            delfileNumber++;
        }
        LocalVideoFileBean localVideoFileBean = mLocalListener.getLocalVideoFileBean(position);
        String fileName = localVideoFileBean.getmName();
        mSPUtil.save("filenumber", delfileNumber);
        mSPUtil.save("deleteFileName" + delfileNumber, fileName);
        //清除array中position位置的元素
        mFileArrayList.remove(position);
        //删除最后一个的时候，避免空指针
        if (position >= mFileArrayList.size()) {
            position = 0;
        }
        //删除所有时，避免空指针
        if (mFileArrayList.size() != 0) {
            //删除以后让播放器播放下一个视频
            localVideoFileBean = mLocalListener.getLocalVideoFileBean(position);
            eventbusPost(localVideoFileBean);
        }

        return mFileArrayList;
    }


    @Override
    public void clickFile(int position) {
        LocalVideoFileBean localVideoFileBean = mLocalListener.getLocalVideoFileBean(position);
        eventbusPost(localVideoFileBean);
    }


    private static class MyThread extends Thread {
        private WeakReference<LocalModelImpl> weakReference;

        public MyThread(LocalModelImpl localmodelImpl) {
            weakReference = new WeakReference<LocalModelImpl>(localmodelImpl);
        }

        @Override
        public void run() {
            LocalModelImpl localmodelImpl = weakReference.get();
            localmodelImpl.mFileUtil.findVideo(localmodelImpl.mFileArrayList);
            //扫描完成后发出消息
            localmodelImpl.mHandler.sendMessage(localmodelImpl.mHandler.obtainMessage(FILEFINDFINISH));
        }
    }


    //点击按钮发送消息到Activity处理事件
    private void eventbusPost(LocalVideoFileBean localVideoFileBean) {
        EventBusBean eventBusBean = new EventBusBean();
        eventBusBean.setName(localVideoFileBean.getmName());
        eventBusBean.setPath(localVideoFileBean.getmPath());
        eventBusBean.setTag(LOCAL_TAG);
        EventBus.getDefault().post(eventBusBean);
    }


}
