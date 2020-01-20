package com.overall.cleanup.pags;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.overall.cleanup.R;
import com.zxingbar.code.autil.ShortcutIconUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class OverallAct_Wel extends AppCompatActivity
{
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_wel);
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
                startActivity(new Intent(OverallAct_Wel.this,OverallAct_BaseMain.class));
                finish();
            }
        });
    }
}