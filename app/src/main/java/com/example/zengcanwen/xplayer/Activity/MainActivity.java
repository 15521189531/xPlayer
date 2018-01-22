package com.example.zengcanwen.xplayer.Activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zengcanwen.xplayer.Bean.EventBusBean;
import com.example.zengcanwen.xplayer.Bean.VideoSaveBean;
import com.example.zengcanwen.xplayer.Fragment.LocalFragment;
import com.example.zengcanwen.xplayer.Fragment.NetFragment;
import com.example.zengcanwen.xplayer.R;
import com.example.zengcanwen.xplayer.Util.DipandPxUtli;
import com.example.zengcanwen.xplayer.Util.FileUtil;
import com.example.zengcanwen.xplayer.Util.PremissionUtil;
import com.example.zengcanwen.xplayer.Util.SPUtil;
import com.example.zengcanwen.xplayer.Util.TimeUtil;
import com.example.zengcanwen.xplayer.View.MyVideoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MyVideoView videoView ;
    private Button allButton ;
    private Button platerButton ;
    private SeekBar seekBar ;
    private LinearLayout progressLl ;
    private TextView hadPlayTimeTv ;
    private TextView allPlayTimeTv ;
    private TextView localTv ;
    private TextView netTv ;
    private boolean isDragSeekBar = false ;  //判断是否正在拖动
    private boolean isPlayer = false ;    //保存前一次是否正在播放
    private boolean touchIsPlayer = false ;  //保存触摸屏幕前的视频播放状态

    private Point prePoint ;               //一开始手指点击的坐标
    private Point currPoint ;              //当前手指的坐标
    private int clickSum ;                 //记录3S内连续点击屏幕的次数
    private boolean stretch_flag = true ;       //判断当前是否为竖屏

    private Display display ;

    private LocalFragment localFragment ;       //本地视频Fragment
    private  NetFragment netFragment ;     //网络视频Fragment
    private SPUtil spUtil ;                //保存数据
    private EventBusBean eventBusBean ;    //用于保存点击后传过来的信息
    private VideoSaveBean videoSaveBean ;  //videoView播放时的数据
    private MyHandler myHandler ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_main_layout);
        //EventBus上车
        EventBus.getDefault().register(this);
        init();
        getVideoData();
        setVideoView();
        videoViewListener();
}

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setVideoViewWhenChanged();
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveVideoData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //EventBus下车
        EventBus.getDefault().unregister(this);
        videoView = null ;
        seekBar = null ;
    }


    //初始化
    private void  init(){
            videoView = (MyVideoView)findViewById(R.id.video_view) ;
            allButton = (Button)findViewById(R.id.all_b) ;
            platerButton = (Button)findViewById(R.id.play_b) ;
            seekBar = (SeekBar)findViewById(R.id.seek_bar) ;
            progressLl = (LinearLayout)findViewById(R.id.progress_ll) ;
            hadPlayTimeTv = (TextView)findViewById(R.id.had_play_time_tv) ;
            allPlayTimeTv = (TextView)findViewById(R.id.all_play_time_tv) ;
            localTv = (TextView)findViewById(R.id.local_tv) ;
            netTv = (TextView)findViewById(R.id.net_tv) ;
            platerButton.setOnClickListener(this);
            allButton.setOnClickListener(this);
            localTv.setOnClickListener(this);
            netTv.setOnClickListener(this);
            eventBusBean = new EventBusBean() ;
            addlocalFragment();
            prePoint = new Point() ;
            currPoint = new Point() ;
            display = getWindowManager().getDefaultDisplay() ;
            spUtil = SPUtil.getInstance() ;
            videoSaveBean = new VideoSaveBean() ;
            myHandler = new MyHandler(this) ;
    }


    private static class MyHandler extends Handler{
        private WeakReference<MainActivity> mWeakReference ;
        public MyHandler(MainActivity mainActivity){
            mWeakReference = new WeakReference<MainActivity>(mainActivity) ;
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity = mWeakReference.get() ;
            switch (msg.what){
                case 101:        //正在播放时会发出消息，并且200ms发送一次，用于更新seekBar
                    if(mainActivity.videoView.isPlaying()  && !mainActivity.isDragSeekBar) {
                        mainActivity.seekBar.setProgress((int)((float)mainActivity.videoView.getCurrentPosition() * 100f / (float)mainActivity.videoView.getDuration()));
                        mainActivity.myHandler.sendMessageDelayed(mainActivity.myHandler.obtainMessage(101), 200);
                    }
                    break;

                case 102:
                    if(msg.arg1 == mainActivity.clickSum){
                        mainActivity.allButton.setVisibility(View.GONE);
                        mainActivity.clickSum = 0 ;
                    }
            }
        }
    }
    //获取播放时的数据到VideoSaveBean中
    private void getVideoData(){
        String path = spUtil.getString("path");
        String seektime = spUtil.getString("seektime");
        String alltime = spUtil.getString("alltime") ;
        String progressBar = spUtil.getString("progress") ;
        String tag = spUtil.getString("tag") ;
        int postion = spUtil.getInt("position") ;
        String name = spUtil.getString("name") ;
        boolean isDownFinish =  spUtil.getBoolean("isDownFinish" + postion)  ;
        if(path != null){
            videoSaveBean.setPath(path);
        }if(seektime != null){
            videoSaveBean.setSeektime(seektime);
        }if(alltime != null){
            videoSaveBean.setAlltime(alltime);
        }if(progressBar != null){
            videoSaveBean.setProgressBar(progressBar);
        }if(tag != null){
            videoSaveBean.setTag(tag);
        }if(name != null ){
            videoSaveBean.setName(name);
        }
        videoSaveBean.setPostion(postion);
        videoSaveBean.setDownFinish(isDownFinish);
    }

    //对videoView的初始设置
    private void setVideoView() {
        String path = videoSaveBean.getPath() ;
        if(videoSaveBean.getTag() != null
                && videoSaveBean.getTag().equals("2")
                && videoSaveBean.getDownFinish()){
            path = FileUtil.FILEPATHBASE + videoSaveBean.getPath() ;
            videoSaveBean.setPath(path);
        }
        if (path != null && videoSaveBean.getSeektime() != null
                && videoSaveBean.getAlltime() != null
                && videoSaveBean.getProgressBar() != null) {
            videoView.setVideoPath(path);
            int seek = Integer.valueOf(videoSaveBean.getSeektime()) ;
            int all = Integer.valueOf(videoSaveBean.getAlltime()) ;
            int progress = Integer.valueOf(videoSaveBean.getProgressBar()) ;
            videoView.seekTo(seek);
            hadPlayTimeTv.setText(TimeUtil.formatTime(seek));
            allPlayTimeTv.setText(TimeUtil.formatTime(all));
            seekBar.setProgress(progress);
        }
    }


    //保存数据
    private  void  saveVideoData(){
        spUtil.save("path" , eventBusBean.getPath());
        spUtil.save("name" , eventBusBean.getName());
        spUtil.save("tag" , eventBusBean.getTag());
        spUtil.save("position" , eventBusBean.getPosition());
        spUtil.save("seektime" , String.valueOf(videoView.getCurrentPosition()));
        spUtil.save("alltime" , String.valueOf(videoView.getDuration()));
        spUtil.save("progress" , String.valueOf(seekBar.getProgress()));
    }







    //videoView的一些回调
    private void videoViewListener(){
        //准备回调
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                myHandler.sendMessage(myHandler.obtainMessage(101));
            }
        });





        //完成回调
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                videoView.resume();
                platerButton.setText("播放");
                seekBar.setProgress(100);
            }
        });






        //触摸屏幕回调
        videoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //记录当前位置
                        prePoint.set((int) motionEvent.getX(), (int) motionEvent.getY());
                        currPoint.set((int) motionEvent.getX(), (int) motionEvent.getY());
                        touchIsPlayer = videoView.isPlaying() ;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) (motionEvent.getX() - currPoint.x);
                        int dy = (int) (motionEvent.getY() - currPoint.y);
                        if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > 5) {
                            //关于X轴的滑动事件
                            if(touchIsPlayer){
                                videoView.pause();
                            }
                            int seekTime = (int) ((float) videoView.getDuration() / 1000.0f * (float) dx) + videoView.getCurrentPosition();
                            if (seekTime < 0) {

                                updataVideoView(0 , "0" , 0);

                            } else if (seekTime > videoView.getDuration()) {

                                updataVideoView(videoView.getDuration() ,
                                        TimeUtil.formatTime(videoView.getDuration()) ,
                                        100);

                            } else {

                                updataVideoView(seekTime ,
                                        TimeUtil.formatTime(seekTime) ,
                                        (int) ((float) seekTime * 100f / (float) videoView.getDuration()) );
                            }
                        }
                        allPlayTimeTv.setText(TimeUtil.formatTime(videoView.getDuration()));
                        progressLl.setVisibility(View.VISIBLE);

                        //更新坐标
                        currPoint.set((int) motionEvent.getX(), (int) motionEvent.getY());
                        break;

                    case MotionEvent.ACTION_UP:

                        int finaldx = (int) (motionEvent.getX() - prePoint.x);
                        int finaldy = (int) (motionEvent.getY() - prePoint.y);

