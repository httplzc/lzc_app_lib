package com.yioks.lzclib.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.unity3d.player.UnityPlayer;
import com.yioks.lzclib.R;
import com.yioks.lzclib.Untils.HttpUtil;
import com.yioks.lzclib.Untils.StringManagerUtil;
import com.yioks.lzclib.View.MyUnity;
import com.yioks.lzclib.View.ParentView;

import java.io.File;
import java.io.Serializable;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Administrator on 2016/8/5 0005.
 */
public class U3dPlayActivity extends AppCompatActivity {
    private FrameLayout frameLayout;
    private ParentView parentView;
    public MyUnity mUnityPlayer;
    private boolean unity_status = false;
    private boolean net_dowfile = false;
    private String play_name;
    public boolean haveError = false;
    private File file;
    Handler handler = new Handler();
    private boolean is_res_start = false;
    private Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("lzc", "oncreate1");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        this.getWindow().setFormat(2);
        setContentView(R.layout.activity_u3d_play);
        initIntentData();
        parentView = (ParentView) findViewById(R.id.parent_view);
        if (data == null) {
            parentView.setstaus(ParentView.Staus.Error);
            return;
        }

        is_res_start = false;
        this.mUnityPlayer = new MyUnity(this);
        frameLayout = (FrameLayout) findViewById(R.id.u3d_parent);

        parentView.getLoadding_list().add("第一次加载可能有点慢……");
        parentView.getLoadding_list().add("请耐心等待……");
        parentView.setError_text("该教案不能看了~~~");
        haveError = false;
        Log.i("lzc", "oncreate2");
        parentView.setReFreshDataListener(new ParentView.ReFreshDataListener() {
            @Override
            public void refreshData() {
                haveError = false;
                parentView.resetProgress();
                parentView.setstaus(ParentView.Staus.Loading);
                try {
                    LoadData();
                } catch (Exception e) {
                    Log.i("lzc", "e11" + e.toString());
                    e.printStackTrace();
                    parentView.setstaus(ParentView.Staus.Error);
                }
            }
        });


