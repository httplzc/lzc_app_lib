package pers.lizechao.android_lib.support.webview.jsBridge;

import android.content.Context;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.webkit.WebView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lzc on 2018/1/15 0015.
 */

public class JsBridgeHelper implements WebViewJavascriptBridge {
    private final Map<String, CallBackFunction> responseCallbacks = new HashMap<>();
    private final Map<String, BridgeHandler> messageHandlers = new HashMap<>();
    private List<Message> startupMessage = null;
    private final WebView webView;
    private final Context context;
    private String currentVersion = "1";

    private List<Message> getStartupMessage() {
        return startupMessage;
    }

    private void setStartupMessage(List<Message> startupMessage) {
        this.startupMessage = startupMessage;
    }

    private long uniqueId = 0;


    public JsBridgeHelper(WebView webView, Context context) {
        this.webView = webView;
        this.context = context;
    }

    /**
     * 获取到CallBackFunction data执行调用并且从数据集移除
     *
     * @param url
     */
    private void handlerReturnData(String url) {
        String functionName = JsBridgeUntil.getFunctionFromReturnUrl(url);
        CallBackFunction f = responseCallbacks.get(functionName);
        String data = JsBridgeUntil.getDataFromReturnUrl(url);
        if (f != null) {
            f.onCallBack(data);
            responseCallbacks.remove(functionName);
        }
    }

    @Override
    public void send(String data) {
        send(data, null);
    }

    @Override
    public void send(String data, CallBackFunction responseCallback) {
        doSend(null, data, responseCallback);
    }

    /**
     * 保存message到消息队列
     *
     * @param handlerName      handlerName
     * @param data             data
     * @param responseCallback CallBackFunction
     */
    private void doSend(String handlerName, String data, CallBackFunction responseCallback) {
        Message m = new Message();
        if (!TextUtils.isEmpty(data)) {
            m.setData(data);
        }
        if (responseCallback != null) {
            String callbackStr = String.format(JsBridgeUntil.CALLBACK_ID_FORMAT, ++uniqueId + (JsBridgeUntil.UNDERLINE_STR + SystemClock.currentThreadTimeMillis()));
            responseCallbacks.put(callbackStr, responseCallback);
            m.setCallbackId(callbackStr);
        }
        if (!TextUtils.isEmpty(handlerName)) {
            m.setHandlerName(handlerName);
        }
        queueMessage(m);
    }

    /**
     * list<message> != null 添加到消息集合否则分发消息
     *
     * @param m Message
     */
    private void queueMessage(Message m) {
        if (startupMessage != null) {
            startupMessage.add(m);
        } else {
            dispatchMessage(m);
        }
    }

    /**
     * 分发message 必须在主线程才分发成功
     *
     * @param m Message
     */
    private void dispatchMessage(Message m) {
        String messageJson = m.toJson();
        //escape special characters for json string  为json字符串转义特殊字符
        messageJson = messageJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");
        messageJson = messageJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");
        String javascriptCommand = String.format(JsBridgeUntil.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson);
        // 必须要找主线程才会将数据传递出去 --- 划重点
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            webView.loadUrl(javascriptCommand);
        }
    }

    /**
     * 刷新消息队列
     */
    private void flushMessageQueue() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            loadUrl(JsBridgeUntil.JS_FETCH_QUEUE_FROM_JAVA, data -> {
                // deserializeMessage 反序列化消息
                List<Message> list = null;
                try {
                    list = Message.toArrayList(data);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (list == null || list.size() == 0) {
                    return;
                }
                for (int i = 0; i < list.size(); i++) {
                    Message m = list.get(i);
                    String responseId = m.getResponseId();
                    // 是否是response  CallBackFunction
                    if (!TextUtils.isEmpty(responseId)) {
                        CallBackFunction function = responseCallbacks.get(responseId);
                        String responseData = m.getResponseData();
                        function.onCallBack(responseData);
                        responseCallbacks.remove(responseId);
                    } else {
                        CallBackFunction responseFunction = null;
                        // if had callbackId 如果有回调Id
                        final String callbackId = m.getCallbackId();
                        if (!TextUtils.isEmpty(callbackId)) {
                            responseFunction = data12 -> {
                                Message responseMsg = new Message();
                                responseMsg.setResponseId(callbackId);
                                responseMsg.setResponseData(data12);
                                queueMessage(responseMsg);
                            };
                        } else {
                            responseFunction = data1 -> {
                                // do nothing
                            };
                        }
                        // BridgeHandler执行
                        BridgeHandler handler = null;
                        if (!TextUtils.isEmpty(m.getHandlerName())) {
                            handler = messageHandlers.get(m.getHandlerName());
                        }
                        if (handler != null) {
                            handler.handler(m.getData(), responseFunction);
                        }
                    }
                }
            });
        }
    }


    private void loadUrl(String jsUrl, CallBackFunction returnCallback) {
        webView.loadUrl(jsUrl);
        // 添加至 Map<String, CallBackFunction>
        responseCallbacks.put(JsBridgeUntil.parseFunctionName(jsUrl), returnCallback);
    }

    /**
     * register handler,so that javascript can newCall it
     * 注册处理程序,以便javascript调用它
     *
     * @param handlerName handlerName
     * @param handler     BridgeHandler
     */
    public void registerHandler(String handlerName, BridgeHandler handler) {
        if (handler != null) {
            // 添加至 Map<String, BridgeHandler>
            messageHandlers.put(handlerName, handler);
        }
    }

    /**
     * unregister handler
     *
     * @param handlerName
     */
    public void unregisterHandler(String handlerName) {
        if (handlerName != null) {
            messageHandlers.remove(handlerName);
        }
    }

    /**
     * newCall javascript registered handler
     * 调用javascript处理程序注册
     *
     * @param handlerName handlerName
     * @param data        data
     * @param callBack    CallBackFunction
     */
    public void callHandler(String handlerName, String data, CallBackFunction callBack) {
        doSend(handlerName, data, callBack);
    }


    public boolean onShouldOverrideUrlLoading(String url) {
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (url == null)
            return false;
        if (url.startsWith(JsBridgeUntil.YY_RETURN_DATA)) { // 如果是返回数据
            handlerReturnData(url);
            return true;
        } else if (url.startsWith(JsBridgeUntil.YY_OVERRIDE_SCHEMA)) { //
            flushMessageQueue();
            return true;
        } else {
            return false;
        }
    }

    public void onPageStart() {
        registerHandler("getVersion", (data, function) -> function.onCallBack(currentVersion));
        if (getStartupMessage() != null) {
            for (Message m : getStartupMessage()) {
                dispatchMessage(m);
            }
            setStartupMessage(null);
        }
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }
}
