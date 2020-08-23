package com.bjz.jzappframe.ControllableThreadPoolExecutor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/* https://blog.csdn.net/a1053904672/article/details/72321016 */
public class ThreadPoolUtil {
    private static PausableThreadPoolExecutor controllableThreadPoolExecutor = new PausableThreadPoolExecutor(1, 1, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1000));

    private ThreadPoolUtil() {
    }

    public static void submit(Runnable runnable) {
        controllableThreadPoolExecutor.submit(runnable);
    }

    public static void pause() {
        controllableThreadPoolExecutor.pause();
    }

    public static void resume() {
        controllableThreadPoolExecutor.resume();
    }
}
