package com.zxingbar.code.autil;

import android.os.Build;
import android.os.Parcelable;
import android.content.Intent;
import android.content.Context;
import io.reactivex.Observable;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.content.pm.ShortcutInfo;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import android.content.BroadcastReceiver;
import android.content.SharedPreferences;
import android.content.pm.ShortcutManager;
import com.zxingbar.code.act.base.BaseConfig;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ShortcutIconUtils
{
    /*******************************************生成快捷图标******************************************/
    public static void createShortcutIcon(final Context context,final int appIcon,final String appName)
    {
        Observable.just("waite a moment!").map(new Function<String,String>()
        {
            public String apply(String noteStr) throws Exception
            {
                Thread.sleep(2000);
                return noteStr;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
        {
            public void accept(String s)
            {
                if(BaseConfig.isAppForeground())
                {
                    ComponentName componentName = new ComponentName(context.getPackageName(),"com.overall.cleanup.pags.OverallAct_Wel");
                    Intent intent = new Intent();/****/
                    intent.setComponent(componentName);
                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
                        addShortcutIcon(context,appIcon,appName,intent);
                    else
                        addShortcutIcon(context,appIcon,appName,intent,componentName);
                }
            }
        });
    }

    /*****************************************生成快捷图标老方式****************************************/
    private static void addShortcutIcon(Context context,int appIcon,String appName,Intent shortcutIntent)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(),Context.MODE_MULTI_PROCESS);
        if(!sharedPreferences.getBoolean("IconsHaveBeenCreated",false))
        {
            Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            intent.putExtra("duplicate", false);/*************************************/
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,appName);/*********************/
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,shortcutIntent);/************/
            sharedPreferences.edit().putBoolean("IconsHaveBeenCreated",true).apply();//
            Parcelable iconParcelable = Intent.ShortcutIconResource.fromContext(context,appIcon);
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,iconParcelable);/***************/
            context.getApplicationContext().sendBroadcast(intent);/*****************************/
        }
    }

    /*****************************************生成快捷图标新方式********************************************************************/
    private static void addShortcutIcon(Context context,int appIcon,String appName,Intent shortcutIntent,ComponentName componentName)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(),Context.MODE_MULTI_PROCESS);
        if(!sharedPreferences.getBoolean("IconsHaveBeenCreated",false))
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                ShortcutManager shortcutManager = (ShortcutManager)context.getSystemService(Context.SHORTCUT_SERVICE);
                if(shortcutManager != null && shortcutManager.isRequestPinShortcutSupported())
                {
                    shortcutIntent.putExtra("duplicate",false);
                    shortcutIntent.setAction(Intent.ACTION_VIEW);
                    ShortcutInfo info = new ShortcutInfo.Builder(context,"app_id").setIntent(shortcutIntent).setActivity(componentName).
                    setIcon(Icon.createWithBitmap(BitmapFactory.decodeResource(context.getResources(),appIcon))).setShortLabel(appName).build();
                    PendingIntent shortcutCallbackIntent = PendingIntent.getBroadcast(context,0,new Intent(context,BroadcastReceiver.class),PendingIntent.FLAG_UPDATE_CURRENT);
                    shortcutManager.requestPinShortcut(info,shortcutCallbackIntent.getIntentSender());sharedPreferences.edit().putBoolean("IconsHaveBeenCreated",true).apply();
                }
            }
        }
    }
}