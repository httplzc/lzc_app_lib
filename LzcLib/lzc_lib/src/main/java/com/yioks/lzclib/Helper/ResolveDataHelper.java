package com.yioks.lzclib.Helper;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.yioks.lzclib.Data.Bean;
import com.yioks.lzclib.Data.GlobalVariable;
import com.yioks.lzclib.Untils.DialogUtil;
import com.yioks.lzclib.Untils.HttpUtil;
import com.yioks.lzclib.Untils.StringManagerUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/8/3 0003.
 */
public abstract class ResolveDataHelper {


    //完成解析的回调方法
    protected onResolveDataFinish onResolveDataFinish;
    protected onProgresUpDate onProgresUpDate;
    protected Context context;
    private RequestDataBase requestData;
    private Object TAG;
    private int dateType = 0;  //0 model 1 list 2 Hashmap -1无特殊返回值
    private int requestType = 0; //0 post 1 get
    private String requestHTTP;
    private RequestParams requestParams;
    private File[] files;


    public ResolveDataHelper(Context context, RequestDataBase requestData) {
        this.context = context;
        this.requestData = requestData;
    }

    public ResolveDataHelper(Context context, RequestDataBase requestData, RequestParams requestParams) {
        this.context = context;
        this.requestData = requestData;
        this.requestParams = requestParams;
    }

    public void StartGetData(String... strings) {
        if (requestParams == null) {
            requestParams = new RequestParams();
        }
        if (files == null) {
            try {
                if (requestData == null) {
                    RequestData(requestParams);
                } else {
                    RequestData(requestData.SetParams(requestParams, dateType, strings));
                }


            } catch (Exception e) {
                e.printStackTrace();
                requestDataFail("请求参数错误");
            }
        } else {
            if (requestData instanceof RequestDataFile) {
                try {
                    RequestData(((RequestDataFile) requestData).setFileParams(requestParams, files, strings));
                } catch (Exception e) {
                    e.printStackTrace();
                    requestDataFail("请求参数错误");
                }
            } else {
                requestDataFail("请求参数错误");
            }
        }
    }

