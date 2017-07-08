package com.yioks.lzclib.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yioks.lzclib.Data.BigImgShowData;

/**
 * Created by ${User} on 2016/11/23 0023.
 */
public class LunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BigImgShowData bigImgShowData=new BigImgShowData();

        bigImgShowData.setData( "http://dev2015.yioks.org:32280/uploads/sircle_resources/images/9005f5de-50f2-11e7-ad39-000c29ccfba6_xxxImg.jpg");

       // bigImgShowData.setData("http://dev2015.yioks.org:32280/uploads/sircle_resources/images/f8abae54-4f52-11e7-ad39-000c29ccfba6_xxxImg.jpg");
        bigImgShowData.setNeedShowReal(true);
        ShowBigImgActivity.showBigImg(this,bigImgShowData);
    }
}
