package com.pxccn.PxcDali2.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

/**
 * 照明系统线程池
 */
public class LcsExecutors {

    /**
     * 创建线程池
     * @param parallelism 并行数量
     * @param namePrefix 线程池命名前缀
     * @return ForkJoinPool
     */
    public static ExecutorService newWorkStealingPool(int parallelism, String namePrefix) {
        return new ForkJoinPool(parallelism, new LcsForkJoinWorkerThreadFactory(namePrefix), null, true);
    }

    /**
     * 创建线程池
     * @param parallelism 并行数量
     * @param clazz 传入class用于自动生成命名前缀
     * @return ForkJoinPool
     */
    public static ExecutorService newWorkStealingPool(int parallelism, Class clazz) {
        return newWorkStealingPool(parallelism, clazz.getSimpleName());
    }
}