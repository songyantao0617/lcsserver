package com.pxccn.PxcDali2.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public class LcsExecutors {

    public static ExecutorService newWorkStealingPool(int parallelism, String namePrefix) {
        return new ForkJoinPool(parallelism, new LcsForkJoinWorkerThreadFactory(namePrefix), null, true);
    }

    public static ExecutorService newWorkStealingPool(int parallelism, Class clazz) {
        return newWorkStealingPool(parallelism, clazz.getSimpleName());
    }
}