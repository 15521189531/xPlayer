package com.example.zengcanwen.xplayer.Bean;

/**
 * Created by zengcanwen on 2017/12/26.
 */

public class VideoSaveBean {
    public static final int LOCAL_TAG = 1;
    public static final int ONLINE_TAG = 2;
    private String path;
    private String seektime;
    private String alltime;
    private int tag;

    public VideoSaveBean() {

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSeektime() {
        return seektime;
    }

    public void setSeektime(String seektime) {
        this.seektime = seektime;
    }

    public String getAlltime() {
        return alltime;
    }

    public void setAlltime(String alltime) {
        this.alltime = alltime;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }


}
