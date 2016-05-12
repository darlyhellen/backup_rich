package com.ytdinfo.keephealth.app;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
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
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.rayelink.eckit.AppUserAccount;
import com.rayelink.eckit.MainChatControllerListener;
import com.rayelink.eckit.SDKCoreHelper;
import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.utils.CommomUtil;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.kitsdk.ECDeviceKit;
import com.yuntongxun.kitsdk.beans.ChatInfoBean;
import com.yuntongxun.kitsdk.ui.chatting.model.IMChattingHelper;

public class MyApp extends Application {
	public static final String TAG = MyApp.class.getName();
	private static MyApp instance;

	/**
	 * 上午11:35:19 TODO 服务器地址
	 */
	private static String xml = CommomUtil.setUpXml("ruiyi.cloopen.com", "8085",
			"ruiyi.cloopen.com", "8888", "ruiyi.cloopen.com", "8090");

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

		SharedPrefsUtil.putValue(Constants.ISLOADED, false);
		SharedPrefsUtil.putValue(Constants.CHECKEDID_RADIOBT, 0);
		SharedPrefsUtil.putValue(Constants.CHECKISUPDATE, true);
		JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
		JPushInterface.init(this); // 初始化 JPush
		// 友盟统计启动
		MobclickAgent.updateOnlineConfig(this);
		// 禁止默认的页面统计方式
		MobclickAgent.openActivityDurationTrack(false);
		initImageLoader();
		initYunTongXunConfig();
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
//								AppUserAccount.changeToZYAppConfig();
								// 新通道
								Log.i("MyApp_ConnectYunTongXun", "链接云云通讯");
								Log.i("MyApp_ConnectYunTongXun", xml);
								ECDevice.initServer(instance, xml);
							}
						} catch (JSONException e) {
						}
						finally{
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
//								AppUserAccount.changeToZYAppConfig();
								Log.i("MyApp_ConnectYunTongXun", "链接云云通讯");
								Log.i("MyApp_ConnectYunTongXun", xml);
								ECDevice.initServer(instance, xml);
							}
						} catch (JSONException e) {
							
						}finally{
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
		if (AppUserAccount.getCurUserAccount() != null) {
			ECDeviceKit.init(AppUserAccount.getCurUserAccount()
					.getVoipAccount(), MyApp.getInstance(), SDKCoreHelper
					.getInstance());// 初始化kit sdk
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
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this)
				// .memoryCacheExtraOptions(480, 800)
				// max width, max height，即保存的每个缓存文件的最大长宽
				.threadPoolSize(3)
				// 线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
				// You can pass your own memory cache
				// implementation/你可以通过自己的内存缓存实现
				.memoryCacheSize(2 * 1024 * 1024)
				.discCacheSize(50 * 1024 * 1024)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCacheFileCount(100)
				// 缓存的文件数量
				.discCache(new UnlimitedDiscCache(cacheDir))
				// 自定义缓存路径
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.imageDownloader(
						new BaseImageDownloader(this, 5 * 1000, 30 * 1000))
				.writeDebugLogs().build();// 开始构建
		ImageLoader.getInstance().init(config);
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

	static {
		System.loadLibrary("jpegbither");
		System.loadLibrary("bitherjni");

	}
}
