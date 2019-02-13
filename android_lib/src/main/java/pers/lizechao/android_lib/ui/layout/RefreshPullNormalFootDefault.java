package pers.lizechao.android_lib.ui.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.ui.widget.refresh.RefreshPullNormalView;

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
 * Date: 2018-08-01
 * Time: 15:07
 */
@SuppressLint("ViewConstructor")
public class RefreshPullNormalFootDefault extends RefreshPullNormalView {
    //刷新文字控件
    private TextView refreshText;
    //刷新图片
    private ImageView reFreshImg;
    //下拉刷新状态视图
    private LinearLayout pull;
    //正在加载状态视图
    private FrameLayout loadding;
    //刷新成功
    private LinearLayout refresh_succeed;
    //加载中效果
    private View loadding_effect;

    public RefreshPullNormalFootDefault(@NonNull Context context, boolean isTop) {
        super(context, isTop);
    }

    @Override
    protected View onCreateView(Context context, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.refresh_more_pull_view, viewGroup, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        refreshText = view.findViewById(R.id.refresh_text);
        reFreshImg = view.findViewById(R.id.refresh_img);
        pull = view.findViewById(R.id.pull);
        loadding = view.findViewById(R.id.loadding);
        refresh_succeed = view.findViewById(R.id.refresh_succeed);
        loadding_effect = view.findViewById(R.id.loadding_effect);
    }

    @Override
    protected void onViewStateChange(PullStatus newStatus) {
        switch (newStatus) {
            case NORMAL:
                loadding.setVisibility(INVISIBLE);
                pull.setVisibility(VISIBLE);
                reFreshImg.setImageResource(R.drawable.indicator_arrow);
                int dx = (int) screenManager.DpToPx(5);
                reFreshImg.setPadding(dx, dx, dx, dx);


                loadding_effect.setVisibility(VISIBLE);
                refresh_succeed.setVisibility(INVISIBLE);
                break;
            case ON_REFRESH:
                loadding.setVisibility(VISIBLE);
                pull.setVisibility(INVISIBLE);
                break;
            case PULL_CANCEL:
                reFreshImg.setRotation(0);
                refreshText.setText(R.string.refresh_more_pull);
                break;
            case PULL_REFRESH:
                reFreshImg.setRotation(180);
                refreshText.setText(R.string.refresh_more_release);
                break;
            case REFRESH_SUCCEED:
                refresh_succeed.setVisibility(VISIBLE);
                loadding_effect.setVisibility(GONE);
                break;
        }
    }

}
