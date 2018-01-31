package com.example.zengcanwen.xplayer.View;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.VideoView;

import java.lang.invoke.MethodHandles;

/**
 * Created by zengcanwen on 2017/12/1.
 */

public class MyVideoView extends VideoView {

    public MyVideoView(Context context) {
        this(context , null);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    //重写onMeasure,使视频播放比例为  宽：高 = 4:3
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //两种情况： 1. 如果widthSize * 3 > heightSize *4 , 则缩小宽度
        //          2. 如果widthSize * 3 < heightSize *4 , 则缩小高度
        if(widthSize * 3 > heightSize *4 ){
            widthSize = heightSize * 4 / 3 ;
        }else if(widthSize * 3 < heightSize *4){
            heightSize = widthSize * 3 / 4 ;
        }
        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSize, heightSize);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

}
