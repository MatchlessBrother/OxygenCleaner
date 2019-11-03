package com.oxygen.cleaner.pags;

import android.os.Bundle;
import com.oxygen.cleaner.R;
import android.content.Intent;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import android.support.v7.app.AppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class OxygenAct_Splash extends AppCompatActivity
{
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashact);
        Observable.just("first page").map(new Function<String, String>()
        {
            public String apply(String str) throws Exception
            {
                Thread.sleep(2000);
                return str;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Consumer<String>()
        {
            public void accept(String str) throws Exception
            {
                startActivity(new Intent(OxygenAct_Splash.this, OxygenAct_BaseMain.class));
                finish();
            }
        });
    }
}