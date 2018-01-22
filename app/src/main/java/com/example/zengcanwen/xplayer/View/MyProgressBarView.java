package com.example.zengcanwen.xplayer.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.zengcanwen.xplayer.R;
import com.example.zengcanwen.xplayer.Util.DipandPxUtli;


/**
 * Created by zengcanwen on 2017/12/5.
 */

public class MyProgressBarView extends View {

    private int preColor ;          //原始圆环底色
    private float preroundWidth ;        //原始圆环宽度
    private int roundNowColor ;     //圆环在进度条中的颜色
    private float roundNowWidth ;   //圆环在进度条中的宽度
    private int progressMax ;       //圆环的最大进度
    private int progress ;          //当前进度
    private int jiantouColor ;      //箭头颜色
    private Paint paint ;           //画笔
    private Path path ;             //绘制路径
    private TypedArray typedArray ;  //加载属性
    private String textStr = "" ;         //下载状态
    private int textCol ;            //字体颜色
    private boolean isNeedInva = true ;       //判断是否需要更新



    public String getTextStr() {
        return textStr;
    }

    public void setTextStr(String textStr) {
        if(isNeedInva){
            this.textStr = textStr;
            invalidate();
        }

    }

    public int getTextCol() {
        return textCol;
    }

    public void setTextCol(int textCol) {
        if(isNeedInva){
            this.textCol = textCol;
            invalidate();
        }

    }

    public float getPreroundWidth() {
        return preroundWidth;
    }

    public void setPreroundWidth(float preroundWidth) {
        if(isNeedInva){
            this.preroundWidth = preroundWidth;
            invalidate();
        }

    }

    public float getRoundNowWidth() {
        return roundNowWidth;
    }

    public void setRoundNowWidth(float roundNowWidth) {
        if(isNeedInva){
            this.roundNowWidth = roundNowWidth;
            invalidate();
        }

    }

    public int getPreColor() {
        return preColor;
    }

    public void setPreColor(int preColor) {
        if(isNeedInva){
            this.preColor = preColor;
            invalidate();
        }

    }



    public int getRoundNowColor() {
        return roundNowColor;
    }

    public void setRoundNowColor(int roundNowColor) {
        if(isNeedInva){
            this.roundNowColor = roundNowColor;
            invalidate();
        }

    }

    public int getProgressMax() {
        return progressMax;
    }

    public void setProgressMax(int progressMax) {
        if(isNeedInva){
            this.progressMax = progressMax;
            invalidate();
        }

    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        if(isNeedInva){
            this.progress = progress;
            invalidate();
            Log.i("wwwww" , "setProgress") ;
        }
    }

    public int getJiantouColor() {
        return jiantouColor;
    }

    public void setJiantouColor(int jiantouColor) {
        if(isNeedInva){
            this.jiantouColor = jiantouColor;
            invalidate();
        }

    }


