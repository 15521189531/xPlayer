package com.example.zengcanwen.xplayer.Bean;

import android.graphics.Bitmap;

import java.nio.file.Path;

/**
 * Created by zengcanwen on 2017/11/28.
 */

public class LocalVideoFileBean {

    private String mPath;
    private Bitmap mBitmap;
    private String mName;
    private long mTime;
    private long size;

    public LocalVideoFileBean() {

    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getmPath() {
        return mPath;
    }

    public void setmPath(String mPath) {
        this.mPath = mPath;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setmBitmap(Bitmap mBitmap) {
        this.mBitmap = mBitmap;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public long getmTime() {
        return mTime;
    }

    public void setmTime(long mTime) {
        this.mTime = mTime;
    }
}
