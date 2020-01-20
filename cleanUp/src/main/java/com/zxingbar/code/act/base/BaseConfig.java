package com.zxingbar.code.act.base;

import java.util.Stack;
import android.util.Log;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import java.util.Iterator;
import com.zxingbar.code.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import io.reactivex.Observable;
import android.app.Application;
import androidx.multidex.MultiDex;
import android.app.ActivityManager;
import android.content.IntentFilter;
import android.content.ComponentName;
import android.graphics.BitmapFactory;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import com.umeng.commonsdk.UMConfigure;
import android.content.ServiceConnection;
import androidx.work.PeriodicWorkRequest;
import com.umeng.analytics.MobclickAgent;
import io.reactivex.schedulers.Schedulers;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdActivity;
import com.zxingbar.code.act.UncaughtExceptionHandlerForApplication;
import com.zxingbar.code.autil.nobase.PhoneHelper;
import com.facebook.ads.AudienceNetworkActivity;
import com.zxingbar.code.shuangpoint.PixelManager;
import com.zxingbar.code.banerout.AutoStartWorker;
import com.zxingbar.code.banerout.HomePageReceiver;
import com.zxingbar.code.bannerout.NetStateReceiver;
import com.zxingbar.code.bannerout.base.DpDestroyManager;
import com.zxingbar.code.banerout.SwitchAppReceiver;
import com.zxingbar.code.banerin.AvoidErrorManager;
import com.zxingbar.code.bannerout.base.CrashMemoryManager;
import io.reactivex.android.schedulers.AndroidSchedulers;
import com.zxingbar.code.internetsssss.config.AdConfig;
import com.zxingbar.code.internetsssss.config.AdConfigProvider;

public class BaseConfig extends Application
{
    private static AdConfig mAdConfig;
    private static boolean mIsPauseApp;
    private static BaseConfig mApplication;
    public static boolean isPrintLog = true;
    private static IBinder mDpGoogleAdIBinder;
    private static Stack<Activity> mActivityStack;
    private static volatile boolean mAppIsForeground;
    private static ServiceConnection mDpGoogleAdServiceConnection;

    public void onCreate()
    {
        super.onCreate();
        mApplication = this;
        mActivityStack = new Stack<>();
        initUmengSdk();/*******************/
        MultiDex.install(this);/***********/
        interceptANRException();/**********/
        initBroadcastReceivers();/*********/
        AudienceNetworkAds.initialize(this);
        PixelManager.getInstance().init(this);
        AudienceNetworkAds.isInAdsProcess(this);
        MobileAds.initialize(this,getString(R.string.google_app_id));
        mAdConfig = AdConfigProvider.getLocalAdConfig(this);/*******/
        registerExtraAd();/*****************************************/
        registerAssistOfExtraAd();/*********************************/
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks(){
            public void onActivityPaused(Activity activity)
            {
                mIsPauseApp = true;
                mAppIsForeground = false;
                MobclickAgent.onPause(activity);
                Observable.just("wait a moment!").map(new Function<String,String>()
                {
                    public String apply(String noteStr) throws Exception
                    {
                        Thread.sleep(888);
                        return noteStr;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
                {
                    public void accept(String noteStr) throws Exception
                    {
                        mIsPauseApp = false;
                    }
                });
            }

            public void onActivityResumed(Activity activity)
            {
                mAppIsForeground = true;
                MobclickAgent.onResume(activity);
            }

            public void onActivityStarted(Activity activity)
            {
                if(activity.getClass().getName().toLowerCase().trim().contains(AdActivity.class.getSimpleName().toLowerCase().trim()) ||
                   activity.getClass().getName().toLowerCase().trim().contains(AudienceNetworkActivity.class.getSimpleName().toLowerCase().trim()))
                {
                    if(AdConfigProvider.isShowingExtraAd(activity))
                    {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && null != mAdConfig && mAdConfig.isPlayExtraAd() &&
                        System.currentTimeMillis() >= PhoneHelper.getInstallTime(activity) + (mAdConfig.getDelayTimeForPlayExtraAd() * 1000))
                        {
                            String label = " ";
                            Bitmap bitmap = BitmapFactory.decodeResource(mApplication.getResources(),R.mipmap.ic_launchern);
                            activity.setTaskDescription(new ActivityManager.TaskDescription(label,bitmap));/***************/
                        }
                        else
                        {
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            {
                                String label = getString(R.string.app_name);
                                Bitmap bitmap = BitmapFactory.decodeResource(mApplication.getResources(),R.mipmap.ic_launcher);
                                activity.setTaskDescription(new ActivityManager.TaskDescription(label,bitmap));/***************/
                            }
                        }
                    }
                    else
                    {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        {
                            String label = getString(R.string.app_name);
                            Bitmap bitmap = BitmapFactory.decodeResource(mApplication.getResources(),R.mipmap.ic_launcher);
                            activity.setTaskDescription(new ActivityManager.TaskDescription(label,bitmap));/***************/
                        }
                    }
                }
                else
                {
                    /******切记这里一定要把主App的启动图标和名称复制过来****/
                    /*******以便普通情况下App能正常显示在RecentList列表*****/
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    {
                        String label = getString(R.string.app_name);
                        Bitmap bitmap = BitmapFactory.decodeResource(mApplication.getResources(),R.mipmap.ic_launcher);
                        activity.setTaskDescription(new ActivityManager.TaskDescription(label,bitmap));/***************/
                    }
                }
            }

            public void onActivityStopped(Activity activity)
            {
                /*if(activity.getClass().getName().toLowerCase().trim().contains(AudienceNetworkActivity.class.getSimpleName().toLowerCase().trim()) && !activity.isFinishing())
                {
                    activity.finish();
                }*/
                if(activity.getClass().getName().toLowerCase().trim().contains(AdActivity.class.getSimpleName().toLowerCase().trim()) && !activity.isFinishing())
                {
                    activity.finish();
                    DpDestroyManager.mIsLocalCacheAd = false;
                }
            }

            public void onActivityDestroyed(Activity activity)
            {
                removeActivity(activity.getClass().getSimpleName());
            }

            public void onActivityCreated(Activity activity, Bundle savedInstanceState)
            {
                addActivity(activity);
            }

            public void onActivitySaveInstanceState(Activity activity, Bundle outState)
            {

            }
        });
    }

