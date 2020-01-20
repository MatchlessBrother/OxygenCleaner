package com.zxingbar.code.shuangpoint;

import java.util.List;
import android.os.Build;
import android.util.Log;
import android.os.Bundle;
import java.util.ArrayList;
import com.zxingbar.code.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.app.Application;
import android.content.Context;
import io.reactivex.Observable;
import android.app.PendingIntent;
import android.app.ActivityManager;
import android.content.IntentFilter;
import java.util.concurrent.TimeUnit;
import android.graphics.BitmapFactory;
import io.reactivex.functions.Consumer;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import com.google.android.gms.ads.AdActivity;
import com.facebook.ads.AudienceNetworkActivity;
import com.zxingbar.code.shuangpoint.inter.AdKeys;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class PixelManager implements Application.ActivityLifecycleCallbacks
{
    private int mFrontCount;
    private boolean mOnePixelFront;
    private boolean mOnePixelClose;
    private boolean mAdActivityIsTop;
    private Application mApplication;
    private List<Activity> mActivities;
    private static final PixelManager ourInstance = new PixelManager();

    void goHome()
    {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        mApplication.startActivity(homeIntent);
    }

    void closeActivities()
    {
        for (Activity activity : mActivities)
        {
            try
            {
                activity.finish();
            }
            catch (Exception e)
            {
            }
        }
    }

    @SuppressLint("CheckResult")
    void startTwoPixelDelay()
    {
        Observable.timer(100,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>()
                {
                    public void accept(Long aLong) throws Exception
                    {
                        mApplication.startActivity(new Intent(mApplication, PixelTwo.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                });
    }

    public boolean isFront()
    {
        return mFrontCount > 0;

    }

    public void closePixel()
    {
        PixelTwo pixelTwo = PixelTwo.getsInstance();
        if (pixelTwo != null)
        {
            pixelTwo.finish();
        }

        PixelOne pixelOne = PixelOne.getInstance();
        if (pixelOne != null)
        {
            pixelOne.finish();
        }
    }

    boolean isOnePixelFront()
    {
        return mOnePixelFront;

    }

    boolean isOnePixelClose()
    {
        return mOnePixelClose;

    }

    @SuppressLint("CheckResult")
    void compatStartTwoPixel()
    {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
        {
            startTwoPixelDelay();
        }
        else
        {
            mApplication.startActivity(new Intent(mApplication, PixelTwo.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    boolean isAdActivityIsTop()
    {
        return mAdActivityIsTop;

    }

    private void onHomeAction()
    {
        Log.d("DoublePoint", "onHomeAction: ");
        closeActivities();
        if(AdKeys.pixelEnable(mApplication))
        {
            boolean pixelTwoAlive = false;
            PixelTwo pixelTwo = PixelTwo.getsInstance();
            if (pixelTwo != null)
            {
                pixelTwo.finish();
                pixelTwoAlive = true;
            }

            boolean isPixelOneFront = isOnePixelFront();
            boolean isAdTop = isAdActivityIsTop();
            PixelOne pixelOne = PixelOne.getInstance();
            if (pixelOne != null)
            {
                pixelOne.finish();//先关闭
            }

            if (pixelTwoAlive)
            {
                startOnePixelDelay(true);
            }
            else
            {
                startOnePixelDelay(!(isPixelOneFront || isAdTop));//点1不在前台并且广告不在前台起广告
            }
        }
        else
        {
            closePixel();
        }
    }

    private void onRecentAction()
    {
        Log.d("DoublePoint", "onReceive: onRecentAction " + mFrontCount);
        closeActivities();
        if (isFront())
        {
            Log.d("DoublePoint", "onRecentAction: go home");
            goHome();
            PixelOne pixelOne = PixelOne.getInstance();
            if (pixelOne != null)
            {
                pixelOne.finish();
                startOnePixelDelay(false);
            }
        }
        else
        {
            if(AdKeys.pixelEnable(mApplication))
            {
                PixelOne pixelOne = PixelOne.getInstance();
                if (pixelOne != null)
                {
                    Log.d("DoublePoint", "isStopValid: " + pixelOne.isStopValid() + "  isFront: " + isOnePixelFront());
                    if (pixelOne.isStopValid() || isOnePixelFront())
                    {
                        pixelOne.finish();
                        goHome();
                        startOnePixelDelay(false);
                        return;
                    }
                }

                PixelTwo pixelTwo = PixelTwo.getsInstance();
                if (pixelTwo != null)
                {
                    pixelTwo.finish();
                    goHome();
                    startOnePixelDelay(true);
                }
                else
                {
                    if(!isAdActivityIsTop())
                    {
                        compatStartTwoPixel();
                    }
                }
            }
            else
            {
                closePixel();
            }
        }
    }

    void startOnePixel(boolean showAd)
    {
        Intent intent = new Intent(mApplication, PixelOne.class);
        intent.putExtra(AdKeys.SHOW_AD, showAd);/****起广告*****/
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mApplication, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        try
        {
            pendingIntent.send();
        }
        catch (PendingIntent.CanceledException e)
        {
            e.printStackTrace();
            Log.d("DoublePoint", "onHomeAction: error");
        }
    }

    void setOnePixelFront(boolean front)
    {
        this.mOnePixelFront = front;

    }

    void setOnePixelClose(boolean close)
    {
        this.mOnePixelClose = close;

    }

    private void closeTwoAndRestartOne()
    {
        PixelTwo pixelTwo = PixelTwo.getsInstance();
        if (pixelTwo != null)
        {
            pixelTwo.finish();
        }
        PixelOne pixelOne = PixelOne.getInstance();
        if (pixelOne != null)
        {
            pixelOne.finish();//先关闭
        }
        startOnePixelDelay(false);
    }

    public static PixelManager getInstance()
    {
        return ourInstance;

    }

    public void init(Application application)
    {
        Log.d("DoublePoint", "init: ****************");
        mOnePixelClose = true;
        mApplication = application;
        mActivities = new ArrayList<>();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        mApplication.registerReceiver(new ExitBackgroundReceiver(), intentFilter);
        mApplication.registerActivityLifecycleCallbacks(this);/******************/
    }

    private boolean isFilter(Activity activity)
    {
        return activity instanceof PixelOne || activity instanceof PixelTwo;
    }

    @SuppressLint("CheckResult")
    void startOnePixelDelay(final boolean showAd)
    {
        Observable.timer(100,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>()
                {
                    public void accept(Long aLong) throws Exception
                    {
                        Log.d("DoublePoint", "Pixel 1 -> startOnePixelDelay");
                        startOnePixel(showAd);
                    }
                });
    }

    public void onActivityStarted(Activity activity)
    {
        if (activity instanceof AudienceNetworkActivity || activity instanceof AdActivity)
        {
            setAdActivityIsTop(true);
        }

        if (!isFilter(activity))
        {
            mFrontCount++;
            Log.d("DoublePoint", "onActivityStarted: front count " + mFrontCount);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            String label = "";
            Bitmap icon = BitmapFactory.decodeResource(mApplication.getResources(),R.mipmap.ic_launchern);
            activity.setTaskDescription(new ActivityManager.TaskDescription(label,icon));
        }
    }

    public void onActivityResumed(Activity activity)
    {
        if (activity instanceof AudienceNetworkActivity || activity instanceof AdActivity)
        {

        }
    }

    public void onActivityPaused(Activity activity)
    {
        if (activity instanceof AudienceNetworkActivity || activity instanceof AdActivity)
        {

        }
    }

    public void onActivityStopped(Activity activity)
    {
        if (activity instanceof AudienceNetworkActivity || activity instanceof AdActivity)
        {
            setAdActivityIsTop(false);
        }

        final boolean filter = isFilter(activity);
        Observable.timer(100,TimeUnit.MILLISECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<Long>()
        {
            public void accept(Long aLong) throws Exception
            {
                if (!filter)
                {
                    mFrontCount--;
                    Log.d("DoublePoint", "onActivityStopped: front count " + mFrontCount);
                }
            }
        });
    }

    void setAdActivityIsTop(boolean adActivityIsTop)
    {
        this.mAdActivityIsTop = adActivityIsTop;

    }

    public void onActivityDestroyed(Activity activity)
    {
        if(activity instanceof AudienceNetworkActivity || activity instanceof AdActivity)
        {
            if(AdKeys.isRestartPixelOne(activity))
            {
                Log.d("DoublePoint", "ad close and start pixel one");
                startOnePixel(false);
                AdKeys.setRestartPixelOne(activity,false);
            }
        }
        mActivities.remove(activity);

    }

    class ExitBackgroundReceiver extends BroadcastReceiver
    {
        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";//按下home键
        private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";//home键旁边的最近程序列表键

        @SuppressLint("CheckResult")
        public void onReceive(final Context context, Intent intent)
        {
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action))
            {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason))
                {
                    onHomeAction();
                }
                else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason))
                {
                    onRecentAction();
                }
            }
            else if (Intent.ACTION_SCREEN_OFF.equals(action))
            {
                closeActivities();
            }
        }
    }

    public void onActivityCreated(Activity activity, Bundle savedInstanceState)
    {
        if (!isFilter(activity))
        {
            mActivities.add(activity);
        }
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState)
    {

    }
}