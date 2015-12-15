package com.clinkworld.pay.loaders;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

/**
 * Created with IntelliJ IDEA.
 * User: ime
 * Date: 13-4-15
 * Time: 下午7:57
 * 这是一个Android的bug，参见<a>https://code.google.com/p/android/issues/detail?id=41028</a>
 * 在Loader stopLoading时不停止异步线程来规避这个bug.这个修正方案是否引入其他bug有待观察
 */
public class HotfixCursorLoader extends CursorLoader {
    public HotfixCursorLoader(Context context) {
        super(context);
    }

    public HotfixCursorLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected void onStopLoading() {
    }
}
