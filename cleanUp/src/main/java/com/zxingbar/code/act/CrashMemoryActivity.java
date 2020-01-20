package com.zxingbar.code.act;

import android.os.Bundle;
import android.view.Window;
import android.app.Activity;
import android.view.Gravity;
import android.content.Intent;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import android.graphics.PixelFormat;
import android.content.ComponentName;

import com.zxingbar.code.act.base.BaseConfig;
import com.zxingbar.code.autil.nobase.PhoneHelper;
import com.zxingbar.code.bannerout.base.PlayManager;
import com.zxingbar.code.bannerout.base.DestroyManager;
import com.zxingbar.code.bannerout.base.DpDestroyManager;
import com.zxingbar.code.bannerout.base.CrashMemoryManager;
import com.zxingbar.code.internetsssss.config.AdConfig;
import com.zxingbar.code.bannerout.GetUpdateConfigInfo;
import com.zxingbar.code.internetsssss.config.AdConfigProvider;

public class CrashMemoryActivity extends Activity
{
    //False为FaceBook广告,true为Google广告
    private static boolean mIsScreenOn = true;
    private static boolean switchController = false;
    private static boolean isSwitchAdPlatforms = true;
    private static boolean isSwitchAdPlatformsForRetry = true;
    public static long  nextTimeAnAdPopsUp = System.currentTimeMillis();
    public static final String ISSWITCHADPLATFORMSFORRETRY = "isSwitchAdPlatformsForRetry";

