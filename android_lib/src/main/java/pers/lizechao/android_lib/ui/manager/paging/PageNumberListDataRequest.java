package pers.lizechao.android_lib.ui.manager.paging;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import io.reactivex.Single;

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
 * Time: 9:51
 */
public abstract class PageNumberListDataRequest<T> extends ListDataRequest<T> implements PageNumberRequest<T> {
    private final int pageSize;
    private final int pageNumberStart;

    private PageNumberListDataRequest(int pageSize, int pageNumberStart) {
        this.pageSize = pageSize;
        this.pageNumberStart = pageNumberStart;
    }

    @Override
    protected Single<List<T>> getLoadMoreSingle() {
        return request(getPagerNumber() + pageNumberStart, pageSize,true);
    }

    @Override
    protected Single<List<T>> getLoadRefreshSingle() {
        return request(pageNumberStart, pageSize,false);
    }

    private int getPagerNumber() {
        return getAdapter().getItemCount() / pageSize;
    }

    public abstract RecyclerView.Adapter getAdapter();


    @Override
    public boolean checkPageFinish(boolean isMore, List<T> list) {
        return list.size() < pageSize;
    }


    public static class Builder<T> {
        private int pageSize = 15;
        private int pageNumberStart = 1;
        private RecyclerView.Adapter adapter;
        private PageNumberRequest<T> pageNumberRequest;

        public Builder<T> setPageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }

        public Builder<T> setPageNumberStart(int pageNumberStart) {
            this.pageNumberStart = pageNumberStart;
            return this;
        }

        public Builder<T> setAdapter(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
            return this;
        }

        public Builder<T> setPageNumberRequest(PageNumberRequest<T> pageNumberRequest) {
            this.pageNumberRequest = pageNumberRequest;
            return this;
        }

        public PageNumberListDataRequest<T> build() {
            return new PageNumberListDataRequest<T>(pageSize, pageNumberStart) {
                @Override
                public RecyclerView.Adapter getAdapter() {
                    return adapter;
                }

                @Override
                public Single<List<T>> request(int pageNumber, int pageSize,boolean isMoreRequest) {
                    return pageNumberRequest.request(pageNumber, pageSize,isMoreRequest);
                }
            };
        }
    }


}
