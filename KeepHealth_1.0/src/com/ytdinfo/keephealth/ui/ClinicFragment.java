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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgentJSInterface;
import com.youzan.sdk.Callback;
import com.youzan.sdk.YouzanSDK;
import com.youzan.sdk.YouzanUser;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.clinic.ClinicWebView;
import com.ytdinfo.keephealth.ui.uzanstore.WebActivity;
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

	private Button bt_update;
	private String loadUrl;

	private String current_url;
	
	private MyProgressDialog synuser;

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
		mainTitle.ibt_back.setVisibility(View.GONE);
		bt_update = (Button) rootView.findViewById(R.id.id_bt_update);
		webview = (MyWebView) rootView.findViewById(R.id.id_webview);
		mainTitle.tv_title.setText("帮忙医门诊");
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
		webSettings.setDomStorageEnabled(true);

		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

		// js调用安卓方法
		webview.addJavascriptInterface(this, "RedirectListner");
	}

	private void webViewListener() {
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
			public void onLoadResource(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onLoadResource(view, url);
			}

			@Override
			public void onPageStarted(WebView view, final String url, Bitmap favicon) {
				LogUtil.i(TAG, "拦截url---onPageStarted-->" + url);
				if (!url.equals(Constants.HOMEINDEX) && !url.contains("#")) {
					// 用户点击任何连接，都跳转到另一个Webview中
					webview.stopLoading();
					if (url.contains(Constants.ROOT_WEB + "/html")) {
						webview.loadUrl(Constants.HOMEINDEX);
					}
					if (!url.contains("/login")) {
						Intent intent = new Intent(getActivity(),
								ClinicWebView.class);
						intent.putExtra("loadUrl", url);
						startActivityForResult(intent, 1001);
						return;
					}
					
				}
				if (url.contains("koudaitong.com")) {
                    synuser = new MyProgressDialog(getActivity());
                    synuser.setMessage("加载中...");
                    synuser.show();
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
                            synuser.dismiss();
                            Intent i = new Intent();
                            i.setClass(getActivity(), WebActivity.class);
                            i.putExtra("loadUrl", url);
                            startActivity(i);
                            webview.stopLoading();
                            return;
                        }
                    });
                    webview.stopLoading();
                    return;
                }
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				LogUtil.i(TAG, "页面加载完后==onPageFinished==" + url);
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
		// webview.reload();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		((MainActivity) getActivity()).onActivityResult(requestCode,
				resultCode, data);
	}
}
