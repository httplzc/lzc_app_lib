package pers.lizechao.android_lib.support.img.cult;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.io.File;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import pers.lizechao.android_lib.R;
import pers.lizechao.android_lib.databinding.ActivityImageCultBinding;
import pers.lizechao.android_lib.storage.file.StoreMedium;
import pers.lizechao.android_lib.storage.file.FileStoreManager;
import pers.lizechao.android_lib.storage.file.FileStoreOption;
import pers.lizechao.android_lib.storage.file.Path;
import pers.lizechao.android_lib.support.img.utils.ImageUtils;
import pers.lizechao.android_lib.ui.common.BaseActivity;
import pers.lizechao.android_lib.ui.common.WaitViewObserver;
import pers.lizechao.android_lib.ui.manager.StatusBarManager;
import pers.lizechao.android_lib.utils.DialogUtil;
import pers.lizechao.android_lib.utils.UriUtils;

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
 * Date: 2018-08-17
 * Time: 11:54
 */
public class ImageCultActivity extends BaseActivity<ActivityImageCultBinding> {
    public static final int CULT_PIC = 1320;
    public static final String ResultKey = "cut_pic";
    private float ratio;
    private boolean is_circle;
    private Uri uri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        initParams();
        super.onCreate(savedInstanceState);
        initView();
        startLoadImage();
    }

    private void startLoadImage() {
        new Single<Bitmap>() {
            @Override
            protected void subscribeActual(SingleObserver<? super Bitmap> observer) {
                try {
                    Bitmap bitmap = ImageUtils.loadBitmapAndCompress(() -> getContentResolver().openInputStream(uri), 1080, 1920);
                    Bitmap result = ImageUtils.rotateBitmap(bitmap, ImageUtils.readPictureDegree(UriUtils.getRealPathFromUri(getContentResolver(), uri)));
                    if (bitmap != result)
                        bitmap.recycle();
                    observer.onSuccess(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    observer.onError(e);
                }
            }
        }.subscribeOn(Schedulers.newThread())
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(new WaitViewObserver<Bitmap>(activity, "正在读取文件……") {
              @Override
              public void onError(Throwable e) {
                  super.onError(e);
                  DialogUtil.ShowToast("读取文件失败！");
                  callError();
              }


              @Override
              public void onSuccess(Bitmap bitmap) {
                  super.onSuccess(bitmap);
                  viewBind.moveImage.setImageBitmap(bitmap);
              }
          });
    }


    private void initView() {
        viewBind.titleBarView.setTitleData(true, "裁剪图片", "保存");
        viewBind.titleBarView.setRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileStoreManager.getRxFileStore(StoreMedium.External).store("ImageCultActivity_Cult", viewBind.moveImage.getCultImage(),
                  Path.parse("/cache/cult_img/cult_img.jpg"), Bitmap.CompressFormat.JPEG, FileStoreOption.CreateNew)
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(new WaitViewObserver<File>(activity, "正在保存文件") {
                      @Override
                      public void onError(Throwable e) {
                          super.onError(e);
                          DialogUtil.ShowToast("文件保存失败");
                          callError();
                      }

                      @Override
                      public void onSuccess(File file) {
                          super.onSuccess(file);
                          callSucceed(file);
                      }
                  });
            }
        });
        viewBind.moveImage.setImagePadding(viewBind.moveImage.getImagePadding(), ratio);
        viewBind.maskView.setIs_circle(is_circle);
        viewBind.maskView.setRatio(ratio);
    }

    private void initParams() {
        ratio = getIntent().getFloatExtra("ratio", 1);
        is_circle = getIntent().getBooleanExtra("is_circle", false);
        uri = getIntent().getData();
    }

    @Override
    protected StatusBarManager.BarState getBarState() {
        return StatusBarManager.BarState.Normal;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_image_cult;
    }

    private void callError() {
        Intent intent = new Intent();
        setResult(CULT_PIC, intent);
        finish();
    }

    private void callSucceed(File file) {
        Intent intent = new Intent();
        intent.putExtra(ResultKey, file.getPath());
        setResult(CULT_PIC, intent);
        finish();
    }


    /**
     * @param uri       需要裁剪 uri
     * @param ratio     比例  w/h
     * @param is_circle 是否为圆形
     */
    public static Intent startCultImgIntent(Activity context, Uri uri, float ratio, boolean is_circle) {
        Intent intent = new Intent();
        intent.setClass(context, ImageCultActivity.class);
        intent.setData(uri);
        intent.putExtra("ratio", ratio);//设置裁剪比例
        intent.putExtra("is_circle", is_circle);
        return intent;
    }


}
