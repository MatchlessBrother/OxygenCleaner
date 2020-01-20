package com.zxingbar.code.banerin;

import android.util.Log;
import android.os.IBinder;
import android.app.Service;
import com.zxingbar.code.R;
import io.reactivex.Observer;
import android.content.Intent;
import io.reactivex.Observable;
import android.app.PendingIntent;
import android.content.ComponentName;
import java.util.concurrent.TimeUnit;
import io.reactivex.functions.Consumer;
import com.zxingbar.code.act.base.BaseConfig;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.disposables.Disposable;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdActivity;
import com.google.android.gms.ads.InterstitialAd;
import io.reactivex.android.schedulers.AndroidSchedulers;
import static com.zxingbar.code.act.base.BaseConfig.isPrintLog;

public class CacheManager extends Service
{
    private int mTotalRetryCount;
    private int mCurrentRetryCount;
    private String mInterstitialAdPlacementId;
    private static volatile boolean mIsRunning;
    public static InterstitialAd mInterstitialAd;
    public static AdListener mInterstitialAdListener;
    private static boolean mIsSwitchAdPlatformsForRetry;
    public static final long mAdIntervalTime = 0l;//毫秒
    public static final long mAdRetryIntervalTime = 15000l;//毫秒

    private void initDatas(Intent intent)
    {
        if(null != intent)mIsSwitchAdPlatformsForRetry = intent.getBooleanExtra(AvoidErrorManager.ISSWITCHADPLATFORMSFORRETRY,false);
        if(isPrintLog)Log.i("AdsNotes","应用内Google广告初始化！");
        if(null == mInterstitialAdListener){
            mInterstitialAdListener = new AdListener()
            {
                public void onAdLoaded()
                {
                    super.onAdLoaded();
                    if(isPrintLog)
                        Log.i("AdsNotes","成功请求到应用内Google广告资源！");
                    if(null != mInterstitialAd && !mInterstitialAd.isLoading() && mInterstitialAd.isLoaded())
                    {
                        if(BaseConfig.isAppForeground())
                        {
                            mInterstitialAd.show();
                        }
                        else
                        {
                            if(isPrintLog)
                                Log.i("AdsNotes","当前App没有显示在前台导致不能显示应用内Google广告！");
                            mIsRunning = false;
                        }
                    }
                    else
                    {
                        if(isPrintLog && null == mInterstitialAd)
                            Log.i("AdsNotes","mInterstitialAd为Null导致不能显示应用内Google广告！");
                        else if(isPrintLog && mInterstitialAd.isLoading())
                            Log.i("AdsNotes","即将显示的应用内Google广告正在加载中导致无法立即显示广告！");
                        else if(isPrintLog && !mInterstitialAd.isLoaded())
                            Log.i("AdsNotes","即将显示的应用内Google广告没有加载完毕导致无法立即显示广告！");
                        mIsRunning = false;
                    }
                }

                public void onAdOpened()
                {
                    super.onAdOpened();
                    if(isPrintLog)
                        Log.i("AdsNotes","应用内Google广告正在展示！");
                }

                public void onAdClosed()
                {
                    super.onAdClosed();
                    if(isPrintLog)
                        Log.i("AdsNotes","关闭应用内Google广告页面！");
                    mIsRunning = false;
                }

                public void onAdClicked()
                {
                    super.onAdClicked();
                    if(isPrintLog)
                        Log.i("AdsNotes","点击应用内Google广告页面！");
                }

                public void onAdImpression()
                {
                    super.onAdImpression();
                    if(isPrintLog)
                        Log.i("AdsNotes","记录应用内Google广告即将展示！");
                }

                public void onAdLeftApplication()
                {
                    super.onAdLeftApplication();
                    if(isPrintLog)
                        Log.i("AdsNotes","从应用内Google广告页面跳转到第三方应用！");
                    mIsRunning = false;
                }

                public void onAdFailedToLoad(int errorCode)
                {
                    super.onAdFailedToLoad(errorCode);
                    if(isPrintLog)
                    {
                        switch(errorCode)
                        {
                            case AdRequest.ERROR_CODE_NO_FILL:Log.i("AdsNotes","请求应用内Google广告发生错误：ERROR_CODE_NO_FILL");break;
                            case AdRequest.ERROR_CODE_NETWORK_ERROR:Log.i("AdsNotes","请求应用内Google广告发生错误：ERROR_CODE_NETWORK_ERROR");break;
                            case AdRequest.ERROR_CODE_INTERNAL_ERROR:Log.i("AdsNotes","请求应用内Google广告发生错误：ERROR_CODE_INTERNAL_ERROR");break;
                            case AdRequest.ERROR_CODE_INVALID_REQUEST:Log.i("AdsNotes","请求应用内Google广告发生错误：ERROR_CODE_INVALID_REQUEST");break;
                        }
                    }
                    if(mCurrentRetryCount > 0)
                    {
                        mCurrentRetryCount--;
                        if(isPrintLog)
                            Log.i("AdsNotes","等待?秒后发起应用内Google广告重试！");
                        Observable.just("Wait ? Second!").doOnNext(new Consumer<String>()
                        {
                            public void accept(String str) throws Exception
                            {
                                Thread.sleep(mAdRetryIntervalTime);
                            }
                        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
                        {
                            public void accept(String s) throws Exception
                            {
                                if(null != mInterstitialAd)
                                {
                                    if(BaseConfig.isAppForeground())
                                    {
                                        if(isPrintLog)
                                            Log.i("AdsNotes","发起应用内Google广告重试！");
                                        mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                    }
                                    else
                                    {
                                        if(isPrintLog)
                                            Log.i("AdsNotes","App没有显示在前台故无法重试应用内Google广告！");
                                        mIsRunning = false;
                                    }
                                }
                                else
                                {
                                    if(mIsSwitchAdPlatformsForRetry)
                                    {
                                        if(isPrintLog)
                                            Log.i("AdsNotes","mInterstitialAd为Null导致无法重试应用内Google广告故重试应用内FaceBook广告！");
                                        Intent intent = new Intent();
                                        intent.setComponent(new ComponentName(
                                        CacheManager.this.getApplication(),LoadManager.class));
                                        CacheManager.this.getApplication().startService(intent);
                                    }
                                    else
                                    {
                                        if(isPrintLog)
                                            Log.i("AdsNotes","mInterstitialAd为Null导致无法重试应用内Google广告！");
                                    }
                                    mIsRunning = false;
                                }
                            }
                        });
                    }
                    else
                    {
                        if(mIsSwitchAdPlatformsForRetry)
                        {
                            if(isPrintLog)
                                Log.i("AdsNotes","应用内Google广告重试次数已用完故重试应用内FaceBook广告！");
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName(
                            CacheManager.this.getApplication(),LoadManager.class));
                            CacheManager.this.getApplication().startService(intent);
                        }
                        else
                        {
                            if(isPrintLog)
                                Log.i("AdsNotes","应用内Google广告重试次数已用完故无法重试！");
                        }
                        mIsRunning = false;
                    }
                }
            };
        }/******************************/
        mInterstitialAdPlacementId = getString(R.string.google_in_placement_id);
        if(null == mInterstitialAd){
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(mInterstitialAdPlacementId);
            mInterstitialAd.setImmersiveMode(true);/***************/
        }/*********************/
        mInterstitialAd.setAdListener(mInterstitialAdListener);
        mCurrentRetryCount = mTotalRetryCount = 1;
    }

