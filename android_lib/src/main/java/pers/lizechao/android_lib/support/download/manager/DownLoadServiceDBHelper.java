package pers.lizechao.android_lib.support.download.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import pers.lizechao.android_lib.support.download.comm.DownLoadConfig;
import pers.lizechao.android_lib.support.download.comm.DownLoadMsg;

/**
 * Created by Lzc on 2017/7/12 0012.
 */

public class DownLoadServiceDBHelper extends SQLiteOpenHelper {
    private static DownLoadServiceDBHelper downLoadServiceDBHelper;
    private static final String dbName = "download_service_db";

    public DownLoadServiceDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DownLoadServiceDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public static DownLoadServiceDBHelper getInstance(Context context) {
        if (downLoadServiceDBHelper == null) {
            synchronized (DownLoadServiceDBHelper.class) {
                if (downLoadServiceDBHelper == null) {
                    downLoadServiceDBHelper = new DownLoadServiceDBHelper(context, dbName, null, 1);
                }
            }
        }
        return downLoadServiceDBHelper;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS download_service_db" +
                    "(uuid VARCHAR(50) PRIMARY KEY,url VARCHAR(255),targetFilePath VARCHAR(255)," +
                    "currentState INTEGER,progress INTEGER,value TEXT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public static byte[] serializeObject(Object object) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (byteArrayOutputStream != null&&objectOutputStream!=null) {
                try {
                    byteArrayOutputStream.close();
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public static Object deSerializeObject(byte[] str) {
        Object object = null;
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(str);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            object = objectInputStream.readObject();
            return object;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static void saveDataToDB(Context applicationContext, DownLoadMsg downLoadMsg) {
        DownLoadServiceDBHelper downLoadServiceDBHelper = DownLoadServiceDBHelper.getInstance(applicationContext);
        SQLiteDatabase sqLiteDatabase = downLoadServiceDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("uuid", downLoadMsg.uuid);
        contentValues.put("url", downLoadMsg.url);
        contentValues.put("targetFilePath", downLoadMsg.targetFilePath);
        contentValues.put("currentState", downLoadMsg.currentState.ordinal());
        contentValues.put("progress", downLoadMsg.progress);
        contentValues.put("value", DownLoadServiceDBHelper.serializeObject(downLoadMsg.downLoadConfig));
        sqLiteDatabase.replace(dbName, null, contentValues);
    }

    public static DownLoadMsg getDataFromDB(Context applicationContext, String uuid) {
        DownLoadServiceDBHelper downLoadServiceDBHelper = DownLoadServiceDBHelper.getInstance(applicationContext);
        SQLiteDatabase sqLiteDatabase = downLoadServiceDBHelper.getReadableDatabase();
        DownLoadMsg downLoadMsg = new DownLoadMsg();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + dbName + " WHERE uuid=?", new String[]{uuid});
        if (cursor.moveToNext()) {
            downLoadMsg.downLoadConfig = (DownLoadConfig) DownLoadServiceDBHelper.deSerializeObject
                    (cursor.getBlob(cursor.getColumnIndex("value")));
            downLoadMsg.uuid = cursor.getString(cursor.getColumnIndex("uuid"));
            downLoadMsg.url = cursor.getString(cursor.getColumnIndex("url"));
            downLoadMsg.targetFilePath = cursor.getString(cursor.getColumnIndex("targetFilePath"));
            downLoadMsg.currentState = DownLoadMsg.DownLoadState.values()[cursor.getInt(cursor.getColumnIndex("currentState"))];
            downLoadMsg.progress = cursor.getInt(cursor.getColumnIndex("progress"));
        } else {
            return null;
        }
        cursor.close();
        return downLoadMsg;
    }

    public static void clearHistory(Context applicationContent) {
        DownLoadServiceDBHelper downLoadServiceDBHelper = DownLoadServiceDBHelper.getInstance(applicationContent);
        SQLiteDatabase sqLiteDatabase = downLoadServiceDBHelper.getWritableDatabase();
        sqLiteDatabase.delete(dbName, "currentState<>?", new String[]{"1"});
    }

    public static void updateData(Context applicationContext, DownLoadMsg downLoadMsg) {
        int currentState = downLoadMsg.currentState.ordinal();
        int progress = downLoadMsg.progress;
        String uuid = downLoadMsg.uuid;
        DownLoadServiceDBHelper downLoadServiceDBHelper = DownLoadServiceDBHelper.getInstance(applicationContext);
        SQLiteDatabase sqLiteDatabase = downLoadServiceDBHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if (currentState != -1)
            contentValues.put("currentState", currentState);
        if (progress != -1)
            contentValues.put("progress", progress);
        sqLiteDatabase.update(dbName, contentValues, "uuid=?", new String[]{uuid});
    }

    public static void deleteData(Context applicationContext, String uuid) {
        DownLoadServiceDBHelper downLoadServiceDBHelper = DownLoadServiceDBHelper.getInstance(applicationContext);
        SQLiteDatabase sqLiteDatabase = downLoadServiceDBHelper.getWritableDatabase();
        sqLiteDatabase.delete(dbName, "uuid=?", new String[]{uuid});
    }
}
