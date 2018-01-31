package com.example.zengcanwen.xplayer.local.other;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;

import com.example.zengcanwen.xplayer.Bean.LocalVideoFileBean;
import com.example.zengcanwen.xplayer.Util.DipandPxUtli;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import static android.provider.MediaStore.Images.Thumbnails.MICRO_KIND;

public class FileUtil {

    private Context context ;

    public static String FILEPATHBASE ;

    public FileUtil(Context context ){
        this.context = context  ;
        FILEPATHBASE = context.getFilesDir() + "/" + "myXplayer" + "/" ;
    }


    //扫描本地视频，VideoView只支持。3gp , MP4 , avi 这三种格式
    //缺点：通过扫描SDcard的全部文件和文件夹得到视频文件，
    // 扫描的时间长达30s,看起来好像是连缓存下来的文件也找了一遍，这种方法可能比较实用与GoSpeed中的文件扫描
    public void getVideoFormLocal(final ArrayList<LocalVideoFileBean> arrayList , File file){
        try {
           file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    String nameStr = file.getName();
                    int i = nameStr.indexOf(".");
                    if (i != -1) {
                        nameStr = nameStr.substring(i);
                        if (nameStr.equalsIgnoreCase(".3gp")
                                || nameStr.equalsIgnoreCase(".MP4")
                                || nameStr.equalsIgnoreCase(".avi")) {
                            String filePath = file.getAbsolutePath();     //视频绝对路径
                            Bitmap bitmap = getVideoThumb(filePath , DipandPxUtli.dip2px(context ,90)
                                    , DipandPxUtli.dip2px(context , 90) , MICRO_KIND) ;
                            //保存扫描出来的视频
                            LocalVideoFileBean localVideoFileBean = new LocalVideoFileBean();
                            localVideoFileBean.setmPath(filePath);
                            localVideoFileBean.setmName(file.getName());
                            localVideoFileBean.setmBitmap(bitmap);
                            arrayList.add(localVideoFileBean);
                            Log.i("aaaaa", file.getName());
                            return true;
                        }
                    } else if (file.isDirectory()) {
                        getVideoFormLocal(arrayList, file);
                    }
                    return false;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //通过直接查找系统的ContentProvider得到视频文件
    //优点：查找速度快，1s左右 ， 只搜索下载的视频
    public void findVideo(final  ArrayList<LocalVideoFileBean> arrayList ){
                Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);

        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)); // id
                String displayName =cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM)); // 专辑
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST)); // 艺术家
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)); // 显示名称
                String mimeType =cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)); // 路径
                long duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)); // 时长
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)); // 大小
                String resolution =cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION));
                Bitmap bitmap =  getVideoThumb(path , DipandPxUtli.dip2px(context ,90)
                        , DipandPxUtli.dip2px(context , 90) , MICRO_KIND) ;
                LocalVideoFileBean localVideoFileBean = new LocalVideoFileBean() ;
                localVideoFileBean.setmBitmap(bitmap);
                localVideoFileBean.setmName(displayName);
                localVideoFileBean.setmPath(path);
                localVideoFileBean.setmTime(duration);
                localVideoFileBean.setSize(size);
                arrayList.add(localVideoFileBean) ;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
    }


    //获取视频的缩略图
    private Bitmap getVideoThumb(String videoPath , int width , int height , int kind){
        Bitmap bitmap = null ;
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath , kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap , width , height , ThumbnailUtils.OPTIONS_RECYCLE_INPUT) ;
        return bitmap ;
    }
}