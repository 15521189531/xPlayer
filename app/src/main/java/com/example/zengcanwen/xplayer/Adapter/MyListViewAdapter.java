package com.example.zengcanwen.xplayer.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zengcanwen.xplayer.Bean.LocalVideoFileBean;
import com.example.zengcanwen.xplayer.R;
import com.example.zengcanwen.xplayer.Util.TimeUtil;
import com.example.zengcanwen.xplayer.View.MyItemListView;
import com.example.zengcanwen.xplayer.View.MyListView;

import java.util.ArrayList;

/**
 * Created by zengcanwen on 2017/11/28.
 */

public class MyListViewAdapter extends BaseAdapter {

    private ArrayList<LocalVideoFileBean> arrayList = new ArrayList<>() ;
    private Context context ;
    private MyListView myListView ;
    private MyItemListView myItemListView ;

    public interface MyClickListener{
        void contentClickListener(int position , String filePath) ;
        void cancelClickListener(int position , String filePath) ;
        void deleteClickListener(int position , String filePath) ;
    }

    private MyClickListener myClickListener ;

    public void setMyClickListener(MyClickListener myClickListener){
        this.myClickListener = myClickListener ;
    }

    public void setArrayList(ArrayList<LocalVideoFileBean> arrayList) {
        this.arrayList = arrayList;

    }

    public MyListViewAdapter(Context context , MyListView myListView) {
        this.context = context;
        this.myListView = myListView ;

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
        ViewHolder viewHolder = null ;

        if(view == null){
            myItemListView = new MyItemListView(context) ;
            ViewGroup contentVg = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.local_content_layout , null) ;
            ViewGroup numeVg = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.local_nemu_layout , null) ;
            myItemListView.setView(myListView , contentVg , numeVg);
            viewHolder = new ViewHolder(contentVg , numeVg , myListView) ;
            myListView.setMyClickListener(new MyListView.MyClickListener() {
                @Override
                public void contentClickListener(int position) {
                    myClickListener.contentClickListener(position , arrayList.get(position).getmPath());
                }

                @Override
                public void deleteClickListener(int position) {
                    myClickListener.deleteClickListener(position , arrayList.get(position).getmPath());
                }

                @Override
                public void cancleClickListener(int position) {
                    myClickListener.cancelClickListener(position , arrayList.get(position).getmPath());
                }
            });
            myItemListView.setTag(viewHolder);
            view = myItemListView ;
        }else {
            viewHolder = (ViewHolder)view.getTag() ;
        }
        viewHolder.nameTv.setText(arrayList.get(i).getmName());
        viewHolder.imageView.setImageBitmap(arrayList.get(i).getmBitmap());
        viewHolder.timeTv.setText(TimeUtil.formatTime((int)arrayList.get(i).getmTime()));

        return view;
    }

    class ViewHolder{
        ImageView imageView ;
        TextView nameTv ;
        TextView timeTv ;
        Button deleteB ;
        Button cancelB ;

        ViewGroup contentVg  ;
        ViewGroup numeVg   ;
        MyListView myListView ;


        public ViewHolder(ViewGroup contentVg, ViewGroup numeVg, MyListView myListView) {
            this.contentVg = contentVg;
            this.numeVg = numeVg;
            this.myListView = myListView;
            imageView = (ImageView)contentVg.findViewById(R.id.local_content_pic_iv) ;
            nameTv = (TextView)contentVg.findViewById(R.id.local_content_name_tv) ;
            timeTv = (TextView)contentVg.findViewById(R.id.local_content_time_tv) ;
            deleteB = (Button)numeVg.findViewById(R.id.local_nume_delete_b) ;
            cancelB = (Button)numeVg.findViewById(R.id.local_nume_cancel_b) ;
            this.contentVg  = contentVg ;
        }
    }
}
