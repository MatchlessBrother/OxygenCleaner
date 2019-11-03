package com.oxygen.cleaner.util;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OxygenAsdhHel {
    private static OxygenAsdhHel sOurInstance = new OxygenAsdhHel();
    private Handler mMainHandler;
    private ScheduledExecutorService mPoolExecutor;

    private OxygenAsdhHel() {
        mMainHandler = new Handler(Looper.getMainLooper());
        mPoolExecutor = Executors.newScheduledThreadPool(1);
    }

    public static OxygenAsdhHel getInstance() {
        return sOurInstance;
    }

    public <T> void async(@NonNull final Async<T> async) {
        mPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    final T result = async.onBackground();
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            async.onResult(result);
                        }
                    });
                } catch (final Exception e) {
                    mMainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            async.onCancel(e);
                        }
                    });
                }
            }
        });
    }

    public void background(Runnable runnable) {
        mPoolExecutor.submit(runnable);
    }

    public void main(Runnable runnable) {
        mMainHandler.post(runnable);
    }

    public void mainDelay(final Runnable runnable, long delay) {
        mPoolExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                mMainHandler.post(runnable);
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    public interface Async<T> {
        T onBackground();

        void onResult(T t);

        void onCancel(Throwable throwable);
    }
}
