package pers.lizechao.android_lib.net.params;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.Optional;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lzc on 2018/6/27 0027.
 */
public class MultipartFormParams extends BaseFormParams<MultipartFormParams.MultipartFormBuilder> {
    private final Map<String, MultipleData[]> multipartDatas;
    public static final String MEDIA_TYPE_BINARY = "application/octet-stream";


    MultipartFormParams(Map<String, String> heads, Map<String, String> urlParams, Map<String, MultipleData[]> multipartDatas) {
        super(heads, urlParams);
        this.multipartDatas = multipartDatas;
    }

    public MultipleData[] getDataFromRawParams(String key) {
        return multipartDatas.get(key);
    }

    public Map<String, MultipleData[]> getMultipartDatas() {
        return multipartDatas;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        stringBuilder.append(super.toString());
        stringBuilder.append("\n");
        for (Map.Entry<String, MultipleData[]> entry : multipartDatas.entrySet()) {
            stringBuilder.append(entry.getKey()).append(" : ").append(entry.getValue()[0].name).append("---").append(entry.getValue()[0].binaryData);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    @Override
    public MultipartFormBuilder newBuilder() {
        return new MultipartFormBuilder().putParams(this);
    }

    public static class MultipartFormBuilder extends FormParams.BuilderAb<MultipartFormParams, MultipartFormBuilder> {
        final Map<String, MultipleData[]> multipartDatas = new HashMap<>();

        public MultipartFormBuilder put(String key, MultipleData[] multipleData) {
            multipartDatas.put(key, multipleData);
            return this;
        }

        @Nullable
        @Override
        public MultipartFormBuilder putParams(@Nullable BaseParams<?> baseParams) {
            if (baseParams != null) {
                super.putParams(baseParams);
                if (baseParams instanceof MultipartFormParams)
                    putAllData(((MultipartFormParams) baseParams).getMultipartDatas());
            }
            return this;
        }

        public MultipartFormBuilder putAllData(Map<String, MultipleData[]> multipartDatas) {
            this.multipartDatas.putAll(multipartDatas);
            return this;
        }

        public MultipartFormBuilder put(String key, File file) {
            return put(key, file, MEDIA_TYPE_BINARY);
        }

        public MultipartFormBuilder put(String key, File file, String mimeType) {
            return put(key, new MultipleData[]{new MultipleData(file, file.getName(), mimeType)});
        }

        public MultipartFormBuilder put(String key, File[] files) {
            String[] mimeTypes = new String[files.length];
            Arrays.fill(mimeTypes, MEDIA_TYPE_BINARY);
            return put(key, files, mimeTypes);
        }

        public MultipartFormBuilder put(String key, File[] files, String[] mimeTypes) {
            MultipleData multipleData[] = new MultipleData[files.length];
            for (int i = 0; i < files.length; i++) {
                multipleData[i] = new MultipleData(files[i], files[i].getName(), mimeTypes[i]);
            }
            put(key, multipleData);
            return this;
        }

        @Override
        public void putUnknownObject(@NonNull String name, @NonNull Object arg, @Nullable Params params) {
            super.putUnknownObject(name, arg, params);
            if (File.class.isAssignableFrom(arg.getClass())) {
                String mimeType = Optional.ofNullable(params).map(p -> p.mimeType()[0]).orElse(MEDIA_TYPE_BINARY);
                this.put(name, (File) arg, mimeType);
            } else if (File[].class.isAssignableFrom(arg.getClass())) {
                File files[] = (File[]) arg;
                String mimeType[] = Optional.ofNullable(params).map(Params::mimeType).orElseGet(() -> {
                    String string[] = new String[files.length];
                    Arrays.fill(string, MEDIA_TYPE_BINARY);
                    return string;
                });
                this.put(name, (File[]) arg, mimeType);
            } else if (MultipleData.class.isAssignableFrom(arg.getClass())) {
                this.put(name, new MultipleData[]{(MultipleData) arg});
            } else if (MultipleData[].class.isAssignableFrom(arg.getClass())) {
                this.put(name, (MultipleData[]) arg);
            }
        }

        @Override
        protected MultipartFormBuilder self() {
            return this;
        }

        @Override
        public MultipartFormParams build() {
            return new MultipartFormParams(heads, urlParams, multipartDatas);
        }
    }

}
