package com.example.zengcanwen.xplayer.online.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.Toast;

import com.example.zengcanwen.xplayer.Bean.EventBusBean;
import com.example.zengcanwen.xplayer.Bean.EventBusServiceBean;
import com.example.zengcanwen.xplayer.Bean.NetVideosDataBean;
import com.example.zengcanwen.xplayer.Util.SPUtil;
import com.example.zengcanwen.xplayer.online.download.HttpGetPic;
import com.example.zengcanwen.xplayer.online.download.MyDownService;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

import static com.example.zengcanwen.xplayer.Bean.EventBusServiceBean.SENDMESSAGE;
import static com.example.zengcanwen.xplayer.Bean.VideoSaveBean.ONLINE_TAG;


/**
 * 在线视频Fragment的model层实现类
 * Created by zengcanwen on 2018/1/26.
 */

public class OnlineModelImpl implements OnlineModel {
    private ArrayList<NetVideosDataBean> mNetFileList;
    private Context mContext;
    private Handler mHandler;
    private Intent mIntent;
    public static final int SEARCHFILEONLINESUCCESS = 101;
    public static final int SEARCHFILEONLINEFAIL = 102;
    public static final int SETBITMAP = 103;

    private HashMap<Integer, Boolean> isDownignHM;
    private HashMap<Integer, Integer> progressHM;
    private HashMap<Integer, Boolean> isFinishHM;
    public Boolean isDowning = false;     //判断是否正在下载
    public Boolean isDownFinish = false;  //判断是否已经下载完成

    private LruCache<String, Drawable> lruCache;    //用于缓存从网络得到的图片,使用Drawable的目的是占用空间比Bitmap小
    private int cacheSize;                           //设置网络图片缓存区的大小

    private int lastFirstVisibleItem;
    private HashMap<Integer, Boolean> isHintHM;     //记录子元素是否隐藏


    public OnlineModelImpl(Context mContext, Handler mHandler) {
        this.mContext = mContext;
        this.mHandler = mHandler;
    }

    @Override
    public void init() {
        mNetFileList = new ArrayList<>();
        isDownignHM = new HashMap<>();
        progressHM = new HashMap<>();
        isFinishHM = new HashMap<>();
        isHintHM = new HashMap<>();
    }

    @Override
    public void searchOnlineFile() {
        MyThread myThread = new MyThread(this);
        myThread.start();
    }


    @Override
    public void downloadFile(NetVideosDataBean netVideosDataBean, int position) {
        mIntent = new Intent(mContext, MyDownService.class);
        mIntent.putExtra("netViedeosDataBean", netVideosDataBean);
        mIntent.putExtra("position", position);
        mContext.startService(mIntent);
    }

    @Override
    public void pauseFile(NetVideosDataBean netVideosDataBean) {
        EventBusServiceBean eventBusServiceBean = new EventBusServiceBean();
        eventBusServiceBean.setTag(SENDMESSAGE);
        eventBusServiceBean.setUrl(netVideosDataBean.getVideo_url());
        EventBus.getDefault().post(eventBusServiceBean);
    }

    @Override
    public void clickLlayout(int position) {
        EventBusBean eventBusBean = new EventBusBean();
        NetVideosDataBean netVideosDataBean = mNetFileList.get(position);
        eventBusBean.setName(netVideosDataBean.getTitle());
        eventBusBean.setPath(netVideosDataBean.getVideo_url());
        eventBusBean.setTag(ONLINE_TAG);
        EventBus.getDefault().post(eventBusBean);
    }

    @Override
    public void downingCallback(EventBusServiceBean eventBusServiceBean, int position) {
        int progress = eventBusServiceBean.getProgress();
        progressHM.put(position, progress);
        isDownignHM.put(position, true);

    }

    @Override
    public void pauseCallback(int position) {
        isDownignHM.put(position, false);
    }

    @Override
    public void finishCallback(int position) {
        progressHM.put(position, 100);
        isDownignHM.put(position, false);
        isFinishHM.put(position, true);

    }

