package com.yioks.lzclib.Untils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.yioks.lzclib.Service.PressImgService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by 李泽超 on 2016/5/12.
 */
public class FileUntil {
    private static final String DirName = "yioks";
    private static final String innerFileName = "yioks_temp_file";
    private static final String tempFileName = "tempFile";
    private final static float ignoreSize=1024*30;


    //存储文件返回uri
    public static File getTempDir() {
        //判断sd卡是否存在
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            //获取sdcard的根目录
            String sdPath = Environment.getExternalStorageDirectory().getPath();
            //创建程序自己创建的文件夹
            File tempFile = new File(sdPath + File.separator + DirName + "/" + innerFileName);

            if (!tempFile.exists()) {
                tempFile.mkdirs();

            }
            return tempFile;
        }
        return null;
    }


    /**
     * 转化uri为file
     *
     * @param contentUri
     * @param context
     * @return
     */
    public static String UriToFile(Uri contentUri, Context context) {
        if (contentUri.getScheme().equals("file")) {
            return contentUri.getPath();
        }
        String res = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getApplicationContext().getContentResolver().query(contentUri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                res = cursor.getString(column_index);
            }
            cursor.close();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static File saveImageAndGetFile(Bitmap bitmap, File file, float limitSize) {
        if (bitmap == null || file == null) {
            return null;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            if (limitSize != -1) {
                compressImage(bitmap, fos, limitSize);
            } else {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * @param bitmap
     * @param fileName
     * @param limitSize -1 则不压缩
     * @return
     */
    public static File saveImageAndGetFile(Bitmap bitmap, String fileName, float limitSize) {

        File file = new File(getTempDir() + File.separator + fileName + ".png");
        try {
            if (file.exists())
                file.delete();
            file.createNewFile();
            return saveImageAndGetFile(bitmap, file, limitSize);
        } catch (Exception e) {
            e.printStackTrace();
            return null;

        }
    }


    /**
     * @param
     * @param uri
     * @param targetWidth  限制宽度
     * @param targetHeight 限制高度
     * @return
     * @throws Exception
     */
    public static Bitmap getBitmapFromUri(Context context, Uri uri, float targetWidth, float targetHeight) throws Exception {
        Bitmap bitmap = null;
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
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        float widthRatio = originalWidth / targetWidth;
        float heightRatio = originalHeight / targetHeight;
        float ratio = widthRatio > heightRatio ? widthRatio : heightRatio;
        if (ratio < 1)
            ratio = 1;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = (int) ratio;
        bitmapOptions.inDither = true;
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        input = context.getContentResolver().openInputStream(uri);
        bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        if (input != null) {
            input.close();
        }
        return bitmap;
    }


    /**
     * 请在线程中执行
     *
     * @param
     * @param file
     * @param newFile
     * @param limitSizeRadio 1个像素占多大
     * @param maxWidth       最大高度
     * @param maxHeight      最小高度
     */
    public static void compressImg(Context context, File file, File newFile, float limitSizeRadio, int maxWidth, int maxHeight) {
        long time=System.currentTimeMillis();
        long dxTime=0;
        Log.i("lzc","time_1--"+dxTime);
        try {
            Bitmap bitmap;
            synchronized (PressImgService.class) {
                bitmap = getBitmapFromUri(context, Uri.fromFile(file), maxWidth, maxHeight);
            }
            dxTime=System.currentTimeMillis()-time;
            time=System.currentTimeMillis();
            Log.i("lzc","time_2--"+dxTime);
            if (bitmap == null)
                return;
            float widthRadio = (float) bitmap.getWidth() /(float) maxWidth;
            float heightRadio = (float) bitmap.getHeight() / (float)maxHeight;
            float radio = widthRadio > heightRadio ? widthRadio : heightRadio;
            Log.i("lzc","bitmap_beforte"+bitmap.getWidth()+"---"+bitmap.getHeight()+"-----"+radio);
            if (radio > 1) {
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, (int) (bitmap.getWidth() / radio), (int) (bitmap.getHeight() / radio));
            }
            Log.i("lzc","bitmap_after"+bitmap.getWidth()+"---"+bitmap.getHeight()+"-----"+radio);
            dxTime=System.currentTimeMillis()-time;
            time=System.currentTimeMillis();
            Log.i("lzc","time_3--"+dxTime);
            if (bitmap == null)
                return;
            Log.i("lzc","----litmatSize"+(bitmap.getWidth()*bitmap.getHeight())+"---"+limitSizeRadio);
            saveImageAndGetFile(bitmap, newFile, limitSizeRadio * bitmap.getWidth() * bitmap.getHeight());
            dxTime=System.currentTimeMillis()-time;
            Log.i("lzc","time_4--"+dxTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param image
     * @param outputStream
     * @param limitSize    单位byte
     * @throws IOException
     */
    public static void compressImage(Bitmap image, OutputStream outputStream, float limitSize) throws Exception {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length > limitSize&&baos.toByteArray().length> ignoreSize) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 15;
            Log.i("lzc","currentSize"+(baos.toByteArray().length/1024));
        }
        image.compress(Bitmap.CompressFormat.JPEG, options, outputStream);
        baos.close();
        outputStream.close();
    }

    /**
     * 获取临时文件
     *
     * @return
     */
    public static File getTempFile() {
        return new File(getTempDir() + File.separator + tempFileName);
    }

    /**
     * 获取临时文件
     *
     * @return
     */
    public static File getTempFile(String fileName) {
        return new File(getTempDir() + File.separator + fileName);
    }

    //创建临时文件
    public static File createTempFile() {
        return createTempFile(tempFileName);
    }


    //创建临时文件
    public static File createTempFile(String tempFileName) {
        File file = null;
        File tempFile = getTempDir();
        if (tempFile == null) {
            return null;
        }

        //创建图片文件
        file = new File(tempFile.getPath() + File.separator + tempFileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    /**
     * 获取文件下大小
     *
     * @param file
     * @return
     */
    public static double getFileSize(File file) {
        if (!file.exists()) {
            return 0;
        }
        if (file.isDirectory()) {
            double length = 0;
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                length += getFileSize(files[i]);
            }
            return length;
        } else {
            return file.length() / 1024d / 1024d;
        }
    }

    /**
     * 清空临时文件夹
     */
    public static void ClearTempFile() {
        File tempFile = getTempDir();
        if (tempFile == null) {
            return;
        }
        if (tempFile.exists()) {

            File file[] = tempFile.listFiles();
            DeleteThread deleteThread = new DeleteThread();
            deleteThread.files = file;
            deleteThread.start();
        }

    }


    //清空文件线程
    private static class DeleteThread extends Thread {
        public File files[];

        @Override
        public void run() {
            if (files != null && files.length > 0) {
                for (File file : files) {
                    synchronized (FileUntil.class) {
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
            }
        }
    }

    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
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

    public static Bitmap toturn(Bitmap img) {
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate(+90); /*翻转90度*/
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

}
