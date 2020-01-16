package pers.lizechao.android_lib.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.HashMap;
import java.util.Map;

import pers.lizechao.android_lib.BuildConfig;
import pers.lizechao.android_lib.ProjectConfig;
import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.ui.widget.refresh.RefreshParentLinerLayoutManager;
import pers.lizechao.android_lib.ui.widget.refresh.RefreshParentNestScrollViewManager;
import pers.lizechao.android_lib.ui.widget.refresh.RefreshParentRecycleViewManager;
import pers.lizechao.android_lib.ui.widget.refresh.RefreshParentScrollViewManager;

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
 * Date: 2018-07-19
 * Time: 14:34
 * 下拉刷新和上拉加载基础类
 */
public class RefreshParent extends FrameLayout {
    //下拉阻尼
    private float pullRatio = 0.35f;
    //上推阻尼
    private float pushRatio = 0.8f;
    //View控制器
    private RefreshParentManager manager;
    //View控制器工厂
    private final RefreshParentManagerFactory factory = new RefreshParentManagerFactory();
    //Head Foot View 工厂
    private RefreshViewFactory refreshViewFactory;

    private int touchSlop;
    //是否处于正在请求状态
    private boolean isRequesting = false;
    private boolean haveRequestHeadOnce = false;

    public RefreshParent(@NonNull Context context) {
        super(context);
        init();
    }

