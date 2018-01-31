package com.example.zengcanwen.xplayer.local.customview;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

import com.example.zengcanwen.xplayer.local.customview.MyItemListView;

/**
 * 自定义ListView , 实现ListView侧滑菜单
 * Created by zengcanwen on 2017/11/27.
 */


public class MyListView extends ListView {

    private float currX = 0f;    //当前手指的X位置
    private int dx = 0;          //手指移动的X位置
    private float currY = 0f;    //当前手指的Y位置
    private int dy = 0;          //手指移动的Y位置
    private float preX = 0f;     //保留最原先的触摸点X位置
    private float preY = 0f;     //保留最原先的触摸点Y位置

    private boolean isOutSide = true;    //判断手指在竖直滑动过程中是否已经划出ListView
    private int position = 0;                //手指横移时处于的ListView的position
    public static int WIDTH;

    private boolean isOpen = false;         //判断上一次的Item是否正在打开
    private int lastPosition = 0;              //记录上一次点击的位置


    public MyListView(Context context) {
        this(context, null);
    }

    public MyListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public int getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(int lastPosition) {
        this.lastPosition = lastPosition;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量ListView宽度，并且根据ListView宽度确定内容宽度和菜单宽度
        WIDTH = MeasureSpec.getSize(widthMeasureSpec);
        for (int i = 0; i < getChildCount(); i++) {
            MyItemListView myItemListView = (MyItemListView) getChildAt(i);
            myItemListView.resetWidth();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //处理拦截的关键，尽量不要打破原有的分发机制，根据自己的需要拦截事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            /*如果拦截了Down事件,则子类不会拿到这个事件序列*/
            case MotionEvent.ACTION_DOWN:

                isOutSide = true;
                currX = ev.getX();
                preX = ev.getX();
                currY = ev.getY();
                preY = ev.getY();

                //如果触摸点不是在同一个Position上，则如果上打开菜单的要收回
                //获取当前手指在ListView中的位置position
                position = getPosition(currY);
                if (isOpen && position != lastPosition) {
                    MyItemListView myItemListView1 = (MyItemListView) getChildAt(lastPosition);
                    myItemListView1.closeNume();
                }

                break;

            case MotionEvent.ACTION_MOVE:
                float deltaX = ev.getX() - currX;
                float deltaY = ev.getY() - currY;
                currX = ev.getX();
                currY = ev.getY();
                //表明是滑动事件，可以直接拦截
                if (Math.abs(deltaX) > 5 || Math.abs(deltaY) > 5) {
                    return true;
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {

            case MotionEvent.ACTION_MOVE:

                dx = (int) (ev.getX() - currX);
                dy = (int) (ev.getY() - currY);

                currX = ev.getX();      //更新当前手指X位置
                currY = ev.getY();      //更新当前手指Y位置

                // 1.判断谁去处理
                if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > 4) {
                    if (isOutSide) {
                        //2.获取当前手指在ListView中的位置position
                        position = getPosition(currY);
                        MyItemListView myItemListView = (MyItemListView) getChildAt(position);
                        myItemListView.viewScroll(dx);
                    }
                    //直接返回true，避免在侧滑的时候出现上下抖动的情况
                    return true;
                }

                break;

            case MotionEvent.ACTION_UP:

                //判断up与down之间位置的间距
                int finaldx = (int) (ev.getX() - preX);
                int finaldy = (int) (ev.getY() - preY);

                //松手时实现自动收缩与展开
                if (Math.abs(finaldx) > Math.abs(finaldy) && Math.abs(finaldx) > 8) {
                    MyItemListView myItemListView2 = (MyItemListView) getChildAt(position);
                    if (myItemListView2.isShowNumeScroll(finaldx)) {
                        myItemListView2.showNume();
                        isOpen = true;
                    } else {
                        myItemListView2.closeNume();
                        isOpen = false;
                    }
                }

                //如果是往上滑，则将之前打开的菜单收回
                if (Math.abs(finaldy) > Math.abs(finaldx) && Math.abs(finaldy) > 8 && isOpen) {
                    MyItemListView myItemListView = (MyItemListView) getChildAt(lastPosition);
                    myItemListView.closeNume();
                    isOpen = false;
                }

                lastPosition = position;
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.onTouchEvent(ev);
    }

    //判断当前手指的位置所在的ListView的位置position , (实际上就是pointToPosition(int x , int y) - getFirstVisiblePosition())
    private int getPosition(float currY) {
        int position;
        int itemHeight = getChildAt(0).getHeight();
        int firstItemY = getChildAt(0).getBottom();
        if (currY > firstItemY) {
            position = ((int) (currY - firstItemY)) / itemHeight + 1;
            isOutSide = true;
        } else if (firstItemY - currY < itemHeight) {
            position = 0;
            isOutSide = true;
        } else {
            position = 0;
            isOutSide = false;
        }
        return position;
    }
}
