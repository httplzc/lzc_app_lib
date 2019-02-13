package pers.lizechao.android_lib.ui.manager;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.annimon.stream.function.Supplier;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import pers.lizechao.android_lib.ProjectConfig;
import pers.lizechao.android_lib.net.api.ApiRequestCreater;

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
 * Date: 2018-09-10
 * Time: 10:51
 * 帮助页面将多个请求合并为一个请求，并向页面赋值
 */
public class DataBindRequestManager {
    private final ViewDataBinding viewBind;
    private final LifecycleOwner lifecycleOwner;
    private List<Request> requestList = new ArrayList<>();

    public DataBindRequestManager(ViewDataBinding viewBind, LifecycleOwner lifecycleOwner) {
        this.viewBind = viewBind;
        this.lifecycleOwner = lifecycleOwner;
    }

    @Nullable
    public Completable getRequest(boolean useCache) {
        return createRequest(useCache);
    }
    @Nullable
    private <D> Completable createRequest(boolean useCache) {
        Completable completable = null;
        for (Request req : requestList) {
            Request<D> request = req;
            Single<D> single = request.supplier.get();
            if (single instanceof ApiRequestCreater.ApiSingle) {
                ((ApiRequestCreater.ApiSingle) single).getApiRequest().setUseCache(useCache);
            }
            Single<D> requestSingle = single.doOnSuccess(request.mutableLiveData::setValue);
            if (completable == null) {
                completable = requestSingle.toCompletable();
            } else {
                completable = completable.concatWith(requestSingle.toCompletable());
            }
        }
        if (completable != null)
            return completable.doOnError(throwable -> {
                if (ProjectConfig.getInstance().getNetAlert() != null)
                    ProjectConfig.getInstance().getNetAlert().onNetError(throwable);
            });
        return null;
    }

    public void cancelRequest() {

    }

    public <D> void registerDataRequest(@NonNull Supplier<Single<D>> supplier, int BR_ID) {
        this.registerDataRequest(supplier, BR_ID, null);
    }

    public <D> void registerDataRequest(@NonNull Supplier<Single<D>> supplier, Observer<D> observer) {
        this.registerDataRequest(supplier, -1, observer);
    }

    public <D> void registerDataRequest(@NonNull Supplier<Single<D>> supplier, int BR_ID, @Nullable Observer<D> observer) {
        MutableLiveData<D> mutableLiveData = new MutableLiveData<>();
        if (BR_ID != -1) {
            mutableLiveData.observe(lifecycleOwner, o -> viewBind.setVariable(BR_ID, o));
        }
        if (observer != null)
            mutableLiveData.observe(lifecycleOwner, observer);
        requestList.add(new Request<D>(supplier, mutableLiveData));
    }

    private class Request<D> {
        Supplier<Single<D>> supplier;
        MutableLiveData<D> mutableLiveData;

        Request(Supplier<Single<D>> supplier, MutableLiveData<D> mutableLiveData) {
            this.supplier = supplier;
            this.mutableLiveData = mutableLiveData;
        }
    }

}
