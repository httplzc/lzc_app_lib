package com.yioks.lzclib.Data;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.yioks.lzclib.Untils.StringManagerUtil;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ${User} on 2017/2/23 0023.
 */

public class BigImgShowData implements Parcelable {
    private List<Uri> uriList = new ArrayList<>();
    //    private List<Integer> resList = new ArrayList<>();
    private HashMap<Integer, MessageUri> uriSparseArray = new HashMap<>();


    public void setData(Uri uri) {
        uriList.clear();
        uriList.add(uri);
    }

    public void setData(Uri uri, MessageUri messageUri) {
        uriList.clear();
        uriList.add(uri);
        uriSparseArray.put(0, messageUri);
    }

    public static class MessageUri implements Serializable {
        private int width;
        private int height;
        private int centerX;
        private int centerY;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getCenterX() {
            return centerX;
        }

        public void setCenterX(int centerX) {
            this.centerX = centerX;
        }

        public int getCenterY() {
            return centerY;
        }

        public void setCenterY(int centerY) {
            this.centerY = centerY;
        }

        public MessageUri(int width, int height, int centerX, int centerY) {
            this.width = width;
            this.height = height;
            this.centerX = centerX;
            this.centerY = centerY;
        }

        public MessageUri(View xiangPian)
        {
            this.setWidth(xiangPian.getWidth());
            this.setHeight(xiangPian.getHeight());
            int  location[]=new int[2];
            xiangPian.getLocationOnScreen(location);
            this.setCenterX(location[0]+xiangPian.getWidth()/2);
            this.setCenterY(location[1]+xiangPian.getHeight()/2);
        }

        public MessageUri() {
        }
    }


    public void setData(File file) {
        setData(Uri.fromFile(file));
    }

    public void setFileList(List<File> fileList) {
        uriList.clear();
        for (File file : fileList) {
            uriList.add(Uri.fromFile(file));
        }
    }

    public void setFileList(List<File> fileList, List<MessageUri> messageUriList) {
        uriList.clear();
        messageUriList.clear();
        for (int i = 0; i < fileList.size(); i++) {
            messageUriList.add(i, messageUriList.get(i));
            uriList.add(Uri.fromFile(fileList.get(i)));
        }
    }

    public void setUriList(List<Uri> uriList) {
        this.uriList = uriList;
    }

    public void setUriList(List<Uri> uriList, List<MessageUri> messageUriList) {
        this.uriList = uriList;
        uriSparseArray.clear();
        for (int i = 0; i < messageUriList.size(); i++) {
            uriSparseArray.put(i, messageUriList.get(i));
        }
    }

    public void setResList(List<Integer> resList, Resources resources) {
        uriList.clear();
        for (Integer integer : resList) {
            uriList.add(StringManagerUtil.resToUri(integer, resources));
        }
    }

    public void setResList(List<Integer> resList, Resources resources, List<MessageUri> messageUriList) {
        uriList.clear();
        messageUriList.clear();
        for (int i = 0; i < resList.size(); i++) {
            uriList.add(StringManagerUtil.resToUri(resList.get(i), resources));
            uriSparseArray.put(i, messageUriList.get(i));
        }
    }

    public void setPathList(List<String> pathList) {
        uriList.clear();
        for (String s : pathList) {
            uriList.add(Uri.parse(s));
        }
    }

    public void setPathList(List<String> pathList, List<MessageUri> messageUriList) {
        uriList.clear();
        uriSparseArray.clear();
        for (int i = 0; i < pathList.size(); i++) {
            uriList.add(Uri.parse(pathList.get(i)));
            uriSparseArray.put(i, messageUriList.get(i));
        }
    }

    public void setData(Integer res, Resources resources) {
        uriList.clear();
        uriList.add(StringManagerUtil.resToUri(res, resources));
    }

    public void setData(Integer res, Resources resources, MessageUri messageUri) {
        uriList.clear();
        uriSparseArray.clear();
        uriList.add(StringManagerUtil.resToUri(res, resources));
        uriSparseArray.put(0, messageUri);
    }

    public List<Uri> getUriList() {
        return uriList;
    }


    public int getCount() {
        return uriList.size();
    }

    public void setData(String path) {
        uriList.clear();
        uriList.add(Uri.parse(path));
    }

    public void setData(String path, MessageUri messageUri) {
        uriList.clear();
        uriSparseArray.clear();
        uriList.add(Uri.parse(path));
        uriSparseArray.put(0, messageUri);
    }

    ;


    public Uri getData(int position) {
        return uriList.get(position);
    }

    private List<String> dealBefore() {
        List<String> stringList = new ArrayList<>();
        if (uriList != null) {
            for (Uri uri : uriList) {
                stringList.add(uri.toString());
            }
            return stringList;
        }
        return stringList;
    }

    private static BigImgShowData dealAfter(List<String> stringList, BigImgShowData bigImgShowData) {
        bigImgShowData.uriList = new ArrayList<>();
        if (stringList != null) {
            for (String s : stringList) {
                bigImgShowData.uriList.add(Uri.parse(s));
            }
        }
        return bigImgShowData;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(dealBefore());
        dest.writeMap(uriSparseArray);
    }

    public static final Creator<BigImgShowData> CREATOR = new Creator<BigImgShowData>() {

        /**
         * 供外部类反序列化本类数组使用
         */
        @Override
        public BigImgShowData[] newArray(int size) {
            return new BigImgShowData[size];
        }

        /**
         * 从Parcel中读取数据
         */
        @Override
        public BigImgShowData createFromParcel(Parcel source) {
            List<String> stringList = new ArrayList<>();
            HashMap<Integer, MessageUri> uriSparseArray = new HashMap<>();
            source.readList(stringList, getClass().getClassLoader());
            source.readMap(uriSparseArray, getClass().getClassLoader());
            BigImgShowData bigImgShowData = dealAfter(stringList, new BigImgShowData());
            bigImgShowData.uriSparseArray = uriSparseArray;
            return bigImgShowData;
        }
    };


    public MessageUri getMessageUri(Integer integer) {
        return uriSparseArray.get(integer);
    }
}
