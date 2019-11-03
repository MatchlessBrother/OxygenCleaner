package com.oxygen.cleaner.pags;

import java.util.List;
import android.Manifest;
import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;
import android.widget.Toast;
import com.oxygen.cleaner.R;
import android.content.Intent;
import android.widget.TextView;
import android.widget.ImageView;
import android.animation.Animator;
import android.widget.ProgressBar;
import com.oxygen.cleaner.model.OxygenStory;
import com.oxygen.cleaner.model.OxygenJuedkc;
import io.reactivex.functions.Consumer;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import com.oxygen.cleaner.util.OxygenShdfUtil;
import com.oxygen.cleaner.view.TickBarsView;
import com.gyf.immersionbar.ImmersionBar;
import com.oxygen.cleaner.util.OxygenFifasdgGl;
import com.oxygen.cleaner.util.OxygenFilasdeUtil;
import com.oxygen.cleaner.apters.base.OxygenApter_Cesasd;
import com.oxygen.cleaner.apters.OxygenAdapter_Jkasd;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.animation.AnimatorListenerAdapter;
import com.oxygen.cleaner.apters.OxygenAdapter_Selectasd;
import com.oxygen.cleaner.apters.OxygenAdapter_Thumbasic;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class OxygenAct_Cldan extends AppCompatActivity implements OxygenFifasdgGl.ScanListener
{
    OxygenFifasdgGl mFileManager;
    ImageView mArrowCacheIV, mArrowTempIV, mArrowLeftoverIV, mArrowThumbIV;
    ImageView mCacheIV, mTempIV, mLeftoverIV, mThumbIV;
    ProgressBar mCacheProgress, mTempProgress, mLeftoverProgress, mThumbProgress;
    RecyclerView mCacheRV;
    RecyclerView mTempRV;
    RecyclerView mLeftoverRV;
    RecyclerView mThumbRV;
    TextView mScanPathTV, mSizeTV, mUnitTV;
    OxygenApter_Cesasd mCacheAdapter;
    OxygenAdapter_Jkasd mTempAdapter, mLeftoverAdapter;
    OxygenAdapter_Thumbasic mThumbAdapter;

    @SuppressLint("CheckResult")
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cleanact);
        ImmersionBar.with(this).statusBarView(R.id.top_bg).init();
        mScanPathTV = findViewById(R.id.tv_scan_path);
        mSizeTV = findViewById(R.id.tv_size);
        mUnitTV = findViewById(R.id.tv_unit);

        mCacheRV = findViewById(R.id.rv_cache);
        mTempRV = findViewById(R.id.rv_temp);
        mLeftoverRV = findViewById(R.id.rv_leftover);
        mThumbRV = findViewById(R.id.rv_thumb);

        mCacheRV.setAdapter(new OxygenApter_Cesasd());
        mTempRV.setAdapter(new OxygenApter_Cesasd());
        mLeftoverRV.setAdapter(new OxygenApter_Cesasd());
        mThumbRV.setAdapter(new OxygenApter_Cesasd());

        mArrowCacheIV = findViewById(R.id.iv_arrow_cache);
        mArrowTempIV = findViewById(R.id.iv_arrow_temp);
        mArrowLeftoverIV = findViewById(R.id.iv_arrow_leftover);
        mArrowThumbIV = findViewById(R.id.iv_arrow_thumb);

        mCacheIV = findViewById(R.id.iv_cache);
        mTempIV = findViewById(R.id.iv_temp);
        mLeftoverIV = findViewById(R.id.iv_leftover);
        mThumbIV = findViewById(R.id.iv_thumb);

        mCacheProgress = findViewById(R.id.progress_cache);
        mTempProgress = findViewById(R.id.progress_temp);
        mLeftoverProgress = findViewById(R.id.progress_leftover);
        mThumbProgress = findViewById(R.id.progress_thumb);

        mCacheIV.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v) {
                mCacheAdapter.select(!v.isActivated());
            }
        });

        mTempIV.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                mTempAdapter.select(!v.isActivated());
            }
        });
        mLeftoverIV.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                mLeftoverAdapter.select(!v.isActivated());
            }
        });
        mThumbIV.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                mThumbAdapter.select(!v.isActivated());
            }
        });

        findViewById(R.id.ll_cache).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                mArrowCacheIV.animate().rotation(mCacheRV.getVisibility() == View.GONE ? 0 : -90).start();
                mCacheRV.setVisibility(mCacheRV.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
        findViewById(R.id.ll_temp).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                mArrowTempIV.animate().rotation(mTempRV.getVisibility() == View.GONE ? 0 : -90).start();
                mTempRV.setVisibility(mTempRV.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
        findViewById(R.id.ll_leftover).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                mArrowLeftoverIV.animate().rotation(mLeftoverRV.getVisibility() == View.GONE ? 0 : -90).start();
                mLeftoverRV.setVisibility(mLeftoverRV.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
        findViewById(R.id.ll_thumb).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                mArrowThumbIV.animate().rotation(mThumbRV.getVisibility() == View.GONE ? 0 : -90).start();
                mThumbRV.setVisibility(mThumbRV.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });

        findViewById(R.id.btn_clean).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                List<OxygenJuedkc> cleanJunks = new ArrayList<>();
                for (OxygenStory item : mCacheAdapter.getItems())
                {
                    for (OxygenJuedkc junk : item.getJunks())
                    {
                        if (junk.isDelete())
                        {
                            cleanJunks.add(junk);
                        }
                    }
                }
                for (OxygenJuedkc item : mTempAdapter.getItems())
                {
                    if (item.isDelete()) {
                        cleanJunks.add(item);
                    }
                }
                for (OxygenJuedkc item : mLeftoverAdapter.getItems()) {
                    if (item.isDelete())
                    {
                        cleanJunks.add(item);
                    }
                }
                for (OxygenJuedkc item : mThumbAdapter.getItems())
                {
                    if (item.isDelete())
                    {
                        cleanJunks.add(item);
                    }
                }

                if (cleanJunks.isEmpty())
                {
                    Toast.makeText(OxygenAct_Cldan.this, getString(R.string.text_no_clean), Toast.LENGTH_SHORT).show();
                    return;
                }
                mFileManager.clean(cleanJunks);
                View cacheView = findViewById(R.id.view_cache);
                View tempView = findViewById(R.id.view_temp);
                View leftoverView = findViewById(R.id.view_leftover);
                View thumbView = findViewById(R.id.view_thumb);
                mCacheRV.setVisibility(View.GONE);
                mTempRV.setVisibility(View.GONE);
                mLeftoverRV.setVisibility(View.GONE);
                mThumbRV.setVisibility(View.GONE);
                findViewById(R.id.ll_cache).setTag("");
                findViewById(R.id.view_scroll).requestLayout();
                v.animate().translationX(-OxygenShdfUtil.getScreenWidth(getApplication())).setDuration(500).start();
                thumbView.animate().translationX(-OxygenShdfUtil.getScreenWidth(getApplication())).setDuration(500).setStartDelay(300).start();
                leftoverView.animate().translationX(-OxygenShdfUtil.getScreenWidth(getApplication())).setDuration(500).setStartDelay(600).start();
                tempView.animate().translationX(-OxygenShdfUtil.getScreenWidth(getApplication())).setDuration(500).setStartDelay(900).start();
                cacheView.animate().translationX(-OxygenShdfUtil.getScreenWidth(getApplication())).setDuration(500).setStartDelay(1200).start();

                final long totalSize = getCleanSize();
                final ProgressBar releasedProgress = findViewById(R.id.progress_released);
                releasedProgress.setVisibility(View.VISIBLE);
                ValueAnimator sizeAnimator = ValueAnimator.ofFloat(totalSize, 0);
                sizeAnimator.setDuration(1000);
                sizeAnimator.setStartDelay(500);
                sizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
                {
                    public void onAnimationUpdate(ValueAnimator animation)
                    {
                        float size = (float) animation.getAnimatedValue();
                        String[] sizes = OxygenFilasdeUtil.fileSizeMB(size);
                        mSizeTV.setText(sizes[0]);
                        mUnitTV.setText(sizes[1]);
                        releasedProgress.setProgress((int) ((totalSize - size) / totalSize * 100f));
                    }
                });
                sizeAnimator.addListener(new AnimatorListenerAdapter()
                {
                    public void onAnimationEnd(Animator animation)
                    {
                        super.onAnimationEnd(animation);
                        releasedProgress.setVisibility(View.INVISIBLE);
                        findViewById(R.id.view_size).setVisibility(View.GONE);
                        findViewById(R.id.view_released).setVisibility(View.VISIBLE);
                        TextView releasedSizeTV = findViewById(R.id.tv_released_size);
                        releasedSizeTV.setText(OxygenFilasdeUtil.sizeFormat(totalSize));
                        TickBarsView tickView = findViewById(R.id.tv_tick);
                        tickView.start();

                        View junkView = findViewById(R.id.view_junk);
                        final View coolerView = findViewById(R.id.view_cooler);
                        coolerView.setTranslationY(junkView.getHeight());
                        coolerView.animate().setDuration(1000).translationY(0).setListener(new AnimatorListenerAdapter()
                        {
                                    public void onAnimationEnd(Animator animation)
                                    {
                                        coolerView.setOnClickListener(new View.OnClickListener()
                                        {
                                            public void onClick(View v)
                                            {
                                                finish();
                                                startActivity(new Intent(OxygenAct_Cldan.this, OxygenAct_asdler.class));
                                            }
                                        });
                                    }
                                })
                                .start();
                        junkView.setVisibility(View.GONE);
                        coolerView.setVisibility(View.VISIBLE);
                    }
                });
                sizeAnimator.start();
            }
        });

        findViewById(R.id.fl_back).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                finish();
            }
        });

        mCacheAdapter = new OxygenApter_Cesasd();
        mTempAdapter = new OxygenAdapter_Jkasd();
        mLeftoverAdapter = new OxygenAdapter_Jkasd();
        mThumbAdapter = new OxygenAdapter_Thumbasic();

        mCacheAdapter.setSelectListener(new OxygenAdapter_Selectasd.SelectListener()
        {
            public void onAllSelect()
            {
                mCacheIV.setImageResource(R.drawable.sasdelect);
                mCacheIV.setActivated(true);
                refreshSize();
            }

            public void onNotSelect()
            {
                mCacheIV.setImageResource(R.drawable.seleasct_not);
                mCacheIV.setActivated(false);
                refreshSize();
            }

            public void onPartSelect()
            {
                mCacheIV.setImageResource(R.drawable.seasdlect_pasrt);
                mCacheIV.setActivated(false);
                refreshSize();
            }
        });
        mTempAdapter.setSelectListener(new OxygenAdapter_Selectasd.SelectListener()
        {
            public void onAllSelect()
            {
                mTempIV.setImageResource(R.drawable.sasdelect);
                mTempIV.setActivated(true);
                refreshSize();
            }

            public void onNotSelect()
            {
                mTempIV.setImageResource(R.drawable.seleasct_not);
                mTempIV.setActivated(false);
                refreshSize();
            }

            public void onPartSelect()
            {
                mTempIV.setImageResource(R.drawable.seasdlect_pasrt);
                mTempIV.setActivated(false);
                refreshSize();
            }
        });
        mLeftoverAdapter.setSelectListener(new OxygenAdapter_Selectasd.SelectListener()
        {
            public void onAllSelect()
            {
                mLeftoverIV.setImageResource(R.drawable.sasdelect);
                mLeftoverIV.setActivated(true);
                refreshSize();
            }

            public void onNotSelect()
            {
                mLeftoverIV.setImageResource(R.drawable.seleasct_not);
                mLeftoverIV.setActivated(false);
                refreshSize();
            }

            public void onPartSelect()
            {
                mLeftoverIV.setImageResource(R.drawable.seasdlect_pasrt);
                mLeftoverIV.setActivated(false);
                refreshSize();
            }
        });
        mThumbAdapter.setSelectListener(new OxygenAdapter_Selectasd.SelectListener()
        {
            public void onAllSelect()
            {
                mThumbIV.setImageResource(R.drawable.sasdelect);
                mThumbIV.setActivated(true);
                refreshSize();
            }

            public void onNotSelect()
            {
                mThumbIV.setImageResource(R.drawable.seleasct_not);
                mThumbIV.setActivated(false);
                refreshSize();
            }

            public void onPartSelect()
            {
                mThumbIV.setImageResource(R.drawable.seasdlect_pasrt);
                mThumbIV.setActivated(false);
                refreshSize();
            }
        });

        mCacheRV.setAdapter(mCacheAdapter);
        mTempRV.setAdapter(mTempAdapter);
        mLeftoverRV.setAdapter(mLeftoverAdapter);
        mThumbRV.setAdapter(mThumbAdapter);

        mFileManager = new OxygenFifasdgGl(this);
        mFileManager.setScanListener(this);

        new RxPermissions(this).request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Consumer<Boolean>()
        {
            public void accept(Boolean grant) throws Exception
            {
                if (grant) {
                    mFileManager.scan();
                }
            }
        });
    }

    private long getCleanSize()
    {
        long size = 0;
        for (OxygenStory item : mCacheAdapter.getItems())
        {
            for (OxygenJuedkc junk : item.getJunks()
            ) {
                if (junk.isDelete())
                {
                    size += junk.getSize();
                }
            }
        }
        for (OxygenJuedkc item : mTempAdapter.getItems())
        {
            if (item.isDelete())
            {
                size += item.getSize();
            }
        }
        for (OxygenJuedkc item : mLeftoverAdapter.getItems())
        {
            if (item.isDelete())
            {
                size += item.getSize();
            }
        }
        for (OxygenJuedkc item : mThumbAdapter.getItems())
        {
            if (item.isDelete())
            {
                size += item.getSize();
            }
        }
        return size;
    }

    private void refreshSize()
    {
        refreshSize(getCleanSize());
    }

    private void refreshSize(long size)
    {
        String[] sizes = OxygenFilasdeUtil.fileSize(size);
        mSizeTV.setText(sizes[0]);
        mUnitTV.setText(sizes[1]);
    }

    protected void onDestroy()
    {
        super.onDestroy();
        mFileManager.destroy();
    }

    public void onScanPath(String path)
    {
        mScanPathTV.setText(path);
    }

    public void onScanSize(long size)
    {
        refreshSize(size);
    }

    public void onScanCache(List<OxygenStory> caches)
    {
        mCacheProgress.setVisibility(View.GONE);
        mCacheIV.setActivated(true);
        mCacheIV.setVisibility(View.VISIBLE);
        mCacheAdapter.addItems(caches);
        mCacheAdapter.notifyDataSetChanged();
        long size = 0;
        for (OxygenStory item : mCacheAdapter.getItems())
        {
            size += item.getSize();
        }
        TextView cacheSizeTV = findViewById(R.id.tv_cache_size);
        cacheSizeTV.setText(OxygenFilasdeUtil.sizeFormat(size));
    }

    public void onScanTemp(List<OxygenJuedkc> tempJunks)
    {
        mTempProgress.setVisibility(View.GONE);
        mTempIV.setActivated(true);
        mTempIV.setVisibility(View.VISIBLE);
        mTempAdapter.addItems(tempJunks);
        mTempAdapter.notifyDataSetChanged();
        long size = 0;
        for (OxygenJuedkc item : mTempAdapter.getItems())
        {
            size += item.getSize();
        }
        TextView tempSizeTV = findViewById(R.id.tv_temp_size);
        tempSizeTV.setText(OxygenFilasdeUtil.sizeFormat(size));
    }

    public void onScanLeftover(List<OxygenJuedkc> leftoverJunks)
    {
        mLeftoverProgress.setVisibility(View.GONE);
        mLeftoverIV.setActivated(true);
        mLeftoverIV.setVisibility(View.VISIBLE);
        mLeftoverAdapter.addItems(leftoverJunks);
        mLeftoverAdapter.notifyDataSetChanged();
        long size = 0;
        for (OxygenJuedkc item : mLeftoverAdapter.getItems())
        {
            size += item.getSize();
        }
        TextView leftoverSizeTV = findViewById(R.id.tv_leftover_size);
        leftoverSizeTV.setText(OxygenFilasdeUtil.sizeFormat(size));
    }

    public void onScanThumb(List<OxygenJuedkc> thumbJunks)
    {
        mThumbProgress.setVisibility(View.GONE);
        mThumbIV.setActivated(true);
        mThumbIV.setVisibility(View.VISIBLE);
        mThumbAdapter.addItems(thumbJunks);
        mThumbAdapter.notifyDataSetChanged();
        long size = 0;
        for (OxygenJuedkc item : mThumbAdapter.getItems())
        {
            size += item.getSize();
        }
        TextView thumbSizeTV = findViewById(R.id.tv_thumb_size);
        thumbSizeTV.setText(OxygenFilasdeUtil.sizeFormat(size));
    }

    public void onScanCompletion(long size)
    {
        mScanPathTV.setVisibility(View.INVISIBLE);
        findViewById(R.id.btn_clean).setVisibility(View.VISIBLE);
    }
}
