package com.example.zengcanwen.xplayer.Util;

import android.content.Context;

/**
 * Created by zengcanwen on 2017/11/30.
 */

public class DipandPxUtli {

    public static int  dip2px(Context context , float dip ){
        final float scale = context.getResources().getDisplayMetrics().density ;
        return (int) (dip * scale  + 0.5f) ;
    }

    public static int px2dip(Context context , float dx){
        final float scale = context.getResources().getDisplayMetrics().density ;
        return (int) (dx / scale  + 0.5f) ;
    }
}
