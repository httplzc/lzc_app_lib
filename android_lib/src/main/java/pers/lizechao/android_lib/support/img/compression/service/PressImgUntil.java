package pers.lizechao.android_lib.support.img.compression.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import pers.lizechao.android_lib.support.img.compression.comm.PressConfig;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by Lzc on 2017/7/26 0026.
 */

public class PressImgUntil {
    final static int ignoreSize = 1024 * 50;
    static final int imgMemoryMaxWidth = 10000;
    static final int imgMemoryMaxHeight = 10000;
    private static final float longImgRatio = 3f;

    static void longImgOption(Context context, Uri uri, PressConfig option) {
        if (!option.specialDealLongImg)
            return;
        try {
            ImgMsg imgMsg = getImgWidthAndHeight(context, uri);
            if (imgMsg == null) {
                return;
            }
            float ratio = (float) imgMsg.width / imgMsg.height;
            //超宽图
            if (ratio > longImgRatio) {
                option.maxWidth = imgMemoryMaxWidth;
            }
            //超长图
            else if (1 / ratio > longImgRatio) {
                option.maxHeight = imgMemoryMaxHeight;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static class ImgMsg {
        public int width;
        public int height;

        ImgMsg(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public ImgMsg() {
        }
    }

    private static ImgMsg getImgWidthAndHeight(Context context, Uri uri) throws IOException {
        InputStream input = context.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        if (input != null) {
            input.close();
        }
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        //Log.i(BuildConfig.LibTAG,"uri"+uri+"--- "+originalWidth+"--- "+originalHeight);
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        return new ImgMsg(originalWidth, originalHeight);
    }


    //计算压缩消耗内存
    public static int calcPressTaskMemoryUse(Context context, ServiceImgPressTask task) {
        int memory = 0;
        final int imgMemoryRatio = 2;
        int targetWidth = task.option.maxWidth;
        int targetHeight = task.option.maxHeight;
        Uri uri = Uri.fromFile(task.originFile);
        try {
            //获取图片宽高
            ImgMsg imgMsg = getImgWidthAndHeight(context, uri);
            if (imgMsg == null)
                return 0;
            //长宽比例
            float widthRatio = (float) imgMsg.width / targetWidth;
            float heightRatio = (float) imgMsg.height / targetHeight;
            int ratio = (int) (widthRatio > heightRatio ? widthRatio : heightRatio);
            if (ratio < 1)
                ratio = 1;
            //第一次处理后宽高
            int originWidth = 0;
            int originHeight = 0;
            ratio = Integer.highestOneBit(ratio);
            originWidth = imgMsg.width / ratio;
            originHeight = imgMsg.height / ratio;
            //计算内存
            memory += originWidth * originHeight * imgMemoryRatio;

            //计算第二次处理
            float secondWidthRadio = (float) originWidth / (float) targetWidth;
            float secondHeightRadio = (float) originHeight / (float) targetHeight;
            float secondRadio = secondWidthRadio > secondHeightRadio ? secondWidthRadio : secondHeightRadio;
            if (secondRadio > 1) {
                memory += (originWidth / secondRadio) * (originHeight / ratio) * imgMemoryRatio;
            }
            memory += targetWidth * targetHeight * task.option.pressRadio;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return memory;
    }
}
