package pers.lizechao.android_lib.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.ObjectsCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.annimon.stream.Objects;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * Date: 2018-07-03
 * Time: 9:50
 */
public class HeadFootRecycleView extends BaseRecycleView {
    private static final int HeadType = 1024;
    private static final int FootType = -1024;
    @Nullable
    HeadFootRecycleViewAdapter realAdapter;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;
    private OnHeadFootClickListener headFootClickListener;
    private RecyclerView.Adapter originAdapter;
    private final List<WrapHeadFoot> headViewList = new ArrayList<>();
    private final List<WrapHeadFoot> footViewList = new ArrayList<>();
    //记录类型与对应View
    private final Map<Integer, WrapHeadFoot> typeViewMap = new HashMap<>();
    private OnAdapterChangeListener onAdapterChangeListener;

    private class WrapHeadFoot {
        View view;
        int type;

        public WrapHeadFoot(View view, int type) {
            this.view = view;
            this.type = type;
        }
    }

    public HeadFootRecycleView(Context context) {
        super(context);
    }

    public HeadFootRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HeadFootRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void addHeadView(View view) {
        addHeadView(view, getHeadCount());
    }

    public void addHeadView(View view, int position) {
        WrapHeadFoot wrapHeadFoot = new WrapHeadFoot(view, HeadType + headViewList.size());
        headViewList.add(position, wrapHeadFoot);
        typeViewMap.put(wrapHeadFoot.type, wrapHeadFoot);
        if (realAdapter != null) {
            realAdapter.notifyItemInserted(position);
        }
    }

    public void removeHeadView(int position) {
        WrapHeadFoot wrapHeadFoot = headViewList.remove(position);
        if (wrapHeadFoot != null)
            typeViewMap.remove(wrapHeadFoot.type);
        if (realAdapter != null)
            realAdapter.notifyItemRemoved(position);
    }


    public void addFootView(View view) {
        addFootView(view, getFootCount());
    }

    public void addFootView(View view, int position) {
        WrapHeadFoot wrapHeadFoot = new WrapHeadFoot(view, FootType - footViewList.size());
        footViewList.add(position, wrapHeadFoot);
        typeViewMap.put(wrapHeadFoot.type, wrapHeadFoot);
        if (realAdapter != null) {
            final int pos = getHeadCount() + getContentCount() + position;
            realAdapter.notifyItemInserted(pos);
        }

    }

    public void removeFootView(int position) {
        WrapHeadFoot wrapHeadFoot = footViewList.remove(position);
        if (wrapHeadFoot != null)
            typeViewMap.remove(wrapHeadFoot.type);
        if (realAdapter != null) {
            final int pos = getHeadCount() + getContentCount() + position;
            realAdapter.notifyItemChanged(pos);
        }
    }

    public void removeFootView(View view) {
        int position = -1;
        for (int i = 0; i < footViewList.size(); i++) {
            if (ObjectsCompat.equals(footViewList.get(i).view, view)) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            removeFootView(position);
        }
    }

    public void removeHeadView(View view) {
        int position = -1;
        for (int i = 0; i < headViewList.size(); i++) {
            if (ObjectsCompat.equals(headViewList.get(i).view, view)) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            removeHeadView(position);
        }
    }


    public int getContentCount() {
        if (originAdapter != null)
            return originAdapter.getItemCount();
        return 0;
    }

    public int getTotalCount() {
        if (realAdapter != null)
            return realAdapter.getItemCount();
        return 0;
    }


    public RecyclerView.Adapter getAdapter() {
        return originAdapter;
    }

    public RecyclerView.Adapter getRealAdapter() {
        return realAdapter;
    }


    public void setHeadFootClickListener(OnHeadFootClickListener headFootClickListener) {
        this.headFootClickListener = headFootClickListener;
    }

