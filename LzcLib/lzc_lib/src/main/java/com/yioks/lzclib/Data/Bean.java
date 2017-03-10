package com.yioks.lzclib.Data;


import com.yioks.lzclib.Helper.JsonManager;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/3 0003.
 */
public class Bean implements Serializable {
    public JsonManager jsonManager;

    public JsonManager getJsonManager() {
        return jsonManager;
    }

    public void setJsonManager(JsonManager jsonManager) {
        this.jsonManager = jsonManager;
    }
}
