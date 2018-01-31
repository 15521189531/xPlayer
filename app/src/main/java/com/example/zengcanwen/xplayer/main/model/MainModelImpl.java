package com.example.zengcanwen.xplayer.main.model;

import android.app.Activity;
import android.app.FragmentTransaction;

import com.example.zengcanwen.xplayer.Bean.EventBusBean;
import com.example.zengcanwen.xplayer.Bean.VideoSaveBean;
import com.example.zengcanwen.xplayer.R;
import com.example.zengcanwen.xplayer.Util.SPUtil;
import com.example.zengcanwen.xplayer.local.fragment.LocalFragment;
import com.example.zengcanwen.xplayer.local.useful.SearchFile;
import com.example.zengcanwen.xplayer.online.fragment.OnlineFragment;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import static com.example.zengcanwen.xplayer.Bean.VideoSaveBean.ONLINE_TAG;

/**
 * 播放界面Activity的model层实现类
 * Created by zengcanwen on 2018/1/30.
 */

public class MainModelImpl implements Mainmodel {

    private Activity mActivity;
    private LocalFragment localFragment;       //本地视频Fragment
    private OnlineFragment mOnlineFragment;     //网络视频Fragment
    private SPUtil mSPUtil;
    private VideoSaveBean mVideoSaveBean;

    public MainModelImpl(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public void init() {
        EventBus.getDefault().register(mActivity);
        mSPUtil = SPUtil.getInstance();
        mVideoSaveBean = new VideoSaveBean();
    }


    @Override
    public VideoSaveBean initVideoData() {
        String path = mSPUtil.getString("path");
        String seektime = mSPUtil.getString("seektime");
        int tag = mSPUtil.getInt("tag");
        String name = mSPUtil.getString("name");
        String alltime = mSPUtil.getString("alltime");
        if (tag == ONLINE_TAG && isOnlineVideoHad(name)) {
            path = SearchFile.FILEPATHBASE + name;
        }
        mVideoSaveBean.setPath(path);
        mVideoSaveBean.setSeektime(seektime);
        mVideoSaveBean.setTag(tag);
        mVideoSaveBean.setAlltime(alltime);

        return mVideoSaveBean;
    }

    @Override
    public void saveVideoData(EventBusBean eventBusBean) {
        mSPUtil.save("path", eventBusBean.getPath());
        mSPUtil.save("name", eventBusBean.getName());
        mSPUtil.save("tag", eventBusBean.getTag());
    }

    @Override
    public void addLocalFragment() {
        android.app.FragmentManager fragmentManager = mActivity.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (localFragment == null) {
            localFragment = new LocalFragment();
            fragmentTransaction.add(R.id.main_framelayout, localFragment);
        }
        hind(fragmentTransaction);
        fragmentTransaction.show(localFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void addOnlineFragment() {
        android.app.FragmentManager fragmentManager = mActivity.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (mOnlineFragment == null) {
            mOnlineFragment = new OnlineFragment();
            fragmentTransaction.add(R.id.main_framelayout, mOnlineFragment);
        }
        hind(fragmentTransaction);
        fragmentTransaction.show(mOnlineFragment);
        fragmentTransaction.commit();
    }

    private void hind(FragmentTransaction fragmentTransaction) {
        if (localFragment != null) {
            fragmentTransaction.hide(localFragment);
        }
        if (mOnlineFragment != null) {
            fragmentTransaction.hide(mOnlineFragment);
        }
    }


    @Override
    public void destory() {
        EventBus.getDefault().unregister(mActivity);
    }

    @Override
    public boolean isOnlineVideoHad(String name) {
        if (null != name) {
            String checkPath = SearchFile.FILEPATHBASE + name;
            File onlineFile = new File(checkPath);
            if (onlineFile.exists()) {
                return true;
            }
        }
        return false;
    }
}
