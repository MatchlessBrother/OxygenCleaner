package com.overall.cleanup.pags;

import java.util.Set;
import java.util.List;
import android.os.Build;
import java.util.HashSet;
import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;
import android.view.Window;
import com.overall.cleanup.R;
import com.overall.cleanup.IAllValues;
import com.overall.cleanup.model.OverallWhites;
import com.overall.cleanup.util.OverallAsdhHel;
import com.gyf.immersionbar.ImmersionBar;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import com.overall.cleanup.util.OverallShjhjasdePref;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import com.overall.cleanup.apters.OverallAdapter_WhiteSex;

public class OverallAct_White extends AppCompatActivity
{
    OverallAdapter_WhiteSex mWhiteAdapter;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            if (window != null)
            {
                window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            }
        }
        setContentView(R.layout.whiteact);
        ImmersionBar.with(this).statusBarView(R.id.top_bg).init();
        findViewById(R.id.fl_back).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                finish();
            }
        });
        mWhiteAdapter = new OverallAdapter_WhiteSex();
        RecyclerView appRV = findViewById(R.id.rv_app);
        appRV.setAdapter(mWhiteAdapter);

        OverallAsdhHel.getInstance().async(new OverallAsdhHel.Async<List<OverallWhites>>()
        {
            public List<OverallWhites> onBackground()
            {
                Set<String> whiteNames = OverallShjhjasdePref.with(getApplicationContext()).getStringSet(IAllValues.KEY_WHITE_LIST, new HashSet<String>());
                List<OverallWhites> whites = new ArrayList<>();
                PackageManager pm = getPackageManager();
                List<ApplicationInfo> applicationList = pm.getInstalledApplications(0);
                for (ApplicationInfo applicationInfo : applicationList)
                {
                    if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && !applicationInfo.packageName.equals(getPackageName())) { // 非系统应用
                        try
                        {
                            OverallWhites white = new OverallWhites();
                            white.setName(applicationInfo.loadLabel(pm));
                            white.setIcon(applicationInfo.loadIcon(pm));
                            white.setPackageName(applicationInfo.packageName);
                            whites.add(white);
                            for (String whiteName : whiteNames)
                            {
                                if (whiteName.equals(applicationInfo.packageName))
                                {
                                    white.setSelect(true);
                                    break;
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                return whites;
            }

            public void onResult(List<OverallWhites> whites)
            {
                mWhiteAdapter.addItems(whites);
                mWhiteAdapter.notifyDataSetChanged();
            }

            public void onCancel(Throwable throwable)
            {

            }
        });
    }

    protected void onDestroy()
    {
        super.onDestroy();
        Set<String> filterNames = new HashSet<>();
        for (OverallWhites item : mWhiteAdapter.getItems())
        {
            if (item.isSelect()) {
                filterNames.add(item.getPackageName());
            }
        }
        OverallShjhjasdePref.with(this).putStringSet(IAllValues.KEY_WHITE_LIST, filterNames);
    }
}