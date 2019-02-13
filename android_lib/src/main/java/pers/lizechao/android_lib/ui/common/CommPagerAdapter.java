package pers.lizechao.android_lib.ui.common;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
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
 * Date: 2018-08-03
 * Time: 16:54
 */
public class CommPagerAdapter<T, BR extends ViewDataBinding> extends PagerAdapter {
    protected List<T> dataList = new ArrayList<>();
    protected BRHolder<BR>[] brHolders;
    protected final Context context;
    protected final int layoutId;
    protected final int BR_ID;


    public CommPagerAdapter(Context context, int layoutId, int BR_ID) {
        this.context = context;
        this.layoutId = layoutId;
        this.BR_ID = BR_ID;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        int wantPos = 0;
        //找到合适position
        while (brHolders[wantPos] != null && brHolders[wantPos].haveAdd() && wantPos < brHolders.length - 1) {
            wantPos++;
        }
        //如果为空则创建
        if (brHolders[wantPos] == null) {
            brHolders[wantPos] = new BRHolder<>(DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, container, false));
        }
        //如果需要重新绑定数据
        if (brHolders[wantPos].needResetData(position)) {
            brHolders[wantPos].br.setVariable(BR_ID, dataList.get(position));
        }

        container.addView(brHolders[wantPos].getView());
        brHolders[wantPos].onAdd(position);
        return brHolders[wantPos].getView();
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        for (CommPagerAdapter.BRHolder brHolder : brHolders) {
            if (brHolder != null && brHolder.lastUsePosition == position) {
                container.removeView(brHolder.getView());
                brHolder.onRemove();
                return;
            }
        }

    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
        brHolders = new BRHolder[dataList.size()];
    }

    public BRHolder<BR>[] getBrHolders() {
        return brHolders;
    }

    public List<T> getDataList() {
        return dataList;
    }


    public class BRHolder<BR_ extends ViewDataBinding> {
        public final BR_ br;
        int lastUsePosition = -1;

        BRHolder(BR_ br) {
            this.br = br;
        }

        void onAdd(int newPosition) {
            lastUsePosition = newPosition;
        }

        void onRemove() {
            lastUsePosition = -1;
        }

        boolean haveAdd() {
            return lastUsePosition != -1;
        }

        boolean needResetData(int position) {
            return lastUsePosition != position;
        }

        View getView() {
            return br.getRoot();
        }
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
