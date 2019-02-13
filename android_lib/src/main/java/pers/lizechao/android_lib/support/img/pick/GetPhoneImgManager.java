package pers.lizechao.android_lib.support.img.pick;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Lzc on 2018/6/13 0013.
 */
public class GetPhoneImgManager {
    public static List<Photo> selectPhotos(ContentResolver resolver) {
        return selectPhotos(resolver, null);
    }

    public static void sortPhotosByTime(List<Photo> photoList) {
        Collections.sort(photoList, (o1, o2) -> Long.compare(o2.getTime_modified(), o1.getTime_modified()));
    }

    public static List<Photo> selectPhotos(ContentResolver resolver, String albumId) {
        if (albumId == null) {
            List<Album> albumList = selectAlbumsReal(resolver, albumId);
            if (albumList == null || albumList.size() == 0)
                return null;
            return albumList.get(0).getPhotoList();
        } else {
            List<Album> albumList = selectAlbums(resolver);
            List<Photo> photoList = new ArrayList<>();
            if (albumList != null) {
                for (Album album : albumList) {
                    photoList.addAll(album.getPhotoList());
                }
            }
            sortPhotosByTime(photoList);
            return photoList;
        }

    }

    //查询所有相册和对应图片
    public static List<Album> selectAlbums(ContentResolver resolver) {
        return selectAlbumsReal(resolver, null);
    }

    //根据相册id 查询所有相册和对应图片
    @Nullable
    private static List<Album> selectAlbumsReal(ContentResolver resolver, String albumId) {
        HashMap<String, Album> albumHashMap = new HashMap<>();
        String[] projection = {
          MediaStore.Images.Media.DATA,
          MediaStore.Images.Media.DATE_MODIFIED,
          MediaStore.Images.Media._ID,
          MediaStore.Images.Media.BUCKET_ID,
          MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
          MediaStore.Images.Media.MIME_TYPE,
          MediaStore.Images.Media.SIZE};
        String selection = null;
        String[] selectionArgs = null;
        if (albumId != null) {
            selection = MediaStore.Images.Media.BUCKET_ID + "=?";
            selectionArgs = new String[]{albumId};
        }
        Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
          selection, selectionArgs, MediaStore.Images.Media.DATE_MODIFIED + " desc");
        if (cursor == null) {
            return null;
        }
        while (cursor.moveToNext()) {
            String albumIdFind = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));
            Album album = albumHashMap.get(albumIdFind);
            if (album == null) {
                album = new Album();
                albumHashMap.put(albumIdFind, album);
            }
            album.setId(albumIdFind);
            album.setName(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)));
            Photo photo = new Photo();
            photo.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media._ID)));
            photo.setMime_type(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE)));
            photo.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)));
            photo.setSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE)));
            photo.setTime_modified(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)));
            album.getPhotoList().add(photo);
        }
        cursor.close();
        return new ArrayList<>(albumHashMap.values());
    }

}
