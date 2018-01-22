package com.example.zengcanwen.xplayer.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.zengcanwen.xplayer.Bean.EventBusBean;
import com.example.zengcanwen.xplayer.Bean.EventBusServiceBean;
import com.example.zengcanwen.xplayer.Bean.NetVideosDataBean;
import com.example.zengcanwen.xplayer.Util.DownListenerUtil;
import com.example.zengcanwen.xplayer.Util.DownManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by zengcanwen on 2017/12/11.
 */

public class MyDownService extends Service {

    private DownManager downManager ;        //下载管理器

    @Override
    public void onCreate() {
        super.onCreate();
        downManager = DownManager.getInstance(this) ;
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NetVideosDataBean netVideosDataBean = (NetVideosDataBean) intent.getSerializableExtra("netViedeosDataBean") ;
        final int position = intent.getIntExtra("position" , -1) ;

        //添加任务
        downManager.addFile(netVideosDataBean, new DownListenerUtil() {
            @Override
            //下载完成
            public void Success() {
                EventBusServiceBean eventBusServiceBean = new EventBusServiceBean() ;
                eventBusServiceBean.setTag(3);
                eventBusServiceBean.setPosition(position);
                EventBus.getDefault().post(eventBusServiceBean);
            }

            @Override
            //正在下载
            public void Downing(int progress) {
                EventBusServiceBean eventBusServiceBean = new EventBusServiceBean() ;
                eventBusServiceBean.setTag(1);
                eventBusServiceBean.setPosition(position);
                eventBusServiceBean.setProgress(progress);
                EventBus.getDefault().post(eventBusServiceBean);
            }

            @Override
            //暂停
            public void pause() {
                EventBusServiceBean eventBusServiceBean = new EventBusServiceBean() ;
                eventBusServiceBean.setTag(2);
                eventBusServiceBean.setPosition(position);
                EventBus.getDefault().post(eventBusServiceBean);
            }
        });

        //正式开启线程下载
        downManager.start(netVideosDataBean.getVideo_url() , position);

        return Service. START_STICKY;
    }



    //接收NetFragment传递过来的消息
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    public void eventHandler(EventBusServiceBean eventBusServiceBean) {
            int tag = eventBusServiceBean.getTag() ;
            if(tag == 4){
                downManager.pause(eventBusServiceBean.getUrl());
            }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
