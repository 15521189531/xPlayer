package com.example.zengcanwen.xplayer.Fragment;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.zengcanwen.xplayer.Adapter.MynetLvAdapter;
import com.example.zengcanwen.xplayer.Bean.EventBusBean;
import com.example.zengcanwen.xplayer.Bean.EventBusServiceBean;
import com.example.zengcanwen.xplayer.Bean.NetVideosDataBean;
import com.example.zengcanwen.xplayer.R;
import com.example.zengcanwen.xplayer.Service.MyDownService;
import com.example.zengcanwen.xplayer.Util.DownManager;
import com.example.zengcanwen.xplayer.Util.HttpUtil;
import com.example.zengcanwen.xplayer.Util.LoadingUtil;
import com.example.zengcanwen.xplayer.Util.SPUtil;
import com.example.zengcanwen.xplayer.View.MyProgressBarView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by zengcanwen on 2017/12/5.
 */

public class NetFragment extends Fragment {

    private MynetLvAdapter mynetLvAdapter ;
    private ListView listView ;

    private ArrayList<NetVideosDataBean> arrayList ;
    public boolean isDowning = false ;    //判断是否正在下载
    public boolean isDownFinish = false ;  //判断是否已经下载完成
    private DownManager downManager ;             //文件下载任务管理器
    private LoadingUtil loadingUtil ;             //等待对话框
    private boolean isFragmentFinish = false ;            //判断Fragment是否已经结束，进入后台运行状态
    private int preProgress = 0  ;                    //保存上一次记录的进度
    private Handler myHanlder ;

    //在onCreate()和onPause()中才获取和保存信息，提高效率
    private HashMap<Integer , Boolean> isDownignHM ;
    private HashMap<Integer , Integer> progressHM ;
    private HashMap<Integer , Boolean> isFinishHM ;
    private Intent intent ;          //开启服务的Intent

    private LruCache<Integer , Drawable> lruCache ;    //用于缓存从网络得到的图片,使用Drawable的目的是占用空间比Bitmap小
    private int cacheSize ;                           //设置网络图片缓存区的大小

