package com.zxingbar.code.bannerout;

import java.util.Map;
import android.util.Log;
import java.util.HashMap;
import java.util.Calendar;
import android.content.Intent;
import android.app.IntentService;
import androidx.annotation.Nullable;
import com.zxingbar.code.act.base.BaseConfig;
import io.reactivex.schedulers.Schedulers;
import com.zxingbar.code.autil.nobase.PhoneHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import com.zxingbar.code.internetsssss.config.AdConfig;
import com.zxingbar.code.internetsssss.client.NetClient;
import com.zxingbar.code.internetsssss.client.NetParams;
import com.zxingbar.code.internetsssss.client.ReturnData;
import com.zxingbar.code.internetsssss.config.AdConfigProvider;
import com.zxingbar.code.internetsssss.client.ReturnNetObjCallBack;
import com.zxingbar.code.internetsssss.client.ReturnResultConfigInfos;

public class GetUpdateConfigInfo extends IntentService
{
    public static final String STATUS_KEY = "status_key";
    private volatile static int fbPlayedAdNumForYesterday;
    private volatile static int ggPlayedAdNumForYesterday;

    public GetUpdateConfigInfo()
    {
        super("GetUpdateConfigInfo");
    }

    protected void onHandleIntent(@Nullable final Intent intent)
    {
        if(null != BaseConfig.getAdConfig() && BaseConfig.getAdConfig().getNumOfPlayExtraFbAd() != 0)
            fbPlayedAdNumForYesterday = BaseConfig.getAdConfig().getNumOfPlayExtraFbAd();
        if(null != BaseConfig.getAdConfig() && BaseConfig.getAdConfig().getNumOfPlayExtraGgAd() != 0)
            ggPlayedAdNumForYesterday = BaseConfig.getAdConfig().getNumOfPlayExtraGgAd();
        Map orgMap = new HashMap();
        Map requestMap = new HashMap();
        orgMap.put("n",getPackageName());
        orgMap.put("b",PhoneHelper.getSerialNumber( ));
        orgMap.put("a",PhoneHelper.getAndroidId(this));
        orgMap.put("p",PhoneHelper.getSystemVersion());
        orgMap.put("v",PhoneHelper.getVersionName(this));
        orgMap.put("fc",String.valueOf(fbPlayedAdNumForYesterday));
        orgMap.put("gc",String.valueOf(ggPlayedAdNumForYesterday));
        orgMap.put("t",String.valueOf(System.currentTimeMillis()));
        orgMap.put("s",String.valueOf(intent.getIntExtra(STATUS_KEY,1)));
        requestMap.putAll(NetParams.getInstance().putFormsAddEncry(orgMap).convertForms().getMultipartForms());
        NetClient.getInstance(getApplicationContext()).getNetAllUrl().updateAdConfigInfos(requestMap)./*******/
        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ReturnNetObjCallBack<ReturnData<ReturnResultConfigInfos>>(getApplicationContext())
        {
            public void onSuccess(ReturnData<ReturnResultConfigInfos> returnResultConfigInfos)
            {
                if(intent.getIntExtra(STATUS_KEY,1) == 1)
                {
                    if(BaseConfig.isPrintLog)Log.i("AdsNotes","成功上传首个应用外广告已播放的状态!" );
                    AdConfigProvider.setFirstExtraAdShowStatus(GetUpdateConfigInfo.this,true);
                }
                else if(intent.getIntExtra(STATUS_KEY,1) == 2)
                {
                    AdConfig adConfig = returnResultConfigInfos.getData().convertToAdConfig();
                    AdConfig localAdConfig = BaseConfig.getAdConfig();
                    if(null != adConfig && null != localAdConfig)
                    {
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DAY_OF_MONTH,1);
                        calendar.set(Calendar.HOUR_OF_DAY,7);
                        calendar.set(Calendar.MINUTE,0);
                        calendar.set(Calendar.SECOND,0);
                        AdConfigProvider.setNextUpdateTime(GetUpdateConfigInfo.this,calendar.getTimeInMillis());
                        /**************************************************************************************/
                        localAdConfig.setIntervalTimeForPlayExtraAd(adConfig.getIntervalTimeForPlayExtraAd());
                        localAdConfig.setDailyNumOfPlayExtraFbAd(adConfig.getDailyNumOfPlayExtraFbAd());
                        localAdConfig.setDailyNumOfPlayExtraGgAd(adConfig.getDailyNumOfPlayExtraGgAd());
                        localAdConfig.setLocatNumOfExtraFbAd(adConfig.getLocatNumOfExtraFbAd());
                        localAdConfig.setLocatNumOfExtraGgAd(adConfig.getLocatNumOfExtraGgAd());
                        localAdConfig.setNumOfPlayExtraFbAd(0);/*******************************/
                        localAdConfig.setNumOfPlayExtraGgAd(0);/*******************************/
                        AdConfigProvider.setLocalAdConfig(getApplicationContext(),localAdConfig);
                        BaseConfig.setAdConfig(localAdConfig);
                        if(BaseConfig.isPrintLog) Log.i("AdsNotes","成功获取到关于广告最新的播放配置: "+localAdConfig.toString());
                    }
                }
            }

            public void onFailure(String msg)
            {
                if(intent.getIntExtra(STATUS_KEY,1) == 1)
                {
                    if(BaseConfig.isPrintLog)Log.i("AdsNotes","上传首个应用外广告已播放的状态失败!" );
                    AdConfigProvider.setFirstExtraAdShowStatus(GetUpdateConfigInfo.this,false);
                }
                else if(intent.getIntExtra(STATUS_KEY,1) == 2)
                {
                    if(BaseConfig.isPrintLog)Log.i("AdsNotes","获取关于广告最新的播放配置失败: " + msg);
                    AdConfig adConfig = BaseConfig.getAdConfig();
                    if(null != adConfig)
                    {
                        adConfig.setNumOfPlayExtraFbAd(0);
                        adConfig.setNumOfPlayExtraGgAd(0);
                        BaseConfig.setAdConfig(adConfig);
                        AdConfigProvider.setLocalAdConfig(getApplicationContext(),adConfig);
                    }
                }
            }

            public void onError(String msg)
            {
                if(intent.getIntExtra(STATUS_KEY,1) == 1)
                {
                    if(BaseConfig.isPrintLog)Log.i("AdsNotes","上传首个应用外广告已播放的状态失败!" );
                    AdConfigProvider.setFirstExtraAdShowStatus(GetUpdateConfigInfo.this,false);
                }
                else if(intent.getIntExtra(STATUS_KEY,1) == 2)
                {
                    if(BaseConfig.isPrintLog)Log.i("AdsNotes","获取关于广告最新的播放配置失败: " + msg);
                    AdConfig adConfig = BaseConfig.getAdConfig();
                    if(null != adConfig)
                    {
                        adConfig.setNumOfPlayExtraFbAd(0);
                        adConfig.setNumOfPlayExtraGgAd(0);
                        BaseConfig.setAdConfig(adConfig);
                        AdConfigProvider.setLocalAdConfig(getApplicationContext(),adConfig);
                    }
                }
            }
        });
    }
}