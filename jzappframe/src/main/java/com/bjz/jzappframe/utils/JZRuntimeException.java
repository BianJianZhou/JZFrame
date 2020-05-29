package com.bjz.jzappframe.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

public class JZRuntimeException extends RuntimeException {
    public JZRuntimeException() {
    }

    public JZRuntimeException(String message) {
        super("JZAPPFrame: " + message);
    }

    public JZRuntimeException(String message, Throwable cause) {
        super("JZAPPFrame: " + message, cause);
    }

    public JZRuntimeException(Throwable cause) {
        super(cause);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public JZRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super("JZAPPFrame: " + message, cause, enableSuppression, writableStackTrace);
    }
}
