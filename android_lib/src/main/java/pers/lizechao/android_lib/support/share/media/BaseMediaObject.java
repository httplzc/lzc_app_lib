package pers.lizechao.android_lib.support.share.media;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

import pers.lizechao.android_lib.storage.file.FileStoreUtil;
import pers.lizechao.android_lib.support.img.utils.ImageUtils;

/**
 * Created by Lzc on 2018/6/22 0022.
 */
public abstract class BaseMediaObject implements IMediaObject, Parcelable {
    protected final File thumb;

    public BaseMediaObject(File thumb) {
        this.thumb = thumb;
    }

    public byte[] getThumbBytes() {
        return ImageUtils.bmpToByteArray(FileStoreUtil.loadBitmap(thumb), true);
    }

    public String getThumbPath() {
        return thumb.getPath();
    }
    public File getThumbFile() {
        return thumb;
    }

    public Uri getThumbUri() {
        return Uri.fromFile(thumb);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.thumb);
    }

    protected BaseMediaObject(Parcel in) {
        this.thumb = (File) in.readSerializable();
    }

    public String toDataPath() {
        return isUrl() ? toUrl() : toLocalPath();
    }

}
