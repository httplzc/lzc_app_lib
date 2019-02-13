package pers.lizechao.android_lib.net.params;

import java.io.File;

/**
 * Created by Lzc on 2018/6/27 0027.
 */
public class MultipleData {
    public final BinaryData binaryData;
    public final String name;


    public MultipleData(byte[] bytes, String name, String mediaType) {
        this.name = name;
        binaryData = new BinaryData(bytes, mediaType);
    }

    public MultipleData(String data, String name, String mediaType) {
        this.name = name;
        binaryData = new BinaryData(data, mediaType);
    }

    public MultipleData(File file, String name, String mediaType) {
        this.name = name;
        binaryData = new BinaryData(file, mediaType);
    }





}
