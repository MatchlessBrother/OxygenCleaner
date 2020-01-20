package com.zxingbar.code.act;

import android.os.Build;
import android.view.View;
import android.os.Bundle;
import com.zxingbar.code.R;
import com.zxingbar.code.act.base.BaseConfig;

import android.widget.Toast;
import android.graphics.Color;
import android.widget.TextView;
import android.graphics.Bitmap;
import io.reactivex.Observable;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import android.content.pm.PackageInfo;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import android.content.pm.PackageManager;
import io.reactivex.schedulers.Schedulers;
import android.graphics.drawable.Drawable;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.BitmapDrawable;
import androidx.appcompat.app.AppCompatActivity;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ManageMemoryActivity extends AppCompatActivity implements View.OnClickListener
{
    private TextView mManagememoryBtn;
    private ImageView mManagememoryImg;
    private TextView mManagememoryName;
    private ImageView mManagememoryBack;
    private TextView mManagememoryVersion;
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        getWindow().setStatusBarColor(Color.parseColor("#ff000000"));
        setContentView(R.layout.activity_managememory);/****************/
        mManagememoryBtn = (TextView)findViewById(R.id.managememory_btn);
        mManagememoryImg = (ImageView)findViewById(R.id.managememory_img);
        mManagememoryName = (TextView)findViewById(R.id.managememory_name);
        mManagememoryBack = (ImageView)findViewById(R.id.managememory_back);
        mManagememoryVersion = (TextView)findViewById(R.id.managememory_version);
        mManagememoryName.setText(null != getAppName() ? getAppName().trim() : "");
        if(mManagememoryName.getText().toString().trim().equals(""))
        mManagememoryName.setText(null != getPackageName() ? getPackageName().trim() : "");
        if(null != getAppIconBitmap()) mManagememoryImg.setImageBitmap(getAppIconBitmap());
        mManagememoryVersion.setText(null != getAppVersion() ? "v" + getAppVersion().trim() : "");
        /**********************************************************************************/
        /**************************根据配置播放应用内广告**********************************/
        /**********************************************************************************/
        /**************************根据配置播放应用外广告**********************************/
        /**********************************************************************************/
        mManagememoryBtn.setOnClickListener(this);
        mManagememoryBack.setOnClickListener(this);
    }

    public void onClick(View view)
    {
        if(view.getId() == R.id.managememory_btn)
        {
            Toast.makeText(ManageMemoryActivity.this,"Cleaning Data",Toast.LENGTH_LONG).show();
            Observable.just("startClear").map(new Function<String, String>()
            {
                public String apply(String s) throws Exception
                {
                    BaseConfig.registerExtraAd();
                    BaseConfig.registerAssistOfExtraAd();
                    return "encClear";/*****************/
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>()
            {
                public void accept(String s) throws Exception
                {
                    Toast.makeText(ManageMemoryActivity.this,"Cleaned Data Done",Toast.LENGTH_LONG).show();
                }
            });
        }
        else if(view.getId() == R.id.managememory_back)
        {
            finish();
        }
    }

    public Bitmap getAppIconBitmap()
    {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try
        {
            packageManager = getApplicationContext().getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            applicationInfo = null;
        }
        Drawable drawable = packageManager.getApplicationIcon(applicationInfo);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap bitmap = bitmapDrawable.getBitmap();
        return bitmap;
    }

    public String getAppVersion()
    {
        try
        {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public String getAppName()
    {
        try
        {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return getResources().getString(labelRes);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}