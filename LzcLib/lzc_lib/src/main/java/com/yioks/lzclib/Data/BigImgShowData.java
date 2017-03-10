package com.yioks.lzclib.Data;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.yioks.lzclib.Untils.StringManagerUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${User} on 2017/2/23 0023.
 */

public class BigImgShowData implements Parcelable {
    private List<Uri> uriList = new ArrayList<>();
    //    private List<Integer> resList = new ArrayList<>();


    public void setData(Uri uri) {
        uriList.clear();
        uriList.add(uri);
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

    public void setUriList(List<Uri> uriList) {
        this.uriList = uriList;
    }

    public void setResList(List<Integer> resList, Resources resources) {
        uriList.clear();
        for (Integer integer : resList) {
            uriList.add(StringManagerUtil.resToUri(integer, resources));
        }
    }

    public void setPathList(List<String> pathList) {
        uriList.clear();
        for (String s : pathList) {
            uriList.add(Uri.parse(s));
        }
    }

    public void setData(Integer res, Resources resources) {
        uriList.clear();
        uriList.add(StringManagerUtil.resToUri(res, resources));
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
            source.readList(stringList, getClass().getClassLoader());
            return dealAfter(stringList, new BigImgShowData());
        }
    };
}
