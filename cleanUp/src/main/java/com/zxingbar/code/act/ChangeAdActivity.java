package com.zxingbar.code.act;

import android.os.Bundle;
import android.view.Window;
import android.app.Activity;
import android.view.Gravity;
import android.content.Intent;
import android.content.Context;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import android.graphics.PixelFormat;
import android.content.ComponentName;
import com.zxingbar.code.bannerout.base.PlayManager;
import com.zxingbar.code.bannerout.base.DestroyManager;

public class ChangeAdActivity extends Activity
{
    public static final int GOOGLE_ADS = 0x0001;
    public static final int FACEBOOK_ADS = 0x0002;
    public static final String TYPE_ADS = "type_ads";

    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Window window = getWindow();/****************/
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.format = PixelFormat.TRANSPARENT;params.x = 0;params.y = 0;params.width = 1;params.height = 1;
        params.windowAnimations = android.R.style.Animation_Translucent;window.setAttributes(params);changeAds();
    }

    public static void changeFaceBookAdToGoogleAd(Context context)
    {
        Intent intent = new Intent(context,ChangeAdActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);/***/
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);/*****/
        intent.putExtra(TYPE_ADS,GOOGLE_ADS);/***************/
        context.getApplicationContext().startActivity(intent);
    }

    public static void changeGoogleAdToFaceBookAd(Context context)
    {
        Intent intent = new Intent(context,ChangeAdActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);/***/
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);/*****/
        intent.putExtra(TYPE_ADS,FACEBOOK_ADS);/*************/
        context.getApplicationContext().startActivity(intent);
    }

    private void changeAds()
    {
        int typeValue = getIntent().getIntExtra(TYPE_ADS,FACEBOOK_ADS);
        switch(typeValue)
        {
            case GOOGLE_ADS:
            {
                Intent intt = new Intent();
                intt.setComponent(new ComponentName(getApplicationContext(),DestroyManager.class));
                getApplicationContext().startService(intt);/**************************************/
                break;
            }
            case FACEBOOK_ADS:
            {
                Intent intt = new Intent();
                intt.setComponent(new ComponentName(getApplicationContext(),PlayManager.class));
                getApplicationContext().startService(intt);/***************************************/
                break;
            }
        }
        finish();
    }

    protected void onPause()
    {
        super.onPause();
        finish();
    }
}