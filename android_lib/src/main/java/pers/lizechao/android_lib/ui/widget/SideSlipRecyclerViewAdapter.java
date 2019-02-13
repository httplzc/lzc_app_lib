package pers.lizechao.android_lib.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import pers.lizechao.android_lib.utils.JavaUtils;

import java.util.Arrays;
import java.util.Collections;
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
 * Date: 2018-07-09
 * Time: 9:21
 * 可以侧滑的RecycleView的数据适配器
 */
public abstract class SideSlipRecyclerViewAdapter extends RecyclerView.Adapter<SideSlipRecyclerViewAdapter.ViewHolder> {
    private final Context context;
    private List<Boolean> isOpenList;
    private ScrollListener scrollListener;
    private OpenListener openListener;

    private void initOpenList() {
        isOpenList= JavaUtils.newList(getItemCount(),false);
    }

    public SideSlipRecyclerViewAdapter(Context context) {
        this.context = context;
        this.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                initOpenList();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                isOpenList.addAll(positionStart, Arrays.asList(new Boolean[itemCount]));
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                for (int i = 0; i < itemCount; i++) {
                    isOpenList.remove(positionStart);
                }

            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                Collections.swap(isOpenList, toPosition, itemCount);
            }
        });
    }


    @NonNull
    @Override
    public SideSlipRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SideSlipAbleView sideSlipAbleView = new SideSlipAbleView(context);
        return new ViewHolderWrapper(sideSlipAbleView, getContentViewHolder(sideSlipAbleView, viewType), getSideViewHolder(sideSlipAbleView, viewType));
    }

    @Nullable
    public abstract SideSlipRecyclerViewAdapter.ViewHolder getSideViewHolder(ViewGroup parent, int viewType);

    public abstract SideSlipRecyclerViewAdapter.ViewHolder getContentViewHolder(ViewGroup parent, int viewType);

    public abstract void onBindContentViewHolder(SideSlipRecyclerViewAdapter.ViewHolder holder, int position);

    public abstract void onBindSideViewHolder(@Nullable SideSlipRecyclerViewAdapter.ViewHolder holder, int position);


    @Override
    public void onBindViewHolder(@NonNull SideSlipRecyclerViewAdapter.ViewHolder holder, int position) {
        if (isOpenList.size() != getItemCount())
            initOpenList();
        ViewHolderWrapper wrapper = (ViewHolderWrapper) holder;
        onBindContentViewHolder(wrapper.viewHolderContent, position);
        onBindSideViewHolder(wrapper.viewHolderSide, position);
        wrapper.slipAbleView.resetState(isOpenList.get(position));
    }

    boolean isOpen(int position) {
        return isOpenList.get(position);
    }

    void setOpenOrClose(int position, boolean open) {
        isOpenList.set(position, open);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public int getCurrentPosition() {
            return viewHolderWrapper.getAdapterPosition();
        }

        private ViewHolderWrapper viewHolderWrapper = null;

        ViewHolder(View itemView) {
            super(itemView);
        }

        public void setViewHolderWrapper(@NonNull ViewHolderWrapper viewHolderWrapper) {
            this.viewHolderWrapper = viewHolderWrapper;
        }
    }


    class ViewHolderWrapper extends ViewHolder {
        private final SideSlipRecyclerViewAdapter.ViewHolder viewHolderContent;
        @Nullable
        private final SideSlipRecyclerViewAdapter.ViewHolder viewHolderSide;

        final SideSlipAbleView slipAbleView;

        ViewHolderWrapper(SideSlipAbleView sideSlipAbleView, SideSlipRecyclerViewAdapter.ViewHolder viewHolderContent,
                          @Nullable SideSlipRecyclerViewAdapter.ViewHolder viewHolderSide) {
            super(sideSlipAbleView);
            this.slipAbleView = sideSlipAbleView;
            sideSlipAbleView.setView(viewHolderContent.itemView, viewHolderSide != null ? viewHolderSide.itemView : null);
            this.viewHolderContent = viewHolderContent;
            this.viewHolderSide = viewHolderSide;
            viewHolderContent.setViewHolderWrapper(this);
            if (viewHolderSide != null)
                viewHolderSide.setViewHolderWrapper(this);

            slipAbleView.setOpenListener(new SideSlipAbleView.OpenListener() {
                @Override
                public void open() {
                    isOpenList.set(getAdapterPosition(), true);
                    if (openListener != null)
                        openListener.openOrClose(getAdapterPosition(), true);
                }

                @Override
                public void close() {
                    isOpenList.set(getAdapterPosition(), false);
                    if (openListener != null)
                        openListener.openOrClose(getAdapterPosition(), false);
                }
            });
            slipAbleView.setScrollListener((currentScroll, maxScroll) -> {
                if (scrollListener != null)
                    scrollListener.scroll(getAdapterPosition(), currentScroll, maxScroll);
            });


        }
    }

    public interface ScrollListener {
        void scroll(int position, float currentScroll, float maxScroll);
    }

    public interface OpenListener {
        void openOrClose(int position, boolean isOpen);
    }

    public void setOpenListener(OpenListener openListener) {
        this.openListener = openListener;
    }

    public void setScrollListener(ScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }
}
