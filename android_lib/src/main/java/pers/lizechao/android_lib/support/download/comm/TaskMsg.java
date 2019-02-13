package pers.lizechao.android_lib.support.download.comm;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created with
 * ********************************************************************************
 * #         ___                     ________                ________             *
 * #       |\  \                   |\_____  \              |\   ____\             *
 * #       \ \  \                   \|___/  /|             \ \  \___|             *
 * #        \ \  \                      /  / /              \ \  \                *
 * #         \ \  \____                /  /_/__              \ \  \____           *
 * #          \ \_______\             |\________\             \ \_______\         *
 * #           \|_______|              \|_______|              \|_______|         *
 * #                                                                              *
 * ********************************************************************************
 * Date: 2018-07-20
 * Time: 15:53
 * 下载任务基本信息
 */
public class TaskMsg implements Parcelable{
    //总大小
    public long totalLength;
    public String contentType;
    public String uuid;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.totalLength);
        dest.writeString(this.contentType);
    }

    public TaskMsg(String uuid) {
        this.uuid = uuid;
    }

    public TaskMsg() {
    }

    protected TaskMsg(Parcel in) {
        this.totalLength = in.readLong();
        this.contentType = in.readString();
    }

    public static final Creator<TaskMsg> CREATOR = new Creator<TaskMsg>() {
        @Override
        public TaskMsg createFromParcel(Parcel source) {
            return new TaskMsg(source);
        }

        @Override
        public TaskMsg[] newArray(int size) {
            return new TaskMsg[size];
        }
    };
}
