package com.yioks.lzclib.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yioks.lzclib.Data.ScreenData;
import com.yioks.lzclib.R;
import com.yioks.lzclib.Untils.FileUntil;
import com.yioks.lzclib.View.MoveImage;
import com.yioks.lzclib.View.PicCultBackground;

import java.io.File;

public class PicCultActivity extends AppCompatActivity {

    private TextView textView;
    private PicCultBackground picCultBackground;
    private MoveImage moveImage;
    private RelativeLayout relativeLayout;
    private float PicRealWidth;
    public static float PicRealHeight;
    public static final int CULT_PIC = 1320;
    private Bitmap bitmap;
    public float bitmapwidth;
    public float bitmapheight;
    private PressPicThread pressPicThread;
    private ProgressDialog progressDialog;
    private int finallyWidth;
    public static float backWidth;
    public static float backHeight;
    public static float bili;
    public static boolean is_circle = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cult_pic);
        initData();
        textView = (TextView) findViewById(R.id.title_txt_right);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    relativeLayout.setDrawingCacheEnabled(true);
                    relativeLayout.buildDrawingCache();
                    Bitmap bitmap = relativeLayout.getDrawingCache();
                    Bitmap realfinBitamp = Bitmap.createBitmap(bitmap, (int) (picCultBackground.getMleft()), (int) (picCultBackground.getMtop()), (int) (picCultBackground.getMwidth()), (int) (picCultBackground.getMheight()));
                    PicCultActivity.this.bitmap.recycle();
                    bitmap.recycle();
                    relativeLayout.setDrawingCacheEnabled(false);
                    SavePicThread savePicThread = new SavePicThread();
                    savePicThread.realfinBitamp = realfinBitamp;

                    savePicThread.start();
                    showProgressBar();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PicCultActivity.this, "裁剪失败", Toast.LENGTH_SHORT).show();
                    setResult(CULT_PIC, null);
                    finish();
                }
            }
        });
        picCultBackground = (PicCultBackground) findViewById(R.id.background);
        picCultBackground.setIs_circle(is_circle);
        moveImage = (MoveImage) findViewById(R.id.moveImage);
        Uri uri = getIntent().getData();

        if (uri == null) {
            setResult(CULT_PIC, null);
            finish();
        } else {
            finallyWidth = getIntent().getIntExtra("width", 1080);
            pressPicThread = new PressPicThread();
            pressPicThread.uri = uri;
            pressPicThread.start();
        }
        relativeLayout = (RelativeLayout) findViewById(R.id.finally_parent);
        showProgressBar();
    }

    public void showProgressBar() {
        progressDialog = new ProgressDialog(PicCultActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        progressDialog.setMessage("正在处理图片");
        progressDialog.show();
    }


    private void PreDealPic() {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        this.bitmapwidth = bitmapWidth;
        this.bitmapheight = bitmapHeight;
        moveImage.setImageBitmap(bitmap);

        if ((float) (bitmapWidth) / bitmapHeight - (ScreenData.widthPX - 40 * ScreenData.density) / (float) PicRealHeight > 0) {
            //宽占满
            float bili = (ScreenData.widthPX - 40 * ScreenData.density) / (float) bitmapWidth;
            if (bitmapHeight * bili < (ScreenData.widthPX - 40 * ScreenData.density)) {
                //放大至

                float bei = (ScreenData.widthPX - 40 * ScreenData.density) / (float) bitmapHeight;
                final Matrix matrix = new Matrix();
                matrix.postScale(bei * 1.01f, bei * 1.01f);
                matrix.postTranslate(20 * ScreenData.density - 0.1f, (PicRealHeight - (PicRealWidth - 40 * ScreenData.density)) / 2f - 0.1f);
                moveImage.setScaleType(ImageView.ScaleType.MATRIX);
                moveImage.setImageMatrix(matrix);
                moveImage.setMaxright(0);
                moveImage.setMaxtop(0);
                moveImage.setMaxbottom(0);
                moveImage.setMaxleft(bitmapWidth * bei - (PicRealWidth - 40 * ScreenData.density));

            } else {
                float bei = ScreenData.widthPX / (float) bitmapWidth;

                moveImage.setMaxleft(ScreenData.density * 20f);
                moveImage.setMaxtop((bitmapHeight * bei - (PicRealWidth - 40 * ScreenData.density)) / 2);
                moveImage.setMaxbottom((bitmapHeight * bei - (PicRealWidth - 40 * ScreenData.density)) / 2);

                moveImage.setMaxright(ScreenData.density * 20f);
            }
        } else {
            //高占满

            float bili = (PicRealHeight) / (float) bitmapHeight;
            if (bitmapWidth * bili < (ScreenData.widthPX - 40 * ScreenData.density)) {
                //放大至

                float bei = (ScreenData.widthPX - 40 * ScreenData.density) / (float) bitmapWidth;
                Matrix matrix = new Matrix();
                matrix.postScale(bei * 1.01f, bei * 1.01f);
                matrix.postTranslate(20 * ScreenData.density - 0.1f, (PicRealHeight - (PicRealWidth - 40 * ScreenData.density)) / 2f - 0.1f);
                moveImage.setScaleType(ImageView.ScaleType.MATRIX);
                moveImage.setImageMatrix(matrix);

                moveImage.setMaxright(0);
                moveImage.setMaxleft(0);
                moveImage.setMaxbottom(0);
                moveImage.setMaxtop(bitmapHeight * bei - (PicRealWidth - 40 * ScreenData.density));
            } else {
                float bei = PicRealHeight / bitmapHeight;
                moveImage.setMaxleft(ScreenData.density * 20f);
                moveImage.setMaxtop((bitmapHeight * bei - (ScreenData.widthPX - 40 * ScreenData.density)) / 2);
                moveImage.setMaxbottom((bitmapHeight * bei - (ScreenData.widthPX - 40 * ScreenData.density)) / 2);
                moveImage.setMaxright(ScreenData.density * 20f);
            }
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                bitmap = (Bitmap) msg.obj;
                if (bitmap == null || bitmap.isRecycled()) {
                    setResult(CULT_PIC, null);
                    Toast.makeText(PicCultActivity.this, "裁剪失败", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                PreDealPic();
                progressDialog.dismiss();

            } else if (msg.what == 1) {
                setResult(CULT_PIC, null);
                Toast.makeText(PicCultActivity.this, "裁剪失败", Toast.LENGTH_SHORT).show();
                finish();
            } else if (msg.what == 2) {
                String s = (String) msg.obj;
                if (s != null) {
                    Intent intent = new Intent();
                    intent.putExtra("filepath", s);
                    setResult(CULT_PIC, intent);
                    progressDialog.dismiss();
                }
                finish();
            }
            super.handleMessage(msg);
        }
    };


    private void initData() {
        bili = getIntent().getFloatExtra("bili", 1.0f);
        is_circle = getIntent().getBooleanExtra("is_circle", false);
        PicRealWidth = ScreenData.widthPX;
        PicRealHeight = ScreenData.heightPX - 50 * ScreenData.density - getStatusBarHeight();
        backWidth = PicRealWidth - 40 * ScreenData.density;
        backHeight = bili * backWidth;
        Log.i("lzc","ddddd"+PicRealHeight+"---"+"backHeight"+backHeight+"---"+bili+"---"+PicRealWidth);

    }


    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.KEYCODE_BACK) {
            setResult(CULT_PIC, null);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public float getBitmapheight() {
        return bitmapheight;
    }

    public void setBitmapheight(float bitmapheight) {
        this.bitmapheight = bitmapheight;
    }

    public float getBitmapwidth() {
        return bitmapwidth;
    }

    public void setBitmapwidth(float bitmapwidth) {
        this.bitmapwidth = bitmapwidth;
    }


    private class PressPicThread extends Thread {
        public Uri uri;

        @Override
        public void run() {
            try {
                bitmap = FileUntil.getBitmapFormUri(PicCultActivity.this, uri, 1080, 1920);
                if (bitmap != null && !bitmap.isRecycled()) {
                    if (FileUntil.readPictureDegree(FileUntil.UriToFile(uri, PicCultActivity.this)) == 90) {
                       bitmap = FileUntil.toturn(bitmap);
                    }
                }
                Message message = new Message();
                message.what = 0;
                message.obj = bitmap;
                handler.sendMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }
    }

    private class SavePicThread extends Thread {
        public Bitmap realfinBitamp;

        @Override
        public void run() {
            File file = FileUntil.WriteToTeamPic(PicCultActivity.this, realfinBitamp);
            realfinBitamp.recycle();
            String filepath = null;
            Log.i("lzc", "filepath" + (filepath == null));
            if (file != null) {
                filepath = file.getPath();
            }

            Message message = new Message();
            message.what = 2;
            message.obj = filepath;
            handler.sendMessage(message);
            progressDialog.dismiss();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
