package com.ytdinfo.keephealth.jpush;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;

public class LittleHelperWebViewActivity extends BaseActivity {
	private WebView webview;
	private CommonActivityTopView topView;
	private Intent intent;
	private String loadUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_little_helper_web_view);

		
		
		topView = (CommonActivityTopView) findViewById(R.id.id_CommonActivityTopView);
		topView.tv_title.setText("帮忙医小助手");
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

	private void loadWebView() {
		webview = (WebView) findViewById(R.id.id_webview);
		// 设置WebView属性，能够执行Javascript脚本
		webview.getSettings().setJavaScriptEnabled(true);

		WebSettings settings = webview.getSettings();
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);

		// 加载需要显示的网页
		webview.loadUrl(loadUrl);
	}

	private void webViewListener() {
		webview.setWebViewClient(new WebViewClient() {
			// 当点击webview中链接时触发该方法
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return super.shouldOverrideUrlLoading(view, url);
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
//				webview.loadUrl("file:///android_asset/error.html#"
//						+ failingUrl);
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
