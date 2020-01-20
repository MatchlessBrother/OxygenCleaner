package com.overall.cleanup.pags;

import java.util.List;
import java.util.Locale;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.overall.cleanup.R;
import android.widget.TextView;
import android.animation.Animator;
import com.overall.cleanup.util.OverallUddil;
import com.overall.cleanup.util.OverallAsdhHel;
import com.overall.cleanup.util.OverallShdfUtil;
import com.gyf.immersionbar.ImmersionBar;
import android.view.ViewPropertyAnimator;
import com.overall.cleanup.util.OverallFilasdeUtil;
import com.overall.cleanup.apters.base.OverallBaseAddapter;
import androidx.recyclerview.widget.RecyclerView;
import com.overall.cleanup.apters.base.OverallCleanAppliction;
import androidx.appcompat.app.AppCompatActivity;
import android.animation.AnimatorListenerAdapter;

public class OverallAct_Basdst extends AppCompatActivity
{
    TextView mSizeTV;
    TextView mSizeUnitTV;
    RecyclerView mBoostRV;
    OverallBaseAddapter mAppAdapter;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.boostact);
        ImmersionBar.with(this).statusBarView(R.id.top_bg).init();
        mSizeTV = findViewById(R.id.tv_size);
        mSizeUnitTV = findViewById(R.id.tv_size_unit);
        mAppAdapter = new OverallBaseAddapter();
        mBoostRV = findViewById(R.id.rv_boost);
        mBoostRV.setAdapter(mAppAdapter);
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
                long size = 0;
                for (OverallCleanAppliction baseApp : baseApps)
                {
                    size += baseApp.getSize();
                }
                String[] sizes = OverallFilasdeUtil.fileSize(size);
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
                    Toast.makeText(OverallAct_Basdst.this, getString(R.string.text_no_boost), Toast.LENGTH_SHORT).show();
                    return;
                }

                int translationWidth = -OverallShdfUtil.getScreenWidth(getApplication());
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
                                for (OverallCleanAppliction item : mAppAdapter.getItems())
                                {
                                    android.os.Process.killProcess(item.getPid());
                                }
                                Toast.makeText(OverallAct_Basdst.this, String.format(Locale.getDefault(), getString(R.string.text_release_format), mSizeTV.getText(), mSizeUnitTV.getText()), Toast.LENGTH_SHORT).show();
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