    @Override
    public boolean stopEBandService() {
        if (isDownignHM != null && isDownignHM.size() != 0) {
            Iterator<Map.Entry<Integer, Boolean>> iterator = isDownignHM.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Boolean> entry = iterator.next();
                if (entry.getValue()) {
                    return false;
                }
            }
            if (mIntent != null) {
                mContext.stopService(mIntent);
            }
        }
        return true;
    }

    @Override
    public void setCache() {
        cacheSize = (int) (Runtime.getRuntime().maxMemory() / 8L);      //设置8分之一的缓存空间进行图片缓存
        lruCache = new LruCache<String, Drawable>(cacheSize) {
            @Override
            protected int sizeOf(String key, Drawable value) {
                if (value instanceof BitmapDrawable) {
                    Bitmap bitmap = ((BitmapDrawable) value).getBitmap();
                    int size = (bitmap == null) ? 0 : bitmap.getByteCount();
                    return size;

                }

                return super.sizeOf(key, value);
            }
        };
    }

    @Override
    public void updatePic(String url, final int position) {
        //先判断缓存区是否已经存在该对象
        Drawable drawable = lruCache.get(String.valueOf(position));
        if (drawable != null) {
            Message message = new Message();
            message.what = SETBITMAP;
            message.obj = ((BitmapDrawable) drawable).getBitmap();
            message.arg1 = position;
            mHandler.sendMessage(message);
            return;
        }

        //没有就去网络获取
        HttpGetPic.getInstance().getHttp(url, null, new HttpGetPic.OkhttpResponse() {
            @Override
            public void onError(Call call, IOException e) {
                Toast.makeText(mContext, "加载图片失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) {
                InputStream inputStream = response.body().byteStream();
                //简单的图片压缩
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                final Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                //加入缓存
                lruCache.put(String.valueOf(position), new BitmapDrawable(bitmap));
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what = SETBITMAP;
                        message.obj = bitmap;
                        message.arg1 = position;
                        mHandler.sendMessage(message);
                    }
                });
            }
        });
    }


    @Override
    public boolean isdownFinish(int position) {
        isDownFinish = isFinishHM.get(position);
        if (null == isDownFinish) return false;
        else return isDownFinish;
    }

    @Override
    public boolean isdowning(int position) {
        isDowning = isDownignHM.get(position);
        if (null == isDowning) return false;
        return isDowning;
    }

    @Override
    public HashMap<Integer, Integer> getProgressHM() {
        if (progressHM != null) {
            return progressHM;
        } else {
            return null;
        }
    }

    @Override
    public ArrayList<NetVideosDataBean> getDataList() {
        if (null != mNetFileList) {
            return mNetFileList;
        } else {
            return null;
        }
    }

    @Override
    public void getDataFormHM() {
        for (int i = 0; i < mNetFileList.size(); i++) {
            NetVideosDataBean netVideosDataBean = mNetFileList.get(i);
            File file = new File(mContext.getFilesDir(), "myXplayer");
            if (!file.exists()) {
                return;
            } else {
                File downloadFile = new File(file, netVideosDataBean.getTitle());
                if (downloadFile.exists()) {
                    isFinishHM.put(i, true);
                    progressHM.put(i, 100);
                } else {
                    File cacheFile = new File(file, netVideosDataBean.getTitle() + "cache");
                    if (!cacheFile.exists()) {
                        isFinishHM.put(i, false);
                        progressHM.put(i, 0);
                    } else {
                        Long fileLength = SPUtil.getInstance().getLong(netVideosDataBean.getTitle());
                        Long cacheFileLength = cacheFile.length();
                        if (fileLength == null || cacheFileLength == null) {
                            isFinishHM.put(i, false);
                            progressHM.put(i, 0);
                        } else {
                            isFinishHM.put(i, false);
                            progressHM.put(i, (int) (cacheFileLength * 100 / fileLength));
                        }
                    }
                }
            }
        }
    }


    @Override
    public void listViewScroll(int firstVisibleItem, int visibleItemCount) {
        int position ;
        if (firstVisibleItem > lastFirstVisibleItem) {  //向下滑
            position = firstVisibleItem + visibleItemCount - 1;
            isHintHM.put(position, false);
            lastFirstVisibleItem = firstVisibleItem;
        } else if (firstVisibleItem < lastFirstVisibleItem) {  //向下滑
            position = firstVisibleItem;
            isHintHM.put(position, false);
            lastFirstVisibleItem = firstVisibleItem;
        }
    }

    @Override
    public void listViewRecycle(int position) {
        isHintHM.put(position, true);
    }

    @Override
    public boolean isHind(int position) {
        if (null == isHintHM.get(position)) return false;
        else return isHintHM.get(position);
    }


    private static class MyThread extends Thread {
        private WeakReference<OnlineModelImpl> weakReference;

        private MyThread(OnlineModelImpl onlineModelImpl) {
            weakReference = new WeakReference<>(onlineModelImpl);
        }

        @Override
        public void run() {
            OnlineModelImpl onlineModelImpl = weakReference.get();
            int resquestTime = 0;
            while (resquestTime < 5) {
                String videoDatasJson = edu.xplayerapi.HttpApi.getVideos(onlineModelImpl.mContext);
                try {
                    JSONObject jsonObject = new JSONObject(videoDatasJson);
                    JSONArray jsonArray = jsonObject.getJSONArray("video_list");
                    if (jsonArray == null || jsonArray.length() == 0) {
                        //请求失败
                        resquestTime++;    //如果失败，重复请求5次
                    } else {
                        //请求成功
                        for (int i = 0; i < jsonArray.length(); i++) {
                            NetVideosDataBean netVideosDataBean = new NetVideosDataBean();
                            jsonObject = jsonArray.getJSONObject(i);
                            netVideosDataBean.setTitle(jsonObject.getString("title"));
                            netVideosDataBean.setPreview_url(jsonObject.getString("preview_url"));
                            netVideosDataBean.setVideo_url(jsonObject.getString("video_url"));
                            onlineModelImpl.mNetFileList.add(netVideosDataBean);
                        }
                        onlineModelImpl.mHandler.sendMessage(onlineModelImpl.mHandler.obtainMessage(SEARCHFILEONLINESUCCESS));    //请求成功
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //最终请求失败
            onlineModelImpl.mHandler.sendMessage(onlineModelImpl.mHandler.obtainMessage(SEARCHFILEONLINEFAIL));
        }
    }

}
