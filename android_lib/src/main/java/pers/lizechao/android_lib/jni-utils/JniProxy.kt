package com.liulishuo.lingococos2dx.jni_utils

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
abstract class JniProxy : IJniProxy {
    protected var nativeObj: Long = 0

    companion object {
        const val SingleDataKey = "SingleDataKey"
    }

    //获取回调对象
    override fun <T : Any, R : Any> jniCallback(key: String): IJniProxy.JniCallback<T, R> {
        return object : IJniProxy.JniCallback<T, R> {
            override fun onSuccess(data: T) {
                jniCallbackSuccess<T>(key, data)
            }

            override fun onFail(data: R) {
                jniCallbackFail<R>(key, data)
            }
        }
    }

    //获取flow分发对象
    override fun <T : Any> jniCallbackSuccess(key: String, data: T) {
        JniManager.dispatchFunctionCallbackSucceed(
            nativeObj,
            key,
            getDataType(data).ordinal,
            wrapDataType(data)
        )
    }

    //获取flow分发对象
    override fun <T : Any> jniCallbackFail(key: String, data: T) {
        JniManager.dispatchFunctionCallbackFail(
            nativeObj,
            key,
            getDataType(data).ordinal,
            wrapDataType(data)
        )
    }

    override fun jniCallbackSuccess(key: String) {
        jniCallbackSuccess(key, Unit)
    }

    override fun jniCallbackFail(key: String) {
        jniCallbackFail(key, Unit)
    }

    //获取flow分发对象
    override fun <T : Any> jniFlow(key: String, data: T) {
        JniManager.dispatchFlow(
            nativeObj, key, getDataType(data).ordinal, wrapDataType(data)
        )
    }

    private fun <T : Any> wrapDataType(data: T): JniBundle {
        return if (data::class == JniBundle::class) {
            data as JniBundle
        } else {
            JniBundle.initBy(
                SingleDataKey to data
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
            Long::class -> {
                DataType.Long
            }
            String::class -> {
                DataType.String
            }
            JniBundle::class -> {
                DataType.JniBundle
            }
            Unit::class -> {
                DataType.Void
            }
            else -> {
                DataType.Object
            }
        }
    }

    private enum class DataType {
        Int, Float, Double, Bool, Long, String, Object, JniBundle, Void
    }

    override fun createFromNative(nativeObj: Long) {
        this.nativeObj = nativeObj
    }

    override fun destroyFromNative() {
    }

    override fun <T : Any> jniFlow(key: String, dataBuilder: () -> T) {
        jniFlow(key, dataBuilder.invoke())
    }
}

