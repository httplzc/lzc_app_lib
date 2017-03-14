package com.yioks.lzclib.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yioks.lzclib.Data.BigImgShowData;
import com.yioks.lzclib.Data.ScreenData;
import com.yioks.lzclib.Helper.ChoicePhotoManager;
import com.yioks.lzclib.R;
import com.yioks.lzclib.Untils.DialogUtil;

import java.util.Arrays;

public class TextActivity extends TitleBaseActivity implements ChoicePhotoManager.onChoiceFinishListener {

    private ImageView xiangPian;
    private Button button;
    private ChoicePhotoManager choicePhotoManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        setTitleState();
        bindTitle(true, "asd", -1);
        ScreenData.init_srceen_data(this);
        xiangPian = (ImageView) findViewById(R.id.xiangPian);

        button = (Button) findViewById(R.id.xiangji);
        choicePhotoManager = new ChoicePhotoManager(this);
        choicePhotoManager.setOnChoiceFinishListener(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChoicePhotoManager.Option option = new ChoicePhotoManager.Option();
                choicePhotoManager.showChoiceWindow(TextActivity.this, 9, option);
//                WebActivity.showWeb(context,new WebActivity.Data("http://www.hao123.com","测试"));
//                pressPic();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        choicePhotoManager.unRegisterReceiver();
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        choicePhotoManager.onCultActivityResultDo(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        choicePhotoManager.onCultRequestPermissionsResultDo(requestCode, permissions, grantResults);
    }

    @Override
    public void onCutPicFinish(final Uri uri) {
        Picasso.with(context).load(uri).into(xiangPian);
        xiangPian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BigImgShowData bigImgShowData = new BigImgShowData();
                bigImgShowData.setData(uri, new BigImgShowData.MessageUri(xiangPian));
                ShowBigImgActivity.showBigImg(context, bigImgShowData);
            }
        });
    }

    @Override
    public void onCutPicFinish(Uri[] uris) {
        DialogUtil.showTopSnack(context, "uris" + uris.length);
//        ShowBigImgDialog showBigImgDialog = new ShowBigImgDialog();
        BigImgShowData bigImgShowData = new BigImgShowData();
//        List<String>stringList=new ArrayList<>();
//        stringList.add("http://upload-images.jianshu.io/upload_images/3136836-3843449df980703d.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240");
//        stringList.add("http://upload-images.jianshu.io/upload_images/3136836-0ac2bbca87ede716.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240");
//        stringList.add("http://upload-images.jianshu.io/upload_images/3136836-6a57d48bdfb3f24a.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240");
//        stringList.add("http://upload-images.jianshu.io/upload_images/3136836-fa17425a3b82164d.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240");
        bigImgShowData.setUriList(Arrays.asList(uris));
//        showBigImgDialog.setBigImgShowData(bigImgShowData);
//        showBigImgDialog.show(getSupportFragmentManager(), "");
//        Intent intent=new Intent();
//        intent.setClass(this,ShowBigImgActivity.class);
//        startActivity(intent);
        ShowBigImgActivity.showBigImg(context, bigImgShowData);
    }


}
