package com.ytdinfo.keephealth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;

public class TmallWebViewActivity extends BaseActivity {
	private final String TAG = "TmallWebViewActivity";
	private WebView webview;
	private Intent intent;
	private String loadUrl;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tmall_web_view);
		webview = (WebView) findViewById(R.id.tmall_webview);
		intent = getIntent();
		loadUrl = intent.getStringExtra("loadUrl");

		loadWebView();
//		webViewListener();
		 webview.loadUrl(loadUrl);

	}
	
	private void loadWebView() {
		// WebSettings settings = webview.getSettings();
		// 实例化WebView对象

		WebSettings settings = webview.getSettings();
		// 设置WebView属性，能够执行Javascript脚本
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);

		 webview.setWebViewClient(new WebViewClient(){
             @Override
             public boolean shouldOverrideUrlLoading(WebView view, String url){
                      
                     return false;
                      
             }
     });

		// 加载需要显示的网页
		
		
		// 设置Web视图
		//setContentView(webview);
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
	public void onResume() {
		super.onResume();
		
		MobclickAgent.onPageStart("TmallWebViewActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		
		MobclickAgent.onPageEnd("TmallWebViewActivity");
		MobclickAgent.onPause(this);
	}

}
