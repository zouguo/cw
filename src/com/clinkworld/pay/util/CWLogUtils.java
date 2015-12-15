package com.clinkworld.pay.util;

import android.util.Log;

public class CWLogUtils {
    public static boolean debuglog = true;

    public static int v(String tag, String msg) {
        if (debuglog) {
            return Log.v(tag, msg);
        }

        return -1;
    }

    public static int v(String tag, String msg, Throwable tr) {
        if (debuglog) {
            return Log.v(tag, msg, tr);
        }

        return -1;
    }

    public static int d(String tag, String msg) {
        if (debuglog) {
            return Log.d(tag, msg);
        }

        return -1;
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (debuglog) {
            return Log.d(tag, msg, tr);
        }

        return -1;
    }

    public static int i(String tag, String msg) {
        if (debuglog) {
            return Log.i(tag, msg);
        }

        return -1;
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (debuglog) {
            return Log.i(tag, msg, tr);
        }

        return -1;
    }

    public static int w(String tag, String msg) {
        if (debuglog) {
            return Log.w(tag, msg);
        }

        return -1;
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (debuglog) {
            return Log.w(tag, msg, tr);
        }

        return -1;
    }

    public static int e(String tag, String msg) {
        if (debuglog) {
            return Log.e(tag, msg);
        }

        return -1;
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (debuglog) {
            return Log.e(tag, msg, tr);
        }

        return -1;
    }
}
