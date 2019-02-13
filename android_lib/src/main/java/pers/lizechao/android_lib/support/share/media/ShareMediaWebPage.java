package pers.lizechao.android_lib.support.share.media;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by Lzc on 2018/6/22 0022.
 */
public class ShareMediaWebPage extends BaseMediaObject implements Parcelable {
    private final String webUrl;

    public ShareMediaWebPage(File thumb, String webUrl) {
        super(thumb);
        this.webUrl = webUrl;
    }

    @Override
    public String toUrl() {
        return webUrl;
    }

    @Override
    public String toLocalPath() {
        return null;
    }

    @Override
    public boolean isUrl() {
        return true;
    }

    @Override
    public boolean isPath() {
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.webUrl);
    }

    private ShareMediaWebPage(Parcel in) {
        super(in);
        this.webUrl = in.readString();
    }

    public static final Creator<ShareMediaWebPage> CREATOR = new Creator<ShareMediaWebPage>() {
        @Override
        public ShareMediaWebPage createFromParcel(Parcel source) {
            return new ShareMediaWebPage(source);
        }

        @Override
        public ShareMediaWebPage[] newArray(int size) {
            return new ShareMediaWebPage[size];
        }
    };
}
