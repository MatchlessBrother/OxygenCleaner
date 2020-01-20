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

/***********当用户切换到桌面时系统发出的广播***********/
public class HomePageReceiver extends BroadcastReceiver
{
    private static final String SYSTEM_REASON = "reason";
    private static final String SYSTEM_HOME_KEY = "home";
    private static HomePageReceiver mInstance = new HomePageReceiver();
    private static final long AvailableIimeIntervalOfHomeKeyEvent = 200l;
    private static final String TAG = HomePageReceiver.class.getSimpleName();
    private static long AvailableIimeOfHomeKeyEvent = System.currentTimeMillis();

    private HomePageReceiver()
    {

    }

    public static HomePageReceiver getInstance()
    {
        return mInstance;

    }

    public synchronized void onReceive(Context context, Intent intent)
    {
        if(null != intent && null != intent.getAction() && intent.getAction().trim().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS) &&
        null != intent.getStringExtra(SYSTEM_REASON) && intent.getStringExtra(SYSTEM_REASON).toLowerCase().trim().contains(SYSTEM_HOME_KEY) &&
        AvailableIimeOfHomeKeyEvent <= System.currentTimeMillis() && (BaseConfig.isPauseApp() || BaseConfig.isAppForeground()) && null != BaseConfig.getAdConfig() && BaseConfig.getAdConfig().isPlayExtraAd())
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
                    AvailableIimeOfHomeKeyEvent = System.currentTimeMillis() + AvailableIimeIntervalOfHomeKeyEvent;
                }
            });
        }
    }
}