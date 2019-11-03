package com.oxygen.cleaner.util;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import java.io.File;

public class OxygenStgddeTool {
    /**
     * 显示RAM的可用和总容量
     *
     * @param context
     * @return
     */
    public static long[] getRAMTotalAvailable(Context context) {
        long[] sizes = new long[2];
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        sizes[0] = mi.totalMem;
        sizes[1] = mi.availMem;
        return sizes;
    }

    /**
     * 显示ROM的可用和总容量
     *
     * @return
     */
    public static long[] getROMTotalAvailable() {
        long[] sizes = new long[2];
        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long blockSize = statFs.getBlockSizeLong();
        long totalBlocks = statFs.getBlockCountLong();
        long availableBlocks = statFs.getAvailableBlocksLong();

        sizes[0] = totalBlocks * blockSize;
        sizes[1] = availableBlocks * blockSize;
        return sizes;
    }

    /**
     * 显示SD卡的可用和总容量
     *
     * @return
     */
    public static long[] getSDTotalAvailable() {
        long[] sizes = new long[2];
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File file = Environment.getExternalStorageDirectory();
            StatFs statFs = new StatFs(file.getPath());
            long blockSize = statFs.getBlockSizeLong();
            long totalBlocks = statFs.getBlockCountLong();
            long availableBlocks = statFs.getAvailableBlocksLong();

            sizes[0] = totalBlocks * blockSize;
            sizes[1] = availableBlocks * blockSize;
        } else {
            sizes[0] = 1;
            sizes[1] = 0;
        }
        return sizes;
    }
}
