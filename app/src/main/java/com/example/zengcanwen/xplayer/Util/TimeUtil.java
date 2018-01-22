package com.example.zengcanwen.xplayer.Util;

/**
 * Created by zengcanwen on 2017/12/1.
 */

public class TimeUtil {


    //时：分：秒
    public static String formatTime(int time){
        int yushu = 0;
        int shang = time;
        int [] result = new int[4];
        String endResult = "";
        for(int i = 0 ; i < result.length ; i++ ){
            if(i == 0){
                yushu = shang % 1000 ;
                shang = shang / 1000 ;
            }else {
                yushu = shang % 60 ;
                shang = shang / 60 ;
            }

            result[i] = yushu ;
        }

        if(result[0] > 500){
            result[1] = result[1] + 1 ;
        }

        for(int i = 3 ; i >0 ; i--){
            if(result[i] <10){
                endResult = endResult + "0" + result[i];
            }else {
                endResult = endResult + result[i];
            }
            if(i > 1){
                endResult = endResult + ":" ;
            }
        }

        return endResult ;
    }
}
