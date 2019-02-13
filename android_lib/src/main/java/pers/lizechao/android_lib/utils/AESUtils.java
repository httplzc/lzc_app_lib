package pers.lizechao.android_lib.utils;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Lzc on 2018/2/6 0006.
 */

public class AESUtils {
    public static final String AES = "AES";
    public static final String DES = "DES";
    public static final String DESede = "DESede";


    public static String CreateSecret(int size) {
        String pubKey = null;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(AES);//密钥生成器
            keyGen.init(size);//初始化密钥生成器
            SecretKey secretKey = keyGen.generateKey();//生成密钥
            byte[] key = secretKey.getEncoded();//密钥字节数组
            pubKey = Base64.encodeToString(key, Base64.DEFAULT);
            return pubKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encryptSecret(byte[] pubKey, byte data[]) {

        try {
            SecretKey secretKey = new SecretKeySpec(pubKey, AES);//恢复密钥
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS1Padding");//Cipher完成加密或解密工作类
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);//对Cipher初始化，加密模式
            byte[] cipherByte = cipher.doFinal(data);//加密data
            return Base64.encodeToString(cipherByte, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decrypt(byte[] pubKey, byte[] secretValue) {
        try {
            SecretKey secretKey = new SecretKeySpec(pubKey, AES);//恢复密钥
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(secretValue));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return null;
    }
}
