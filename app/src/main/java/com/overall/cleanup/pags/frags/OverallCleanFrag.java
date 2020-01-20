package com.overall.cleanup.pags.frags;

import java.util.Locale;
import android.os.Bundle;
import android.view.View;
import com.overall.cleanup.R;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.LayoutInflater;
import com.overall.cleanup.pags.OverallAct_Sder;
import com.overall.cleanup.pags.OverallAct_Cldan;
import com.overall.cleanup.pags.OverallAct_Basdst;
import com.overall.cleanup.pags.OverallAct_asdler;
import com.overall.cleanup.util.OverallCdcfTool;
import com.gyf.immersionbar.ImmersionBar;
import com.overall.cleanup.util.OverallStgddeTool;
import com.overall.cleanup.pags.OverallAct_BaseMain;
import androidx.annotation.NonNull;
import com.overall.cleanup.util.OverallFilasdeUtil;
import androidx.annotation.Nullable;
import com.monke.mprogressbar.MRingProgressBar;
import com.gyf.immersionbar.components.SimpleImmersionFragment;

public class OverallCleanFrag extends SimpleImmersionFragment
{
    private MRingProgressBar cpuMRingProgressBar;
    private TextView cpuPercent;
    private TextView ramDetail;
    private TextView ramPercent;
    private TextView romDetail;
    private TextView romPercent;
    private View stateBar;

    public static OverallCleanFrag newInstance()
    {
        Bundle args = new Bundle();
        OverallCleanFrag fragment = new OverallCleanFrag();
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
                if(getActivity() instanceof OverallAct_BaseMain)
                {
                    ((OverallAct_BaseMain)getActivity()).drawerToggleClick();
                }
            }
        });
        cpuMRingProgressBar = view.findViewById(R.id.progress_cpu_point);
        cpuPercent = view.findViewById(R.id.progress_cpu_percent);

        ramDetail = view.findViewById(R.id.progress_ram_detail);
        ramPercent = view.findViewById(R.id.progress_ram_percent);

        romDetail = view.findViewById(R.id.progress_rom_detail);
        romPercent = view.findViewById(R.id.progress_rom_percent);

        long[] romSizes = OverallStgddeTool.getROMTotalAvailable();
        long[] ramSizes = OverallStgddeTool.getRAMTotalAvailable(view.getContext());
        cpuPercent.setText((int) OverallCdcfTool.getCpuTemperatureFinder(getContext()) + "℃");
        ramPercent.setText((int) ((ramSizes[0] - ramSizes[1]) / (float) ramSizes[0] * 100) + "%");
        romPercent.setText((int) ((romSizes[0] - romSizes[1]) / (float) romSizes[0] * 100) + "%");
        cpuMRingProgressBar.setDurProgressWithAnim(OverallCdcfTool.getCpuTemperatureFinder(getContext()));
        String[] romTotalTexts = OverallFilasdeUtil.fileSize(romSizes[0]);
        String[] romUsedTexts = OverallFilasdeUtil.fileSize(romSizes[0] - romSizes[1]);
        String[] ramTotalTexts = OverallFilasdeUtil.fileSize(ramSizes[0]);
        String[] ramUsedTexts = OverallFilasdeUtil.fileSize(ramSizes[0] - ramSizes[1]);
        ramDetail.setText(String.format(Locale.getDefault(), "%s%s/%s%s", ramUsedTexts[0], ramUsedTexts[1], ramTotalTexts[0], ramTotalTexts[1]));
        romDetail.setText(String.format(Locale.getDefault(), "%s%s/%s%s", romUsedTexts[0], romUsedTexts[1], romTotalTexts[0], romTotalTexts[1]));

        view.findViewById(R.id.ll_clean).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivity(new Intent(getActivity(), OverallAct_Cldan.class));
            }
        });

        view.findViewById(R.id.ll_boost).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivity(new Intent(getActivity(), OverallAct_Basdst.class));
            }
        });

        view.findViewById(R.id.ll_saver).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivity(new Intent(getActivity(), OverallAct_Sder.class));
            }
        });

        view.findViewById(R.id.ll_cooler).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivity(new Intent(getActivity(), OverallAct_asdler.class));
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
