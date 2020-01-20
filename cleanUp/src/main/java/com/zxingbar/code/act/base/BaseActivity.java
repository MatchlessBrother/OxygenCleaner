package com.zxingbar.code.act.base;

import android.util.Log;
import android.content.Intent;
import com.zxingbar.code.baner.BannerView;
import androidx.appcompat.app.AppCompatActivity;
import com.zxingbar.code.bannerout.NetStateReceiver;
import com.zxingbar.code.banerin.AvoidErrorManager;
import static com.zxingbar.code.act.base.BaseConfig.isLastPageForApp;
import static com.zxingbar.code.act.base.BaseConfig.finishAllActivity;

public class BaseActivity extends AppCompatActivity
{
    private Integer bannerViewId;
    protected BannerView bannerView;
    public static boolean mShowBannerAndInner = true;

    /********此方法必须用于Activity中onResume方法**********/
    /********以实现首次播放应用内广告和Banner广告**********/
    private void showBannerAndInner(BannerView bannerView)
    {
        if(mShowBannerAndInner)
        {
            mShowBannerAndInner = false;/**********************************************/
            Intent intent = new Intent(AvoidErrorManager.INNERMEMORYMANAGERACTION);/***/
            intent.putExtra(AvoidErrorManager.ISSWITCHADPLATFORMS,true);/******/
            intent.putExtra(AvoidErrorManager.ISSWITCHADPLATFORMSFORRETRY,true);
            if(null != bannerView)bannerView.loadAd();
            sendBroadcast(intent);
        }
    }

    protected void homePageInit(int bannerViewId)
    {
        this.bannerViewId = bannerViewId;
        bannerView = findViewById(bannerViewId);
        NetStateReceiver.getConfigInfosImmediately(this);
    }

    public void onBackPressed()
    {
        if(isLastPageForApp())
        {
            goToHome();
            finishAllActivity();
            mShowBannerAndInner = true;
        }
        else
        {
            super.onBackPressed();
        }
    }

    protected void goToHome()
    {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mainIntent.addCategory(Intent.CATEGORY_HOME);
        getApplication().startActivity(mainIntent);
    }

    protected void onResume()
    {
        super.onResume();
        if(null != bannerView)
            showBannerAndInner(bannerView);
        else
        {
            if(null != bannerViewId)
            {
                bannerView = findViewById(bannerViewId);
                if(null != bannerView)showBannerAndInner(bannerView);
            }
            else
            {
                Log.i("AdsNotes","因未能找到BannerView导致无法加载横幅广告!");
            }
        }
    }
}