package com.oxygen.cleaner.pags;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import com.oxygen.cleaner.R;
import android.content.Intent;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.support.v4.view.ViewPager;
import com.oxygen.cleaner.apters.base.OxygenApter_Frg;
import android.support.v4.view.GravityCompat;
import com.oxygen.cleaner.pags.frags.OxygenPrevFrag;
import android.support.v4.widget.DrawerLayout;
import com.oxygen.cleaner.pags.frags.OxygenCleanFrag;
import com.oxygen.cleaner.pags.frags.OxygenStatesFrag;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;

public class OxygenAct_BaseMain extends AppCompatActivity
{
    private  DrawerLayout drawer;
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Window window = getWindow();
            if (window != null)
            {
                window.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
            }
        }
        setContentView(R.layout.mainact);
        drawer = findViewById(R.id.drawer_layout);
        final ImageView imageView = findViewById(R.id.title_menu);
        final FrameLayout frameLayout = findViewById(R.id.title);
        final TextView textView = findViewById(R.id.title_title);
        textView.setText("");
        final BubbleNavigationConstraintView navigationConstraintView = findViewById(R.id.bottomnavigationview);
        OxygenApter_Frg adapter = new OxygenApter_Frg(getSupportFragmentManager());
        adapter.addFragment(OxygenCleanFrag.newInstance());
        adapter.addFragment(OxygenStatesFrag.newInstance());
        adapter.addFragment(OxygenPrevFrag.newInstance());

        final ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {
            public void onPageSelected(int position)
            {
                switch (position)
                {
                    case 0:
                        frameLayout.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.background_material_light));
                        navigationConstraintView.setCurrentActiveItem(0);
                        textView.setText("");
                        break;
                    case 1:
                        frameLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.color.background_material_light));
                        navigationConstraintView.setCurrentActiveItem(1);
                        textView.setTextColor(Color.parseColor("#000000"));
                        textView.setText("Preview");
                        break;
                    case 2:
                        frameLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bdfghj_status));
                        navigationConstraintView.setCurrentActiveItem(2);
                        textView.setTextColor(Color.parseColor("#ffffff"));
                        textView.setText("Status");
                        break;
                }
            }
        });
        viewPager.setAdapter(adapter);
        navigationConstraintView.setNavigationChangeListener(new BubbleNavigationChangeListener()
        {
            public void onNavigationChanged(View view, int position)
            {
                viewPager.setCurrentItem(position);
            }
        });

        findViewById(R.id.ll_whiteList).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivity(new Intent(OxygenAct_BaseMain.this, OxygenAct_White.class));
            }
        });

        findViewById(R.id.ll_about).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                startActivity(new Intent(OxygenAct_BaseMain.this, OxygenAoutAct.class));
            }
        });

        findViewById(R.id.ll_update).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                toDetails();
            }
        });

        findViewById(R.id.ll_rate).setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                toDetails();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if(!drawer.isDrawerOpen(GravityCompat.START))
                {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
    }

    public void drawerToggleClick()
    {
        if(null != drawer && !drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    public void toDetails()
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=" + getPackageName()));
            intent.setPackage("com.android.vending");
            if (intent.resolveActivity(getPackageManager()) != null)
            {
                startActivity(intent);
            }
            else//没有应用市场，通过浏览器跳转到Google Play
            {

                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                intent2.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                if (intent2.resolveActivity(getPackageManager()) != null)
                {
                    startActivity(intent2);
                }
            }
        }
        catch (Exception e)
        {
           e.printStackTrace();
        }
    }
}