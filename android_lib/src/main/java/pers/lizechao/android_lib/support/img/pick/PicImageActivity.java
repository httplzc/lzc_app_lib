package pers.lizechao.android_lib.support.img.pick;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import pers.lizechao.android_lib.BR;
import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.databinding.ActivityPicImageBinding;
import pers.lizechao.android_lib.databinding.ItemAlbumLayoutBinding;
import pers.lizechao.android_lib.databinding.ItemPickImgLayoutBinding;
import pers.lizechao.android_lib.ui.common.BaseActivity;
import pers.lizechao.android_lib.ui.common.CommRecyclerAdapter;
import pers.lizechao.android_lib.ui.manager.ScreenManager;
import pers.lizechao.android_lib.ui.manager.StatusBarManager;
import pers.lizechao.android_lib.ui.widget.HeadFootRecycleView;
import pers.lizechao.android_lib.ui.widget.RecycleViewDecoration;
import pers.lizechao.android_lib.utils.DialogUtil;

public class PicImageActivity extends BaseActivity<ActivityPicImageBinding> {

    private CommRecyclerAdapter<PickAlbumData, ItemAlbumLayoutBinding> albumAdapter;
    private CommRecyclerAdapter<PickImageData, ItemPickImgLayoutBinding> imageAdapter;
    private PopupWindow popupWindow;
    private ScreenManager screenManager;
    //当前选中的相册
    private int lastAlbumChoicePosition = 0;
    //最大选中数
    private int limitSize = 9;
    //已选中的集合
    private final List<PickImageData> pickImageChoiceDataList = new ArrayList<>();

    //数据集合
    private final HashMap<String, List<PickImageData>> dataHashMap = new HashMap<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initIntentParams();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initExtraView() {
        super.initExtraView();
        screenManager = new ScreenManager(getResources());
        initAdapter();
        initPopupView();
        initRecycleView();
        initListener();
        updateFinishText();
        initData();
        changeData("-1");
    }

