package com.yioks.lzclib.Helper;


import java.io.File;

/**
 * Created by ${User} on 2016/9/20 0020.
 */
public interface RequestDataFile{
    public RequestParams setFileParams(RequestParams requestParams, File[] files, String... strings) throws Exception;
}
