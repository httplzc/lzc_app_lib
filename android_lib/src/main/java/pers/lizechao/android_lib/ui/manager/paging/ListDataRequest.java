package pers.lizechao.android_lib.ui.manager.paging;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
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
 * Date: 2018-09-07
 * Time: 9:11
 */
public abstract class ListDataRequest<T> {
    private final List<ListPageObserver<T>> listPageManagerList = new ArrayList<>();
    private Disposable disposable;

    public void loadMoreData(boolean useCache) {
        Single<List<T>> single = getLoadMoreSingle();
        if (single instanceof ApiRequestCreater.ApiSingle) {
            ((ApiRequestCreater.ApiSingle) single).getApiRequest().setUseCache(useCache);
        }
        single.subscribe(new SingleObserver<List<T>>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onSuccess(List<T> list) {
                for (ListPageObserver<T> tListPageObserver : listPageManagerList) {
                    tListPageObserver.onLoadMoreSucceed(list, checkPageFinish(true, list));
                }
            }

            @Override
            public void onError(Throwable e) {
                for (ListPageObserver<T> tListPageObserver : listPageManagerList) {
                    tListPageObserver.onLoadMoreError(e);
                }
                if (ProjectConfig.getInstance().getNetAlert() != null)
                    ProjectConfig.getInstance().getNetAlert().onNetError(e);
            }
        });
    }

    public void loadRefreshData(SingleObserver<? super List<T>> observer, boolean useCache) {
        Single<List<T>> single = getLoadRefreshSingle();
        if (single instanceof ApiRequestCreater.ApiSingle) {
            ((ApiRequestCreater.ApiSingle) single).getApiRequest().setUseCache(useCache);
        }
        single.subscribe(new SingleObserver<List<T>>() {
            @Override
            public void onSubscribe(Disposable d) {
                disposable = d;
            }

            @Override
            public void onSuccess(List<T> list) {
                observer.onSuccess(list);
                for (ListPageObserver<T> tListPageObserver : listPageManagerList) {
                    tListPageObserver.onRefreshSucceed(list, checkPageFinish(false, list));
                }
            }

            @Override
            public void onError(Throwable e) {
                observer.onError(e);
                for (ListPageObserver<T> tListPageObserver : listPageManagerList) {
                    tListPageObserver.onRefreshError(e);
                }
                if (ProjectConfig.getInstance().getNetAlert() != null)
                    ProjectConfig.getInstance().getNetAlert().onNetError(e);
            }
        });
    }

    public void loadRefreshData(boolean useCache) {
        Single<List<T>> single = getLoadRefreshSingle();
        if (single instanceof ApiRequestCreater.ApiSingle) {
            ((ApiRequestCreater.ApiSingle) single).getApiRequest().setUseCache(useCache);
        }
        single
                .subscribe(new SingleObserver<List<T>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onSuccess(List<T> list) {
                        for (ListPageObserver<T> tListPageObserver : listPageManagerList) {
                            tListPageObserver.onRefreshSucceed(list, checkPageFinish(false, list));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        for (ListPageObserver<T> tListPageObserver : listPageManagerList) {
                            tListPageObserver.onRefreshError(e);
                        }
                        if (ProjectConfig.getInstance().getNetAlert() != null)
                            ProjectConfig.getInstance().getNetAlert().onNetError(e);
                    }
                });
    }


    public void registerManager(ListPageObserver<T> pageManager) {
        listPageManagerList.add(pageManager);
    }

    public void unRegister(ListPageObserver<T> pageManager) {
        listPageManagerList.remove(pageManager);
    }


    protected abstract Single<List<T>> getLoadMoreSingle();

    protected abstract Single<List<T>> getLoadRefreshSingle();

    protected abstract boolean checkPageFinish(boolean isMore, List<T> list);

    public Disposable getDisposable() {
        return disposable;
    }
}
