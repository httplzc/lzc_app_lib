package com.yioks.lzclib.Untils;

import android.app.Activity;
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

import com.yioks.lzclib.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 李泽超 on 2016/5/12.
 */
public class FileUntil {
    private static final String DirName = "yioks_club";
    private static final String innerFileName = "yioks_temp";
    public static String tempFilePath = "";

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
     * 保存图片并获取url地址
     *
     * @param context
     * @param bitmap
     * @param fileName
     * @return
     */
    public static Uri saveImageAndGetURI(Context context, Bitmap bitmap, String fileName) {

        return Uri.fromFile(saveImageAndGetFile(context, bitmap, fileName));


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


    /**
     * 存储文件，返回文件
     *
     * @param context
     * @param bitmap
     * @param fileName
     * @return
     */
    public static File saveImageAndGetFile(Context context, Bitmap bitmap, String fileName) {
        if (bitmap == null) {
            return null;
        }
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, 512, 512);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileNametemp = simpleDateFormat.format(new Date(System.currentTimeMillis()));
        fileName = fileNametemp + fileName;
        FileOutputStream fos = null;
        File file = null;
        try {
            File tempFile = getTempDir();
            if (tempFile == null) {
                throw new IOException();
            }
            //创建图片文件
            file = new File(tempFile.getPath() + File.separator + fileName + ".png");
            file.deleteOnExit();
            file.createNewFile();
            fos = new FileOutputStream(file);
            if (fos != null) {

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }
//            Looper.prepare();
//            Toast.makeText(context, "图片已保存到相册", Toast.LENGTH_SHORT).show();
//            Looper.loop();

        } catch (IOException e) {
            e.printStackTrace();
//            Looper.prepare();
//            Toast.makeText(context, "保存图片失败", Toast.LENGTH_SHORT).show();
//            Looper.loop();
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
     * 存入临时文件
     *
     * @param context
     * @param bitmap
     * @return
     */
    public static File WriteToTeamPic(Context context, Bitmap bitmap) {
        return saveImageAndGetFile(context, bitmap, "");
    }

    /**
     * 根据传入Uri得到压缩过的Bitmap
     *
     * @param ac
     * @param uri
     * @param tagerWidth
     * @param tagerHeight
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     * @throws OutOfMemoryError
     */
    public static Bitmap getBitmapFormUri(Activity ac, Uri uri, float tagerWidth, float tagerHeight) throws FileNotFoundException, IOException, OutOfMemoryError {
        Bitmap bitmap = null;
        try {
            InputStream input = ac.getContentResolver().openInputStream(uri);
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
            float hh = tagerHeight;
            float ww = tagerWidth;

            int be = 1;

            if (originalWidth > originalHeight && originalWidth > ww) {
                be = (int) (originalWidth / ww);
            } else if (originalWidth < originalHeight && originalHeight > hh) {
                be = (int) (originalHeight / hh);
            }
            if (be <= 0)
                be = 1;

            if (originalWidth * originalHeight < tagerWidth * tagerHeight) {
                be = 1;
            }


            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = be;
            bitmapOptions.inDither = true;
            bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
            input = ac.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
            if (input != null) {
                input.close();
            }
            if (bitmap != null) {
                Log.i("lzc", "bitmap.isRecycled()1111" + bitmap.isRecycled());
            }
            bitmap = compressSize(bitmap, tagerWidth, tagerHeight);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError error) {
            return BitmapFactory.decodeResource(ac.getResources(), R.drawable.holder);
        }
        return bitmap;
    }

    /**
     * 压缩图片（大小）
     *
     * @param bitmap
     * @param tagerWidth
     * @param tagerHeight
     * @return
     */
    public static Bitmap compressSize(Bitmap bitmap, float tagerWidth, float tagerHeight) {
        if (bitmap.isRecycled()) {
            return null;
        }
        if (bitmap.getWidth() * bitmap.getHeight() <= tagerHeight * tagerWidth) {
            return bitmap;
        }

        if (bitmap.getWidth() > tagerWidth || bitmap.getHeight() > tagerHeight) {
            int originalWidth = bitmap.getWidth();
            int originalHeight = bitmap.getHeight();
            float hh = tagerHeight;
            float ww = tagerWidth;
            int newwidth = originalWidth;
            int newheight = originalHeight;
            if (bitmap.getWidth() > tagerHeight || bitmap.getHeight() > tagerHeight) {
                float be1 = ww / originalWidth;
                float be2 = hh / originalHeight;
                float be;
                if (originalWidth > originalHeight) {
                    be = be2;
                } else {
                    be = be1;
                }
                if (be < 1) {
                    newwidth = (int) (originalWidth * be);
                    newheight = (int) (originalHeight * be);
                }

            }

            if (newwidth < 480) {
                newwidth = 480;
            }
            if (newheight < 480) {
                newheight = 480;
            }
            Log.i("lzc", newwidth + "--" + newheight);
            Bitmap bitmap1 = ThumbnailUtils.extractThumbnail(bitmap, newwidth, newheight);
            return bitmap1;
        }
        return bitmap;
    }

    /**
     * 压缩图片（大小）
     *
     * @param image
     * @param outputStream
     * @throws IOException
     */
    public static void compressImage(Bitmap image, OutputStream outputStream) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        int limitszie = image.getWidth() * image.getHeight() / (12288);
        while (baos.toByteArray().length / 1024f > limitszie) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 5;
        }
        image.compress(Bitmap.CompressFormat.JPEG, options, outputStream);
        image.recycle();
        baos.close();
        outputStream.close();
    }

    public static File getTempFile() {
        return new File(tempFilePath);
    }

    public static File createTempFile() {
        File file = null;
        File tempFile = getTempDir();
        if (tempFile == null) {
            return null;
        }

        //创建图片文件
        file = new File(tempFile.getPath() + File.separator + "" + System.currentTimeMillis() + "tempimg.jpg");
        if (file.exists()) {
            file.delete();

        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tempFilePath = file.getPath();
        return file;
    }

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

}
