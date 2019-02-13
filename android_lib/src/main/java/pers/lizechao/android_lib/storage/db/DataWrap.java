package pers.lizechao.android_lib.storage.db;

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
 * Date: 2018/11/16 0016
 * Time: 16:06
 */
class DataWrap<T> {
    private T data;
    private long expires;
    private String typeStr;
    private String key;

    public String getKey() {
        return key;
    }

    public long getExpires() {
        return expires;
    }

    public DataWrap(String key, T data, long expires, String typeStr) {
        this.data = data;
        this.expires = expires;
        this.typeStr = typeStr;
        this.key = key;
    }

    public T getData() {
        return data;
    }

    public String getTypeStr() {
        return typeStr;
    }
}
