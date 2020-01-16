package com.liulishuo.myapplication

import android.util.Log

/**
 * Created by
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
 * Date: 2020-01-13
 * Time: 11:54
 * Jni 代理对象的基类，实现回调分发等功能，不支持多线程！！
 */
abstract class JniProxy {
    protected var nativeObj: Long = 0

    companion object {
        const val SingleDataKey = "SingleDataKey"
    }

    //获取回调对象
    fun <T : Any, R : Any> jniCallback(key: String): JniCallback<T, R> {
        return object : JniCallback<T, R> {
            override fun onSuccess(data: T) {
                jniCallbackSuccess<T>(key).invoke(data);
            }

            override fun onFail(data: R) {
                jniCallbackFail<R>(key).invoke(data);
            }
        }
    }

    //获取flow分发对象
    fun <T : Any> jniCallbackSuccess(key: String): (T) -> Unit = {
        JniManager.dispatchFunctionCallbackSucceed(
            nativeObj,
            key,
            getDataType(it).ordinal,
            wrapDataType(it)
        )
    }

    //获取flow分发对象
    fun <T : Any> jniCallbackFail(key: String): (T) -> Unit = {
        JniManager.dispatchFunctionCallbackFail(
            nativeObj,
            key,
            getDataType(it).ordinal,
            wrapDataType(it)
        )
    }


    //获取flow分发对象
    fun <T : Any> jniFlow(key: String): (T) -> Unit = {
        JniManager.dispatchFlow(
            nativeObj, key, getDataType(it).ordinal, wrapDataType(it)
        )
    }

    interface JniCallback<T, R> {
        fun onSuccess(data: T)
        fun onFail(data: R)
    }

    private fun <T : Any> wrapDataType(data: T): JniBundle {
        if (data::class == JniBundle::class) {
            return data as JniBundle
        } else {
            return JniBundle.initBy(
                mapOf(
                    SingleDataKey to data
                )
            )
        }
    }

    private fun <T : Any> getDataType(data: T): DataType {
        return when (data::class) {
            Int::class -> {
                DataType.Int
            }
            Float::class -> {
                DataType.Float
            }
            Double::class -> {
                DataType.Double
            }
            Boolean::class -> {
                DataType.Bool
            }
            String::class -> {
                DataType.String
            }
            JniBundle::class -> {
                DataType.JniBundle
            }
            else -> {
                DataType.Object
            }
        }
    }

    private enum class DataType {
        Int, Float, Double, Bool, String, Object, JniBundle
    }

    open fun createFromNative(nativeObj: Long) {
        this.nativeObj = nativeObj
        Log.i("lzc", "nativeObj $nativeObj")
    }

    open fun destroyFromNative() {

    }
}

