package com.example.zengcanwen.xplayer.View;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;

import com.example.zengcanwen.xplayer.Activity.MainActivity;

import static com.example.zengcanwen.xplayer.View.MyListView.WIDTH;

/**
 * Created by zengcanwen on 2017/11/28.
 */

public class MyItemListView extends LinearLayout {

    private ViewGroup contentVg ;
    private ViewGroup numeVg ;
    private MyListView myListView ;

    private float numeScale = 0.5f ;         //菜单与内容的比例

    private Scroller scroller ;             //用于滑动的Scroller

    private  ViewGroup.LayoutParams contentParams ;   //内容控件的LayoutParams
    private ViewGroup.LayoutParams numeParams ;        //菜单控件的LayoutParams
    private boolean isOpenNume = false              ;  //判断菜单是否打开

    public boolean getIsOpenNume(){
        return isOpenNume ;
    }

    public MyItemListView(Context context ) {
        this(context , null);
    }

    public MyItemListView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs , 0);
    }

    public MyItemListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scroller = new Scroller(context) ;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //向MyItemListView填充控件,并设置初始宽高
    public void setView(MyListView myListView, ViewGroup contentVg , ViewGroup numeVg){
        this.myListView = myListView ;
        this.contentVg = contentVg ;
        this.numeVg = numeVg ;
        if(contentParams == null){
            contentParams   = new ViewGroup.LayoutParams(WIDTH, ViewGroup.LayoutParams.MATCH_PARENT) ;
        }
        contentVg.setLayoutParams(contentParams);
        addView(contentVg);

        if(numeParams == null){
            numeParams = new ViewGroup.LayoutParams((int)(WIDTH * numeScale) , ViewGroup.LayoutParams.MATCH_PARENT) ;
        }
        numeVg.setLayoutParams(numeParams);
        addView(numeVg);
    }

    //设置添加控价的宽高,确保控件宽高适应MyListView宽高
    public void resetWidth(){
        ViewGroup.LayoutParams contentParams = contentVg.getLayoutParams() ;
        if(contentParams == null){
            contentParams = new ViewGroup.LayoutParams(WIDTH , ViewGroup.LayoutParams.MATCH_PARENT) ;
        }else {
            contentParams.width = WIDTH ;
        }
        contentVg.setLayoutParams(contentParams);

        ViewGroup.LayoutParams numeParams = numeVg.getLayoutParams() ;
        if(numeParams == null){
            numeParams = new ViewGroup.LayoutParams((int) (WIDTH * numeScale) , ViewGroup.LayoutParams.MATCH_PARENT) ;
        }else {
            numeParams.width = (int) (WIDTH * numeScale) ;
        }
        numeVg.setLayoutParams(numeParams);
    }


    /*
    是否应该展开，两种情况，右滑展开，左滑收缩
    dx: 手指移动的距离
    true：展开    false：收缩
     */

    public boolean isShowNumeScroll(int dx) {
        if (numeVg.getWidth() == 0) {
            resetWidth();
        }

        //右滑时，当控件滑动距离大于菜单控件的3/1时，收缩
        if (dx > 0) {
            if (scroller.getFinalX() < numeVg.getWidth() * 3 / 2) {
                return false;
            } else {
                return true;
            }
        }

        //左滑时，当控价滑动距离大于菜单控价的3/1时，展开
        else{
            if (scroller.getFinalX() > numeVg.getWidth() / 3) {
                return true;
            } else {
                return false;
            }
        }
    }

    //在触摸过程中实现的滑动 (需考虑到边界)
    public void viewScroll( int dx){

        //判断是右滑
        if(dx > 0){
            //处理边界，如果scroller.getFinalX() > 0 ; 说明还没有出界
            if(scroller.getFinalX() > 0) {
                //更加细微的边界处理
                int min = Math.min(dx, scroller.getFinalX());
                scroller.startScroll(scroller.getFinalX(), scroller.getStartY(), -min, 0);
                invalidate();
            }else{
                scroller.setFinalX(0);
            }
        }else {                  //判断左滑

            //处理边界，如果左滑过程中，scroller.getFinalX() < 菜单宽度，说明没有出界
            Log.i("aaaaa" , "菜单宽度---------->" + numeVg.getWidth()) ;
            if(scroller.getFinalX() < numeVg.getWidth()){
                //更加细微的边界处理
                int min = Math.min(dx, numeVg.getWidth()-scroller.getFinalX());
                scroller.startScroll(scroller.getFinalX(), scroller.getStartY(), -min, 0);
                invalidate();
            }else{
                scroller.setFinalX(numeVg.getWidth());
            }
        }

        Log.i("aaaaa"  , "scroll.getFinalX()--------->" + scroller.getFinalX()) ;
    }


    //自动弹出菜单
    public void showNume(){
        scroller.startScroll(scroller.getFinalX() , scroller.getFinalY() , numeVg.getWidth() - scroller.getFinalX() , scroller.getFinalY());
        invalidate();
    }


    //自动收缩菜单
    public void closeNume(){
        scroller.startScroll(scroller.getFinalX() , scroller.getFinalY() , -scroller.getFinalX() , scroller.getFinalY());
        invalidate();
    }



    @Override
    public void computeScroll() {
        //判断滑动是否已经完成
        if(scroller.computeScrollOffset()){
            scrollTo(scroller.getCurrX() ,scroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }

}