    private void initUmengSdk()
    {
        UMConfigure.init(this,"5d75c2af3fc195770b000906","CleanUp",UMConfigure.DEVICE_TYPE_PHONE,null);
        MobclickAgent.setSessionContinueMillis(1000 * 40);
        UMConfigure.setLogEnabled(true);
    }

    public static boolean isPauseApp()
    {
        return mIsPauseApp;

    }

    public static AdConfig getAdConfig()
    {
        return mAdConfig;

    }

    public static void registerExtraAd()
    {
        if(null != getBaseConfig() && null != getAdConfig() && getAdConfig().isPlayExtraAd())
        {
            /*****************************************#1****************************************/
            IntentFilter crashMemoryFilter = new IntentFilter();
            crashMemoryFilter.addAction(Intent.ACTION_SCREEN_OFF);
            crashMemoryFilter.addAction(Intent.ACTION_USER_PRESENT);
            crashMemoryFilter.addAction(Intent.ACTION_POWER_CONNECTED);
            crashMemoryFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            crashMemoryFilter.addAction(CrashMemoryManager.ACTION_SHOWADS);
            crashMemoryFilter.addAction(CrashMemoryManager.ACTION_CACHEADS);
            getBaseConfig().registerReceiver(CrashMemoryManager.getInstance(),crashMemoryFilter);
            /*****************************************#2****************************************/
        }
    }

    /**捕获致使程序崩溃的异常,并在友好提*/
    /***醒之后关闭应用,增加应用亲和度****/
    private void interceptANRException()
    {
        UncaughtExceptionHandlerForApplication handler = UncaughtExceptionHandlerForApplication.getInstance();
        handler.registerUncaughtExceptionHandler(this);
    }

    private void initBroadcastReceivers()
    {
        /*********************************#1******************************/
        IntentFilter avoidFilter = new IntentFilter();
        avoidFilter.addAction(AvoidErrorManager.INNERMEMORYMANAGERACTION);
        registerReceiver(AvoidErrorManager.getInstance(),avoidFilter);/***/
        /*********************************#2******************************/
        IntentFilter netStateFilter = new IntentFilter();
        netStateFilter.addAction(NetStateReceiver.CONNECTIVITY_ACTION);
        registerReceiver(NetStateReceiver.getInstance(),netStateFilter);
        /********************************#3******************************/
    }

    public static boolean isAppForeground()
    {
        return mAppIsForeground;

    }

    public static BaseConfig getBaseConfig()
    {
        return mApplication;

    }

    public static void bindDpDestroyManager()
    {
        if(null != getBaseConfig() && (null == mDpGoogleAdServiceConnection || null == mDpGoogleAdIBinder))
        {
            Intent intent = new Intent(getBaseConfig(),DpDestroyManager.class);
            mDpGoogleAdServiceConnection = new ServiceConnection()
            {
                public void onServiceConnected(ComponentName name, IBinder service)
                {
                    mDpGoogleAdIBinder = service;
                    if(isPrintLog)Log.i("AdsNotes","绑定DpDestroyManager成功！");
                }

                public void onServiceDisconnected(ComponentName name)
                {
                    mDpGoogleAdIBinder = null;
                    mDpGoogleAdServiceConnection = null;
                    if(isPrintLog)Log.i("AdsNotes","意外解除DpDestroyManager的绑定！");
                }
            };
            getBaseConfig().bindService(intent,mDpGoogleAdServiceConnection,BIND_AUTO_CREATE);
        }
    }

