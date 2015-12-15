package com.clinkworld.pay.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;


public class SafeHandler extends Handler {
	WeakReference<Context> mActivityReference;

	public SafeHandler(Context context) {
		mActivityReference = new WeakReference<Context>(context);
	}

	@Override
	public void handleMessage(Message msg) {
		final Context context = mActivityReference.get();
		if (context != null) {
		}
	}
}