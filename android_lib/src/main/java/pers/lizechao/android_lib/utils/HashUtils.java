package pers.lizechao.android_lib.utils;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Lzc on 2018/6/29 0029.
 */
public class HashUtils {
    public static final String MD5 = "MD5";
    public static final String SHA256 = "SHA-256";
    public static final String HmacSHA256 = "HmacSHA256";
    public static final String HmacSHA1 = "HmacSHA1";


    public static String HMAC_SHA1(byte[] key, byte[] data) {
        return StrUtils.byteToHexString(Hash(key, data, HmacSHA1));
    }

    public static String HMAC_SHA256(byte[] key, byte[] data) {
        return StrUtils.byteToHexString(Hash(key, data, HmacSHA256));
    }

    public static String MD5(String string) {
        if (string == null)
            return null;
        return StrUtils.byteToHexString(Hash(string.getBytes(), MD5));
    }

    public static String SHA256(byte[] bytes) {
        return StrUtils.byteToHexString(Hash(bytes, SHA256));
    }

    public static String fileMD5(File file) {
        return Base64.encodeToString(fileHash(file, MD5),0);
    }


    public static byte[] fileHash(File file, String hashType) {
        int bufferSize = 10 * 1024;
        FileInputStream fileInputStream = null;
        DigestInputStream digestInputStream = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(hashType);
            fileInputStream = new FileInputStream(file);
            digestInputStream = new DigestInputStream(fileInputStream, messageDigest);
            byte[] buffer = new byte[bufferSize];
            int readLength = 0;
            while ((readLength = digestInputStream.read(buffer)) > 0) {
                digestInputStream.getMessageDigest().update(buffer, 0, readLength);
            }
            return digestInputStream.getMessageDigest().digest();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (digestInputStream != null)
                    digestInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (fileInputStream != null)
                    fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static byte[] Hash(byte[] data, String type) {
        byte[] hash = null;
        try {
            hash = MessageDigest.getInstance(type).digest(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }


    public static byte[] Hash(byte[] key, byte[] value, String type) {
        if (key == null || value == null || type == null)
            return null;
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key, type);
            Mac mac = Mac.getInstance(type);
            mac.init(signingKey);
            return mac.doFinal(value);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        return null;
    }


}
