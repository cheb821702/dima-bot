package com.dima.bot.manager.util;

import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 * User: ShemiareiD
 * Date: 7/21/14
 * Time: 10:32 AM
 * To change this template use File | Settings | File Templates.
 */
public enum ThreadManager {
    INSTANCE;

    private int cores;
    private BlockingQueue<Runnable> worksQueue;
    private RejectedExecutionHandler executionHandler;
    private ThreadPoolExecutor executor;

    private ThreadManager() {
        cores = Runtime.getRuntime().availableProcessors();
//        cores = 100;
        worksQueue = new ArrayBlockingQueue<Runnable>(10);
        executionHandler = new RejectedExecutionHandelerImpl();
        executor = new ThreadPoolExecutor(cores, cores+1, 10,
                TimeUnit.SECONDS, worksQueue, executionHandler);
        executor.allowCoreThreadTimeOut(true);
    }

    public void execute(Runnable newThread) {
        executor.execute(newThread);
    }

    public void shutdown() {
        executor.shutdown();
    }
}
