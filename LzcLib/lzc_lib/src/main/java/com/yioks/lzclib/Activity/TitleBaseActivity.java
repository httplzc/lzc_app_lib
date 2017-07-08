package com.yioks.lzclib.Activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.yioks.lzclib.Data.ScreenData;
import com.yioks.lzclib.R;
import com.yioks.lzclib.Untils.DeviceUtil;
import com.yioks.lzclib.Untils.DialogUtil;
import com.yioks.lzclib.Untils.HttpUtil;


/**
 * Created by Administrator on 2016/8/2 0002.
 */
public class TitleBaseActivity extends AppCompatActivity {
    //左边返回按钮
    public ImageView left;
    //标题栏
    public TextView title;
    //右边图标
    public ImageView right;
    public View mStatusBarView;
    protected Context context;
    //右边文字
    protected TextView title_text;
    private View statusBarView;

    /**
     * 绑定控件，并预制回调
     *
     * @param leftRes
     * @param titleString
     * @param rightRes
     */

    public void bindTitle(boolean leftRes, String titleString, int rightRes) {

        initTitleView();
        title.setText(titleString);
        if (leftRes) {
            left.setVisibility(View.VISIBLE);
            //  Picasso.with(TitleBaseActivity.this).load(R.drawable.common_back).fit().into(left);
            left.setImageResource(R.drawable.common_back);
            left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            left.setVisibility(View.GONE);
        }

        if (rightRes != -1) {
            right.setVisibility(View.VISIBLE);
            //  Picasso.with(TitleBaseActivity.this).load(rightRes).fit().into(right);
            right.setImageResource(rightRes);
        } else {
            right.setVisibility(View.GONE);
        }
    }


    public void bindTitle(boolean leftRes, String titleString, String rightRes) {
        initTitleView();
        title.setText(titleString);
        if (leftRes) {
            left.setVisibility(View.VISIBLE);
            //Picasso.with(TitleBaseActivity.this).load(R.drawable.common_back).fit().into(left);
            left.setImageResource(R.drawable.common_back);
            left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else {
            left.setVisibility(View.GONE);
        }
        right.setVisibility(View.GONE);
        title_text.setVisibility(View.VISIBLE);
        title_text.setText(rightRes);
    }

    private void initTitleView() {
        left = (ImageView) findViewById(R.id.left_img);
        title = (TextView) findViewById(R.id.title);
        right = (ImageView) findViewById(R.id.right_img);
        title_text = (TextView) findViewById(R.id.title_text);
    }


    public void setTitleState() {
        context = this;
        ScreenData.init_srceen_data(this);
        ViewGroup contentLayout = (ViewGroup) findViewById(android.R.id.content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            window.setStatusBarColor(ContextCompat.getColor(context, R.color.colorPrimary));

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            setupStatusBarView(contentLayout, R.color.colorPrimary);

        }

    }


    private void setupStatusBarView(ViewGroup contentLayout, int color) {

        statusBarView = new View(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, DeviceUtil.getStatusBarHeight(this));
        contentLayout.addView(statusBarView, lp);
        mStatusBarView = statusBarView;
        mStatusBarView.setBackgroundResource(color);
        changeMargin(true, contentLayout);
    }

    private void changeMargin(boolean isPadding, ViewGroup contentLayout) {
        View contentChild = contentLayout.getChildAt(0);
//        if(contentChild instanceof LinearLayout)
//            return;
//        int a = getStatusBarHeight(context);
//        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) contentChild.getLayoutParams();
//        lp.topMargin = isPadding ? a : 0;
//        contentChild.setLayoutParams(lp);
        contentChild.setFitsSystemWindows(isPadding);
        //  contentChild.setPadding(contentChild.getLeft(),, contentChild.getRight(), contentChild.getBottom());
        //contentChild.requestLayout();
    }

    protected void changeStatusBarView(boolean hide) {
        if (hide) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ViewGroup contentLayout = (ViewGroup) findViewById(android.R.id.content);
                contentLayout.removeView(statusBarView);
                changeMargin(false, contentLayout);

            }
        } else {
            setTitleState();
        }
    }



    protected void cancelRequest() {
        HttpUtil.cancelAllClient(this);
        DialogUtil.cancelToast();
    }

    @Override
    protected void onPause() {
        super.onPause();
        DialogUtil.cancelToast();
        DialogUtil.dismissDialog();
    }

    @Override
    protected void onDestroy() {
        cancelRequest();
        super.onDestroy();
    }
}
