package pers.lizechao.android_lib.ui.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
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
 * Time: 14:28
 */
public class RecycleViewDecoration extends RecyclerView.ItemDecoration {
    protected final int spanCount;
    private int outLeft;
    private int outRight;
    private int outTop;
    private int outBottom;
    private int innerHorizontal;
    private int innerVertical;
    protected int color = -1;
    private Paint paint = new Paint();


    public RecycleViewDecoration() {
        this.spanCount = 1;
    }

    public RecycleViewDecoration(int spanCount) {
        this.spanCount = spanCount;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //当前position
        int position = parent.getChildAdapterPosition(view);
        calcOutRect(outRect, parent.getAdapter().getItemCount(), position);

    }


    protected void calcOutRect(Rect outRect, int totalCount, int position) {
        //最大行数
        int maxLine = (int) Math.ceil(totalCount / (float) spanCount);
        //当前列
        int column = position % spanCount;
        //当前行
        int line = position / spanCount;
        //第一列
        if (column == 0) {
            outRect.left = outLeft;
        } else {
            outRect.left = innerHorizontal / 2;
        }
        //最后一列
        if (column == spanCount - 1) {
            outRect.right = outRight;
        } else
            outRect.right = innerHorizontal / 2;

        //第一行
        if (line == 0) {
            outRect.top = outTop;
        } else {
            outRect.top = innerVertical / 2;
        }

        //最后一行
        if (line == maxLine - 1) {
            outRect.bottom = outBottom;
        } else {
            outRect.bottom = innerVertical / 2;
        }
    }


    protected void drawInnerLine(Canvas canvas, View child, int adapterPosition, int totalCount) {
        //最大行数
        int maxLine = (int) Math.ceil(totalCount / (float) spanCount);
        //当前列
        int column = adapterPosition % spanCount;
        //当前行
        int line = adapterPosition / spanCount;
        //最后一列
        if (column == spanCount - 1 || adapterPosition == totalCount - 1) {
            //非最后一行
            if (line != maxLine - 1) {
                canvas.drawRect(child.getLeft()
                        , child.getBottom()
                        , child.getRight()
                        , child.getBottom() + innerVertical
                        , paint);
            }

        }
        //非最后一列
        else {
            //非最后一行
            if (line != maxLine - 1) {
                canvas.drawRect(child.getLeft()
                        , child.getBottom()
                        , child.getRight() + innerHorizontal
                        , child.getBottom() + innerVertical
                        , paint);

            }
            canvas.drawRect(child.getRight()
                    , child.getTop()
                    , child.getRight() + innerHorizontal
                    , child.getBottom()
                    , paint);
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (color == -1)
            return;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            int adapterPosition = parent.getLayoutManager().getPosition(child);
            drawInnerLine(c, child, adapterPosition, parent.getAdapter().getItemCount());
        }
    }


    public void setOutInterval(int outLeft, int outTop, int outRight, int outBottom) {
        this.outLeft = outLeft;
        this.outTop = outTop;
        this.outRight = outRight;
        this.outBottom = outBottom;
    }

    public void setInnerInterval(int innerVertical, int innerHorizontal) {
        this.innerVertical = innerVertical;
        this.innerHorizontal = innerHorizontal;
    }

    public void setVerticalData(int innerVertical, int color) {
        this.innerVertical = innerVertical;
        setColor(color);
    }

    public void setInnerInterval(int innerVertical, int innerHorizontal, int color) {
        this.innerVertical = innerVertical;
        this.innerHorizontal = innerHorizontal;
        setColor(color);
    }

    public void setVerticalInterval(int innerVertical) {
        this.innerVertical = innerVertical;
    }

    public void setVerticalInterval(int innerVertical, int color) {
        this.innerVertical = innerVertical;
        setColor(color);
    }

    public void setColor(int color) {
        this.color = color;
        paint.setAntiAlias(true);
        paint.setColor(color);
    }

    public void setInnerVertical(int innerVertical) {
        this.innerVertical = innerVertical;
    }
}
