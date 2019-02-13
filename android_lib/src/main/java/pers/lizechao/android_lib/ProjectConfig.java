package pers.lizechao.android_lib;

import android.content.Context;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;

import org.w3c.dom.Document;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import pers.lizechao.android_lib.common.CrashHandle;
import pers.lizechao.android_lib.common.SerializerFactory;
import pers.lizechao.android_lib.data.ApplicationData;
import pers.lizechao.android_lib.net.api.NetAlert;
import pers.lizechao.android_lib.net.okhttp.OkHttpInstance;
import pers.lizechao.android_lib.storage.db.Storage;
import pers.lizechao.android_lib.storage.file.FileStoreManager;
import pers.lizechao.android_lib.support.img.load.FrescoConfigFactory;
import pers.lizechao.android_lib.support.log.LogRecorder;
import pers.lizechao.android_lib.support.pay.PayExtraData;
import pers.lizechao.android_lib.support.share.data.ShareExtraData;
import pers.lizechao.android_lib.ui.widget.PageStateView;
import pers.lizechao.android_lib.ui.widget.RefreshParent;

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
 * Date: 2018-08-06
 * Time: 16:10
 * 项目初始化
 */
public class ProjectConfig {
    private Class<? extends RefreshParent.RefreshViewFactory> refreshViewFactoryClass;
    private Class<? extends PageStateView.StateViewFactory> stateViewFactoryClass;
    private Class<? extends OkHttpInstance.OkHttpFactory> okHttpFactoryClass;
    private Class<? extends FrescoConfigFactory> frescoConfigFactoryClass;
    private Class<? extends SerializerFactory> serializerFactoryClass;
    private static final ProjectConfig projectConfig = new ProjectConfig();
    private NetAlert netAlert;
    private boolean haveInit = false;

    public static ProjectConfig getInstance() {
        if (!projectConfig.haveInit)
            throw new IllegalStateException("还未被初始化！");
        return projectConfig;
    }

    private static long calcTime(long time, String name) {
        Log.i("lzc", name + (System.currentTimeMillis() - time));
        return System.currentTimeMillis();
    }

    public static ProjectConfig Create(Context context) {
        ApplicationData.applicationContext = context;
        projectConfig.resolveConfig(context.getResources().openRawResource(R.raw.android_config));
        return projectConfig;
    }

    private ProjectConfig() {

    }


    /**
     * @param context 上下文
     * @param stream  网络请求密钥
     */
    public void init(Context context, InputStream... stream) {
        //okHttp 初始化
        OkHttpInstance.initOkHttpClient(context, stream);
        //Fresco初始化
        Fresco.initialize(context, FrescoConfigFactory.newInstance().createConfig(context));
        //缓存系统初始化
        Storage.init(context);
        //文件存储初始化
        FileStoreManager.init(context);
        //崩溃处理
        CrashHandle.getInstance().init();
        //日志系统
        LogRecorder.init(CrashHandle.getInstance());

    }

    private void resolveConfig(InputStream stream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder domBuilder = factory.newDocumentBuilder();
            Document document = domBuilder.parse(stream);
            initPay(document);
            initShare(document);
            initFactory(document);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("解析配置文件错误");
        }
        haveInit = true;
    }

    private void initFactory(Document document) throws Exception {
        refreshViewFactoryClass = Class.forName(getConfigValue("RefreshViewFactory", document)).asSubclass(RefreshParent.RefreshViewFactory.class);
        stateViewFactoryClass = Class.forName(getConfigValue("StateViewFactory", document)).asSubclass(PageStateView.StateViewFactory.class);
        okHttpFactoryClass = Class.forName(getConfigValue("OkHttpFactory", document)).asSubclass(OkHttpInstance.OkHttpFactory.class);
        frescoConfigFactoryClass = Class.forName(getConfigValue("FrescoConfigFactory", document)).asSubclass(FrescoConfigFactory.class);
        serializerFactoryClass = Class.forName(getConfigValue("SerializerFactory", document)).asSubclass(SerializerFactory.class);
    }

    public Class<? extends RefreshParent.RefreshViewFactory> getRefreshViewFactory() {
        return refreshViewFactoryClass;
    }

    public Class<? extends PageStateView.StateViewFactory> getStateViewFactory() {
        return stateViewFactoryClass;
    }

    public Class<? extends OkHttpInstance.OkHttpFactory> getOkHttpFactory() {
        return okHttpFactoryClass;
    }

    public Class<? extends FrescoConfigFactory> getFrescoConfigFactoryClass() {
        return frescoConfigFactoryClass;
    }

    public Class<? extends SerializerFactory> getSerializerFactoryClass() {
        return serializerFactoryClass;
    }

    public void setRefreshViewFactoryClass(Class<? extends RefreshParent.RefreshViewFactory> refreshViewFactoryClass) {
        this.refreshViewFactoryClass = refreshViewFactoryClass;
    }

    public void setStateViewFactoryClass(Class<? extends PageStateView.StateViewFactory> stateViewFactoryClass) {
        this.stateViewFactoryClass = stateViewFactoryClass;
    }

    public void setOkHttpFactoryClass(Class<? extends OkHttpInstance.OkHttpFactory> okHttpFactoryClass) {
        this.okHttpFactoryClass = okHttpFactoryClass;
    }

    public void setFrescoConfigFactoryClass(Class<? extends FrescoConfigFactory> frescoConfigFactoryClass) {
        this.frescoConfigFactoryClass = frescoConfigFactoryClass;
    }

    public void setSerializerFactoryClass(Class<? extends SerializerFactory> serializerFactoryClass) {
        this.serializerFactoryClass = serializerFactoryClass;
    }

    public void setPayKey(String wxPayId, String alPayId) {
        PayExtraData.wxPayId = wxPayId;
        PayExtraData.alPayId = alPayId;
    }

    public void setShareKey(String qqId, String wbId, String wxId) {
        ShareExtraData.QQId = qqId;
        ShareExtraData.WBId = wbId;
        ShareExtraData.WXId = wxId;
    }

    public NetAlert getNetAlert() {
        return netAlert;
    }

    private void initPay(Document document) throws Exception {
        //支付
        setPayKey(getConfigValue("wxPayId", document), getConfigValue("alPayId", document));
    }

    private void initShare(Document document) throws Exception {
        //分享
        setShareKey(getConfigValue("qqId", document), getConfigValue("wbId", document), getConfigValue("wxId", document));
    }

    private String getConfigValue(String name, Document document) throws Exception {
        return document.getElementsByTagName(name).item(0).getTextContent();
    }

    public void setNetAlert(NetAlert netAlert) {
        this.netAlert = netAlert;
    }
}
