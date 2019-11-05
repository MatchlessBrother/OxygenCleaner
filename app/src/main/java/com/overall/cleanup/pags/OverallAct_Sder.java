package com.overall.cleanup.pags;

import java.util.List;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.overall.cleanup.R;
import android.content.Intent;
import android.content.Context;
import android.widget.TextView;
import android.os.BatteryManager;
import android.animation.Animator;
import android.app.ActivityManager;
import android.content.IntentFilter;
import com.overall.cleanup.util.OverallUddil;
import com.overall.cleanup.util.OverallAsdhHel;
import com.overall.cleanup.util.OverallShdfUtil;
import com.gyf.immersionbar.ImmersionBar;
import android.view.ViewPropertyAnimator;
import com.overall.cleanup.apters.base.OverallBaseAddapter;
import android.support.v7.widget.RecyclerView;
import com.overall.cleanup.apters.base.OverallCleanAppliction;
import android.support.v7.app.AppCompatActivity;
import android.animation.AnimatorListenerAdapter;

public class OverallAct_Sder extends AppCompatActivity
{
    TextView mBatteryLevelTV;
    RecyclerView mBatteryRV;
    OverallBaseAddapter mAppAdapter;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saveract);
        ImmersionBar.with(this).statusBarView(R.id.top_bg).init();
        mBatteryLevelTV = findViewById(R.id.tv_battery_level);
        Intent intent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        mBatteryLevelTV.setText(level + "%");
        mAppAdapter = new OverallBaseAddapter();
        mBatteryRV = findViewById(R.id.rv_app);
        mBatteryRV.setAdapter(mAppAdapter);

        OverallAsdhHel.getInstance().async(new OverallAsdhHel.Async<List<OverallCleanAppliction>>()
        {
            public List<OverallCleanAppliction> onBackground()
            {
                return OverallUddil.getRunningApps(getApplicationContext());
            }

            public void onResult(List<OverallCleanAppliction> baseApps)
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
                    Toast.makeText(OverallAct_Sder.this, getString(R.string.text_no_optimization), Toast.LENGTH_SHORT).show();
                    return;
                }

                int translationWidth = -OverallShdfUtil.getScreenWidth(getApplication());
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
                                for (OverallCleanAppliction item : mAppAdapter.getItems())
                                {
                                    activityManager.killBackgroundProcesses(item.getPackageName());
                                }
                                Toast.makeText(OverallAct_Sder.this, getString(R.string.text_complete_optimization), Toast.LENGTH_SHORT).show();
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