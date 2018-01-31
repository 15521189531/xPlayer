package com.example.zengcanwen.xplayer.local.model;

import com.example.zengcanwen.xplayer.Bean.LocalVideoFileBean;

/**
 * 本地列表Fragment中model层的接口，辅助modelImp实现功能。
 * Created by zengcanwen on 2018/1/25.
 */

public interface LocalListener {

    LocalVideoFileBean getLocalVideoFileBean(int position);

}
