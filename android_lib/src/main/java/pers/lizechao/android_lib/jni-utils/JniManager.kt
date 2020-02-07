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
 * Date: 2020-01-14
 * Time: 22:53
 */
class JniManager {
    companion object {
        //分发成功
        @JvmStatic
        external fun dispatchFunctionCallbackSucceed(
            nativeObj: Long,
            key: String,
            dataType: Int,
            bundle: Any
        )

        //分发失败
        @JvmStatic
        external fun dispatchFunctionCallbackFail(
            nativeObj: Long,
            key: String,
            dataType: Int,
            bundle: Any
        )

        //分发flow
        @JvmStatic
        external fun dispatchFlow(nativeObj: Long, key: String, dataType: Int, bundle: Any)
    }
}

class JniBundle {
    private val mapData = HashMap<String, Any>()

    companion object {
        @JvmStatic
        fun getInt(bundle: Any, key: String) = (bundle as JniBundle).mapData[key] as Int

        @JvmStatic
        fun getFloat(bundle: Any, key: String) = (bundle as JniBundle).mapData[key] as Float

        @JvmStatic
        fun getDouble(bundle: Any, key: String) = (bundle as JniBundle).mapData[key] as Double

        @JvmStatic
        fun getLong(bundle: Any, key: String) = (bundle as JniBundle).mapData[key] as Long

        @JvmStatic
        fun getString(bundle: Any, key: String) = (bundle as JniBundle).mapData[key] as String

        @JvmStatic
        fun getBool(bundle: Any, key: String) = (bundle as JniBundle).mapData[key] as Boolean

        @JvmStatic
        fun getObj(bundle: Any, key: String) = (bundle as JniBundle).mapData[key]

        @JvmStatic
        fun clear(bundle: Any) {
            (bundle as JniBundle).mapData.clear()
        }

        fun initBy(vararg pairs: Pair<String, Any>): JniBundle {
            return JniBundle().also { it.mapData.putAll(mapOf(*pairs)) }
        }
    }
}