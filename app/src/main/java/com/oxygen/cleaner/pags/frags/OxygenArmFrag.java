package com.oxygen.cleaner.pags.frags;

import com.oxygen.cleaner.R;
import java.util.Locale;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.LayoutInflater;
import com.oxygen.cleaner.util.OxygenFilasdeUtil;
import com.oxygen.cleaner.util.OxygenStgddeTool;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.moos.library.CircleProgressView;

public class OxygenArmFrag extends Fragment
{
    private TextView mPercent;
    private TextView mPercentDetail;
    private CircleProgressView circleProgressView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view =  LayoutInflater.from(getActivity()).inflate(R.layout.frgram_ment,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        mPercent = view.findViewById(R.id.percent);
        mPercentDetail = view.findViewById(R.id.percentdetail);
        circleProgressView = view.findViewById(R.id.progress_ram);
        long[] ramSizes = OxygenStgddeTool.getRAMTotalAvailable(view.getContext());
        String[] ramTotalTexts = OxygenFilasdeUtil.fileSize(ramSizes[0]);
        String[] ramUsedTexts = OxygenFilasdeUtil.fileSize(ramSizes[0] - ramSizes[1]);
        mPercent.setText((int)((ramSizes[0] - ramSizes[1]) / (float) ramSizes[0] * 100) + "%");
        circleProgressView.setEndProgress((int) ((ramSizes[0] - ramSizes[1]) / (float) ramSizes[0] * 100));
        mPercentDetail.setText(String.format(Locale.getDefault(), "%s%s/%s%s", ramUsedTexts[0], ramUsedTexts[1], ramTotalTexts[0], ramTotalTexts[1]));
        circleProgressView.setProgressDuration(2000);
        circleProgressView.startProgressAnimation();
    }
}