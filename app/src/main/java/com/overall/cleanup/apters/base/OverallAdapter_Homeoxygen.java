package com.overall.cleanup.apters.base;

import java.util.ArrayList;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class OverallAdapter_Homeoxygen extends FragmentPagerAdapter
{
    private String[] mTitles;
    private ArrayList<Fragment> mFragments;

    public OverallAdapter_Homeoxygen(FragmentManager fm, ArrayList<Fragment> mFragments, String[] mTitles)
    {
        super(fm);
        this.mTitles=mTitles;
        this.mFragments=mFragments;
    }

    public int getCount()
    {
        return mFragments.size();
    }

    public CharSequence getPageTitle(int position)
    {
        return mTitles[position];
    }

    public Fragment getItem(int position)
    {
        return mFragments.get(position);
    }
}