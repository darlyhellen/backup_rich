/**上午10:33:04
 * @author zhangyh2
 * VersionUpdatePresenter.java
 * TODO
 */
package com.ytdinfo.keephealth.ui;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.app.MyApp;
import com.ytdinfo.keephealth.service.UpdateService;
import com.ytdinfo.keephealth.ui.setting.SettingActivity;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;

/**
 * @author zhangyh2 VersionUpdatePresenter 上午10:33:04 TODO
 *         应用程序版本更新提示提示逻辑处理类，主要功能为接入MainActivity和SettingActivity两个类中的版本更新
 */
public class VersionUpdatePresenter {

	private AlertDialog isinWifi;

	private AlertDialog alertDialog;

	private AlertDialog heightVersion;

	private int width;
	
	public static boolean isAutoShowed;

	/**
	 * 上午10:39:56 TODO 是否手动升级，即后面设置中的升级 <code>true</code>是SettingActivity
	 * <code>false</code>是MainActivity
	 */
	@SuppressWarnings("deprecation")
	public VersionUpdatePresenter(Context context) {
		super();
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
	}

	/**
	 * 上午9:57:09
	 * 
	 * @author zhangyh2 TODO 版本更新接口进行请求，不在意时间限制，直接点击开始请求。
	 */
	public void showUpdateInfo(final Context context) {
		// TODO Auto-generated method stub
		int versionCode = MyApp.getInstance().getVersionCode();
		String channel = MyApp.getInstance().getChannelName();
		String url = Constants.ROOT_URl + "/api/SoftwareUpdate/List?version="
				+ versionCode + "&type=0&channel=" + channel;
		LogUtil.i("paul", url);
		RequestParams params = new RequestParams();
		HttpClient.get(context, url, params, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				LogUtil.i("paul", arg0.result.toString());
				parseJson(context, arg0.result.toString());
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {

			}
		});
	}

	/**
	 * 上午10:01:39
	 * 
	 * @author zhangyh2 TODO 解析数据
	 */
	private void parseJson(Context context, String jsonStr) {
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			if (!jsonObject.has("Data")) {
				return;
			}
			JSONObject jsonData = jsonObject.getJSONObject("Data");
			String lastestUrl = jsonData.optString("LastestUrl", null);
			String description = jsonData.optString("Description", null);
			String desMd5 = jsonData.optString("compareStr", null);
			int type = jsonData.optInt("downloadType");
			int version = jsonData.optInt("Version");

			// 对象的非空判断一定要放在最前面，然后才可以调用它的方法，否则会空指针
			if (lastestUrl != null && !lastestUrl.equals("null")
					&& !lastestUrl.equals("")) {
				int versionCode = MyApp.getInstance().getVersionCode();

				LogUtil.i("paul", "服务器上版本： " + version + "=====" + "本地版本： "
						+ versionCode);
				if (version - versionCode > 0) {
					showUpdate(context, lastestUrl, desMd5, version, type,
							description);
				} else {
					if (heightVersion != null && heightVersion.isShowing()) {
						return;
					}
					heightVersion = new AlertDialog.Builder(context).create();
					heightVersion.show();
					WindowManager.LayoutParams params = heightVersion
							.getWindow().getAttributes();
					params.width = (int) (width * 0.86);
					heightVersion.getWindow().setAttributes(params);
					heightVersion.setCanceledOnTouchOutside(false);
					Window window = heightVersion.getWindow();
					window.setContentView(R.layout.update);// 设置对话框的布局
					TextView descripttitle = (TextView) window
							.findViewById(R.id.description_title);
					descripttitle.setText("提示");
					TextView descriptiontv = (TextView) window
							.findViewById(R.id.description);
					descriptiontv.setText("当前已是最新版本。");
					Button sure = (Button) window.findViewById(R.id.sure);
					sure.setText("我知道了");
					sure.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							heightVersion.dismiss();
						}
					});

					Button notsure = (Button) window.findViewById(R.id.notsure);
					notsure.setVisibility(View.GONE);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 上午10:04:51
	 * 
	 * @author zhangyh2 TODO 弹出对话框，是否升级 {@link SettingActivity}解决方案
	 */
	protected void showUpdate(final Context context, final String lastestUrl,
			final String desMd5, final int version, final int type,
			String description) {
		if (alertDialog != null && alertDialog.isShowing()) {
			return;
		}
		alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.show();
		WindowManager.LayoutParams params = alertDialog.getWindow()
				.getAttributes();
		params.width = (int) (width * 0.86);
		alertDialog.getWindow().setAttributes(params);
		alertDialog.setCanceledOnTouchOutside(false);
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.update);// 设置对话框的布局
		TextView descripttitle = (TextView) window
				.findViewById(R.id.description_title);
		descripttitle.setText("版本升级");
		TextView descriptiontv = (TextView) window
				.findViewById(R.id.description);
		if (description != null) {
			String desc = description.replace("#", "\r\n");
			descriptiontv.setMovementMethod(ScrollingMovementMethod
					.getInstance());
			descriptiontv.setText(desc);
		}
		Button sure = (Button) window.findViewById(R.id.sure);
		sure.setText("立即更新");
		sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				if (lastestUrl != null && lastestUrl.length() > 0) {
					// the service will give keyword to check,how to do;
					if (type == 0) {
						// 根据服务端返回的参数，决定跳转位置
						// 打开对应应用市场
						String channel = MyApp.getInstance().getChannelName();
						if ("华为应用市场".equals(channel)) {
							try {
								// 跳转到华为应用市场
								Intent intent = new Intent();
								intent.setAction("com.huawei.appmarket.intent.action.AppDetail");
								intent.putExtra("APP_PACKAGENAME",
										"com.ytdinfo.keephealth");
								context.startActivity(intent);
							} catch (Exception e) {
								Intent it = new Intent(Intent.ACTION_VIEW, Uri
										.parse(lastestUrl));
								it.setClassName("com.android.browser",
										"com.android.browser.BrowserActivity");
								context.startActivity(it);
							}
						} else if ("魅族应用市场".equals(channel)) {
							// 跳转到魅族应用市场
							try {
								// 跳转到魅族应用市场
								Intent intent = new Intent();
								intent.setAction("android.intent.action.VIEW");
				                intent.setData(Uri.parse("market://search?q=pname:com.ytdinfo.keephealth"));
								context.startActivity(intent);
							} catch (Exception e) {
								Intent it = new Intent(Intent.ACTION_VIEW, Uri
										.parse(lastestUrl));
								it.setClassName("com.android.browser",
										"com.android.browser.BrowserActivity");
								context.startActivity(it);
							}
						}
					} else if (type == 1) {
						// 启动本地下载
						gotoDownLoadFile(context, lastestUrl, desMd5, version);
					} else {
						// 使用网页打开
						Intent it = new Intent(Intent.ACTION_VIEW, Uri
								.parse(lastestUrl));
						it.setClassName("com.android.browser",
								"com.android.browser.BrowserActivity");
						context.startActivity(it);
					}
				}
			}
		});

		Button notsure = (Button) window.findViewById(R.id.notsure);
		notsure.setText("暂不更新");
		notsure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();
			}
		});
	}

	/**
	 * 上午11:01:04
	 * 
	 * @author zhangyh2 TODO {@link MainActivity}调用的方案
	 */
	public void showUpdateDialog(final MainActivity context,
			final String lastestUrl, final String desMd5, final int version,
			final int type, String description, final boolean isQZupdate) {
		if (alertDialog != null && alertDialog.isShowing()) {
			return;
		}
		isAutoShowed = true;
		alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (isQZupdate) {
						SharedPrefsUtil
								.putValue(Constants.CHECKEDID_RADIOBT, 0);
						SharedPrefsUtil.putValue(Constants.CHECKISUPDATE, true);
						System.exit(0);
					} else {
						// 计算间隔几天进行重新检验更新。
						Calendar calendar = Calendar.getInstance();
						calendar.set(Calendar.DAY_OF_YEAR,
								calendar.get(Calendar.DAY_OF_YEAR) + 3);
						SharedPrefsUtil.putValue(Constants.NOTUPDATE,
								calendar.get(Calendar.DAY_OF_YEAR));
					}
				}
				return false;
			}
		});
		alertDialog.show();
		WindowManager.LayoutParams params = alertDialog.getWindow()
				.getAttributes();
		params.width = (int) (width * 0.86);
		alertDialog.getWindow().setAttributes(params);
		alertDialog.setCanceledOnTouchOutside(false);
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.update);// 设置对话框的布局
		TextView descripttitle = (TextView) window
				.findViewById(R.id.description_title);
		descripttitle.setText("版本升级");
		TextView descriptiontv = (TextView) window
				.findViewById(R.id.description);
		if (description != null) {
			String desc = description.replace("#", "\r\n");
			descriptiontv.setMovementMethod(ScrollingMovementMethod
					.getInstance());
			descriptiontv.setText(desc);
		}
		Button sure = (Button) window.findViewById(R.id.sure);
		sure.setText("立即更新");
		sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				if (lastestUrl != null && lastestUrl.length() > 0) {
					// the service will give keyword to check,how to do;
					if (type == 0) {
						// 根据服务端返回的参数，决定跳转位置
						// 打开对应应用市场
						String channel = MyApp.getInstance().getChannelName();
						if ("华为应用市场".equals(channel)) {
							try {
								// 跳转到华为应用市场
								Intent intent = new Intent();
								intent.setAction("com.huawei.appmarket.intent.action.AppDetail");
								intent.putExtra("APP_PACKAGENAME",
										"com.ytdinfo.keephealth");
								context.startActivity(intent);
							} catch (Exception e) {
								Intent it = new Intent(Intent.ACTION_VIEW, Uri
										.parse(lastestUrl));
								it.setClassName("com.android.browser",
										"com.android.browser.BrowserActivity");
								context.startActivity(it);
							}
						} else if ("魅族应用市场".equals(channel)) {
							// 跳转到魅族应用市场
							try {
								// 跳转到魅族应用市场
								Intent intent = new Intent();
								intent.setAction("android.intent.action.VIEW");
				                intent.setData(Uri.parse("market://search?q=pname:com.ytdinfo.keephealth"));
								context.startActivity(intent);
							} catch (Exception e) {
								Intent it = new Intent(Intent.ACTION_VIEW, Uri
										.parse(lastestUrl));
								it.setClassName("com.android.browser",
										"com.android.browser.BrowserActivity");
								context.startActivity(it);
							}
						}

					} else if (type == 1) {
						// 启动本地下载
						gotoDownLoadFile(context, lastestUrl, desMd5, version);
					} else {
						// 使用网页打开
						Intent it = new Intent(Intent.ACTION_VIEW, Uri
								.parse(lastestUrl));
						it.setClassName("com.android.browser",
								"com.android.browser.BrowserActivity");
						context.startActivity(it);
					}
				}
				if (isQZupdate) {
					context.finish();
				}
			}
		});

		Button notsure = (Button) window.findViewById(R.id.notsure);
		notsure.setText("暂不更新");
		if (isQZupdate) {
			alertDialog.setCancelable(false);
			notsure.setVisibility(View.GONE);
		} else {
			alertDialog.setCancelable(true);
			notsure.setVisibility(View.VISIBLE);
			notsure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					alertDialog.dismiss();
					// 计算间隔几天进行重新检验更新。
					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.DAY_OF_YEAR,
							calendar.get(Calendar.DAY_OF_YEAR) + 3);
					SharedPrefsUtil.putValue(Constants.NOTUPDATE,
							calendar.get(Calendar.DAY_OF_YEAR));
				}
			});
		}
	}

	/**
	 * make true current connect service is wifi
	 * 
	 * @param mContext
	 * @return
	 */
	private static boolean isWifi(Context mContext) {
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * 上午11:23:05
	 * 
	 * @author zhangyh2 TODO 用户为使用WIFI，使用4G给用户提示。
	 */
	private void gotoDownLoadFile(final Context context,
			final String lastestUrl, final String desMd5, final int version) {
		if (isWifi(context)) {
			Intent intent = new Intent(context, UpdateService.class);
			intent.putExtra("url", lastestUrl);
			intent.putExtra("version", version);
			intent.putExtra("desMd5", desMd5);
			context.startService(intent);
		} else {
			// dialog to see

			if (isinWifi != null && isinWifi.isShowing()) {
				return;
			}
			isinWifi = new AlertDialog.Builder(context).create();
			isinWifi.setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(DialogInterface dialog, int keyCode,
						KeyEvent event) {
					// TODO Auto-generated method
					if (keyCode == KeyEvent.KEYCODE_BACK) {
						isinWifi.dismiss();
					}
					return false;
				}
			});
			isinWifi.show();
			WindowManager.LayoutParams params = isinWifi.getWindow()
					.getAttributes();
			params.width = (int) (width * 0.86);
			isinWifi.getWindow().setAttributes(params);
			isinWifi.setCanceledOnTouchOutside(false);
			Window window = isinWifi.getWindow();
			window.setContentView(R.layout.update);// 设置对话框的布局
			TextView descripttitle = (TextView) window
					.findViewById(R.id.description_title);
			descripttitle.setText("提示");
			TextView descriptiontv = (TextView) window
					.findViewById(R.id.description);
			descriptiontv.setText("非WiFi环境下更新版本将产生流量费用，确定继续？");
			Button sure = (Button) window.findViewById(R.id.sure);
			sure.setText("下载");
			sure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					isinWifi.dismiss();
					Intent intent = new Intent(context, UpdateService.class);
					intent.putExtra("url", lastestUrl);
					intent.putExtra("version", version);
					intent.putExtra("desMd5", desMd5);
					context.startService(intent);
				}
			});

			Button notsure = (Button) window.findViewById(R.id.notsure);
			notsure.setText("取消");
			isinWifi.setCancelable(true);
			notsure.setVisibility(View.VISIBLE);
			notsure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					isinWifi.dismiss();
				}
			});
		}
	}
}
