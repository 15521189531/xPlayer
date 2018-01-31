package com.example.zengcanwen.xplayer.online.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zengcanwen.xplayer.Bean.NetVideosDataBean;
import com.example.zengcanwen.xplayer.R;
import com.example.zengcanwen.xplayer.online.customView.MyProgressBarView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zengcanwen on 2017/12/5.
 */

public abstract class MynetLvAdapter extends BaseAdapter {
    private Context context ;
    private Handler handler = new Handler() ;
    private HashMap<Integer , MyProgressBarView> ProgressBarHM ;
    private HashMap<Integer , ImageView> imageViewHM ;
    private ArrayList<NetVideosDataBean> arrayList = new ArrayList<>() ;

    public ArrayList<NetVideosDataBean> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<NetVideosDataBean> arrayList) {
        this.arrayList = arrayList;
    }

    public MynetLvAdapter(Context context ) {
        this.context = context ;
        ProgressBarHM = new HashMap<>() ;
        imageViewHM = new HashMap<>() ;
    }

    public HashMap<Integer, MyProgressBarView> getHashMap() {
        return ProgressBarHM;
    }


    //点击按钮接口
    public interface MyViewListener{
        void progressClick(View v ,int position , String url) ;
        void LlClick(int position , String url) ;
    }

    private MyViewListener myViewListener ;

    public void setMyClickListener(MyViewListener myViewListener){
        this.myViewListener = myViewListener ;
    }

    public MyProgressBarView getMyProgressBarView(int position){
        return ProgressBarHM.get(position) ;
    }

    public ImageView getImageView(int position){
        return imageViewHM.get(position) ;
    }

    @Override
    public int getCount() {
        return (arrayList == null)? 0 : arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        MyViewHolder myViewHolder = null ;
        if(view == null){

            view = LayoutInflater.from(context).inflate(R.layout.net_lv_adap_layout , viewGroup , false) ;
            myViewHolder = new MyViewHolder() ;
            myViewHolder.imageView = (ImageView)view.findViewById(R.id.net_pic_iv) ;
            myViewHolder.textView = (TextView)view.findViewById(R.id.net_name_tv) ;
            myViewHolder.linearLayout = (LinearLayout) view.findViewById(R.id.net_adap_ll) ;
            myViewHolder.myProgressBarView = (MyProgressBarView)view.findViewById(R.id.my_progress_bar_view) ;
            view.setTag(myViewHolder);
        }else {
            myViewHolder = (MyViewHolder)view.getTag() ;
        }

        myViewHolder.myProgressBarView.setTag(i);
        ProgressBarHM.put(i , myViewHolder.myProgressBarView) ;
        imageViewHM.put(i , myViewHolder.imageView) ;

        myViewHolder.textView.setText(arrayList.get(i).getTitle());

        //加载图片和初始化进度抛到Fragment处理
        initProgress(myViewHolder.myProgressBarView , i);
        updataPic(arrayList.get(i).getPreview_url() , myViewHolder.imageView , i);


        //设置点击回调
        myViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myViewListener.LlClick(i , arrayList.get(i).getVideo_url());
            }
        });

        myViewHolder.myProgressBarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myViewListener.progressClick(view , i , arrayList.get(i).getVideo_url());
            }
        });

        return view;
    }

    class MyViewHolder {
        ImageView imageView ;
        TextView textView ;
        MyProgressBarView myProgressBarView ;
        LinearLayout linearLayout ;

       MyViewHolder(){

       }
    }

    //加载图片
    public abstract void updataPic(String url , ImageView imageView , int position) ;

    //初始化进度条
    public abstract void initProgress(MyProgressBarView myProgressBarView , int position) ;
}