    public RefreshParent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        initAttrs(attrs);
    }

    //滑动主体
    protected ViewGroup scrollView;
    //头部可拖拽View

    //头部是否有效
    private boolean enableHead = true;
    //尾部是否有效
    private boolean enableFoot = true;
    //是否在没有更多数据时显示尾部
    private boolean showNoMoreFoot = true;

    //是否拦截点击事件 优先级高于 enable
    private boolean disallowIntercept = false;

    private boolean footIsShow = false;

    private boolean canCallCancel = true;

    //头部
    @Nullable
    private PullView headPullView;
    //尾部
    @Nullable
    private PullView footPullView;
    @Nullable
    private RefreshMoreView footRefreshView;

    //触屏事件处理相关
    //上一次点击的点
    protected final PointF lastPoint = new PointF();
    //第一次点击的点
    protected final PointF firstPoint = new PointF();
    //活动手指id
    private int activePointerId;
    //无效的手指
    private static final int INVALID_POINTER = -1;

    private RefreshCallBack refreshCallBack;

    //没有更多数据了！不能触发下拉与底部
    private boolean noMoreData = false;
    //一直显示 footView在底部
    private boolean showFootAlways = false;
    //下拉，上拉回调
    private OnPullListener onHeadPullListener;
    private OnPullListener onFootPullListener;

    //粘性布局处理
    private StickyTagHelper stickyTagHelper;

    private boolean enableStickyTagView = false;

    private void initAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RefreshParent);
        String factoryPath = typedArray.getString(R.styleable.RefreshParent_refreshViewFactory);
        enableHead = typedArray.getBoolean(R.styleable.RefreshParent_refreshEnableHead, enableHead);
        enableFoot = typedArray.getBoolean(R.styleable.RefreshParent_refreshEnableFoot, enableFoot);
        showFootAlways = typedArray.getBoolean(R.styleable.RefreshParent_refreshFootIsAlwaysShow, showFootAlways);
        if (!TextUtils.isEmpty(factoryPath)) {
            try {
                refreshViewFactory = (RefreshViewFactory) Class.forName(factoryPath).newInstance();
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                e.printStackTrace();
                Log.i(BuildConfig.LibTAG, "创建头部失败！" + factoryPath);
            }
        }
        typedArray.recycle();
    }

    private void init() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        touchSlop = viewConfiguration.getScaledTouchSlop();
    }

    //加载完xml后 添加刷新view
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 1)
            throw new IllegalArgumentException("必须需包，且只能包含一个子类");
        scrollView = (ViewGroup) getChildAt(0);
        manager = factory.createManager(scrollView);
        if (manager == null)
            throw new IllegalArgumentException("不支持" + scrollView.getClass() + "类型");
        if (refreshViewFactory == null)
            refreshViewFactory = RefreshViewFactory.newInstance();
        initSticky();
        initRefreshView();
        initManager();
    }

    //初始化粘性头部
    private void initSticky() {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        stickyTagHelper = new StickyTagHelper(scrollView, linearLayout);
        this.addView(linearLayout);
    }

    public StickyTagHelper getStickyTagHelper() {
        return stickyTagHelper;
    }

    public void addStickyView(View headView, View stickyView) {
        enableStickyTagView = true;
        stickyTagHelper.addStickyView(headView, stickyView);
    }

    public void removeStickyHeadView(View headView) {
        stickyTagHelper.removeStickyView(headView);
    }

    private void initManager() {
        manager.addScrollListener(view -> {
            if (enableStickyTagView)
                stickyTagHelper.onScroll();
            if (showFootAlways)
                return;
            if (!enableFoot || footRefreshView == null)
                return;
            if (footIsShow)
                return;
            if (noMoreData)
                return;
            if (headPullView != null && headPullView.havePull())
                return;
            if (!manager.isReadyLoadMore())
                return;
            if (isRequesting)
                return;
            if (!haveRequestHeadOnce)
                return;
            isRequesting = true;
            footRefreshView.onRefresh(true);
            this.post(this::addRefreshFootView);
        });
    }

    /**
     * 初始化头尾部View
     */
    protected void initRefreshView() {
        //创建头部 尾部
        headPullView = refreshViewFactory.createHeadPullView(getContext(), scrollView);
        footPullView = refreshViewFactory.createFootPullView(getContext(), scrollView);
        footRefreshView = refreshViewFactory.createFootView(getContext(), scrollView);
        if (headPullView != null) {
            headPullView.refreshParent = this;
            manager.addToHead(headPullView);
        }

        if (footPullView != null) {
            footPullView.refreshParent = this;
            manager.addToFoot(footPullView);
        }

        if (footRefreshView != null) {
            footRefreshView.refreshParent = this;
            if (showFootAlways) {
                addRefreshFootView();
            }
        }
    }

    public void setRefreshViewFactory(RefreshViewFactory refreshViewFactory) {
        this.refreshViewFactory = refreshViewFactory;
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    //判断头部是否需要处理事件
    private boolean headNeedHandle() {
        return !disallowIntercept && headPullView != null && enableHead && headPullView.handlePull();
    }

    //判断尾部是否需要处理事件
    private boolean footNeedHandle() {
        return !disallowIntercept && footPullView != null && enableFoot && footPullView.handlePull();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int activeIndex = ev.getActionIndex();
        int activePointerIndex;
        if (activePointerId != INVALID_POINTER
                && (activePointerIndex = ev.findPointerIndex(activePointerId)) >= 0) {
            activeIndex = activePointerIndex;
        }
        float eventX = ev.getX(activeIndex);
        float eventY = ev.getY(activeIndex);
        boolean handle = false;
        //子类不允许拦截  头部 尾部 刷新不处理
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                canCallCancel = true;
                lastPoint.set(eventX, eventY);
                firstPoint.set(lastPoint);
                activePointerId = ev.getPointerId(activeIndex);
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(eventY - firstPoint.y) < touchSlop)
                    break;
                //是否为下拉
                boolean isPull = eventY - lastPoint.y > 0;
                //拖拽头部
                if (headNeedHandle() && (headPullView.havePull() || (manager.isOnTop() && isPull))) {
                    float dy = (eventY - lastPoint.y) * (isPull ? pullRatio : pushRatio);
                    headPullView.addPullHeight((int) dy);
                    handle = true;
                }
                //拖拽尾部
                if (footNeedHandle() && (footPullView.havePull() || (manager.isOnBottom() && !isPull))) {
                    float dy = (eventY - lastPoint.y) * (isPull ? pushRatio : pullRatio);
                    footPullView.addPullHeight((int) -dy);
                    if (!isPull)
                        manager.callScrollBy(0, (int) -dy);
                    handle = true;
                }
                if (handle && canCallCancel) {
                    ev.setAction(MotionEvent.ACTION_CANCEL);
                    for (int i = 0; i < scrollView.getChildCount(); i++) {
                        scrollView.getChildAt(i).dispatchTouchEvent(ev);
                    }
                    canCallCancel = false;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                activePointerId = INVALID_POINTER;
                if (headNeedHandle() && headPullView.havePull()) {
                    headPullView.onUp();
                    handle = true;
                }

                if (footNeedHandle() && footPullView.havePull()) {
                    handle = true;
                    footPullView.onUp();
                }

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                activePointerId = ev.getPointerId(ev.getActionIndex());
                eventX = ev.getX(ev.getActionIndex());
                eventY = ev.getY(ev.getActionIndex());
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if (activePointerId == ev.getPointerId(ev.getActionIndex())) { // 如果松开的是活动手指, 让还停留在屏幕上的最后一根手指作为活动手指
                    int actionIndex = (ev.getActionIndex() == 0 ? 1 : 0);
                    activePointerId = ev.getPointerId(actionIndex);
                    eventX = ev.getX(actionIndex);
                    eventY = ev.getY(actionIndex);
                }
                break;
        }

        lastPoint.set(eventX, eventY);
        return handle || super.dispatchTouchEvent(ev);
    }

    public boolean isRequesting() {
        return isRequesting;
    }

    private void callbackLoadingMore() {
        if (refreshCallBack != null)
            refreshCallBack.onLoadingMore();
    }

    private void callbackRefresh() {
        if (refreshCallBack != null)
            refreshCallBack.onRefresh();
    }

    private void addRefreshFootView() {
        if (footRefreshView != null) {
            if (footIsShow)
                return;
            footIsShow = true;
            manager.addToFoot(footRefreshView);

        }
    }

    private void removeRefreshFootView() {
        if (footRefreshView != null) {
            if (!footIsShow)
                return;
            manager.removeFromFoot(footRefreshView);
            footIsShow = false;
        }
    }

    /**
     * 加载更多完成时调用
     *
     * @param succeed 是否成功
     */
    public void loadingMoreFinish(boolean succeed, boolean finish) {
        isRequesting = false;
        if (footPullView != null) {
            if (succeed) {
                footPullView.onSucceed();
            } else {
                footPullView.onFail();
            }
        }
        if (footRefreshView != null) {
            if (succeed) {
                if (finish) {
                    setNoMoreData(true);
                } else {
                    if (!showFootAlways) {
                        footRefreshView.onRemove();
                        removeRefreshFootView();
                    } else
                        footRefreshView.onSucceed();
                }
            } else {
                footRefreshView.onFail();
            }

        }

    }

    public void setNoMoreData(boolean noMoreData) {
        if (noMoreData == this.noMoreData)
            return;
        this.noMoreData = noMoreData;
        if (footRefreshView != null) {
            if (noMoreData && showNoMoreFoot) {
                footRefreshView.onFinish(true);
                addRefreshFootView();
            } else {
                footRefreshView.onFinish(false);
                removeRefreshFootView();
            }

        }
    }

    /**
     * 刷新完成时调用
     *
     * @param succeed 是否成功
     */
    public void refreshFinish(boolean succeed) {
        isRequesting = false;
        haveRequestHeadOnce = true;
        if (headPullView != null) {
            if (succeed) {
                headPullView.onSucceed();
            } else {
                headPullView.onFail();
            }
        }

    }

    public void setRefreshCallBack(RefreshCallBack refreshCallBack) {
        this.refreshCallBack = refreshCallBack;
    }

    public void animToRefresh() {
        isRequesting = true;
        if (headPullView != null) {
            headPullView.onRefresh(true);
        }
    }

    public void toRefresh() {
        isRequesting = true;
        if (headPullView != null) {
            headPullView.onRefresh(false);
        }
    }

    //内部类与接口分界线
    //--------------------------------------------------------------------------------------------------------------------------------

    public interface RefreshCallBack {
        void onRefresh();

        void onLoadingMore();
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
     * Date: 2018-07-21
     * Time: 11:02
     * 更多View
     * 刷新view基础
     */
    abstract static class RefreshViewBase extends FrameLayout {
        RefreshParent refreshParent;
        final View child;

        RefreshViewBase(@NonNull Context context) {
            super(context);
            this.child = onCreateView(context, this);
            this.addView(child);
        }

        //返回真实View
        protected abstract View onCreateView(Context context, ViewGroup viewGroup);

        //调整View到刷新状态
        protected abstract void onRefresh(boolean anim);

        //调整View到刷新状态
        protected abstract void onSucceed();

        //调整View到刷新状态
        protected abstract void onFail();

        protected abstract void callGetData();

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
     * Date: 2018-07-21
     * Time: 11:02
     * 更多View
     * 尾部显示，提醒正在更新
     */
    public static abstract class RefreshMoreView extends RefreshViewBase {
        protected RefreshMoreView(@NonNull Context context) {
            super(context);
        }

        protected abstract void onRemove();

        protected abstract void onFinish(boolean finish);

        @Override
        protected void callGetData() {
            refreshParent.callbackLoadingMore();
        }
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
     * Date: 2018-07-21
     * Time: 11:02
     * 包裹View 用于隐藏头尾部View
     * 处理头部拉动效果
     */
    public abstract static class PullView extends RefreshViewBase {
        //下拉高度
        private int pullHeight = 0;
        private int childHeight = -1;
        private final boolean isTop;
        @Nullable
        private OnPullListener onPullListener;

        protected PullView(@NonNull Context context, boolean isTop) {
            super(context);
            this.isTop = isTop;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            child.measure(widthMeasureSpec, heightMeasureSpec);
            childHeight = child.getMeasuredHeight();
            LayoutParams frameLayout = (LayoutParams) child.getLayoutParams();
            if (isTop)
                frameLayout.topMargin = -childHeight + pullHeight;
            else
                frameLayout.bottomMargin = -childHeight + pullHeight;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        protected abstract void onUp();

        public int getChildHeight() {
            return childHeight;
        }

        protected boolean handlePull() {
            return true;
        }

        //是否为拖动状态
        boolean havePull() {
            return pullHeight != 0;
        }

        /**
         * @param pullHeightAdd 增量
         */
        public void addPullHeight(int pullHeightAdd) {
            setPullHeight(this.pullHeight + pullHeightAdd);
        }

        /**
         * @param hideHeight 直接赋值
         */
        public void setPullHeight(int hideHeight) {
            this.pullHeight = hideHeight;
            if (pullHeight < 0)
                pullHeight = 0;
            child.requestLayout();
            if (onPullListener != null) {
                onPullListener.onPull(pullHeight);
            }
        }

        public int getPullHeight() {
            return pullHeight;
        }

        @Override
        protected void callGetData() {
            if (isTop)
                refreshParent.callbackRefresh();
            else
                refreshParent.callbackLoadingMore();
        }

        public void setOnPullListener(@Nullable OnPullListener onPullListener) {
            this.onPullListener = onPullListener;
        }
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
     * Date: 2018-07-21
     * Time: 11:02
     * view控制器基础类
     */
    public static abstract class RefreshParentManager<T extends ViewGroup> {
        protected T contentView;

        protected void setContentView(T contentView) {
            this.contentView = contentView;
        }

        public T getContentView() {
            return contentView;
        }

        protected abstract void addToHead(@NonNull View view);

        protected abstract void addToFoot(@NonNull View view);

        protected abstract void removeFromFoot(@NonNull View view);

        //是否为顶部
        protected abstract boolean isOnTop();

        //是否为底部
        protected abstract boolean isOnBottom();

        //可以加载更多
        protected abstract boolean isReadyLoadMore();

        protected abstract void addScrollListener(OnScrollListener<T> scrollListener);

        public abstract void callScrollBy(int x, int y);

        public interface OnScrollListener<T extends ViewGroup> {
            void onScroll(T view);
        }
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
     * Date: 2018-07-21
     * Time: 11:04
     * View控制器工厂方法
     * 用于根据View类型获取View控制器
     */
    private static class RefreshParentManagerFactory {
        private static final Map<Class<? extends ViewGroup>, Class<? extends RefreshParentManager<? extends ViewGroup>>> managers = new HashMap<>();

        <T extends ViewGroup> RefreshParentManager<T> createManager(T contentView) {
            try {
                Class<? extends RefreshParentManager<T>> managerClass = null;
                Class current = contentView.getClass();
                while (current != null && managerClass == null) {
                    managerClass = (Class<? extends RefreshParentManager<T>>) managers.get(current);
                    current = current.getSuperclass();
                }
                if (managerClass == null)
                    return null;
                RefreshParentManager<T> manager = managerClass.newInstance();
                manager.setContentView(contentView);
                return manager;
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

        static {
            registerManager(HeadFootRecycleView.class, RefreshParentRecycleViewManager.class);
            registerManager(ScrollView.class, RefreshParentScrollViewManager.class);
            registerManager(NestedScrollView.class, RefreshParentNestScrollViewManager.class);
            registerManager(LinearLayout.class, RefreshParentLinerLayoutManager.class);
        }
    }

    public static <T extends ViewGroup> void registerManager(Class<T> viewClass, Class<? extends RefreshParentManager<T>> managerClass) {
        RefreshParentManagerFactory.managers.put(viewClass, managerClass);
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
     * Date: 2018-07-21
     * Time: 11:37
     * 刷新View工厂方法
     */
    public static abstract class RefreshViewFactory {
        protected abstract PullView createHeadPullView(Context context, ViewGroup viewGroup);

        protected abstract PullView createFootPullView(Context context, ViewGroup viewGroup);

        protected abstract RefreshMoreView createFootView(Context context, ViewGroup viewGroup);

        public static RefreshViewFactory newInstance() {
            try {
                return ProjectConfig.getInstance().getRefreshViewFactory().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    public interface OnPullListener {
        void onPull(int height);
    }

    public void setPullRatio(float pullRatio) {
        this.pullRatio = pullRatio;
    }

    public void setPushRatio(float pushRatio) {
        this.pushRatio = pushRatio;
    }

    public void setEnableHead(boolean enableHead) {
        this.enableHead = enableHead;
    }

    public void setEnableFoot(boolean enableFoot) {
        this.enableFoot = enableFoot;
    }

    public boolean isEnableHead() {
        return enableHead;
    }

    public boolean isEnableFoot() {
        return enableFoot;
    }

    public void setDisallowIntercept(boolean disallowIntercept) {
        this.disallowIntercept = disallowIntercept;
    }

    public void setOnFootPullListener(OnPullListener onFootPullListener) {
        this.onFootPullListener = onFootPullListener;
        if (footPullView != null)
            footPullView.setOnPullListener(onFootPullListener);
    }

    public void setOnHeadPullListener(OnPullListener onHeadPullListener) {
        this.onHeadPullListener = onHeadPullListener;
        if (headPullView != null)
            headPullView.setOnPullListener(onHeadPullListener);
    }

    public OnPullListener getOnHeadPullListener() {
        return onHeadPullListener;
    }

    public void setShowNoMoreFoot(boolean showNoMoreFoot) {
        this.showNoMoreFoot = showNoMoreFoot;
    }
}
