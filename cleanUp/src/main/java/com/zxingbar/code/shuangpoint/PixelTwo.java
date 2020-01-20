package com.zxingbar.code.shuangpoint;

import android.os.Build;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.view.Window;
import android.view.Gravity;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.content.Intent;
import android.graphics.Color;
import android.widget.TextView;
import io.reactivex.Observable;
import android.view.WindowManager;
import android.graphics.PixelFormat;
import java.util.concurrent.TimeUnit;
import android.annotation.SuppressLint;
import io.reactivex.functions.Consumer;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class PixelTwo extends Activity
{
    private static PixelTwo sInstance;
    private TextView mTextView;

    protected void onPause()
    {
        super.onPause();

    }

    protected void onResume()
    {
        super.onResume();

    }

    @SuppressLint("CheckResult")
    protected void onStop()
    {
        super.onStop();
        Log.d("DoublePoint", "Pixel 2-> onStop");
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
        {
            finish();
        }
    }

    protected void onStart()
    {
        super.onStart();
        Log.i("DoublePoint", "pixel2 onStart");
        mTextView.setFocusable(true);
        mTextView.setFocusableInTouchMode(true);
        mTextView.requestFocus();
        mTextView.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    finish();
                    PixelOne pixelOne = PixelOne.getInstance();
                    if (pixelOne != null)
                    {
                        pixelOne.finish();
                    }

                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
                    {
                        Observable.timer(100,TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>()
                        {
                            public void accept(Long aLong) throws Exception
                            {
                                PixelManager.getInstance().goHome();
                                PixelManager.getInstance().startOnePixel(true);
                            }
                        });
                    }
                    else
                    {
                        PixelManager.getInstance().startOnePixelDelay(true);
                    }
                }
                return false;
            }
        });
    }

    protected void onDestroy()
    {
        super.onDestroy();
        sInstance = null;
        Log.d("DoublePoint", "Pixel 2 onDestroy");
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        Log.i("DoublePoint", "pixel2 back");
    }

    public static PixelTwo getsInstance()
    {
        return sInstance;

    }

    @SuppressLint("CheckResult")
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        sInstance = this;Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;params.format = PixelFormat.TRANSPARENT;
        params.x = 1;params.y = 1;params.height = 1;params.width = 1;params.windowAnimations = android.R.style.Animation_Translucent;
        window.setAttributes(params);mTextView = new TextView(this);mTextView.setBackgroundColor(Color.BLUE);mTextView.setText("Pixel 2");
        mTextView.setGravity(Gravity.CENTER);
        if (mTextView.getParent() == null)
        {
            ViewGroup rootView = (ViewGroup) getWindow().getDecorView();
            rootView.addView(mTextView, params);
        }
    }
}
