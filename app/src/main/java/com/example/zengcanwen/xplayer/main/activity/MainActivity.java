package com.example.zengcanwen.xplayer.main.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zengcanwen.xplayer.Bean.EventBusBean;
import com.example.zengcanwen.xplayer.Bean.VideoSaveBean;
import com.example.zengcanwen.xplayer.R;
import com.example.zengcanwen.xplayer.Util.DipandPxUtli;
import com.example.zengcanwen.xplayer.Util.SPUtil;
import com.example.zengcanwen.xplayer.Util.TimeUtil;
import com.example.zengcanwen.xplayer.main.customview.MyVideoView;
import com.example.zengcanwen.xplayer.main.presenter.MainPresenterImpl;
import com.example.zengcanwen.xplayer.main.view.MainView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.example.zengcanwen.xplayer.Bean.VideoSaveBean.LOCAL_TAG;
import static com.example.zengcanwen.xplayer.Bean.VideoSaveBean.ONLINE_TAG;
import static com.example.zengcanwen.xplayer.main.presenter.MainPresenterImpl.SHOWBUTTON;
import static com.example.zengcanwen.xplayer.main.presenter.MainPresenterImpl.SYNCSEEKBAR;

/**
 * 首界面Activity,播放视频的Activity
 * Created by zengcanwen on 2018/1/30.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, MainView {

    private MyVideoView mVideoView;
    private Button mAllButton;
    private Button mPlaterButton;
    private SeekBar mSeekBar;
    private LinearLayout mProgressLl;
    private TextView mHadPlayTimeTv;
    private TextView mAllPlayTimeTv;
    private TextView mLocalTv;
    private TextView mOnlineTv;

    private boolean isDragSeekBar = false;  //判断是否正在拖动
    private boolean isPlayer = false;    //保存前一次是否正在播放
    private boolean touchIsPlayer = false;  //保存触摸屏幕前的视频播放状态
    private int mClickSum = 0;             //记录三秒内连续点击屏幕的次数
    private boolean stretch_flag = true;          //判断横竖屏状态
    private Display mDisplay;                      //屏幕参数

    private MainPresenterImpl mMainPresenter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.action_main_layout);
        mMainPresenter = new MainPresenterImpl(this, this);
        init();
    }

    @Override
    public void init() {
        mVideoView = findViewById(R.id.video_view);
        mAllButton = findViewById(R.id.all_b);
        mPlaterButton = findViewById(R.id.play_b);
        mSeekBar = findViewById(R.id.seek_bar);
        mProgressLl = findViewById(R.id.progress_ll);
        mHadPlayTimeTv = findViewById(R.id.had_play_time_tv);
        mAllPlayTimeTv = findViewById(R.id.all_play_time_tv);
        mLocalTv = findViewById(R.id.local_tv);
        mOnlineTv = findViewById(R.id.net_tv);
        mPlaterButton.setOnClickListener(this);
        mAllButton.setOnClickListener(this);
        mLocalTv.setOnClickListener(this);
        mOnlineTv.setOnClickListener(this);
        mDisplay = getWindowManager().getDefaultDisplay();

        mVideoView.setOnMyTouchListener(new MyVideoView.OnMyTouchListener() {
            @Override
            public void onActionDown() {
                touchIsPlayer = mVideoView.isPlaying();
            }

            @Override
            public void onActionMove(float dx) {
                //关于X轴的滑动事件
                if (touchIsPlayer) {
                    mVideoView.pause();
                }
                int seekTime = (int) ((float) mVideoView.getDuration() / 1000.0f * dx) + mVideoView.getCurrentPosition();
                if (seekTime < 0) {

                    mMainPresenter.updataVideoView(0, "0", 0);

                } else if (seekTime > mVideoView.getDuration()) {

                    mMainPresenter.updataVideoView(mVideoView.getDuration(),
                            TimeUtil.formatTime(mVideoView.getDuration()),
                            100);

                } else {
                    mMainPresenter.updataVideoView(seekTime,
                            TimeUtil.formatTime(seekTime),
                            (int) ((float) seekTime * 100f / (float) mVideoView.getDuration()));
                }
                mAllPlayTimeTv.setText(TimeUtil.formatTime(mVideoView.getDuration()));
                mProgressLl.setVisibility(View.VISIBLE);
            }

            @Override
            public void onClick() {
                //处理3S内连续点击屏幕时的特殊情况
                mAllButton.setVisibility(View.VISIBLE);
                mClickSum++;
                int thisClickSum = mClickSum;
                Message message = new Message();
                message.what = SHOWBUTTON;
                message.arg1 = thisClickSum;
                mMainPresenter.sendMessageDelayed(message, 3000);
            }

            @Override
            public void onActionUp() {
                if (mVideoView.getCurrentPosition() != mVideoView.getDuration()) {
                    if (touchIsPlayer) {
                        mVideoView.start();
                        Message message = new Message();
                        message.what = SYNCSEEKBAR;
                        mMainPresenter.sendMessage(message);
                    }
                } else {
                    mVideoView.seekTo(0);
                }
                mProgressLl.setVisibility(View.GONE);
            }

        });

        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Message message = new Message();
                message.what = SYNCSEEKBAR;
                mMainPresenter.sendMessage(message);
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.resume();
                mPlaterButton.setText("播放");
                mSeekBar.setProgress(100);
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mVideoView.seekTo((int) ((float) mVideoView.getDuration() / 100f * (float) progress));
                    mHadPlayTimeTv.setText(TimeUtil.formatTime((int) ((float) mVideoView.getDuration() / 100f * (float) progress)));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isDragSeekBar = true;
                isPlayer = mVideoView.isPlaying();
                //拖动的时候先暂停视频播放，避免卡顿现象
                if (isPlayer) {
                    mVideoView.pause();
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isDragSeekBar = false;
                //回到拖动前的状态
                if (isPlayer) {
                    mVideoView.start();
                }
                Message message = new Message();
                message.what = SYNCSEEKBAR;
                mMainPresenter.sendMessage(message);
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //播放 , 暂停按钮
            case R.id.play_b:
                if (mVideoView.isPlaying()) {          //正在播放
                    mPlaterButton.setText("播放");
                    mProgressLl.setVisibility(View.VISIBLE);
                    mHadPlayTimeTv.setText(TimeUtil.formatTime(mVideoView.getCurrentPosition()));
                    mAllPlayTimeTv.setText(TimeUtil.formatTime(mVideoView.getDuration()));
                    mVideoView.pause();

                } else if (!mVideoView.isPlaying()) {        //正在暂停
                    mPlaterButton.setText("暂停");
                    mProgressLl.setVisibility(View.GONE);
                    mVideoView.start();
                    Message message = new Message();
                    message.what = SYNCSEEKBAR;
                    mMainPresenter.sendMessage(message);
                } else {
                    Toast.makeText(MainActivity.this, "操作出错", Toast.LENGTH_SHORT).show();
                }
                break;

            //全屏按钮
            case R.id.all_b:
                if (stretch_flag) {
                    stretch_flag = false;
                    //横向
                    MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    stretch_flag = true;
                    //竖向
                    MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;

            //本地按钮
            case R.id.local_tv:
                mMainPresenter.localTvClick();
                mLocalTv.setBackgroundColor(getResources().getColor(R.color.localTvColor));
                mOnlineTv.setBackgroundColor(getResources().getColor(R.color.netTvColor));
                break;

            //在线按钮
            case R.id.net_tv:
                mMainPresenter.onlineTvClick();
                mLocalTv.setBackgroundColor(getResources().getColor(R.color.netTvColor));
                mOnlineTv.setBackgroundColor(getResources().getColor(R.color.localTvColor));
                break;
        }
    }

    //接收点击事件传递过来来的消息
    @Subscribe(threadMode = ThreadMode.MAIN, priority = 100)
    public void eventHandler(EventBusBean eventBusBean) {
        mMainPresenter.eventHandler(eventBusBean);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int screenWidth = mDisplay.getWidth();
        int screenHeight = mDisplay.getHeight();
        mVideoView.seekTo(mVideoView.getCurrentPosition());
        mSeekBar.setProgress((int) ((float) mVideoView.getCurrentPosition() * 100f / (float) mVideoView.getDuration()));
        if (stretch_flag) {
            //切换到竖屏时的操作
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mVideoView.getLayoutParams();
            layoutParams.height = DipandPxUtli.dip2px(this, 200);
            layoutParams.width = DipandPxUtli.dip2px(this, 267);
            mVideoView.setLayoutParams(layoutParams);
            mAllButton.setText("横屏");
        } else {
            //切换到横屏时的操作
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mVideoView.getLayoutParams();
            layoutParams.height = screenHeight;
            layoutParams.width = screenWidth;
            mVideoView.setLayoutParams(layoutParams);
            mAllButton.setText("竖屏");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMainPresenter.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView = null;
        mSeekBar = null;
        mMainPresenter.destory();
    }

    @Override
    public void updataVideoView(int seekTo, String time, int progress) {
        mVideoView.seekTo(seekTo);
        mHadPlayTimeTv.setText(time);
        mSeekBar.setProgress(progress);
    }

    @Override
    public boolean syncSeekBar() {
        if (mVideoView.isPlaying() && !isDragSeekBar) {
            mSeekBar.setProgress((int) ((float) mVideoView.getCurrentPosition() * 100f / (float) mVideoView.getDuration()));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void showAllButton(Message msg) {
        if (msg.arg1 == mClickSum) {
            mAllButton.setVisibility(View.GONE);
            mClickSum = 0;
        }
    }

    @Override
    public void saveSeekTime() {
        SPUtil.getInstance().save("seektime", String.valueOf(mVideoView.getCurrentPosition()));
        SPUtil.getInstance().save("alltime", String.valueOf(mVideoView.getDuration()));
    }

    @Override
    public void eventBusHandler(int tag, String path) {
        if (tag == ONLINE_TAG || tag == LOCAL_TAG) {
            mVideoView.setVideoPath(path);
            mVideoView.seekTo(1);
            mSeekBar.setProgress(0);
        }
    }

    @Override
    public void initVideoView(VideoSaveBean videoSaveBean) {
        String path = videoSaveBean.getPath();
        String seektime = videoSaveBean.getSeektime();
        String alltimeStr = videoSaveBean.getAlltime();
        if (null == path || null == seektime || null == alltimeStr) return;
        int seek = Integer.valueOf(seektime);
        int alltime = Integer.valueOf(alltimeStr);
        mVideoView.setVideoPath(path);
        mVideoView.seekTo(Integer.valueOf(seektime));
        mHadPlayTimeTv.setText(TimeUtil.formatTime(seek));
        mAllPlayTimeTv.setText(TimeUtil.formatTime(alltime));
        int progress = (int) ((float) seek * 100f / (float) alltime);
        mSeekBar.setProgress(progress);
    }
}
