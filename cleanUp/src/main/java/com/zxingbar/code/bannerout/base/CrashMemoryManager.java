package com.zxingbar.code.bannerout.base;

import android.util.Log;
import android.content.Intent;
import android.content.Context;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import com.zxingbar.code.act.base.BaseConfig;
import com.zxingbar.code.autil.nobase.PhoneHelper;
import com.zxingbar.code.shuangpoint.inter.AdKeys;
import com.zxingbar.code.shuangpoint.PixelManager;
import com.zxingbar.code.act.CrashMemoryActivity;
import com.zxingbar.code.internetsssss.config.AdConfig;

import static com.zxingbar.code.act.base.BaseConfig.getBaseConfig;
import static com.zxingbar.code.act.base.BaseConfig.isPrintLog;

public class CrashMemoryManager extends BroadcastReceiver
{
    public static final String ACTION_NAME = "action_name";
    public static final String ACTION_SHOWADS = "action_showads";
    public static final String ACTION_CACHEADS = "action_cacheads";
    private static CrashMemoryManager mInstance = new CrashMemoryManager();

    private CrashMemoryManager()
    {

    }

    public static CrashMemoryManager getInstance()
    {
        return mInstance;

    }

    public void onReceive(Context context,Intent intent)
    {
        processDetail(context,intent);

    }

    private void turnOnTwoPointMechanism(Context context)
    {
        AdConfig adConfig = BaseConfig.getAdConfig();
        if(null != adConfig && adConfig.isPlayExtraAd() &&
        CrashMemoryActivity.nextTimeAnAdPopsUp <= System.currentTimeMillis() &&
        adConfig.getNumOfPlayExtraGgAd() < adConfig.getDailyNumOfPlayExtraGgAd() && !AdKeys.pixelEnable(context) &&
        System.currentTimeMillis() >= PhoneHelper.getInstallTime(context) + (adConfig.getDelayTimeForPlayExtraAd() * 1000))
        {
            BaseConfig.bindDpDestroyManager();
            AdKeys.setPixelEnable(context,true);
            if(isPrintLog)Log.i("AdsNotes","开启应用外双点机制！");
            AdKeys.setRestartPixelOne(context.getApplicationContext(),true);
        }
        else if(AdKeys.pixelEnable(context))
        {
            if(isPrintLog)Log.i("AdsNotes","关闭应用外双点机制！");
            AdKeys.setPixelEnable(context,false);
            BaseConfig.unBindDpDestroyManager();
        }
    }

    private void turnOffTwoPointMechanism(Context context)
    {
        if(AdKeys.pixelEnable(context))
        {
            if(isPrintLog)Log.i("AdsNotes","关闭应用外双点机制！");
            AdKeys.setPixelEnable(context,false);
            BaseConfig.unBindDpDestroyManager();
        }
    }

    private void processDetail(Context context,Intent intent)
    {
        if(null != intent && null != intent.getAction())
        {
            /****双点机制衔接部分****/
            switch(intent.getAction())
            {
                case Intent.ACTION_SCREEN_OFF:
                {
                    PixelManager.getInstance().closePixel();
                    turnOffTwoPointMechanism(context.getApplicationContext());
                    break;
                }
                case Intent.ACTION_USER_PRESENT:
                {
                    turnOnTwoPointMechanism(context.getApplicationContext());
                    break;
                }
            }
            /**************************************************************************************/
            try
            {
                if(getBaseConfig().hasActivity(CrashMemoryActivity.class.getSimpleName()))
                    getBaseConfig().finishActivity(CrashMemoryActivity.class.getSimpleName());
                Intent intt = new Intent(context.getApplicationContext(),CrashMemoryActivity.class);
                intt.putExtra(ACTION_NAME,intent.getAction());/************************************/
                intt.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK   |  Intent.FLAG_ACTIVITY_NO_ANIMATION);
                PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(),0,intt,PendingIntent.FLAG_ONE_SHOT);
                pendingIntent.send();
            }
            catch (PendingIntent.CanceledException e)
            {
                e.printStackTrace();
            }
        }
    }
}