package com.zxingbar.code.banerout;

import android.content.Intent;
import android.content.Context;
import android.content.BroadcastReceiver;
import com.zxingbar.code.act.base.BaseConfig;

public class AutoStartReceiver extends BroadcastReceiver
{
    public void onReceive(final Context context, Intent intent)
    {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) || intent.getAction().equals(Intent.ACTION_LOCKED_BOOT_COMPLETED)||
           intent.getAction().equals(Intent.ACTION_PACKAGE_DATA_CLEARED) || intent.getAction().equals(Intent.ACTION_PACKAGE_RESTARTED))
        {
            BaseConfig.registerExtraAd();
            BaseConfig.registerAssistOfExtraAd();
        }
    }
}