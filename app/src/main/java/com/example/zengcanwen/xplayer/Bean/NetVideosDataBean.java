package com.example.zengcanwen.xplayer.Bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by zengcanwen on 2017/12/6.
 */

public class NetVideosDataBean implements Serializable {

    private String title;
    private String preview_url;
    private String video_url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPreview_url() {
        return preview_url;
    }

    public void setPreview_url(String preview_url) {
        this.preview_url = preview_url;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

}
