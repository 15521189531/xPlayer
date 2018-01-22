package com.example.zengcanwen.xplayer.Bean;

/**
 * Created by zengcanwen on 2017/12/26.
 */

public class VideoSaveBean {
    private String path ;
    private String seektime ;
    private String alltime  ;
    private String progressBar  ;
    private String tag ;
    private int postion  ;
    private String name  ;
    private boolean isDownFinish ;

    public VideoSaveBean() {

    }

    public boolean getDownFinish() {
        return isDownFinish;
    }

    public void setDownFinish(boolean downFinish) {
        isDownFinish = downFinish;
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

    public String getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(String progressBar) {
        this.progressBar = progressBar;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getPostion() {
        return postion;
    }

    public void setPostion(int postion) {
        this.postion = postion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
