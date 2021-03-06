package com.ytdinfo.keephealth.app;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.rayelink.eckit.AppUserAccount;
import com.rayelink.eckit.MainChatControllerListener;
import com.rayelink.eckit.SDKCoreHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.comm.core.sdkmanager.ShareSDKManager;
import com.umeng.comm.custom.AppAdd;
import com.umeng.community.NickNameCheckImpl;
import com.umeng.message.PushAgent;
import com.umeng.message.UHandler;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.entity.UMessage;
import com.umeng.socialize.PlatformConfig;
import com.youzan.sdk.YouzanSDK;
import com.ytdinfo.keephealth.utils.CommomUtil;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.kitsdk.ECDeviceKit;
import com.yuntongxun.kitsdk.beans.ChatInfoBean;
import com.yuntongxun.kitsdk.ui.chatting.model.IMChattingHelper;

//import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;

public class MyApp extends Application {

	public static final String TAG = MyApp.class.getName();
	private static MyApp instance;

	/**
	 * 上午11:35:19 TODO 服务器地址
	 */
	private static String xml = CommomUtil.setUpXml("ruiyi.cloopen.com",
			"8085", "ruiyi.cloopen.com", "8888", "ruiyi.cloopen.com", "8090");

	/**
	 * 单例，返回一个实例
	 * 
	 * @return
	 */
	public static MyApp getInstance() {
		if (instance == null) {
			LogUtil.w("[MyApp] instance is null.");
		}
		return instance;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
		initUzan();
		SharedPrefsUtil.putValue(Constants.ISLOADED, false);
		SharedPrefsUtil.putValue(Constants.CHECKEDID_RADIOBT, 0);
		SharedPrefsUtil.putValue(Constants.CHECKISUPDATE, true);
		JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
		JPushInterface.init(this); // 初始化 JPush
		AppAdd.getInstance();
		AppAdd.setAppInterface(new NickNameCheckImpl());
		// 友盟统计启动
		MobclickAgent.updateOnlineConfig(this);
		// 禁止默认的页面统计方式
		MobclickAgent.openActivityDurationTrack(false);
		initImageLoader();
		initYunTongXunConfig();
		// ConnectYunTongXun();
		InitChatController();
		// 分享初始化
		PlatformConfig.setWeixin("wxe9dfaf997a35d828",
				"e98b52d02f8112bcc93181490b980aab");
		// 豆瓣RENREN平台目前只能在服务器端配置
		// 新浪微博
		// PlatformConfig.setSinaWeibo("275392174",
		// "d96fb6b323c60a42ed9f74bfab1b4f7a");
		// PlatformConfig.setQQZone("1104513231", "VFVBeqWa7Rv2ZeDf");
		// 初始化微社区

		// 推送通知
		PushAgent.getInstance(this).setDebugMode(true);
		PushAgent.getInstance(this).setMessageHandler(
				new UmengMessageHandler() {
					@Override
					public void dealWithNotificationMessage(Context arg0,
							UMessage msg) {
						super.dealWithNotificationMessage(arg0, msg);
					}
				});
		PushAgent.getInstance(this).setNotificationClickHandler(new UHandler() {
			@Override
			public void handleMessage(Context context, UMessage uMessage) {
				com.umeng.comm.core.utils.Log.d("notifi", "getting message");
				try {
					JSONObject jsonObject = uMessage.getRaw();
					String feedid = "";
					if (jsonObject != null) {
						com.umeng.comm.core.utils.Log.d("json",
								jsonObject.toString());
						JSONObject extra = uMessage.getRaw().optJSONObject(
								"extra");
						feedid = extra
								.optString(com.umeng.comm.core.constants.Constants.FEED_ID);
					}
					Class myclass = Class.forName(uMessage.activity);
					Intent intent = new Intent(context, myclass);
					Bundle bundle = new Bundle();
					bundle.putString(
							com.umeng.comm.core.constants.Constants.FEED_ID,
							feedid);
					intent.putExtras(bundle);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} catch (Exception e) {
					com.umeng.comm.core.utils.Log.d("class", e.getMessage());
				}
			}
		});

		
	}

	/**
	 * 上午11:14:20
	 * 
	 * @author zhangyh2 TODO 初始化有赞SDK
	 */
	private void initUzan() {
		/**
		 * 初始化SDK
		 */
		YouzanSDK.init(this, "9d4c27edeafad2e13a1464165102698");
	}