    private void initPopupView() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_pic_img_popupwindow_layout, null);
        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, screenManager.DpToPx(500));
        popupWindow.setAnimationStyle(R.style.pop_window_anim_style_bottom);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        popupWindow.setOnDismissListener(() -> {
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.alpha = 1;
            getWindow().setAttributes(layoutParams);
        });
        view.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                popupWindow.dismiss();
            }
            return false;
        });
        HeadFootRecycleView recyclerView = view.findViewById(R.id.albumRecycleView);
        initAlbumRecyclerView(recyclerView);

        //切换相册
        viewBind.choiceAlbum.setOnClickListener(v -> {
            popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.alpha = 0.3f;
            getWindow().setAttributes(layoutParams);
        });

    }

    /**
     * 初始化相册列表
     *
     *
     */
    private void initAlbumRecyclerView(HeadFootRecycleView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(albumAdapter);
        RecycleViewDecoration recycleViewDecoration = new RecycleViewDecoration();
        recycleViewDecoration.setVerticalInterval(screenManager.DpToPx(1f));
        recyclerView.addItemDecoration(recycleViewDecoration);
        recyclerView.setOnItemClickListener((view, position) -> {
            albumAdapter.getDataList().get(lastAlbumChoicePosition).setCheck(false);
            albumAdapter.getDataList().get(position).setCheck(true);
            albumAdapter.notifyItemChanged(lastAlbumChoicePosition);
            albumAdapter.notifyItemChanged(position);
            lastAlbumChoicePosition = position;
            changeData(albumAdapter.getDataList().get(position).getId());
            viewBind.recycleView.smoothScrollToPosition(0);
            viewBind.choiceAlbum.setText(albumAdapter.getDataList().get(position).getName());
            popupWindow.dismiss();
        });
    }


    private void initAdapter() {
        albumAdapter = new CommRecyclerAdapter<>(R.layout.item_album_layout, BR.album, this);
        imageAdapter = new CommRecyclerAdapter<>(R.layout.item_pick_img_layout, BR.picImageData, this);
    }

    private void initRecycleView() {
        viewBind.recycleView.setLayoutManager(new GridLayoutManager(this, 4));
        viewBind.recycleView.setAdapter(imageAdapter);
        RecycleViewDecoration recycleViewDecoration = new RecycleViewDecoration(4);
        recycleViewDecoration.setInnerInterval(screenManager.DpToPx(3f), screenManager.DpToPx(3f));
        viewBind.recycleView.addItemDecoration(recycleViewDecoration);
        viewBind.recycleView.setOnItemClickListener((view, position) -> onClickImage(position));
    }

    private void onClickImage(int position) {
        PickImageData pickImageDataCurrent = imageAdapter.getDataList().get(position);
        //取消选中
        if (pickImageDataCurrent.isHaveChoice()) {
            pickImageDataCurrent.setHaveChoice(false);
            pickImageChoiceDataList.remove(pickImageDataCurrent);
        }
        //选中
        else {
            if (pickImageChoiceDataList.size() >= limitSize) {
                if (limitSize == 1) {
                    int last = imageAdapter.getDataList().indexOf(pickImageChoiceDataList.get(0));
                    imageAdapter.getDataList().get(last).setHaveChoice(false);
                    pickImageChoiceDataList.clear();
                    notifyNumberCheckChange(last);
                } else {
                    DialogUtil.ShowToast("只能选择" + limitSize + "张");
                    return;
                }

            }
            pickImageDataCurrent.setHaveChoice(true);
            pickImageChoiceDataList.add(pickImageDataCurrent);
        }
        notifyNumberCheckChange(position);
        //修改序号
        for (int i = 0; i < pickImageChoiceDataList.size(); i++) {
            pickImageChoiceDataList.get(i).setChoiceIndex(i + 1);
            int indexNow = imageAdapter.getDataList().indexOf(pickImageChoiceDataList.get(i));
            if (indexNow != -1) {
                notifyNumberCheckChange(indexNow);
            }
        }
        updateFinishText();
    }

    private void notifyNumberCheckChange(int position) {
        RecyclerView.ViewHolder viewHolder = viewBind.recycleView.findViewHolderForAdapterPosition(position);
        if (viewHolder == null)
            return;
        ItemPickImgLayoutBinding itemBind = ((CommRecyclerAdapter<PickImageData, ItemPickImgLayoutBinding>.ViewHolder) viewHolder).viewDataBinding;
        PickImageData pickImageData = imageAdapter.getDataList().get(position);
        itemBind.picCheckBox.setNumberCheckCheck(pickImageData.isHaveChoice());
        itemBind.picCheckBox.setNumberCheckNumber(pickImageData.getChoiceIndex());
    }

    private void updateFinishText() {
        viewBind.finishText.setText(String.format(Locale.CHINESE, "%d/%d 完成", pickImageChoiceDataList.size(), limitSize));
    }

    private void initListener() {
        viewBind.leftImg.setOnClickListener(v -> finish());
        viewBind.finishText.setOnClickListener(v -> {
            if (pickImageChoiceDataList.size() == 0)
                finish();
            else {
                Uri uris[] = new Uri[pickImageChoiceDataList.size()];
                for (int i = 0; i < uris.length; i++) {
                    uris[i] = pickImageChoiceDataList.get(i).getUri();
                }
                callBackData(uris);
            }

        });
    }

    @Override
    protected StatusBarManager.BarState getBarState() {
        return StatusBarManager.BarState.Normal;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pic_image;
    }


    private void changeData(String id) {
        imageAdapter.setDataList(dataHashMap.get(id));
        imageAdapter.notifyDataSetChanged();
    }


    private void initData() {
        List<PickAlbumData> pickAlbumDataList = new ArrayList<>();
        List<Album> albumList = GetPhoneImgManager.selectAlbums(getContentResolver());
        Album albumAll = new Album();
        albumAll.setId("-1");
        albumAll.setName("全部图片");
        List<Photo> allImage = new ArrayList<>();
        for (Album album : albumList) {
            allImage.addAll(album.getPhotoList());
            pickAlbumDataList.add(new PickAlbumData(album));
            dataHashMap.put(album.getId(), Stream.ofNullable(album.getPhotoList()).map(photo -> new PickImageData(photo.getPath())).toList());
        }
        GetPhoneImgManager.sortPhotosByTime(allImage);
        albumAll.setPhotoList(allImage);
        albumList.add(0, albumAll);
        PickAlbumData pickAlbumData = new PickAlbumData(albumAll);
        pickAlbumData.setCheck(true);
        pickAlbumDataList.add(0, pickAlbumData);
        dataHashMap.put(albumAll.getId(), Stream.ofNullable(albumAll.getPhotoList()).map(photo -> new PickImageData(photo.getPath())).toList());
        albumAdapter.setDataList(pickAlbumDataList);
    }


    private void callBackData(Uri uri[]) {
        Intent intent = new Intent();
        Parcelable parcelable[] = new Parcelable[uri.length];
        System.arraycopy(uri, 0, parcelable, 0, parcelable.length);
        intent.putExtra("uris", parcelable);
        setResult(PickImageManager.Pick_Image, intent);
        finish();
    }


    public static Intent createIntent(Context context, int limitSize) {
        Intent intent = new Intent(context, PicImageActivity.class);
        intent.putExtra("limitSize", limitSize);
        return intent;
    }

    public void initIntentParams() {
        Intent intent = getIntent();
        limitSize = intent.getIntExtra("limitSize", 0);
    }
}