        try {
            LoadData();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("lzc", "e" + e.toString());
            parentView.setstaus(ParentView.Staus.Error);
        }
    }

    public static void playU3d(Context context, Data data) {
        Intent intent = new Intent();
        intent.setClass(context, U3dPlayActivity.class);
        intent.putExtra("data", data);
        context.startActivity(intent);
    }

    public static class Data implements Serializable {
        public String url = "";
        public String chapter = "0";
        public int mode = 0;
        public String id = "";

        public Data(int mode, String url, String chapter, String id) {
            this.mode = mode;
            this.url = url;
            this.chapter = chapter;
            this.id = id;
        }

        public Data() {
        }
    }

    /**
     * 初始化数据
     */
    private void initIntentData() {
        data = (Data) getIntent().getSerializableExtra("data");
        if (data == null) {
            Uri uri = getIntent().getData();
            if (uri == null)
                return;
            String url = uri.getQueryParameter("url");
            String mode = uri.getQueryParameter("mode");
            if (StringManagerUtil.CheckNull(url) || StringManagerUtil.CheckNull(mode) || !StringManagerUtil.VerifyNumber(mode))
                return;
            data = new Data();
            data.url = url;
            data.mode = Integer.valueOf(mode);
            if (StringManagerUtil.CheckNull(uri.getQueryParameter("chapter")))
                return;
            data.chapter = uri.getQueryParameter("chapter");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i("lzc", "onNewIntent");
        setIntent(intent);
        initIntentData();
        haveError = false;
        parentView.setVisibility(View.VISIBLE);
        parentView.setstaus(ParentView.Staus.Loading);
        is_res_start = true;
        parentView.setProgressNoAnim(60);
        try {
            LoadData();
        } catch (Exception e) {
            e.printStackTrace();
            parentView.setstaus(ParentView.Staus.Error);
        }
    }

    @Override
    protected void onRestart() {
        Log.i("lzc", "onRestart");
        super.onRestart();
    }


    private void LoadData() throws Exception {

        if (data.url == null || data.url.trim().equals("")) {
            parentView.setstaus(ParentView.Staus.Error);
            return;
        }
        this.net_dowfile = false;

        if (data.mode == 0) {
            File dir = new File(getExternalFilesDir(null).getPath() + "/XmlData");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file = new File(dir.getPath() + "/u3ddata" + ".xml");
        } else {
            File dir = new File(getExternalFilesDir(null).getPath() + "/AnimationData");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            file = new File(dir.getPath() + "/u3ddata" + ".assetbundle");
        }

        if (file.exists()) {
            boolean b = file.delete();
        }
        file.createNewFile();
        Log.i("lzc", "url" + data.url);
        HttpUtil.download(data.url, new FileAsyncHttpResponseHandler(file) {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                parentView.setstaus(ParentView.Staus.Error);
                net_dowfile = false;
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, File file) {
                Log.i("lzc", "filename" + file.getPath());
                if (is_res_start) {
                    parentView.setProgress(80);
                } else {
                    parentView.setProgress(80, 4000);
                }

                net_dowfile = true;
                callU3dLoad();
                if (mUnityPlayer.getParent() == null) {
                    frameLayout.addView(mUnityPlayer);
                }
            }
        });
    }

    private void setRead() {

    }

    @Override
    protected void onDestroy() {
        HttpUtil.cancelAllClient(U3dPlayActivity.this);
        if (mUnityPlayer.getParent() != null) {
            try {
                this.mUnityPlayer.quit();
            } catch (Exception ex) {
            }
        }
        this.unity_status = false;
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            callU3dClose();
            return true;
        }
        return this.mUnityPlayer.injectEvent(keyEvent);
    }


    protected void onPause() {
        super.onPause();
        if (mUnityPlayer.getParent() != null) {
            this.mUnityPlayer.pause();
        }

    }

    protected void onResume() {
        super.onResume();
        this.mUnityPlayer.resume();
    }

    public void onConfigurationChanged(Configuration var1) {
        super.onConfigurationChanged(var1);
        this.mUnityPlayer.configurationChanged(var1);
    }

    public void onWindowFocusChanged(boolean var1) {
        super.onWindowFocusChanged(var1);
        this.mUnityPlayer.windowFocusChanged(var1);
    }

    public boolean dispatchKeyEvent(KeyEvent var1) {
        return var1.getAction() == 2 ? this.mUnityPlayer.injectEvent(var1) : super.dispatchKeyEvent(var1);
    }

    public boolean onKeyUp(int var1, KeyEvent var2) {
        return this.mUnityPlayer.injectEvent(var2);
    }


    public boolean onTouchEvent(MotionEvent var1) {
        return this.mUnityPlayer.injectEvent(var1);
    }

    public boolean onGenericMotionEvent(MotionEvent var1) {
        return this.mUnityPlayer.injectEvent(var1);
    }


    private void callU3dLoad() {
        Log.i("lzc", "loading" + unity_status + "---" + net_dowfile);
        if (!this.unity_status) return;

        if (!this.net_dowfile) return;

        if (play_name != null && !play_name.equals("")) {

            handler.post(new Runnable() {
                @Override
                public void run() {
                    parentView.setProgress(90);
                }
            });
            if (data.mode == 0) {
                Log.i("lzc", "chapter" + data.chapter);
                String message = file.getName().replace(".xml", "") + "|" + data.chapter;
                Log.i("lzc", "mypath" + message);
                UnityPlayer.UnitySendMessage(play_name, "API_LoodXmlScend", message);
            } else {
                String filename = file.getName().replace(".assetbundle", "");
                UnityPlayer.UnitySendMessage(play_name, "API_LoodAnimation", filename + "|" + data.chapter);
            }


        }
    }

    private void callU3dClose() {
        HttpUtil.cancelAllClient(U3dPlayActivity.this);
        UnityPlayer.UnitySendMessage(play_name, "API_StopLoadXml", "");
        moveTaskToBack(true);
    }


    /*
    后退
     */
    public void unity_break() {
        Log.i("lzc", "BACK");
        callU3dClose();
    }

    /**
     * 初始化完成
     */
    public void unity_init() {
        Log.i("lzc", "init");
        unity_status = true;
        callU3dLoad();

    }

    /*
        开始加载
     */
    public void unity_loadingstart() {
//        Log.i("lzc", "start");
//        parentView.setProgress(50);
    }

    /**
     * 加载完毕
     */
    public void unity_loadingend() {
        if (haveError) {
            return;
        }
        Log.i("lzc", "end");
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.i("lzc", "normal");
                parentView.setProgress(100, 500);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        parentView.setstaus(ParentView.Staus.Normal);
                    }
                }, 500);
            }
        });

    }

    public void unity_message(String str) {
        Log.i("lzc", "message:" + str);
    }

    public void unity_error(final String error) {
        haveError = true;
        handler.post(new Runnable() {
            @Override
            public void run() {
                parentView.setstaus(ParentView.Staus.Error);
                Log.i("lzc", "error");
                //  DialogUtil.ShowToast(U3dPlayActivity.this, "" + error);
            }
        });


    }

    public void unity_setdplaytype(String id) {
        play_name = id;
        Log.i("lzc", "id" + id);
    }


}
