package com.oxygen.cleaner.pags;

import java.util.List;
import java.util.Locale;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.oxygen.cleaner.R;
import android.widget.TextView;
import android.animation.Animator;
import com.oxygen.cleaner.util.OxygenUddil;
import com.oxygen.cleaner.util.OxygenAsdhHel;
import com.oxygen.cleaner.util.OxygenShdfUtil;
import com.gyf.immersionbar.ImmersionBar;
import android.view.ViewPropertyAnimator;
import com.oxygen.cleaner.util.OxygenFilasdeUtil;
import com.oxygen.cleaner.apters.base.OxygenBaseAddapter;
import android.support.v7.widget.RecyclerView;
import com.oxygen.cleaner.apters.base.OxygenCleanAppliction;
import android.support.v7.app.AppCompatActivity;
import android.animation.AnimatorListenerAdapter;

public class OxygenAct_Basdst extends AppCompatActivity
{
    TextView mSizeTV;
    TextView mSizeUnitTV;
    RecyclerView mBoostRV;
    OxygenBaseAddapter mAppAdapter;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boostact);
        ImmersionBar.with(this).statusBarView(R.id.top_bg).init();
        mSizeTV = findViewById(R.id.tv_size);
        mSizeUnitTV = findViewById(R.id.tv_size_unit);
        mAppAdapter = new OxygenBaseAddapter();
        mBoostRV = findViewById(R.id.rv_boost);
        mBoostRV.setAdapter(mAppAdapter);
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
                long size = 0;
                for (OxygenCleanAppliction baseApp : baseApps)
                {
                    size += baseApp.getSize();
                }
                String[] sizes = OxygenFilasdeUtil.fileSize(size);
                mSizeTV.setText(sizes[0]);
                mSizeUnitTV.setText(sizes[1]);
            }

            public void onCancel(Throwable throwable)
            {

            }
        });
        findViewById(R.id.fl_back).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                finish();
            }
        });
        findViewById(R.id.btn_boost).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (mAppAdapter.getItemCount() == 0)
                {
                    Toast.makeText(OxygenAct_Basdst.this, getString(R.string.text_no_boost), Toast.LENGTH_SHORT).show();
                    return;
                }

                int translationWidth = -OxygenShdfUtil.getScreenWidth(getApplication());
                v.animate().translationX(translationWidth).start();
                View childView;
                int childCount = mBoostRV.getChildCount();
                for (int index = 0; index < childCount; index++)
                {
                    childView = mBoostRV.getChildAt(index);
                    ViewPropertyAnimator animator = childView.animate();
                    animator.translationX(-childView.getWidth());
                    animator.setStartDelay((childCount - index + 1) * 200);
                    if (index == childCount - 1)
                    {
                        animator.setListener(new AnimatorListenerAdapter()
                        {
                            public void onAnimationEnd(Animator animation)
                            {
                                for (OxygenCleanAppliction item : mAppAdapter.getItems())
                                {
                                    android.os.Process.killProcess(item.getPid());
                                }
                                Toast.makeText(OxygenAct_Basdst.this, String.format(Locale.getDefault(), getString(R.string.text_release_format), mSizeTV.getText(), mSizeUnitTV.getText()), Toast.LENGTH_SHORT).show();
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