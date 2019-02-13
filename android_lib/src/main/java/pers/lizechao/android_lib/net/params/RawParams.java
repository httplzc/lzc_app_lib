package pers.lizechao.android_lib.net.params;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.Optional;

import java.io.File;
import java.util.Map;

/**
 * Created by Lzc on 2018/6/27 0027.
 */
public class RawParams extends BaseParams {
    private final BinaryData binaryData;
    public static final String MEDIA_TYPE_BINARY = "application/octet-stream";
    public RawParams(Map<String, String> heads, BinaryData binaryData) {
        super(heads);
        this.binaryData = binaryData;
    }

    public BinaryData getBinaryData() {
        return binaryData;
    }

    public RawBuilder newBuilder() {
        return new RawBuilder().putParams(this);
    }

    public static class RawBuilder extends BaseParams.Builder<RawBuilder, RawParams> {
        private BinaryData binaryData;

        public RawBuilder put(BinaryData binaryData) {
            this.binaryData = binaryData;
            return this;
        }

        @Override
        public RawBuilder putParams(@Nullable BaseParams<?> baseParams) {
            if (baseParams != null && binaryData != null&&baseParams instanceof RawParams)
                binaryData = ((RawParams) baseParams).binaryData;
            return super.putParams(baseParams);
        }

        public RawBuilder set(String data, String mediaType) {
            binaryData = new BinaryData(data, mediaType);
            return this;
        }

        public RawBuilder set(byte[] bytes, String mediaType) {
            binaryData = new BinaryData(bytes, mediaType);
            return this;
        }

        public RawBuilder set(File file, String mediaType) {
            binaryData = new BinaryData(file, mediaType);
            return this;
        }

        @Override
        protected RawBuilder self() {
            return this;
        }

        @Override
        public RawParams build() {
            return new RawParams(heads, binaryData);
        }

        @Override
        public void putUnknownObject(@NonNull String name, @NonNull Object args, @Nullable Params params) {
            super.putUnknownObject(name, args, params);
            String mimeType = Optional.ofNullable(params).map(p -> p.mimeType()[0]).orElse(MEDIA_TYPE_BINARY);
            if (args instanceof BinaryData)
                this.put((BinaryData) args);
            else if (args instanceof File) {
                this.set((File) args, mimeType);
            } else if (args instanceof String) {
                this.set((String) args, mimeType);
            } else if (args instanceof byte[]) {
                this.set((byte[]) args, mimeType);
            }
        }
    }

}
