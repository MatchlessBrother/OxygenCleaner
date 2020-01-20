package com.zxingbar.code.banerin;

import android.util.Log;
import android.os.IBinder;
import android.app.Service;
import com.facebook.ads.Ad;
import com.zxingbar.code.R;
import io.reactivex.Observer;
import android.content.Intent;
import io.reactivex.Observable;
import com.facebook.ads.AdError;
import android.app.PendingIntent;
import com.facebook.ads.AdSettings;
import android.content.ComponentName;
import java.util.concurrent.TimeUnit;
import com.facebook.ads.InterstitialAd;
import io.reactivex.functions.Consumer;
import com.zxingbar.code.act.base.BaseConfig;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.disposables.Disposable;
import com.facebook.ads.InterstitialAdListener;
import com.facebook.ads.AudienceNetworkActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;
import static com.zxingbar.code.act.base.BaseConfig.isPrintLog;
import static com.facebook.ads.AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CALLBACK_MODE;

public class LoadManager extends Service
{
    private int mTotalRetryCount;
    private int mCurrentRetryCount;
    private String mInterstitialAdPlacementId;
    private static volatile boolean mIsRunning;
    public static InterstitialAd mInterstitialAd;
    private static boolean mIsSwitchAdPlatformsForRetry;
    public static final long mAdIntervalTime = 0l;//毫秒
    public static final long mAdRetryIntervalTime = 15000l;//毫秒
    public static InterstitialAdListener mInterstitialAdListener;

    private void initDatas(Intent intent)
    {
        if(isPrintLog)
            Log.i("AdsNotes","应用内FaceBook广告初始化！");
        if(null != intent)
        {
            mIsSwitchAdPlatformsForRetry = intent.getBooleanExtra(
            AvoidErrorManager.ISSWITCHADPLATFORMSFORRETRY,false);
        }
        mTotalRetryCount = 1;
        mCurrentRetryCount=mTotalRetryCount;
        if(null == mInterstitialAdListener)
        {
            mInterstitialAdListener = new InterstitialAdListener()
            {
                public void onAdClicked(Ad ad)
                {
                    if(isPrintLog)
                        Log.i("AdsNotes","点击应用内FaceBook广告页面！");
                }

                public void onLoggingImpression(Ad ad)
                {
                    if(isPrintLog)
                        Log.i("AdsNotes","记录应用内FaceBook广告即将展示！");
                }

                public synchronized void onAdLoaded(Ad ad)
                {
                    if(isPrintLog)
                        Log.i("AdsNotes","成功请求到应用内FaceBook广告资源！");
                    if(null != mInterstitialAd && !mInterstitialAd.isAdInvalidated())
                    {
                        if(BaseConfig.isAppForeground())
                        {
                            mInterstitialAd.show();
                        }
                        else
                        {
                            if(isPrintLog)
                                Log.i("AdsNotes","当前App没有显示在前台导致不能显示应用内FaceBook广告！");
                            mIsRunning = false;
                        }
                    }
                    else
                    {
                        if(isPrintLog && null == mInterstitialAd)
                            Log.i("AdsNotes","mInterstitialAd为Null导致不能显示应用内FaceBook广告！");
                        else if(isPrintLog && mInterstitialAd.isAdInvalidated())
                            Log.i("AdsNotes","即将显示的应用内FaceBook广告已经失效导致无法显示广告！");
                        mIsRunning = false;
                    }
                }

                public synchronized void onInterstitialDisplayed(Ad ad)
                {
                    if(isPrintLog)
                        Log.i("AdsNotes","应用内FaceBook广告正在展示！");
                }

                public synchronized void onInterstitialDismissed(Ad ad)
                {
                    if(isPrintLog)
                        Log.i("AdsNotes","关闭应用内FaceBook广告页面！");
                    mIsRunning = false;
                }

                public void onError(Ad ad,final AdError adError)
                {
                    if(isPrintLog)
                        Log.i("AdsNotes","请求应用内FaceBook广告发生错误：" + adError.getErrorMessage());
                    if(adError.getErrorMessage().contains("cannot be loaded"))
                    {
                        openLocalFaceBookAdActivity();
                    }
                    else if(adError.getErrorMessage().contains("can't call load()"))
                    {
                        openLocalFaceBookAdActivity();
                    }
                    else if(adError.getErrorMessage().contains("ad in state SHOWING"))
                    {
                        openLocalFaceBookAdActivity();
                    }
                    else if(adError.getErrorMessage().contains("re-loaded too frequently"))
                    {
                        if(isPrintLog)
                            Log.i("AdsNotes","因广告请求太频繁而停止应用内FaceBook广告重试！");
                        mIsRunning = false;
                    }
                    else
                    {
                        if(mCurrentRetryCount > 0)
                        {
                            mCurrentRetryCount--;
                            if(isPrintLog)
                                Log.i("AdsNotes","等待?秒后发起应用内FaceBook广告重试！");
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
                                                Log.i("AdsNotes","发起应用内FaceBook广告重试！");
                                            mInterstitialAd.loadAd();
                                        }
                                        else
                                        {
                                            if(isPrintLog)
                                                Log.i("AdsNotes","App没有显示在前台故无法重试应用内FaceBook广告！");
                                            mIsRunning = false;
                                        }
                                    }
                                    else
                                    {
                                        if(mIsSwitchAdPlatformsForRetry)
                                        {
                                            if(isPrintLog)
                                                Log.i("AdsNotes","mInterstitialAd为Null导致无法重试应用内FaceBook广告故重试应用内Google广告！");
                                            Intent intent = new Intent();
                                            intent.setComponent(new ComponentName(
                                            LoadManager.this.getApplication(),CacheManager.class));
                                            LoadManager.this.getApplication().startService(intent);
                                        }
                                        else
                                        {
                                            if(isPrintLog)
                                                Log.i("AdsNotes","mInterstitialAd为Null导致无法重试应用内FaceBook广告！");
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
                                    Log.i("AdsNotes","应用内FaceBook广告重试次数已用完故重试应用内Google广告！");
                                Intent intent = new Intent();
                                intent.setComponent(new ComponentName(
                                LoadManager.this.getApplication(),CacheManager.class));
                                LoadManager.this.getApplication().startService(intent);
                            }
                            else
                            {
                                if(isPrintLog)
                                    Log.i("AdsNotes","应用内FaceBook广告重试次数已用完故无法重试！");
                            }
                            mIsRunning = false;
                        }
                    }
                }
            };
        }/****************************/
        AdSettings.setIntegrationErrorMode(INTEGRATION_ERROR_CALLBACK_MODE);
        mInterstitialAdPlacementId = LoadManager.this.getString(R.string.facebook_in_placement_id);
        if(null == mInterstitialAd)mInterstitialAd = new InterstitialAd(this,mInterstitialAdPlacementId);
        mInterstitialAd.setAdListener(mInterstitialAdListener);/*************************************************/
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
            mInterstitialAd.destroy();
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

