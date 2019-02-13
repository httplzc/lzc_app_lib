package pers.lizechao.android_lib.storage.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lzc on 2018/5/24 0024.
 */
public class DbHelper extends SQLiteOpenHelper {
    private final static String TABLE_NAME = "storeDB";
    private final static String CreateTable = "CREATE TABLE IF NOT EXISTS " +
            TABLE_NAME +
            "(" +
            "data_key TEXT PRIMARY KEY," +
            "value TEXT," +
            "type VARCHAR(100) NOT NULL," +
            "expires INTEGER DEFAULT 0"+
            ")";
    private final static String DeleteTable = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DbHelper(Context context) {
        super(context, TABLE_NAME, null, 2);
    }

    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DeleteTable);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DeleteTable);
        onCreate(db);
    }


}
