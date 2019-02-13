package pers.lizechao.android_lib.support.webview.jsBridge;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

/**
 * Created by Lzc on 2018/1/13 0013.
 */

public class WebViewUntil {

    //调用JS
    public static void loadJavaScript(WebView webView, String js) {
        if (webView != null) {
            try {
                webView.evaluateJavascript(js, s -> Log.i("webView", s));
            } catch (NoSuchMethodError e) {
                e.printStackTrace();
                webView.loadUrl("javascript:" + js);
            }
        }
    }

    //在头部注入css
    public static void addCss(WebView webView, String cssData) {
        if (webView != null)
            webView.loadUrl("JavaScript:(function() {" + "var parent = document.getElementsByTagName('head').item(0);"
              + "var style = document.createElement('style');"
              + "style.type = 'TestAop/css';"
              + "style.innerHTML = window.atob('" + cssData + "');"
              + "parent.appendChild(style)" + "})();");
    }


    //在头部注入js
    public static void webViewAddHeadJs(WebView view, String url) {
        String js = "var newscript = document.createElement(\"script\");";
        js += "newscript.src=\"" + url + "\";";
        js += "document.scripts[0].parentNode.insertBefore(newscript,document.scripts[0]);";
        view.loadUrl("javascript:" + js);
    }

    public static String getCookies(String domain) {
        CookieManager cookieManager = CookieManager.getInstance();
        return cookieManager.getCookie(domain);
    }

    public static void setCookies(Context context, String domain, String cookie) {
        CookieManager.getInstance().removeAllCookie();
        if (!TextUtils.isEmpty(cookie)) {
            String[] cookieArray = cookie.split(";");
            for (String aCookieArray : cookieArray) {
                int position = aCookieArray.indexOf("=");// 在Cookie中键值使用等号分隔
                String cookieName = aCookieArray.substring(0, position);// 获取键
                String cookieValue = aCookieArray.substring(position + 1);// 获取值

                String value = cookieName + "=" + cookieValue;// 键值对拼接成 value
                CookieManager.getInstance().setCookie(domain, value);// 设置 Cookie
            }
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
            cookieSyncManager.sync();
        } else {
            CookieManager.getInstance().flush();
        }
    }

}
