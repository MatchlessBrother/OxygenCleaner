package com.oxygen.cleaner.pags;

import java.util.List;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.oxygen.cleaner.R;
import android.content.Intent;
import android.content.Context;
import android.widget.TextView;
import android.os.BatteryManager;
import android.animation.Animator;
import android.app.ActivityManager;
import android.content.IntentFilter;
import com.oxygen.cleaner.util.OxygenUddil;
import com.oxygen.cleaner.util.OxygenAsdhHel;
import com.oxygen.cleaner.util.OxygenShdfUtil;
import com.gyf.immersionbar.ImmersionBar;
import android.view.ViewPropertyAnimator;
import com.oxygen.cleaner.apters.base.OxygenBaseAddapter;
import android.support.v7.widget.RecyclerView;
import com.oxygen.cleaner.apters.base.OxygenCleanAppliction;
import android.support.v7.app.AppCompatActivity;
import android.animation.AnimatorListenerAdapter;

public class OxygenAct_Sder extends AppCompatActivity
{
    TextView mBatteryLevelTV;
    RecyclerView mBatteryRV;
    OxygenBaseAddapter mAppAdapter;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saveract);
        ImmersionBar.with(this).statusBarView(R.id.top_bg).init();
        mBatteryLevelTV = findViewById(R.id.tv_battery_level);
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        mBatteryLevelTV.setText(level + "%");
        mAppAdapter = new OxygenBaseAddapter();
        mBatteryRV = findViewById(R.id.rv_app);
        mBatteryRV.setAdapter(mAppAdapter);

        OxygenAsdhHel.getInstance().async(new OxygenAsdhHel.Async<List<OxygenCleanAppliction>>()
        {
            public List<OxygenCleanAppliction> onBackground()
            {
                return OxygenUddil.getRunningApps(getApplicationContext());
            }

            public void onResult(List<OxygenCleanAppliction> baseApps)
            {
                mAppAdapter.addItems(baseApps);
                mAppAdapter.notifyDataSetChanged();
            }

            public void onCancel(Throwable throwable)
            {

            }
        });
        findViewById(R.id.fl_back).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_hibernate).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (mAppAdapter.getItemCount() == 0)
                {
                    Toast.makeText(OxygenAct_Sder.this, getString(R.string.text_no_optimization), Toast.LENGTH_SHORT).show();
                    return;
                }

                int translationWidth = -OxygenShdfUtil.getScreenWidth(getApplication());
                v.animate().translationX(translationWidth).start();
                View childView;
                int childCount = mBatteryRV.getChildCount();
                for (int index = 0; index < childCount; index++)
                {
                    childView = mBatteryRV.getChildAt(index);
                    ViewPropertyAnimator animator = childView.animate();
                    animator.translationX(-childView.getWidth());
                    animator.setStartDelay((childCount - index + 1) * 200);
                    if (index == childCount - 1)
                    {
                        animator.setListener(new AnimatorListenerAdapter()
                        {
                            public void onAnimationEnd(Animator animation)
                            {
                                ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                                for (OxygenCleanAppliction item : mAppAdapter.getItems())
                                {
                                    activityManager.killBackgroundProcesses(item.getPackageName());
                                }
                                Toast.makeText(OxygenAct_Sder.this, getString(R.string.text_complete_optimization), Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                    animator.start();
                }
            }
        });
    }
}