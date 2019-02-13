package pers.lizechao.android_lib.common;

import android.support.annotation.NonNull;

import pers.lizechao.android_lib.ProjectConfig;
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
 * Time: 12:17
 */
public abstract class SerializerFactory {
    public abstract Serializer createJsonSerializer();

    public abstract Serializer createSerializableSerializer();

    @NonNull
    public static SerializerFactory newInstance() {
        try {
            return ProjectConfig.getInstance().getSerializerFactoryClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }
}