    private void openLocalFaceBookAdActivity()
    {
        Intent secondIntent = new Intent(LoadManager.this,AudienceNetworkActivity.class);
        secondIntent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        PendingIntent pendingIntent = PendingIntent.getActivity(LoadManager.this,0,secondIntent,PendingIntent.FLAG_ONE_SHOT);
        try
        {
            if(isPrintLog)
                Log.i("AdsNotes","弹出本地缓存的应用内FaceBook广告！");
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
                    mInterstitialAd.loadAd();
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
                                    Log.i("AdsNotes","此次应用内FaceBook广告请求手动结束");
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
                                    Log.i("AdsNotes","此次应用内FaceBook广告请求自动结束");
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
                            minNumberOfChecks = 15;
                            maxNumberOfChecks = (mAdRetryIntervalTime * mTotalRetryCount / 1000l)+((mTotalRetryCount + 1) * minNumberOfChecks);
                        }
                    });
                }
                else
                {
                    destroy();
                    mIsRunning = false;
                    mCurrentRetryCount = 0;
                    if(isPrintLog)
                        Log.i("AdsNotes","mInterstitialAd为Null导致无法请求应用内FaceBook广告！");
                }
            }
            else
            {
                destroy();
                mIsRunning = false;
                mCurrentRetryCount = 0;
                if(isPrintLog)
                    Log.i("AdsNotes","当前App没有显示在前台导致不能请求应用内FaceBook广告！");
            }
        }
        else
        {
            if(isPrintLog)
                Log.i("AdsNotes","上次应用内FaceBook广告请求未结束导致当前请求广告无效！");
        }
        return Service.START_STICKY;
    }
}