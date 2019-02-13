package pers.lizechao.android_lib.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;

import com.annimon.stream.Optional;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import pers.lizechao.android_lib.data.ApplicationData;


/**
 * Created by Lzc on 2016/5/12.
 */
public class FileUntil {


    public static boolean isFile(Uri uri) {
        return ContentResolver.SCHEME_FILE.equals(uri.getScheme());
    }


    /**
     * 转化uri为file
     *
     * @param contentUri
     * @param
     * @return
     */
    public static File UriToFile(Uri contentUri) {
        return Optional.ofNullable(UriUtils.getRealPathFromUri(ApplicationData.applicationContext.getContentResolver(), contentUri))
          .map(File::new).orElse(null);
    }


    /**
     * 获取文件下大小
     *
     * @param file 文件或文件夹
     * @return 总大小
     */
    public static double getFileSize(File file) {
        if (!file.exists()) {
            return 0;
        }
        if (file.isDirectory()) {
            double length = 0;
            File[] files = file.listFiles();
            for (File file1 : files) {
                length += getFileSize(file1);
            }
            return length;
        } else {
            return file.length() / 1024d / 1024d;
        }
    }


    public static List<File> getAllFile(File file) {
        List<File> fileList = new ArrayList<>();
        if (file.isDirectory()) {
            File files[] = file.listFiles();
            if (files != null)
                for (File file1 : files) {
                    if (file1.isDirectory())
                        fileList.addAll(getAllFile(file1));
                    else
                        fileList.add(file1);
                }
        } else {
            fileList.add(file);
        }
        return fileList;
    }


    public static byte[] inputStreamToByte(InputStream is) {
        try {
            ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
            int ch;
            while ((ch = is.read()) != -1) {
                bytestream.write(ch);
            }
            byte imgdata[] = bytestream.toByteArray();
            bytestream.close();
            return imgdata;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void callImgFileUpdate(Context context, File file, String mimeType) {
        MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, new String[]{mimeType}, null);
    }

    public static void move(File source, File target) throws IOException {
        copy(source, target);
        source.delete();
    }

    public static void copy(File source, File target) throws IOException {
        if (source == null || !source.exists())
            throw new FileNotFoundException();
        if (target == null || !target.exists())
            throw new FileNotFoundException();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } else {
            FileInputStream inputStream = new FileInputStream(source);
            FileOutputStream outputStream = new FileOutputStream(target);
            FileChannel inputChannel = inputStream.getChannel();
            FileChannel outputChannel = outputStream.getChannel();
            MappedByteBuffer mappedByteBuffer = inputChannel.map(FileChannel.MapMode.READ_ONLY, 0, inputChannel.size());
            outputChannel.write(mappedByteBuffer);
            inputChannel.force(true);
            outputChannel.force(true);
            inputChannel.close();
            outputChannel.close();
        }

    }

}
