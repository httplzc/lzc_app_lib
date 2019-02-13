package pers.lizechao.android_lib.net.okhttp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.function.BiConsumer;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by Lzc on 2017/9/6 0006.
 */

public class ProxyRequestBody extends RequestBody {
    private final RequestBody requestBody;
    private final BiConsumer<Long, Long> onRequestProgress;
    private BufferedSink bufferedSink;


    public ProxyRequestBody(RequestBody requestBody, @Nullable BiConsumer<Long, Long> onRequestProgress) {
        this.requestBody = requestBody;
        this.onRequestProgress = onRequestProgress;
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            bufferedSink = Okio.buffer(sinkWrapper(sink));
        }
        try {
            requestBody.writeTo(bufferedSink);
            bufferedSink.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入，回调进度接口
     *
     * @param sink Sink
     * @return Sink
     */
    private Sink sinkWrapper(Sink sink) {
        return new ForwardingSink(sink) {
            //当前写入字节数
            long bytesWritten = 0L;
            //总字节长度，避免多次调用contentLength()方法
            long contentLength = 0L;

            @Override
            public void write(@NonNull Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    contentLength = contentLength();
                }
                //增加当前写入的字节数
                bytesWritten += byteCount;
                //回调
                if (onRequestProgress != null)
                    onRequestProgress.accept(contentLength, bytesWritten);
            }
        };
    }

}
