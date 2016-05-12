package com.ytdinfo.keephealth.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkReachabilityUtil {
	
	public static enum NetworkType {
		NETTYPE_NONET,
		NETTYPE_WIFI,
		NETTYPE_CMWAP,
		NETTYPE_CMNET
	}
	
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}
	
	@SuppressLint("NewApi")
	public static NetworkType getNetworkType(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return NetworkType.NETTYPE_NONET;
		}		
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if(!extraInfo.isEmpty()){
				if (extraInfo.toLowerCase().equals("cmnet")) {
					return NetworkType.NETTYPE_CMNET;
				} else {
					return NetworkType.NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			
			return NetworkType.NETTYPE_WIFI;
		}
		return NetworkType.NETTYPE_NONET;
	}
	
	
}
