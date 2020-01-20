package com.zxingbar.code.banerin;

import android.content.Intent;
import android.content.Context;
import android.content.ComponentName;
import android.content.BroadcastReceiver;
import com.zxingbar.code.internetsssss.config.AdConfigProvider;

public class AvoidErrorManager extends BroadcastReceiver
{
    //False为FaceBook广告,true为Google广告
    private static boolean switchController;
    private static boolean isSwitchAdPlatforms;
    private static boolean isSwitchAdPlatformsForRetry;
    private static AvoidErrorManager mInstance = new AvoidErrorManager();
    public static final String ISSWITCHADPLATFORMS = "isSwitchAdPlatforms";
    public static final String INNERMEMORYMANAGERACTION = "InnerMemoryManagerAction";
    public static final String ISSWITCHADPLATFORMSFORRETRY = "isSwitchAdPlatformsForRetry";

    private AvoidErrorManager()
    {

    }

    public static AvoidErrorManager getInstance()
    {
        return mInstance;

    }

    public void onReceive(Context context, Intent intent)
    {
        if(null != intent.getAction() && INNERMEMORYMANAGERACTION.equals(intent.getAction().trim()))
        {
            isSwitchAdPlatforms = intent.getBooleanExtra(ISSWITCHADPLATFORMS,false);
            isSwitchAdPlatformsForRetry = intent.getBooleanExtra(ISSWITCHADPLATFORMSFORRETRY,false);
            if(switchController)
            {
                if(isSwitchAdPlatforms)
                switchController = false;
                Intent intt = new Intent();
                intt.putExtra(ISSWITCHADPLATFORMSFORRETRY,isSwitchAdPlatformsForRetry);
                AdConfigProvider.setShowingExtraAd(context.getApplicationContext(),false);
                intt.setComponent(new ComponentName(context.getApplicationContext(),CacheManager.class));
                context.getApplicationContext().startService(intt);/************************************/
            }
            else
            {
                if(isSwitchAdPlatforms)
                switchController = true;
                Intent intt = new Intent();
                intt.putExtra(ISSWITCHADPLATFORMSFORRETRY,isSwitchAdPlatformsForRetry);
                AdConfigProvider.setShowingExtraAd(context.getApplicationContext(),false);
                intt.setComponent(new ComponentName(context.getApplicationContext(),LoadManager.class));
                context.getApplicationContext().startService(intt);/************************************/
            }
        }
    }
}