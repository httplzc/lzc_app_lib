package com.yioks.lzclib.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yioks.lzclib.Adapter.PicImgGridAdapter;
import com.yioks.lzclib.Adapter.XiangceListAdapter;
import com.yioks.lzclib.Data.ScreenData;
import com.yioks.lzclib.Data.XiangceData;
import com.yioks.lzclib.Helper.GetPhoneImgManager;
import com.yioks.lzclib.R;
import com.yioks.lzclib.Untils.DialogUtil;
import com.yioks.lzclib.View.NumberCheckView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PickImgActivity extends AppCompatActivity {

    private ImageView back;
    private GridView gridView;
    private TextView choice_album;
    private PopupWindow popupWindow;
    private PicImgGridAdapter picImgGridAdapter;
    private XiangceListAdapter xiangceListAdapter;
    private Button finishButton;
    private int limitsize = 9;
    public static final int PICK_MANY_PIC = 3560;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_img);
        limitsize = getIntent().getIntExtra("limitsize", 9);
        initView();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.pic_img_back);
        gridView = (GridView) findViewById(R.id.pick_pic_grid);
        choice_album = (TextView) findViewById(R.id.choice_album);
        finishButton = (Button) findViewById(R.id.finish_text);
        initGridAdapter();
        initClickListener();
    }

    /**
     * 初始化监听数据
     */
    private void initClickListener() {
        //点击结束按钮
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Uri[] uris = new Uri[picImgGridAdapter.getChoiceUriList().size()];
                intent.putExtra("uriList", picImgGridAdapter.getChoiceUriList().toArray(uris));
                setResult(PICK_MANY_PIC, intent);
                finish();
            }
        });
        finishButton.setText("0/" + limitsize + "完成");

        //设置适配器
        gridView.setAdapter(picImgGridAdapter);

        //点击返回键
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //滑动事件监听
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int firstVisibleItem;
            private int visibleItemCount;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // DialogUtil.ShowToast(PickImgActivity.this,"22222222");
                if (scrollState == SCROLL_STATE_IDLE) {
                    for (int i = 0; i < picImgGridAdapter.getUriList().size(); i++) {
                        Picasso.with(PickImgActivity.this).resumeTag("picimg" + i);
                    }

                } else {
                    //    DialogUtil.ShowToast(PickImgActivity.this,"asdhjgashjdqeeklqheikhqw");
                    for (int i = 0; i < picImgGridAdapter.getUriList().size(); i++) {
                        Picasso.with(PickImgActivity.this).pauseTag("picimg" + i);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                this.firstVisibleItem = firstVisibleItem;
                this.visibleItemCount = visibleItemCount;
            }
        });

        //列表点击事件监听
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (picImgGridAdapter.getChoiceUriList().size() == limitsize && !picImgGridAdapter.getChoiceUriList().contains(picImgGridAdapter.getUriList().get(position))) {
                    if (limitsize == 1) {
                        int lastPosition = picImgGridAdapter.getUriList().indexOf(picImgGridAdapter.getChoiceUriList().get(0));
                        picImgGridAdapter.getChoiceUriList().clear();
                        picImgGridAdapter.getChoiceUriList().add(picImgGridAdapter.getUriList().get(position));
                        if (CheckPositionVisible(position)) {
                            setCheck(gridView.getChildAt(position - gridView.getFirstVisiblePosition()), position);
                        }
                        if (CheckPositionVisible(lastPosition)) {
                            setCheck(gridView.getChildAt(lastPosition - gridView.getFirstVisiblePosition()), lastPosition);
                        }
                        return;
                    }
                    DialogUtil.ShowToast(PickImgActivity.this, "选取图片已达上限");
                    return;
                }
                if (!picImgGridAdapter.getChoiceUriList().contains(picImgGridAdapter.getUriList().get(position))) {
                    picImgGridAdapter.getChoiceUriList().add(picImgGridAdapter.getUriList().get(position));
                } else {
                    picImgGridAdapter.getChoiceUriList().remove(picImgGridAdapter.getUriList().get(position));
                }
                finishButton.setText("" + picImgGridAdapter.getChoiceUriList().size() + "/" + limitsize + "完成");
                if (CheckPositionVisible(position)) {
                    setCheck(gridView.getChildAt(position - gridView.getFirstVisiblePosition()), position);
                }

            }
        });


        //切换相册
        choice_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
                WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                layoutParams.alpha = 0.3f;
                getWindow().setAttributes(layoutParams);
            }
        });
    }

    private boolean CheckPositionVisible(int position) {
        return position >= gridView.getFirstVisiblePosition() && position <= gridView.getLastVisiblePosition();
    }


    /**
     * 初始化适配器数据
     */
    private void initGridAdapter() {
        picImgGridAdapter = new PicImgGridAdapter(this);
        List<Uri> uriList = GetPhoneImgManager.GetAllPic(PickImgActivity.this.getContentResolver());
        if (uriList == null) {
            return;
        }
        //为图片添加数据
        picImgGridAdapter.getUriList().addAll(uriList);
        HashMap<String, XiangceData> xiangce = new HashMap<>();
        //遍历以得出相册列表
        List<String> stringList = GetPhoneImgManager.GetXiangCeList(PickImgActivity.this.getContentResolver());
        if (stringList == null) {
            choice_album.setClickable(false);
            return;
        }

        //得出几种相册类型
        for (String str : stringList) {
            if (xiangce.containsKey(str)) {
                xiangce.get(str).setCount(xiangce.get(str).getCount() + 1);
            } else {
                XiangceData xiangceData = new XiangceData();
                xiangceData.setCount(1);
                List<Uri> uris = GetPhoneImgManager.GetAllPicByParentName(PickImgActivity.this.getContentResolver(), str, true);
                if (uris.size() != 0) {
                    xiangceData.setUri(uris.get(0));
                }

                xiangceData.setName(str);
                xiangce.put(str, xiangceData);
            }

        }

        List<XiangceData> xiangceDataList = new ArrayList<>();
        //为相册基础类赋值
        //数量，名称，预览图，

        //所有图片
        XiangceData xiangceData = new XiangceData();
        xiangceData.setName("所有图片");
        if (picImgGridAdapter.getUriList().size() != 0) {
            xiangceData.setUri(picImgGridAdapter.getUriList().get(0));
        } else {
            choice_album.setClickable(false);
        }
        xiangceData.setCount(picImgGridAdapter.getCount());
        xiangceDataList.add(xiangceData);


        for (Map.Entry<String, XiangceData> integerEntry : xiangce.entrySet()) {
            xiangceDataList.add(1, integerEntry.getValue());
        }


        initPopupwindow(xiangceDataList);
    }

    private void initPopupwindow(List<XiangceData> xiangceList) {
        View view = LayoutInflater.from(PickImgActivity.this).inflate(R.layout.picimg_popupwindow_layout, null);
        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, (int) (ScreenData.density * 500f));
        final ListView listView = (ListView) view.findViewById(R.id.xiangce_list);
        xiangceListAdapter = new XiangceListAdapter(PickImgActivity.this);
        xiangceListAdapter.setXiangceDataList(xiangceList);
        listView.setAdapter(xiangceListAdapter);
        popupWindow.setAnimationStyle(R.style.popwindow_anim_style_bottom);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
                layoutParams.alpha = 1;
                getWindow().setAttributes(layoutParams);
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });


        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    popupWindow.dismiss();
                }
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                xiangceListAdapter.setSelectPostion(position);
                xiangceListAdapter.notifyDataSetChanged();
                popupWindow.dismiss();
                if (position != 0) {
                    picImgGridAdapter.setUriList(GetPhoneImgManager.GetAllPicByParentName(getContentResolver(), xiangceListAdapter.getXiangceDataList().get(position).getName(), false));
                } else {
                    picImgGridAdapter.setUriList(GetPhoneImgManager.GetAllPic(getContentResolver()));

                }
                gridView.smoothScrollToPosition(0);
                picImgGridAdapter.notifyDataSetChanged();
                choice_album.setText(xiangceListAdapter.getXiangceDataList().get(position).getName());
            }
        });
    }

    public void setCheck(View view, int postion) {
        try {
            PicImgGridAdapter.ViewHoler viewHoler = (PicImgGridAdapter.ViewHoler) view.getTag();
            Uri uri = picImgGridAdapter.getUriList().get(postion);
            NumberCheckView numberCheckView = (NumberCheckView) viewHoler.checkBox;
            if (picImgGridAdapter.getChoiceUriList().contains(uri)) {

                numberCheckView.setChecked(true);
                numberCheckView.setNumber(picImgGridAdapter.getChoiceUriList().indexOf(uri) + 1);
            } else {
                int cancelNumber = numberCheckView.getNumber();
                numberCheckView.setChecked(false);
                for (int i = 0; i < gridView.getCount(); i++) {
                    if(gridView.getChildAt(i)==numberCheckView)
                        continue;
                    PicImgGridAdapter.ViewHoler viewHolertemp = (PicImgGridAdapter.ViewHoler) gridView.getChildAt(i).getTag();
                    NumberCheckView numberCheckViewTemp = (NumberCheckView) viewHolertemp.checkBox;
                    if (numberCheckViewTemp.getNumber() > cancelNumber) {
                        numberCheckViewTemp.setNumber(numberCheckViewTemp.getNumber() - 1);
                        numberCheckViewTemp.invalidate();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