    private int lastFirstVisibleItem  ;
    private HashMap<Integer , Boolean> isHintHM ;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.net_fragment_layout , container , false) ;
         listView = (ListView)view.findViewById(R.id.net_fragment_lv) ;
         EventBus.getDefault().register(this);
         init();
         return view ;
    }



    @Override
    public void onPause() {
        super.onPause();
        //只是在onPause中保存数据肯定时不可靠的 ， 当在后台上运行时，没办法保证可以保存数据
        saveDataFormSp();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        isFragmentFinish = true ;
        //在销毁的时候，只有当全部下载任务结束的时候才停止EventBus和Service
        stopEBandService();
    }

    private void init() {
        arrayList = new ArrayList<>();
        isDownignHM = new HashMap<>();
        progressHM = new HashMap<>();
        isFinishHM = new HashMap<>();
        isHintHM = new HashMap<>() ;

        cacheSize = (int)( Runtime.getRuntime().maxMemory() / 8L) ;      //设置8分之一的缓存空间进行图片缓存
        lruCache = new LruCache<Integer , Drawable >(cacheSize){
            @Override
            protected int sizeOf(Integer key, Drawable value) {
                if(value instanceof BitmapDrawable){
                    Bitmap bitmap = ((BitmapDrawable) value).getBitmap() ;
                    int size =  (bitmap == null) ? 0 : bitmap.getByteCount() ;
                    return size;

                }

                return super.sizeOf(key , value) ;
            }
        } ;

        downManager = DownManager.getInstance(getActivity());
        loadingUtil = new LoadingUtil(getActivity());
        loadingUtil.showProgressDialog();
        MyThread myThread = new MyThread(this) ;
        myThread.start();
        myHanlder = new MyHandler(this) ;
    }

    private static class MyHandler extends Handler{
        private WeakReference<NetFragment> mWeakReference ;
        public MyHandler(NetFragment netFragment){
            mWeakReference = new WeakReference<NetFragment>(netFragment) ;
        }

        @Override
        public void handleMessage(Message msg) {
            NetFragment netFragment = mWeakReference.get() ;
            switch (msg.what){
                case 101:      //获取数据成功
                    netFragment.loadingUtil.cancelProgressDialog();
                    netFragment.getDataFormHM();
                    netFragment.setMynetLvAdapter1();
                    break;

                case 102:      //最终获取数据失败
                    netFragment.loadingUtil.cancelProgressDialog();
                    Toast.makeText(netFragment.getActivity() , "获取数据失败" , Toast.LENGTH_SHORT).show();
                    break;
            }
        }


    }

    //获取服务器数据并解析
    private static class MyThread extends Thread{
        private WeakReference<NetFragment> weakReference ;
        public MyThread(NetFragment netFragment){
            weakReference = new WeakReference<NetFragment>(netFragment) ;
        }

        @Override
        public void run() {
            NetFragment netFragment = weakReference.get() ;
            int resquestTime = 0 ;
            while (resquestTime < 5) {
                String videoDatasJson = edu.xplayerapi.HttpApi.getVideos(netFragment.getActivity());
                try {
                    JSONObject jsonObject = new JSONObject(videoDatasJson);
                    JSONArray jsonArray = jsonObject.getJSONArray("video_list");
                    if (jsonArray == null || jsonArray.length() == 0) {
                        //请求失败
                        resquestTime++;    //如果失败，重复请求5次
                    } else {
                        //请求成功
                        for (int i = 0; i < jsonArray.length(); i++) {
                            NetVideosDataBean netVideosDataBean = new NetVideosDataBean();
                            jsonObject = jsonArray.getJSONObject(i);
                            netVideosDataBean.setTitle(jsonObject.getString("title"));
                            netVideosDataBean.setPreview_url(jsonObject.getString("preview_url"));
                            netVideosDataBean.setVideo_url(jsonObject.getString("video_url"));
                            netFragment.arrayList.add(netVideosDataBean);
                        }
                        netFragment.myHanlder.sendMessage(netFragment.myHanlder.obtainMessage(101));    //请求成功
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //最终请求失败
            netFragment.myHanlder.sendMessage(netFragment.myHanlder.obtainMessage(102)) ;
        }
    }



    private void setMynetLvAdapter1() {
        mynetLvAdapter = new MynetLvAdapter(getActivity()){
            @Override
            public void updataPic(String url, ImageView imageView, int position) {
                loadNetPic(url , imageView , position);
            }

            @Override
            public void initProgress(MyProgressBarView myProgressBarView , int position) {
                int nowProgress = progressHM.get(position) ;
                if(nowProgress == 0){
                    updateProgressBar("", 0, myProgressBarView);

                }else if(nowProgress == 100){
                    updateProgressBar("下载完成", 100, myProgressBarView);

                }else {
                    updateProgressBar("暂停", nowProgress, myProgressBarView);
                }
            }
        };
        mynetLvAdapter.setArrayList(arrayList);
        mynetLvAdapter.setMyClickListener(new MynetLvAdapter.MyViewListener() {
            @Override
            public void progressClick(View v, int position, String url) {

                isDownFinish = isFinishHM.get(position) ;

                if(isDownFinish){
                    Toast.makeText(getActivity() , "已经下载完成" , Toast.LENGTH_SHORT).show();
                    return;
                }
                //点击下载按钮
                final NetVideosDataBean netVideosDataBean = (NetVideosDataBean)mynetLvAdapter.getItem(position);
                isDowning = isDownignHM.get(position) ;
                if (isDowning) {

                    //暂停
                    EventBusServiceBean eventBusServiceBean = new EventBusServiceBean() ;
                    eventBusServiceBean.setTag(4);
                    eventBusServiceBean.setUrl(netVideosDataBean.getVideo_url());
                    EventBus.getDefault().post(eventBusServiceBean);

                } else {

                    //下载
                    intent = new Intent(getActivity() , MyDownService.class) ;
                    intent.putExtra("netViedeosDataBean" , netVideosDataBean) ;
                    intent.putExtra("position" , position) ;
                    getActivity().startService(intent) ;
                }
            }

            @Override
            public void LlClick(int position, String url) {
                // 点击LinearLayout
                EventBusBean eventBusBean = new EventBusBean();
                NetVideosDataBean netVideosDataBean = arrayList.get(position);
                eventBusBean.setName(netVideosDataBean.getTitle());
                eventBusBean.setPath(netVideosDataBean.getVideo_url());
                eventBusBean.setTag("2");
                eventBusBean.setPosition(position);
                eventBusBean.setFinish(isFinishHM.get(position));
                EventBus.getDefault().post(eventBusBean);
            }
        });

        //Item被回收，停止更新进度
        listView.setRecyclerListener(new AbsListView.RecyclerListener() {
            @Override
            public void onMovedToScrapHeap(View view) {
                MyProgressBarView myProgressBarView = view.findViewById(R.id.my_progress_bar_view) ;
                int position = (Integer)myProgressBarView.getTag() ;
                isHintHM.put(position , true) ;
            }
        });

        //Item重新出现到界面，继续更新进度
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem > lastFirstVisibleItem){  //向下滑
                    int position = firstVisibleItem + visibleItemCount -1  ;
                    isHintHM.put(position , false ) ;

                    lastFirstVisibleItem = firstVisibleItem ;
                }else if(firstVisibleItem < lastFirstVisibleItem){  //向下滑
                    int position = firstVisibleItem ;
                    isHintHM.put(position , false ) ;

                    lastFirstVisibleItem = firstVisibleItem ;
                }

            }
        });
        listView.setAdapter(mynetLvAdapter);
    }


    //处理下载视频的回调
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    public void eventHandler(EventBusServiceBean eventBusServiceBean) {
        int tag = eventBusServiceBean.getTag() ;
        int position = eventBusServiceBean.getPosition() ;
        MyProgressBarView myProgressBarView = mynetLvAdapter.getMyProgressBarView(position) ;


        if(tag == 1){  //正在下载
            int progress = eventBusServiceBean.getProgress() ;
            Boolean isHint = isHintHM.get(position) ;
            if(isHint == null || !isHint) {
                updateProgressBar("正在下载", progress, myProgressBarView);
            }
            if(!isFragmentFinish){
                progressHM.put(position , progress) ;
                isDownignHM.put(position , true ) ;
            }else{
                //每前进8个单位记录一次,避免进程被杀死,后台下载两种情况的数据丢失
                        if(progress - preProgress >= 10){
                            SPUtil.getInstance().save("progress" + position , progress);
                            preProgress = progress ;
                        }
            }


        }else if(tag == 2){  //暂停
            Boolean isHint = isHintHM.get(position) ;
            if(isHint == null || !isHint) {
                updateProgressBar("暂停", -1, myProgressBarView);
            }
            isDownignHM.put(position , false) ;

        }else if(tag == 3){  //完成
            Boolean isHint = isHintHM.get(position) ;
            if(isHint == null || !isHint){
                updateProgressBar("下载完成", 100, myProgressBarView );
            }
            progressHM.put(position , 100) ;
            isDownignHM.put(position , false) ;
            isFinishHM.put(position , true) ;
            if(isFragmentFinish){
                //直接保存进度到SP,防止进程被杀死时数据丢失
                SPUtil.getInstance().save("progress" + position , 100);
                SPUtil.getInstance().save("isFinish" + position , true);
                //当界面销毁并且没有在下载东西之后关闭EventBus和Service
                stopEBandService();
            }

        }else {
            return;
        }
    }




    //更新进度条
    private void updateProgressBar(String status , int progress , MyProgressBarView myProgressBarView ){
        if(null != status){
            myProgressBarView.setTextStr(status);
        }
        if(progress != -1){
            myProgressBarView.setProgress(progress);
        }
    }


    //加载网络图片
    private void loadNetPic(String url , final ImageView imageView , final int position){
        //先判断缓存区是否已经存在该对象
        Drawable drawable = lruCache.get(position) ;
        if(drawable != null){
            imageView.setImageBitmap(((BitmapDrawable)drawable).getBitmap());
            return;
        }

        //没有就去网络获取
        HttpUtil.getInstance().getHttp(url, null, new HttpUtil.OkhttpResponse() {
            @Override
            public void onError(Call call, IOException e) {
                Toast.makeText(getActivity() , "加载图片失败" , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) {
                InputStream inputStream = response.body().byteStream() ;
                //简单的图片压缩
                BitmapFactory.Options options = new BitmapFactory.Options() ;
                options.inSampleSize = 2 ;
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream , null , options ) ;
                //加入缓存
                lruCache.put(position , new BitmapDrawable(bitmap)) ;
                myHanlder.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(bitmap);
                    }
                }) ;
            }
        });
    }

    //判断下载任务是否全部结束，然后结束EventBus和Service
    private void stopEBandService(){
        if (isDownignHM != null && isDownignHM.size() != 0) {
            Iterator<Map.Entry<Integer, Boolean>> iterator = isDownignHM.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Boolean> entry = iterator.next();
                if(entry.getValue()){
                    return;
                }
            }
            EventBus.getDefault().unregister(this);
            if(intent != null){
                getActivity().stopService(intent) ;
            }
        }
    }


    //保存信息到SP
    private void saveDataFormSp(){

        if (progressHM != null && progressHM.size() != 0) {
            Iterator<Map.Entry<Integer, Integer>> iterator = progressHM.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Integer> entry = iterator.next();
                SPUtil.getInstance().save("progress" + entry.getKey() , entry.getValue());
            }
        }

        if (isFinishHM != null && isFinishHM.size() != 0) {
            Iterator<Map.Entry<Integer, Boolean>> iterator = isFinishHM.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Boolean> entry = iterator.next();
                SPUtil.getInstance().save("isFinish" + entry.getKey() , entry.getValue());
            }
        }

    }

    //从Sp中获取数据到HashMap
    private void  getDataFormHM(){
        for(int i = 0 ; i < arrayList.size() ; i++) {
            if (isDownignHM != null) {
                isDownignHM.put(i, false);
            }

            if(isFinishHM != null ){
                boolean isFinish = SPUtil.getInstance().getBoolean("isFinish" + i) ;
                isFinishHM.put(i , isFinish) ;
            }

            if(progressHM != null ){
                int progress = SPUtil.getInstance().getInt("progress" + i) ;
                if(progress == -1){
                    progress = 0 ;
                }
                progressHM.put(i , progress) ;
            }
        }
    }
}
