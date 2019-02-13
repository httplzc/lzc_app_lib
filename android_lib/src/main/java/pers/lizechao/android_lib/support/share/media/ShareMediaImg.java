package pers.lizechao.android_lib.support.share.media;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import pers.lizechao.android_lib.utils.FileUntil;

import java.io.File;
import java.util.Objects;

/**
 * Created by Lzc on 2018/6/22 0022.
 */
public class ShareMediaImg extends BaseMediaObject implements Parcelable {
    private Uri imgUri;
    private Uri[] imgUris;

    public ShareMediaImg(File thumb, Uri imgUrl) {
        super(thumb);
        this.imgUri = imgUrl;
    }

    public ShareMediaImg(File thumb, Uri[] imgUris) {
        super(thumb);
        this.imgUris = imgUris;
    }

    protected ShareMediaImg(Parcel in) {
        super(in);
        imgUri = in.readParcelable(Uri.class.getClassLoader());
        imgUris = in.createTypedArray(Uri.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(imgUri, flags);
        dest.writeTypedArray(imgUris, flags);
    }

    public Uri[] getImgUris() {
        return imgUris;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ShareMediaImg> CREATOR = new Creator<ShareMediaImg>() {
        @Override
        public ShareMediaImg createFromParcel(Parcel in) {
            return new ShareMediaImg(in);
        }

        @Override
        public ShareMediaImg[] newArray(int size) {
            return new ShareMediaImg[size];
        }
    };

    @Override
    public String toUrl() {
        return imgUri.toString();
    }

    @Override
    public String toLocalPath() {
        return Objects.requireNonNull(FileUntil.UriToFile(imgUri)).getPath();
    }

    @Override
    public boolean isUrl() {
        return imgUri.toString().contains("http") || imgUri.toString().contains("https");
    }

    @Override
    public boolean isPath() {
        return FileUntil.isFile(imgUri);
    }
}
