package com.yioks.lzclib.Helper;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.yioks.lzclib.Activity.PicCultActivity;
import com.yioks.lzclib.Activity.PickImgActivity;
import com.yioks.lzclib.Data.ScreenData;
import com.yioks.lzclib.R;
import com.yioks.lzclib.Service.PressImgService;
import com.yioks.lzclib.Untils.DialogUtil;
import com.yioks.lzclib.Untils.FileUntil;

import java.io.File;
import java.io.Serializable;

import static android.app.Activity.RESULT_OK;

/**
 * Created by ${User} on 2017/2/20 0020.
 * <p>
 * 使用方法  ChoicePhotoManager showChoiceWindow 显示弹窗
 * 需要在onActivityResult 和 onRequestPermissionsResult  中调用对应方法
 */

public class ChoicePhotoManager {

    private final static int maxWidth = 720;
    private final static int maxHeight = 1280;
    private final static float pressRadio = 0.2f;
    private ChoicePhotoManager choicePhotoManager;
    //底部弹窗
    public PopupWindow popupWindow;
    //选取图片值
    public static final int Pick_Pic = 1024;
    //    public float bili = 1f;
    //选择相机值
    public static final int Choice_Cameia = 2000;
    private File file;
    private int limit = 1;
    private boolean is_circle = false;

    private Uri photoUri;
    private Activity activity;


//    private boolean needToCult = true;

    private Option option;

    private onChoiceFinishListener onChoiceFinishListener;

    private Context context;

    private BroadcastReceiver registerReceiver;


    public static class Option implements Serializable {
        public boolean needToPress = true;
        public boolean needToCult = true;
        public float bili = 1;
        public int maxWidth = ChoicePhotoManager.maxWidth;
        public int maxHeight = ChoicePhotoManager.maxHeight;
        public float pressRadio = ChoicePhotoManager.pressRadio;
    }

    public void unRegisterReceiver() {
        context.unregisterReceiver(registerReceiver);
        FileUntil.ClearTempFile();
    }


    public ChoicePhotoManager(Activity context) {
        this.context = context;
        registerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Object datas[] = intent.getParcelableArrayExtra("data");
                Uri uris[]=new Uri[datas.length];
                for (int i = 0; i < datas.length; i++) {
                    uris[i]= (Uri) datas[i];
                }
                DialogUtil.dismissDialog();
                if (onChoiceFinishListener != null) {
                    if (uris.length == 1)
                        onChoiceFinishListener.onCutPicFinish(uris[0]);
                    else
                        onChoiceFinishListener.onCutPicFinish(uris);
                }

            }
        };
        context.registerReceiver(registerReceiver, new IntentFilter(PressImgService.callbackReceiver));
    }

//    public ChoicePhotoManager getInstance(Activity context)
//    {
//        if(choicePhotoManager==null)
//        {
//            synchronized (ChoicePhotoManager.class)
//            {
//                if(choicePhotoManager==null)
//                    choicePhotoManager=new ChoicePhotoManager(context);
//            }
//        }
//        return choicePhotoManager;
//    }


    /**
     * 判断是否裁剪
     *
     * @param uri
     */
    private void DealCultPic(Uri uri) {
        if (uri == null) {
            uri = photoUri;
        }
        if (uri == null) {
            return;
        }
        if (option.needToCult)
            startCult(uri);
        else {
            if (option.needToPress) {
                pressImage(uri);
            } else {
                if (onChoiceFinishListener != null)
                    onChoiceFinishListener.onCutPicFinish(uri);
            }
        }
    }

    public void startCult(Uri uri) {
        if (option.bili == -1) {
            return;
        }
        //调用裁剪图片activity
        Intent intent = new Intent();
        intent.setClass(activity, PicCultActivity.class);
        intent.setData(uri);
        intent.putExtra("bili", option.bili);//设置裁剪比例
        intent.putExtra("is_circle", is_circle);
        activity.startActivityForResult(intent, PicCultActivity.CULT_PIC);
    }


    //activity 返回值
    public void onCultActivityResultDo(int requestCode, int resultCode, Intent data) {

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
                if (option.needToPress) {
                    pressImage(uris);
                } else {
                    if (onChoiceFinishListener != null)
                        onChoiceFinishListener.onCutPicFinish(uris);
                }
            } else {
                Uri uri = uris[0];
                DealCultPic(uri);
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
                Uri uri = data.getData();
                if (option.needToPress) {
                    pressImage(uri);
                } else {
                    if (uri != null) {
                        if (onChoiceFinishListener != null)
                            onChoiceFinishListener.onCutPicFinish(uri);
                    }
                }
            }
        }
    }



    /**
     * 压缩图片
     *
     * @param uris
     */
    private void pressImage(Uri[] uris) {
        DialogUtil.showDialog(context,"正在压缩图片……");
        for (int i = 0; i < uris.length; i++) {
            PressImgService.startActionPress(context, uris[i], option, uris.length,i);
        }
    }

    /**
     * 压缩图片
     *
     * @param uri
     */
    private void pressImage(Uri uri) {
        DialogUtil.showDialog(context,"正在压缩图片……");
        PressImgService.startActionPress(context, uri, option, 1,0);
    }


    public interface onChoiceFinishListener {
        void onCutPicFinish(Uri uri);

        void onCutPicFinish(Uri[] uris);

    }
