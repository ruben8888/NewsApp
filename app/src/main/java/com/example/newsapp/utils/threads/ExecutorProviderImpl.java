package com.example.newsapp.utils.threads;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecutorProviderImpl implements ExecutorProvider {

    private final Executor mainExecutor;
    private final Executor mainExecutorWithDelay;
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private final ScheduledExecutorService scheduledExecutorService;
    private final ThreadPoolExecutor executor;

    ExecutorProviderImpl() {
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
        this.executor = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES * 2, 60, TimeUnit.SECONDS, queue);
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        this.mainExecutor = handler::post;
        this.mainExecutorWithDelay = r -> handler.postDelayed(r, 500);
    }

    @Override
    public Executor getExecutor(int type) {
        switch (type) {
            case ExecutorType.MAIN_WITH_DELAY:
                return mainExecutorWithDelay;
            case ExecutorType.MAIN:
                return mainExecutor;
            case ExecutorType.DB_COMMUNICATION:
                return executor;
            case ExecutorType.BG_SCHEDULER:
                return scheduledExecutorService;
            default:
                return null;
        }
    }
}