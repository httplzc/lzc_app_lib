package com.yioks.lzclib.Activity;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yioks.lzclib.R;
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
            Picasso.with(TitleBaseActivity.this).load(R.drawable.common_back).fit().into(left);
            left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        else
        {
            left.setVisibility(View.GONE);
        }

        if (rightRes != -1) {
            right.setVisibility(View.VISIBLE);
            Picasso.with(TitleBaseActivity.this).load(rightRes).fit().into(right);
        }
        else
        {
            right.setVisibility(View.GONE);
        }
    }

    public void bindTitle(boolean leftRes, String titleString, String rightRes)
    {
        initTitleView();
        title.setText(titleString);
        if (leftRes) {
            left.setVisibility(View.VISIBLE);
            Picasso.with(TitleBaseActivity.this).load(R.drawable.common_back).fit().into(left);
            left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        else
        {
            left.setVisibility(View.GONE);
        }
        right.setVisibility(View.GONE);
        title_text.setVisibility(View.VISIBLE);
        title_text.setText(rightRes);
    }

    private void initTitleView()
    {
        left = (ImageView) findViewById(R.id.left_img);
        title = (TextView) findViewById(R.id.title);
        right = (ImageView) findViewById(R.id.right_img);
        title_text= (TextView) findViewById(R.id.title_text);
    }

    public void setTitleState() {
        context=this;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        else
        {
            return;
        }

//        final int sdk = Build.VERSION.SDK_INT;
//        Window window = this.getWindow();
//        WindowManager.LayoutParams params = window.getAttributes();
//
//        if (sdk == Build.VERSION_CODES.KITKAT) {
//            int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
//            // 设置透明状态栏
//            if ((params.flags & bits) == 0) {
//                params.flags |= bits;
//                window.setAttributes(params);
//            }

        // 设置状态栏颜色
        ViewGroup contentLayout = (ViewGroup) findViewById(android.R.id.content);
        setupStatusBarView(contentLayout, R.color.colorPrimary);
        View contentChild = contentLayout.getChildAt(0);
        contentChild.setFitsSystemWindows(true);

    }


    private void setupStatusBarView(ViewGroup contentLayout, int color) {
        if (mStatusBarView == null) {
            View statusBarView = new View(this);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(this));
            contentLayout.addView(statusBarView, lp);

            mStatusBarView = statusBarView;
        }
        mStatusBarView.setBackgroundResource(color);
    }

    /**
     * 获得状态栏高度
     */
    private static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancelAllClient(this);
        super.onDestroy();
    }
}
