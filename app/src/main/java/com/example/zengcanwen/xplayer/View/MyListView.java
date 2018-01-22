package com.example.zengcanwen.xplayer.View;

import android.app.Application;
import android.content.Context;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.security.acl.LastOwnerException;

/**
 * Created by zengcanwen on 2017/11/27.
 * 实现ListView侧滑菜单
 */


public class MyListView extends ListView {

    private Point downPoint ;        //dowm时手指的坐标
    private Point movePoint ;        //move时手指的坐标

    private float currX = 0f ;    //当前手指的X位置
    private int dx = 0 ;          //手指移动的X位置
    private float currY = 0f ;    //当前手指的Y位置
    private int dy = 0 ;          //手指移动的Y位置
    private float preX = 0f ;     //保留最原先的触摸点X位置
    private float preY = 0f ;     //保留最原先的触摸点Y位置

    private boolean isOutSide = true ;    //判断手指在竖直滑动过程中是否已经划出ListView
    private int position = 0 ;                //手指横移时处于的ListView的position
    public static int WIDTH ;

    private float scale = 0.5f ;             //菜单宽度等于内容宽度的一半
    private boolean isOpen = false ;         //判断上一次的Item是否正在打开
    private int lastPosition = 0 ;              //记录上一次点击的位置



    public MyListView(Context context) {
        this(context , null);
    }

