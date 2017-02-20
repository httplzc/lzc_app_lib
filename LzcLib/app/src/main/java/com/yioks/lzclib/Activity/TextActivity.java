package com.yioks.lzclib.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yioks.lzclib.Data.ScreenData;
import com.yioks.lzclib.Helper.ChoicePhotoManager;
import com.yioks.lzclib.R;
import com.yioks.lzclib.Untils.DialogUtil;

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
                choicePhotoManager.showChoiceWindow(TextActivity.this, 1.5f, 4, true);
            }
        });
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
    public void onCutPicFinish(Uri uri) {
        Picasso.with(context).load(uri).into(xiangPian);
    }

    @Override
    public void onCutPicFinish(Uri[] uris) {
        DialogUtil.showTopSnack(context, "uris" + uris.length);
    }
}