    public void onDestroy()
    {
        super.onDestroy();
        destroy();
    }

    public static void destroy()
    {
        if(null != mInterstitialAd)
        {
            mInterstitialAd.setAdListener(null);
            mInterstitialAd = null;
        }
        if(null != mInterstitialAdListener)
        {
            mInterstitialAdListener = null;
        }
    }

    public IBinder onBind(Intent intent)
    {
        return null;

    }

    private void openLocalGoogleAdActivity()
    {
        Intent secondIntent = new Intent(CacheManager.this,AdActivity.class);
        secondIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(CacheManager.this,0,secondIntent,PendingIntent.FLAG_ONE_SHOT);
        try
        {
            if(isPrintLog)
                Log.i("AdsNotes","弹出本地缓存的应用内Google广告！");
            pendingIntent.send();
            mIsRunning = false;
        }
        catch (PendingIntent.CanceledException e)
        {
            e.printStackTrace();
        }
    }

    public synchronized int onStartCommand(Intent intent, int flags, int startId)
    {
        if(!mIsRunning)
        {
            initDatas(intent);
            mIsRunning = true;
            if(BaseConfig.isAppForeground())
            {
                if(null != mInterstitialAd)
                {
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    Observable.interval(0l,1000, TimeUnit.MILLISECONDS).
                    subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Long>()
                    {
                        Disposable disposable;
                        long minNumberOfChecks;
                        long maxNumberOfChecks;
                        public void onComplete()
                        {

                        }

                        public void onError(Throwable e)
                        {

                        }

                        public void onNext(Long numberOfChecks)
                        {
                            if(numberOfChecks > maxNumberOfChecks)
                            {
                                if(isPrintLog)
                                    Log.i("AdsNotes","此次应用内Google广告请求手动结束");
                                destroy();/***********/
                                mCurrentRetryCount = 0;
                                if(null != disposable)
                                    disposable.dispose();
                                mIsRunning = false;
                                return;
                            }
                            if(!mIsRunning)
                            {
                                if(isPrintLog)
                                    Log.i("AdsNotes","此次应用内Google广告请求自动结束");
                                destroy();/***********/
                                mCurrentRetryCount = 0;
                                if(null != disposable)
                                    disposable.dispose();
                                return;
                            }
                        }

                        public void onSubscribe(Disposable dis)
                        {
                            disposable = dis;
                            minNumberOfChecks = 20;
                            maxNumberOfChecks = (mAdRetryIntervalTime * mTotalRetryCount / 1000l)+((mTotalRetryCount + 2) * minNumberOfChecks);
                        }
                    });
                }
                else
                {
                    destroy();
                    mIsRunning = false;
                    mCurrentRetryCount = 0;
                    if(isPrintLog)
                        Log.i("AdsNotes","mInterstitialAd为Null导致无法请求应用内Google广告！");
                }
            }
            else
            {
                destroy();
                mIsRunning = false;
                mCurrentRetryCount = 0;
                if(isPrintLog)
                    Log.i("AdsNotes","当前App没有显示在前台导致不能请求应用内Google广告！");
            }
        }
        else
        {
            if(isPrintLog)
                Log.i("AdsNotes","上次应用内Google广告请求未结束导致当前请求广告无效！");
        }
        return Service.START_STICKY;
    }
}