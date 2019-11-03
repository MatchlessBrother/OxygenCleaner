package com.oxygen.cleaner.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.oxygen.cleaner.IAllValues;
import com.oxygen.cleaner.R;
import com.oxygen.cleaner.apters.base.OxygenCleanAppliction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OxygenUddil {
    public static List<OxygenCleanAppliction> getRunningApps(Context context) {
        Set<String> whiteNames = OxygenShjhjasdePref.with(context).getStringSet(IAllValues.KEY_WHITE_LIST, new HashSet<String>());
        whiteNames.add(context.getPackageName());

        List<OxygenCleanAppliction> baseApps = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        OxygenCleanAppliction baseApp;
        ApplicationInfo applicationInfo;
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : runningAppProcesses) {
            if (whiteNames.contains(appProcessInfo.processName)) {
                continue;
            }

            baseApp = new OxygenCleanAppliction();
            baseApp.setPackageName(appProcessInfo.processName);
            baseApp.setPid(appProcessInfo.pid);
            try {
                applicationInfo = packageManager.getApplicationInfo(appProcessInfo.processName, 0);
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {

                } else {
                }
                baseApp.setIcon(applicationInfo.loadIcon(packageManager));
                baseApp.setName(applicationInfo.loadLabel(packageManager));
            } catch (PackageManager.NameNotFoundException e) {
                //:服务的命名
                if (appProcessInfo.processName.contains(":")) {
                    applicationInfo = getApplicationInfo(packageManager, appProcessInfo.processName.split(":")[0]);
                    if (applicationInfo != null) {
                        baseApp.setIcon(applicationInfo.loadIcon(packageManager));
                    } else {
                        baseApp.setIcon(context.getResources().getDrawable(R.drawable.def_launscher));
                    }

                } else {
                    baseApp.setIcon(context.getResources().getDrawable(R.drawable.def_launscher));
                }
            }


            long memorySize = activityManager.getProcessMemoryInfo(new int[]{appProcessInfo.pid})[0].getTotalPrivateDirty() * 1024;
            baseApp.setSize(memorySize);

            baseApps.add(baseApp);
        }
        return baseApps;
    }

    private static ApplicationInfo getApplicationInfo(PackageManager packageManager, String processName) {
        if (processName == null) {
            return null;
        }
        List<ApplicationInfo> appList = packageManager.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (ApplicationInfo appInfo : appList) {
            if (processName.equals(appInfo.processName)) {
                return appInfo;
            }
        }
        return null;
    }
}
