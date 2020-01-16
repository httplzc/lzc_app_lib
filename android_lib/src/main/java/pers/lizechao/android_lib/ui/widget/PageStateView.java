package pers.lizechao.android_lib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.HashMap;
import java.util.Map;

import pers.lizechao.android_lib.ProjectConfig;
import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.function.Notification;

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
 * Date: 2018-08-04
 * Time: 13:52
 * 用于显示视图状态
 */
public class PageStateView extends FrameLayout {
    private StateViewFactory stateViewFactory;
    private final Map<State, View> viewMap = new HashMap<>();
    private State state = State.Normal;
    //需要掩盖的View
    private View contentView;
    private Notification refreshNotify;
    private Rect coverRect;

    private final int[] location = new int[2];
    private final int[] location2 = new int[2];

    //状态枚举  正在加载中  普通  内容错误 ，内容为空
    public enum State {
        Loading, Normal, Error, Null
    }

    public PageStateView(@NonNull Context context) {
        super(context);
        initView();
    }

    public PageStateView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        initAttr(attrs);
    }

    private void initAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PageStateView);
        String factoryClassStr=typedArray.getString(R.styleable.PageStateView_state_view_factory);
        typedArray.recycle();
        if(TextUtils.isEmpty(factoryClassStr))
            return;
        try {
            setStateViewFactory((StateViewFactory) Class.forName(factoryClassStr).newInstance());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

    }

    private void initView() {
    }

    public void setState(State state) {
        if (this.state == state)
            return;
        if (this.state != State.Normal) {
            this.removeView(viewMap.get(this.state));
        }
        if (state != State.Normal)
            this.addView(viewMap.get(state));
        this.state = state;
        requestLayout();
    }

    public View getStateView(State state) {
        if (state == State.Normal)
            return contentView;
        return viewMap.get(state);
    }

    public void setCoverRect(Rect coverRect) {
        this.coverRect = coverRect;
        requestLayout();
    }

    public Rect getCoverRect() {
        return coverRect;
    }

    public void setContentView(View contentView) {
        this.contentView = contentView;
        coverRect = null;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return state == State.Loading;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (state != State.Normal) {
            if (coverRect != null) {
                View current = viewMap.get(state);
                current.layout(coverRect.left, coverRect.top, coverRect.right, coverRect.bottom);
            } else if (contentView != null) {
                View current = viewMap.get(state);
                contentView.getLocationInWindow(location2);
                this.getLocationInWindow(location);
                int dx = location2[0] - location[0];
                int dy = location2[1] - location[1];
                current.layout(dx, dy, dx + contentView.getWidth(), dy + contentView.getHeight());
            }
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (stateViewFactory == null)
            stateViewFactory = StateViewFactory.newInstance();
        viewMap.clear();
        if (contentView == null)
            contentView = getChildAt(0);
        viewMap.put(State.Loading, stateViewFactory.createLoadingView(getContext(), this));
        viewMap.put(State.Null, stateViewFactory.createNullView(getContext(), this));
        viewMap.put(State.Error, stateViewFactory.createErrorView(getContext(), this));
        viewMap.get(State.Error).setOnClickListener(v -> {
            if (refreshNotify != null)
                refreshNotify.notifying();
        });
        viewMap.get(State.Null).setOnClickListener(v -> {
            if (refreshNotify != null)
                refreshNotify.notifying();
        });
    }

    public void setStateViewFactory(StateViewFactory stateViewFactory) {
        this.stateViewFactory = stateViewFactory;
    }

    public State getState() {
        return state;
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
     * Date: 2018-08-04
     * Time: 13:52
     * 视图状态View的工厂
     */
    public static abstract class StateViewFactory {
        public abstract View createLoadingView(Context context, ViewGroup viewGroup);

        public abstract View createNullView(Context context, ViewGroup viewGroup);

        public abstract View createErrorView(Context context, ViewGroup viewGroup);

        public static StateViewFactory newInstance() {
            try {
                return ProjectConfig.getInstance().getStateViewFactory().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void setRefreshNotify(Notification refreshNotify) {
        this.refreshNotify = refreshNotify;
    }
}
