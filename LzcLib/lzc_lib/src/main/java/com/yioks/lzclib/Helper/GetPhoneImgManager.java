package com.yioks.lzclib.Helper;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.yioks.lzclib.Data.XiangceData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 获取手机图片
 * Created by Administrator on 2016/7/15 0015.
 */
public class GetPhoneImgManager {
    final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
    };

    public static void GetAlbumList(Activity activity) {
    }

    /**
     * 获取相册列表
     *
     * @param cr
     * @return
     */
    public static List<String> GetXiangCeList(ContentResolver cr) {
        List<String> stringList = new ArrayList<>();
        String[] projection = {MediaStore.Images.Media.DATE_MODIFIED, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if (cursor == null) {
            return null;
        }
        while (cursor.moveToNext()) {
            String parentPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
            stringList.add(parentPath);
        }
        cursor.close();
        return stringList;
    }


    /**
     * 获取相册列表
     *
     * @param cr
     * @return
     */
    public static HashMap<String, XiangceData> GetXiangCeHash(ContentResolver cr) {
        HashMap<String, XiangceData> xiangceDatas = new HashMap<>();
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_MODIFIED, MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if (cursor == null) {
            return xiangceDatas;
        }
        while (cursor.moveToNext()) {
            String parentPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
            String id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
            String image_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            File file = new File(image_path);
            if (file.exists() && file.length() != 0) {
                if (xiangceDatas.containsKey(id)) {
                    XiangceData xiangceData = xiangceDatas.get(id);
                    xiangceData.setCount(xiangceData.getCount() + 1);
                } else {
                    XiangceData xiangceData = new XiangceData();
                    xiangceData.setId(id);
                    xiangceData.setName(parentPath);
                    xiangceData.setUri(Uri.fromFile(file));
                    xiangceData.setCount(1);
                    xiangceDatas.put(id, xiangceData);
                }

            }
        }
        cursor.close();
        return xiangceDatas;
    }

    public static int getCountByXiangceID(ContentResolver cr, String id) {
        int count = 0;
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{"COUNT(" + MediaStore.Images.Media.DATA + ")", MediaStore.Images.Media.BUCKET_ID},
                MediaStore.Images.Media.BUCKET_ID + "=?", new String[]{id}, null);
        if (cursor == null || cursor.getCount() <= 0)
            return count;
        cursor.moveToFirst();
        count = cursor.getCount();
        cursor.close();
        return count;
    }


    /**
     * 获取全部图片
     *
     * @param cr
     * @return
     */
    public static List<Uri> GetAllPic(ContentResolver cr) {
        List<Uri> uriList = new ArrayList<>();
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_MODIFIED, MediaStore.Images.Media._ID};
        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if (cursor == null) {
            return null;
        }
        while (cursor.moveToNext()) {
            String image_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            String id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID));
            //    String smallFilePath=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
            File file = new File(image_path);
            //    File smallFile=new File(smallFilePath);
            if (file.exists() && file.length() != 0) {
//                if(smallFile.exists()&&smallFile.length()!=0)
//                {
//                    uriList.add(Uri.fromFile(smallFile));
//                }
//                else
//                {
                uriList.add(Uri.fromFile(file));

            }

        }
        cursor.close();

        return uriList;
    }


    public static Uri getThumbnail(ContentResolver cr, String id) {
        Log.i("lzc", " String id" + id);
        if (id == null)
            return null;
        String[] projection = {MediaStore.Images.Thumbnails.IMAGE_ID, MediaStore.Images.Thumbnails.DATA};
        Cursor cursor = cr.query(
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,//指定缩略图数据库的Uri
                projection,
                MediaStore.Images.Thumbnails.IMAGE_ID + "=?",
                new String[]{id},
                null);

        if (cursor == null || cursor.getCount() <= 0)
            return null;
        cursor.moveToFirst();
        String image_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA));
        File file = new File(image_path);
        if (file.exists() && file.length() != 0) {
            cursor.close();
            return Uri.fromFile(file);
        }
        return null;
    }

    public static String getSmallUri(ContentResolver contentResolver, Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);
        if (cursor == null)
            return null;
        cursor.moveToFirst();
        String id = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();
        return id;
    }


    /**
     * 根据相册名获取图片
     *
     * @param cr
     * @param parentName
     * @return
     */
    public static List<Uri> GetAllPicByParentName(ContentResolver cr, String parentName, boolean limit) {
        List<Uri> uriList = new ArrayList<>();
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_MODIFIED};
        Cursor cursor = null;
        if (limit) {
            cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Images.Media.BUCKET_DISPLAY_NAME + "=?", new String[]{parentName}, MediaStore.Images.Media.DATE_MODIFIED + " desc" + " limit 0,1");
        } else {
            cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Images.Media.BUCKET_DISPLAY_NAME + "=?", new String[]{parentName}, MediaStore.Images.Media.DATE_MODIFIED + " desc");
        }
        if (cursor == null) {
            return null;
        }
        while (cursor.moveToNext()) {
            String image_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            File file = new File(image_path);
            if (file.exists())
                uriList.add(Uri.fromFile(file));
        }
        cursor.close();
        return uriList;
    }

    /**
     * 根据相册名获取图片
     *
     * @param cr
     * @param parentId
     * @return
     */
    public static List<Uri> GetAllPicByParentId(ContentResolver cr, String parentId, int limit) {
        Log.i("lzc", "parentId" + parentId);
        List<Uri> uriList = new ArrayList<>();
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_MODIFIED};
        Cursor cursor = null;
        if (limit != -1) {
            cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Images.Media.BUCKET_ID + "=?", new String[]{parentId}, MediaStore.Images.Media.DATE_MODIFIED + " desc" + " limit 0,1");
        } else {
            cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Images.Media.BUCKET_ID + "=?", new String[]{parentId}, MediaStore.Images.Media.DATE_MODIFIED + " desc");
        }
        if (cursor == null) {
            return uriList;
        }
        while (cursor.moveToNext()) {
            String image_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            File file = new File(image_path);
            if (file.exists())
                uriList.add(Uri.fromFile(file));
        }
        cursor.close();
        return uriList;
    }

}
