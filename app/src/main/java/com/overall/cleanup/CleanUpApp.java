package com.overall.cleanup;

import java.util.Map;
import android.util.Log;
import com.appsflyer.AppsFlyerLib;
import androidx.multidex.MultiDex;
import com.zxingbar.code.act.base.BaseConfig;
import com.appsflyer.AppsFlyerConversionListener;

public class CleanUpApp extends BaseConfig
{
    private static final String AF_DEV_KEY = "RweDbhA2OKjPoLCvBQ4Qem";

    public void onCreate()
    {
        super.onCreate();
        MultiDex.install(this);
        AppsFlyerConversionListener conversionDataListener = new AppsFlyerConversionListener()
        {
            public void onInstallConversionDataLoaded(Map<String, String> map)
            {
                for(Map.Entry<String, String> entry : map.entrySet())
                {
                    String mapKey = entry.getKey();
                    String mapValue = entry.getValue();
                    Log.i("Flyer",mapKey + ":" +  mapValue);
                }
            }

            public void onInstallConversionFailure(String s)
            {

            }

            public void onAppOpenAttribution(Map<String, String> map)
            {

            }

            public void onAttributionFailure(String s)
            {

            }
        };
        AppsFlyerLib.getInstance().init(AF_DEV_KEY,conversionDataListener,getApplicationContext());
        AppsFlyerLib.getInstance().startTracking(this);
    }
}