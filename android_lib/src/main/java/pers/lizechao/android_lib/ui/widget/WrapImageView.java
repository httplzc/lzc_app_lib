package pers.lizechao.android_lib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;

import pers.lizechao.android_lib.R;

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
 * Date: 2018-08-13
 * Time: 18:32
 */
public class WrapImageView extends SimpleDraweeView {
    private String imageUrl;
    private String imageUri;
    private int imageResource;
    private float imageWidth;
    private float imageHeight;
    private boolean noCache;

    public WrapImageView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

    public WrapImageView(Context context) {
        super(context);
    }

    public WrapImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public WrapImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(R.styleable.WrapImageView);
        imageUrl = typedArray.getString(R.styleable.WrapImageView_imageUrl);
        imageWidth = typedArray.getDimension(R.styleable.WrapImageView_imageWidth, 0);
        imageHeight = typedArray.getDimension(R.styleable.WrapImageView_imageHeight, 0);
        noCache = typedArray.getBoolean(R.styleable.WrapImageView_noCache, false);
        typedArray.recycle();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(float imageWidth) {
        this.imageWidth = imageWidth;
    }

    public float getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(float imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public int getImageResource() {
        return imageResource;
    }

    @Override
    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public boolean isNoCache() {
        return noCache;
    }

    public void setNoCache(boolean noCache) {
        this.noCache = noCache;
    }
}
