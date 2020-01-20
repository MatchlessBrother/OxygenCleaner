package com.zxingbar.code.bannerout.base;

import android.util.Log;
import android.os.Binder;
import android.os.IBinder;
import android.app.Service;
import com.zxingbar.code.R;
import android.content.Intent;
import android.content.Context;
import io.reactivex.Observable;
import android.content.ComponentName;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import com.zxingbar.code.act.base.BaseConfig;
import com.google.android.gms.ads.AdRequest;
import com.zxingbar.code.act.ChangeAdActivity;
import com.google.android.gms.ads.InterstitialAd;
import com.zxingbar.code.act.ExtraAdShowManager;
import com.zxingbar.code.act.ExtraAdShowActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;

import com.zxingbar.code.bannerout.GetUpdateConfigInfo;
import com.zxingbar.code.internetsssss.config.AdConfig;
import static com.zxingbar.code.act.base.BaseConfig.isPrintLog;
import com.zxingbar.code.internetsssss.config.AdConfigProvider;

public class DpDestroyManager extends Service
{
    private int mRetryCount;
    private Context mContext;
    /**Ture为显示,False为缓存**/
    private boolean mActionType;
    private static AdListener mAdListener;
    private String mInterstitialAdPlacementId;
    private boolean mIsSwitchAdPlatformsForRetry;
    public static InterstitialAd mInterstitialAd;
    public static volatile boolean mIsLocalCacheAd;
    private static final long mAdRetryIntervalTime = 15000l;
    public static final String ACTIONTYPE  = "ActionType";
    public static final String SWITCHADPLATFORMSFORRETRY  = "SwitchAdPlatformsForRetry";

