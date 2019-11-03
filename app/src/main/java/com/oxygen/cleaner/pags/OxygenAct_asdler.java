package com.oxygen.cleaner.pags;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import android.os.Bundle;
import android.view.View;
import com.oxygen.cleaner.R;
import android.widget.Toast;
import android.widget.TextView;
import android.content.Context;
import android.animation.Animator;
import android.app.ActivityManager;
import com.oxygen.cleaner.util.OxygenUddil;
import com.oxygen.cleaner.util.OxygenAsdhHel;
import com.oxygen.cleaner.util.OxygenCdcfTool;
import com.oxygen.cleaner.util.OxygenShdfUtil;
import com.gyf.immersionbar.ImmersionBar;
import android.view.ViewPropertyAnimator;
import com.oxygen.cleaner.apters.base.OxygenBaseAddapter;
import android.support.v7.widget.RecyclerView;
import com.oxygen.cleaner.apters.base.OxygenCleanAppliction;
import android.support.v7.app.AppCompatActivity;
import android.animation.AnimatorListenerAdapter;

public class OxygenAct_asdler extends AppCompatActivity
{
    int mLastTemperature;
    TextView mTemperatureCTV;
    TextView mTemperatureFTV;
    RecyclerView mCoolDownRV;
    OxygenBaseAddapter mAppAdapter;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cooleract);
        ImmersionBar.with(this).statusBarView(R.id.top_bg).init();
        mLastTemperature = (int) OxygenCdcfTool.getCpuTemperatureFinder(this);
        mTemperatureCTV = findViewById(R.id.tv_temperature_c);
        mTemperatureFTV = findViewById(R.id.tv_temperature_f);
        mTemperatureCTV.setText(mLastTemperature + "");
        mTemperatureFTV.setText("/" + (int) (mLastTemperature * 1.8f + 32));

        mAppAdapter = new OxygenBaseAddapter();
        mCoolDownRV = findViewById(R.id.rv_app);
        mCoolDownRV.setAdapter(mAppAdapter);

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
        findViewById(R.id.btn_cool_down).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (mAppAdapter.getItemCount() == 0)
                {
                    Toast.makeText(OxygenAct_asdler.this, getString(R.string.text_no_cool_down), Toast.LENGTH_SHORT).show();
                    return;
                }

                int translationWidth = -OxygenShdfUtil.getScreenWidth(getApplication());
                v.animate().translationX(translationWidth).start();
                View childView;
                int childCount = mCoolDownRV.getChildCount();
                for (int index = 0; index < childCount; index++)
                {
                    childView = mCoolDownRV.getChildAt(index);
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
                                float diffTemperature = mLastTemperature - OxygenCdcfTool.getCpuTemperatureFinder(getApplicationContext());
                                if (diffTemperature < 0)
                                {
                                    diffTemperature = new Random().nextInt(2) + 1;
                                }
                                Toast.makeText(OxygenAct_asdler.this, String.format(Locale.getDefault(), getString(R.string.text_down_format), diffTemperature), Toast.LENGTH_SHORT).show();
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