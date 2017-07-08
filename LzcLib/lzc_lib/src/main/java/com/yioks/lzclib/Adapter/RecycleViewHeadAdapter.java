package com.yioks.lzclib.Adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.yioks.lzclib.View.RecycleView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 装饰者模式添加头部
 */

public final class RecycleViewHeadAdapter extends RecyclerView.Adapter {
    private RecyclerView.Adapter adapter;
    private List<View> headViewList = new ArrayList<>();
    private List<View> footViewList = new ArrayList<>();
    private onItemClickListener itemClickListener;
    private HashMap<View, WrapperViewHolder> bindData = new HashMap<>();

    public RecycleViewHeadAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    public RecycleViewHeadAdapter() {
    }

    public int getIndexOfFoot(View view) {
        return footViewList.indexOf(view);
    }

    public boolean footContain(View view) {
        return footViewList.contains(view);
    }

    public boolean headContain(View view) {
        return headViewList.contains(view);
    }

    public int getIndexOfHead(View view) {
        return headViewList.indexOf(view);
    }


//    public List<View> getFootViewList() {
//        return footViewList;
//    }
//
//    public void setFootViewList(List<View> footViewList) {
//        this.footViewList = footViewList;
//    }
//
//    public List<View> getHeadViewList() {
//        return headViewList;
//    }
//
//    public void setHeadViewList(List<View> headViewList) {
//        this.headViewList = headViewList;
//    }

    public HashMap<View, WrapperViewHolder> getBindData() {
        return bindData;
    }

    public void setBindData(HashMap<View, WrapperViewHolder> bindData) {
        this.bindData = bindData;
    }

    public void addHeadView(View view, WrapperViewHolder wrapperViewHolder) {
        headViewList.add(view);
        bindData.put(view, wrapperViewHolder);
    }

    public void addHeadView(View view, int position, WrapperViewHolder wrapperViewHolder) {
        headViewList.add(position, view);
        bindData.put(view, wrapperViewHolder);
    }

    public void removeHeadView(View view) {
        headViewList.remove(view);
        bindData.remove(view);

    }

    public int getHeadCount() {
        return headViewList.size();
    }

    public int getFootCount() {
        return footViewList.size();
    }

    public void addFootView(View view, WrapperViewHolder wrapperViewHolder) {
        footViewList.add(view);
        bindData.put(view, wrapperViewHolder);
    }

    public void addFootView(View view, int position, WrapperViewHolder wrapperViewHolder) {
        footViewList.add(position, view);
        bindData.put(view, wrapperViewHolder);
    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    public RecyclerView.Adapter setAdapter(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
        return this;
    }

    public void removeFootView(View view) {
        footViewList.remove(view);
        bindData.remove(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (isHead(position)) {
            return 100 + position;
        } else if (isFoot(position)) {
            return -(position - adapter.getItemCount() - headViewList.size()) - 100;
        } else {
            return adapter.getItemViewType(position - headViewList.size());
        }
    }

    public onItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(onItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType >= 100) {
            View view = headViewList.get(viewType - 100);
            WrapperViewHolder wrapperViewHolder = bindData.get(view);
            Log.i("lzc", "create_haed"+(viewType - 100));
            return wrapperViewHolder == null ? new WrapperViewHolderImp(view) : wrapperViewHolder;
        } else if (viewType <= -100) {
            Log.i("lzc", "create_bottom"+(-(viewType + 100)));
            View view = footViewList.get(-(viewType + 100));
            WrapperViewHolder wrapperViewHolder = bindData.get(view);
            return wrapperViewHolder == null ? new WrapperViewHolderImp(view) : wrapperViewHolder;
        } else {
            return adapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (!isFoot(holder.getAdapterPosition())) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        if (isHead(holder.getAdapterPosition())) {
                            itemClickListener.onHeadClick(v, holder.getAdapterPosition());
                        } else if (isFoot(holder.getAdapterPosition())) {
                            itemClickListener.onFootClick(v, holder.getAdapterPosition() - adapter.getItemCount() - headViewList.size());
                        } else {
                            itemClickListener.onItemClick(v, holder.getAdapterPosition() - headViewList.size());
                        }
                    }

                }
            });
        }
        if (!isHeadOrFoot(position)) {
            adapter.onBindViewHolder(holder, position - headViewList.size());
        } else {
            if (holder instanceof WrapperViewHolder)
                ((WrapperViewHolder) holder).bindData(isHead(position) ? headViewList.indexOf(holder.itemView) : footViewList.indexOf(holder.itemView)
                        , isHead(position));
        }
    }


    @Override
    public int getItemCount() {
        return headViewList.size() + adapter.getItemCount() + footViewList.size();
    }

    public abstract static class WrapperViewHolder extends RecyclerView.ViewHolder {
        public WrapperViewHolder(View itemView) {
            super(itemView);
        }

        abstract public void bindData(int position, boolean iHead);
    }

    private class WrapperViewHolderImp extends WrapperViewHolder {
        public WrapperViewHolderImp(View itemView) {
            super(itemView);
        }

        public void bindData(int position, boolean iHead) {

        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        adapter.onDetachedFromRecyclerView(recyclerView);
    }


    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        adapter.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
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

    public boolean isHeadOrFoot(RecyclerView.ViewHolder holder) {
        return isHeadOrFoot(holder.getAdapterPosition());
    }

    public boolean isHead(int position) {
        return position < headViewList.size();
    }

    public boolean isFoot(int position) {
//        Log.i("lzc","getItemCount"+getItemCount()+"----"+footViewList.size());
        return position > getItemCount() - footViewList.size() - 1;
    }

    public boolean isHeadOrFoot(int position) {
        return isHead(position) || isFoot(position);
    }


    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            ((StaggeredGridLayoutManager.LayoutParams) lp).setFullSpan(isHeadOrFoot(holder));
        } else if (!(lp instanceof GridLayoutManager.LayoutParams)) {
            if (isHeadOrFoot(holder))
                ((RecycleView.LayoutParams) lp).width = RecyclerView.LayoutParams.MATCH_PARENT;
        }
        adapter.onViewAttachedToWindow(holder);
    }

    public interface onItemClickListener {
        void onItemClick(View view, int position);

        void onHeadClick(View view, int position);

        void onFootClick(View view, int position);
    }

    public List<View> getHeadViewList() {
        return headViewList;
    }

    public void setHeadViewList(List<View> headViewList) {
        this.headViewList = headViewList;
    }

    public List<View> getFootViewList() {
        return footViewList;
    }

    public void setFootViewList(List<View> footViewList) {
        this.footViewList = footViewList;
    }
}
