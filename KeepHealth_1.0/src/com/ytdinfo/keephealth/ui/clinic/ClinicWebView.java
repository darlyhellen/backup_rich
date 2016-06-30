package com.ytdinfo.keephealth.ui.clinic;

import java.util.HashMap;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgentJSInterface;
import com.umeng.socialize.UMShareAPI;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;
import com.ytdinfo.keephealth.ui.view.MyPopWindow;
import com.ytdinfo.keephealth.ui.view.MyProgressDialog;
import com.ytdinfo.keephealth.ui.view.MyWebView;
import com.ytdinfo.keephealth.utils.ImageTools;
import com.ytdinfo.keephealth.utils.JsonUtil;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;
import com.ytdinfo.keephealth.wxapi.CustomShareBoard;
import com.ytdinfo.keephealth.wxapi.WXCallBack;

@SuppressLint("JavascriptInterface")
public class ClinicWebView extends BaseActivity implements WXCallBack {
	private final String TAG = "WebViewActivity";
	// private CommonActivityTopView commonActivityTopView;
	private MyWebView webview;
	// private RelativeLayout rl;
	private CommonActivityTopView mainTitle;

	private Button bt_update;
	private Intent intent;
	private String loadUrl;

	private String current_url;

	/**
	 * 下午2:24:15 TODO 是否子页面
	 */
	private boolean isPageLoaded = false;
	private boolean isback = false;

	private MyProgressDialog myProgressDialog2;

	public CustomShareBoard shareBoard;

	private boolean isUserAsy;

	private MyPopWindow mypop;
	private PopupWindow pop;
	private MyProgressDialog myProgressDialog;
	private String image_path;
	private Bitmap feedback;// 需要上传的Bitmap

	// private UMSocialService mController = UMServiceFactory
	// .getUMSocialService(Constants.DESCRIPTOR);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clinic);

		mainTitle = (CommonActivityTopView) findViewById(R.id.v21_clinic_title);
		myProgressDialog2 = new MyProgressDialog(this);
		myProgressDialog2.setMessage("加载中...");

		intent = getIntent();
		String titles = intent.getStringExtra("title");
		if (!TextUtils.isEmpty(titles)) {
			mainTitle.tv_title.setText(titles);
		}
		loadUrl = intent.getStringExtra("loadUrl");
		bt_update = (Button) findViewById(R.id.id_bt_update);

