package com.example.zengcanwen.xplayer.online.adapter;

import android.content.Context;
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
 * 在线视频列表ListView的适配器
 * Created by zengcanwen on 2017/12/5.
 */

public abstract class MyOnlineLvAdapter extends BaseAdapter {
    private Context context;
    private HashMap<Integer, MyProgressBarView> mProgressBarHM;
    private HashMap<Integer, ImageView> mImageViewHM;
    private ArrayList<NetVideosDataBean> arrayList = new ArrayList<>();

    public void setArrayList(ArrayList<NetVideosDataBean> arrayList) {
        this.arrayList = arrayList;
    }

    public MyOnlineLvAdapter(Context context) {
        this.context = context;
        mProgressBarHM = new HashMap<>();
        mImageViewHM = new HashMap<>();
    }


    //点击按钮接口
    public interface MyViewListener {
        void progressClick(int position);

        void LlClick(int position);
    }

    private MyViewListener myViewListener;

    public void setMyClickListener(MyViewListener myViewListener) {
        this.myViewListener = myViewListener;
    }

    public MyProgressBarView getMyProgressBarView(int position) {
        return mProgressBarHM.get(position);
    }

    public ImageView getImageView(int position) {
        return mImageViewHM.get(position);
    }

    @Override
    public int getCount() {
        return (arrayList == null) ? 0 : arrayList.size();
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
        MyViewHolder myViewHolder;
        if (view == null) {

            view = LayoutInflater.from(context).inflate(R.layout.net_lv_adap_layout, viewGroup, false);
            myViewHolder = new MyViewHolder();
            myViewHolder.imageView = view.findViewById(R.id.net_pic_iv);
            myViewHolder.textView = view.findViewById(R.id.net_name_tv);
            myViewHolder.linearLayout = view.findViewById(R.id.net_adap_ll);
            myViewHolder.myProgressBarView = view.findViewById(R.id.my_progress_bar_view);
            view.setTag(myViewHolder);
        } else {
            myViewHolder = (MyViewHolder) view.getTag();
        }

        myViewHolder.myProgressBarView.setTag(i);
        mProgressBarHM.put(i, myViewHolder.myProgressBarView);
        mImageViewHM.put(i, myViewHolder.imageView);

        myViewHolder.textView.setText(arrayList.get(i).getTitle());

        //加载图片和初始化进度抛到Fragment处理
        initProgress(i);
        updataPic(arrayList.get(i).getPreview_url(), i);


        //设置点击回调
        myViewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myViewListener.LlClick(i);
            }
        });

        myViewHolder.myProgressBarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myViewListener.progressClick(i);
            }
        });

        return view;
    }

    class MyViewHolder {
        ImageView imageView;
        TextView textView;
        MyProgressBarView myProgressBarView;
        LinearLayout linearLayout;

        MyViewHolder() {

        }
    }

    //加载图片
    public abstract void updataPic(String url, int position);

    //初始化进度条
    public abstract void initProgress(int position);
}
