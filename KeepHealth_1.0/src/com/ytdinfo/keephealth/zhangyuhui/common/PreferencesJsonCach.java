package com.ytdinfo.keephealth.zhangyuhui.common;

import android.content.Context;
import android.content.SharedPreferences.Editor;

public class PreferencesJsonCach {
	public final static String SETTING = "ytdinfo_preference";

	public static void putValue(String key, String value,Context context) {
		Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
				.edit();
		sp.putString(key, value);
		sp.commit();
	}

	public static String getInfo(String key,Context context) {
		return context.getSharedPreferences(SETTING, Context.MODE_PRIVATE)
				.getString(key, null);
	}
}
