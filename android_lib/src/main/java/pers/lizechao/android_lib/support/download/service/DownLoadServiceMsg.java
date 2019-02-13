package pers.lizechao.android_lib.support.download.service;

import android.os.Parcel;
import android.os.Parcelable;

import pers.lizechao.android_lib.support.download.comm.DownLoadMsg;

import io.reactivex.disposables.Disposable;

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
 * Time: 17:07
 */
class DownLoadServiceMsg implements Parcelable {
     String url;
    //唯一标识
     String uuid;
    //目标文件
     String targetFilePath;

     boolean isOnlyWifi = true;

     Disposable disposable;

     DownLoadServiceMsg() {
    }

     DownLoadServiceMsg(DownLoadMsg downLoadMsg) {
        url = downLoadMsg.url;
        uuid = downLoadMsg.uuid;
        targetFilePath = downLoadMsg.targetFilePath;
        isOnlyWifi = downLoadMsg.downLoadConfig.onlyWifi;
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
        dest.writeInt(isOnlyWifi ? 1 : 0);
    }

    public static final Creator<DownLoadServiceMsg> CREATOR = new ClassLoaderCreator<DownLoadServiceMsg>() {
        @Override
        public DownLoadServiceMsg createFromParcel(Parcel source, ClassLoader loader) {
            return createFromParcel(source);
        }

        @Override
        public DownLoadServiceMsg createFromParcel(Parcel source) {
            DownLoadServiceMsg downLoadMsg = new DownLoadServiceMsg();
            downLoadMsg.url = source.readString();
            downLoadMsg.uuid = source.readString();
            downLoadMsg.targetFilePath = source.readString();
            downLoadMsg.isOnlyWifi = source.readInt() == 1;
            return downLoadMsg;
        }

        @Override
        public DownLoadServiceMsg[] newArray(int size) {
            return new DownLoadServiceMsg[size];
        }
    };
}
