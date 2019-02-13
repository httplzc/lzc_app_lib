package pers.lizechao.android_lib.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;

import pers.lizechao.android_lib.function.Serializer;

/**
 * Created by Lzc on 2018/5/30 0030.
 */
public class SerializableSerialCoder implements Serializer {
    private static final SerializableSerialCoder coder = new SerializableSerialCoder();

    @Override
    public <T> String serial(T object) {
        return new String(SerializableData(object));
    }

    @Override
    public <T> T unSerial(String serialStr, Type type) {
        return (T) UnSerializableData(serialStr.getBytes());
    }

    @Override
    public <T> T unSerial(String serialStr, Class<T> tClass) {
        return (T) UnSerializableData(serialStr.getBytes());
    }

    @Override
    public <T> T unSerial(String serialStr, Class<T> main, Type... type) {
        return (T) UnSerializableData(serialStr.getBytes());
    }


    public static SerializableSerialCoder getInstance() {
        return coder;
    }

    private static byte[] SerializableData(Object object) {
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (byteArrayOutputStream != null && objectOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    private static Object UnSerializableData(byte[] str) {
        Object object = null;
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(str);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            object = objectInputStream.readObject();
            return object;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (byteArrayInputStream != null) {
                    byteArrayInputStream.close();
                }
                if (objectInputStream != null) {
                    objectInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
