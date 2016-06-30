package com.ytdinfo.keephealth.ui.uzanstore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgentJSInterface;
import com.umeng.socialize.UMShareAPI;
import com.youzan.sdk.Callback;
import com.youzan.sdk.YouzanBridge;
import com.youzan.sdk.YouzanSDK;
import com.youzan.sdk.YouzanUser;
import com.youzan.sdk.model.goods.GoodsShareModel;
import com.youzan.sdk.web.bridge.IBridgeEnv;
import com.youzan.sdk.web.event.ShareDataEvent;
import com.youzan.sdk.web.event.UserInfoEvent;
import com.youzan.sdk.web.plugin.YouzanChromeClient;
import com.youzan.sdk.web.plugin.YouzanWebClient;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.view.MyProgressDialog;
import com.ytdinfo.keephealth.ui.view.MyWebView;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;
import com.ytdinfo.keephealth.wxapi.CustomShareBoard;
import com.ytdinfo.keephealth.wxapi.WXCallBack;

@SuppressLint("JavascriptInterface")
public class WebActivity extends BaseActivity implements OnClickListener,
		WXCallBack {
	private final String TAG = "WebViewActivity";
	private MyWebView webview;
	private String loadUrl = "https://wap.koudaitong.com/v2/showcase/homepage?alias=1e99alxjl";

	/**
	 * 上午10:17:06 TODO 分享的URL
	 */
	private String shareUrl = "";

	private ImageButton back;

	private TextView title;

	private ImageButton other;

	private String current_url;

	private boolean isPageLoaded = false;

	private boolean isFirstPage = false;

	private MyProgressDialog myProgressDialog2;

	/**
	 * 上午10:50:02 TODO 分享
	 */
	public CustomShareBoard shareBoard;

	/**
	 * H5和原生的桥接对象
	 */
	private YouzanBridge bridge;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);

		back = (ImageButton) findViewById(R.id.id_webview_back);
		title = (TextView) findViewById(R.id.id_webview_title);
		other = (ImageButton) findViewById(R.id.id_webview_save);

		// 获取传递过来的URL;
		if (getIntent() != null) {
			loadUrl = getIntent().getStringExtra("loadUrl");
		}
		title.setText("帮忙医商城");
		myProgressDialog2 = new MyProgressDialog(this);
		myProgressDialog2.setMessage("加载中...");
		myProgressDialog2.show();
		loadWebView();
		initBridge();
		openWebview();
		webViewListener();
		initShare();

	}

	/**
	 * 上午10:50:48
	 * 
	 * @author zhangyh2 TODO 设置分享
	 */
	private void initShare() {
		// TODO Auto-generated method stub
		shareBoard = new CustomShareBoard(this);
		shareBoard.setWXCallBack(this);
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
		webview = (MyWebView) findViewById(R.id.id_webview_view);
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

		webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

		// js调用安卓方法
		webview.addJavascriptInterface(this, "RedirectListner");
	}

	/**
	 * 初始化桥接对象.
	 * 
	 * <pre>
	 * 可使用的扩展有:
	 *      {@link UserInfoEvent}
	 *          用户同步登录,使用{@link YouzanSDK#asyncRegisterUser}完成信息注册的可以忽略这个扩展;
	 *          具体参考{@link LoginWebActivity}
	 * 
	 *      {@link ShareDataEvent}
	 *          获取分享数据
	 *          具体参考{@link ShareEvent}
	 * 
	 * ...
	 * </pre>
	 */
	private void initBridge() {
		bridge = YouzanBridge.build(this, webview)
				.setWebClient(new WebClient())
				.setChromeClient(new ChromeClient()).create();
		bridge.hideTopbar(true);// 隐藏顶部店铺信息栏
		// 根据需求添加相应的桥接事件
		bridge.add(new ShareEvent());// 分享
	}

	/**
	 * 打开链接
	 */
	private void openWebview() {
		if (webview != null && !TextUtils.isEmpty(loadUrl)) {
			webview.loadUrl(loadUrl);
		}
	}

	/**
	 * 自定义ChromeClient 必须继承自{@link YouzanWebClient}
	 */
	private class ChromeClient extends YouzanChromeClient {

		@Override
		public void onReceivedTitle(WebView view, String title) {
			super.onReceivedTitle(view, title);
			// 这里获取到WebView的标题
			LogUtils.i("onReceivedTitle--->" + title);
			WebActivity.this.title.setText(title);
		}
	}

	/**
	 * 自定义WebClient 必须继承自{@link YouzanWebClient}
	 */
	private class WebClient extends YouzanWebClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// 用户登录超时，截获字段进行重新同步登录
			if (url.contains("buyer/kdtunion")) {
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
						webview.loadUrl(current_url);
					}
				});
			} else {
				current_url = url;
			}
			LogUtils.i("shouldOverrideUrlLoading--->" + url
					+ " current_url---->" + current_url);
			if (super.shouldOverrideUrlLoading(view, url)) {
				return true;
			}
			return false;// 或者做其他操作
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.webkit.WebViewClient#onReceivedError(android.webkit.WebView,
		 * int, java.lang.String, java.lang.String)
		 */
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			LogUtil.i(TAG, "加载失败===" + errorCode + "---" + description + "---"
					+ failingUrl + "---");

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
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			LogUtil.i(TAG, "页面加载完后==onPageFinished==" + url);
			if (url.contains("/goods")) {
				other.setVisibility(View.INVISIBLE);
			} else {
				other.setVisibility(View.INVISIBLE);
			}

			if (url.contains("https://wap.koudaitong.com/v2/showcase/homepage")
					|| url.contains("https://wap.koudaitong.com/v2/home/")) {
				isFirstPage = true;
			} else {
				isFirstPage = false;
			}

			// 判断加载完成的是不是会员中心和购物车页面

			if (url.contains("usercenter")) {
				isPageLoaded = true;
			} else {
				isPageLoaded = false;
			}
			myProgressDialog2.dismiss();

		}
	}

	/**
	 * @author zhangyh2 ShareEvent 上午10:09:50 TODO 分享
	 */
	public class ShareEvent extends ShareDataEvent {
		/**
		 * 回传分享数据, 再调用组件进行分享
		 * 
		 * @param env
		 *            一些上下文环境
		 * @param data
		 *            分享数据
		 */
		@Override
		public void call(IBridgeEnv env, GoodsShareModel data) {
			// new AlertDialog.Builder(env.getActivity())
			// .setTitle(data.getTitle())
			// .setMessage(data.getDesc() + "\n\n" + data.getLink())
			// .create().show();

			shareBoard.setShareContent(data.getTitle(), data.getImgUrl(), shareUrl,
					data.getDesc());
			// 设置分享内容
			// setShareContent(data.getTitle(), data.getImgUrl(), shareUrl,
			// data.getDesc());
			postShare();
		}
	}

	/**
	 * 调用postShare分享。跳转至分享编辑页，然后再分享。</br> [注意]<li>
	 * 对于新浪，豆瓣，人人，腾讯微博跳转到分享编辑页，其他平台直接跳转到对应的客户端
	 */
	private void postShare() {
		shareBoard.showAtLocation(getWindow().getDecorView(), Gravity.BOTTOM,
				0, 0);
	}

	/**
	 * 根据不同的平台设置不同的分享内容</br>
	 */
	private void setShareContent(String titleName, String thumbUrl, String url,
			String siteDesc) {
		Log.i("setShareContent", titleName + "======" + siteDesc + "======="
				+ url + "========" + thumbUrl);
		
//		// 微信分享
//		WeiXinShareContent weixinContent = new WeiXinShareContent();
//		weixinContent.setShareContent(siteDesc);
//		weixinContent.setTitle(titleName);
//		weixinContent.setTargetUrl(url);
//		UMImage urlImage = new UMImage(this, thumbUrl);
//		weixinContent.setShareMedia(urlImage);
//		mController.setShareMedia(weixinContent);
//
//		// 设置微信圈分享的内容
//		CircleShareContent circleMedia = new CircleShareContent();
//		circleMedia.setShareContent(siteDesc);
//		circleMedia.setTitle(titleName);
//		circleMedia.setTargetUrl(url);
//		circleMedia.setShareMedia(urlImage);
//		mController.setShareMedia(circleMedia);
	}

	/**
	 * 页面回退 bridge.pageGoBack()返回True表示处理的是网页的回退
	 */
	@Override
	public void onBackPressed() {
		if (bridge == null || !bridge.pageGoBack()) {
			super.onBackPressed();
		}
	}

	private void webViewListener() {
		back.setOnClickListener(this);
		other.setOnClickListener(this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			LogUtil.i(TAG, "==onKeyDown==");

			if (isFirstPage) {
				finish();
			} else {
				if (webview.canGoBack()) {
					if (isPageLoaded) {
						reloadUrl(loadUrl);
					} else {
						bridge.pageGoBack();
					}
				} else {
					finish();
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.id_webview_back:
			if (isFirstPage) {
				finish();
			} else {
				if (webview.canGoBack()) {
					if (isPageLoaded) {
						reloadUrl(loadUrl);
					} else {
						bridge.pageGoBack();
					}
				} else {
					finish();
				}

			}
			break;
		case R.id.id_webview_save:
			bridge.sharePage();
			break;
		default:
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("WebActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		MobclickAgent.onPageEnd("WebActivity");
		MobclickAgent.onPause(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytdinfo.keephealth.wxapi.WXCallBack#shareComplete(boolean)
	 */
	@Override
	public void shareComplete(boolean flag) {
		// TODO Auto-generated method stub
		if (flag) {
			LogUtils.i("分享成功");
		} else {
			LogUtils.i("分享失败");
		}
	}

	/**
	 * JS调用的方法
	 */
	@JavascriptInterface
	public void reloadUrl(String url) {
		Log.i(TAG, "reloadUrl()");
		webview.loadUrl(url);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
	}
}
