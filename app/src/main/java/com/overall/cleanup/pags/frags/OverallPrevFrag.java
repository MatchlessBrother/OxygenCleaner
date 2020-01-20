package com.overall.cleanup.pags.frags;

import java.util.List;
import java.util.Locale;
import android.os.Bundle;
import android.view.View;
import java.util.TimeZone;
import java.util.ArrayList;
import com.overall.cleanup.R;
import android.view.ViewGroup;
import android.content.Intent;
import android.content.Context;
import android.widget.TextView;
import android.widget.ImageView;
import android.provider.Settings;
import android.os.BatteryManager;
import java.text.SimpleDateFormat;
import android.view.WindowManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.content.IntentFilter;
import com.overall.cleanup.util.OverallCdcfTool;
import com.overall.cleanup.util.OverallShdfUtil;
import com.gyf.immersionbar.ImmersionBar;
import android.content.BroadcastReceiver;
import androidx.annotation.NonNull;
import com.overall.cleanup.pags.OverallAct_BaseMain;
import androidx.annotation.Nullable;
import com.gyf.immersionbar.components.SimpleImmersionFragment;

public class OverallPrevFrag extends SimpleImmersionFragment
{
    private View stateBar;
    private LinearLayout mOSContainerView;
    private LinearLayout mCpuContainerView;
    private LinearLayout mScreenContainerView;
    private LinearLayout mProductContainerView;
    private LinearLayout mBatteryContainerView;

    public static OverallPrevFrag newInstance()
    {
        Bundle args = new Bundle();
        OverallPrevFrag fragment = new OverallPrevFrag();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.frg_preview_ment, container, false);
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

