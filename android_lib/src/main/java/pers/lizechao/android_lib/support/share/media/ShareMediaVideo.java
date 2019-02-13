package pers.lizechao.android_lib.support.share.media;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.Objects;

import pers.lizechao.android_lib.utils.FileUntil;

/**
 * Created by Lzc on 2018/6/22 0022.
 */
public class ShareMediaVideo extends BaseMediaObject implements Parcelable {

    private final Uri videoUri;

    public ShareMediaVideo(File thumb, Uri videoUri) {
        super(thumb);
        this.videoUri = videoUri;
    }

    @Override
    public String toUrl() {
        return videoUri.toString();
    }

    @Override
    public String toLocalPath() {
        return Objects.requireNonNull(FileUntil.UriToFile(videoUri)).getPath();
    }

    @Override
    public boolean isUrl() {
        return videoUri.toString().contains("http") || videoUri.toString().contains("https");
    }

    @Override
    public boolean isPath() {
        return FileUntil.isFile(videoUri);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.videoUri, flags);
    }

    private ShareMediaVideo(Parcel in) {
        super(in);
        this.videoUri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<ShareMediaVideo> CREATOR = new Creator<ShareMediaVideo>() {
        @Override
        public ShareMediaVideo createFromParcel(Parcel source) {
            return new ShareMediaVideo(source);
        }

        @Override
        public ShareMediaVideo[] newArray(int size) {
            return new ShareMediaVideo[size];
        }
    };
}
