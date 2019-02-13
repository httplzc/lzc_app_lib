package pers.lizechao.android_lib.support.img.compression.manager;

import android.arch.lifecycle.LifecycleOwner;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;

import pers.lizechao.android_lib.common.DestroyListener;
import pers.lizechao.android_lib.support.img.compression.comm.PressConfig;

/**
 * Created by Lzc on 2018/6/11 0011.
 */
public class ImgPressTask implements Parcelable {
    private final String uuid;
    private Uri uris[];
    private PressConfig config = new PressConfig();

    public String getUuid() {
        return uuid;
    }

    public ImgPressTask() {
        uuid = UUID.randomUUID().toString();
    }

    public ImgPressTask setUris(Uri[] uris) {
        this.uris = uris;
        return this;
    }

    public ImgPressTask setUri(Uri uri) {
        uris = new Uri[]{uri};
        return this;
    }

    public ImgPressTask setConfig(PressConfig config) {
        this.config = config;
        return this;
    }


    public Uri[] getUris() {
        return uris;
    }

    public PressConfig getConfig() {
        return config;
    }

    //开始压缩
    public ImgPressTask startPress() {
        ImgPressManager.getInstance().startPress(this);
        return this;
    }

    public ImgPressTask stopPress() {
        ImgPressManager.getInstance().stopPress(uuid);
        return this;
    }

    public ImgPressTask setCallback(LifecycleOwner lifecycleOwner, PressCallBack pressCallBack) {
        ImgPressManager.getInstance().registerCallBack(uuid, pressCallBack);
        lifecycleOwner.getLifecycle().addObserver(new DestroyListener(this::unregisterCallBack));
        return this;
    }

    public ImgPressTask unregisterCallBack() {
        ImgPressManager.getInstance().unregisterCallBack(uuid);
        return this;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uuid);
        dest.writeTypedArray(this.uris, flags);
        dest.writeSerializable(this.config);
    }

    protected ImgPressTask(Parcel in) {
        this.uuid = in.readString();
        this.uris = in.createTypedArray(Uri.CREATOR);
        this.config = (PressConfig) in.readSerializable();
    }

    public static final Creator<ImgPressTask> CREATOR = new Creator<ImgPressTask>() {
        @Override
        public ImgPressTask createFromParcel(Parcel source) {
            return new ImgPressTask(source);
        }

        @Override
        public ImgPressTask[] newArray(int size) {
            return new ImgPressTask[size];
        }
    };
}