    @Override
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        super.setOnItemClickListener((view, position) -> {
            if (realAdapter == null)
                return;
            if (realAdapter.isHead(position)) {
                if (headFootClickListener != null)
                    headFootClickListener.onHeadClick(view, realAdapter.positionToHeadPosition(position));
            } else if (realAdapter.isFoot(position)) {
                if (headFootClickListener != null)
                    headFootClickListener.onFootClick(view, realAdapter.positionToFootPosition(position));
            } else {
                if (this.onItemClickListener != null)
                    onItemClickListener.onItemClick(view, realAdapter.positionToContentPosition(position));
            }
        });
    }

    @Override
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
        super.setOnItemLongClickListener((view, position) -> {
            if (realAdapter == null)
                return;
            if (!realAdapter.isHeadOrFoot(position)) {
                if (this.onItemLongClickListener != null)
                    this.onItemLongClickListener.onItemLongClick(view, realAdapter.positionToContentPosition(position));
            }
        });
    }

    private AdapterDataObserver dataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            if (realAdapter == null)
                return;
            realAdapter.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            if (realAdapter == null)
                return;
            super.onItemRangeChanged(positionStart, itemCount);
            realAdapter.notifyItemRangeChanged(positionStart + getHeadCount(), itemCount);
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            if (realAdapter == null)
                return;
            super.onItemRangeChanged(positionStart, itemCount, payload);
            realAdapter.notifyItemRangeChanged(positionStart + getHeadCount(), itemCount, payload);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            if (realAdapter == null)
                return;
            realAdapter.notifyItemRangeInserted(positionStart + getHeadCount(), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            if (realAdapter == null)
                return;
            realAdapter.notifyItemRangeRemoved(positionStart + getHeadCount(), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            if (realAdapter == null)
                return;
            realAdapter.notifyItemMoved(fromPosition + getHeadCount(), toPosition + getHeadCount());
        }
    };

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter == null)
            return;
        if (originAdapter != null) {
            originAdapter.unregisterAdapterDataObserver(dataObserver);
        }
        this.originAdapter = adapter;
        adapter.registerAdapterDataObserver(dataObserver);
        adapter = realAdapter = new HeadFootRecycleViewAdapter(adapter);
        super.setAdapter(adapter);
        if (onAdapterChangeListener != null)
            onAdapterChangeListener.onChange(originAdapter);
    }


    public List<View> getHeadViewList() {
        return Stream.of(headViewList).map(w -> w.view).toList();
    }


    public List<View> getFootViewList() {
        return Stream.of(footViewList).map(w -> w.view).toList();
    }


    public int getHeadCount() {
        return headViewList.size();
    }

    public int getFootCount() {
        return footViewList.size();
    }


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
     * Date: 2018-07-03
     * Time: 13:44
     * 装饰者模式添加头部和尾部的RecycleView Adapter
     */
    final class HeadFootRecycleViewAdapter extends RecyclerView.Adapter {

        //原始Adapter
        @NonNull
        private final RecyclerView.Adapter<? super RecyclerView.ViewHolder> adapter;

        HeadFootRecycleViewAdapter(@NonNull RecyclerView.Adapter<? super RecyclerView.ViewHolder> adapter) {
            adapter = Objects.requireNonNull(adapter);
            this.adapter = adapter;
        }

        boolean isHead(int position) {
            return position < headViewList.size();
        }

        boolean isFoot(int position) {
            return position > getItemCount() - footViewList.size() - 1;
        }

        boolean isHeadOrFoot(int position) {
            return isHead(position) || isFoot(position);
        }


        int positionToHeadPosition(int position) {
            return position;
        }

        int positionToContentPosition(int position) {
            return position - headViewList.size();
        }

        int positionToFootPosition(int position) {
            return position - adapter.getItemCount() - headViewList.size();
        }


        @Override
        public int getItemViewType(int position) {
            if (isHead(position)) {
                return headViewList.get(position).type;
            } else if (isFoot(position)) {
                return footViewList.get(position - getHeadCount() - getContentCount()).type;
            } else {
                return adapter.getItemViewType(position - headViewList.size());
            }
        }


        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType >= HeadType) {
                HeadFootRecycleView.this.removeView(typeViewMap.get(viewType).view);
                return new HeadFootViewHolder(typeViewMap.get(viewType).view);
            } else if (viewType <= FootType) {
                return new HeadFootViewHolder(typeViewMap.get(viewType).view);
            } else {
                return adapter.onCreateViewHolder(parent, viewType);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
            if (!isHeadOrFoot(position)) {
                adapter.onBindViewHolder(holder, position - headViewList.size());
            }
        }


        private class HeadFootViewHolder extends RecyclerView.ViewHolder {
            HeadFootViewHolder(View itemView) {
                super(itemView);
            }
        }


        @Override
        public int getItemCount() {
            return headViewList.size() + adapter.getItemCount() + footViewList.size();
        }


        @Override
        public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
            adapter.onDetachedFromRecyclerView(recyclerView);
        }


        @Override
        public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
            adapter.onViewDetachedFromWindow(holder);
        }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                final GridLayoutManager gridManager = ((GridLayoutManager) manager);
                gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return isHeadOrFoot(position)
                                ? gridManager.getSpanCount() : 1;
                    }
                });
            }
            adapter.onAttachedToRecyclerView(recyclerView);
        }


        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                ((StaggeredGridLayoutManager.LayoutParams) lp).setFullSpan(isHeadOrFoot(holder.getAdapterPosition()));
            } else if (!(lp instanceof GridLayoutManager.LayoutParams)) {
                if (isHeadOrFoot(holder.getAdapterPosition()))
                    ((RecyclerView.LayoutParams) lp).width = RecyclerView.LayoutParams.MATCH_PARENT;
            }
            adapter.onViewAttachedToWindow(holder);
        }
    }

    public interface OnHeadFootClickListener {
        void onHeadClick(View view, int position);

        void onFootClick(View view, int position);
    }

    public interface OnAdapterChangeListener {
        void onChange(Adapter newAdapter);
    }

    public void setOnAdapterChangeListener(OnAdapterChangeListener onAdapterChangeListener) {
        this.onAdapterChangeListener = onAdapterChangeListener;
    }
}