//                        判断是否为点击事件
                        if (Math.abs(finaldx) < 5 && Math.abs(finaldy) < 5) {
                            //处理3S内连续点击屏幕时的特殊情况
                            allButton.setVisibility(View.VISIBLE);
                            clickSum++;
                            int thisClickSum = clickSum;
                            Message message = new Message();
                            message.what = 102;
                            message.arg1 = thisClickSum;
                            myHandler.sendMessageDelayed(message, 3000);
                        }else {
                            if (videoView.getCurrentPosition() != videoView.getDuration()) {
                                if(touchIsPlayer){
                                    videoView.start();
                                    myHandler.sendMessage(myHandler.obtainMessage(101));
                                }
                            } else {
                                videoView.seekTo(0);
                            }
                            progressLl.setVisibility(View.GONE);
                        }
                            break;

                            case MotionEvent.ACTION_CANCEL:
                                Log.i("aaaaaa", "ACTION_CANCEL");
                                break;
                        }
                        return true;
                }
        });




        //seekBar滑动回调
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b){
                    videoView.seekTo( (int) ((float)videoView.getDuration() / 100f * (float) i));
                    hadPlayTimeTv.setText(TimeUtil.formatTime( (int) ((float)videoView.getDuration() / 100f * (float) i)));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDragSeekBar = true ;
                isPlayer = videoView.isPlaying() ;
                //拖动的时候先暂停视频播放，避免卡顿现象
                if(isPlayer){
                    videoView.pause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDragSeekBar = false ;
                //回到拖动前的状态
                if(isPlayer){
                    videoView.start();
                }
                myHandler.sendMessage(myHandler.obtainMessage(101)) ;

            }
        });
    }






    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //播放 , 暂停按钮
            case R.id.play_b:
                if(videoView.isPlaying()){
                    platerButton.setText("播放");
                    progressLl.setVisibility(View.VISIBLE);
                    hadPlayTimeTv.setText(TimeUtil.formatTime(videoView.getCurrentPosition()));
                    allPlayTimeTv.setText(TimeUtil.formatTime(videoView.getDuration()));
                    videoView.pause();

                }else if(!videoView.isPlaying()){
                    platerButton.setText("暂停");
                    progressLl.setVisibility(View.GONE);
                    videoView.start();
                    myHandler.sendMessage(myHandler.obtainMessage(101)) ;
                }
                else {
                    Toast.makeText(MainActivity.this , "操作出错" , Toast.LENGTH_SHORT).show();
                }
                break ;

            //全屏按钮
            case R.id.all_b:
                if(stretch_flag){
                    stretch_flag = false ;
                    //横向
                    MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }else {
                    stretch_flag = true ;
                    //竖向
                    MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;

                //本地按钮
            case R.id.local_tv :
                addlocalFragment();
                localTv.setBackgroundColor(getResources().getColor(R.color.localTvColor));
                netTv.setBackgroundColor(getResources().getColor(R.color.netTvColor));
                break;

                //在线按钮
            case R.id.net_tv :
                addnetFragment();
                localTv.setBackgroundColor(getResources().getColor(R.color.netTvColor));
                netTv.setBackgroundColor(getResources().getColor(R.color.localTvColor));
                break;
        }
  }



  private void updataVideoView(int seekTo  , String time , int progress){
      videoView.seekTo(seekTo);
      hadPlayTimeTv.setText(time);
      seekBar.setProgress(progress);
  }


  //显示localFragment
    private void addlocalFragment(){
        android.app.FragmentManager fragmentManager = getFragmentManager() ;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction() ;

        if(localFragment == null){
            localFragment = new LocalFragment() ;
            fragmentTransaction.add(R.id.main_framelayout , localFragment) ;
        }
        hind(fragmentTransaction);
        fragmentTransaction.show(localFragment) ;
        fragmentTransaction.commit() ;
    }





    //显示netFragment
    private void addnetFragment(){
        android.app.FragmentManager fragmentManager = getFragmentManager() ;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction() ;

        if(netFragment == null){
            netFragment = new NetFragment() ;
            fragmentTransaction.add(R.id.main_framelayout , netFragment) ;
        }
        hind(fragmentTransaction);
        fragmentTransaction.show(netFragment) ;
        fragmentTransaction.commit() ;
    }




    //隐藏所有的Fragment
    private void hind(FragmentTransaction fragmentTransaction){
        if(localFragment != null){
            fragmentTransaction.hide(localFragment) ;
        }
        if(netFragment != null){
            fragmentTransaction.hide(netFragment) ;
        }
    }





    //横竖屏切换时对VideoView进行处理
    private void setVideoViewWhenChanged(){
        int screenWidth = display.getWidth() ;
        int screenHeight = display.getHeight() ;
        videoView.seekTo(videoView.getCurrentPosition());
        seekBar.setProgress((int)((float)videoView.getCurrentPosition() * 100f / (float)videoView.getDuration()));
        if(stretch_flag){
            //切换到竖屏时的操作
            FrameLayout.LayoutParams layoutParams =  (FrameLayout.LayoutParams)videoView.getLayoutParams() ;
            layoutParams.height = DipandPxUtli.dip2px(this , 200) ;
            layoutParams.width = DipandPxUtli.dip2px(this , 267) ;
            videoView.setLayoutParams(layoutParams);
            allButton.setText("横屏");
        }else {
            //切换到横屏时的操作
            FrameLayout.LayoutParams layoutParams =  (FrameLayout.LayoutParams)videoView.getLayoutParams() ;
            layoutParams.height = screenHeight ;
            layoutParams.width = screenWidth ;
            videoView.setLayoutParams(layoutParams);
            allButton.setText("竖屏");
        }
    }




    //接收点击事件传递过来来的消息
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    public void eventHandler(EventBusBean eventBusBean){
        //设置播放内容
        if(eventBusBean.getTag() != null && eventBusBean.getTag().equals("2")){
            if(eventBusBean.getIsFinish()){     //如果下载完成，直接播放下载好的视频
                videoView.setVideoPath(FileUtil.FILEPATHBASE + eventBusBean.getName());
            }else {
                videoView.setVideoPath(eventBusBean.getPath());
            }
        }else {
            videoView.setVideoPath(eventBusBean.getPath());
        }
        videoView.seekTo(1);
        seekBar.setProgress(0);
        //保存当前对象，可以保存内容播放内容
        this.eventBusBean = eventBusBean ;
    }


}
