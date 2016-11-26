package com.yioks.lzclib.Helper;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;
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
        String[] projection = {MediaStore.Images.Media.DATE_MODIFIED, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

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
     * 获取全部图片
     *
     * @param cr
     * @return
     */
    public static List<Uri> GetAllPic(ContentResolver cr) {
        List<Uri> uriList = new ArrayList<>();
        String[] projection = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_MODIFIED};

        Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if (cursor == null) {
            return null;
        }
        while (cursor.moveToNext()) {
            String image_path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            File file = new File(image_path);
            if (file.exists() && file.length() != 0)
                uriList.add(Uri.fromFile(file));
        }
        cursor.close();
        return uriList;
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

}
