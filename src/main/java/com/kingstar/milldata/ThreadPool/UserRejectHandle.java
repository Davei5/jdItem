package com.kingstar.milldata.ThreadPool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class UserRejectHandle implements RejectedExecutionHandler {

    private Logger logger = LoggerFactory.getLogger(UserRejectHandle.class);

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        logger.info("task reject" + executor.toString());
//        System.out.println("task reject" + executor.toString());
    }
}