//
//    //裁剪后图片回调
//    public abstract void onCutPicfinish(File file);
//
//    //未裁剪的图片回调
//    public abstract void onCutPicfinish(Uri uri);
//
//
//    public abstract void onCutPicfinish(Uri[] uris);


    // 创建 选择拍照或相册弹出窗口
    private void CreatePopWindow() {
        View view = LayoutInflater.from(activity).inflate(R.layout.write_popwindow_layout, null);
        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setAnimationStyle(R.style.popwindow_anim_style_bottom);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ScreenData.UpScreenColor(activity);
            }
        });
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                ScreenData.UpScreenColor(activity);
                return true;
            }
        });

        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    popupWindow.dismiss();
                    ScreenData.UpScreenColor(activity);
                }
                return false;
            }
        });


        final TextView quxiao = (TextView) view.findViewById(R.id.quexiao);
        //点击取消
        quxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScreenData.UpScreenColor(activity);
                popupWindow.dismiss();

            }
        });

        //点击拍照
        TextView paizhao = (TextView) view.findViewById(R.id.tuwen);
        paizhao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{"android.permission.READ_EXTERNAL_STORAGE", "android.permission.CAMERA"},
                            12546);
                } else {
                    ScreenData.UpScreenColor(activity);
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
                if (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{"android.permission.READ_EXTERNAL_STORAGE"},
                            20221);
                } else {
                    ScreenData.UpScreenColor(activity);
                    popupWindow.dismiss();
                    getImageFromAlbum();
                }
            }
        });

    }


    public void onCultRequestPermissionsResultDo(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 20221) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ScreenData.UpScreenColor(activity);
                popupWindow.dismiss();
                getImageFromAlbum();
            } else {
                ScreenData.UpScreenColor(activity);
                popupWindow.dismiss();
            }
        } else if (requestCode == 12546) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                ScreenData.UpScreenColor(activity);
                popupWindow.dismiss();
                getImageFromCamera();
            } else {
                ScreenData.UpScreenColor(activity);
                popupWindow.dismiss();
            }
        }
    }

    /**
     * 显示底部弹窗
     *
     * @param activity
     * @param
     * @return
     */
    public PopupWindow showChoiceWindow(Activity activity, int limitCount, Option option) {
        //判断有没有继承本类
        this.option = option;
        this.activity = activity;
        this.limit = limitCount;
        CreatePopWindow();
        //降低屏幕颜色
        ScreenData.DownScreenColor(activity);
        popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM, 0, 0);
        return popupWindow;
    }


    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    /**
     * 选择相片
     */
    private void getImageFromAlbum() {
        Intent intent = new Intent();
        intent.setClass(activity, PickImgActivity.class);
        intent.putExtra("limitsize", limit);
        activity.startActivityForResult(intent, PickImgActivity.PICK_MANY_PIC);
    }


    /**
     * 选择照相
     */
    private void getImageFromCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            //创建临时文件
            file = FileUntil.createTempFile();
            if (file == null) {
                Toast.makeText(activity, "创建临时文件失败", Toast.LENGTH_LONG).show();
                return;
            }
            ;

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            photoUri = activity.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            activity.startActivityForResult(getImageByCamera, Choice_Cameia);
        } else {
            Toast.makeText(activity, "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }

    public ChoicePhotoManager.onChoiceFinishListener getOnChoiceFinishListener() {
        return onChoiceFinishListener;
    }

    public void setOnChoiceFinishListener(ChoicePhotoManager.onChoiceFinishListener onChoiceFinishListener) {
        this.onChoiceFinishListener = onChoiceFinishListener;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
