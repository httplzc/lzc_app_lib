package pers.lizechao.android_lib.support.protocol.bus;

import android.arch.lifecycle.LifecycleOwner;

import java.util.HashMap;
import java.util.Map;

import pers.lizechao.android_lib.common.DestroyListener;

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
 * Date: 2018-09-11
 * Time: 9:15
 * 接口化 处理任务
 */
public class Bus extends LiveDataBus {
    private final static Bus instance = new Bus();
    private final Map<String, Object> sendMap = new HashMap<>();
    private final Map<Object, BusStubReceiver> forever = new HashMap<>();

    private Bus() {
    }


    public static Bus getInstance() {
        return instance;
    }

    public <T> T getCallBack(String name, Class<T> tClass) {
        String tagName = BusUtils.getTagName(name, tClass);
        T interfaceData = (T) sendMap.get(tagName);
        if (interfaceData == null) {
            BusStubSender<T> busStubSender = new BusStubSender<>(tClass, name);
            interfaceData = busStubSender.asInterface();
            sendMap.put(tagName, interfaceData);
        }
        return interfaceData;
    }

    public <T> T getCallBack(Class<T> tClass) {
        return getCallBack("", tClass);
    }


    public void unReceiver(Object listener) {
        BusStubReceiver stubReceiver = forever.get(listener);
        if (stubReceiver != null)
            stubReceiver.unReceive();
    }

    //T 为接口
    public <T> void receiver(String name, Class<T> tClass, LifecycleOwner lifecycleOwner, T listener) {
        BusStubReceiver<T> stubReceiver = new BusStubReceiver<>(tClass, name, lifecycleOwner);
        if (lifecycleOwner == null) {
            forever.put(listener, stubReceiver);
        }
        stubReceiver.receive(listener);
    }

    public <T> void receiver(Class<T> tClass, LifecycleOwner lifecycleOwner, T listener) {
        receiver("", tClass, lifecycleOwner, listener);
    }

    public <T> void receiverForever(Class<T> tClass, T listener) {
        receiver("", tClass, null, listener);
    }

    public <T> void receiverForever(String name, Class<T> tClass, T listener) {
        receiver(name, tClass,null, listener);
    }

    public <T> void receiverBeforeDestroy(String name, Class<T> tClass,LifecycleOwner lifecycleOwner,T listener) {
        lifecycleOwner.getLifecycle().addObserver(new DestroyListener(() -> unReceiver(listener)));
        receiver(name, tClass,null, listener);
    }

}
