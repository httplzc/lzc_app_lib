package pers.lizechao.android_lib.net.api;

import java.lang.reflect.Type;

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
 * Date: 2018-08-21
 * Time: 9:52
 * 转化器 将接口数据转化为页面需要的数据
 */
public interface ApiDataConversion {
    boolean isSucceed(String data) throws Exception;

    /**
     * @param data       返回的字符串
     * @param targetType 期望获的的数据类型
     * @return 与期望获的的数据类型一致
     */
    Object getBean(String data, Type targetType) throws Exception;
}
