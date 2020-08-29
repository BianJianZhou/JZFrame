package com.bjz.jzappframe.utils.customPausePoolExecutor2;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class JZPausableThreadPoolExecutor extends ThreadPoolExecutor {
    private boolean isPaused;
    private ReentrantLock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public JZPausableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    protected void beforeExecute(Thread t, Runnable r) {
        lock.lock();
        super.beforeExecute(t, r);
        try {
            while (isPaused) {
                System.out.printf("pausing, %s\n", t.getName());
                Thread.sleep(1000);
                condition.await();
            }
        } catch (InterruptedException ie) {
            t.interrupt();
        } finally {
            lock.unlock();
        }
    }

    public void pause() {
        lock.lock();
        try {
            isPaused = true;
        } finally {
            lock.unlock();
        }
    }

    public void resume() {
        lock.lock();
        try {
            isPaused = false;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }
}
