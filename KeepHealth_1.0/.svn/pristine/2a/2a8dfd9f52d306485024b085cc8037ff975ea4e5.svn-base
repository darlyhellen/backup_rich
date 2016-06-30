package com.ytdinfo.keephealth.app;



import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;

import android.content.Context;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.NetworkReachabilityUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;

public class HttpClient {
	private static final String TAG = HttpClient.class.getName();
//private static final Context context = MyApp.getInstance().getApplicationContext();
	
	private static int  VersionCode=MyApp.getInstance().getVersionCode();
	
	private static final String APPSYS_STRING="Android_";
	
	
	private HttpClient() {
	}

	private static HttpUtils httpUtils = new HttpUtils();
	public static void post( String url, String s,RequestCallBack<String> callBack) 
	{
		if (!NetworkReachabilityUtil.isNetworkConnected(MyApp.getInstance())) {
			ToastUtil.showMessage("网络未连接...");
		} else {
			LogUtil.i(TAG, "接口地址--------->>>" + url);
			LogUtil.i(TAG, "param----------->>>" + s.toString());
			//httpUtils.configCurrentHttpCacheExpiry(1000*10);
			RequestParams params = new RequestParams();
			params.addHeader("Content-Type", "application/json;charset=UTF-8");
			params.addHeader("Accept", "application/json");
			params.addHeader("charset", "utf-8");
			if(VersionCode==0)
			{
				VersionCode=MyApp.getInstance().getVersionCode();
			}
			params.addHeader("version",APPSYS_STRING+VersionCode);
			//params.addBodyParameter("param", s);
			try {
				StringEntity se = new StringEntity(s, "utf-8");
				params.setBodyEntity(se);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (null!=SharedPrefsUtil.getValue(Constants.TOKEN, null)) {
			String token = SharedPrefsUtil.getValue(Constants.TOKEN, null);
			LogUtil.i(TAG,"token--------->>>" + token);
			params.addHeader("token", token);	
			
			}
			httpUtils.configTimeout(5*1000);
			httpUtils.configRequestRetryCount(1);
			httpUtils.configRequestThreadPoolSize(3);
			httpUtils.configResponseTextCharset("utf-8");
			httpUtils.send(HttpMethod.POST, url, params, callBack);
			
	}
	}
	public static void put( String url, String s,
			RequestCallBack<String> callBack) {
		if (!NetworkReachabilityUtil.isNetworkConnected(MyApp.getInstance())) {
			ToastUtil.showMessage("网络未连接...");
		} else {
			LogUtil.i(TAG, "接口地址--------->>>" + url);
			LogUtil.i(TAG, "param----------->>>" + s.toString());
			//httpUtils.configCurrentHttpCacheExpiry(1000*10);
			RequestParams params = new RequestParams();
			params.addHeader("Content-Type", "application/json;charset=UTF-8");
			params.addHeader("Accept", "application/json");
			params.addHeader("charset", "utf-8");
			if(VersionCode==0)
			{
				VersionCode=MyApp.getInstance().getVersionCode();
			}
			params.addHeader("version",APPSYS_STRING+VersionCode);
			//params.addBodyParameter("param", s);
			try {
				StringEntity se = new StringEntity(s, "utf-8");
				params.setBodyEntity(se);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (null!=SharedPrefsUtil.getValue(Constants.TOKEN, null)) {
			
			params.addHeader("token", SharedPrefsUtil.getValue(Constants.TOKEN, null));	
			
			}
			httpUtils.configTimeout(5*1000);
			httpUtils.configRequestRetryCount(1);
			httpUtils.configRequestThreadPoolSize(3);
			httpUtils.configResponseTextCharset("utf-8");
			httpUtils.send(HttpMethod.PUT, url, params, callBack);
			
	}
	}
	public static void options( String url, String param,
			RequestCallBack<String> callBack) {
		if (!NetworkReachabilityUtil.isNetworkConnected(MyApp.getInstance())) {
			ToastUtil.showMessage("网络未连接...");
		} else {
			LogUtil.i(TAG, "接口地址--------->>>" + url);
			LogUtil.i(TAG, "param----------->>>" + param);
			//httpUtils.configCurrentHttpCacheExpiry(1000*10);
			RequestParams params = new RequestParams();
			params.addBodyParameter("param", param);
			params.addHeader("token", SharedPrefsUtil.getValue(Constants.TOKEN, null));
			if(VersionCode==0)
			{
				VersionCode=MyApp.getInstance().getVersionCode();
			}
			params.addHeader("version",APPSYS_STRING+VersionCode);
			httpUtils.configTimeout(5*1000);
			httpUtils.configRequestRetryCount(1);
			httpUtils.configRequestThreadPoolSize(3);
			httpUtils.configResponseTextCharset("utf-8");
			httpUtils.send(HttpMethod.OPTIONS, url, params, callBack);
			
	}
	}
	/*public static void post(Context context, String url, 
			RequestCallBack<String> callBack) {
		if (!NetworkReachabilityUtil.isNetworkConnected(context)) {
			//ToastUtils.ToastShort(context, "网络未连接...");
		} else {
			Log.i(TAG, "接口地址--------->>>" + url);
			// Log.i(TAG, "YKEY------------>>>"+KeyGenUtil.gen(s));
			//httpUtils.configCurrentHttpCacheExpiry(1000*10);
			httpUtils.configRequestRetryCount(1);
			httpUtils.configRequestThreadPoolSize(3);
			httpUtils.configResponseTextCharset("utf-8");
			httpUtils.send(HttpMethod.POST, url,  callBack);
	}
	}*/
	public static void get(Context context, String url, RequestParams params,
			RequestCallBack<String> callBack) {
		if (!NetworkReachabilityUtil.isNetworkConnected(context)) {
			ToastUtil.showMessage("网络未连接...");
		} else {
			LogUtil.i(TAG, "接口地址----------->>>" + url);
			if (null!=params.getQueryStringParams()) {
		LogUtil.i(TAG, "params------------>>>"+ params.getQueryStringParams().toString());
			}
			params.addHeader("token", SharedPrefsUtil.getValue(Constants.TOKEN, null));
			params.addHeader("Content-Type", "application/json");
			params.addHeader("Accept", "application/json");
			if(VersionCode==0)
			{
				VersionCode=MyApp.getInstance().getVersionCode();
			}
			params.addHeader("version",APPSYS_STRING+VersionCode);
			httpUtils.configTimeout(5*1000);
			httpUtils.configResponseTextCharset("utf-8");
			httpUtils.configCurrentHttpCacheExpiry(500);
			httpUtils.send(HttpMethod.GET, url, params, callBack);
		}
	}
	public static void delete(Context context, String url, RequestParams params,
			RequestCallBack<String> callBack) {
		if (!NetworkReachabilityUtil.isNetworkConnected(context)) {
			ToastUtil.showMessage("网络未连接...");
		} else {
			LogUtil.i(TAG, "接口地址----------->>>" + url);
			if (null!=params.getQueryStringParams()) {
				LogUtil.i(TAG, "params------------>>>"+ params.getQueryStringParams().toString());
			}
			params.addHeader("token", SharedPrefsUtil.getValue(Constants.TOKEN, null));
			params.addHeader("Content-Type", "application/json");
			params.addHeader("Accept", "application/json");
			if(VersionCode==0)
			{
				VersionCode=MyApp.getInstance().getVersionCode();
			}
			params.addHeader("version",APPSYS_STRING+VersionCode);
			httpUtils.configTimeout(5*1000);
			httpUtils.configResponseTextCharset("utf-8");
			httpUtils.send(HttpMethod.DELETE, url, params, callBack);
		}
	}

	/*public static void get(Context context, String url,
			RequestCallBack<String> callBack) {
		if (!NetworkReachabilityUtil.isNetworkConnected(context)) {
			ToastUtils.ToastShort(context, "网络未连接...");
		} else {
			Log.i(TAG, "接口地址----------->>>" + url);
			httpUtils.configResponseTextCharset("utf-8");
			httpUtils.send(HttpMethod.GET, url, callBack);
		}
	}*/
	 static HttpHandler httpHandler = null;
	@SuppressWarnings("rawtypes")
	public static HttpHandler download(final Context context, final String url,final String fileneme,
			final RequestCallBack<File> callBack) {
			httpUtils.configCurrentHttpCacheExpiry(1000*10);
			return httpUtils.download(url, fileneme, true,true,callBack);
		}


}