    public MyProgressBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context , attrs);

    }


    private void init(Context context , AttributeSet attrs){
        paint = new Paint() ;
        path = new Path() ;
        //加载初始化的属性
        typedArray = context.obtainStyledAttributes(attrs , R.styleable.MyProgressBarView) ;
        preColor = typedArray.getColor(R.styleable.MyProgressBarView_preColor , Color.GRAY) ;
        preroundWidth = typedArray.getDimension(R.styleable.MyProgressBarView_preroundWidth , 10) ;
        roundNowColor = typedArray.getColor(R.styleable.MyProgressBarView_roundNowColor ,getResources().getColor( R.color.roundNowColor)) ;
        roundNowWidth = typedArray.getDimension(R.styleable.MyProgressBarView_roundNowWidth , 12) ;
        progressMax = (int) typedArray.getDimension(R.styleable.MyProgressBarView_progressMax , 100) ;
        progress = (int) typedArray.getDimension(R.styleable.MyProgressBarView_progress , 0) ;
        jiantouColor = typedArray.getColor(R.styleable.MyProgressBarView_jiantouColor , Color.GRAY) ;
        textStr = typedArray.getString(R.styleable.MyProgressBarView_testStr ) ;
        if(textStr == null)  textStr = "" ;
        textCol = typedArray.getColor(R.styleable.MyProgressBarView_textCol , getResources().getColor( R.color.roundNowColor)) ;
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width ;
        int height ;
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec) ;
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec) ;
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec) ;
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec) ;
        if(modeWidth == MeasureSpec.EXACTLY){
            width = measureWidth ;
        }else{
            int widthMax = DipandPxUtli.dip2px(getContext() , 65) ;
            width = Math.min(measureWidth , widthMax) ;
        }

        if(modeHeight == MeasureSpec.EXACTLY){
            height = measureHeight ;
        }else{
            int heightMax = DipandPxUtli.dip2px(getContext() , 65) ;
            height = Math.min(measureHeight , heightMax) ;
        }
        setMeasuredDimension(width , height);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //基本量
        int width = getWidth() ;
        int height = getHeight() ;
        int paddingLeft = getPaddingLeft() ;
        int paddingRight = getPaddingRight() ;
        int paddingTop = getPaddingTop() ;
        int paddingBottom = getPaddingBottom() ;
        float locateX = getX() ;
        float locateY = getY() ;

        //半径
        float roundLong = Math.min(width , height) / 2  - paddingLeft ;
        //圆心
        float roundHreadX =  width/2 ;
        float roundHreadY =  height/2 ;

        //先画原始外圆
        paint.setColor(preColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(preroundWidth);
        paint.setAntiAlias(true);
        paint.setDither(true);
        canvas.drawCircle(roundHreadX , roundHreadY , roundLong , paint);
        paint.reset();

        //绘制下载箭头
        paint.setColor(jiantouColor);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        paint.setDither(true);
        canvas.drawRect(roundHreadX - 6 , roundHreadY - 40 - 10, roundHreadX + 6 , roundHreadY + 40 - 10 , paint);
        //绘制三角形
        path.moveTo( roundHreadX - 6  - 25 , roundHreadY + 40 - 10);
        path.lineTo(roundHreadX +6 + 25, roundHreadY + 40 - 10);
        path.lineTo(roundHreadX , roundHreadY + 40 - 10 + 25 );
        path.close();
        canvas.drawPath(path , paint);
        paint.reset();

        //绘制进度条
        paint.setColor(roundNowColor);
        paint.setStrokeWidth(roundNowWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setDither(true);
        paint.setAntiAlias(true);
        //通过计算得到的比例计算需要绘制的弧度
        int needArc = (int)(360.0f / (float) getProgressMax() * (float)getProgress()) ;
        RectF rect = new RectF(paddingLeft , paddingTop , width - paddingRight , height - paddingBottom) ;
        canvas.drawArc(rect , -90 , needArc , false , paint);
        paint.reset();

        //绘画字体
        paint.setColor(textCol);
        paint.setTextSize(DipandPxUtli.dip2px(getContext() ,12));
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(textStr , roundHreadX , roundHreadY + 5  , paint);
        paint.reset();
    }


    //停止绘制
    public void stopInvaladate(){
        if(isNeedInva){
            isNeedInva = !isNeedInva ;

        }
    }

    //开始绘制
    public void startInvaladate(){
        if(!isNeedInva){
            isNeedInva = !isNeedInva ;

        }
    }

//    @Override
//    protected void onAttachedToWindow() {
//        super.onAttachedToWindow();
//        isNeedInva = true ;
//        if(setOnAttachToWindow != null){
//            setOnAttachToWindow.setMyOnAttachToWindow(this);
//        }
//        Log.i("wwwww" , "onAttachedToWindow") ;
//    }
//
//    @Override
//    protected void onDetachedFromWindow() {
//        super.onDetachedFromWindow();
//        isNeedInva = false ;
//        Log.i("wwwww" , "onDetachedFromWindow") ;
//
//    }

//    @Override
//    protected void onWindowVisibilityChanged(int visibility) {
//        super.onWindowVisibilityChanged(visibility);
//        if(visibility  == VISIBLE){
//            isNeedInva = true ;
//        if(setOnAttachToWindow != null){
//            setOnAttachToWindow.setMyOnAttachToWindow(this);
//        }
//            Log.i("wwwww" , "VISIBLE---------------------------------") ;
//        }else{
//            isNeedInva = false ;
//            Log.i("wwwww" , "UNVISIBLE---------------------------------") ;
//
//        }
//    }
}
