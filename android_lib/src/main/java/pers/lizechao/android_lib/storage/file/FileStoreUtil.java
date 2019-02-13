package pers.lizechao.android_lib.storage.file;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.annimon.stream.function.Consumer;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Created by Lzc on 2018/6/7 0007.
 */
public class FileStoreUtil {
    public static void saveTextData(File file, String data, boolean append) throws IOException {
        BufferedWriter bufferedWriter = null;
        FileLock filelock = null;
        try {
            FileOutputStream  fileOutputStream = new FileOutputStream(file, append);
            filelock = fileOutputStream.getChannel().lock();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(data);
            bufferedWriter.flush();
        } finally {
            if (filelock != null)
                filelock.release();
            if (bufferedWriter != null)
                bufferedWriter.close();

        }
    }

    public static void saveInputStream(File file, InputStream inputStream, boolean append, Consumer<Long> consumer) throws IOException {
        FileLock fileLock = null;
        BufferedInputStream bufferedInputStream = null;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, append);
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileLock = fileChannel.lock();
            bufferedInputStream = new BufferedInputStream(inputStream);
            byte[] byteArray = new byte[2048];
            int bytesCount;
            long current = 0;
            while ((bytesCount = bufferedInputStream.read(byteArray)) != -1) {
                fileChannel.write(ByteBuffer.wrap(byteArray, 0, bytesCount));
                if (consumer != null) {
                    current += bytesCount;
                    consumer.accept(current);
                }

            }
            fileChannel.force(true);
        } finally {
            if (fileLock != null)
                fileLock.release();
            if (bufferedInputStream != null)
                bufferedInputStream.close();

        }
    }

    public static void saveInputStream(File file, InputStream inputStream, boolean append) throws IOException {
        saveInputStream(file, inputStream, append, null);
    }

    public static void saveByteData(File file, byte[] data, boolean append) throws IOException {
        FileOutputStream fileOutputStream = null;
        FileLock fileLock = null;
        try {
            fileOutputStream = new FileOutputStream(file, append);
            FileChannel fileChannel = fileOutputStream.getChannel();
            fileLock = fileChannel.lock();
            fileChannel.write(ByteBuffer.wrap(data));
            fileChannel.force(true);
        } finally {
            if (fileLock != null)
                fileLock.release();
            if (fileOutputStream != null)
                fileOutputStream.close();


        }
    }

    public static void saveBitmap(File file, Bitmap bitmap, Bitmap.CompressFormat compressFormat) throws IOException {
        FileOutputStream fos = null;
        FileLock fileLock = null;
        try {
            fos = new FileOutputStream(file);
            fileLock = fos.getChannel().lock();
            bitmap.compress(compressFormat, 100, fos);
        } finally {
            if (fileLock != null)
                fileLock.release();
            if (fos != null)
                fos.close();

        }
    }

    public static Bitmap loadBitmap(File file) {
        return BitmapFactory.decodeFile(file.getPath());
    }


    public static byte[] loadBytes(File file) throws IOException {
        FileInputStream fileInputStream = null;
        FileLock fileLock = null;
        try {
            fileInputStream = new FileInputStream(file);
            FileChannel channel = fileInputStream.getChannel();
            fileLock = channel.lock(0, file.length(), true);
            MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            byte[] backData = new byte[mappedByteBuffer.remaining()];
            mappedByteBuffer.get(backData, 0, backData.length);
            return backData;
        } finally {
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (fileLock != null) {
                try {
                    fileLock.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static byte[] loadByte(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            int length = 0;
            byte read[] = new byte[10 * 1024];
            while ((length = inputStream.read(read)) > 0) {
                byteArrayOutputStream.write(read, 0, length);
            }
            byteArrayOutputStream.flush();
            return byteArrayOutputStream.toByteArray();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String loadStr(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStreamReader  reader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(reader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } finally {
            if (bufferedReader != null)
                bufferedReader.close();
        }
    }

    public static String loadStr(File file) throws IOException {
        BufferedReader bufferedReader = null;
        FileLock fileLock = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileLock = fileInputStream.getChannel().lock(0, file.length(), true);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String temp;
            StringBuilder stringBuilder = new StringBuilder();
            while ((temp = bufferedReader.readLine()) != null) {
                stringBuilder.append(temp);
                stringBuilder.append("\n");
            }
            return stringBuilder.toString();
        } finally {
            try {
                if (fileLock != null)
                    fileLock.release();
                if (bufferedReader != null)
                    bufferedReader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
