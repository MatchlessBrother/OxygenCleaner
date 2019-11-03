package com.oxygen.cleaner.pags.frags;

import java.util.Locale;
import android.os.Bundle;
import android.view.View;
import com.oxygen.cleaner.R;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.LayoutInflater;
import com.oxygen.cleaner.pags.OxygenAct_Sder;
import com.oxygen.cleaner.pags.OxygenAct_Cldan;
import com.oxygen.cleaner.pags.OxygenAct_Basdst;
import com.oxygen.cleaner.pags.OxygenAct_asdler;
import com.oxygen.cleaner.util.OxygenCdcfTool;
import com.gyf.immersionbar.ImmersionBar;
import com.oxygen.cleaner.util.OxygenStgddeTool;
import com.oxygen.cleaner.pags.OxygenAct_BaseMain;
import android.support.annotation.NonNull;
import com.oxygen.cleaner.util.OxygenFilasdeUtil;
import android.support.annotation.Nullable;
import com.monke.mprogressbar.MRingProgressBar;
import com.gyf.immersionbar.components.SimpleImmersionFragment;

public class OxygenCleanFrag extends SimpleImmersionFragment
{
    private MRingProgressBar cpuMRingProgressBar;
    private TextView cpuPercent;
    private TextView ramDetail;
    private TextView ramPercent;
    private TextView romDetail;
    private TextView romPercent;
    private View stateBar;

    public static OxygenCleanFrag newInstance()
    {
        Bundle args = new Bundle();
        OxygenCleanFrag fragment = new OxygenCleanFrag();
        fragment.setArguments(args);
        return fragment;
    }

    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.frg_home_ment, container, false);
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
                if(getActivity() instanceof OxygenAct_BaseMain)
                {
                    ((OxygenAct_BaseMain)getActivity()).drawerToggleClick();
                }
            }
        });
        cpuMRingProgressBar = view.findViewById(R.id.progress_cpu_point);
        cpuPercent = view.findViewById(R.id.progress_cpu_percent);

        ramDetail = view.findViewById(R.id.progress_ram_detail);
        ramPercent = view.findViewById(R.id.progress_ram_percent);

        romDetail = view.findViewById(R.id.progress_rom_detail);
        romPercent = view.findViewById(R.id.progress_rom_percent);

        long[] romSizes = OxygenStgddeTool.getROMTotalAvailable();
        long[] ramSizes = OxygenStgddeTool.getRAMTotalAvailable(view.getContext());
        cpuPercent.setText((int) OxygenCdcfTool.getCpuTemperatureFinder(getContext()) + "℃");
        ramPercent.setText((int) ((ramSizes[0] - ramSizes[1]) / (float) ramSizes[0] * 100) + "%");
        romPercent.setText((int) ((romSizes[0] - romSizes[1]) / (float) romSizes[0] * 100) + "%");
        cpuMRingProgressBar.setDurProgressWithAnim(OxygenCdcfTool.getCpuTemperatureFinder(getContext()));
        String[] romTotalTexts = OxygenFilasdeUtil.fileSize(romSizes[0]);
        String[] romUsedTexts = OxygenFilasdeUtil.fileSize(romSizes[0] - romSizes[1]);
        String[] ramTotalTexts = OxygenFilasdeUtil.fileSize(ramSizes[0]);
        String[] ramUsedTexts = OxygenFilasdeUtil.fileSize(ramSizes[0] - ramSizes[1]);
        ramDetail.setText(String.format(Locale.getDefault(), "%s%s/%s%s", ramUsedTexts[0], ramUsedTexts[1], ramTotalTexts[0], ramTotalTexts[1]));
        romDetail.setText(String.format(Locale.getDefault(), "%s%s/%s%s", romUsedTexts[0], romUsedTexts[1], romTotalTexts[0], romTotalTexts[1]));

        view.findViewById(R.id.ll_clean).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivity(new Intent(getActivity(), OxygenAct_Cldan.class));
            }
        });

        view.findViewById(R.id.ll_boost).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivity(new Intent(getActivity(), OxygenAct_Basdst.class));
            }
        });

        view.findViewById(R.id.ll_saver).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivity(new Intent(getActivity(), OxygenAct_Sder.class));
            }
        });

        view.findViewById(R.id.ll_cooler).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivity(new Intent(getActivity(), OxygenAct_asdler.class));
            }
        });
    }

    public void onDestroyView()
    {
        super.onDestroyView();
    }

    public void initImmersionBar()
    {
        ImmersionBar.with(this).statusBarView(stateBar).init();
    }
}
