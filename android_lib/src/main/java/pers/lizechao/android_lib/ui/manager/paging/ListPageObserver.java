package pers.lizechao.android_lib.ui.manager.paging;

import java.util.List;

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
 * Date: 2018-09-06
 * Time: 18:24
 */
public interface ListPageObserver<T> {
    void onRefreshSucceed(List<T> list,boolean finish);

    void onLoadMoreSucceed(List<T> list,boolean finish);

    void onRefreshError(Throwable e);

    void onLoadMoreError(Throwable e);
}
