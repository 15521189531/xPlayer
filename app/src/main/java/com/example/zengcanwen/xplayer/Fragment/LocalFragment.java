package com.example.zengcanwen.xplayer.Fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.telecom.Connection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.zengcanwen.xplayer.Adapter.MyListViewAdapter;
import com.example.zengcanwen.xplayer.Bean.EventBusBean;
import com.example.zengcanwen.xplayer.Bean.EventBusServiceBean;
import com.example.zengcanwen.xplayer.Bean.LocalVideoFileBean;
import com.example.zengcanwen.xplayer.R;
import com.example.zengcanwen.xplayer.Util.FileUtil;
import com.example.zengcanwen.xplayer.Util.LoadingUtil;
import com.example.zengcanwen.xplayer.Util.SPUtil;
import com.example.zengcanwen.xplayer.View.MyListView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zengcanwen on 2017/11/28.
 */

public class LocalFragment extends Fragment {

    private MyListView myListView ;
    private ArrayList<LocalVideoFileBean> arrayList ;
    private MyListViewAdapter myListViewAdapter ;
    private SPUtil spUtil ;
    private LoadingUtil loadingUtil ;
    private MyHandler myHandler ;
    private FileUtil fileUtil ;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.local_fragment_layout , container , false) ;
        myListView = (MyListView)view.findViewById(R.id.local_fragment_mlv) ;
        init();
        return view ;
    }





    private void init(){
        spUtil = SPUtil.getInstance() ;
        arrayList = new ArrayList<>() ;
        fileUtil = new FileUtil(getActivity()) ;
        loadingUtil = new LoadingUtil(getActivity()) ;
        loadingUtil.showProgressDialog();
        scannerVideo();
        myHandler = new MyHandler(this) ;
    }





    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //解决横屏后MyListView的错位,延时更新
        myHandler.sendMessageDelayed(myHandler.obtainMessage(102) , 500) ;
    }


    private static class MyHandler extends Handler{
        WeakReference<LocalFragment> mWeakReference ;
        public MyHandler(LocalFragment fragment){
            mWeakReference = new WeakReference<LocalFragment>(fragment) ;
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 101 :      //扫描结束后发出的处理
                    mWeakReference.get().filterDeleteFile(mWeakReference.get().arrayList);
                    mWeakReference.get().initMyListViewAdapter();
                    mWeakReference.get().loadingUtil.cancelProgressDialog();
                    break;

                case 102 :        //解决横屏后MyListView的错位
                    mWeakReference.get().myListView.setAdapter(mWeakReference.get().myListViewAdapter);
            }
        }
    }



    // 扫描本地视频
    private void scannerVideo(){
        MyThread myThread = new MyThread(this) ;
        myThread.start();
    }


    private static class MyThread extends Thread{
        private WeakReference<LocalFragment> weakReference ;
        public MyThread(LocalFragment fragment){
            weakReference = new WeakReference<LocalFragment>(fragment) ;
        }
        @Override
        public void run() {
            LocalFragment localFragment = weakReference.get() ;
            localFragment.fileUtil.findVideo(localFragment.arrayList);
            //扫描完成后发出消息
            localFragment.myHandler.sendMessage(localFragment.myHandler.obtainMessage(101)) ;
        }
    }



    //扫描完成后过滤已经删除的文件
    public void filterDeleteFile(ArrayList<LocalVideoFileBean> arrayList){
        int fileNumber = spUtil.getInt("filenumber") ;
        ArrayList<String> fileNameArr = new ArrayList<>() ;
        for(int i = 0 ; i < arrayList.size() ; i++){
            fileNameArr.add(arrayList.get(i).getmName()) ;
        }
        if(fileNumber != -1){
            for(int i =  1 ; i <= fileNumber ; i++){
                String fileName = spUtil.getString("deleteFileName" + i) ;
                if(fileNameArr.contains(fileName)){
                    int index = fileNameArr.indexOf(fileName) ;
                    fileNameArr.remove(index) ;
                    arrayList.remove(index) ;
                }

            }
        }
    }





    //对MyListViewAdapter做初始化
    private void initMyListViewAdapter(){
        myListViewAdapter = new MyListViewAdapter(getActivity() , myListView) ;
        myListViewAdapter.setMyClickListener(new MyListViewAdapter.MyClickListener() {
            EventBusBean eventBusBean = new EventBusBean() ;
            @Override
            public void contentClickListener(int position, String filePath) {
                LocalVideoFileBean localVideoFileBean = (LocalVideoFileBean) myListViewAdapter.getItem(position)  ;
                eventbusPost(eventBusBean , localVideoFileBean);
            }

            @Override
            public void cancelClickListener(int position, String filePath) {
            }

            @Override
            public void deleteClickListener(int position, String filePath) {
                //在Sp中保存删除的文件，可以在扫描完成后的ArrayList中进行去除
                int fileNumber = spUtil.getInt("filenumber") ;
                if(fileNumber == -1){
                    fileNumber = 1 ;
                }else {
                    fileNumber ++ ;
                }
                LocalVideoFileBean localVideoFileBean =(LocalVideoFileBean) myListViewAdapter.getItem(position) ;
                String fileName = localVideoFileBean.getmName() ;
                spUtil.save("filenumber" , fileNumber);
                spUtil.save("deleteFileName" + fileNumber , fileName);
                //清除array中position位置的元素
                arrayList.remove(position) ;
                myListViewAdapter.setArrayList(arrayList);
                myListViewAdapter.notifyDataSetChanged();
                //删除以后让播放器播放下一个视频
                //删除最后一个的时候，避免空指针
                if(position >= arrayList.size()){
                    position = 0 ;
                }
                //删除所有时，避免空指针
                if(arrayList.size() != 0){
                    localVideoFileBean = (LocalVideoFileBean) myListViewAdapter.getItem(position) ;
                    eventbusPost(eventBusBean , localVideoFileBean);
                }
            }
        });
        myListViewAdapter.setArrayList(arrayList);
        myListView.setAdapter(myListViewAdapter);
    }




    //点击按钮发送消息到Activity处理事件
    private void eventbusPost(EventBusBean eventBusBean , LocalVideoFileBean localVideoFileBean){
        eventBusBean.setName(localVideoFileBean.getmName());
        eventBusBean.setPath(localVideoFileBean.getmPath());
        eventBusBean.setTag("1");
        EventBus.getDefault().post(eventBusBean);
    }
}
