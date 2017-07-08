package com.yioks.lzclib.View;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.yioks.lzclib.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/8/4 0004.
 */
public class ParentView extends FrameLayout {
    //上下文
    protected Context context;
    //滑动工具
    protected Scroller scroller;
    //上一次点击的X坐标
    protected float lastX;
    //上一次点击的Y坐标
    protected float lastY;
    //第一次电机的X坐标
    protected float firstX;
    //第一次电机的Y坐标
    protected float firstY;
    //是否可以滑动
    protected boolean canScroll = true;
    //正在加载中视图
    protected View loadView;
    //网络错误视图
    protected View netFailLayout;
    //内容为空视图
    protected View nullContentView;
    //主内容
    protected View contentView;
    //是否拦截事件
    protected boolean InterceptTouch = false;
    //刷新回调类
    protected ReFreshDataListener reFreshDataListener;
    //当前状态
    protected Staus staus;
    //内容全部是按钮
    protected boolean is_all_button = false;

    private int centerViewOffY = 0;


    //状态枚举  正在加载中  普通  内容错误 ，内容为空
    public enum Staus {
        Loading, Normal, Error, Null
    }

    //是否为测试模式
    private boolean isDebug;
    private Handler handler = new Handler();
    //是否可以刷新
    private boolean canRefrsh = true;
    //错误时显示的信息
    private String error_text = "sorry~内容出错了~";
    //加载中显示的信息
    private List<String> loadding_list = new ArrayList<>();
    //没有数据时显示的文字
    private String null_text = getResources().getString(R.string.no_data);
    //正在加载中的进度条
    private ProgressTextView loadding_progress;
    //是否加载中
    private boolean is_show_progress = false;
    //加载中的文字控件
    private TextView loadding_textView;

    private TextView null_textView;

    private TextView error_textView;


    private TextView netFailRefreshTextView;
    private TextView nullRefreshTextView;

    private String netFailRefreshText = "下拉刷新";
    private String nullRefreshText = "下拉刷新";

    public Animation animation;
    public ToNormal toNormal;

    private View net_fail_real;
    private View null_content_real;
    private View loading_real;


    public ParentView(Context context) {
        super(context);
        initView(context);
    }

