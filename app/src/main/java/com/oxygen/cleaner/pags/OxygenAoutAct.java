package com.oxygen.cleaner.pags;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import com.oxygen.cleaner.R;
import android.content.Intent;
import android.widget.TextView;
import com.gyf.immersionbar.ImmersionBar;
import android.support.v7.app.AppCompatActivity;

public class OxygenAoutAct extends AppCompatActivity
{
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aboutact);
        ImmersionBar.with(this).statusBarView(R.id.top_bg).init();
        String versionName = "";
        try
        {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        TextView versionNameTV = findViewById(R.id.tv_version_name);
        versionNameTV.setText("V" + versionName);

        findViewById(R.id.fl_back).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                finish();
            }
        });

        findViewById(R.id.tv_update).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                toDetails();
            }
        });
        findViewById(R.id.tv_share).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                toShare();
            }
        });
        findViewById(R.id.tv_feedback).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                toMail("feedback");
            }
        });
        findViewById(R.id.tv_translate).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                toMail("translate");
            }
        });
        findViewById(R.id.tv_privacy).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivity(new Intent(OxygenAoutAct.this, OxygenAct_Prvacy.class));
            }
        });
        ((TextView)findViewById(R.id.copyright)).setText("Copyright 2019 " + getString(R.string.app_name) + "All reghts reervid");
    }

    private void toMail(String title)
    {
        Uri uri = Uri.parse("mailto:sshh3726@gmail.com");
        String[] email = {"sshh3726@gmail.com"};
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_CC, email); // 抄送人
        intent.putExtra(Intent.EXTRA_SUBJECT, title); // 主题
        intent.putExtra(Intent.EXTRA_TEXT, ""); // 正文
        startActivity(Intent.createChooser(intent, getString(R.string.text_select_email)));
    }

    public void toDetails()
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + getPackageName()));
            intent.setPackage("com.android.vending");
            if (intent.resolveActivity(getPackageManager()) != null)
            {
                startActivity(intent);
            }
            else//没有应用市场，通过浏览器跳转到Google Play
            {
                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                intent2.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                if (intent2.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent2);
                }
            }
        }
        catch (Exception e)
        {
           e.printStackTrace();
        }
    }

    public void toShare()
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + getPackageName());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}