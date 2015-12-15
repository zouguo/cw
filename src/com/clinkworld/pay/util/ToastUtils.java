package com.clinkworld.pay.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by shirenhua on 2015/10/16.
 */
public class ToastUtils {
    public static void showToast(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, String toastStr) {
        Toast.makeText(context, toastStr, Toast.LENGTH_SHORT).show();
    }
}
