package com.yioks.lzclib.Helper;

import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * Created by ${User} on 2017/3/6 0006.
 */

public class RequestParams extends com.loopj.android.http.RequestParams {
    public String getDataFromUrlParams(String key)
    {
        return urlParams.get(key);
    }

    public File getDataFromFileListParams(String key,int index)
    {
        return fileArrayParams.get(key).get(index).file;
    }

    public Set<Map.Entry<String,String>> getUrlParamsEntry()
    {
        return urlParams.entrySet();
    }
}
