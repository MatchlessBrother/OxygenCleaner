package com.overall.cleanup.pags.frags;

import com.overall.cleanup.R;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.LayoutInflater;
import com.overall.cleanup.util.OverallCdcfTool;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.moos.library.CircleProgressView;

public class OverallPuFrag extends Fragment
{
    private TextView mPercent;
    private TextView mPercentDetail;
    private CircleProgressView circleProgressView;

    @Nullable
    @Override
public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
        {
        View view =  LayoutInflater.from(getActivity()).inflate(R.layout.frg_cpu_ment,container,false);
        return view;
        }

@Override
public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
        {
        super.onViewCreated(view, savedInstanceState);
        mPercent = view.findViewById(R.id.percent);
        circleProgressView = view.findViewById(R.id.progress_cpu);
        mPercent.setText((int) OverallCdcfTool.getCpuTemperatureFinder(getContext()) + "℃");
        circleProgressView.setEndProgress((int) OverallCdcfTool.getCpuTemperatureFinder(getContext()));
        circleProgressView.setProgressDuration(2000);
        circleProgressView.startProgressAnimation();
        }
        }