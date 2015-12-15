package com.clinkworld.pay.util;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;


public class UiUtils {

    public static void showKeyBoard(Activity activity, View view, int flags) {
        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(view, flags);
    }

    public static void hideKeyBoard(Activity activity) {
        if (activity.getCurrentFocus() != null && activity.getCurrentFocus().getWindowToken() != null) {
            InputMethodManager manager = (InputMethodManager) activity.getSystemService((Context.INPUT_METHOD_SERVICE));
            manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int getDisplayWidth(Activity context) {
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        return display.getWidth();
    }

    public static int getDisplayHeight(Activity context) {
        WindowManager windowManager = context.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        return display.getHeight();
    }
}
