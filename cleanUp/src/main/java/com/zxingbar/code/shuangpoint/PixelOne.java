package com.zxingbar.code.shuangpoint;

import android.util.Log;
import android.os.Bundle;
import android.view.Window;
import android.view.Gravity;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import io.reactivex.Observable;
import android.os.PowerManager;
import android.view.WindowManager;
import android.graphics.PixelFormat;
import java.util.concurrent.TimeUnit;
import io.reactivex.functions.Consumer;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import com.zxingbar.code.act.base.BaseConfig;
import io.reactivex.disposables.Disposable;
import com.zxingbar.code.bannerout.base.CrashMemoryManager;
import com.zxingbar.code.shuangpoint.inter.AdKeys;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class PixelOne extends Activity
{
    private boolean mStop;
    private boolean mStopValid;
    private boolean mPressHome;
    private boolean mPressBack;
    private Disposable mDisposable;
    private static PixelOne sInstance;

    private void onOpen()
    {
        Log.d("DoublePoint", "Pixel 1 onOpen");
        mStop = false;mPressBack = false;mPressHome = false;
        PixelManager.getInstance().setOnePixelClose(false);
    }

    protected void onResume()
    {
        super.onResume();
        PixelManager.getInstance().setOnePixelFront(true);
    }

    protected void onPause()
    {
        super.onPause();
        PixelManager.getInstance().setOnePixelFront(false);
    }

    public boolean isStopValid()
    {
        return mStopValid;

    }

    @SuppressLint("CheckResult")
    protected void onStart()
    {
        super.onStart();
        Log.d("DoublePoint", "Pixel 1 -> onStart");
        if (PixelManager.getInstance().isAdActivityIsTop())
        {

        }
        else if(getIntent().getBooleanExtra(AdKeys.SHOW_AD, false))
        {
            Log.d("DoublePoint", "SHOW_AD");
            Observable.timer(200L, TimeUnit.MILLISECONDS).subscribe(new Consumer<Long>()
            {
                public void accept(Long aLong) throws Exception
                {
                    sendBroadcast(new Intent(CrashMemoryManager.ACTION_SHOWADS));
                }
            });
        }
        else
        {
            if (mStop)
            {
                Observable.timer(1, TimeUnit.SECONDS).subscribe(new Consumer<Long>()
                {
                    public void accept(Long aLong) throws Exception
                    {
                        sendBroadcast(new Intent(CrashMemoryManager.ACTION_SHOWADS));
                    }
                });
            }
        }
        mStop = false;
        mStopValid = false;
    }

    @SuppressLint("CheckResult")
    protected void onStop()
    {
        super.onStop();
        Log.d("DoublePoint", "Pixel 1 -> onStop");
        if (!isScreenOn())
        {
            Log.d("DoublePoint", "isScreenOn: ");
            if (!isFinishing())
            {
                finish();
            }
        }
        else if (PixelManager.getInstance().isAdActivityIsTop())
        {
            Log.d("DoublePoint", "isAdActivityIsTop: ");
        }
        else
        {
            mStop = true;
            mStopValid = true;
            disposeStopValidTimer();
            mDisposable = Observable.timer(100, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Long>()
            {
                public void accept(Long time) throws Exception
                {
                    Log.d("DoublePoint", "Pixel 1 -> isStopValid: false");
                    if (!mPressBack && !mPressHome)
                    {
                        sendBroadcast(new Intent(CrashMemoryManager.ACTION_CACHEADS));
                    }
                    mPressBack = false;
                    mPressHome = false;
                    mStopValid = false;
                }
            });
        }
    }

    protected void onDestroy()
    {
        super.onDestroy();
        sInstance = null;
        disposeStopValidTimer();
        Log.d("DoublePoint", "Pixel 1 onDestroy");
        PixelManager.getInstance().setOnePixelClose(true);
    }

    public boolean isScreenOn()
    {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        Log.d("DoublePoint", "Pixel 1 -> isScreenOn " + powerManager.isScreenOn());
        return powerManager.isScreenOn();
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        Log.i("DoublePoint","pixel1 back");mPressBack = true;
        PixelManager.getInstance().startOnePixelDelay(false);
        BaseConfig.removeActivity(BaseConfig.getCurrentActivity().getClass().getSimpleName());
        //if(null != BaseConfig.getCurrentActivity())BaseConfig.removeActivity(BaseConfig.getCurrentActivity().getClass().getSimpleName());
    }

    private void disposeStopValidTimer()
    {
        if (mDisposable != null && !mDisposable.isDisposed())
        {
            mDisposable.dispose();
        }
    }

    public static PixelOne getInstance()
    {
        return sInstance;

    }

    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);/***************/
        Log.d("DoublePoint", "Pixel 1 onNewIntent");
        setIntent(intent);
        onOpen();
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sInstance = this;Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        params.format = PixelFormat.TRANSPARENT;params.x = 0;params.y = 0;params.height = 1;params.width = 1;/************/
        params.windowAnimations = android.R.style.Animation_Translucent;/*************************************************/
        window.setAttributes(params);Log.d("DoublePoint", "Pixel 1 onCreate");onOpen();/**********************************/
    }

    class HomeWatcherReceiver extends BroadcastReceiver
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
                if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason) || SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason))
                {
                    mPressHome = true;
                }
            }
        }
    }
}