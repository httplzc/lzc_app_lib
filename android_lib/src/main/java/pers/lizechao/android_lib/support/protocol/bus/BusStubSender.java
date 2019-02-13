package pers.lizechao.android_lib.support.protocol.bus;

import pers.lizechao.android_lib.support.protocol.base.StubSender;

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
 * Time: 9:35
 */
public class BusStubSender<T> extends StubSender<T> {
    private final String tag;

    public BusStubSender(Class<T> interfaceClass, String tag) {
        super(interfaceClass);
        this.tag = tag;
    }

    @Override
    protected void sendStubData(StubData stubData) {
        LiveDataBus.get().with(BusUtils.getTagName(tag,interfaceClass)).setValue(stubData);
    }
}
