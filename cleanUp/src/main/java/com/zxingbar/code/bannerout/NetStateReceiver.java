package com.zxingbar.code.bannerout;

import android.util.Log;
import android.content.Intent;
import android.content.Context;
import android.net.NetworkInfo;
import android.content.ComponentName;
import android.net.ConnectivityManager;
import android.content.BroadcastReceiver;

import static com.zxingbar.code.act.base.BaseConfig.isPrintLog;

public class NetStateReceiver extends BroadcastReceiver
{
    private boolean mIsConnect4G;
    private boolean mIsConnectWifi;
    private NetworkInfo mNetworkInfo;
    private static boolean mIsNotNeed;
    private ConnectivityManager mConnectivityManager;
    private static NetStateReceiver mInstance = new NetStateReceiver();
    public static final String CONNECTIVITY_ACTION = "android.net.CONNECTIVITY_CHANGE";

    private NetStateReceiver()
    {

    }

    public static void allowGetConfigInfos()
    {
        setIsNotNeed(false);

    }

    public static NetStateReceiver getInstance()
    {
        return mInstance;

    }

    public static void setIsNotNeed(boolean isNotNeed)
    {
        mIsNotNeed = isNotNeed;

    }

    public static void getConfigInfosImmediately(Context context)
    {
        setIsNotNeed(false);
        Intent intent = new Intent(NetStateReceiver.CONNECTIVITY_ACTION);
        context.getApplicationContext().sendBroadcast(intent);/*********/
    }

    public synchronized void onReceive(Context context, Intent intent)
    {
        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION) || intent.getAction().equals(CONNECTIVITY_ACTION))
        {
            mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            mIsConnect4G = false;mIsConnectWifi = false;
            if(null != mNetworkInfo)
            {
                switch(mNetworkInfo.getType())
                {
                    case ConnectivityManager.TYPE_WIFI:
                    {
                        mIsConnectWifi = mNetworkInfo.isConnected();
                        break;
                    }
                    case ConnectivityManager.TYPE_MOBILE:
                    {
                        mIsConnect4G = mNetworkInfo.isConnected();
                        break;
                    }
                }
            }
            if((mIsConnectWifi || mIsConnect4G) && !mIsNotNeed)
            {
                mIsNotNeed = true;
                Intent intt = new Intent();
                intt.setComponent(new ComponentName(context.getApplicationContext(), GetDefConfigInfo.class));
                context.getApplicationContext().startService(intt);/****************************************/
            }
            else if((mIsConnectWifi || mIsConnect4G) && mIsNotNeed)
            {
                if(isPrintLog)
                    Log.i("AdsNotes","已经成功获取过关于广告的配置信息故不再获取！");
            }
            else
            {
                if(isPrintLog)
                    Log.i("AdsNotes","抱歉,因无网状态导致无法获取关于广告的配置信息！");
            }
        }
    }
}