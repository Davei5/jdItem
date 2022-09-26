package com.kingstar.milldata.ThreadPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class UserThreadFactory implements ThreadFactory {

    private final String namePrefix;
    private final AtomicInteger atomicInteger = new AtomicInteger(1);

    private Logger logger = LoggerFactory.getLogger(UserThreadFactory.class);

    public UserThreadFactory(String whatNamePrefix) {
        namePrefix = "From UserThreadFactory" + whatNamePrefix + "-Worker-";
    }

    @Override
    public Thread newThread(Runnable r) {
        String threadName = namePrefix + atomicInteger.getAndIncrement();
        Thread thread = new Thread(r, threadName);
        //打印线程池名称
        logger.info(thread.getName());
//        System.out.println(thread.getName());
        return thread;
    }
}
