package pers.lizechao.android_lib.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import pers.lizechao.android_lib.utils.JavaUtils;

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
 * Date: 2018-08-14
 * Time: 18:08
 */
public class LabelSelectorView extends LabelView {
    //是否允许多选
    private boolean isMultiSelect = false;
    //当前选择状态
    private List<Boolean> choiceState;
    //是否可以取消选择全部
    private boolean canCancelAll = false;
    private LabelSelectorAdapter adapter;
    private LabelViewAdapter.AdapterDataObserver observer;
    private OnChoiceListener onChoiceListener;
    private OnCancelChoiceAllListener onCancelChoiceAllListener;
    private int firstSelectPosition = 0;

    public LabelSelectorView(@NonNull Context context) {
        super(context);
        init();
    }

    public LabelSelectorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    @Deprecated
    public void setOnItemClickListener(OnImgClickListener onImgClickListener) {

    }

    private void init() {
        super.setOnItemClickListener((view, position) -> {
            if (haveChoice(position))
                cancelChoice(position);
            else
                choice(position);
        });
        observer = new LabelViewAdapter.AdapterDataObserver() {
            @Override
            public void onChanged() {
                choiceState = JavaUtils.newList(adapter.getCount(), false);
                if (!canCancelAll && adapter.getCount() != 0 && getChoiceIndex() == -1)
                    AndroidSchedulers.mainThread().scheduleDirect(() -> {
                        changeChoiceState(firstSelectPosition, true);
                    });
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {

            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                choiceState.addAll(positionStart - 1, JavaUtils.newList(itemCount, false));
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                for (int i = 0; i < itemCount; i++) {
                    choiceState.remove(positionStart);
                }
                if (!canCancelAll && getChoiceIndex() == -1 && choiceState.size() == 0)
                    changeChoiceState(0, true);
            }
        };
    }

    public void setSelectorAdapter(LabelSelectorAdapter adapter) {
        if (adapter == null)
            return;
        super.setAdapter(adapter);
        if (this.adapter != null)
            this.adapter.unregisterAdapterDataObserver(observer);
        this.adapter = adapter;
        adapter.registerAdapterDataObserver(observer);
    }

    @Override
    public LabelSelectorAdapter getAdapter() {
        return adapter;
    }

    @Override
    @Deprecated
    public void setAdapter(LabelViewAdapter adapter) {
        super.setAdapter(adapter);
    }

    public boolean haveChoice(int position) {
        return choiceState.get(position);
    }


    public int getChoiceIndex() {
        for (int i = 0; i < choiceState.size(); i++) {
            if (choiceState.get(i))
                return i;
        }
        return -1;
    }

    public void getChoiceIndex(List<Integer> choice) {
        for (int i = 0; i < choiceState.size(); i++) {
            if (choiceState.get(i))
                choice.add(i);
        }
    }

    public void choice(int position) {
        //多选
        if (isMultiSelect)
            changeChoiceState(position, true);
        else {
            //单选先取消单选
            int index = getChoiceIndex();
            if (index == position)
                return;
            if (index != -1)
                changeChoiceState(index, false);
            if (position != -1)
                changeChoiceState(position, true);
        }
    }

    public void cancelChoice(int position) {
        //单选不允许取消
        List<Integer> choiceList = new ArrayList<>();
        getChoiceIndex(choiceList);
        //只剩一个不允许取消
        if (!canCancelAll && choiceList.size() == 1 && choiceList.get(0) == position)
            return;
        changeChoiceState(position, false);
        if (choiceList.size() == 1) {
            onCancelChoiceAllListener.onCancelChoiceAll();
        }
    }

    private void changeChoiceState(int position, boolean choice) {
        choiceState.set(position, choice);
        if (adapter != null)
            adapter.onChoiceStateChange(getChildAt(position), position, choice);
        if (onChoiceListener != null && choice)
            onChoiceListener.onChoice(position);
    }

    public void setFirstSelectPosition(int firstSelectPosition) {
        this.firstSelectPosition = firstSelectPosition;
    }

    public void setMultiSelect(boolean multiSelect) {
        isMultiSelect = multiSelect;
    }

    public void setCanCancelAll(boolean canCancelAll) {
        this.canCancelAll = canCancelAll;
    }

    public boolean isCanCancelAll() {
        return canCancelAll;
    }

    public boolean isMultiSelect() {
        return isMultiSelect;
    }

    public interface OnChoiceListener {
        void onChoice(int position);
    }

    public interface OnCancelChoiceAllListener {
        void onCancelChoiceAll();
    }


    public void setOnChoiceListener(OnChoiceListener onChoiceListener) {
        this.onChoiceListener = onChoiceListener;
    }

    public void setOnCancelChoiceAllListener(OnCancelChoiceAllListener onCancelChoiceAllListener) {
        this.onCancelChoiceAllListener = onCancelChoiceAllListener;
    }
}
