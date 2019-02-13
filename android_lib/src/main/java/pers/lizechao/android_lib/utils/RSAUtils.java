package pers.lizechao.android_lib.utils;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by Lzc on 2018/1/22 0022.
 */

public class RSAUtils {


    public static class RSAData {
        //base 64的
        public final String publicKey;
        public final String privateKey;

        public RSAData(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }
    }

    public static RSAData CreateRSASecret(int size) {
        String pubKey = null;
        String priKey = null;
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(size);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
            pubKey = Base64.encodeToString(publicKey.getEncoded(), Base64.DEFAULT);
            priKey = Base64.encodeToString(privateKey.getEncoded(), Base64.DEFAULT);
            return new RSAData(pubKey, priKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }


    //加密
    public static byte[] encryptByKey(String data, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data.getBytes());
        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 107.     * 解密
     * 108.     *
     * 109.     * @param data
     * 110.     * @param privateKey
     * 111.     * @return
     * 112.     * @throws Exception
     * 113.
     */
    public static String decryptByKey(byte[] data, Key key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(data));
        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 从字符串中加载公钥
     *
     * @param
     * @return
     * @throws Exception
     */
    public static RSAPublicKey loadPublicKey(byte buffer[]) {
        try {
            KeyFactory keyFactory = null;
            keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec x509 = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(x509);
        } catch (NoSuchAlgorithmException | NullPointerException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从字符串中加载私钥
     *
     * @param
     * @return
     * @throws Exception
     */
    public static RSAPrivateKey loadPrivateKey(byte buffer[]) throws Exception {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | NullPointerException | InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

}
