package com.example.zengcanwen.xplayer.Bean;

/**
 * Created by zengcanwen on 2017/12/7.
 */

public class EventBusBean {
    private String path ;
    private String name ;
    private String tag ;
    private int position ;
    private boolean isFinish ;

    public boolean getIsFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
