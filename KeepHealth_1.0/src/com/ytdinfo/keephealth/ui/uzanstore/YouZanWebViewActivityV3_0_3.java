package com.ytdinfo.keephealth.ui.uzanstore;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.umeng.analytics.MobclickAgent;
import com.youzan.sdk.YouzanSDK;
import com.youzan.sdk.YouzanUser;
import com.youzan.sdk.http.engine.OnRegister;
import com.youzan.sdk.http.engine.QueryError;
import com.youzan.sdk.model.goods.GoodsShareModel;
import com.youzan.sdk.web.bridge.IBridgeEnv;
import com.youzan.sdk.web.event.ShareDataEvent;
import com.youzan.sdk.web.event.UserInfoEvent;
import com.youzan.sdk.web.plugin.YouzanBrowser;
import com.youzan.sdk.web.plugin.YouzanChromeClient;
import com.youzan.sdk.web.plugin.YouzanWebClient;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.view.MyProgressDialog;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;

/**
 * @author zhangyh2 YouZanWebViewActivityV3_0_3 下午5:09:41 TODO同步调用. 可直接打开网页,
 *         但需订阅用户信息同步事件{@link UserInfoEvent}.
 */
public class YouZanWebViewActivityV3_0_3 extends BaseActivity implements
		OnClickListener {
	private String TAG = getClass().getSimpleName();

	private YouzanBrowser browser;

	private ImageButton back;

	private TextView title;

	private ImageButton other;

	private boolean isPageLoaded = false;

	private boolean isFirstPage = false;

	private MyProgressDialog loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_youzan_webview3_0_3);
		initWebView();

		Intent intent = getIntent();
		String url = intent.getStringExtra("loadUrl");

		if (!TextUtils.isEmpty(url)) {
			invokeAsyncRegister(url);
		} else {
			// 打开店铺链接等
			// TODO-WARNING: 请修改成你们店铺的链接
			invokeAsyncRegister(Constants.SHANGCHENG);
		}
	}

	private void initWebView() {
		browser = (YouzanBrowser) findViewById(R.id.id_youzan_browser);
		back = (ImageButton) findViewById(R.id.id_youzan_back);
		title = (TextView) findViewById(R.id.id_youzan_title);
		other = (ImageButton) findViewById(R.id.id_youzan_save);
		title.setText("帮忙医商城");
		loading = new MyProgressDialog(this);
		loading.setMessage("加载中...");
		back.setOnClickListener(this);
		other.setOnClickListener(this);
		// 订阅分享回调
//		browser.subscribe(new ShareDataEvent() {
//			@Override
//			public void call(IBridgeEnv iBridgeEnv, GoodsShareModel data) {
//				String content = data.getDesc() + data.getLink();
//				Intent sendIntent = new Intent();
//				sendIntent.setAction(Intent.ACTION_SEND);
//				sendIntent.putExtra(Intent.EXTRA_TEXT, content);
//				sendIntent.putExtra(Intent.EXTRA_SUBJECT, data.getTitle());
//				sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				sendIntent.setType("text/plain");
//				startActivity(sendIntent);
//			}
//		});
		// 上传文件的回调
//		browser.setOnChooseFileCallback(new YouzanBrowser.OnChooseFile() {
//			@Override
//			public void onWebViewChooseFile(Intent intent, int i)
//					throws ActivityNotFoundException {
//				startActivityForResult(intent, i);
//			}
//		});
		browser.setWebChromeClient(new ChromeClient());
		browser.setWebViewClient(new WebClient());
	}

	/**
	 * 上午10:15:39
	 * 
	 * @author zhangyh2 TODO用户异步登录直接打开页面，对用户进行同步。
	 * @param urld
	 */
	private void invokeAsyncRegister(final String urld) {
		if (loading != null && !loading.isShowing()) {
			loading.show();
		}
		String jsonUserModel = SharedPrefsUtil
				.getValue(Constants.USERMODEL, "");
		UserModel userModel = new Gson().fromJson(jsonUserModel,
				UserModel.class);
		YouzanUser user = new YouzanUser();
		user.setUserId(userModel.getPid() + "");// 用户唯一性ID, 你可以使用用户的ID等标识
		int sex = 0;
		if ("Man".endsWith(userModel.getUserSex())) {
			sex = 1;
		}
		user.setGender(sex);// "1"表示男性, "0"表示女性
		user.setNickName(userModel.getAddition1());// 昵称, 会显示在有赞商家版后台
		user.setTelephone(userModel.getMobilephone());// 手机号
		user.setUserName(userModel.getUserName());// 用户名
		// ...其他参数说明请看API文档{@link }

		YouzanSDK.asyncRegisterUser(user, new OnRegister() {
			@Override
			public void onFailed(QueryError queryError) {
				// 用户同步失败。则整个页面返回
				loading.dismiss();
				onBackPressed();
			}

			@Override
			public void onSuccess() {
				// 同步成功。进行后续操作
				loading.dismiss();
				browser.loadUrl(urld);
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		browser.isReceiveFileForWebView(requestCode, data);
		// 处理WebView上传文件, 就上面一句就行了
	}

	/**
	 * 页面回退 YouzanBrowser.pageGoBack()返回True表示处理的是网页的回退
	 */
	@Override
	public void onBackPressed() {
//		if (!browser.pageGoBack()) {
//			super.onBackPressed();
//		}
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
			YouZanWebViewActivityV3_0_3.this.title.setText(title);
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
				invokeAsyncRegister(url);
			}
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

			browser.clearView();
			if (failingUrl.contains("#")) {
				String[] temp;
				temp = failingUrl.split("#");
				browser.loadUrl(temp[0]);
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				browser.loadUrl(failingUrl);
			} else {
				browser.loadUrl("file:///android_asset/uzan_error.html#"
						+ failingUrl);
			}
			ToastUtil.showMessage("页面加载失败，请点击重新加载");
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			LogUtil.i(TAG, "页面加载完后==onPageFinished==" + url);
			title.setText(browser.getTitle());
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
		}
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
		case R.id.id_youzan_back:
			if (isFirstPage) {
				finish();
			} else {
				if (browser.canGoBack()) {
					if (isPageLoaded) {
						reloadUrl(Constants.SHANGCHENG);
					} else {
						onBackPressed();
					}
					browser.goBack();
				} else {
					finish();
				}
			}
			break;
		case R.id.id_youzan_save:
//			browser.sharePage();// 触发分享
			break;
		default:
			break;
		}
	}

	/**
	 * JS调用的方法
	 */
	@JavascriptInterface
	public void reloadUrl(String url) {
		Log.i(TAG, "reloadUrl()");
		browser.loadUrl(url);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isFirstPage) {
				finish();
			} else {
				if (browser.canGoBack()) {
					if (isPageLoaded) {
						reloadUrl(Constants.SHANGCHENG);
					} else {
						onBackPressed();
					}
					browser.goBack();
				} else {
					finish();
				}
			}
		}
		return false;
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("YouZanWebViewActivityV3_0_3");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		MobclickAgent.onPageEnd("YouZanWebViewActivityV3_0_3");
		MobclickAgent.onPause(this);
	}
}
