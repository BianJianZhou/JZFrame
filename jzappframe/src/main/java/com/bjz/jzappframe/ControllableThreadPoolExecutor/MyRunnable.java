package com.bjz.jzappframe.ControllableThreadPoolExecutor;

import android.util.Log;

import com.bjz.jzappframe.utils.JZLog;

public class MyRunnable implements Runnable {
    int i = 0;

    public MyRunnable(int i) {
        this.i = i;
    }

    int index = 0;

    @Override
    public void run() {
        while (true) {
            try {
                if (index == 10) {
                    break;
                } else {
                    Log.i("bian:", "第" + i + "个线程index: " + index);
                    index++;
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
