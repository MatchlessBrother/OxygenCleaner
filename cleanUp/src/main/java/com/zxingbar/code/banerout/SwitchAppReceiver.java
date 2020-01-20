package com.zxingbar.code.banerout;

import android.content.Intent;
import android.content.Context;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import android.content.BroadcastReceiver;
import com.zxingbar.code.act.base.BaseConfig;
import io.reactivex.schedulers.Schedulers;
import com.zxingbar.code.act.base.BaseActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import static com.zxingbar.code.act.base.BaseConfig.finishAllActivity;

/***********当用户切换到RecentApps页面时自动跳转到桌面**********/
public class SwitchAppReceiver extends BroadcastReceiver
{
    private static final String SYSTEM_REASON = "reason";
    private static final String SYSTEM_RECENT_APPS = "recent";
    private static final String SYSTEM_LONG_RECENT_APPS = "assist";
    private static final long AvailableIimeIntervalOfSwitchApp = 200l;
    private static SwitchAppReceiver mInstance = new SwitchAppReceiver();
    private static final long AvailableIimeIntervalOfLongSwitchApp = 200l;
    private static final String TAG = SwitchAppReceiver.class.getSimpleName();
    private static long AvailableIimeOfSwitchApp = System.currentTimeMillis();
    private static long AvailableIimeOfLongSwitchApp = System.currentTimeMillis();

    private SwitchAppReceiver()
    {

    }

    public static SwitchAppReceiver getInstance()
    {
        return mInstance;

    }

    public synchronized void onReceive(final Context context, Intent intent)
    {
        if(null != intent && null != intent.getAction() && intent.getAction().trim().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS) &&
        null != intent.getStringExtra(SYSTEM_REASON) && (BaseConfig.isPauseApp() || BaseConfig.isAppForeground()) && null != BaseConfig.getAdConfig() && BaseConfig.getAdConfig().isPlayExtraAd())
        {
            /**********************************点击SwitchApp按钮************************************/
            if(AvailableIimeOfSwitchApp <= System.currentTimeMillis() && intent.
            getStringExtra(SYSTEM_REASON).toLowerCase().trim().contains(SYSTEM_RECENT_APPS))
            {
                Observable.just("waite a moment!").map(new Function<String,String>()
                {
                    public String apply(String noteStr) throws Exception
                    {
                        BaseActivity.mShowBannerAndInner = true;
                        finishAllActivity();
                        Thread.sleep(400);
                        return noteStr;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
                {
                    public void accept(String noteStr) throws Exception
                    {
                        Intent homeIntent = new Intent();
                        homeIntent.setAction(Intent.ACTION_MAIN);
                        homeIntent.addCategory(Intent.CATEGORY_HOME);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.getApplicationContext().startActivity(homeIntent);
                        AvailableIimeOfSwitchApp = System.currentTimeMillis() + AvailableIimeIntervalOfSwitchApp;
                    }
                });
            }
            /***********************************长按SwitchApp按钮***********************************/
            else if(AvailableIimeOfLongSwitchApp <= System.currentTimeMillis() && intent.
            getStringExtra(SYSTEM_REASON).toLowerCase().trim().contains(SYSTEM_LONG_RECENT_APPS))
            {
                Observable.just("waite a moment!").map(new Function<String,String>()
                {
                    public String apply(String noteStr) throws Exception
                    {
                        BaseActivity.mShowBannerAndInner = true;
                        finishAllActivity();
                        Thread.sleep(400);
                        return noteStr;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
                {
                    public void accept(String s) throws Exception
                    {
                        Intent homeIntent = new Intent();
                        homeIntent.setAction(Intent.ACTION_MAIN);
                        homeIntent.addCategory(Intent.CATEGORY_HOME);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.getApplicationContext().startActivity(homeIntent);
                        AvailableIimeOfLongSwitchApp = System.currentTimeMillis() + AvailableIimeIntervalOfLongSwitchApp;
                    }
                });
            }
        }
    }
}