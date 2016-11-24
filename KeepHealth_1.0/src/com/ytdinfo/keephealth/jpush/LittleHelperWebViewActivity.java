package com.ytdinfo.keephealth.jpush;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgentJSInterface;
import com.youzan.sdk.Callback;
import com.youzan.sdk.YouzanSDK;
import com.youzan.sdk.YouzanUser;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.WebViewActivity;
import com.ytdinfo.keephealth.ui.ZHWebViewActivity;
import com.ytdinfo.keephealth.ui.clinic.ClinicWebView;
import com.ytdinfo.keephealth.ui.clinic.NativeClinicWebView;
import com.ytdinfo.keephealth.ui.login.LoginActivity;
import com.ytdinfo.keephealth.ui.uzanstore.WebActivity;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;

public class LittleHelperWebViewActivity extends BaseActivity {
	private String TAG = getClass().getSimpleName();
	private WebView webview;
	private CommonActivityTopView topView;
	private Intent intent;
	private String loadUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == SharedPrefsUtil.getValue(Constants.TOKEN, null)) 
		{
			Intent i = new Intent(this, LoginActivity.class);
			startActivityForResult(i, 1003);
			this.finish();
			return;
		}
		setContentView(R.layout.activity_little_helper_web_view);

		topView = (CommonActivityTopView) findViewById(R.id.id_CommonActivityTopView);
		topView.tv_title.setText("帮忙医小助手");
		SharedPrefsUtil.putValue(Constants.CHECKEDID_RADIOBT, 1);
		SharedPrefsUtil.putValue(Constants.CHECKISUPDATE, true);
		topView.ibt_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		intent = getIntent();
		loadUrl = intent.getStringExtra("loadUrl");

		loadWebView();
		webViewListener();

	}

	@SuppressLint({ "NewApi", "JavascriptInterface" })
	private void loadWebView() {
		
		webview = (WebView) findViewById(R.id.id_webview);
		// 设置WebView属性，能够执行Javascript脚本
        LogUtil.i(TAG, "loadWebView===实例化WebView===");
        new MobclickAgentJSInterface(this, webview);
        WebSettings webSettings = webview.getSettings();
        // 设置WebView属性，能够执行Javascript脚本
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //
        webSettings.setUseWideViewPort(true);// 关键点
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // js调用安卓方法
        webview.addJavascriptInterface(this, "RedirectListner");

		// 加载需要显示的网页
		webview.loadUrl(loadUrl);
	}

	private void webViewListener() {
		webview.setWebViewClient(new WebViewClient() {
			// 当点击webview中链接时触发该方法
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// webview.loadUrl("file:///android_asset/error.html#"
				// + failingUrl);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.webkit.WebViewClient#onPageStarted(android.webkit.WebView
			 * , java.lang.String, android.graphics.Bitmap)
			 */
			@Override
			public void onPageStarted(WebView view, final String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				if (url.contains("typeshop")) {
					String jsonUserModel = SharedPrefsUtil.getValue(
							Constants.USERMODEL, "");
					UserModel userModel = new Gson().fromJson(jsonUserModel,
							UserModel.class);
					YouzanUser user = new YouzanUser();
					user.setUserId(userModel.getPid() + "");
					int sex = 0;
					if ("Man".endsWith(userModel.getUserSex())) {
						sex = 1;
					}
					user.setGender(sex);
					user.setNickName(userModel.getAddition1());
					user.setTelephone(userModel.getMobilephone());
					user.setUserName(userModel.getUserName());
					YouzanSDK.asyncRegisterUser(user, new Callback() {
						@Override
						public void onCallback() {
							Intent i = new Intent();
							i.setClass(LittleHelperWebViewActivity.this, WebActivity.class);
							i.putExtra("loadUrl", url);
							LittleHelperWebViewActivity.this.startActivity(i);
						}
					});
					view.stopLoading();
					return;
				} else if (url.contains("typeclinic")) {
					Intent i = new Intent();
					i.setClass(LittleHelperWebViewActivity.this, ClinicWebView.class);
					i.putExtra("loadUrl", url);
					LittleHelperWebViewActivity.this.startActivity(i);
					view.stopLoading();
					return;
				} else if (url.contains("typeserver")) {
					Intent i = new Intent();
					i.setClass(LittleHelperWebViewActivity.this, ZHWebViewActivity.class);
					i.putExtra("loadUrl", url);
					LittleHelperWebViewActivity.this.startActivity(i);
					view.stopLoading();
					return;
				} else if (url.contains("typenormal")) {
					Intent i = new Intent();
					i.setClass(LittleHelperWebViewActivity.this, WebViewActivity.class);
					i.putExtra("loadUrl", url);
					LittleHelperWebViewActivity.this.startActivity(i);
					view.stopLoading();
					return;
				} else if (url.contains("typenative")) {
					Intent i = new Intent();
					i.setClass(LittleHelperWebViewActivity.this, NativeClinicWebView.class);
					i.putExtra("loadUrl", url);
					LittleHelperWebViewActivity.this.startActivity(i);
					view.stopLoading();
					return;
				}
				super.onPageStarted(view, url, favicon);
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 点击手机的回退键，触发
		if (webview.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
			Log.i(getClass().getName(), "webview.goBack()");
			webview.goBack(); // goBack()表示返回webView的上一页面
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.i(getClass().getName(), "webview退出");
			finish();
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();

		MobclickAgent.onPageStart("LittleHelperWebViewActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		MobclickAgent.onPageEnd("LittleHelperWebViewActivity");
		MobclickAgent.onPause(this);
	}

}
