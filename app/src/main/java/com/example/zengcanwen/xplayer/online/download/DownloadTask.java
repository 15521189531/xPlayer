package com.example.zengcanwen.xplayer.online.download;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.example.zengcanwen.xplayer.Bean.NetVideosDataBean;
import com.example.zengcanwen.xplayer.Util.SPUtil;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 实现断点续传功能的主要类
 * Created by zengcanwen on 2017/12/18
 */

public class DownloadTask extends Handler {
    private NetVideosDataBean netVideosDataBean;
    private Context context;

    private boolean isDownloading = false;
    private HttpDowload httpUtil2;
    private boolean pause;

    //下载文件的状态
    private final int MSG_FINISH = 1;
    private final int MSG_DOWNLOAD = 2;
    private final int MSG_PAUSE = 3;

    private DownListener mListener;

    //初始化下载管理器
    public DownloadTask(NetVideosDataBean netVideosDataBean, DownListener mListener, Context context) {
        this.netVideosDataBean = netVideosDataBean;
        this.mListener = mListener;
        httpUtil2 = HttpDowload.getInstance();
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        if (null == mListener) return;

        switch (msg.what) {
            case MSG_FINISH:
                resetStutus();
                if (mListener != null) {
                    mListener.Success();
                }
                break;

            case MSG_DOWNLOAD:
                int progress = msg.arg1;
                if (mListener != null) {
                    mListener.Downing(progress);
                }
                break;

            case MSG_PAUSE:
                resetStutus();
                if (mListener != null) {
                    mListener.pause();
                }
                break;
        }
    }


    public void start(final int position) {
        try {
            if (isDownloading) {
                return;
            }
            isDownloading = true;
            final long startPoints;
            //储存下载的目录
            String pathStr = isExistDir();
            final File filecache = new File(pathStr, netVideosDataBean.getTitle() + "cache");
            final File downFinishFile = new File(pathStr, netVideosDataBean.getTitle());
            if (downFinishFile.exists() && downFinishFile.isFile()) {
                //已经下载完成
                Toast.makeText(context, "已经下载完成", Toast.LENGTH_SHORT).show();
                return;
            }
            if (filecache.exists() && filecache.isFile()) {
                startPoints = filecache.length();
            } else {
                startPoints = 0;
            }

            final RandomAccessFile randomAccessFile = new RandomAccessFile(filecache, "rwd"); //随机访问文件，可以指定断点续传的指定位置 , 找到目的文件并指明权限
            randomAccessFile.seek(startPoints);
            httpUtil2.downFileByRanger(netVideosDataBean.getVideo_url(), startPoints, new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    isDownloading = false;
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    InputStream inputStream = response.body().byteStream();
                    SPUtil.getInstance().save(netVideosDataBean.getTitle(), response.body().contentLength() + startPoints);
                    byte[] bytes = new byte[1024];
                    int len ;
                    long total = startPoints;  //记录当前下载了多少字节
                    int progress ;
                    while ((len = inputStream.read(bytes)) > 0) {
                        if (pause) {
                            close(inputStream, response, randomAccessFile);
                            sendEmptyMessage(MSG_PAUSE);
                            return;
                        }
                        randomAccessFile.write(bytes, 0, len);
                        total += len;
                        progress = (int) (total * 100 / (response.body().contentLength() + startPoints));

                        Message message = new Message();
                        message.what = MSG_DOWNLOAD;
                        message.arg1 = progress;
                        sendMessage(message);
                    }
                    close(inputStream, response, randomAccessFile);
                    renameFile(filecache, downFinishFile);
                    sendEmptyMessage(MSG_FINISH);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void renameFile(File oldFile, File newFile) {
        if (oldFile.exists()) {
            oldFile.renameTo(newFile);
        }
    }

    //关闭物理流
    private void close(Closeable... closeables) {
        int length = closeables.length;
        try {
            for(Closeable closeable : closeables){
                if(null != closeable){
                    closeable.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            for (int i = 0; i < length; i++) {
                closeables[i] = null;
            }
        }
    }

    //下载的文件夹是否存在
    private String isExistDir() {
        File file = new File(context.getFilesDir(), "myXplayer");
        if (!file.mkdirs()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }

    //重置下载状态
    private void resetStutus() {
        pause = false;
        isDownloading = false;
    }


    // 暂停
    public void pause() {
        pause = true;
    }

}
