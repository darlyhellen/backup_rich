package com.ytdinfo.keephealth.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;
import com.youzan.sdk.Callback;
import com.youzan.sdk.YouzanSDK;
import com.youzan.sdk.YouzanUser;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.app.MyApp;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.clinic.ClinicWebView;
import com.ytdinfo.keephealth.ui.login.LoginActivity;
import com.ytdinfo.keephealth.ui.report.ChooseReportActivity;
import com.ytdinfo.keephealth.ui.uzanstore.WebActivity;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;
import com.ytdinfo.keephealth.ui.view.HomeItem_View_V21;
import com.ytdinfo.keephealth.ui.view.MyAdGallery;
import com.ytdinfo.keephealth.ui.view.MyAdGallery.MyOnItemClickListener;
import com.ytdinfo.keephealth.ui.view.MyProgressDialog;
import com.ytdinfo.keephealth.ui.view.OnScrollChangedListener;
import com.ytdinfo.keephealth.ui.view.ScrollHeaderView;
import com.ytdinfo.keephealth.utils.ImageTools;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.NetworkReachabilityUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;

/**
 * @author zhangyh2 HomeFragmentV21 下午3:25:23 TODO 所有的字体大小需要调整
 */
public class HomeFragmentV21 extends Fragment implements OnClickListener,
		MyOnItemClickListener {
	Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			gallery.start(getActivity(), listImageUrl, listImagePath, 3000,
					ovalLayout, R.drawable.point_bright, R.drawable.point_light);
		};
	};

	private int width;
	private int height;
	public static final String TAG = "HomeFragment";

	private int currentIndex = 0;// 当前页面,默认首页
	// private List<Map<String, Object>> listData;
	private List<String> listImagePath;
	private List<String> listImageUrl;
	private List<String> listUrl;
	private MyAdGallery gallery;
	private int[] imageId = new int[] { R.drawable.banner, R.drawable.banner };
	LinearLayout ovalLayout;

	private CommonActivityTopView mainTitle;

	/**
	 * 下午3:17:20 TODO 重写的下拉刷新
	 */
	// private PullToRefreshScrollView scrollPull;

	private ScrollHeaderView scroll;

	private LinearLayout show1;

	/**
	 * 上午11:08:24 TODO 体检商城
	 */
	private ImageView store;
	private MyProgressDialog synuser;
	/**
	 * 上午11:08:24 TODO 体检预约
	 */
	private HomeItem_View_V21 subscribe;
	/**
	 * 上午11:08:24 TODO 报告查询
	 */
	private HomeItem_View_V21 inquire;
	/**
	 * 上午11:08:24 TODO 报告解读
	 */
	private HomeItem_View_V21 reportunscramble;
	private LinearLayout show2;

	/**
	 * 上午11:08:24 TODO 帮忙医诊所
	 */
	private ImageView clinic;
	/**
	 * 上午11:08:24 TODO 预约挂号
	 */
	private HomeItem_View_V21 registration;
	/**
	 * 上午11:08:24 TODO 专家排班
	 */
	private HomeItem_View_V21 scheduling;

	// 综合服务
	/**
	 * 上午11:08:24 TODO 胶囊胃镜
	 */
	private ImageView gastroscope;
	/**
	 * 上午11:08:24 TODO aha
	 */
	private ImageView aha;
	/**
	 * 上午11:08:24 TODO 齿科
	 */
	private ImageView dentistry;
	/**
	 * 上午11:08:24 TODO 复健理疗
	 */
	private ImageView physiotherapy;

	private FragmentManager fragmentManager;
	private HotDiscussFragment fragment;
	private HotNewsFragment hotNewsFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.home_fragment_v21, container,
				false);// 关联布局文件
		// 获取屏幕宽高
		WindowManager wm = (WindowManager) getActivity().getSystemService(
				Context.WINDOW_SERVICE);
		width = wm.getDefaultDisplay().getWidth();
		height = wm.getDefaultDisplay().getHeight();
		initView(rootView);
		initListener();

		listImagePath = new ArrayList<String>();
		listImageUrl = new ArrayList<String>();
		listUrl = new ArrayList<String>();
		if (!SharedPrefsUtil.getValue(Constants.ISLOADED, false)) {
			initViewPagerData();
			SharedPrefsUtil.putValue(Constants.ISLOADED, true);
		} else {
			// 加载本地的
			loadNative();
		}
		return rootView;

	}

	private void initView(View v) {
		mainTitle = (CommonActivityTopView) v.findViewById(R.id.v21_home_title);
		mainTitle.tv_title.setText("帮忙医");
		mainTitle.ibt_back.setVisibility(View.INVISIBLE);
		mainTitle.setAlpha(0.4f);
		// scrollPull = (PullToRefreshScrollView) v
		// .findViewById(R.id.v21_home_pullscroll);
		scroll = (ScrollHeaderView) v.findViewById(R.id.v21_home_scroll);
		ovalLayout = (LinearLayout) v.findViewById(R.id.v21_ovalLayout);
		gallery = (MyAdGallery) v.findViewById(R.id.id_v21_adv);

		show1 = (LinearLayout) v.findViewById(R.id.v21_home_banner_1);
		store = (ImageView) v.findViewById(R.id.v21_home_item_store);
		subscribe = (HomeItem_View_V21) v.findViewById(R.id.v21_home_item_tjyy);
		inquire = (HomeItem_View_V21) v.findViewById(R.id.v21_home_item_bgcx);
		reportunscramble = (HomeItem_View_V21) v
				.findViewById(R.id.v21_home_item_bgjd);
		show2 = (LinearLayout) v.findViewById(R.id.v21_home_banner_2);
		clinic = (ImageView) v.findViewById(R.id.v21_home_item_zhensuo);
		registration = (HomeItem_View_V21) v
				.findViewById(R.id.v21_home_item_yygh);
		scheduling = (HomeItem_View_V21) v
				.findViewById(R.id.v21_home_item_zjpb);
		// 初始化以前样式控件
		// 综合服务
		gastroscope = (ImageView) v.findViewById(R.id.v21_zh_wjjn);
		aha = (ImageView) v.findViewById(R.id.v21_zh_aha);
		dentistry = (ImageView) v.findViewById(R.id.v21_zh_ck);
		physiotherapy = (ImageView) v.findViewById(R.id.v21_zh_kfll);
		// 商城初始化
		show1.setLayoutParams(new LayoutParams(width, width / 2));
		store.setLayoutParams(new LayoutParams(width / 2, width / 2));
		// 体检预约初始化
		subscribe.iv_icon.setImageResource(R.drawable.ic_v21_tijianyuyue);
		subscribe.tv_title.setText("体检预约");
		subscribe.tv_desc.setText("在线预约方便快捷");
		// subscribe.setLayoutParams(new LayoutParams(width, width / 6));
		// 报告查询初始化
		inquire.iv_icon.setImageResource(R.drawable.ic_v21_baogaochaxun);
		inquire.tv_title.setText("报告查询");
		inquire.tv_desc.setText("体检报告快速查询");
		// inquire.setLayoutParams(new LayoutParams(width, width / 6));
		// 报告解读初始化
		reportunscramble.iv_icon
				.setImageResource(R.drawable.ic_v21_baogaojiedu);
		reportunscramble.tv_title.setText("报告解读");
		reportunscramble.tv_desc.setText("医生在线免费解读");
		// reportunscramble.setLayoutParams(new LayoutParams(width, width / 6));
		// 帮忙医诊所初始化
		show2.setLayoutParams(new LayoutParams(width, (int) (width / (2 * 1.5))));
		clinic.setLayoutParams(new LayoutParams(width / 2,
				(int) (width / (2 * 1.5))));
		// 预约挂号初始化
		registration.iv_icon.setImageResource(R.drawable.ic_v21_yuyueguahao);
		registration.tv_title.setText("预约挂号");
		registration.tv_desc.setText("三甲名医免费预约");
		// registration.setLayoutParams(new LayoutParams(width, (int) (width
		// / (2 * 1.5) * 0.5)));
		// 专家排班初始化
		scheduling.iv_icon.setImageResource(R.drawable.ic_v21_zhuanjiapaiban);
		scheduling.tv_title.setText("专家排班");
		scheduling.tv_desc.setText("出诊时间一手掌握");
		// scheduling.setLayoutParams(new LayoutParams(width, (int) (width
		// / (2 * 1.5) * 0.5)));
		// 综合服务
		gastroscope.setLayoutParams(new LayoutParams(width * 2 / 5,
				(int) (width * 0.166)));
		aha.setLayoutParams(new LayoutParams(width * 3 / 5,
				(int) (width * 0.166)));
		dentistry.setLayoutParams(new LayoutParams(width * 2 / 5,
				(int) (width * 0.166)));
		physiotherapy.setLayoutParams(new LayoutParams(width * 3 / 5,
				(int) (width * 0.166)));
		initFragments(hotNewsFragment, HotNewsFragment.class,
				R.id.hot_news_area);
		initFragments(fragment, HotDiscussFragment.class, R.id.hot_discuss_area);

	}

	private void initFragments(Fragment mFragment, Class cls, int resId) {
		fragmentManager = getActivity().getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		if (mFragment == null) {
			try {
				mFragment = (Fragment) cls.newInstance();
				transaction.add(resId, mFragment);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			if (mFragment.isVisible())
				return;
			transaction.show(mFragment);
		}
		transaction.commit();
	}

	private void initListener() {
		// scrollPull.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
		// @Override
		// public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
		// new GetDataTask().execute();
		// }
		// });
		gallery.setMyOnItemClickListener(this);
		store.setOnClickListener(this);
		subscribe.setOnClickListener(this);
		inquire.setOnClickListener(this);
		reportunscramble.setOnClickListener(this);
		clinic.setOnClickListener(this);
		registration.setOnClickListener(this);
		scheduling.setOnClickListener(this);

		gastroscope.setOnClickListener(this);
		aha.setOnClickListener(this);
		dentistry.setOnClickListener(this);
		physiotherapy.setOnClickListener(this);

		// v21_main_information.setOnClickListener(this);
		scroll.setOnScrollChangedListener(new OnScrollChangedListener() {

			@Override
			public void onScrollChanged(ScrollHeaderView scrollView, int x,
					int y, int oldx, int oldy) {
				// TODO Auto-generated method stub
				if (mainTitle != null && mainTitle.getHeight() > 0) {
					int height = mainTitle.getHeight();

					if (y < height) {
						mainTitle.setAlpha((float) y / height + 0.4f);
					} else {
						mainTitle.setAlpha(1.0f);
					}
				}
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.v21_home_item_store:
			// v21 点击进入商城首页（正确）
			if (checkUser()) {
				registerYouzanUserForWeb();
			}
			break;
		case R.id.v21_home_item_tjyy:
			// 体检预约（正确）
			if (checkUser()) {
				Intent i = new Intent();
				i.setClass(getActivity(), WebViewActivity.class);
				i.putExtra("loadUrl", Constants.RESERVATION);
				startActivity(i);
			}
			break;
		case R.id.v21_home_item_bgcx:
			// 报告查询（正确）
			if (checkUser()) {
				Intent i = new Intent();
				i.setClass(getActivity(), WebViewActivity.class);
				i.putExtra("loadUrl", Constants.REPORTQUERY);
				startActivity(i);

			}
			break;
		case R.id.v21_home_item_bgjd:
			// 报告解读（正确）
			if (checkUser()) {
				MobclickAgent.onEvent(getActivity(), Constants.UMENG_EVENT_23);
				Intent intent = new Intent();
				intent.setClass(getActivity(), ChooseReportActivity.class);
				startActivity(intent);
			}
			break;

		case R.id.v21_home_item_zhensuo:
			// 同步用户，同步成功跳入诊所。失败则原地
			if (checkUser()) {
				// 跳转到主页面的
				((MainActivity) getActivity()).radioButtonClinic
						.setChecked(true);
			}
			break;
		case R.id.v21_home_item_yygh:
			if (checkUser()) {
				Intent intent = new Intent(getActivity(), ClinicWebView.class);
				intent.putExtra("loadUrl", Constants.DOCTORLIST);
				intent.putExtra("title", "预约挂号");
				startActivity(intent);
			}
			break;
		case R.id.v21_home_item_zjpb:
			// 专家排班
			if (checkUser()) {
				Intent intent = new Intent(getActivity(), ClinicWebView.class);
				intent.putExtra("loadUrl", Constants.ORDERLIST);
				intent.putExtra("title", "专家排班");
				startActivity(intent);
			}
			break;
		case R.id.v21_zh_wjjn:
			// 胶囊胃镜
			if (checkUser()) {
				Intent intent = new Intent(getActivity(), WebViewActivity.class);
				intent.putExtra("loadUrl", Constants.WEIJING);
				startActivity(intent);
			}
			break;
		case R.id.v21_zh_aha:
			// AHA
			if (checkUser()) {
				Intent intent = new Intent(getActivity(), WebViewActivity.class);
				intent.putExtra("loadUrl", Constants.AHA);
				startActivity(intent);
			}
			break;
		case R.id.v21_zh_ck:
			// 齿科
			if (checkUser()) {
				Intent intent = new Intent(getActivity(), WebViewActivity.class);
				intent.putExtra("loadUrl", Constants.KOUQIANG);
				startActivity(intent);
			}
			break;
		case R.id.v21_zh_kfll:
			// 康复理疗
			if (checkUser()) {
				Intent intent = new Intent(getActivity(), WebViewActivity.class);
				intent.putExtra("loadUrl", Constants.KANGFULILIAO);
				startActivity(intent);
			}
			break;
		/*
		 * case R.id.v21_main_information: Intent intent = new
		 * Intent(getActivity(), ClinicWebView.class);
		 * intent.putExtra("loadUrl", Constants.NEWSLISTS);
		 * intent.putExtra("title", "最新资讯"); startActivity(intent); break;
		 */
		default:
			break;
		}
	}

	private boolean checkUser() {
		// TODO Auto-generated method stub
		if (null != SharedPrefsUtil.getValue(Constants.TOKEN, null)) {
			return true;
		} else {
			Intent i11 = new Intent();
			i11.setClass(getActivity(), LoginActivity.class);
			startActivity(i11);
			return false;
		}

	}

	/**
	 * 上午10:46:12
	 * 
	 * @author zhangyh2 TODO 有赞直接注册窗口
	 */
	private void registerYouzanUserForWeb() {
		// TODO Auto-generated method stub
		synuser = new MyProgressDialog(getActivity());
		synuser.setMessage("加载中...");
		synuser.show();
		String jsonUserModel = SharedPrefsUtil
				.getValue(Constants.USERMODEL, "");
		UserModel userModel = new Gson().fromJson(jsonUserModel,
				UserModel.class);
		/**
		 * 打开有赞入口网页需先注册有赞用户
		 * 
		 * <pre>
		 * 如果你们App的用户这个时候还没有登录, 请先跳转你们的登录页面, 然后再回来同步用户信息
		 * 
		 * 或者参考{@link LoginWebActivity}
		 * </pre>
		 */
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
				Intent intent = new Intent(getActivity(), WebActivity.class);
				// 传入链接, 请修改成你们店铺的链接
				intent.putExtra("loadUrl",
						"https://wap.koudaitong.com/v2/showcase/homepage?alias=1e99alxjl");
				startActivity(intent);

			}
		});
	}

	public void initViewPagerData() {
		if (NetworkReachabilityUtil.isNetworkConnected(MyApp.getInstance())) {
			// 有网络，从服务器上更新数据
			requestBanner();
		} else {
			// 没有网络，加载本地的
			loadNative();
		}
	}

	private void loadNative() {
		// 没有网络，加载本地的
		String jsonStr = SharedPrefsUtil.getValue(Constants.VIEWPAGER, "");
		LogUtil.i("wpc", "没有网络1==" + jsonStr);
		if (!jsonStr.equals("")) {
			parseViewPager(jsonStr);
			String json = SharedPrefsUtil.getValue(
					Constants.VIEWPAGER_IMAGEPATH, "");
			LogUtil.i("wpc", "没有网络2==" + json);
			// json转为带泛型的list
			try {
				listImagePath = new Gson().fromJson(json,
						new TypeToken<List<String>>() {
						}.getType());
				if (listImagePath == null || listImagePath.size() == 0) {
					localLoad();
				} else {
					gallery.start(getActivity(), new ArrayList<String>(),
							listImagePath, 3000, ovalLayout,
							R.drawable.point_bright, R.drawable.point_light);
				}
			} catch (Exception e) {
				localLoad();
			}
		} else {
			localLoad();
		}

	}

	private void localLoad() {
		// 本地也为空
		LogUtil.i("wpc", "本地也为空");
		Bitmap bit = BitmapFactory.decodeResource(getResources(),
				R.drawable.private_doc);
		String path = Constants.IMAGES_DIR;
		String photoName = "banner_default";
		ImageTools.savePhotoToSDCard(bit, path, photoName);
		listImagePath = new ArrayList<String>();
		listUrl = new ArrayList<String>();
		listImagePath.add(path + photoName + ".png");
		listUrl.add(Constants.PRIVATEDOCTOR);
		gallery.start(getActivity(), new ArrayList<String>(), listImagePath,
				3000, ovalLayout, R.drawable.point_bright,
				R.drawable.point_light);
	}

	private void requestBanner() {
		RequestParams requestParams = new RequestParams();
		HttpClient.get(MyApp.getInstance(), Constants.BANNER, requestParams,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						LogUtil.i("wpc", arg0.result.toString());
						// 存到本地
						SharedPrefsUtil.putValue(Constants.VIEWPAGER,
								arg0.result.toString());
						parseViewPager(arg0.result.toString());
						loadImage();
						// updateViewList();
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						loadNative();

					}
				});
	}

	private void loadImage() {
		listImagePath.clear();
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < listImageUrl.size(); i++) {
					String path = Constants.IMAGES_DIR;
					String photoName = "banner_" + i;
					ImageTools.savePhotoToSDCard(listImageUrl.get(i), path,
							photoName);
					listImagePath.add(path + photoName + ".png");
				}
				SharedPrefsUtil.putValue(Constants.VIEWPAGER_IMAGEPATH,
						new Gson().toJson(listImagePath));
				handler.sendEmptyMessage(0x123);
			}
		}).start();

	}

	private void parseViewPager(String jsonStr) {
		try {
			listImageUrl.clear();
			listUrl.clear();
			JSONArray jsonArray = new JSONArray(jsonStr);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				String ImgUrl = jsonObject.getString("ImgUrl");
				String Url = jsonObject.getString("Url");
				LogUtil.i("wpc", "ImgUrl==" + ImgUrl + "\nUrl==" + Url);
				listImageUrl.add(ImgUrl);
				listUrl.add(Url);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.i("wpc--", "onCreate");
	}

	@Override
	public void onResume() {
		super.onResume();
		LogUtil.i("wpc--", "onResume");
		MobclickAgent.onPageStart("HomeFragment"); // 统计页面
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtil.i("wpc--", "onPause");
		MobclickAgent.onPageEnd("HomeFragment");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		LogUtil.i("wpc--", "onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LogUtil.i("wpc--", "onDestroy");
	}

	// 滚动画面的点击事件
	@Override
	public void onItemClick(int curIndex) {
		// 私人医生
		LogUtil.i("wpc", "onItemClick---3");
		if (checkUser()) {
			Intent i = new Intent();
			i.setClass(getActivity(), WebViewActivity.class);
			i.putExtra("loadUrl", listUrl.get(curIndex));
			startActivity(i);
		}
	}

	// private class GetDataTask extends AsyncTask<Void, Void, String[]> {
	//
	// @Override
	// protected String[] doInBackground(Void... params) {
	// // Simulates a background job.
	// try {
	// initViewPagerData();
	// fragment.sync();
	// hotNewsFragment.sync();
	// Thread.sleep(1500);
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(String[] result) {
	// // Do some stuff here
	// // Call onRefreshComplete when the list has been refreshed.
	// scrollPull.onRefreshComplete();
	// super.onPostExecute(result);
	// }
	// }
}