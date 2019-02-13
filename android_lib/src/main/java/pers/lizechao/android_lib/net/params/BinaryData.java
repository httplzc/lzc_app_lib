package pers.lizechao.android_lib.net.params;

import java.io.File;

/**
 * Created by Lzc on 2018/6/27 0027.
 */
public class BinaryData {
    public File file;
    public byte bytes[];
    public String data;
    public final String mediaType;

    public BinaryData(String data, String mediaType) {
        this.data = data;
        this.mediaType = mediaType;
    }

    public BinaryData(byte[] bytes, String mediaType) {
        this.bytes = bytes;
        this.mediaType = mediaType;
    }

    public BinaryData(File file, String mediaType) {
        this.file = file;
        this.mediaType = mediaType;
    }

    public boolean isFile() {
        return file != null;
    }

    public boolean isBytes() {
        return bytes != null;
    }

    public boolean isString() {
        return data != null;
    }

    @Override
    public String toString() {
        return file != null ? file.getPath() : "" + "    " + mediaType;

    }
}
