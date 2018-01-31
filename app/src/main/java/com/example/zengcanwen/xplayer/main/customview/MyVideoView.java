package com.example.zengcanwen.xplayer.main.customview;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.VideoView;

/**
 * 播放视屏的VideoView的一些自定义操作
 * Created by zengcanwen on 2017/12/1.
 */

public class MyVideoView extends VideoView {

    private PointF mPrePointF;
    private PointF mCurrPointF;

    public interface OnMyTouchListener {
        void onActionDown();

        void onActionMove(float dx);

        void onClick();

        void onActionUp();
    }

    private OnMyTouchListener onMyTouchListener;

    public void setOnMyTouchListener(OnMyTouchListener onMyTouchListener) {
        this.onMyTouchListener = onMyTouchListener;
    }

    public MyVideoView(Context context) {
        this(context, null);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPrePointF = new PointF();
        mCurrPointF = new PointF();
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
        if (widthSize * 3 > heightSize * 4) {
            widthSize = heightSize * 4 / 3;
        } else if (widthSize * 3 < heightSize * 4) {
            heightSize = widthSize * 3 / 4;
        }
        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSize, heightSize);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //记录当前位置
                mPrePointF.set(ev.getX(), ev.getY());
                mCurrPointF.set(ev.getX(), ev.getY());
                //当触摸开始的操作
                onMyTouchListener.onActionDown();
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = ev.getX() - mCurrPointF.x;
                float dy = ev.getY() - mCurrPointF.y;
                //关于X轴的滑动事件
                if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > 5) {
                    onMyTouchListener.onActionMove(dx);

                }

                //更新坐标
                mCurrPointF.set(ev.getX(), ev.getY());
                break;

            case MotionEvent.ACTION_UP:
                float finaldx = ev.getX() - mPrePointF.x;
                float finaldy = ev.getY() - mPrePointF.y;
                //判断是否为点击事件
                if (Math.abs(finaldx) < 5 && Math.abs(finaldy) < 5) {

                    onMyTouchListener.onClick();
                } else {

                    onMyTouchListener.onActionUp();
                }
                break;
            case MotionEvent.ACTION_CANCEL:

                break;
        }

        return true;
    }
}
