package pers.lizechao.android_lib.support.img.compression.comm;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Lzc on 2018/6/11 0011.
 */
public class PressConfig implements Serializable ,Cloneable{
    private final static int maxWidthDefault = 1080;
    private final static int maxHeightDefault = 1920;
    public final static float pressRadioDefault = 0.3f;
    public int maxWidth = maxWidthDefault;
    public int maxHeight = maxHeightDefault;
    public  float pressRadio = pressRadioDefault;
    public  int compressFormat = Bitmap.CompressFormat.JPEG.ordinal();
    //是否特殊处理长图
    public final boolean specialDealLongImg = true;

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
