package com.example.zengcanwen.xplayer.welcome.customtrailview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.zengcanwen.xplayer.R;

/**
 * Created by zcw on 2018/2/28.
 */

public class BezierTrailAnimationView {

    private ImageView redIv  ;
    private ImageView blueIv ;
    private ImageView yellowIv ;
    private ImageView greenIv ;
    private Context context ;
    private float height ;
    private float width ;
    private ViewPath redViewPath , yellowViewPath , blueViewPath , greenViewPath ;
    private AnimatorSet redAnimationSet , yellowAnimationSet , blueAnimationSet , greenAnimationSet ;
    private FrameLayout parentFl ;

    public interface OnEndAnimation{
        void endAnimation() ;
    }

    private OnEndAnimation mOnEndAnimation ;

    public void setOnEndAnimation(OnEndAnimation onEndAnimation){
        this.mOnEndAnimation = onEndAnimation ;
    }

    public BezierTrailAnimationView(FrameLayout parentFl , Context context) {
        this.parentFl = parentFl;
        this.context = context ;
    }

    public void start(){
        init();
        redViewPath();
        blueViewPath() ;
        yellowViewPath();
        greenViewPath() ;
    }

    private void init(){

        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);;
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();

        redIv = new ImageView(context) ;
        initIv(redIv , R.drawable.red_drawable);
        yellowIv = new ImageView(context) ;
        initIv(yellowIv , R.drawable.yellow_drawable);
        blueIv = new ImageView(context) ;
        initIv(blueIv , R.drawable.blue_drawable);
        greenIv = new ImageView(context) ;
        initIv(greenIv , R.drawable.green_drawable);
    }

    private void initIv(ImageView imageView , int drawableR){
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT , FrameLayout.LayoutParams.WRAP_CONTENT) ;
        imageView.setImageResource(drawableR);
        layoutParams.height = Util.dp2px(context , 40) ;
        layoutParams.width = Util.dp2px(context , 40);
        layoutParams.gravity = Gravity.CENTER ;
        imageView.setLayoutParams(layoutParams);
        parentFl.addView(imageView);
    }

    //红色球的运行轨迹
    private void redViewPath(){
        redViewPath = new ViewPath() ;
        redViewPath.moveTo(0  , 0);
        redViewPath.lineTo(-(width / 4.0f) , 0);
        redViewPath.curveTo(-700, -height / 2, width / 3 * 2, -height / 3 * 2, 0, -Util.dp2px(context , 80)) ;
        setAnimation(redIv , redViewPath);
        redAnimationSet.start();
    }

    //蓝色球的运行轨迹
    private void blueViewPath(){
        blueViewPath = new ViewPath() ;
        blueViewPath.moveTo(0, 0);
        blueViewPath.lineTo(width / 5 * 2 - width / 2, 0);
        blueViewPath.curveTo(-300, -height / 2, width, -height / 9 * 5, 0, -Util.dp2px(context , 80)) ;
        setAnimation( blueIv, blueViewPath);
        blueAnimationSet.start();
    }

    //黄色球的运行轨迹
    private void yellowViewPath(){
        yellowViewPath = new ViewPath() ;
        yellowViewPath.moveTo(0, 0);
        yellowViewPath.lineTo(width / 5 * 3 - width / 2, 0);
        yellowViewPath.curveTo(300, height, -width, -height / 9 * 5, 0, -Util.dp2px(context , 80));
        setAnimation(yellowIv , yellowViewPath);
        yellowAnimationSet.start();
    }

    //绿色球的运行轨迹
    private void greenViewPath(){
        greenViewPath = new ViewPath() ;
        greenViewPath.moveTo(0, 0);
        greenViewPath.lineTo(width / 5 * 4 - width / 2, 0);
        greenViewPath.curveTo(700, height / 3 * 2, -width / 2, height / 2, 0, -Util.dp2px(context , 80));
        setAnimation(greenIv , greenViewPath);
        greenAnimationSet.start();
    }


    private void setAnimation(ImageView imageView , ViewPath viewPath){
        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(new ViewObj(imageView) , "fabLoc" , new ViewPathEvaluator() , viewPath.getPoints().toArray()) ;
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.setDuration(2300) ;
        addAnimation(objectAnimator , imageView);
    }

    private void addAnimation(ObjectAnimator objectAnimator , final ImageView imageView){
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1 , 1000) ;
        valueAnimator.setDuration(1800) ;
        valueAnimator.setStartDelay(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float)animation.getAnimatedValue() ;
                float scale = getScale(imageView);
                if (value <= 500) {
                    scale = 1.0f + (value / 1000.0f) * scale;
                } else {
                    scale = 1 + ((1000.0f - value) / 1000.0f) * scale;
                }
                imageView.setScaleY(scale);
                imageView.setScaleX(scale);
                float alpha = value / 1000.0f ;
                imageView.setAlpha(1.0f - alpha);
            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                parentFl.removeView(imageView);
                mOnEndAnimation.endAnimation();
            }
        });

        if(imageView == redIv){
            redAnimationSet = new AnimatorSet() ;
            redAnimationSet.playTogether(valueAnimator , objectAnimator);
        }else if(imageView == greenIv){
            greenAnimationSet = new AnimatorSet() ;
            greenAnimationSet.playTogether(valueAnimator , objectAnimator);
        }else if(imageView == yellowIv){
            yellowAnimationSet = new AnimatorSet() ;
            yellowAnimationSet.playTogether(valueAnimator , objectAnimator);
        }else if(imageView == blueIv){
            blueAnimationSet = new AnimatorSet() ;
            blueAnimationSet.playTogether(valueAnimator , objectAnimator);
        }
    }

    private float getScale(ImageView imageView){
        if(imageView == redIv){
            return 1.0f ;
        }else if(imageView == blueIv){
            return 1.3f ;
        }else if(imageView == greenIv){
            return 1.8f ;
        }else if(imageView == yellowIv){
            return 2.1f ;
        }else {
            return 1.0f ;
        }
    }

    public class ViewObj {
        private final ImageView imageView;
        public ViewObj(ImageView imageView) {
            this.imageView = imageView;
        }

        public void setFabLoc(ViewPoint newLoc) {
            imageView.setTranslationX(newLoc.x);
            imageView.setTranslationY(newLoc.y);
        }
    }
}
