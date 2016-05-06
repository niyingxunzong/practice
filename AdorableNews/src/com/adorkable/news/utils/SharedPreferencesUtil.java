package com.adorkable.news.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {

	private static String CONFIG = "config";
	private static SharedPreferences sharedPreferences;

	public static String getStringData(Context context,
			String key, String value) {
		if (sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(CONFIG,
					Context.MODE_PRIVATE);
		}
		return sharedPreferences.getString(key, value);
	}

	public static void saveStringData(Context context,
			String key, String value) {
		if (sharedPreferences == null) {
			sharedPreferences = context.getSharedPreferences(CONFIG,
					Context.MODE_PRIVATE);
		}
		sharedPreferences.edit().putString(key, value).commit();
	}

}