		loadWebView();
		webViewListener();
		initListener();
		if (loadUrl != null && !loadUrl.contains(Constants.NEWSLISTS)
				&& !loadUrl.contains(Constants.NEWSPAGE)
				&& loadUrl.contains(Constants.ROOT_WEB)) {
			SYZUser(loadUrl);
		} else {
			LogUtil.i(TAG, SharedPrefsUtil.getValue(Constants.TOKEN, null));
			HashMap<String, String> hashmap = new HashMap<String, String>();
			if (null != SharedPrefsUtil.getValue(Constants.TOKEN, null)) {
				hashmap.put("token",
						SharedPrefsUtil.getValue(Constants.TOKEN, null));
			}
			webview.loadUrl(loadUrl, hashmap);
		}
	}

	/**
	 * 上午11:07:12
	 * 
	 * @author zhangyh2 TODO
	 */
	private void initPOP() {
		// TODO Auto-generated method stub
		mypop = new MyPopWindow(this);
		pop = mypop.getPop();
	}

	/**
	 * 下午3:25:00
	 * 
	 * @author zhangyh2 TODO 同步用户有
	 */
	private void SYZUser(final String url) {
		// TODO Auto-generated method stub
		LogUtils.i(TAG + "开始用户微官网数据同步" + url);
		String jsonUserModel = SharedPrefsUtil
				.getValue(Constants.USERMODEL, "");
		UserModel userModel = new Gson().fromJson(jsonUserModel,
				UserModel.class);

		JSONObject jsonParam = new JSONObject();
		// UserModel参数说明：
		// UserName(姓名)；Telphone（手机号）;pID;HeadPicture(用户头像)
		if (userModel == null) {
			return;
		}
		try {
			jsonParam.put("UserName", ""/* userModel.getUserName() */);
			jsonParam.put("Telphone", userModel.getMobilephone());
			jsonParam.put("pID", userModel.getPid());
			jsonParam.put("HeadPicture", userModel.getHeadPicture());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpClient.post(Constants.SYZUSER, jsonParam.toString(),
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						String cookieString = "";
						Header[] headers = arg0.getHeaders("Set-Cookie");
						for (int i = 0; i < headers.length; i++) {
							cookieString = headers[i].getValue() + ";";
						}
						cookieString = cookieString + "domain:test.bmyi.cn";
						CookieSyncManager
								.createInstance(getApplicationContext());
						CookieManager cookieManager = CookieManager
								.getInstance();
						cookieManager.setAcceptCookie(true);
						cookieManager.removeSessionCookie();// 移除
						cookieManager.removeAllCookie();
						cookieManager.setCookie(url, cookieString);
						LogUtils.i(cookieManager.getCookie(url));
						CookieSyncManager.getInstance().sync();

						LogUtils.i(arg0.result.toString());
						try {
							JSONObject object = new JSONObject(arg0.result
									.toString());
							String stat = object.getString("Status");
							if ("Success".equals(stat)) {
								isUserAsy = true;
								webview.loadUrl(url);
							} else {
								isUserAsy = false;
								webview.loadUrl("file:///android_asset/uzan_error.html#"
										+ url);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							isUserAsy = false;
							e.printStackTrace();
							webview.loadUrl("file:///android_asset/uzan_error.html#"
									+ url);
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						LogUtils.i(arg1);
						isUserAsy = false;
						webview.loadUrl("file:///android_asset/uzan_error.html#"
								+ url);
					}
				});
	}

	private void initListener() {
		mainTitle.ibt_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (webview.canGoBack()) {
					webview.goBack();
				} else {
					finish();
				}
			}
		});
		bt_update.setOnClickListener(new OnClickListener() {
			// 重新加载
			@Override
			public void onClick(View v) {
				LogUtil.i(TAG, "重新加载");
				LogUtil.i("paul", current_url);
			}
		});
	}

	/**
	 * 同步一下cookie
	 */
	public static void synCookies(Context context, String url) {
		CookieSyncManager.createInstance(context);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		cookieManager.removeAllCookie();// 移除
		String token = SharedPrefsUtil.getValue(Constants.TOKEN, null);
		cookieManager.acceptCookie();
		cookieManager.setCookie(url, "token=" + token + ";path=/");// cookies是在HttpClient中获得的cookie
		CookieSyncManager.getInstance().sync();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	private void loadWebView() {
		// 实例化WebView对象
		// webview = new MyWebView(WebViewActivity.this);
		LogUtil.i(TAG, "loadWebView===实例化WebView===");
		webview = (MyWebView) findViewById(R.id.id_webview);
		new MobclickAgentJSInterface(this, webview);

		WebSettings webSettings = webview.getSettings();

		// 设置可以访问文件
		webSettings.setAllowFileAccess(true);
		webSettings.setPluginState(PluginState.ON);
		// 设置WebView属性，能够执行Javascript脚本
		webSettings.setBuiltInZoomControls(true);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webSettings.setUseWideViewPort(true);// 关键点
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setAppCacheEnabled(true);

		webSettings.setAppCacheMaxSize(8 * 1024 * 1024); // 8MB
		// webSettings.setAppCachePath(Constants.WEBVIEW_CACHE_DIR );
		String appCacheDir = this.getApplicationContext()
				.getDir("cache", Context.MODE_PRIVATE).getPath();
		webSettings.setAppCachePath(appCacheDir);
		webSettings.setDomStorageEnabled(true);
		// 启用数据库
		webSettings.setDatabaseEnabled(true);
		// 设置定位的数据库路径
		String dir = this.getApplicationContext()
				.getDir("database", Context.MODE_PRIVATE).getPath();
		webSettings.setGeolocationDatabasePath(dir);
		// 启用地理定位
		webSettings.setGeolocationEnabled(true);

		webSettings.setCacheMode(WebSettings.LOAD_NORMAL);
		webSettings.setDefaultTextEncodingName("UTF-8");

		// js调用安卓方法
		webview.addJavascriptInterface(this, "RedirectListner");

	}

	private void webViewListener() {
		webview.setWebChromeClient(new WebChromeClient() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.webkit.WebChromeClient#onReceivedTitle(android.webkit
			 * .WebView, java.lang.String)
			 */
			@Override
			public void onReceivedTitle(WebView view, String title) {
				// TODO Auto-generated method stub
				super.onReceivedTitle(view, title);
				LogUtils.i("onReceivedTitle--->" + title);
				mainTitle.tv_title.setText(title);

			}

			@Override
			public void onGeolocationPermissionsShowPrompt(String origin,
					Callback callback) {
				// TODO Auto-generated method stub
				if (isGpsEnable()) {
					callback.invoke(origin, true, false);
				} else {
					// 打开GPS
					isOpenGps();
				}
				super.onGeolocationPermissionsShowPrompt(origin, callback);
			}

		});
		webview.setWebViewClient(new WebViewClient() {
			// 加载失败
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {

				LogUtil.i(TAG, "加载失败===" + errorCode + "---" + description
						+ "---" + failingUrl + "---");

				current_url = failingUrl;
				webview.clearView();
				if (failingUrl.contains("#")) {
					String[] temp;
					temp = failingUrl.split("#");
					webview.loadUrl(temp[0]);
					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					webview.loadUrl(failingUrl);
				} else {
					webview.loadUrl("file:///android_asset/uzan_error.html#"
							+ failingUrl);
				}

				ToastUtil.showMessage("页面加载失败，请点击重新加载");

				// super.onReceivedError(view, errorCode, description,
				// failingUrl);

			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				/*
				 * Toast.makeText(getApplicationContext(),
				 * "WebViewClient.shouldOverrideUrlLoading",
				 * Toast.LENGTH_SHORT);
				 */
				LogUtil.i(TAG, "拦截url---shouldOverrideUrlLoading-->" + url);
				if (url.startsWith("tel:")) {
					Intent intent = new Intent(Intent.ACTION_DIAL, Uri
							.parse(url));
					startActivity(intent);
				} else {
					HashMap<String, String> hashmap = new HashMap<String, String>();
					if (null != SharedPrefsUtil.getValue(Constants.TOKEN, null)) {
						hashmap.put("token",
								SharedPrefsUtil.getValue(Constants.TOKEN, null));
					}
					webview.loadUrl(url, hashmap);
				}

				return true;
			}

			@Override
			public void onLoadResource(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onLoadResource(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				CookieManager cookieManager = CookieManager.getInstance();
				LogUtils.i("onPageStarted" + cookieManager.getCookie(url));
				cookieManager.acceptCookie();
				if (url.contains("ryclinic://yuyuechenggong")) {
					// 预约成功跳转首页
					setResult(1001, intent);
					finish();
				}

				if (!url.contains(Constants.NEWSPAGE)
						&& myProgressDialog2 != null) {
					myProgressDialog2.show();
				}
				if (url.equalsIgnoreCase(Constants.HOMEINDEX)) {
					// 跳转到首页
					setResult(1002, intent);
					finish();
				}

				LogUtil.i(TAG, "拦截url---onPageStarted-->" + url);
				HashMap<String, String> hashmap = new HashMap<String, String>();
				if (null != SharedPrefsUtil.getValue(Constants.TOKEN, null)) {
					hashmap.put("token",
							SharedPrefsUtil.getValue(Constants.TOKEN, null));
				}
				if (url.toLowerCase().contains("/login")) {
					/*
					 * Intent i11 = new Intent();
					 * i11.setClass(ClinicWebView.this, LoginActivity.class);
					 * startActivity(i11);
					 */
					SYZUser(url);
				}
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				LogUtil.i(TAG, "页面加载完后==onPageFinished==" + url + "---");
				myProgressDialog2.dismiss();
				if (view.getTitle() != null && view.getTitle().length() < 30) {
					mainTitle.tv_title.setText(view.getTitle());
				}
			}
		});
	}

	/**
	 * 上午10:36:43
	 * 
	 * @author zhangyh2 TODO 判断GPS是否打开
	 */
	private boolean isGpsEnable() {
		LocationManager locationManager = ((LocationManager) getSystemService(Context.LOCATION_SERVICE));
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			LogUtil.i(TAG, "==onKeyDown==");
			if (webview.canGoBack()) {
				webview.goBack();
			} else {
				finish();
			}
		}
		return false;
	}

	/**
	 * JS调用的方法
	 */
	@JavascriptInterface
	public void goToIndex() {
		Log.i(TAG, "goToIndex()");
		finish();
	}

	/**
	 * JS调用的方法
	 */
	@JavascriptInterface
	public void reloadUrl(String url) {
		Log.i(TAG, "reloadUrl()");
		if (isUserAsy) {
			HashMap<String, String> hashmap = new HashMap<String, String>();
			if (null != SharedPrefsUtil.getValue(Constants.TOKEN, null)) {
				hashmap.put("token",
						SharedPrefsUtil.getValue(Constants.TOKEN, null));
			}
			webview.clearView();
			webview.loadUrl(url, hashmap);
		} else {
			SYZUser(url);
		}

	}

	/**
	 * JS调用的方法
	 */
	@JavascriptInterface
	public void goToActivity(String packageName, String className,
			boolean isCloseCurrent) {
		Intent intent = new Intent();
		intent.setClassName(packageName, className);

		startActivity(intent);
		if (isCloseCurrent)
			this.finish();
	}

	/**
	 * JS调用的方法
	 */
	@JavascriptInterface
	public void goToUpdateUrl() {
		Log.i(TAG, "goToUpdateUrl()");
		webview.loadUrl("file:///android_asset/a.html？'" + current_url + "'");
		// finish();
	}

	/**
	 * JS调用的方法
	 */
	@JavascriptInterface
	public void goToPhone(int number) {
		Log.i(TAG, "goToPhone()");
		// 用intent启动拨打电话
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ number));
		startActivity(intent);

		// finish();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("WebViewActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("WebViewActivity");
		MobclickAgent.onPause(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytdinfo.keephealth.ui.BaseActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		webview.loadUrl("about:blank");
		webview.stopLoading();
		webview.setWebChromeClient(null);
		webview.setWebViewClient(null);
		webview.destroy();
		webview = null;
	}

	// 标题 图片Url,网址Url,网址简单描述
	@JavascriptInterface
	public void shareWebSiteToPlatForm(String titleName, String thumbUrl,
			String url, String siteDesc) {
		// setShareContent(titleName, thumbUrl, url, siteDesc);
		shareBoard.setShareContent(titleName, thumbUrl, url, siteDesc);
		Log.i("shareWebSiteToPlatForm", titleName + thumbUrl + url + siteDesc);
		postShare();
	}

	/**
	 * 调用postShare分享。跳转至分享编辑页，然后再分享。</br> [注意]<li>
	 * 对于新浪，豆瓣，人人，腾讯微博跳转到分享编辑页，其他平台直接跳转到对应的客户端
	 */
	private void postShare() {
		shareBoard.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM,
				0, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytdinfo.keephealth.wxapi.WXCallBack#shareComplete(boolean)
	 */
	@Override
	public void shareComplete(boolean flag) {
		// TODO Auto-generated method stub
		Log.i("shareComplete", "webview.loadUrl--s" + flag);
		webview.loadUrl("javascript:shareCheck('" + flag + "')");
		Log.i("shareComplete", "webview.loadUrl--e" + flag);
	}

	@JavascriptInterface
	public void setSharedTheme(String theme) {
		Constants.DESCRIPTOR = theme;
		LogUtil.i("变更分享主题" + theme);
		shareBoard = new CustomShareBoard(ClinicWebView.this);
		shareBoard.setWXCallBack(ClinicWebView.this);
	}

	public void isOpenGps() {
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					alertDialog.dismiss();
				}
				return false;
			}
		});
		alertDialog.show();
		alertDialog.setCanceledOnTouchOutside(false);
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.update);// 设置对话框的布局

		TextView title = (TextView) window.findViewById(R.id.description_title);
		title.setText("提示");
		TextView descriptiontv = (TextView) window
				.findViewById(R.id.description);
		descriptiontv.setText("是否允许打开GPS定位权限?");
		Button sure = (Button) window.findViewById(R.id.sure);
		sure.setText("允许");
		sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				Intent intent = new Intent(
						Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivityForResult(intent, 10001);

			}
		});

		Button notsure = (Button) window.findViewById(R.id.notsure);
		notsure.setText("不允许");
		alertDialog.setCancelable(true);
		notsure.setVisibility(View.VISIBLE);
		notsure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				webview.loadUrl("javascript:NativeNoGPS()");
				alertDialog.dismiss();
			}
		});
	}

	@JavascriptInterface
	public void setImageDir() {
		LogUtil.i("调用上传图片方法");
		initPOP();
		// 启动弹层
		pop.showAtLocation(webview, Gravity.BOTTOM, 0, 0);
		// 隐藏输入法
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(this.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@SuppressLint("NewApi")
	private void cropImage(String image_path) {
		feedback = ImageTools.cropBitmap(image_path);
		LogUtil.i("paul", feedback.getByteCount() / 1024 + "K");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 10001) {
			if (isGpsEnable()) {
				webview.reload();
			} else {
				webview.loadUrl("javascript:NativeNoGPS()");
			}
		}

		if (pop != null) {
			image_path = mypop.INonActivityResult(requestCode, data, 0);
			if (image_path == null) {
				return;
			}

			myProgressDialog = new MyProgressDialog(this);
			myProgressDialog.setMessage("上传照片....");
			myProgressDialog.show();

			new Thread(new Runnable() {

				@Override
				public void run() {
					cropImage(image_path);
					handler.sendEmptyMessage(0);
				}
			}).start();

		}
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (feedback != null) {
				// 上传图片
				requestImages();
			}
		};
	};

	private void requestImages() {
		try {

			// 向服务器发送请求
			JSONArray jsonArray = JsonUtil.bitmapTOjsonArray(feedback);

			HttpClient.post(Constants.APIUPLOADPICFORCLINIC,
					jsonArray.toString(), new RequestCallBack<String>() {

						@Override
						public void onStart() {
							Log.i("HttpUtil", "onStart");
						}

						@Override
						public void onLoading(long total, long current,
								boolean isUploading) {
							Log.i("HttpUtil", "onLoading");
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							Log.i("HttpUtil", "onSuccess");
							Log.i("HttpUtil", "onSuccess==="
									+ responseInfo.result.toString());

							try {
								JSONObject jsonObject = new JSONObject(
										responseInfo.result.toString());
								JSONArray jsonArray = jsonObject
										.getJSONArray("path");
								String suc = jsonObject.getString("Success");
								if ("true".equals(suc)) {
									if (jsonArray != null
											&& jsonArray.length() > 0) {
										webview.loadUrl("javascript:showPicList('"
												+ jsonArray.get(0).toString()
												+ "')");
									}

								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

							myProgressDialog.dismiss();

						}

						@Override
						public void onFailure(HttpException error, String msg) {
							Log.i("HttpUtil", "onFailure===" + msg);
							LogUtil.i("===============", error.toString());

							myProgressDialog.dismiss();
							ToastUtil.showMessage("照片上传失败");
						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
