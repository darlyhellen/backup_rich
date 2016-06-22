package com.ytdinfo.keephealth.ui.clinic;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.lidroid.xutils.util.LogUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgentJSInterface;
import com.umeng.socialize.UMShareAPI;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.login.LoginActivity;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;
import com.ytdinfo.keephealth.ui.view.MyProgressDialog;
import com.ytdinfo.keephealth.ui.view.MyWebView;
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

	// private UMSocialService mController = UMServiceFactory
	// .getUMSocialService(Constants.DESCRIPTOR);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clinic);

		mainTitle = (CommonActivityTopView) findViewById(R.id.v21_clinic_title);
		myProgressDialog2 = new MyProgressDialog(this);
		myProgressDialog2.setMessage("正在请求...");
		myProgressDialog2.show();

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
		LogUtil.i(TAG, SharedPrefsUtil.getValue(Constants.TOKEN, null));
		HashMap<String, String> hashmap = new HashMap<String, String>();
		if (null != SharedPrefsUtil.getValue(Constants.TOKEN, null)) {
			hashmap.put("token",
					SharedPrefsUtil.getValue(Constants.TOKEN, null));
		}
		webview.loadUrl(loadUrl, hashmap);
	}

	private void initListener() {
		mainTitle.ibt_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isPageLoaded) {
					webview.loadUrl(Constants.NEWSLISTS);
				} else {
					if (isback) {
						finish();
					} else {
						if (webview.canGoBack()) {
							webview.goBack();
						} else {
							finish();
						}
					}
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
		cookieManager.removeSessionCookie();// 移除
		String token = SharedPrefsUtil.getValue(Constants.TOKEN, null);
		cookieManager.setCookie(url, "token=" + token + ";path=/");// cookies是在HttpClient中获得的cookie
		CookieSyncManager.getInstance().sync();
	}

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	private void loadWebView() {
		// 实例化WebView对象
		// webview = new MyWebView(WebViewActivity.this);
		LogUtil.i(TAG, "loadWebView===实例化WebView===");
		webview = (MyWebView) findViewById(R.id.id_webview);
		new MobclickAgentJSInterface(this, webview);

		WebSettings webSettings = webview.getSettings();
		// 设置WebView属性，能够执行Javascript脚本
		webSettings.setJavaScriptEnabled(true);
		webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		//
		webSettings.setUseWideViewPort(true);// 关键点
		webSettings.setLoadWithOverviewMode(true);
		webSettings.setAppCacheEnabled(true);

		webSettings.setAppCacheMaxSize(8 * 1024 * 1024); // 8MB
		// webSettings.setAppCachePath(Constants.WEBVIEW_CACHE_DIR );
		String appCacheDir = this.getApplicationContext()
				.getDir("cache", Context.MODE_PRIVATE).getPath();
		webSettings.setAppCachePath(appCacheDir);
		webSettings.setAllowFileAccess(true);
		webSettings.setDomStorageEnabled(true);
		// 启用数据库
		webSettings.setDatabaseEnabled(true);
		// 设置定位的数据库路径
		String dir = this.getApplicationContext()
				.getDir("database", Context.MODE_PRIVATE).getPath();
		webSettings.setGeolocationDatabasePath(dir);
		// 启用地理定位
		webSettings.setGeolocationEnabled(true);

		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

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
				callback.invoke(origin, true, false);
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
				if (url.contains(Constants.NEWSPAGE)) {
					isPageLoaded = true;
				} else {
					isPageLoaded = false;
				}
				if (url.contains(Constants.NEWSLISTS)) {
					isback = true;
				} else {
					isback = false;
				}

				LogUtil.i(TAG, "拦截url---onPageStarted-->" + url);
				HashMap<String, String> hashmap = new HashMap<String, String>();
				if (null != SharedPrefsUtil.getValue(Constants.TOKEN, null)) {
					hashmap.put("token",
							SharedPrefsUtil.getValue(Constants.TOKEN, null));
				}
				if (url.toLowerCase().contains("/login")) {
					Intent i11 = new Intent();
					i11.setClass(ClinicWebView.this, LoginActivity.class);
					startActivity(i11);
				}
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				LogUtil.i(TAG, "页面加载完后==onPageFinished==" + url);
				myProgressDialog2.dismiss();
				mainTitle.tv_title.setText(view.getTitle());
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			LogUtil.i(TAG, "==onKeyDown==");
			if (isPageLoaded) {
				webview.loadUrl(Constants.NEWSLISTS);
			} else {
				if (isback) {
					finish();
				} else {
					if (webview.canGoBack()) {
						webview.goBack();
					} else {
						finish();
					}
				}
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
		HashMap<String, String> hashmap = new HashMap<String, String>();
		if (null != SharedPrefsUtil.getValue(Constants.TOKEN, null)) {
			hashmap.put("token",
					SharedPrefsUtil.getValue(Constants.TOKEN, null));
		}
		webview.clearView();
		webview.loadUrl(url, hashmap);
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
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
	}

}
