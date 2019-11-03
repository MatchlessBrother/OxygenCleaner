package com.oxygen.cleaner.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;

import com.oxygen.cleaner.IAllValues;
import com.oxygen.cleaner.model.OxygenStory;
import com.oxygen.cleaner.model.OxygenJuedkc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OxygenFifasdgGl {
    private Context mContext;
    private Handler mMainHandler;
    private ScanListener mScanListener;
    private ExecutorService mExecutorService;
    private boolean mScannedCache, mScannedTemp, mScannedLeftover, mScannedThumb;
    private volatile long mTotalSize;

    public OxygenFifasdgGl(Context context) {
        this.mContext = context.getApplicationContext();
        this.mMainHandler = new Handler(Looper.getMainLooper());
        this.mExecutorService = Executors.newFixedThreadPool(4);
    }

    public void clean(final List<OxygenJuedkc> junks) {
        background(new Runnable() {
            @Override
            public void run() {
                for (OxygenJuedkc junk : junks) {
                    if (new File(junk.getPath()).delete()) {
                        //ignore delete result
                        System.out.println("//////////////////////");
                    } else {
                        System.out.println("**********************" + junk.getPath());
                    }
                }
            }
        });
    }

    public void scan() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                background(new Runnable() {
                    @Override
                    public void run() {
                        final List<OxygenStory> caches = scanCache();
                        main(new Runnable() {
                            @Override
                            public void run() {
                                if (mScanListener != null) {
                                    mScanListener.onScanCache(caches);
                                }
                            }
                        });
                        mScannedCache = true;
                        notifyCompletion();
                    }
                });
                background(new Runnable() {
                    @Override
                    public void run() {
                        final List<OxygenJuedkc> tempJunks = scanTemp();
                        main(new Runnable() {
                            @Override
                            public void run() {
                                if (mScanListener != null) {
                                    mScanListener.onScanTemp(tempJunks);
                                }
                            }
                        });
                        mScannedTemp = true;
                        notifyCompletion();
                    }
                });
                background(new Runnable() {
                    @Override
                    public void run() {
                        final List<OxygenJuedkc> leftoverJunks = scanLeftover();
                        main(new Runnable() {
                            @Override
                            public void run() {
                                if (mScanListener != null) {
                                    mScanListener.onScanLeftover(leftoverJunks);
                                }
                            }
                        });
                        mScannedLeftover = true;
                        notifyCompletion();
                    }
                });
                background(new Runnable() {
                    @Override
                    public void run() {
                        final List<OxygenJuedkc> thumbJunks = scanThumb();
                        main(new Runnable() {
                            @Override
                            public void run() {
                                if (mScanListener != null) {
                                    mScanListener.onScanThumb(thumbJunks);
                                }
                            }
                        });
                        mScannedThumb = true;
                        notifyCompletion();
                    }
                });
            }
        }
    }

    private void background(Runnable runnable) {
        mExecutorService.execute(runnable);
    }

    private void main(Runnable runnable) {
        mMainHandler.post(runnable);
    }

    private void notifyPath(final String path) {
        main(new Runnable() {
            @Override
            public void run() {
                if (mScanListener != null) {
                    mScanListener.onScanPath(path);
                }
            }
        });
    }

    private synchronized void notifySize(long size) {
        mTotalSize += size;
        main(new Runnable() {
            @Override
            public void run() {
                if (mScanListener != null) {
                    mScanListener.onScanSize(mTotalSize);
                }
            }
        });
    }

    private void notifyCompletion() {
        if (mScannedCache && mScannedTemp && mScannedLeftover && mScannedThumb && mScanListener != null) {
            main(new Runnable() {
                @Override
                public void run() {
                    mScanListener.onScanCompletion(mTotalSize);
                }
            });
        }
    }

    private List<OxygenJuedkc> convert(File file) {
        List<OxygenJuedkc> junks = new ArrayList<>();
        if (file.exists()) {
            if (file.isDirectory()) {
                notifyPath(file.getPath());
                File[] subFiles = file.listFiles();
                if (subFiles != null) {
                    for (File subFile : subFiles) {
                        junks.addAll(convert(subFile));
                    }
                }
            } else {
                if (file.canWrite() && file.canRead()) {
                    OxygenJuedkc junk = new OxygenJuedkc();
                    junk.setDelete(true);
                    junk.setSize(file.length());
                    junk.setPath(file.getAbsolutePath());
                    junk.setName(OxygenFilasdeUtil.getName(file.getPath()));
                    junk.setSizeText(OxygenFilasdeUtil.sizeFormat(file.length()));
                    junks.add(junk);
                }
            }
        }
        return junks;
    }

    private List<OxygenStory> scanCache() {
        List<OxygenStory> caches = new ArrayList<>();
        File cacheRootFile = new File(Environment.getExternalStorageDirectory(), "Android/data");
        if (cacheRootFile.exists()) {
            PackageManager pm = mContext.getPackageManager();
            List<ApplicationInfo> applicationList = pm.getInstalledApplications(0);
            File itemCacheFile;
            Set<String> whiteNames = OxygenShjhjasdePref.with(mContext).getStringSet(IAllValues.KEY_WHITE_LIST, new HashSet<String>());
            whiteNames.add(mContext.getPackageName());
            for (ApplicationInfo applicationInfo : applicationList) {
                if (whiteNames.contains(applicationInfo.packageName)) {
                    continue;
                }
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { // 非系统应用
                    itemCacheFile = new File(cacheRootFile, applicationInfo.packageName + File.separator + "cache");
                    if (itemCacheFile.exists()) {
                        List<OxygenJuedkc> junks = convert(itemCacheFile);
                        long size = 0;
                        if (!junks.isEmpty()) {
                            for (OxygenJuedkc junk : junks) {
                                size += junk.getSize();
                            }
                        }
                        if (size > 0) {
                            notifySize(size);
                            OxygenStory cache = new OxygenStory();
                            cache.setSize(size);
                            cache.setTextSize(OxygenFilasdeUtil.sizeFormat(size));
                            cache.setJunks(junks);
                            try {
                                cache.setLabel(applicationInfo.loadLabel(pm));
                                cache.setIcon(applicationInfo.loadIcon(pm));
                            } catch (Exception e) {
                                //ignore
                            }
                            caches.add(cache);
                        }
                    }
                }
            }
        }
        return caches;
    }

    private List<OxygenJuedkc> scanThumb() {
        List<OxygenJuedkc> thumbJunks = new ArrayList<>();
        File thumbRootFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        if (thumbRootFile.exists()) {
            thumbJunks.addAll(convert(thumbRootFile));
            long size = 0;
            for (OxygenJuedkc thumbJunk : thumbJunks) {
                size += thumbJunk.getSize();
            }
            notifySize(size);
        }
        return thumbJunks;
    }

    private List<OxygenJuedkc> scanTemp() {
        List<OxygenJuedkc> tempJunks = new ArrayList<>();
        List<OxygenJuedkc> dirJunks = new ArrayList<>();
        Uri uri = MediaStore.Files.getContentUri("external");
        String selection = "(" + MediaStore.Files.FileColumns.DATA + " LIKE '%.temp'" +
                " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%/temp'" +
                " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%.log'" +
                " or " + MediaStore.Files.FileColumns.DATA + " LIKE '%/log'" + ")";
        final Cursor cursor = mContext.getContentResolver().query(
                uri,
                null,
                selection,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC");
        if (cursor != null && !cursor.isClosed()) {
            while (cursor.moveToNext()) {
//            String imageId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE));
                File file = new File(path);
                if (file.canWrite() && file.canRead()) {
                    if (file.isDirectory()) {
                        notifyPath(file.getPath());
                        dirJunks.addAll(convert(file));
                    } else {
                        OxygenJuedkc junk = new OxygenJuedkc();
                        junk.setDelete(true);
                        junk.setSize(size);
                        junk.setPath(path);
                        junk.setName(title);
                        junk.setSizeText(OxygenFilasdeUtil.sizeFormat(size));
                        tempJunks.add(junk);
                    }
                }
            }
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        for (OxygenJuedkc dirJunk : dirJunks) {
            boolean exists = false;
            for (OxygenJuedkc tempJunk : tempJunks) {
                if (dirJunk.getPath().equals(tempJunk.getPath())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                tempJunks.add(dirJunk);
            }
        }

        long size = 0;
        for (OxygenJuedkc tempJunk : tempJunks) {
            size += tempJunk.getSize();
        }
        notifySize(size);
        return tempJunks;
    }

    public List<OxygenJuedkc> scanLeftover() {
        List<OxygenJuedkc> leftoverJunks = new ArrayList<>();
        File leftoverRootFile = new File(Environment.getExternalStorageDirectory(), "Android/data");
        if (leftoverRootFile.exists()) {
            File[] subFiles = leftoverRootFile.listFiles();
            if (subFiles != null && subFiles.length > 0) {
                List<File> subFileList = new ArrayList<>();
                Collections.addAll(subFileList, subFiles);
                PackageManager pm = mContext.getPackageManager();
                List<ApplicationInfo> applicationList = pm.getInstalledApplications(0);
                for (ApplicationInfo applicationInfo : applicationList) {
                    if (subFileList.isEmpty()) {
                        break;
                    }
                    for (Iterator<File> iterator = subFileList.iterator(); iterator.hasNext(); ) {
                        if (iterator.next().getName().equals(applicationInfo.packageName)) {
                            iterator.remove();
                        }
                    }
                }
                if (!subFileList.isEmpty()) {
                    for (File file : subFileList) {
                        if (file.canRead() && file.canWrite() && !file.isHidden()) {
                            OxygenJuedkc junk = new OxygenJuedkc();
                            junk.setDelete(true);
                            junk.setSize(OxygenFilasdeUtil.fileSize(file));
                            junk.setPath(file.getPath());
                            junk.setName(file.getName());
                            junk.setSizeText(OxygenFilasdeUtil.sizeFormat(junk.getSize()));
                            leftoverJunks.add(junk);
                            notifySize(junk.getSize());
                        }
                    }
                }
            }
        }
        return leftoverJunks;
    }

    public void setScanListener(ScanListener listener) {
        mScanListener = listener;
    }

    public void destroy() {
        mScanListener = null;
        if (!mExecutorService.isShutdown()) {
            mExecutorService.shutdown();
        }
        mExecutorService = null;
    }

    public interface ScanListener {
        void onScanPath(String path);

        void onScanSize(long size);

        void onScanCache(List<OxygenStory> caches);

        void onScanTemp(List<OxygenJuedkc> tempJunks);

        void onScanLeftover(List<OxygenJuedkc> leftoverJunks);

        void onScanThumb(List<OxygenJuedkc> thumbJunks);

        void onScanCompletion(long size);
    }
}
