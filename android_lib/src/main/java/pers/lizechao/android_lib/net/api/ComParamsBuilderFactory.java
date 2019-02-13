package pers.lizechao.android_lib.net.api;

import pers.lizechao.android_lib.net.params.FormParams;

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
 * Time: 9:53
 * 公共请求参数构造器
 */
public abstract class ComParamsBuilderFactory {
    abstract protected FormParams createBuilder();

    protected ComParamsBuilderFactory newDefault() {
        return null;
    }
}
