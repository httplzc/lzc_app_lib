package pers.lizechao.android_lib.storage.db;

import java.lang.reflect.Type;
import java.util.List;

import pers.lizechao.android_lib.support.protocol.base.ThreadReceiverTarget;
import pers.lizechao.android_lib.support.protocol.broadcast.BroadcastMsg;

/**
 * Created by Lzc on 2018/6/2 0002.
 */
@BroadcastMsg(action = "com.lizechao.store_StoreMsgStub_DataChangeAction",
  permission = "")
@ThreadReceiverTarget(ThreadReceiverTarget.ThreadTarget.Io)
public interface IStoreDataChange {
    void dataUpdate(String key, Type type);

    void dataUpdate(List<String> key, Type type);

    void dataDelete(String key, Type type);

    void dataDelete(List<String> key, Type type);
}