    protected void initMd5(RequestParams params) throws Exception {
        StringBuilder data_md5 = new StringBuilder();
        Iterator it = params.getUrlParamsEntry().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
            String value = entry.getValue();
            data_md5.append(value);
        }
        params.put("dataKey", StringManagerUtil.md5(data_md5.toString()));
    }

    //请求数据的方法
    protected void RequestData(RequestParams params) {

        if (params == null) {
            requestDataFail("请求错误");
        }
        try {
            initMd5(params);
            AsyncHttpResponseHandler asyncHttpResponseHandler = new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String string = new String(responseBody);
                    Log.i("date_print", "data" + string);
                    JsonManager jsonManager = new JsonManager();
                    try {
                        jsonManager.resolve(string);
                    } catch (JSONException e) {
                        requestDataFail("服务器出问题了~~");
                        e.printStackTrace();
                        return;
                    }
                    if ((jsonManager.getCode() != null && (jsonManager.getCode().equals("0") || jsonManager.getCode().equals("-301")))) {
                        callbackData(jsonManager.getDataInfo(), jsonManager);
                    } else {
                        if (jsonManager.getCode() != null && jsonManager.getCode().equals("-201") && checkTokenError()) {
                            tokenError();
                            return;
                        }
                        requestDataFail(jsonManager.getMsg(), jsonManager.getCode());
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    requestDataFail();
                }

                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    super.onProgress(bytesWritten, totalSize);
                    //回调进度
                    if (onProgresUpDate != null) {
                        int progress = (int) (((float) bytesWritten / (float) totalSize) * 100);
                        onProgresUpDate.onProgress(progress);
                    }
                }
            };
            //为请求设置TAG
            if (TAG != null) {
                asyncHttpResponseHandler.setTag(TAG);
            }
            printParams(params);

            //  区分post和get请求
            requestHTTP=GlobalVariable.getHTTP(context);
            if (requestType == 0) {
                HttpUtil.post(context, requestHTTP, params, asyncHttpResponseHandler);
            } else {
                HttpUtil.get(context, requestHTTP, params, asyncHttpResponseHandler);
            }
        } catch (Exception e) {
            e.printStackTrace();
            requestDataFail("未知错误——");
        }
    }

    private void printParams(RequestParams params) {
        if (params == null)
            return;
        Log.i("date_print", "request" + params.toString() + "----" + GlobalVariable.getHTTP(context));
    }


    /**
     * 错误
     */
    protected void requestDataFail() {

        DialogUtil.ShowToast(context, "获取数据失败,请检查网络");
        DialogUtil.dismissDialog();
        if (onResolveDataFinish != null) {
            onResolveDataFinish.onFail(null);
        }
        context = null;
    }

    /**
     * 错误
     */
    protected void requestDataFail(String string) {

        DialogUtil.ShowToast(context, "错误信息:" + string);
        DialogUtil.dismissDialog();
        if (onResolveDataFinish != null) {
            onResolveDataFinish.onFail(null);
        }
        context = null;
    }

    /**
     * 错误
     */
    protected void requestDataFail(String string, String code) {

        DialogUtil.ShowToast(context, "错误信息:" + string);
        DialogUtil.dismissDialog();
        if (onResolveDataFinish != null) {
            onResolveDataFinish.onFail(code);
        }
        context = null;
    }


    /**
     * 回调收到的数据
     *
     * @param data        //dateInfo 里的数据
     * @param jsonManager //原始数据
     */
    protected void callbackData(Object data, JsonManager jsonManager) {
        try {
            //替换null为""
            removeEmptyValue(data);
            if (data == null)
                data = "";
            if (onResolveDataFinish != null) {
                Object resolveData = null;
                //model
                if (dateType == 0) {
                    RequestData requestDataModel = (RequestData) requestData;
                    resolveData = requestDataModel.resolveData(data);
                    if (resolveData == null) {
                        requestDataFail("服务器出问题了~~");
                        return;
                    }
                    ((Bean) resolveData).setJsonManager(jsonManager);
                } else if (dateType == 1) {
                    //list
                    RequestDataByList requestDataByList = (RequestDataByList) requestData;
                    resolveData = requestDataByList.resolveDataByList(data);
                    if (resolveData == null) {
                        requestDataFail("服务器出问题了~~");
                        return;
                    }
                } else if (dateType == 2) {
                    //hashmap
                    RequestDataByHashMap requestDataByHashMap = (RequestDataByHashMap) requestData;
                    resolveData = requestDataByHashMap.resolveDataByHashMap(data);
                    if (resolveData == null) {
                        requestDataFail("服务器出问题了~~");
                        return;
                    }
                } else if (dateType == -1) {
                    resolveData = null;
                } else {
                    requestDataFail("请求内容错误");
                }
                onResolveDataFinish.resolveFinish(resolveData);
                context = null;
            }
        } catch (Exception e) {
            requestDataFail("服务器出问题了~~");
            e.printStackTrace();
            //  WriteRizhi(context, e);
        }
    }

    //清除null
    public void removeEmptyValue(Object data) {
        if (data == null || data.equals("")) {
            return;
        }

        if (data instanceof JSONArray) {
            removeJsonArrayEmpty((JSONArray) data);
        } else if (data instanceof JSONObject) {
            removeJsonEmpty((JSONObject) data);
        }

    }


    /**
     * //清除null
     *
     * @param jsonObject
     */
    protected void removeJsonEmpty(JSONObject jsonObject) {

        if (jsonObject == null) {
            return;
        }
        try {
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object o = jsonObject.get(key);
                if (o instanceof JSONObject) {
                    removeJsonEmpty((JSONObject) o);
                } else if (o instanceof JSONArray) {
                    removeJsonArrayEmpty((JSONArray) o);
                } else {
                    if (jsonObject.isNull(key)) {
                        jsonObject.put(key, "");
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * //清除null
     *
     * @param jsonArray
     */
    protected void removeJsonArrayEmpty(JSONArray jsonArray) {
        try {
            if (jsonArray == null) {
                return;
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                Object o = jsonArray.get(i);
                if (o instanceof JSONArray) {
                    removeJsonArrayEmpty((JSONArray) o);
                } else if (o instanceof JSONObject) {
                    removeJsonEmpty((JSONObject) o);
                } else {
                    return;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public abstract boolean checkTokenError();

    public abstract void tokenError();

    public interface onProgresUpDate {
        void onProgress(int progress);
    }

    public com.yioks.lzclib.Helper.onResolveDataFinish getOnResolveDataFinish() {
        return onResolveDataFinish;
    }

    public void setOnResolveDataFinish(com.yioks.lzclib.Helper.onResolveDataFinish onResolveDataFinish) {
        this.onResolveDataFinish = onResolveDataFinish;
    }


    public ResolveDataHelper.onProgresUpDate getOnProgresUpDate() {
        return onProgresUpDate;
    }

    public void setOnProgresUpDate(ResolveDataHelper.onProgresUpDate onProgresUpDate) {
        this.onProgresUpDate = onProgresUpDate;
    }

    public Object getTAG() {
        return TAG;
    }

    public void setTAG(Object TAG) {
        this.TAG = TAG;
    }

    public int getDateType() {
        return dateType;
    }

    public void setDateType(int dateType) {
        this.dateType = dateType;
    }

    public int getRequestType() {
        return requestType;
    }

    public void setRequestType(int requestType) {
        this.requestType = requestType;
    }

    public String getRequestHTTP() {
        return requestHTTP;
    }

    public void setRequestHTTP(String requestHTTP) {
        this.requestHTTP = requestHTTP;
    }

    public RequestParams getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(RequestParams requestParams) {
        this.requestParams = requestParams;
    }

    public File[] getFiles() {
        return files;
    }

    public void setFiles(File[] files) {
        this.files = files;
    }
}
