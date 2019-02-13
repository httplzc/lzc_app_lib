package pers.lizechao.android_lib.storage.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

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
 * Date: 2018/11/17 0017
 * Time: 11:31
 */
class DbJsonDao implements StoreDao<String> {
    private final DbHelper dbHelper;

    private final static String KeyName = "data_key";
    private final static String TypeName = "type";
    private final static String DataName = "value";
    private final static String Expires = "expires";
    private final String replaceHead;
    private final String selectHead;
    private final String clearAll;

    DbJsonDao(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
        replaceHead = String.format("REPLACE INTO %s (%s,%s,%s,%s) VALUES", dbHelper.getTableName(), KeyName, TypeName, DataName, Expires);
        selectHead = String.format("SELECT * FROM %s WHERE (%s=?)", dbHelper.getTableName(), KeyName);
        clearAll = String.format("DROP TABLE %s", dbHelper.getTableName());
    }


    //是否有效
    private boolean checkNotExpires(long expires) {
        return expires == 0 || expires >= System.currentTimeMillis();
    }


    @Override
    public void updateOrInsert(DataWrap<String> dataWrap) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataName, dataWrap.getData());
        contentValues.put(KeyName, dataWrap.getKey());
        contentValues.put(TypeName, dataWrap.getTypeStr());
        contentValues.put(Expires, dataWrap.getExpires());
        dbHelper.getWritableDatabase().replace(dbHelper.getTableName(), null, contentValues);
    }

    @Override
    public void updateOrInsert(List<? extends DataWrap<String>> data) {
        StringBuilder sqlHead = new StringBuilder(replaceHead).append("(");
        Object realDatas[] = new Object[4 * data.size()];
        for (int i = 0; i < data.size(); i++) {
            sqlHead.append("(?,?,?,?)").append(i != data.size() - 1 ? "," : ")");
            realDatas[i * 4] = data.get(i).getKey();
            realDatas[i * 4 + 1] = data.get(i).getTypeStr();
            realDatas[i * 4 + 2] = data.get(i).getData();
            realDatas[i * 4 + 3] = data.get(i).getExpires() + "";
        }
        dbHelper.getWritableDatabase().execSQL(sqlHead.toString(), realDatas);
    }

    @Override
    public void delete(String key) {
        dbHelper.getWritableDatabase().delete(dbHelper.getTableName(),
                KeyName + "=?", new String[]{key});
    }

    @Override
    public void deleteLike(String keyLike) {
        try {
            dbHelper.getWritableDatabase().delete(dbHelper.getTableName(), KeyName + " like ?", new String[]{"%"+keyLike+"%"});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(List<String> key) {
        if (key.size() == 0)
            return;
        StringBuilder where = new StringBuilder("(");
        String realDatas[] = new String[key.size()];
        for (int i = 0; i < key.size(); i++) {
            where.append(KeyName + "=?").append(i != key.size() - 1 ? " OR " : ")");
            realDatas[i] = key.get(i);
        }
        dbHelper.getWritableDatabase().delete(dbHelper.getTableName(), where.toString(), realDatas);
    }

    private DataWrap<String> selectData(Cursor cursor) {
        return new DataWrap<>(cursor.getString(cursor.getColumnIndex(KeyName)),
                cursor.getString(cursor.getColumnIndex(DataName)),
                cursor.getLong(cursor.getColumnIndex(Expires)),
                cursor.getString(cursor.getColumnIndex(TypeName)));
    }

    @Override
    public DataWrap<String> select(String key) {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(selectHead, new String[]{key});
        DataWrap<String> dataWrap = null;
        boolean isExpires = false;
        if (cursor.moveToFirst()) {
            DataWrap<String> data = selectData(cursor);
            if (checkNotExpires(data.getExpires())) {
                dataWrap = data;
            } else {
                isExpires = true;
            }
        }
        cursor.close();
        if (isExpires)
            delete(key);
        return dataWrap;
    }

    @Override
    public List<DataWrap<String>> select(List<String> key) {
        List<DataWrap<String>> returnData = new ArrayList<>();
        List<String> expiresKey = new ArrayList<>();
        StringBuilder where = new StringBuilder("(");
        String realDatas[] = new String[key.size()];
        for (int i = 0; i < key.size(); i++) {
            where.append(KeyName + "=?").append(i != key.size() - 1 ? " OR " : ")");
            realDatas[i] = key.get(i);
        }
        Cursor cursor = dbHelper.getReadableDatabase().query(dbHelper.getTableName(), new String[]{KeyName, DataName, TypeName, Expires}
                , where.toString(), realDatas, null, null, null);
        while (cursor.moveToNext()) {
            DataWrap<String> data = selectData(cursor);
            if (checkNotExpires(data.getExpires())) {
                returnData.add(data);
            } else {
                expiresKey.add(data.getKey());
            }
        }
        cursor.close();
        delete(expiresKey);
        return returnData;
    }

    @Override
    public List<DataWrap<String>> selectByTypeStr(String typeStr) {
        List<DataWrap<String>> returnData = new ArrayList<>();
        List<String> expiresKey = new ArrayList<>();
        Cursor cursor = dbHelper.getReadableDatabase().query(dbHelper.getTableName(), new String[]{KeyName, DataName, TypeName, Expires}, TypeName + "=?",
                new String[]{typeStr}, null, null, null);
        while (cursor.moveToNext()) {
            DataWrap<String> data = selectData(cursor);
            if (checkNotExpires(data.getExpires())) {
                returnData.add(data);
            } else {
                expiresKey.add(data.getKey());
            }
        }
        cursor.close();
        delete(expiresKey);
        return returnData;
    }


    @Override
    public boolean contains(String key) {
        return select(key) != null;
    }

    @Override
    public void clear() {
        dbHelper.getWritableDatabase().execSQL(clearAll);
        dbHelper.onCreate(dbHelper.getWritableDatabase());
    }
}
