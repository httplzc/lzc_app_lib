package pers.lizechao.android_lib.support.share.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import pers.lizechao.android_lib.support.share.media.BaseMediaObject;
import pers.lizechao.android_lib.support.share.media.IMediaObject;
import pers.lizechao.android_lib.support.share.media.ShareMediaImg;
import pers.lizechao.android_lib.support.share.media.ShareMediaVideo;
import pers.lizechao.android_lib.support.share.media.ShareMediaWebPage;

/**
 * Created by Lzc on 2018/6/22 0022.
 */
public class ShareContent implements Parcelable {
    public String mText;
    public String mSubject;
    public BaseMediaObject mMedia;



    public ShareDateType getShareType() {
        if ((this.mMedia == null)) {
            if (TextUtils.isEmpty(this.mText)) {
                throw new IllegalArgumentException("分享内容不能为空！");
            }
            return ShareDateType.TEXT;
        } else {
            if ((this.mMedia instanceof ShareMediaImg)) {
                return ShareDateType.IMG;
            }
            if ((this.mMedia instanceof ShareMediaVideo)) {
                return ShareDateType.VIDEO;
            }
            if ((this.mMedia instanceof ShareMediaWebPage))
                return ShareDateType.WEB_PAGE;
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mText);
        dest.writeString(this.mSubject);
        dest.writeParcelable(this.mMedia, flags);
    }

    public ShareContent() {
    }

    protected ShareContent(Parcel in) {
        this.mText = in.readString();
        this.mSubject = in.readString();
        this.mMedia = in.readParcelable(IMediaObject.class.getClassLoader());
    }

    public static final Creator<ShareContent> CREATOR = new Creator<ShareContent>() {
        @Override
        public ShareContent createFromParcel(Parcel source) {
            return new ShareContent(source);
        }

        @Override
        public ShareContent[] newArray(int size) {
            return new ShareContent[size];
        }
    };
}
