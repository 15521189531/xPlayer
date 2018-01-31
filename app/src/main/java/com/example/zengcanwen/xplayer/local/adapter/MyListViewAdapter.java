package com.example.zengcanwen.xplayer.local.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zengcanwen.xplayer.Bean.LocalVideoFileBean;
import com.example.zengcanwen.xplayer.R;
import com.example.zengcanwen.xplayer.Util.TimeUtil;
import com.example.zengcanwen.xplayer.local.customview.MyItemListView;
import com.example.zengcanwen.xplayer.local.customview.MyListView;

import java.util.ArrayList;

/**
 * 侧滑菜单ListView的适配器
 * Created by zengcanwen on 2017/11/28.
 */

public class MyListViewAdapter extends BaseAdapter {

    private ArrayList<LocalVideoFileBean> arrayList = new ArrayList<>();
    private Context context;
    private MyListView myListView;
    private MyItemListView myItemListView;

    public interface MyClickListener {
        void contentClickListener(int position);

        void cancelClickListener(int position);

        void deleteClickListener(int position);
    }

    private MyClickListener myClickListener;

    public void setMyClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public void setArrayList(ArrayList<LocalVideoFileBean> arrayList) {
        this.arrayList = arrayList;

    }

    public MyListViewAdapter(Context context, MyListView myListView) {
        this.context = context;
        this.myListView = myListView;

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
        ViewHolder viewHolder;

        if (view == null) {
            myItemListView = new MyItemListView(context);
            ViewGroup contentVg = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.local_content_layout, null);
            ViewGroup numeVg = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.local_nemu_layout, null);
            myItemListView.setView(contentVg, numeVg);
            viewHolder = new ViewHolder(contentVg, numeVg, myListView);
            myItemListView.setTag(viewHolder);
            view = myItemListView;
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.nameTv.setText(arrayList.get(i).getmName());
        viewHolder.imageView.setImageBitmap(arrayList.get(i).getmBitmap());
        viewHolder.timeTv.setText(TimeUtil.formatTime((int) arrayList.get(i).getmTime()));

        viewHolder.contentVg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myListView.isOpen()) {
                    int lastPosition = myListView.getLastPosition();
                    MyItemListView myItemListView = (MyItemListView) myListView.getChildAt(lastPosition);
                    myItemListView.closeNume();
                    myListView.setOpen(false);
                }
                myListView.setLastPosition(i);
                myClickListener.contentClickListener(i);
            }
        });

        viewHolder.deleteB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myListView.isOpen()) {
                    int lastPosition = myListView.getLastPosition();
                    MyItemListView myItemListView = (MyItemListView) myListView.getChildAt(lastPosition);
                    myItemListView.closeNume();
                    myListView.setOpen(false);
                }
                myListView.setLastPosition(i);
                myClickListener.deleteClickListener(i);
            }
        });

        viewHolder.cancelB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myListView.isOpen()) {
                    int lastPosition = myListView.getLastPosition();
                    MyItemListView myItemListView = (MyItemListView) myListView.getChildAt(lastPosition);
                    myItemListView.closeNume();
                    myListView.setOpen(false);
                }
                myListView.setLastPosition(i);
                myClickListener.cancelClickListener(i);
            }
        });

        return view;
    }

    class ViewHolder {
        ImageView imageView;
        TextView nameTv;
        TextView timeTv;
        Button deleteB;
        Button cancelB;

        ViewGroup contentVg;
        ViewGroup numeVg;
        MyListView myListView;


        private ViewHolder(ViewGroup contentVg, ViewGroup numeVg, MyListView myListView) {
            this.contentVg = contentVg;
            this.numeVg = numeVg;
            this.myListView = myListView;
            imageView = contentVg.findViewById(R.id.local_content_pic_iv);
            nameTv = contentVg.findViewById(R.id.local_content_name_tv);
            timeTv = contentVg.findViewById(R.id.local_content_time_tv);
            deleteB = numeVg.findViewById(R.id.local_nume_delete_b);
            cancelB = numeVg.findViewById(R.id.local_nume_cancel_b);
        }
    }
}
