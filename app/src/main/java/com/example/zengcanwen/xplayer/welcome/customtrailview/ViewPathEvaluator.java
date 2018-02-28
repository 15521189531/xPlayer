package com.example.zengcanwen.xplayer.welcome.customtrailview;

import android.animation.TypeEvaluator;

public class ViewPathEvaluator implements TypeEvaluator<ViewPoint> {
    private float x ;
    private float y ;

    private float startX ;
    private float startY ;

    public ViewPathEvaluator() {
    }

    @Override
    public ViewPoint evaluate(float fraction, ViewPoint startValue, ViewPoint endValue) {
        if(endValue.operation == ViewPath.MOVE){
            x = startValue.x ;
            y = startValue.y ;

        }else if(endValue.operation == ViewPath.LINE){
            startX = (startValue.operation == ViewPath.QUAD)? startValue.x1 : startValue.x ;
            startX = (startValue.operation == ViewPath.CURVE)? startValue.x2 : startX ;
            startY = (startValue.operation == ViewPath.QUAD)? startValue.y1 : startValue.y ;
            startY = (startValue.operation == ViewPath.CURVE)? startValue.y2 : startY ;

            x = startX + fraction * (endValue.x - startX);
            y = startY+ fraction * (endValue.y - startY);

        }else if(endValue.operation == ViewPath.QUAD){
            startX = (startValue.operation == ViewPath.QUAD)? startValue.x1 : startValue.x ;
            startX = (startValue.operation == ViewPath.CURVE)? startValue.x2 : startX ;
            startY = (startValue.operation == ViewPath.QUAD)? startValue.y1 : startValue.y ;
            startY = (startValue.operation == ViewPath.CURVE)? startValue.y2 : startY ;

            float oneMinusT = 1 - fraction;
            x = oneMinusT * oneMinusT *  startX +
                    2 * oneMinusT *  fraction * endValue.x +
                    fraction * fraction * endValue.x1;

            y = oneMinusT * oneMinusT * startY +
                    2  * oneMinusT * fraction * endValue.y +
                    fraction * fraction * endValue.y1;

        }else if(endValue.operation == ViewPath.CURVE){
            startX = (startValue.operation == ViewPath.QUAD)? startValue.x1 : startValue.x ;
            startX = (startValue.operation == ViewPath.CURVE)? startValue.x2 : startX ;
            startY = (startValue.operation == ViewPath.QUAD)? startValue.y1 : startValue.y ;
            startY = (startValue.operation == ViewPath.CURVE)? startValue.y2 : startY ;

            float oneMinusT = 1 - fraction;

            x = oneMinusT * oneMinusT * oneMinusT * startX +
                    3 * oneMinusT * oneMinusT * fraction * endValue.x +
                    3 * oneMinusT * fraction * fraction * endValue.x1+
                    fraction * fraction * fraction * endValue.x2;

            y = oneMinusT * oneMinusT * oneMinusT * startY +
                    3 * oneMinusT * oneMinusT * fraction * endValue.y +
                    3 * oneMinusT * fraction * fraction * endValue.y1+
                    fraction * fraction * fraction * endValue.y2;

        }else {
            x = endValue.x ;
            y = endValue.y ;
        }


        return new ViewPoint(x , y);
    }
}