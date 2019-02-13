package pers.lizechao.android_lib.ui.widget;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Lzc on 2017/7/28 0028.
 */

public class StickyTagHelper {
    private HashMap<View, View> stickyViews = new HashMap<>();
    private List<View> cloneViewList = new ArrayList<>();
    public boolean isGroupTagReplace = true;
    private LinearLayout groupLinearLayout;
    public View scrollView;
    private int offset=0;

    public StickyTagHelper(View scrollView, LinearLayout groupLinearLayout) {
        this.scrollView = scrollView;
        this.groupLinearLayout = groupLinearLayout;
    }

    public void addStickyView(View origin, View clone) {
        stickyViews.put(origin, clone);
        cloneViewList.add(clone);
    }

    public void removeStickyView(View view) {
        View cloneView = stickyViews.remove(view);
        cloneViewList.remove(cloneView);
    }


    public void onScroll() {
        for (Map.Entry<View, View> viewViewEntry : stickyViews.entrySet()) {
            dealTag(viewViewEntry.getKey(), viewViewEntry.getValue());
        }
    }

    //标签替换
    private void dealTagByReplace(View groupView, View cloneView) {
        //到达触发区域
        if (getChildViewTopDistance(groupView) < groupLinearLayout.getHeight() - ((groupLinearLayout != cloneView.getParent()) ? 0 : cloneView.getHeight())) {
            //增加
            if (cloneViewList.indexOf(cloneView) == 0) {
                if (cloneView.getParent() != groupLinearLayout && groupLinearLayout.getChildCount() == 0) {
                    groupLinearLayout.addView(cloneView);
                    // Log.i(BuildConfig.LibTAG, " addView_normal;");
                    groupLinearLayout.setTranslationY(0);
                    groupLinearLayout.invalidate();

                } else {
                    groupLinearLayout.setTranslationY(0);
                }
            } else {
                //两个标签替换动画
                if (cloneView.getParent() != groupLinearLayout) {
                    float trans = -(groupLinearLayout.getHeight() - getChildViewTopDistance(groupView));
                    if (-trans >= groupLinearLayout.getHeight()) {
                        groupLinearLayout.removeAllViews();
                        groupLinearLayout.addView(cloneView);
                        groupLinearLayout.setTranslationY(0);
                    } else {
                        groupLinearLayout.setTranslationY(trans);
                    }
                    groupLinearLayout.invalidate();
                } else {
                    groupLinearLayout.setTranslationY(0);
                    groupLinearLayout.invalidate();
                }

            }
        } else {
            //返回时重现之前的标签
            if (groupLinearLayout == cloneView.getParent()) {
                groupLinearLayout.removeView(cloneView);
                if ((int) cloneViewList.indexOf(cloneView) != 0) {
                    View last = getGroupViewByPosition((int) cloneViewList.indexOf(cloneView) - 1);
                    if (last != null) {
                        groupLinearLayout.addView(last);
                        groupLinearLayout.setTranslationY(-last.getHeight());
                    } else {
                        groupLinearLayout.setTranslationY(0);
                    }
                }
                groupLinearLayout.invalidate();
            } else {
                //高度修正
                if (groupLinearLayout.getChildCount() == 1) {
                    View next = getGroupKeyByTagPosition((int) cloneViewList.indexOf(groupLinearLayout.getChildAt(0)) + 1);
                    if (next == null || -(groupLinearLayout.getHeight() - getChildViewTopDistance(next)) > groupLinearLayout.getHeight()) {
                        if (groupLinearLayout.getTranslationY() != 0) {
                            groupLinearLayout.setTranslationY(0);
                            groupLinearLayout.invalidate();
                        }
                    }
                }
            }
        }
    }


    //标签叠加
    private void dealTagByAdd(View groupView, View cloneView) {
        if (getChildViewTopDistance(groupView) < groupLinearLayout.getHeight() - ((groupLinearLayout != cloneView.getParent()) ? 0 : cloneView.getHeight())) {
            if (groupLinearLayout != cloneView.getParent()) {
                groupLinearLayout.addView(cloneView);
                groupLinearLayout.invalidate();
            }
        } else {
            if (groupLinearLayout == cloneView.getParent()) {
                groupLinearLayout.removeView(cloneView);
                groupLinearLayout.invalidate();
            }
        }
        if (groupLinearLayout.getChildCount() != 0) {
            View view = groupLinearLayout.getChildAt(groupLinearLayout.getChildCount() - 1);
            int tag = (int) cloneViewList.indexOf(view);
            if (tag != groupLinearLayout.getChildCount() - 1) {
                groupLinearLayout.removeAllViews();
                int count = 0;
                for (Map.Entry<View, View> viewViewEntry : stickyViews.entrySet()) {
                    if (count > tag)
                        break;
                    groupLinearLayout.addView(viewViewEntry.getValue());
                    count++;
                }
            }

        }
    }


    //分类型处理
    public void dealTag(View groupView, View cloneView) {
        if (isGroupTagReplace) {
            dealTagByReplace(groupView, cloneView);
        } else {
            dealTagByAdd(groupView, cloneView);
        }
    }

    public void setGroupTagReplace(boolean groupTagReplace) {
        isGroupTagReplace = groupTagReplace;
    }

    private float getCurrentTrans(LinearLayout groupLinearLayout) {
        float trans = 0;
        if (groupLinearLayout.getChildCount() == 1)
            return 0;
        else {
            for (int i = 0; i < groupLinearLayout.getChildCount() - 1; i++) {
                trans += groupLinearLayout.getChildAt(i).getHeight();
            }
        }
        return trans;
    }


    //距离顶部距离
    private float getChildViewTopDistance(View view) {
        return view.getY() - scrollView.getScrollY()-offset;
    }

    private View getGroupViewByPosition(int position) {
        if (position < cloneViewList.size() && position >= 0)
            return cloneViewList.get(position);
        else
            return null;
    }


    private View getGroupKeyByTagPosition(int position) {
        View cloneView = getGroupViewByPosition(position);
        if (cloneView == null)
            return null;
        else
            for (Map.Entry<View, View> viewViewEntry : stickyViews.entrySet()) {
                if (viewViewEntry.getValue() == cloneView)
                    return viewViewEntry.getKey();
            }
        return null;
    }

    public void setOffset(int offset) {
        this.offset = offset;
        FrameLayout.LayoutParams lp= (FrameLayout.LayoutParams) groupLinearLayout.getLayoutParams();
        lp.topMargin=offset;
        groupLinearLayout.setLayoutParams(lp);
    }
}
