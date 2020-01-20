package com.zxingbar.code.act;

import android.content.Intent;
import android.content.Context;
import java.lang.ref.WeakReference;

public class ExtraAdShowManager
{
    private WeakReference<ExtraAdShowActivity> mExtraAdShowActivity;
    private static final ExtraAdShowManager mInstance = new ExtraAdShowManager();

    private ExtraAdShowManager()
    {

    }

    public static ExtraAdShowManager getInstance()
    {
        return mInstance;

    }

    /**************************************************************************/
    /**************************************************************************/

    public void finishExtraAdShowActivity()
    {
        if (null != mExtraAdShowActivity)
        {
            ExtraAdShowActivity extraAdShowActivity = mExtraAdShowActivity.get();
            if (null != extraAdShowActivity && !extraAdShowActivity.isFinishing())
            {
                extraAdShowActivity.finish();
            }
            mExtraAdShowActivity = null;
        }
    }

    public void startExtraAdShowActivity(Context context,int adTypeValue)
    {
        /******/Intent intent = new Intent(context,ExtraAdShowActivity.class);
        intent.putExtra(ExtraAdShowActivity.ADTYPEKEY,adTypeValue);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);/***/
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);/*****/
        context.getApplicationContext().startActivity(intent);
    }

    public void setExtraAdShowActivity(ExtraAdShowActivity extraAdShowActivity)
    {
        mExtraAdShowActivity = new WeakReference<>(extraAdShowActivity);
    }
}