package pers.lizechao.android_lib.support.download.comm;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.UUID;


/**
 * Created by Lzc on 2018/2/9 0009.
 * 下载基本信息
 */

public class DownLoadMsg implements Parcelable {
    //   未开始 下载中   停止中  下载完毕
    public enum DownLoadState {
        UnStart, Downloading, Stop, Finish
    }

    public DownLoadMsg() {
        this.uuid = UUID.randomUUID().toString();
    }

    /**
     *
     * @param uuid 请保证uuid为唯一，否则会导致任务错乱！
     */
    public DownLoadMsg(String uuid) {
        this.uuid = uuid;
    }


    //下载地址
    public String url;
    //唯一标识
    public String uuid;
    //目标文件
    public String targetFilePath;
    public DownLoadState currentState = DownLoadState.UnStart;
    //当前进度
    public int progress = 0;
    //开始时间
    public long startTime;
    //结束时间
    public long endTime;
    //上一次启动
    public long lastStart;
    //总时长
    public long totalTime;

    //文件总大小
    private long totalFileLength;
    //服务器传回的文件名
    private String serviceFileName;

    public DownLoadConfig downLoadConfig = getDefaultConfig();

    private static DownLoadConfig getDefaultConfig() {
        DownLoadConfig downLoadConfig = new DownLoadConfig();
        downLoadConfig.onlyWifi = true;
        downLoadConfig.showByNotification = true;
        return downLoadConfig;
    }


    //重置
    public void reset() {
        currentState = DownLoadState.UnStart;
        progress = 0;
        startTime = 0;
        endTime = 0;
        totalTime = 0;
    }

    public long getTotalFileLength() {
        return totalFileLength;
    }

    public String getServiceFileName() {
        return serviceFileName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(uuid);
        dest.writeString(targetFilePath);
        dest.writeInt(currentState.ordinal());
        dest.writeInt(progress);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
        dest.writeLong(lastStart);
        dest.writeLong(totalTime);
        dest.writeLong(totalFileLength);
        dest.writeString(serviceFileName);
        dest.writeSerializable(downLoadConfig);
    }


    public static final Creator<DownLoadMsg> CREATOR = new ClassLoaderCreator<DownLoadMsg>() {
        @Override
        public DownLoadMsg createFromParcel(Parcel source, ClassLoader loader) {
            return createFromParcel(source);
        }

        @Override
        public DownLoadMsg createFromParcel(Parcel source) {
            DownLoadMsg downLoadMsg = new DownLoadMsg();
            downLoadMsg.url = source.readString();
            downLoadMsg.uuid = source.readString();
            downLoadMsg.targetFilePath = source.readString();
            downLoadMsg.currentState = DownLoadState.values()[source.readInt()];
            downLoadMsg.progress = source.readInt();
            downLoadMsg.startTime = source.readLong();
            downLoadMsg.endTime = source.readLong();
            downLoadMsg.lastStart = source.readLong();
            downLoadMsg.totalTime = source.readLong();
            downLoadMsg.totalFileLength = source.readLong();
            downLoadMsg.serviceFileName = source.readString();
            downLoadMsg.downLoadConfig = (DownLoadConfig) source.readSerializable();
            return downLoadMsg;
        }

        @Override
        public DownLoadMsg[] newArray(int size) {
            return new DownLoadMsg[size];
        }
    };
}