    public synchronized int onStartCommand(Intent intent, int flags, int startId)
    {
        mContext = this;
        mRetryCount = 1;
        if(null == mAdListener)
        mAdListener = new AdListener();
        if(null != BaseConfig.getAdConfig() &&
        null != BaseConfig.getAdConfig().getLocatNumOfExtraGgAd() &&
        !"".equals(BaseConfig.getAdConfig().getLocatNumOfExtraGgAd()))
        mInterstitialAdPlacementId = BaseConfig.getAdConfig().getLocatNumOfExtraGgAd();
        if(isPrintLog)Log.i("AdsNotes","应用外双点GoogleService启动！");/*************/
        /********************************************************************************************************************************/
        String[] mInterstitialAdPlacementIds = mInterstitialAdPlacementId.split("/");
        if(mInterstitialAdPlacementIds.length != 2)
            mInterstitialAdPlacementId = getString(R.string.google_out_placement_id);
        else
            if(!mInterstitialAdPlacementIds[0].startsWith("ca-app-pub-") || mInterstitialAdPlacementIds[1].length() != 10)
                mInterstitialAdPlacementId = getString(R.string.google_out_placement_id);
        /********************************************************************************************************************************/
        if(null == mInterstitialAd || (null != mInterstitialAd && !mInterstitialAd.getAdUnitId().equals(mInterstitialAdPlacementId))){
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setImmersiveMode(true);/**/
            mInterstitialAd.setAdUnitId(mInterstitialAdPlacementId);
        }
        mInterstitialAd.setAdListener(mAdListener);/*************************************************************************************/
        /********************************************************************************************************************************/
        /********************************************************************************************************************************/
        if(null != BaseConfig.getAdConfig() &&
        BaseConfig.getAdConfig().getNumOfPlayExtraGgAd() <
        BaseConfig.getAdConfig().getDailyNumOfPlayExtraGgAd())
        {
            if(null != intent)
            {
                mActionType = intent.getBooleanExtra(ACTIONTYPE,false);
                mIsSwitchAdPlatformsForRetry = intent.getBooleanExtra(SWITCHADPLATFORMSFORRETRY,false);
            }
            else
            {
                mActionType = false;
                mIsSwitchAdPlatformsForRetry = false;
            }
            if(null != mInterstitialAd && !mIsLocalCacheAd && !mActionType)
            {
                Log.i("AdsNotes","缓存应用外双点Google广告资源！");
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
            else if(null != mInterstitialAd && mIsLocalCacheAd && mActionType)
            {
                if(mInterstitialAd.isLoaded() && !mInterstitialAd.isLoading())
                {
                    Log.i("AdsNotes","弹出应用外双点本地缓存Google广告！");
                    ExtraAdShowManager.getInstance().startExtraAdShowActivity(DpDestroyManager.this,ExtraAdShowActivity.DPGOOGLE_TYPEVALUE);
                }
                else
                {
                    Log.i("AdsNotes","应用外双点本地缓存Google广告已失效导致无法显示！");
                }
            }
            else if(null != mInterstitialAd && mIsLocalCacheAd &&
            !mActionType && !mInterstitialAd.isLoaded() && !mInterstitialAd.isLoading())
            {
                Log.i("AdsNotes","应用外双点本地缓存Google广告已失效故重新缓存Google广告资源！");
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**********************************************************************************************/
    /**********************************************************************************************/
    private class AdListener extends com.google.android.gms.ads.AdListener
    {
        public void onAdLoaded()
        {
            super.onAdLoaded();
            mIsLocalCacheAd = true;
            if(BaseConfig.isPrintLog)Log.i("AdsNotes","成功请求到应用外双点Google广告资源！");
        }

        public void onAdOpened()
        {
            super.onAdOpened();
            if(BaseConfig.isPrintLog)Log.i("AdsNotes","应用外双点Google广告正在展示！");
            AdConfig adConfig = BaseConfig.getAdConfig();
            if(null != adConfig)
            {
                adConfig.setNumOfPlayExtraGgAd(adConfig.getNumOfPlayExtraGgAd() + 1);
                AdConfigProvider.setLocalAdConfig(DpDestroyManager.this,adConfig);
                BaseConfig.setAdConfig(adConfig);
            }
            if(!AdConfigProvider.isShowFirstExtraAd(DpDestroyManager.this))
            {
                Intent intent = new Intent();/*********************************************************/
                intent.putExtra(GetUpdateConfigInfo.STATUS_KEY,1);/************************************/
                intent.setComponent(new ComponentName(DpDestroyManager.this,GetUpdateConfigInfo.class));
                getApplication().startService(intent);
            }
        }

        public void onAdClosed()
        {
            super.onAdClosed();
            mIsLocalCacheAd = false;
            if(BaseConfig.isPrintLog)Log.i("AdsNotes","关闭应用外双点Google广告页面！");
        }

        public void onAdClicked()
        {
            super.onAdClicked();
            if(BaseConfig.isPrintLog)Log.i("AdsNotes","点击应用外双点Google广告页面！");
        }

        public void onAdImpression()
        {
            super.onAdImpression();
            if(BaseConfig.isPrintLog)Log.i("AdsNotes","记录应用外双点Google广告即将展示！");
        }

        public void onAdLeftApplication()
        {
            super.onAdLeftApplication();
            if(BaseConfig.isPrintLog)Log.i("AdsNotes","从应用外双点Google广告页面跳转到第三方应用！");
        }

        public void onAdFailedToLoad(final int errorCode)
        {
            super.onAdFailedToLoad(errorCode);
            if(BaseConfig.isPrintLog)
            {
                switch(errorCode)
                {
                    case AdRequest.ERROR_CODE_NO_FILL:Log.i("AdsNotes","请求应用外双点Google广告发生错误：ERROR_CODE_NO_FILL");break;
                    case AdRequest.ERROR_CODE_NETWORK_ERROR:Log.i("AdsNotes","请求应用外双点Google广告发生错误：ERROR_CODE_NETWORK_ERROR");break;
                    case AdRequest.ERROR_CODE_INTERNAL_ERROR:Log.i("AdsNotes","请求应用外双点Google广告发生错误：ERROR_CODE_INTERNAL_ERROR");break;
                    case AdRequest.ERROR_CODE_INVALID_REQUEST:Log.i("AdsNotes","请求应用外双点Google广告发生错误：ERROR_CODE_INVALID_REQUEST");break;
                }
            }
            if(mRetryCount > 0)
            {
                mRetryCount--;
                if(BaseConfig.isPrintLog)Log.i("AdsNotes","等待?秒后发起应用外双点Google广告重试！");
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
                            if(BaseConfig.isPrintLog)Log.i("AdsNotes","发起应用外双点Google广告重试！");
                            mInterstitialAd.loadAd(new AdRequest.Builder().build());
                        }
                        else
                        {
                            if(mIsSwitchAdPlatformsForRetry)
                            {
                                if(BaseConfig.isPrintLog)Log.i("AdsNotes","mInterstitialAd为Null导致无法重试应用外双点Google广告故重试应用外双点FaceBook广告！");
                                ChangeAdActivity.changeGoogleAdToFaceBookAd(DpDestroyManager.this);
                            }
                            else
                            {
                                if(BaseConfig.isPrintLog)Log.i("AdsNotes","mInterstitialAd为Null导致无法重试应用外双点Google广告！");
                            }
                        }
                    }
                });
            }
            else
            {
                if(mIsSwitchAdPlatformsForRetry)
                {
                    if(BaseConfig.isPrintLog)Log.i("AdsNotes","应用外双点Google广告重试次数已用完故重试应用外双点FaceBook广告！");
                    ChangeAdActivity.changeGoogleAdToFaceBookAd(DpDestroyManager.this);
                }
                else
                {
                    if(BaseConfig.isPrintLog)Log.i("AdsNotes","应用外双点Google广告重试次数已用完故无法重试！");
                }
            }
        }
    }

    /**********************************************************************************************/
    /**********************************************************************************************/
    public void onDestroy()
    {
        if(BaseConfig.isPrintLog)Log.i("AdsNotes","应用外双点GoogleService关闭！");
        super.onDestroy();
        destroy();
    }

    public static void showAd()
    {
        if(null != mInterstitialAd && !mInterstitialAd.isLoading() && mInterstitialAd.isLoaded())
        {
            mInterstitialAd.show();
        }
    }

    public static void destroy()
    {
        if(null != mInterstitialAd)
        {
            mInterstitialAd.setAdListener(null);
            mInterstitialAd = null;
        }
        if(null != mAdListener)
        {
            mAdListener = null;
        }
    }

    public IBinder onBind(Intent intent)
    {
        return new Binder();

    }
}