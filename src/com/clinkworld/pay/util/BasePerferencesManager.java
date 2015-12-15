package com.clinkworld.pay.util;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class BasePerferencesManager {

	private static final String DEFAULT_FILE_NAME = "app_preferences";

	protected SharedPreferences settings;
	protected SharedPreferences.Editor editor;

	public BasePerferencesManager(Context context) {
		this(context, DEFAULT_FILE_NAME);
	}
	
	public BasePerferencesManager(Context context, String fileName) {
		context = context.getApplicationContext();
		settings = context
				.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		editor = settings.edit();
	}

	public void saveBoolean(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void saveFloat(String key, float value) {
		editor.putFloat(key, value);
		editor.commit();
	}

	public void saveInt(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public void saveLong(String key, long value) {
		editor.putLong(key, value);
		editor.commit();
	}

	public void saveString(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public void clear(String key) {
		editor.remove(key);
		editor.commit();
	}

	public void clear() {
		editor.clear();
		editor.commit();
	}

	protected boolean getBoolean(String key, boolean defValue) {
		return settings.getBoolean(key, defValue);
	}

	protected float getFloat(String key, float defValue) {
		return settings.getFloat(key, defValue);
	}

	protected int getInt(String key, int defValue) {
		return settings.getInt(key, defValue);
	}

	protected long getLong(String key, long defValue) {
		return settings.getLong(key, defValue);
	}

	protected String getString(String key, String defValue) {
		return settings.getString(key, defValue);
	}
}
