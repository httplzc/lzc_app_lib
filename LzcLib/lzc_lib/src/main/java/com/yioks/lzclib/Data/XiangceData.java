package com.yioks.lzclib.Data;

import android.net.Uri;

/**
 * Created by Administrator on 2016/7/18 0018.
 */
public class XiangceData {
    private String name;
    private Uri uri;
    private int count;
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof  XiangceData)
        {
            XiangceData xiangceData= (XiangceData) obj;
            return xiangceData.getId().equals(id);
        }
       return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
