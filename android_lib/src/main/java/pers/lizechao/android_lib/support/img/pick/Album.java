package pers.lizechao.android_lib.support.img.pick;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lzc on 2018/6/13 0013.
 */
public class Album {
    private String id;
    private String name;
    private List<Photo> photoList = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Photo> getPhotoList() {
        return photoList;
    }

    public void setPhotoList(List<Photo> photoList) {
        this.photoList = photoList;
    }

    @Nullable
    public String getAlbumImage()
    {
        if(photoList!=null&&photoList.size()!=0)
        {
            return photoList.get(0).getPath();
        }
        return null;
    }
}
