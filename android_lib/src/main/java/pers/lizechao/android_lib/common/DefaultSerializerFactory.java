package pers.lizechao.android_lib.common;

import pers.lizechao.android_lib.function.Serializer;

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
 * Date: 2018-08-07
 * Time: 12:30
 */
public class DefaultSerializerFactory extends SerializerFactory{
    @Override
    public Serializer createJsonSerializer() {
        return GsonJsonSerialCoder.getInstance();
    }

    @Override
    public Serializer createSerializableSerializer() {
        return null;
    }
}
