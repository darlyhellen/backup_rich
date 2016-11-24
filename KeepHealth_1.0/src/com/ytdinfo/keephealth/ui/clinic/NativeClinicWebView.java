package com.ytdinfo.keephealth.ui.clinic;

import java.util.HashMap;
import java.util.List;

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
import android.location.Location;
import android.location.LocationListener;
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
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
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
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.sdkmanager.LocationSDKManager;
import com.umeng.socialize.UMShareAPI;
import com.youzan.sdk.YouzanSDK;
import com.youzan.sdk.YouzanUser;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.WebViewActivity;
import com.ytdinfo.keephealth.ui.login.LoginActivity;
import com.ytdinfo.keephealth.ui.uzanstore.WebActivity;
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

/**
 * @author zhangyh2
 *         NativeClinicWebView
 *         上午9:57:50
 *         TODO 带原生标题栏
 */
@SuppressLint({"JavascriptInterface", "NewApi"})
public class NativeClinicWebView extends BaseActivity implements WXCallBack {
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

    public CustomShareBoard shareBoard;

    private MyPopWindow mypop;
    private PopupWindow pop;
    private MyProgressDialog myProgressDialog, synuser;
    private String image_path;
    private Bitmap feedback;// 需要上传的Bitmap

    // private UMSocialService mController = UMServiceFactory
    // .getUMSocialService(Constants.DESCRIPTOR);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic);

        mainTitle = (CommonActivityTopView) findViewById(R.id.v21_clinic_title);

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
    private boolean islogin = false;

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
    @SuppressLint({"NewApi", "SetJavaScriptEnabled"})
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
        // 广播没有加载注册完成引起崩溃
        // webSettings.setBuiltInZoomControls(true);
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
                if (title != null && title.length() < 30) {
                    mainTitle.tv_title.setText(title);
                }
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           Callback callback) {
                // TODO Auto-generated method stub
                // if (isGpsEnable()) {
                callback.invoke(origin, true, false);
                // } else {
                // // 打开GPS
                // isOpenGps();
                // }
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
				 * Toast.LENGTH_SHORT); //
				 */
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri
                            .parse(url));
                    startActivity(intent);
                    view.reload();
                    return true;
                }
                return false;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onLoadResource(view, url);
            }

            @Override
            public void onPageStarted(WebView view, final String url, Bitmap favicon) {
                LogUtil.i(TAG, "拦截url---onPageStarted-->" + url);
                if (url.toLowerCase().contains("/login")) {
                    Intent i11 = new Intent();
                    i11.setClass(NativeClinicWebView.this, LoginActivity.class);
                    startActivity(i11);
                }
                if (url.contains(Constants.ROOT_WEB)) {
                    Intent intent = new Intent(NativeClinicWebView.this,
                            ClinicWebView.class);
                    intent.putExtra("loadUrl", url);
                    startActivity(intent);
                    webview.stopLoading();
                    return;
                }
                if (url.contains("koudaitong.com")) {
                    synuser = new MyProgressDialog(NativeClinicWebView.this);
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
                    YouzanSDK.asyncRegisterUser(user, new com.youzan.sdk.Callback() {
                        @Override
                        public void onCallback() {
                            synuser.dismiss();
                            Intent i = new Intent();
                            i.setClass(NativeClinicWebView.this, WebActivity.class);
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
                LogUtil.i(TAG, "页面加载完后==onPageFinished==" + url + "---");
                //为V3.1提供一个单独接口兼容低版本页面，功能，展示有赞商城入口。
                webview.loadUrl("javascript:ShowYZMallPort()");

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
        shareBoard = new CustomShareBoard(NativeClinicWebView.this);
        shareBoard.setWXCallBack(NativeClinicWebView.this);
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

        LogUtils.i("调用图片上传" + pop);
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
        }

        ;
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

    /**
     * 下午4:16:35
     *
     * @author zhangyh2 TODO 测试方案，后期无用
     */
    @JavascriptInterface
    public void findLocationByGD() {
        // androidLoacal();
        localForautoNav();
    }

    // ------高德定位前期使用版本过低，没有定位失败回调方案。无法正常使用故而替换为手机自身定位方案。

    private void localForautoNav() {
        LogUtils.i("吊起定位方法findLocationByGD");
        LocationSDKManager.getInstance().getCurrentSDK()
                .requestLocation(this, new SimpleFetchListener<Location>() {

                    @Override
                    public void onComplete(final Location arg0) {
                        LogUtils.i(arg0.getLatitude() + "高德地图定位"
                                + arg0.getLongitude());
                        // ？如何 判断定位失败
                        if (arg0.getLatitude() != 0 && arg0.getLongitude() != 0) {
                            setLocation(arg0);
                            LocationSDKManager.getInstance().getCurrentSDK()
                                    .onPause();
                        } else {
                            // 没有拿到数据调取胡接口
                            setNoLocation();
                        }

                    }

                });
    }

    // ------依靠手机自身定位方案进行解决，但公司要求精准定位，故而进行舍弃。替换为高德定位。

    private String locationProvider;
    private LocationManager locationManager;

    private void androidLoacal() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            // 如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            // 如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            setNoLocation();
            return;
        }

        // 获取Location
        Location location = locationManager
                .getLastKnownLocation(locationProvider);
        if (location != null) {
            // 不为空,显示地理位置经纬度
            LogUtils.i("获取" + location.getLatitude() + location.getLongitude());
            setLocation(location);
        } else {
            setNoLocation();
        }
        // 监视地理位置变化
        locationManager.requestLocationUpdates(locationProvider, 3000, 1,
                locationListener);

    }

    /**
     * 下午2:56:12
     *
     * @author zhangyh2 TODO 获取经纬度，调取页面
     */
    private void setLocation(final Location location) {
        runOnUiThread(new Runnable() {
            public void run() {
                webview.loadUrl("javascript:getLocationbyNative("
                        + location.getLatitude() + ","
                        + location.getLongitude() + ")");
            }
        });
    }

    private void setNoLocation() {
        runOnUiThread(new Runnable() {
            public void run() {
                webview.loadUrl("javascript:NativeNoGPS()");
            }
        });
    }

    /**
     * LocationListern监听器 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
     */

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            // 如果位置发生变化,重新显示
            if (location != null) {
                LogUtils.i("变化获取" + location.getLatitude()
                        + location.getLongitude());
                setLocation(location);
            }
            if (locationManager != null) {
                locationManager.removeUpdates(locationListener);
            }

        }
    };

}