    public ParentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        initData(attrs);
    }

    public ParentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
        initData(attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 初始化xml里的属性
     *
     * @param attrs
     */
    protected void initData(AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ParentView);
        canScroll = typedArray.getBoolean(R.styleable.ParentView_canScroll, true);
        InterceptTouch = typedArray.getBoolean(R.styleable.ParentView_InterceptTouch, false);
        is_all_button = typedArray.getBoolean(R.styleable.ParentView_content_not_scroll, false);
        isDebug = typedArray.getBoolean(R.styleable.ParentView_is_debug, false);
        is_show_progress = typedArray.getBoolean(R.styleable.ParentView_show_progress, false);
        typedArray.recycle();
        loadding_list.add("正在加载中……");
        loadding_list.add("耐心等待呦……");
    }

    /**
     * 初始化滑动控件与三大状态视图
     *
     * @param context
     */
    protected void initView(Context context) {
        scroller = new Scroller(context, new OvershootInterpolator());
        this.context = context;
        initLoadView();
    }

    /**
     * 初始化三大状态视图
     */
    protected void initLoadView() {
        loadView = LayoutInflater.from(context).inflate(R.layout.load_layout, this, false);
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            netFailLayout = LayoutInflater.from(context).inflate(R.layout.netfail_layout_heng, this, false);

        } else {
            netFailLayout = LayoutInflater.from(context).inflate(R.layout.netfail_layout, this, false);

        }
        nullContentView = LayoutInflater.from(context).inflate(R.layout._null_content_layout, this, false);
    }

    /**
     * 在加载完xml后对三大状态组件进行组合
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(getChildCount() - 1);
        this.addView(netFailLayout);
        this.addView(nullContentView);
        this.addView(loadView);

        net_fail_real = netFailLayout.findViewById(R.id.net_fail_real);
        null_content_real = nullContentView.findViewById(R.id.null_content_real);
        loading_real = loadView.findViewById(R.id.loading_real);
        setOffY(centerViewOffY);

        error_textView = (TextView) netFailLayout.findViewById(R.id.net_faile);
        error_textView.setText(error_text);
        null_textView = (TextView) nullContentView.findViewById(R.id.null_content);
        null_textView.setText(null_text);
        nullRefreshTextView = (TextView) nullContentView.findViewById(R.id.null_refresh_text);
        nullRefreshTextView.setText(nullRefreshText);

        netFailRefreshTextView = (TextView) netFailLayout.findViewById(R.id.net_faile_refresh_text);
        nullRefreshTextView.setText(netFailRefreshText);


        loadding_textView = (TextView) findViewById(R.id.loadding_text);
        loadding_textView.setText(loadding_list.get(0));
        loadding_progress = (ProgressTextView) loadView.findViewById(R.id.progress_text);
       // LoaddingBollView loadding_boll = (LoaddingBollView) loadView.findViewById(R.id.loadding_boll);
        if (!is_show_progress) {
            loadding_progress.setVisibility(GONE);
        } else {
            //  loadding_boll.setVisibility(GONE);
            loadding_progress.setIsShowSin(false);
        }
        if (isDebug) {
            setstaus(Staus.Normal, 1);
        } else {
            setstaus(Staus.Loading);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        if (staus != Staus.Normal) {
            return true;
        }
        //是否都为按钮（没有滑动组件）
        if (is_all_button) {
            float eventX = ev.getX();
            float eventY = ev.getY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = eventX;
                    lastY = eventY;
                    firstX = eventX;
                    firstY = eventY;
                    return false;
                case MotionEvent.ACTION_MOVE:

                    //区分滑动与点击事件
                    int dy = Math.abs((int) (firstY - ev.getY()));
                    if (ViewConfiguration.get(getContext()).getScaledPagingTouchSlop() < dy) {
                        lastX = eventX;
                        lastY = eventY;
                        return true;
                    } else {
                        return false;
                    }

                case MotionEvent.ACTION_UP:
                    return false;
            }
            return false;
        } else {
            if (InterceptTouch) {
                return true;
            } else {
                return false;
            }
        }

    }

    public void move(int dy) {
        scrollBy(0, (int) (-dy));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //判断是否可滑动
        if (!canScroll) {
            return false;
        }
        if (staus == Staus.Normal||staus==Staus.Loading)
            return false;
        float eventX = event.getX();
        float eventY = event.getY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if (scroller.computeScrollOffset()) {
                    return false;
                } else {
                    scroller.abortAnimation();
                }
                lastX = eventX;
                lastY = eventY;
                firstX = eventX;
                firstY = eventY;
                return true;

            //视图随手指滑动
            case MotionEvent.ACTION_MOVE:
                //  Log.i("lzc","move");
                if (lastY == firstY) {
                    lastX = eventX;
                    lastY = eventY;
                    return true;
                }
                float dy = eventY - lastY;
                move((int) (dy * 0.45f));
                lastX = eventX;
                lastY = eventY;
                return true;
            //手指松开后回弹
            case MotionEvent.ACTION_UP:
                scroller.startScroll(this.getScrollX(), this.getScrollY(), 0, -this.getScrollY(), 400);
                invalidate();
                //  Log.i("lzc","up");
                if (Math.abs(this.getScrollY()) > 50) {
                    if (staus == Staus.Null || staus == Staus.Error) {
                        if (reFreshDataListener != null) {
                            if (canRefrsh) {
                                loadding_progress.reset();
                                reFreshDataListener.refreshData();
                                canRefrsh = false;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        canRefrsh = true;
                                    }
                                }, 1000);
                            }
                        }
                    }

                }
                performClick();
                return true;

        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            this.scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        } else {
//            if(istanxing)
//            {
//                istanxing=false;
//                tanxingDistance=0;
//                scroller.abortAnimation();
//            }
//            else
//            {
//                istanxing=true;
//                scroller.startScroll(this.getScrollX(),this.getScrollY(),0,tanxingDistance,80);
//                tanxingDistance=0;
//                invalidate();
//            }
            scroller.abortAnimation();
        }

    }

    protected void hideLoadlayout() {
        loadView.setVisibility(GONE);
    }

    protected void hildNullView() {
        nullContentView.setVisibility(GONE);
    }

    protected void hideNetFaillayout() {
        netFailLayout.setVisibility(INVISIBLE);
    }

    protected void showLoadlayout() {
        loadView.setVisibility(VISIBLE);
    }

    protected void showNetFaillayout() {
        netFailLayout.setVisibility(VISIBLE);
    }

    public boolean isCanScroll() {
        return canScroll;
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    protected void hideallViewCurrent() {
        loadView.setVisibility(GONE);
        netFailLayout.setVisibility(GONE);
        nullContentView.setVisibility(GONE);
    }

    //隐藏所有视图（淡出动画）
    protected void hideAllView() {
        loadView.clearAnimation();
        netFailLayout.clearAnimation();
        nullContentView.clearAnimation();
        animation = AnimationUtils.loadAnimation(context, R.anim.fade);
        if (loadView.getVisibility() == VISIBLE) {
            loadView.setAnimation(animation);
        }
        if (netFailLayout.getVisibility() == VISIBLE) {
            netFailLayout.setAnimation(animation);
        }

        if (nullContentView.getVisibility() == VISIBLE) {
            nullContentView.setAnimation(animation);
        }
        toNormal = new ToNormal();
        handler.postDelayed(toNormal, 500);
        animation.start();
    }


    public class ToNormal implements Runnable {

        @Override
        public void run() {
            loadView.setVisibility(GONE);
            netFailLayout.setVisibility(GONE);
            nullContentView.setVisibility(GONE);
        }
    }

    protected void ShowContentData() {
        contentView.setVisibility(VISIBLE);
    }

    protected void ShowNullView() {
        nullContentView.setVisibility(VISIBLE);
    }

    protected void HideContentData() {
        contentView.setVisibility(INVISIBLE);
    }

    public View getLoadView() {
        return loadView;
    }

    public void setLoadView(View loadView) {
        this.loadView = loadView;
    }

    public View getNetFailLayout() {
        return netFailLayout;
    }

    public void setNetFailLayout(View netFailLayout) {
        this.netFailLayout = netFailLayout;
    }

    public boolean isInterceptTouch() {
        return InterceptTouch;
    }

    public void setInterceptTouch(boolean interceptTouch) {
        InterceptTouch = interceptTouch;
    }


    /***
     * 设置当前状态
     *
     * @param staus
     * @param flag
     */
    public void setstaus(Staus staus, int... flag) {
        this.staus = staus;
        if (staus == Staus.Normal) {
            if (flag.length > 0) {
                hideallViewCurrent();
            } else {
                hideAllView();
            }
            contentView.setVisibility(VISIBLE);
            InterceptTouch = false;
            handler.removeCallbacks(runnable);
        } else if (staus == Staus.Error) {
            cancelAllAnim();
            hideLoadlayout();
            hildNullView();
            showNetFaillayout();
            InterceptTouch = true;
            handler.removeCallbacks(runnable);

        } else if (staus == Staus.Loading) {
            DissMissRun();
            hideNetFaillayout();
            hildNullView();
            showLoadlayout();
            InterceptTouch = true;
            handler.postDelayed(runnable, 2000);
        } else if (staus == Staus.Null) {
            hideNetFaillayout();
            hideLoadlayout();
            ShowNullView();
            InterceptTouch = true;
        }
    }

    public void cancelAllAnim() {
        loadView.clearAnimation();
        netFailLayout.clearAnimation();
        nullContentView.clearAnimation();
        if (animation != null)
            animation.setAnimationListener(null);
        if (toNormal != null)
            handler.removeCallbacks(toNormal);
    }

    public interface ReFreshDataListener {
        void refreshData();
    }

    public ReFreshDataListener getReFreshDataListener() {
        return reFreshDataListener;
    }

    public void setReFreshDataListener(ReFreshDataListener reFreshDataListener) {
        this.reFreshDataListener = reFreshDataListener;
    }

    public String getNull_text() {
        return null_text;
    }

    public void setNull_text(String null_text) {
        this.null_text = null_text;
        if (null_textView != null)
            null_textView.setText(null_text);
    }

    public List<String> getLoadding_list() {
        return loadding_list;
    }

    public void setLoadding_list(List<String> loadding_list) {
        this.loadding_list = loadding_list;
    }

    public String getError_text() {
        return error_text;
    }

    public void setError_text(String error_text) {
        this.error_text = error_text;
        if (error_textView != null)
            error_textView.setText(error_text);
    }

    public void setProgress(int progress) {
        loadding_progress.setCurrentProgressByAnim(progress);
    }

    public void setProgress(int progress, int time) {
        loadding_progress.setCurrentProgressByAnim(progress, time);
    }

    public void setProgressNoAnim(int progress) {
        loadding_progress.setCurrentProgress(progress);
    }

    public void setmaxProgress(int max_progress) {
        loadding_progress.setMax_progress(max_progress);
    }

    public void resetProgress() {
        loadding_progress.reset();
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Random random = new Random();
            loadding_textView.setText(loadding_list.get(random.nextInt(loadding_list.size())));
            handler.postDelayed(runnable, 2000);
        }
    };

    public void DissMissRun() {
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onDetachedFromWindow() {
        handler.removeCallbacks(runnable);
        super.onDetachedFromWindow();
    }

    public Staus getStaus() {
        return staus;
    }

    public TextView getNetFailRefreshTextView() {
        return netFailRefreshTextView;
    }

    public void setNetFailRefreshTextView(TextView netFailRefreshTextView) {
        this.netFailRefreshTextView = netFailRefreshTextView;
    }

    public TextView getNullRefreshTextView() {
        return nullRefreshTextView;
    }

    public void setNullRefreshTextView(TextView nullRefreshTextView) {
        this.nullRefreshTextView = nullRefreshTextView;
    }

    public String getNetFailRefreshText() {
        return netFailRefreshText;
    }

    public void setNetFailRefreshText(String netFailRefreshText) {
        this.netFailRefreshText = netFailRefreshText;
        if (netFailRefreshTextView != null)
            netFailRefreshTextView.setText(netFailRefreshText);
    }

    public String getNullRefreshText() {
        return nullRefreshText;
    }

    public void setNullRefreshText(String nullRefreshText) {
        this.nullRefreshText = nullRefreshText;
        if (nullRefreshTextView != null)
            nullRefreshTextView.setText(nullRefreshText);
    }

    public View getNet_fail_real() {
        return net_fail_real;
    }

    public void setNet_fail_real(View net_fail_real) {
        this.net_fail_real = net_fail_real;
    }

    public View getNull_content_real() {
        return null_content_real;
    }

    public void setNull_content_real(View null_content_real) {
        this.null_content_real = null_content_real;
    }

    public View getLoading_real() {
        return loading_real;
    }

    public void setLoading_real(View loading_real) {
        this.loading_real = loading_real;
    }

    public void setOffY(int y) {
        this.centerViewOffY = y;
        if (net_fail_real != null)
            net_fail_real.setTranslationY(y);
        if (null_content_real != null)
            null_content_real.setTranslationY(y);
        if (loading_real != null)
            loading_real.setTranslationY(y);
    }
}
