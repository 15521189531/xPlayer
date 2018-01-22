package com.example.zengcanwen.xplayer.Util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.zengcanwen.xplayer.Application.MyApplication;

/**
 * Created by zengcanwen on 2017/12/7.
 */

public class SPUtil {
    private SharedPreferences sharedPreferences ;
    private SharedPreferences.Editor editor ;
    private Context context = MyApplication.getContext();
    private static SPUtil spUtil ;
    private SPUtil(){
        sharedPreferences = context.getSharedPreferences("mySharePreferences" , Activity.MODE_PRIVATE) ;
        editor = sharedPreferences.edit() ;
    }
    //双重锁
    public static SPUtil getInstance(){
        if(spUtil == null){
            synchronized (SPUtil.class){
                if(spUtil == null){
                    spUtil = new SPUtil() ;
                }
            }
        }
        return spUtil ;
    }

    //获取数据
    public String getString(String key){
        return sharedPreferences.getString(key , null) ;
    }

    //获取数据
    public int getInt(String key){
        return sharedPreferences.getInt(key , -1) ;
    }

    //存放数据
    public void save(String key , int value){
        editor.putInt(key , value) ;
        editor.commit() ;
    }

    public void save(String key , String value){
        editor.putString(key , value) ;
        editor.commit() ;
    }

    public void save(String key , boolean value){
        editor.putBoolean(key , value) ;
        editor.commit() ;
    }

    public boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key , false) ;
    }

    public void save(String key , long value){
        editor.putLong(key , value) ;
        editor.commit() ;
    }

    public long getLong(String key){
        return sharedPreferences.getLong(key , 0) ;
    }

}
