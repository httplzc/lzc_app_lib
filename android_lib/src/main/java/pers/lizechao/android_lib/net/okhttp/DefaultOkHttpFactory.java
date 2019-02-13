package pers.lizechao.android_lib.net.okhttp;

import android.content.Context;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.TlsVersion;

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
 * Date: 2018-08-06
 * Time: 15:58
 */
public class DefaultOkHttpFactory extends OkHttpInstance.OkHttpFactory {
    protected int cacheSize = 20 * 1024 * 1024;
    protected int connectTime = 10000;
    protected int writeTime = 30000;
    protected int readTime = 60000;

    @Override
    protected OkHttpClient createOkHttpClient(Context context, InputStream... stream) throws Exception {
        TrustManagerFactory trustManagerFactory = createTrustManagerFactory(stream);
        return new OkHttpClient.Builder()
                .connectTimeout(connectTime, TimeUnit.MILLISECONDS)
                .readTimeout(writeTime, TimeUnit.MILLISECONDS)
                .writeTimeout(readTime, TimeUnit.MILLISECONDS)
                .cache(new Cache(context.getCacheDir(), cacheSize))
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Request newRequest = null;
                    if (request != null) {
                        newRequest = request.newBuilder()
                                .header("user-agent", "lzc,android,okHttp")
                                .build();
                    }
                    return chain.proceed(newRequest);
                })
                .followSslRedirects(true)
                .connectionSpecs(Arrays.asList(new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).
                        tlsVersions(TlsVersion.TLS_1_0).cipherSuites(CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA256,
                        CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA, CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA256,
                        CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA, CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA).build(), ConnectionSpec.CLEARTEXT))
                .sslSocketFactory(createSSLSocketFactory(trustManagerFactory), (X509TrustManager) trustManagerFactory.getTrustManagers()[0])
                .hostnameVerifier((hostname, session) -> true).build();
    }

    private static SSLSocketFactory createSSLSocketFactory(TrustManagerFactory trustManagerFactory) throws Exception {
        final SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), new java.security.SecureRandom());
        return sslContext.getSocketFactory();
    }

    /**
     * @param stream 证书的stream
     * @return
     * @throws
     */
    private static TrustManagerFactory createTrustManagerFactory(InputStream... stream) throws Exception {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null);
        int index = 0;
        for (InputStream certificate : stream) {
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
        }
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(stream.length == 0 ? null : keyStore);
        return trustManagerFactory;
    }
}
