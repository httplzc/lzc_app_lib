package pers.lizechao.android_lib.support.share.media;

import android.os.Parcelable;

/**
 * Created by Lzc on 2018/6/22 0022.
 */
public interface IMediaObject extends Parcelable {
    String toUrl();

    String toLocalPath();

    boolean isUrl();

    boolean isPath();

}
