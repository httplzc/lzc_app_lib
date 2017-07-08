package com.yioks.lzclib.Helper;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.yioks.lzclib.Untils.FileUntil;
import com.yioks.lzclib.Untils.StringManagerUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ${User} on 2017/5/6 0006.
 */

public class ParamsSignHelper extends AsyncTask {
    private Context context;
    private onFinishSignListener onFinishSignListener;

    public ParamsSignHelper(Context context) {
        this.context = context;
    }

    private String signParams(RequestParams requestParams) {
        List<ParamsWrapper> paramsWrapperList = new ArrayList<>();
        for (Map.Entry<String, String> entry : requestParams.getUrlParamsEntry()) {
            paramsWrapperList.add(new ParamsWrapper(entry.getKey(), entry.getValue()));
        }
        for (Map.Entry<String, RequestParams.FileWrapper> stringFileWrapperEntry : requestParams.getFileParams()) {
            paramsWrapperList.add(new ParamsWrapper(stringFileWrapperEntry.getKey(), FileUntil.fileMD5(stringFileWrapperEntry.getValue().file)));
        }

        for (Map.Entry<String, List<RequestParams.FileWrapper>> stringListEntry : requestParams.getFileListParams()) {
            StringBuilder stringBuilder = new StringBuilder();

            for (RequestParams.FileWrapper fileWrapper : stringListEntry.getValue()) {
                stringBuilder.append(FileUntil.fileMD5(fileWrapper.file));
            }
            paramsWrapperList.add(new ParamsWrapper(stringListEntry.getKey(), stringBuilder.toString()));
        }
        return sortListGetParams(paramsWrapperList);
    }


    public void startSignParams(RequestParams requestParams) {
        this.execute(requestParams);
    }


    @Override
    protected void onPostExecute(Object o) {
        if (onFinishSignListener != null)
            onFinishSignListener.signFinish((String) o);
    }

    public boolean verifySignParams(Object jsonObject, JsonManager jsonManager) {
        String data = null;
        data = jsonObject.toString();
        String md5 = StringManagerUtil.md5(StringManagerUtil.md5(jsonManager.getTime() + data) + jsonManager.getCodeKey());
        Log.i("date_print", "verifySignParams  DataKey" + jsonManager.getDataKey() + "  md5   " + md5);
        return data != null && jsonManager.getDataKey().equals(md5);
    }


    @Override
    protected Object doInBackground(Object[] params) {
        return signParams((RequestParams) params[0]);
    }


    private class ParamsWrapper {
        String key;
        Object data;

        public ParamsWrapper(String key, Object data) {
            this.key = key;
            this.data = data;
        }
    }

    private String getJsonData(Object data) throws JSONException {
        if (data == null || data.equals("")) {
            return "";
        }
        if (data instanceof JSONArray) {
            return getJsonArraySort((JSONArray) data);
        } else if (data instanceof JSONObject) {
            return getJsonObjectSort((JSONObject) data);
        }
        return "";
    }


    private String getJsonObjectSort(JSONObject jsonObject) throws JSONException {
        if (jsonObject == null)
            return "";
        List<ParamsWrapper> paramsWrapperList = new ArrayList<>();
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Object o = jsonObject.get(key);

            if (o instanceof JSONObject) {
                paramsWrapperList.add(new ParamsWrapper(key, getJsonObjectSort((JSONObject) o)));
            } else if (o instanceof JSONArray) {
                paramsWrapperList.add(new ParamsWrapper(key, getJsonArraySort((JSONArray) o)));
            } else {
                paramsWrapperList.add(new ParamsWrapper(key, jsonObject.getString(key)));
            }
        }
        return sortListGetParams(paramsWrapperList);
    }

    private String sortListGetParams(List<ParamsWrapper> paramsWrapperList) {
        ParamsWrapper[] paramsWrappers = new ParamsWrapper[paramsWrapperList.size()];
        paramsWrapperList.toArray(paramsWrappers);
        Arrays.sort(paramsWrappers, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                ParamsWrapper paramsWrapper1 = (ParamsWrapper) o1;
                ParamsWrapper paramsWrapper2 = (ParamsWrapper) o2;
                String key1 = paramsWrapper1.key;
                String key2 = paramsWrapper2.key;
                return key1.compareTo(key2);
            }
        });
        StringBuilder stringBuilder = new StringBuilder();
        for (ParamsWrapper paramsWrapper : paramsWrappers) {
            if (paramsWrapper.data != null)
                stringBuilder.append(paramsWrapper.data);
        }
        return stringBuilder.toString();
    }

    private String getJsonArraySort(JSONArray jsonArray) throws JSONException {
        if (jsonArray == null) {
            return "";
        }
        StringBuilder stringBuffer = new StringBuilder();

        for (int i = 0; i < jsonArray.length(); i++) {
            Object o = jsonArray.get(i);
            if (o instanceof JSONArray) {
                stringBuffer.append(getJsonArraySort((JSONArray) o));
            } else if (o instanceof JSONObject) {
                stringBuffer.append(getJsonObjectSort((JSONObject) o));
            } else {
                stringBuffer.append(o);
            }

        }
        return stringBuffer.toString();
    }

    public interface onFinishSignListener {
        void signFinish(String sign);

        void signFailure();
    }

    public ParamsSignHelper.onFinishSignListener getOnFinishSignListener() {
        return onFinishSignListener;
    }

    public void setOnFinishSignListener(ParamsSignHelper.onFinishSignListener onFinishSignListener) {
        this.onFinishSignListener = onFinishSignListener;
    }
}
