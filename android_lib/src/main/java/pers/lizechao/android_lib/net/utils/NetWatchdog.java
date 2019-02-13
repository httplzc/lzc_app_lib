package pers.lizechao.android_lib.net.utils;

import android.arch.lifecycle.LifecycleOwner;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import pers.lizechao.android_lib.data.ApplicationData;
import pers.lizechao.android_lib.support.protocol.bus.Bus;


/**
 * Created by Lzc on 2017/12/11 0011.
 * 网络检测
 */

public class NetWatchdog {
    private static final NetWatchdog netWatchdog = new NetWatchdog();
    private List<NetWatchdog.NetChangeListener> listeners = new ArrayList<>();

    public enum ConnectState {Wifi, Mobile, None}

    private ConnectState currentConnectState = ConnectState.None;
    private final IntentFilter filter = new IntentFilter();
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            AndroidSchedulers.mainThread().scheduleDirect(() -> onStateChange());
        }
    };

    private NetWatchdog() {
        this.filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        startWatch();
        onStateChange();
    }

    private void onStateChange() {
        ConnectState lastState = currentConnectState;
        if (isMobileConnect(ApplicationData.applicationContext)) {
            currentConnectState = ConnectState.Mobile;
        } else if (isWifiConnect(ApplicationData.applicationContext)) {
            currentConnectState = ConnectState.Wifi;
        } else {
            currentConnectState = ConnectState.None;
        }


        if (currentConnectState == lastState)
            return;
        NetChangeListener listener = Bus.getInstance().getCallBack(NetChangeListener.class);
        if (currentConnectState == ConnectState.Mobile && lastState == ConnectState.Wifi) {
            listener.onWifiToMobile();
        } else if (currentConnectState == ConnectState.Wifi && lastState == ConnectState.Mobile) {
            listener.onMobileToWifi();
        } else if (currentConnectState == ConnectState.None) {
            listener.onNetDisconnected();
        } else if (lastState == ConnectState.None && (currentConnectState == ConnectState.Wifi ||
                currentConnectState == ConnectState.Mobile)) {
            listener.onNetConnect(currentConnectState);
        }
    }

    public static NetWatchdog getInstance() {
        return netWatchdog;
    }

    public ConnectState getCurrentConnectState() {
        return currentConnectState;
    }

    public static boolean hasNet(Context context) {
        return isWifiConnect(context) || isMobileConnect(context);
    }

    public static boolean isWifiConnect(Context context) {
        ConnectivityManager cm = null;
        try {
            cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cm == null)
            return false;
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.getState() == State.CONNECTED;
    }

    @Nullable
    public static NetworkInfo getNetInfo(Context context) {
        ConnectivityManager cm = null;
        try {
            cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cm == null)
            return null;
        return cm.getActiveNetworkInfo();
    }

    public static boolean isMobileConnect(Context context) {
        NetworkInfo networkInfo = getNetInfo(context);
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && networkInfo.getState() == State.CONNECTED;
    }

    public static boolean is4GConnect(Context context) {
        NetworkInfo networkInfo = getNetInfo(context);
        return networkInfo != null
                && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE
                && networkInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE;
    }

    public static boolean isFastConnect(Context context) {
        NetworkInfo networkInfo = getNetInfo(context);
        return networkInfo != null
                && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || isFastMobileNetwork(networkInfo.getSubtype()));
    }


    private static boolean isFastMobileNetwork(int subType) {
        switch (subType) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }

//    GPRS       2G(2.5) General Packet Radia Service 114kbps
//    EDGE       2G(2.75G) Enhanced Data Rate for GSM Evolution 384kbps
//    UMTS      3G WCDMA 联通3G Universal Mobile Telecommunication System 完整的3G移动通信技术标准
//    CDMA     2G 电信 Code Division Multiple Access 码分多址
//    EVDO_0   3G (EVDO 全程 CDMA2000 1xEV-DO) Evolution - Data Only (Data Optimized) 153.6kps - 2.4mbps 属于3G
//    EVDO_A  3G 1.8mbps - 3.1mbps 属于3G过渡，3.5G
//    1xRTT      2G CDMA2000 1xRTT (RTT - 无线电传输技术) 144kbps 2G的过渡,
//    HSDPA    3.5G 高速下行分组接入 3.5G WCDMA High Speed Downlink Packet Access 14.4mbps
//    HSUPA    3.5G High Speed Uplink Packet Access 高速上行链路分组接入 1.4 - 5.8 mbps
//    HSPA      3G (分HSDPA,HSUPA) High Speed Packet Access
//    IDEN      2G Integrated Dispatch Enhanced Networks 集成数字增强型网络 （属于2G，来自维基百科）
//    EVDO_B 3G EV-DO Rev.B 14.7Mbps 下行 3.5G
//    LTE        4G Long Term Evolution FDD-LTE 和 TDD-LTE , 3G过渡，升级版 LTE Advanced 才是4G
//    EHRPD  3G CDMA2000向LTE 4G的中间产物 Evolved High Rate Packet Data HRPD的升级
//    HSPAP  3G HSPAP 比 HSDPA 快些


    public void registerNetChangeListener(NetWatchdog.NetChangeListener l, LifecycleOwner lifecycleOwner) {
        Bus.getInstance().receiver(NetWatchdog.NetChangeListener.class, lifecycleOwner, l);
    }

    public void registerNetChangeListener(NetWatchdog.NetChangeListener l) {
        Bus.getInstance().receiverForever(NetWatchdog.NetChangeListener.class, l);
    }

    public void unRegisterNetChangeListener(NetWatchdog.NetChangeListener l) {
        Bus.getInstance().unReceiver(l);
    }

    public void startWatch() {
        try {
            ApplicationData.applicationContext.registerReceiver(this.receiver, this.filter);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public void stopWatch() {
        try {
            ApplicationData.applicationContext.unregisterReceiver(this.receiver);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public interface NetChangeListener {
        void onWifiToMobile();

        void onMobileToWifi();

        void onNetDisconnected();

        void onNetConnect(ConnectState connectState);
    }
}
