package pers.lizechao.android_lib.ui.common;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.ViewGroup;

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
 * Date: 2018-09-01
 * Time: 17:58
 */
public class CommInfinitePagerAdapter<T, BR extends ViewDataBinding> extends CommPagerAdapter<T, BR> {
    public CommInfinitePagerAdapter(Context context, int layoutId, int BR_ID) {
        super(context, layoutId, BR_ID);
    }

    public int calcCurrentItem(ViewPager viewPager)
    {
       return viewPager.getCurrentItem()%dataList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int positionOri) {
        int position = positionOri % dataList.size();
        int wantPos = 0;
        //找到合适position
        while (brHolders[wantPos] != null && brHolders[wantPos].haveAdd() && wantPos < brHolders.length - 1) {
            wantPos++;
        }
        //如果为空则创建
        if (brHolders[wantPos] == null) {
            brHolders[wantPos] = new BRHolderInfinite(DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, container, false));
        }
        //如果需要重新绑定数据
        if (brHolders[wantPos].needResetData(positionOri)) {
            brHolders[wantPos].br.setVariable(BR_ID, dataList.get(position));
        }

        container.addView(brHolders[wantPos].getView());
        brHolders[wantPos].onAdd(positionOri);
        return brHolders[wantPos].getView();
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public void setDataList(List<T> dataList) {
        this.dataList = dataList;
        brHolders = new CommPagerAdapter.BRHolder[Math.max(dataList.size(), 6)];
    }

    @Override
    public int getCount() {
        return getRealCount()>1?Short.MAX_VALUE:getRealCount();
    }

    public int getRealCount() {
        return dataList.size();
    }

    protected class BRHolderInfinite extends BRHolder {
        BRHolderInfinite(BR br) {
            super(br);
        }

        @Override
        boolean needResetData(int position) {
            return lastUsePosition==-1||lastUsePosition % dataList.size() != position % dataList.size();
        }
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
