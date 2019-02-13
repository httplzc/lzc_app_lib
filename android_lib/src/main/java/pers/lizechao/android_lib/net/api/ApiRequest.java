package pers.lizechao.android_lib.net.api;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import pers.lizechao.android_lib.net.base.HttpMethod;
import pers.lizechao.android_lib.net.params.BaseParams;
import pers.lizechao.android_lib.net.params.FormParams;
import pers.lizechao.android_lib.utils.JavaUtils;

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
 * Date: 2018-08-20
 * Time: 18:47
 */
public class ApiRequest<T> {
    private final Type type;
    private final String path;
    private BaseParams baseParams;
    private HttpMethod httpMethod;
    private boolean cacheEnable=false;
    private boolean useCache = true;


    public ApiRequest(Type type, String path, BaseParams baseParams, HttpMethod httpMethod, boolean cacheEnable) {
        this.type = preProcessType(type);
        this.path = path;
        this.baseParams = baseParams;
        this.httpMethod = httpMethod;
        if (!(baseParams instanceof FormParams)) {
            this.cacheEnable = false;
        } else {
            this.cacheEnable = cacheEnable;
        }
    }

    private Type preProcessType(Type type) {
        Class rawType = JavaUtils.getRawType(type);
        if (!rawType.isInterface())
            return type;
        if (rawType == List.class)
            return JavaUtils.createType(ArrayList.class, ((ParameterizedType) type).getActualTypeArguments()[0]);
        return type;
    }


    public Type getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public BaseParams<?> getBaseParams() {
        return baseParams;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public void setBaseParams(BaseParams baseParams) {
        this.baseParams = baseParams;
    }

    public void setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public boolean isCacheEnable() {
        return cacheEnable;
    }

}