    public MyListView(Context context, AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //点击事件回调
    public interface MyClickListener{
        void contentClickListener(int position) ;
        void deleteClickListener(int position) ;
        void cancleClickListener(int position) ;
    }

    private MyClickListener myClickListener ;

    public void setMyClickListener(MyClickListener myClickListener){
        this.myClickListener = myClickListener ;
    }

    private void init(){
        downPoint = new Point() ;
        movePoint = new Point() ;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //测量ListView宽度，并且根据ListView宽度确定内容宽度和菜单宽度
        WIDTH = MeasureSpec.getSize(widthMeasureSpec) ;
        for(int i = 0 ; i < getChildCount() ; i++){
            MyItemListView myItemListView = (MyItemListView)getChildAt(i) ;
            myItemListView.resetWidth();
        }
        super.onMeasure(widthMeasureSpec , heightMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true ;
    }

    //    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        boolean intercepted = false;
//        int x = (int) ev.getX();
//        int y = (int) ev.getY();
//
//        switch (ev.getAction()) {
//            /*如果拦截了Down事件,则子类不会拿到这个事件序列*/
//            case MotionEvent.ACTION_DOWN:
//                lastXIntercept = x;
//                lastYIntercept = y;
//                intercepted = false;
//                Log.i("77777" , "onInterceptTouchEvent--------->ACTION_DOWN" ) ;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                final int deltaX = x - lastXIntercept;
//                final int deltaY = y - lastYIntercept;
//                /*根据条件判断是否拦截该事件*/
//                if (Math.abs(deltaX) < 3  && Math.abs(deltaY) < 3 ) {
//                    intercepted = false;
//                } else {
//                    intercepted = true ;
//                }
//                Log.i("77777" , "onInterceptTouchEvent--------->ACTION_MOVE----------->" + intercepted ) ;
//
//                break;
//            case MotionEvent.ACTION_UP:
//                intercepted = false;
//                Log.i("77777" , "onInterceptTouchEvent--------->ACTION_UP" ) ;
//
//                break;
//        }
//        lastXIntercept = x;
//        lastYIntercept = y;
//        return intercepted;
//    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){

            case MotionEvent.ACTION_DOWN:
                Log.i("ttttt" , "ACTION_DOWN") ;
                isOutSide = true ;
                currX = ev.getX() ;
                preX = ev.getX() ;
                currY = ev.getY() ;
                preY = ev.getY() ;
                Log.i("ttttt" , "ACTION_DOWN-------->" + preX + "-------->" + preY) ;

                //如果触摸点不是在同一个Position上，则如果上打开菜单的要收回
                //获取当前手指在ListView中的位置position
                position  =  getPosition(currY) ;
                if(isOpen  &&  position != lastPosition){
                    MyItemListView myItemListView1 = (MyItemListView) getChildAt(lastPosition) ;
                    myItemListView1.closeNume();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                Log.i("ttttt" , "ACTION_MOVE") ;

                dx = (int)( ev.getX() - currX) ;
                dy = (int)( ev.getY() - currY) ;
                // 1.判断谁去处理
                if(Math.abs(dx) > Math.abs(dy)  && Math.abs(dx) > 4){
                    if(isOutSide){
                        //2.获取当前手指在ListView中的位置position
                        position  =  getPosition(currY) ;
                        MyItemListView myItemListView = (MyItemListView) getChildAt(position) ;
                        myItemListView.viewScroll( dx);
                    }
                }
                currX = ev.getX() ;      //更新当前手指X位置
                currY = ev.getY() ;      //更新当前手指Y位置
                break;

            case MotionEvent.ACTION_UP:

                //判断up与down之间位置的间距
                int finaldx =(int) (ev.getX() - preX)  ;
                int finaldy = (int) (ev.getY() - preY) ;

                //松手时实现自动收缩与展开
                if(Math.abs(finaldx) > Math.abs(finaldy)  && Math.abs(finaldx) > 8){
                    MyItemListView myItemListView2 = (MyItemListView) getChildAt(position) ;
                    if(myItemListView2.isShowNumeScroll(finaldx)){
                        myItemListView2.showNume();
                        isOpen = true ;
                    }else {
                        myItemListView2.closeNume();
                        isOpen = false ;
                    }
                }

                //如果是往上滑，则将之前打开的菜单收回
                if(Math.abs(finaldy) > Math.abs(finaldx)  && Math.abs(finaldy) > 8  && isOpen) {
                    MyItemListView myItemListView = (MyItemListView)getChildAt(lastPosition) ;
                    myItemListView.closeNume();
                    isOpen = false ;
                }

                //点击事件
                if(Math.abs(finaldx) < 8  && Math.abs(finaldy) < 8){
                    //避免空指针
                    if(myClickListener != null){
                        int firstItem = getFirstVisiblePosition() ;
                    //点击当前展开的Item，则为特殊情况
                    if(isOpen && lastPosition == position){
                        int widght = getChildAt(0).getWidth() ;

                        if(preX < widght / 2){
                            myClickListener.contentClickListener(position + firstItem);

                        }else if( widght /2 <= preX  && preX < widght * 3 / 4){
                            MyItemListView myItemListView = (MyItemListView)getChildAt(position ) ;
                            myItemListView.closeNume();
                            isOpen = false ;     //避免删除之后还以为是打开着
                            myClickListener.deleteClickListener(position + firstItem);

                        }else if(widght *3 / 4 <= preX  && preX <= widght){
                            MyItemListView myItemListView = (MyItemListView)getChildAt(position ) ;
                            myItemListView.closeNume();
                            isOpen = false ;
                            myClickListener.cancleClickListener(position + firstItem);
                        }
                    }else {
                        myClickListener.contentClickListener(position + firstItem);
                    }
                }
                }

                lastPosition = position ;
                break;

            case MotionEvent.ACTION_CANCEL:
//                Log.i("aaaaa" , "myItemListView------ACTION_CANCEL------>") ;
                break;
        }
        return super.onTouchEvent(ev);
    }

    //判断当前手指的位置所在的ListView的位置position , (实际上就是pointToPosition(int x , int y) - getFirstVisiblePosition())
    private int getPosition(float currY){
        int position = 0;
        int itemHeight = getChildAt(0).getHeight() ;
        int firstItem = getFirstVisiblePosition() ;
        int firstItemY = getChildAt(0).getBottom() ;
        if(currY > firstItemY) {
            position = ((int) (currY - firstItemY)) / itemHeight + 1;
            isOutSide = true ;
        }else if(firstItemY - currY < itemHeight) {
            position = 0 ;
            isOutSide = true ;
        }else {
            position = 0 ;
            isOutSide = false ;
        }
        return position;
    }
}
