package com.overall.cleanup.pags.frags;

import java.util.Locale;
import android.os.Bundle;
import android.view.View;
import com.overall.cleanup.R;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.TextView;
import android.content.Context;
import android.widget.ImageView;
import android.os.BatteryManager;
import android.widget.ProgressBar;
import android.view.LayoutInflater;
import android.content.IntentFilter;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import com.overall.cleanup.pags.OverallAct_Sder;
import com.overall.cleanup.pags.OverallAct_Cldan;
import com.overall.cleanup.pags.OverallAct_asdler;
import com.overall.cleanup.pags.OverallAct_Basdst;
import com.overall.cleanup.util.OverallCdcfTool;
import com.gyf.immersionbar.ImmersionBar;
import android.content.BroadcastReceiver;
import com.overall.cleanup.pags.OverallAct_BaseMain;
import android.support.annotation.NonNull;
import com.overall.cleanup.util.OverallStgddeTool;
import android.support.annotation.Nullable;
import com.overall.cleanup.util.OverallFilasdeUtil;
import java.util.concurrent.ScheduledExecutorService;
import com.gyf.immersionbar.components.SimpleImmersionFragment;

public class OverallStatesFrag extends SimpleImmersionFragment
{
    private View stateBar;
    ScheduledExecutorService mExecutorService;
    TextView mTemperatureTV, mCpuTV, mRomTV, mRamTV;
    ProgressBar mBatteryProgressBar, mCpuProgressBar, mRomProgressBar, mRamProgressBar;

    public static OverallStatesFrag newInstance()
    {
        Bundle args = new Bundle();
        OverallStatesFrag fragment = new OverallStatesFrag();
        fragment.setArguments(args);
        return fragment;
    }

    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser)
        {
            if (mExecutorService == null)
            {
                mExecutorService = Executors.newSingleThreadScheduledExecutor();
                mExecutorService.scheduleWithFixedDelay(new Runnable()
                {
                    public void run()
                    {
                        final int cpuUsage = (int) (OverallCdcfTool.getCpuUsage() * 100f);
                        mCpuTV.post(new Runnable()
                        {
                            public void run()
                            {
                                mCpuTV.setText(String.format(Locale.getDefault(), "%d%%", cpuUsage));
                                mCpuProgressBar.setProgress(cpuUsage);
                            }
                        });
                    }
                }, 0, 1, TimeUnit.SECONDS);
            }
        }
        else
        {
            if(mExecutorService != null)
            {
                mExecutorService.shutdown();
                mExecutorService = null;
            }
        }
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.frg_status_ment, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        stateBar = view.findViewById(R.id.statebar);
        final ImageView imageView = view.findViewById(R.id.title_menu);
        imageView.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(getActivity() instanceof OverallAct_BaseMain)
                {
                    ((OverallAct_BaseMain)getActivity()).drawerToggleClick();
                }
            }
        });

        mTemperatureTV = view.findViewById(R.id.tv_temperature);
        mCpuTV = view.findViewById(R.id.tv_cpu);
        mRomTV = view.findViewById(R.id.tv_rom);
        mRamTV = view.findViewById(R.id.tv_ram);
        mBatteryProgressBar = view.findViewById(R.id.progress_battery);
        mCpuProgressBar = view.findViewById(R.id.progress_cpu);
        mRomProgressBar = view.findViewById(R.id.progress_rom);
        mRamProgressBar = view.findViewById(R.id.progress_ram);

        long[] romSizes = OverallStgddeTool.getROMTotalAvailable();
        long[] ramSizes = OverallStgddeTool.getRAMTotalAvailable(view.getContext());

        mRomProgressBar.setProgress((int) ((romSizes[0] - romSizes[1]) / (float) romSizes[0] * 100));
        mRamProgressBar.setProgress((int) ((ramSizes[0] - ramSizes[1]) / (float) ramSizes[0] * 100));

        String[] romTotalTexts = OverallFilasdeUtil.fileSize(romSizes[0]);
        String[] romUsedTexts = OverallFilasdeUtil.fileSize(romSizes[0] - romSizes[1]);

        String[] ramTotalTexts = OverallFilasdeUtil.fileSize(ramSizes[0]);
        String[] ramUsedTexts = OverallFilasdeUtil.fileSize(ramSizes[0] - ramSizes[1]);

        mRomTV.setText(String.format(Locale.getDefault(), "%s%s/%s%s", romUsedTexts[0], romUsedTexts[1], romTotalTexts[0], romTotalTexts[1]));
        mRamTV.setText(String.format(Locale.getDefault(), "%s%s/%s%s", ramUsedTexts[0], ramUsedTexts[1], ramTotalTexts[0], ramTotalTexts[1]));

        view.findViewById(R.id.cl_battery).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivity(new Intent(getActivity(), OverallAct_Sder.class));
            }
        });

        view.findViewById(R.id.cl_cpu).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivity(new Intent(getActivity(), OverallAct_asdler.class));
            }
        });

        view.findViewById(R.id.cl_rom).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivity(new Intent(getActivity(), OverallAct_Cldan.class));
            }
        });

        view.findViewById(R.id.cl_ram).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivity(new Intent(getActivity(), OverallAct_Basdst.class));
            }
        });
    }

    public void onResume()
    {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        getContext().registerReceiver(mBroadcastReceiver, filter);
    }

    public void onPause()
    {
        super.onPause();
        getContext().unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction()))
            {
                int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10;
                mTemperatureTV.setText(String.format(Locale.getDefault(), "%d℃/%d℉", temperature, (int) (temperature * 1.8 + 32)));
                mBatteryProgressBar.setProgress(temperature);
            }
        }
    };

    public void initImmersionBar()
    {
        ImmersionBar.with(this).statusBarView(stateBar).init();
    }
}