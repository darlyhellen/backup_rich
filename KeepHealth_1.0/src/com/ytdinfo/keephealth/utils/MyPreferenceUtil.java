package com.ytdinfo.keephealth.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class MyPreferenceUtil {
	private static Context mContext;
	private  static final String PREF_NAME = "my_pref";
	private static MyPreferenceUtil instance;
	private static SharedPreferences pref;
	
	private static String USER_ACCOUNT = "user_acount";// 用户帐号
	/** 用户token标示 */
	public static  String TOKEN = "token";
	
	public static MyPreferenceUtil getInstance(Context context) {
		mContext = context;
		if (instance == null) {
			synchronized (PREF_NAME) {
				if (instance == null) {
					instance = new MyPreferenceUtil();
				}
			}
		}
		return instance;
	}

	private MyPreferenceUtil() {
		pref = mContext.getSharedPreferences(PREF_NAME,
				Context.MODE_PRIVATE);
	}
	
	/**
	 * 获取当前帐号
	 */
	public String getUserAccount() {
		return pref.getString(USER_ACCOUNT, "");
	}

	/**
	 * 设置当前帐号
	 * 
	 * @param userAccount
	 */
	public void setUserAccount(String userAccount) {
		Editor appspEditor = pref.edit();
		appspEditor.putString(USER_ACCOUNT, userAccount);
		appspEditor.commit();
	}
	
	/**
	 * 获取当前token
	 */
	public String getToken() {
		return pref.getString(TOKEN, "");
	}

	/**
	 * 设置当前token
	 * 
	 * @param token
	 */
	public void setToken(String token) {
		Editor appspEditor = pref.edit();
		appspEditor.putString(TOKEN, token);
		appspEditor.commit();
	}
}
