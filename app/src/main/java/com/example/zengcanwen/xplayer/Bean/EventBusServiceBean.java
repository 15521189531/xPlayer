package com.example.zengcanwen.xplayer.Bean;

/**
 * Created by zengcanwen on 2017/12/21.
 */

public class EventBusServiceBean {
    public static final int DOWNING = 1;
    public static final int PAUSE = 2;
    public static final int FINISH = 3;
    public static final int SENDMESSAGE = 4;
    private int tag;
    private int progress;
    private String url;
    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public EventBusServiceBean() {

    }
}