	/**
	 * 上午11:18:39
	 * 
	 * @author zhangyh2 TODO
	 */
	private void initYunTongXunConfig() {
		// TODO Auto-generated method stub
		LogUtils.i("initYunTongXunConfig");
		HttpClient.get(this, Constants.CHANGECHANNEL, new RequestParams(),
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						// 服务器返回获取到的地址
						SharedPrefsUtil.putValue("CHANGECHANNEL", arg0.result);
						LogUtils.i(arg0.result);
						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							JSONObject data = jsonObject.getJSONObject("Data");
							String change = data.getString("isChange");
							if ("true".equals(change)) {
								// AppUserAccount.changeToZYAppConfig();
								// 新通道
								Log.i("MyApp_ConnectYunTongXun", "链接云云通讯");
								// Log.i("MyApp_ConnectYunTongXun", xml);
								ECDevice.initServer(instance, xml);
							}
						} catch (JSONException e) {
						} finally {
							ConnectYunTongXun();
							InitChatController();
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						// 服务端异常，则获取上次保存在本地的可用地址
						;
						try {
							JSONObject jsonObject = new JSONObject(
									SharedPrefsUtil.getValue("CHANGECHANNEL",
											""));
							JSONObject data = jsonObject.getJSONObject("Data");
							String change = data.getString("isChange");
							if ("true".equals(change)) {
								// AppUserAccount.changeToZYAppConfig();
								Log.i("MyApp_ConnectYunTongXun", "链接云云通讯");
								// Log.i("MyApp_ConnectYunTongXun", xml);
								ECDevice.initServer(instance, xml);
							}
						} catch (JSONException e) {

						} finally {
							ConnectYunTongXun();
							InitChatController();
						}

					}
				});

	}

	public void InitChatController() {
		IMChattingHelper.getInstance().setChatControllerListener(
				MainChatControllerListener.getInstance());
	}

	public static void ConnectYunTongXun() {
		try {
			if (AppUserAccount.getCurUserAccount() != null) {
				synchronized (AppUserAccount.getCurUserAccount()) {
					if (!ECDevice.isInitialized()) {
						Log.e("ConnectYunTongxun", "isInitialized");
						ECDeviceKit.init(AppUserAccount.getCurUserAccount()
								.getVoipAccount(), MyApp.getInstance(),
								SDKCoreHelper.getInstance());// 初始化kit sdk
					}
				}
			}
		} catch (Exception e) {
			Log.e("MyAPP-ConnectYunTongXun", e.getMessage());
		}
	}

	/**
	 * Retrieves application's version code from the manifest
	 * 
	 * @return versionCode
	 */
	public int getVersionCode() {
		int code = 1;
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			code = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return code;
	}

	/**
	 * 上午9:49:43
	 * 
	 * @author zhangyh2 TODO
	 */
	public String getChannelName() {
		// TODO Auto-generated method stub
		String channel = "Default";
		try {
			ApplicationInfo appInfo = getPackageManager().getApplicationInfo(
					getPackageName(), PackageManager.GET_META_DATA);
			channel = appInfo.metaData.getString("UMENG_CHANNEL");
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return channel;
	}

	ChatInfoBean chatInfoBean;

	/**
	 * 返回当前程序版本名
	 */
	public String getAppVersionName(Context context) {
		String versionName = "";
		try {
			// ---get the package info---
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			if (versionName == null || versionName.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			Log.e("VersionInfo", "Exception", e);
		}
		return versionName;
	}

	@SuppressWarnings("deprecation")
	private void initImageLoader() {
		File cacheDir = StorageUtils.getOwnCacheDirectory(
				getApplicationContext(), "KeepHealth/image");
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
				this);
		config.memoryCacheExtraOptions(480, 800); // default = device screen
													// dimensions 内存缓存文件的最大长宽
		config.diskCacheExtraOptions(480, 800, null);
		config.threadPoolSize(3);
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.memoryCacheSize(2 * 1024 * 1024); // 内存缓存的最大值
		config.memoryCacheSizePercentage(13);
		config.diskCacheFileCount(100);
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.denyCacheImageMultipleSizesInMemory();
		// config.writeDebugLogs(); // Remove for release app
		config.diskCache(new UnlimitedDiskCache(cacheDir));
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config.build());
	}

	public void showToast(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
				.show();
	}

	public void showToast(int resId) {
		Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_SHORT)
				.show();
	}

	public String getDevicNO() {
		if (!TextUtils.isEmpty(getDeviceId())) {
			return getDeviceId();
		}

		if (!TextUtils.isEmpty(getMacAddress())) {
			return getMacAddress();
		}
		return " ";
	}

	public String getDeviceId() {
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager != null) {
			return telephonyManager.getDeviceId();
		}

		return null;

	}

	public String getMacAddress() {
		// start get mac address
		WifiManager wifiMan = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (wifiMan != null) {
			WifiInfo wifiInf = wifiMan.getConnectionInfo();
			if (wifiInf != null && wifiInf.getMacAddress() != null) {
				// 48位，如FA:34:7C:6D:E4:D7
				return wifiInf.getMacAddress();
			}
		}
		return null;
	}

	/**
	 * device model name, e.g: GT-I9100
	 * 
	 * @return the user_Agent
	 */
	public String getDevice() {
		return Build.MODEL;
	}

	/**
	 * device factory name, e.g: Samsung
	 * 
	 * @return the vENDOR
	 */
	public String getVendor() {
		return Build.BRAND;
	}

	/**
	 * @return the SDK version
	 */
	public int getSDKVersion() {
		return Build.VERSION.SDK_INT;
	}

	/**
	 * @return the OS version
	 */
	public String getOSVersion() {
		return Build.VERSION.RELEASE;
	}

	/**
	 * Retrieves application's version number from the manifest
	 * 
	 * @return versionName
	 */
	public String getVersion() {
		String version = "0.0.0";
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			version = packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return version;
	}

	/**
	 * 
	 * @param mode
	 */
	public void setAudioMode(int mode) {
		AudioManager audioManager = (AudioManager) getApplicationContext()
				.getSystemService(Context.AUDIO_SERVICE);
		if (audioManager != null) {
			audioManager.setMode(mode);
		}
	}

	/**
	 * 
	 * @param phoneNum
	 */
	public void startCalling(String phoneNum) {
		try {
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel://"
					+ phoneNum));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void quitApp() {
		System.exit(0);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();

	}
	/*
	 * static { System.loadLibrary("jpegbither");
	 * System.loadLibrary("bitherjni");
	 * 
	 * }
	 */
}
