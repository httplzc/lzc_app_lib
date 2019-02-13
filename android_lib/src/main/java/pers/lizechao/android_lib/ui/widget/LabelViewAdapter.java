package pers.lizechao.android_lib.ui.widget;

import android.database.Observable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

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
 * Date: 2018-07-12
 * Time: 13:55
 * 标签View的数据适配器
 */
public abstract class LabelViewAdapter {
    private final AdapterDataObservable mObservable = new AdapterDataObservable();

    public abstract View bindData(int position, ViewGroup viewGroup);

    public abstract int getCount();

    class AdapterDataObservable extends Observable<AdapterDataObserver> {
        void notifyChanged() {
            for (AdapterDataObserver mObserver : mObservers) {
                mObserver.onChanged();
            }
        }

        void notifyItemRangeChanged(int positionStart, int itemCount) {
            for (AdapterDataObserver mObserver : mObservers) {
                mObserver.onItemRangeChanged(positionStart, itemCount);
            }
        }


        void notifyItemRangeInserted(int positionStart, int itemCount) {
            for (AdapterDataObserver mObserver : mObservers) {
                mObserver.onItemRangeInserted(positionStart, itemCount);
            }
        }

        void notifyItemRangeRemoved(int positionStart, int itemCount) {
            for (AdapterDataObserver mObserver : mObservers) {
                mObserver.onItemRangeRemoved(positionStart, itemCount);
            }
        }
    }

    public void notifyChanged() {
        mObservable.notifyChanged();
    }

    public void notifyItemChanged(int position) {
        mObservable.notifyItemRangeChanged(position, 1);

    }

    public void notifyItemInserted(int position) {
        notifyItemRangeInserted(position, 1);
    }

    public void notifyItemRemove(int position) {
        notifyItemRangeRemoved(position, 1);
    }

    public void notifyItemRangeChanged(int positionStart, int itemCount) {
        mObservable.notifyItemRangeChanged(positionStart, itemCount);

    }

    public void notifyItemRangeInserted(int positionStart, int itemCount) {
        mObservable.notifyItemRangeInserted(positionStart, itemCount);

    }

    public void notifyItemRangeRemoved(int positionStart, int itemCount) {
        mObservable.notifyItemRangeRemoved(positionStart, itemCount);

    }

    interface AdapterDataObserver {
        void onChanged();

        void onItemRangeChanged(int positionStart, int itemCount);

        void onItemRangeInserted(int positionStart, int itemCount);

        void onItemRangeRemoved(int positionStart, int itemCount);
    }

    public void registerAdapterDataObserver(@NonNull AdapterDataObserver observer) {
        mObservable.registerObserver(observer);
    }

    public void unregisterAdapterDataObserver(@NonNull AdapterDataObserver observer) {
        mObservable.unregisterObserver(observer);
    }
}
