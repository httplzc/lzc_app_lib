package pers.lizechao.android_lib.support.img.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.support.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Lzc on 2018/6/28 0028.
 */
public class ImageUtils {

    public static Bitmap compressImgPixel(Bitmap bitmap, int maxWidth, int maxHeight) {
        float widthRadio = (float) bitmap.getWidth() / (float) maxWidth;
        float heightRadio = (float) bitmap.getHeight() / (float) maxHeight;
        float radio = widthRadio > heightRadio ? widthRadio : heightRadio;
        if (radio > 1) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, (int) (bitmap.getWidth() / radio), (int) (bitmap.getHeight() / radio));
        }
        return bitmap;
    }

    public static byte[] compressImgSize(Bitmap bitmap, int limitSize, int ignoreSize) {
        ByteArrayOutputStream imgBytes = new ByteArrayOutputStream();
        byte data[];
        int options = 100;
        do {
            imgBytes.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, imgBytes);
            float ratio = (float) imgBytes.size() / limitSize;
            if (ratio > 2)
                options -= 30;
            else if (ratio > 1)
                options -= 25;
            else
                options -= 20;
        } while (imgBytes.size() > limitSize && imgBytes.size() > ignoreSize && options > 20);
        data = imgBytes.toByteArray();
        return data;
    }


    public static Bitmap loadBitmapAndCompress(Context context, Uri uri, int targetWidth, int targetHeight) throws IOException {
        return loadBitmapAndCompress(() -> context.getContentResolver().openInputStream(uri), targetWidth, targetHeight);
    }

    public static Bitmap loadBitmapAndCompress(File file, int targetWidth, int targetHeight) throws IOException {
        return loadBitmapAndCompress(() -> new FileInputStream(file), targetWidth, targetHeight);
    }


    public interface IOExceptionSupplier<T> {
        T get() throws IOException;
    }

    public static Bitmap loadBitmapAndCompress(IOExceptionSupplier<InputStream> inputStreamSupplier, int targetWidth, int targetHeight) throws IOException {
        InputStream inputStream = inputStreamSupplier.get();
        if (inputStream == null)
            return null;
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        BitmapFactory.decodeStream(inputStream, null, onlyBoundsOptions);
        inputStream.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            throw new IOException("读取长宽错误");
        float widthRatio = (float) originalWidth / (float) targetWidth;
        float heightRatio = (float) originalHeight / (float) targetHeight;
        int ratio = (int) (widthRatio > heightRatio ? widthRatio : heightRatio);
        if (ratio < 1)
            ratio = 1;
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = ratio;
        bitmapOptions.inDither = true;
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        inputStream = inputStreamSupplier.get();
        if (inputStream == null)
            return null;
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, bitmapOptions);
        inputStream.close();
        Bitmap result = compressImgPixel(bitmap, targetWidth, targetHeight);
        if (result != bitmap)
            bitmap.recycle();
        return result;
    }


    public static int getBitmapUseMemory(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {     //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }


    public static int readPictureDegree(String imgPath) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(imgPath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotateBitmap(Bitmap img, int angle) {
        if (angle == 0)
            return img;
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle); /*翻转90度*/
            int width = img.getWidth();
            int height = img.getHeight();
            img = Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return img;
    }


    public static void setFilePictureDegree(File file, int degree) {
        try {
            ExifInterface exifInterface = new ExifInterface(file.getPath());
            int orientation = ExifInterface.ORIENTATION_NORMAL;
            switch (degree) {
                case 90:
                    orientation = ExifInterface.ORIENTATION_ROTATE_90;
                    break;
                case 180:
                    orientation = ExifInterface.ORIENTATION_ROTATE_180;
                    break;
                case 270:
                    orientation = ExifInterface.ORIENTATION_ROTATE_270;
                    break;
            }
            exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, orientation + "");
            exifInterface.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, output);
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (needRecycle) {
            bmp.recycle();
        }
        return result;
    }


}
