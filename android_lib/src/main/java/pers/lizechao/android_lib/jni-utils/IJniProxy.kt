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
 * Date: 2020-01-30
 * Time: 14:51
 */
interface IJniProxy {
    //获取回调对象
    fun <T : Any, R : Any> jniCallback(key: String): JniCallback<T, R>

    //获取flow分发对象
    fun <T : Any> jniCallbackSuccess(key: String, data: T)

    //获取flow分发对象
    fun <T : Any> jniCallbackFail(key: String, data: T)

    //无参回调成功
    fun jniCallbackSuccess(key: String)

    //无参回调失败
    fun jniCallbackFail(key: String)

    //获取flow分发对象
    fun <T : Any> jniFlow(key: String, data: T)

    //获取flow分发对象
    fun <T : Any> jniFlow(key: String, dataBuilder: () -> T)

    //C++对象创建回调
    fun createFromNative(nativeObj: Long)

    //c++对象销毁回调
    fun destroyFromNative()

    interface JniCallback<T, R> {
        fun onSuccess(data: T)
        fun onFail(data: R)
    }
}

