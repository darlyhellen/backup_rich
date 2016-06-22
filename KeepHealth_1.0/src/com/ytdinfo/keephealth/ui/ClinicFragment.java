/**下午2:58:44
 * @author zhangyh2
 * ClinicFragment.java
 * TODO
 */
package com.ytdinfo.keephealth.ui;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
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
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.ui.clinic.ClinicWebView;
import com.ytdinfo.keephealth.ui.login.LoginActivity;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;
import com.ytdinfo.keephealth.ui.view.MyProgressDialog;
import com.ytdinfo.keephealth.ui.view.MyWebView;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;

/**
 * @author zhangyh2 ClinicFragment 下午2:58:44 TODO
 *         这个Fragment应公司要求，将门诊嵌入到APP中。由于底部标签按钮也存在门诊
 *         ，所以不能以Activity的形式展示，只能以Fragment的形式展示。
 */
public class ClinicFragment extends Fragment {
	private String TAG = getClass().getSimpleName();
	private MyWebView webview;
	private CommonActivityTopView mainTitle;
	private MyProgressDialog myProgressDialog2;

	private Button bt_update;
	private String loadUrl;

	private String current_url;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.activity_clinic, container,
				false);// 关联布局文件
		mainTitle = (CommonActivityTopView) rootView
				.findViewById(R.id.v21_clinic_title);
		mainTitle.setback();
		bt_update = (Button) rootView.findViewById(R.id.id_bt_update);
		webview = (MyWebView) rootView.findViewById(R.id.id_webview);
		myProgressDialog2 = new MyProgressDialog(getActivity());
		myProgressDialog2.setMessage("正在请求...");
		myProgressDialog2.show();
		mainTitle.tv_title.setText("门诊");
		loadUrl = Constants.HOMEINDEX;/* intent.getStringExtra("loadUrl"); */
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
		return rootView;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.i(TAG, "onCreate");
	}

	@SuppressLint({ "NewApi", "SetJavaScriptEnabled" })
	private void loadWebView() {
		// 实例化WebView对象
		// webview = new MyWebView(WebViewActivity.this);
		LogUtil.i(TAG, "loadWebView===实例化WebView===");

		new MobclickAgentJSInterface(getActivity(), webview);

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
		String appCacheDir = getActivity().getApplicationContext()
				.getDir("cache", Context.MODE_PRIVATE).getPath();
		webSettings.setAppCachePath(appCacheDir);
		webSettings.setAllowFileAccess(true);
		// 启用数据库
		webSettings.setDatabaseEnabled(true);
		// 设置定位的数据库路径
		String dir = getActivity().getApplicationContext()
				.getDir("database", Context.MODE_PRIVATE).getPath();
		webSettings.setGeolocationDatabasePath(dir);
		// 启用地理定位
		webSettings.setGeolocationEnabled(true);
		// 最重要的方法，一定要设置，这就是出不来的主要原因
		webSettings.setDomStorageEnabled(true);

		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

		// js调用安卓方法
		webview.addJavascriptInterface(this, "RedirectListner");
	}

	private void webViewListener() {
		webview.setWebChromeClient(new WebChromeClient() {
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
				LogUtil.i(TAG, "拦截url---onPageStarted-->" + url);
				if (url.contains(Constants.HOMEINDEX)) {
					mainTitle.ibt_back.setVisibility(View.GONE);
				} else {
					mainTitle.ibt_back.setVisibility(View.VISIBLE);
				}
				HashMap<String, String> hashmap = new HashMap<String, String>();
				if (null != SharedPrefsUtil.getValue(Constants.TOKEN, null)) {
					hashmap.put("token",
							SharedPrefsUtil.getValue(Constants.TOKEN, null));
				}
				if (url.toLowerCase().contains("/login")) {
					Intent i11 = new Intent();
					i11.setClass(getActivity(), LoginActivity.class);
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

	private void initListener() {
		mainTitle.ibt_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (webview.canGoBack()) {
					webview.goBack();
				} else {
					mainTitle.ibt_back.setVisibility(View.GONE);
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

	@Override
	public void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume");
		MobclickAgent.onPageStart("ClinicFragment"); // 统计页面
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause");
		MobclickAgent.onPageEnd("ClinicFragment");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		LogUtil.i(TAG, "onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.i(TAG, "onDestroy");
	}

}