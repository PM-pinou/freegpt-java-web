package com.chat.base.handler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolManager {

    private static ThreadFactory promptRecordThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("promptRecord-pool-%d").build();
    public static ThreadPoolExecutor promptRecordPool = new ThreadPoolExecutor(1, 2, 10, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100),promptRecordThreadFactory);

    private static ThreadFactory discernRecordThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("discern-pool-%d").build();
    public static ThreadPoolExecutor discernRecordPool = new ThreadPoolExecutor(1, 2, 10, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100),discernRecordThreadFactory);



    private static ThreadFactory consumptionRecordThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("consumption-pool-%d").build();
    public static ThreadPoolExecutor consumptionRecordPool = new ThreadPoolExecutor(1, 2, 10, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100),consumptionRecordThreadFactory);

}