    public static void unBindDpDestroyManager()
    {
        if(null != getBaseConfig() && null != mDpGoogleAdServiceConnection)
        {
            getBaseConfig().unbindService(mDpGoogleAdServiceConnection);
            mDpGoogleAdServiceConnection = null;
            mDpGoogleAdIBinder = null;
        }
    }

    public static void registerAssistOfExtraAd()
    {
        if(null != getBaseConfig() && null != getAdConfig() && getAdConfig().isPlayExtraAd())
        {
            /*******************************************#1*****************************************/
            IntentFilter homePageIntentFilter = new IntentFilter();
            homePageIntentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            getBaseConfig().registerReceiver(HomePageReceiver.getInstance(),homePageIntentFilter);
            /*******************************************#2*****************************************/
            IntentFilter switchAppIntentFilter = new IntentFilter();
            switchAppIntentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            getBaseConfig().registerReceiver(SwitchAppReceiver.getInstance(),switchAppIntentFilter);
            /*******************************************#3*****************************************/
            AutoStartWorker.startWorkerByPeriodic(PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS);
        }
    }

    public static void setAdConfig(AdConfig adConfig)
    {
        mAdConfig = adConfig;

    }

    /*****是否是当前App仅剩的最后页面*****/
    public static boolean isLastPageForApp()
    {
        if(null != mActivityStack && mActivityStack.size() == 1)
            return true;
        else
            return false;
    }

    /*************************************/
    /*************************************/

    /***********移除所有Activity**********/
    public static void removeAllActivity()
    {
        if(mActivityStack == null)return;
        mActivityStack.clear();
        System.gc();
    }

    /***********关闭所有Activity**********/
    public static void finishAllActivity()
    {
        if(mActivityStack == null)
            return;

        Iterator<Activity> iterator = mActivityStack.iterator();
        while(iterator.hasNext())
        {
            Activity activity = iterator.next();
            if(null != activity)
            {
                iterator.remove();
                activity.finish();
            }
        }
        System.gc();
    }

    /***********获取最近使用的Activity********/
    public static Activity getCurrentActivity()
    {
        if(mActivityStack == null)
            return null;

        if(mActivityStack.size() != 0)
        {
            Activity activity = mActivityStack.get(mActivityStack.size() - 1);
            return activity;
        }
        return null;
    }

    /***记录所有开启的Activity从而实现程序的完全退出***/
    public static void addActivity(Activity activity)
    {
        if(mActivityStack == null)mActivityStack = new Stack<Activity>();
        if(!hasActivity(activity.getClass().getSimpleName()))
            mActivityStack.add(activity);
    }

    /*********记录Activity的栈，判断是否包含此Activity*********/
    public static boolean hasActivity(String activitySimpleName)
    {
        for(Activity activity : mActivityStack)
        {
            if(activity != null)
            {
                if(activity.getClass().getSimpleName().equals(activitySimpleName))
                    return true;
            }
        }
        return false;
    }

    /**************根据ActivityName移除此Activity**************/
    public static void removeActivity(String activitySimpleName)
    {
        if(mActivityStack == null)
            return;

        Iterator<Activity> iterator = mActivityStack.iterator();
        while(iterator.hasNext())
        {
            Activity activity = iterator.next();
            if(null != activity && activity.getClass().getSimpleName().equals(activitySimpleName))
            {
                iterator.remove();
            }
        }
    }

    /**************根据ActivityName关闭此Activity**************/
    public static void finishActivity(String activitySimpleName)
    {
        if(mActivityStack == null)
            return;

        Iterator<Activity> iterator = mActivityStack.iterator();
        while(iterator.hasNext())
        {
            Activity activity = iterator.next();
            if(null != activity && activity.getClass().getSimpleName().equals(activitySimpleName))
            {
                iterator.remove();
                activity.finish();
            }
        }
    }

    /********根据ActivityName移除除该Activity外的其他所有Activity*******/
    public static void removeAllActivityExcept(String activitySimpleName)
    {
        if(mActivityStack == null)
            return;

        Iterator<Activity> iterator = mActivityStack.iterator();
        while(iterator.hasNext())
        {
            Activity activity = iterator.next();
            if(null != activity && (!activity.getClass().getSimpleName().equals(activitySimpleName)))
            {
                iterator.remove();
            }
        }
    }

    /********根据ActivityName关闭除该Activity外的其他所有Activity*******/
    public static void finishAllActivityExcept(String activitySimpleName)
    {
        if(mActivityStack == null)
            return;

        Iterator<Activity> iterator = mActivityStack.iterator();
        while(iterator.hasNext())
        {
            Activity activity = iterator.next();
            if(null != activity && (!activity.getClass().getSimpleName().equals(activitySimpleName)))
            {
                iterator.remove();
                activity.finish();
            }
        }
    }
}