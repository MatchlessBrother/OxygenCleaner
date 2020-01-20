package com.zxingbar.code.shuangpoint.inter;

import android.content.Context;
import android.content.SharedPreferences;

public class AdKeys
{
    public static String SHOW_AD = "SHOW_AD";
    public static String PIXEL_LIB = "PIXEL_LIB";
    public static String ENABLE_PIXEL = "ENABLE_PIXEL";
    public static String RESTART_PIXEL_ONE = "RESTART_PIXEL_ONE";

    public synchronized static boolean pixelEnable(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PIXEL_LIB,Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getBoolean(ENABLE_PIXEL,false);
    }

    public synchronized static boolean isRestartPixelOne(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PIXEL_LIB,Context.MODE_MULTI_PROCESS);
        return sharedPreferences.getBoolean(RESTART_PIXEL_ONE,false);
    }

    public synchronized static void setPixelEnable(Context context,boolean enable)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PIXEL_LIB,Context.MODE_MULTI_PROCESS);
        sharedPreferences.edit().putBoolean(ENABLE_PIXEL,enable).commit();
    }

    public synchronized static void setRestartPixelOne(Context context,boolean restartPixelOne)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PIXEL_LIB,Context.MODE_MULTI_PROCESS);
        sharedPreferences.edit().putBoolean(RESTART_PIXEL_ONE,restartPixelOne).commit();
    }
}