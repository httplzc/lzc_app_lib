package com.yioks.lzclib.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * 统一处理json外层格式类
 * Created by Yioks-ZhangMengzhen on 2016/5/12.
 */
public class JsonManager implements Serializable{
    String code="";//获取数据
    String msg;//消息
    int time;//时间戳
    Object dataInfo;//数据内容
    int pageNum;//当前页数
    Object pageData;//每页显示条数
    Object countData;//数据总条数
    Object countPage;//总页数
    String flag="";//请求标识
    String dataKey;//数据MD5标识
    String codeKey;//指纹校验key

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Object getDataInfo() {
        if(dataInfo==null||dataInfo.equals(""))
            return new JSONObject();
        return dataInfo;
    }

    public void setDataInfo(Object dataInfo) {
        this.dataInfo = dataInfo;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public Object getPageData() {
        return pageData;
    }

    public void setPageData(Object pageData) {
        this.pageData = pageData;
    }

    public Object getCountPage() {
        return countPage;
    }

    public void setCountPage(Object countPage) {
        this.countPage = countPage;
    }

    public Object getCountData() {
        return countData;
    }

    public void setCountData(Object countData) {
        this.countData = countData;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getDataKey() {
        return dataKey;
    }

    public void setDataKey(String dataKey) {
        this.dataKey = dataKey;
    }

    public String getCodeKey() {
        return codeKey;
    }

    public void setCodeKey(String codeKey) {
        this.codeKey = codeKey;
    }

    public JsonManager resolve(String jsonStr) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonStr);
        if (!jsonObject.isNull("code")) {
            this.code = jsonObject.getString("code");
        }
        if (!jsonObject.isNull("msg")) {
            this.msg = jsonObject.getString("msg");
        }
        if (!jsonObject.isNull("time")) {
            this.time = jsonObject.getInt("time");
        }
        if (!jsonObject.isNull("dataInfo")) {
            this.dataInfo = jsonObject.get("dataInfo");
        }
        if (!jsonObject.isNull("pageData")) {
            this.pageData = jsonObject.get("pageData");
        }
        if (!jsonObject.isNull("countData")) {
            this.countData = jsonObject.get("countData");
            if(this.countData == null || this.countData.equals(""))
            {
                this.countData = 0;
            }
        }
        if (!jsonObject.isNull("countPage")) {
            this.countPage = jsonObject.get("countPage");
        }
        if (!jsonObject.isNull("flag")) {
            this.flag = jsonObject.getString("flag");
        }
        if (!jsonObject.isNull("dataKey")) {
            this.dataKey = jsonObject.getString("dataKey");
        }
        if (!jsonObject.isNull("codeKey")) {
            this.codeKey = jsonObject.getString("codeKey");
        }
        return this;
    }

}
