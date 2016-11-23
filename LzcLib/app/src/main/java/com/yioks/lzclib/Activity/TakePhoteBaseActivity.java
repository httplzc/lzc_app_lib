package com.yioks.lzclib.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.yioks.lzclib.R;
import com.yioks.lzclib.Untils.FileUntil;

import java.io.File;

/**
 * 继承他可调用相机
 * Created by lzc on 2016/7/11 0011.
 */
public abstract class TakePhoteBaseActivity extends TitleBaseActivity {
    //底部弹窗
    public PopupWindow popupWindow;
    //选取图片值
    public static final int Pick_Pic = 1024;
    public float bili = 1f;
    //选择相机值
    public static final int Choice_Cameia = 2000;
    private File file;
    private int limit = 1;
    private boolean is_circle = false;

    private Uri photoUri;
    private Activity activity;

    private int limitCount;

    /**
     * 调用裁剪图片方法
     *
     * @param uri
     */
    public void DealCultPic(Uri uri) {
        if (uri == null) {
            uri = photoUri;
        }
        if (uri == null) {
            return;
        }
        startCult(uri);
    }

    private void startCult(Uri uri) {
        if (bili == -1) {
            onCutPicfinish(uri);
            return;
        }
        //调用裁剪图片activity
        Intent intent = new Intent();
        intent.setClass(this, PicCultActivity.class);
        intent.setData(uri);
        intent.putExtra("bili", bili);//设置裁剪比例
        intent.putExtra("is_circle", is_circle);
        startActivityForResult(intent, PicCultActivity.CULT_PIC);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //activity 返回值
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //选择图片
        if (requestCode == PickImgActivity.PICK_MANY_PIC) {
            if (data == null) {
                return;
            }
            Parcelable[] parcelables = data.getParcelableArrayExtra("uriList");
            Uri[] uris = new Uri[parcelables.length];
            for (int i = 0; i < parcelables.length; i++) {
                uris[i] = (Uri) parcelables[i];
            }
            if (uris.length == 0) {
                return;
            }
            if (limit != 1) {
                onCutPicfinish(uris);
                return;
            } else {
                Uri uri = uris[0];
                if (uri != null) {
                    DealCultPic(uri);
                    return;
                }
            }


        }
        //照相
        else if (requestCode == Choice_Cameia && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri == null) {
                    DealCultPic(null);
                } else {
                    DealCultPic(uri);
                }
            } else {
                DealCultPic(null);
            }
            //裁剪图片回调
        } else if (requestCode == PicCultActivity.CULT_PIC) {
            if (data != null) {
                String filepath = data.getStringExtra("filepath");
                if (filepath == null) {
                    return;
                } else {
                    File file = new File(filepath);
                    //返回得到的头像文件
                    onCutPicfinish(file);
                }

            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public abstract void onCutPicfinish(File file);

    public abstract void onCutPicfinish(Uri uri);

    public abstract void onCutPicfinish(Uri[] uris);


    // 创建 选择拍照或相册弹出窗口
    private void CreatePopWindow(final Activity activity, final int limitCount) {
        this.activity = activity;
        this.limitCount = limitCount;
        View view = LayoutInflater.from(this).inflate(R.layout.write_popwindow_layout, null);
        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(R.style.popwindow_anim_style_bottom);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackGroundAlpha(activity, 1f);
            }
        });
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                setBackGroundAlpha(activity, 1f);
                return true;
            }
        });

        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    popupWindow.dismiss();
                    setBackGroundAlpha(activity, 1f);
                }
                return false;
            }
        });


        final TextView quxiao = (TextView) view.findViewById(R.id.quexiao);
        //点击取消
        quxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBackGroundAlpha(activity, 1f);
                popupWindow.dismiss();

            }
        });

        //点击拍照
        TextView paizhao = (TextView) view.findViewById(R.id.tuwen);
        paizhao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.CAMERA"},
                            12546);
                } else {
                    setBackGroundAlpha(activity, 1f);
                    popupWindow.dismiss();
                    getImageFromCamera();
                }
            }
        });

        //点击图库
        TextView tuku = (TextView) view.findViewById(R.id.shipin);

        tuku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{"android.permission.READ_EXTERNAL_STORAGE"},
                            20221);
                } else {
                    setBackGroundAlpha(activity, 1f);
                    popupWindow.dismiss();
                    getImageFromAlbum(limitCount);
                }
            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 20221) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setBackGroundAlpha(activity, 1f);
                popupWindow.dismiss();
                getImageFromAlbum(limitCount);
            } else {
                setBackGroundAlpha(activity, 1f);
                popupWindow.dismiss();
            }
        } else if (requestCode == 12546) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                setBackGroundAlpha(activity, 1f);
                popupWindow.dismiss();
                getImageFromCamera();
            } else {
                setBackGroundAlpha(activity, 1f);
                popupWindow.dismiss();
            }
        }
    }

    /**
     * 显示底部弹窗
     *
     * @param activity
     * @param bili
     * @return
     */
    public PopupWindow showPopwindow(Activity activity, float bili, int limitCount) {
        //判断有没有继承本类
        if (!(activity instanceof TakePhoteBaseActivity)) {
            return null;
        }
        this.bili = bili;
        //创建弹窗
        limit = limitCount;
        CreatePopWindow(activity, limit);
        //降低屏幕颜色
        setBackGroundAlpha(activity, 0.5f);
        popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        return popupWindow;
    }


    public PopupWindow showPopwindow(Activity activity, float bili, int limitCount, boolean isCircle) {
        this.is_circle = isCircle;
        return showPopwindow(activity, bili, limitCount);
    }


    /**
     * 选择相片
     */
    public void getImageFromAlbum(int limitsize) {
        Intent intent = new Intent();
        intent.setClass(TakePhoteBaseActivity.this, PickImgActivity.class);
        intent.putExtra("limitsize", limitsize);
        startActivityForResult(intent, PickImgActivity.PICK_MANY_PIC);
    }

    private void setBackGroundAlpha(Activity activity, float alpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = alpha;
        if (alpha == 1) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }
        activity.getWindow().setAttributes(lp);
    }

    /**
     * 选择照相
     */
    public void getImageFromCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            //创建临时文件
            file = FileUntil.createTempFile();
            if (file == null) {
                Toast.makeText(TakePhoteBaseActivity.this, "创建临时文件失败", Toast.LENGTH_LONG).show();
                return;
            }
            ;

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            photoUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(getImageByCamera, Choice_Cameia);
        } else {
            Toast.makeText(TakePhoteBaseActivity.this, "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        FileUntil.ClearTempFile();
        super.onDestroy();
    }
}
