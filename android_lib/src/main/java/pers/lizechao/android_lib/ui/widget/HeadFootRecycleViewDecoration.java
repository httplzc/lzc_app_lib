package pers.lizechao.android_lib.ui.widget;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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
 * Time: 14:42
 */
public class HeadFootRecycleViewDecoration extends RecycleViewDecoration {
    private int headOutLeft;
    private int headOutRight;
    private int headOutTop;
    private int headOutBottom;
    private final HeadFootRecycleView recycleView;

    public HeadFootRecycleViewDecoration(HeadFootRecycleView recycleView) {
        super();
        this.recycleView = recycleView;
    }



    public HeadFootRecycleViewDecoration(HeadFootRecycleView recycleView, int spanCount) {
        super(spanCount);
        this.recycleView = recycleView;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (recycleView.realAdapter == null)
            return;
        if (recycleView.realAdapter.isHeadOrFoot(parent.getChildAdapterPosition(view))) {
            outRect.set(headOutLeft, headOutTop, headOutRight, headOutBottom);
        } else {
            //最大position
            int maxCount = recycleView.getContentCount();
            //当前position
            int position = parent.getChildAdapterPosition(view) - recycleView.getHeadCount();
            calcOutRect(outRect, maxCount, position);
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (color == -1)
            return;
        if (recycleView.realAdapter == null)
            return;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            int adapterPosition = parent.getLayoutManager().getPosition(child);
            if (recycleView.realAdapter.isHeadOrFoot(adapterPosition)) {
                continue;
            }
            //最大position
            int maxCount = recycleView.getContentCount() - recycleView.getHeadCount() - recycleView.getFootCount();
            drawInnerLine(c, child, adapterPosition-recycleView.getHeadCount(), maxCount);
        }
    }

    public void setHeadOutInterval(int outLeft, int outTop, int outRight, int outBottom) {
        this.headOutLeft = outLeft;
        this.headOutTop = outTop;
        this.headOutRight = outRight;
        this.headOutBottom = outBottom;
    }


}
