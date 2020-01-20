package com.zxingbar.code.act;

import android.os.Bundle;
import android.view.Window;
import android.app.Activity;
import android.view.Gravity;
import android.content.Intent;
import io.reactivex.Observable;
import android.view.WindowManager;
import android.graphics.PixelFormat;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import com.zxingbar.code.bannerout.base.PlayManager;
import com.zxingbar.code.bannerout.base.DestroyManager;
import com.zxingbar.code.bannerout.base.DpDestroyManager;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ExtraAdShowActivity extends Activity
{
    private int mAdTypeValue;
    public static final int GOOGLE_TYPEVALUE = 1;
    public static final int DPGOOGLE_TYPEVALUE = 2;
    public static final int FACEBOOK_TYPEVALUE = 0;
    public static final String ADTYPEKEY ="ad_type_key";

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Window window = getWindow();/****************/
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.format = PixelFormat.TRANSPARENT;params.x = 0;params.y = 0;
        params.width = 1;params.height = 1;params.windowAnimations = android.R.style.Animation_Translucent;
        window.setAttributes(params);ExtraAdShowManager.getInstance().setExtraAdShowActivity(this);openExtraAdActivity();
    }

    protected void onNewIntent(Intent intent)
    {
        ExtraAdShowManager.getInstance().setExtraAdShowActivity(this);
        super.onNewIntent(intent);
        openExtraAdActivity();
    }

    public void openExtraAdActivity()
    {
        mAdTypeValue = getIntent().getIntExtra(ADTYPEKEY,0);
        switch(mAdTypeValue)
        {
            case FACEBOOK_TYPEVALUE://显示Facebook广告
            {
                PlayManager.showAd();
                break;
            }
            case GOOGLE_TYPEVALUE://显示Google广告
            {
                DestroyManager.showAd();
                break;
            }
            case DPGOOGLE_TYPEVALUE://显示双点机制缓存的Google广告
            {
                DpDestroyManager.showAd();
                break;
            }
        }
        /******************************************************************************************/
        /******************************************************************************************/
        Observable.just("ReadyCloseExtraAdShowActivity").map(new Function<String, String>()
        {
            public String apply(String noteStr) throws Exception
            {
                Thread.sleep(2000);
                return "CloseExtraAdShowActivity";
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
        {
            public void accept(String noteStr) throws Exception
            {
                /***ClosedExtraAdShowActivity***/
                ExtraAdShowManager.getInstance().finishExtraAdShowActivity();
            }
        });
    }

    protected void onDestroy()
    {
        super.onDestroy();
        ExtraAdShowManager.getInstance().setExtraAdShowActivity(null);
    }
}