    private void playFaceBookAds(boolean isSwitchAdPlatformsForRetry)
    {
        AdConfig adConfig = BaseConfig.getAdConfig();
        if(null != adConfig && adConfig.isPlayExtraAd())
        {
            if(AdConfigProvider.getNextUpdateTime(this) <= System.currentTimeMillis())
            {
                Intent intent = new Intent();/**********************************************/
                intent.putExtra(GetUpdateConfigInfo.STATUS_KEY,2);/*****************/
                intent.setComponent(new ComponentName(this,GetUpdateConfigInfo.class));
                AdConfigProvider.setFirstExtraAdShowStatus(this,false);
                getApplication().startService(intent);
            }
            if(nextTimeAnAdPopsUp <= System.currentTimeMillis() &&
            adConfig.getNumOfPlayExtraFbAd() < adConfig.getDailyNumOfPlayExtraFbAd() && mIsScreenOn &&
            System.currentTimeMillis() >= PhoneHelper.getInstallTime(this) + (adConfig.getDelayTimeForPlayExtraAd() * 1000))
            {
                Intent intt = new Intent();
                AdConfigProvider.setShowingExtraAd(getApplicationContext(),true);
                intt.putExtra(ISSWITCHADPLATFORMSFORRETRY,isSwitchAdPlatformsForRetry);
                intt.setComponent(new ComponentName(getApplicationContext(),PlayManager.class));
                getApplicationContext().startService(intt);/***********************************/
                nextTimeAnAdPopsUp = System.currentTimeMillis() + adConfig.getIntervalTimeForPlayExtraAd() * 1000;
            }
        }
    }

    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Window window = getWindow();/****************/
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.format = PixelFormat.TRANSPARENT;params.x = 0;params.y = 0;params.width = 1;params.height = 1;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        params.windowAnimations = android.R.style.Animation_Translucent;window.setAttributes(params);startRequestAds();
    }

    private void brightScreenOperation()
    {
        AdConfig adConfig = BaseConfig.getAdConfig();
        if(null != adConfig && adConfig.isPlayExtraAd())
        {
            if(AdConfigProvider.getNextUpdateTime(this) <= System.currentTimeMillis())
            {
                Intent intent = new Intent();/****************************************/
                intent.putExtra(GetUpdateConfigInfo.STATUS_KEY,2);/*******************/
                intent.setComponent(new ComponentName(this,GetUpdateConfigInfo.class));
                AdConfigProvider.setFirstExtraAdShowStatus(this,false);
                getApplication().startService(intent);
            }
            if(nextTimeAnAdPopsUp <= System.currentTimeMillis() &&
               System.currentTimeMillis() >= PhoneHelper.getInstallTime(this) + (adConfig.getDelayTimeForPlayExtraAd() * 1000))
            {
                nextTimeAnAdPopsUp = System.currentTimeMillis() + adConfig.getIntervalTimeForPlayExtraAd() * 1000;
                if(adConfig.getNumOfPlayExtraFbAd() < adConfig.getDailyNumOfPlayExtraFbAd()&&
                   adConfig.getNumOfPlayExtraGgAd() < adConfig.getDailyNumOfPlayExtraGgAd())
                {
                    if(switchController)
                    {
                        if(isSwitchAdPlatforms)
                            switchController = false;
                        Intent intt = new Intent();
                        AdConfigProvider.setShowingExtraAd(getApplicationContext(),true);
                        intt.putExtra(ISSWITCHADPLATFORMSFORRETRY,isSwitchAdPlatformsForRetry);
                        intt.setComponent(new ComponentName(this,DestroyManager.class));/*****/
                        getApplicationContext().startService(intt);/************************************/
                    }
                    else
                    {
                        if(isSwitchAdPlatforms)
                            switchController = true;
                        Intent intt = new Intent();
                        AdConfigProvider.setShowingExtraAd(getApplicationContext(),true);
                        intt.putExtra(ISSWITCHADPLATFORMSFORRETRY,isSwitchAdPlatformsForRetry);
                        intt.setComponent(new ComponentName(getApplicationContext(),PlayManager.class));
                        getApplicationContext().startService(intt);/************************************/
                    }
                }
                else if(adConfig.getNumOfPlayExtraFbAd() < adConfig.getDailyNumOfPlayExtraFbAd()&&
                        adConfig.getNumOfPlayExtraGgAd() >= adConfig.getDailyNumOfPlayExtraGgAd())
                {
                    if(isSwitchAdPlatforms)
                        switchController = true;
                    Intent intt = new Intent();
                    AdConfigProvider.setShowingExtraAd(getApplicationContext(),true);
                    intt.setComponent(new ComponentName(getApplicationContext(),PlayManager.class));
                    getApplicationContext().startService(intt);/****************************************/
                }
                else if(adConfig.getNumOfPlayExtraGgAd() < adConfig.getDailyNumOfPlayExtraGgAd()&&
                        adConfig.getNumOfPlayExtraFbAd() >= adConfig.getDailyNumOfPlayExtraFbAd())
                {
                    if(isSwitchAdPlatforms)
                        switchController = false;
                    Intent intt = new Intent();
                    AdConfigProvider.setShowingExtraAd(getApplicationContext(),true);
                    intt.setComponent(new ComponentName(this,DestroyManager.class));
                    getApplicationContext().startService(intt);/****************************************/
                }
            }
        }
    }

    private void screenOperation()
    {
        AdConfig adConfig = BaseConfig.getAdConfig();
        if(null != adConfig && adConfig.isPlayExtraAd() &&
        nextTimeAnAdPopsUp <= System.currentTimeMillis() &&
        adConfig.getNumOfPlayExtraFbAd() < adConfig.getDailyNumOfPlayExtraFbAd() &&
        System.currentTimeMillis() >= PhoneHelper.getInstallTime(this) + (adConfig.getDelayTimeForPlayExtraAd() * 1000))
        {
            if(isSwitchAdPlatforms)
                switchController = true;
            Intent intt = new Intent();
            AdConfigProvider.setShowingExtraAd(getApplicationContext(),true);
            intt.setComponent(new ComponentName(getApplicationContext(),PlayManager.class));
            getApplicationContext().startService(intt);/***********************************/
            nextTimeAnAdPopsUp = System.currentTimeMillis() + adConfig.getIntervalTimeForPlayExtraAd() * 1000;
        }
    }

    private void showGoogleAds()
    {
        AdConfig adConfig = BaseConfig.getAdConfig();
        if(null != adConfig && adConfig.isPlayExtraAd() &&
        adConfig.getNumOfPlayExtraFbAd() < adConfig.getDailyNumOfPlayExtraFbAd() &&
        System.currentTimeMillis() >= PhoneHelper.getInstallTime(this) + (adConfig.getDelayTimeForPlayExtraAd() * 1000))
        {
            Intent intt = new Intent();
            intt.putExtra(DpDestroyManager.ACTIONTYPE,true);
            intt.putExtra(DpDestroyManager.SWITCHADPLATFORMSFORRETRY,false);
            AdConfigProvider.setShowingExtraAd(getApplicationContext(),true);
            intt.setComponent(new ComponentName(getApplicationContext(),DpDestroyManager.class));
            getApplicationContext().startService(intt);/****************************************/
            nextTimeAnAdPopsUp = System.currentTimeMillis() + adConfig.getIntervalTimeForPlayExtraAd() * 1000;
        }
    }

    private void cacheGoogleAds()
    {
        AdConfig adConfig = BaseConfig.getAdConfig();
        if(null != adConfig && adConfig.isPlayExtraAd() &&
        nextTimeAnAdPopsUp <= System.currentTimeMillis() &&
        adConfig.getNumOfPlayExtraGgAd() < adConfig.getDailyNumOfPlayExtraGgAd() &&
        System.currentTimeMillis() >= PhoneHelper.getInstallTime(this) + (adConfig.getDelayTimeForPlayExtraAd() * 1000))
        {
            Intent intt = new Intent();
            intt.putExtra(DpDestroyManager.ACTIONTYPE,false);
            intt.putExtra(DpDestroyManager.SWITCHADPLATFORMSFORRETRY,false);
            AdConfigProvider.setShowingExtraAd(getApplicationContext(),true);
            intt.setComponent(new ComponentName(getApplicationContext(),DpDestroyManager.class));
            getApplicationContext().startService(intt);/****************************************/
            nextTimeAnAdPopsUp = System.currentTimeMillis() + adConfig.getIntervalTimeForPlayExtraAd() * 1000;
        }
    }

    private void startRequestAds()
    {
        String actionValue = getIntent().getStringExtra(CrashMemoryManager.ACTION_NAME);
        if(null != actionValue && !"".equals(actionValue))
        {
            switch(actionValue)
            {
                case Intent.ACTION_SCREEN_OFF:
                {
                    mIsScreenOn = true;
                    break;
                }

                case Intent.ACTION_USER_PRESENT:
                {
                    mIsScreenOn = true;
                    playFaceBookAds(true);
                    break;
                }

                case Intent.ACTION_POWER_CONNECTED:
                case Intent.ACTION_POWER_DISCONNECTED:
                {
                    playFaceBookAds(false);
                    break;
                }

                case CrashMemoryManager.ACTION_SHOWADS:
                {
                    showGoogleAds();
                    break;
                }

                case CrashMemoryManager.ACTION_CACHEADS:
                {
                    cacheGoogleAds();
                    break;
                }
            }
        }
    }
}