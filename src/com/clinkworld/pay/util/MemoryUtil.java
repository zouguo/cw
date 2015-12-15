package com.clinkworld.pay.util;

import android.graphics.Bitmap;
import android.os.Build;

/**
 * Created with IntelliJ IDEA.
 * User: ime
 * Date: 13-8-5
 * Time: 下午3:50
 * To change this template use File | Settings | File Templates.
 */
public class MemoryUtil {

    public static int getBitmapMemory(Bitmap bitmap) {
        if (bitmap == null) {
            return 0;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }

    public static long getFreeMemory() {
        return Runtime.getRuntime().maxMemory() - Runtime.getRuntime().totalMemory() + Runtime.getRuntime().freeMemory();
    }
}
