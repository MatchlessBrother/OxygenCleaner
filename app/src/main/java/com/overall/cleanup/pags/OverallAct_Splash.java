package com.overall.cleanup.pags;

import android.os.Build;
import android.os.Bundle;
import com.overall.cleanup.R;
import android.content.Intent;
import android.content.Context;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import android.content.SharedPreferences;
import io.reactivex.schedulers.Schedulers;
import androidx.appcompat.app.AppCompatActivity;
import com.zxingbar.code.autil.ShortcutIconUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class OverallAct_Splash extends AppCompatActivity
{
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashact);
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_MULTI_PROCESS);
        ShortcutIconUtils.createShortcutIcon(this,R.mipmap.ic_launcher,getString(R.string.app_name));
        Observable.just("waite!").map(new Function<String,String>()
        {
            public String apply(String noteStr) throws Exception
            {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !sharedPreferences.getBoolean("IconsHaveBeenCreated",false))
                    Thread.sleep(6000);
                else
                    Thread.sleep(2000);
                return noteStr;
            }
        }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
        {
            public void accept(String noteStr) throws Exception
            {
                startActivity(new Intent(OverallAct_Splash.this,OverallAct_BaseMain.class));
                finish();
            }
        });
    }
}