package com.prisonerprice.dayscountup.middleware;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.MainThread;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.LogRecord;

public class AppExecutors {

    private static final Object LOCK = new Object();
    private static AppExecutors appExecutors;
    private final Executor diskIO;
    private final Executor mainThread;
    private final Executor networkIO;

    private AppExecutors(Executor diskIO, Executor mainThread, Executor networkIO) {
        this.diskIO = diskIO;
        this.mainThread = mainThread;
        this.networkIO = networkIO;
    }

    public static AppExecutors getInstance() {
        if (appExecutors == null) {
            synchronized (LOCK) {
                appExecutors = new AppExecutors(
                        Executors.newSingleThreadExecutor(),
                        Executors.newFixedThreadPool(3),
                        new MainThreadExecutor()
                );
            }
        }
        return appExecutors;
    }

    public Executor getDiskIO() {
        return diskIO;
    }

    public Executor getMainThread() {
        return mainThread;
    }

    public Executor getNetworkIO() {
        return networkIO;
    }

    private static class MainThreadExecutor implements Executor {

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(Runnable command) {

        }
    }
}