        mProductContainerView = view.findViewById(R.id.product_container);
        mCpuContainerView = view.findViewById(R.id.cpu_container);
        mBatteryContainerView = view.findViewById(R.id.battery_container);
        mScreenContainerView = view.findViewById(R.id.screen_container);
        mOSContainerView = view.findViewById(R.id.os_container);
        addProductContent();
        addScreenContent();
        addCpuContent();
        addOSContent();
    }

    private void addContent(LinearLayout containerView, List<String> nameAndValues)
    {
        containerView.removeAllViews();
        if (nameAndValues.size() % 2 == 0)
        {
            Context context = containerView.getContext();
            LinearLayout.LayoutParams params;
            LinearLayout lineContainer;
            TextView nameView;
            TextView valueView;
            for (int i = 0; i < nameAndValues.size(); i = i + 2)
            {
                lineContainer = new LinearLayout(context);
                params = new LinearLayout.LayoutParams(0, OverallShdfUtil.dip2px(context, 40));
                params.weight = 1;
                nameView = new TextView(context);
                nameView.setText(nameAndValues.get(i));
                nameView.setTextSize(15);
                lineContainer.addView(nameView, params);
                params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, OverallShdfUtil.dip2px(context, 30));
                valueView = new TextView(context);
                valueView.setText(nameAndValues.get(i + 1));
                valueView.setTextSize(13);
                lineContainer.addView(valueView, params);
                containerView.addView(lineContainer);
            }
        }
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

    private void addScreenContent()
    {
        int brightness = 0;
        try
        {
            brightness = Settings.System.getInt(getContext().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        }
        catch(Settings.SettingNotFoundException e)
        {
            e.printStackTrace();
        }
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        int brightnessLevel = brightness * 100 / 255;
        int refreshRate = (int) windowManager.getDefaultDisplay().getRefreshRate();
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        List<String> nameAndValues = new ArrayList<>();
        nameAndValues.add(getString(R.string.text_screen_brightness));
        nameAndValues.add(brightnessLevel + "%");
        nameAndValues.add(getString(R.string.text_resolution));
        nameAndValues.add(metrics.widthPixels + "×" + metrics.heightPixels);
        nameAndValues.add(getString(R.string.text_density));
        nameAndValues.add(metrics.densityDpi + "");
        nameAndValues.add(getString(R.string.text_font_scaling));
        nameAndValues.add(metrics.scaledDensity + "");
        nameAndValues.add(getString(R.string.text_refresh_rate));
        nameAndValues.add(refreshRate + "Hz");
        addContent(mScreenContainerView, nameAndValues);
    }

    private void addProductContent()
    {
        List<String> nameAndValues = new ArrayList<>();
        nameAndValues.add(getString(R.string.text_brand));
        nameAndValues.add(android.os.Build.BRAND);
        nameAndValues.add(getString(R.string.text_model));
        nameAndValues.add(android.os.Build.MODEL);
        nameAndValues.add(getString(R.string.text_host));
        nameAndValues.add(android.os.Build.HOST);
        nameAndValues.add(getString(R.string.text_serial));
        nameAndValues.add(android.os.Build.SERIAL);
        nameAndValues.add(getString(R.string.text_id));
        nameAndValues.add(android.os.Build.ID);
        nameAndValues.add(getString(R.string.text_time));
        nameAndValues.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(android.os.Build.TIME));
        addContent(mProductContainerView, nameAndValues);
    }

    private void addCpuContent()
    {
        List<String> nameAndValues = new ArrayList<>();
        nameAndValues.add(getString(R.string.text_cpu_model));
        nameAndValues.add(OverallCdcfTool.getCpuName());
        nameAndValues.add(getString(R.string.text_cpu_core_number));
        nameAndValues.add(OverallCdcfTool.getNumCores() + "");
        nameAndValues.add(getString(R.string.text_maximum_frequency));
        nameAndValues.add(OverallCdcfTool.getMaxCpuFreq());
        nameAndValues.add(getString(R.string.text_minimum_frequency));
        nameAndValues.add(OverallCdcfTool.getMinCpuFreq());
        nameAndValues.add(getString(R.string.text_current_frequency));
        nameAndValues.add(OverallCdcfTool.getCurCpuFreq());
        nameAndValues.add(getString(R.string.text_cpu_architecture));
        nameAndValues.add(OverallCdcfTool.getCpuAbi());
        addContent(mCpuContainerView, nameAndValues);
    }

    private void addOSContent()
    {
        List<String> nameAndValues = new ArrayList<>();
        nameAndValues.add(getString(R.string.text_system_release));
        nameAndValues.add(android.os.Build.VERSION.RELEASE);
        nameAndValues.add(getString(R.string.text_system_language));
        nameAndValues.add(Locale.getDefault().getLanguage());
        nameAndValues.add(getString(R.string.text_system_encoding));
        nameAndValues.add(System.getProperty("file.encoding"));
        nameAndValues.add(getString(R.string.text_system_time_zone));
        nameAndValues.add(TimeZone.getDefault().getDisplayName());

        addContent(mOSContainerView, nameAndValues);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action))
            {
                List<String> nameAndValues = new ArrayList<>();
                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
                int temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0);
                String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
                nameAndValues.add(getString(R.string.text_battery_level));
                nameAndValues.add(level + "%");
                nameAndValues.add(getString(R.string.text_battery_temperature));
                nameAndValues.add(temperature / 10 + "℃" + "/" + (int) (temperature / 10 * 1.8 + 32) + "℉");
                nameAndValues.add(getString(R.string.text_battery_voltage));
                nameAndValues.add(voltage + "mV");
                nameAndValues.add(getString(R.string.text_battery_technology));
                nameAndValues.add(technology);
                nameAndValues.add(getString(R.string.text_battery_status));
                nameAndValues.add(getBatteryStatus(status));
                addContent(mBatteryContainerView, nameAndValues);
            }
        }

        private String getBatteryStatus(int status)
        {
            switch (status)
            {
                case BatteryManager.BATTERY_STATUS_FULL:
                    return getString(R.string.text_battery_full);
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    return getString(R.string.text_battery_charging);
                default:
                    return getString(R.string.text_battery_discharging);
            }
        }
    };

    public void initImmersionBar()
    {
        ImmersionBar.with(this).statusBarView(stateBar).init();
    }
}