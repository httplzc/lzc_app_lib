package pers.lizechao.android_lib.support.img.pick;

/**
 * Created by Lzc on 2018/6/13 0013.
 */
public class Photo {
    private String path;
    private String id;
    private String mime_type;
    private long size;
    private long time_modified;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTime_modified() {
        return time_modified;
    }

    public void setTime_modified(long time_modified) {
        this.time_modified = time_modified;
    }
}
