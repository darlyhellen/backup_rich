package com.ytdinfo.keephealth.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.app.MyApp;
import com.ytdinfo.keephealth.ui.login.LoginActivity;
import com.ytdinfo.keephealth.ui.report.ChooseReportActivity;
import com.ytdinfo.keephealth.ui.view.MyAdGallery;
import com.ytdinfo.keephealth.ui.view.MyAdGallery.MyOnItemClickListener;
import com.ytdinfo.keephealth.utils.ImageTools;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.NetworkReachabilityUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.ytdinfo.keephealth.zhangyuhui.IAintelligentguidance;

public class HomeFragment extends Fragment implements OnClickListener,
		MyOnItemClickListener {
	Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			gallery.start(getActivity(), listImageUrl, listImagePath, 3000,
					ovalLayout, R.drawable.point_bright, R.drawable.point_light);
		};
	};
	public static final String TAG = "HomeFragment";
	private HomeItem_1View shouYeItem_2View_1;
	private HomeItem_1View shouYeItem_2View_2;

	private HomeItem_2View shouYeItem_1View_1;
	private HomeItem_2View shouYeItem_1View_2;
	private HomeItem_2View shouYeItem_1View_3;
	private HomeItem_2View shouYeItem_1View_4;

	// private ImageView iv_private_doc;

	private int currentIndex = 0;// 当前页面,默认首页
	// private List<Map<String, Object>> listData;
	private List<String> listImagePath;
	private List<String> listImageUrl;
	private List<String> listUrl;
	private MyAdGallery gallery;
	private int[] imageId = new int[] { R.drawable.banner, R.drawable.banner };
	LinearLayout ovalLayout;

	// ==原来的==
	// private ShouYeItem_1View shouYeItem_1View_1;
	// private ShouYeItem_1View shouYeItem_1View_2;
	// private ShouYeItem_1View shouYeItem_1View_3;
	// private ShouYeItem_1View shouYeItem_1View_4;
	//
	// private ShouYeItem_2View shouYeItem_2View_1;
	// private ShouYeItem_2View shouYeItem_2View_2;
	// private ShouYeItem_2View shouYeItem_2View_3;
	// private ShouYeItem_2View shouYeItem_2View_4;
	// private ShouYeItem_2View shouYeItem_2View_5;
	//
	// private RelativeLayout b1;
	// ==原来的==

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LogUtil.i("wpc--", "onCreateView");
		View rootView = inflater.inflate(R.layout.home_fragment, container,
				false);// 关联布局文件
		initView(rootView);
		initListener();

		listImagePath = new ArrayList<String>();
		listImageUrl = new ArrayList<String>();
		listUrl = new ArrayList<String>();
		if (!SharedPrefsUtil.getValue(Constants.ISLOADED, false)) {
			LogUtil.i(
					"wpc--",
					"Main---> "
							+ SharedPrefsUtil.getValue(Constants.ISLOADED,
									false) + "");

			initViewPagerData();
			SharedPrefsUtil.putValue(Constants.ISLOADED, true);

		} else {
			LogUtil.i(
					"wpc--",
					"---> "
							+ SharedPrefsUtil.getValue(Constants.ISLOADED,
									false) + "");
			// 加载本地的
			loadNative();
		}

		/*
		 * DbUtils db = DbUtils.create(getActivity()); ChatInfoBean chatInfoBean
		 * = new ChatInfoBean(); chatInfoBean.setStatus(true);
		 * chatInfoBean.setSubjectID("001"); try { db.save(chatInfoBean);
		 * ChatInfoBean chatInfoBean2
		 * =db.findFirst(Selector.from(ChatInfoBean.class
		 * ).where("status","=",true)); chatInfoBean2.setStatus(false);
		 * db.saveOrUpdate(chatInfoBean2); ChatInfoBean chatInfoBean3 = new
		 * ChatInfoBean(); chatInfoBean3.setStatus(true);
		 * chatInfoBean3.setSubjectID("002"); } catch (DbException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); }
		 */
		return rootView;

	}

	private void initView(View v) {
		ovalLayout = (LinearLayout) v.findViewById(R.id.ovalLayout);
		gallery = (MyAdGallery) v.findViewById(R.id.id_iv_private_doc);
		shouYeItem_2View_1 = (HomeItem_1View) v
				.findViewById(R.id.id_homeItem_1View_1);
		shouYeItem_2View_2 = (HomeItem_1View) v
				.findViewById(R.id.id_homeItem_1View_2);
		shouYeItem_1View_1 = (HomeItem_2View) v
				.findViewById(R.id.id_homeItem_2View_1);
		shouYeItem_1View_2 = (HomeItem_2View) v
				.findViewById(R.id.id_homeItem_2View_2);
		shouYeItem_1View_3 = (HomeItem_2View) v
				.findViewById(R.id.id_homeItem_2View_3);
		shouYeItem_1View_4 = (HomeItem_2View) v
				.findViewById(R.id.id_homeItem_2View_4);
		// iv_private_doc = (ImageView) v
		// .findViewById(R.id.id_iv_private_doc);
		// viewpager = (ViewPager) v
		// .findViewById(R.id.viewpager);

		shouYeItem_2View_1.iv_icon.setImageResource(R.drawable.ask_doc);
		shouYeItem_2View_1.tv_title.setText("报告解读");
		shouYeItem_2View_1.bt_mark
				.setBackgroundResource(R.drawable.bt_bg_green);
		shouYeItem_2View_1.bt_mark.setText("免费");
		shouYeItem_2View_1.tv_desc.setText("点击找医生快速解读");

		shouYeItem_2View_2.iv_icon.setImageResource(R.drawable.yuemingyi);
		shouYeItem_2View_2.tv_title.setText("约名医");
		shouYeItem_2View_2.tv_desc.setText("三甲名医帮你免费约");

		shouYeItem_1View_1.iv_icon.setImageResource(R.drawable.tijianyuyue);
		shouYeItem_1View_1.tv_title.setText("体检预约");

		shouYeItem_1View_2.iv_icon.setImageResource(R.drawable.zhinengdaojian);
		shouYeItem_1View_2.tv_title.setText("智能导检");

		shouYeItem_1View_3.iv_icon.setImageResource(R.drawable.baogaochaxun);
		shouYeItem_1View_3.tv_title.setText("报告查询");

		shouYeItem_1View_4.iv_icon.setImageResource(R.drawable.baogaojiedu);
		shouYeItem_1View_4.tv_title.setText("报告详情");
		// ==原来的==
		// shouYeItem_1View_1 = (ShouYeItem_1View) v
		// .findViewById(R.id.id_ShouYeItem_1View_1);
		// shouYeItem_1View_2 = (ShouYeItem_1View) v
		// .findViewById(R.id.id_ShouYeItem_1View_2);
		// shouYeItem_1View_3 = (ShouYeItem_1View) v
		// .findViewById(R.id.id_ShouYeItem_1View_3);
		// shouYeItem_1View_4 = (ShouYeItem_1View) v
		// .findViewById(R.id.id_ShouYeItem_1View_4);
		//
		// shouYeItem_2View_1 = (ShouYeItem_2View) v
		// .findViewById(R.id.id_ShouYeItem_2View_1);
		// shouYeItem_2View_2 = (ShouYeItem_2View) v
		// .findViewById(R.id.id_ShouYeItem_2View_2);
		// shouYeItem_2View_3 = (ShouYeItem_2View) v
		// .findViewById(R.id.id_ShouYeItem_2View_3);
		// shouYeItem_2View_4 = (ShouYeItem_2View) v
		// .findViewById(R.id.id_ShouYeItem_2View_4);
		// shouYeItem_2View_5 = (ShouYeItem_2View) v
		// .findViewById(R.id.id_ShouYeItem_2View_5);
		//
		// // 设置各个图标
		// shouYeItem_1View_1.setIcon(getResources().getDrawable(
		// R.drawable.tijianyuyue));
		// shouYeItem_1View_2.setIcon(getResources().getDrawable(
		// R.drawable.zhinengdaojian));
		// shouYeItem_1View_3.setIcon(getResources().getDrawable(
		// R.drawable.baogaochaxun));
		// shouYeItem_1View_4.setIcon(getResources().getDrawable(
		// R.drawable.baogaojiedu));
		//
		// shouYeItem_2View_1.setIcon(getResources().getDrawable(
		// R.drawable.zaixianwenzhen));
		// shouYeItem_2View_2.setIcon(getResources().getDrawable(
		// R.drawable.yuemingyi));
		// shouYeItem_2View_3.setIcon(getResources().getDrawable(
		// R.drawable.yundongkangfu));
		// shouYeItem_2View_4.setIcon(getResources().getDrawable(
		// R.drawable.jiatingyisheng));
		// shouYeItem_2View_5.setIcon(getResources().getDrawable(
		// R.drawable.sirenyisheng));
		// // 设置各个标题信息
		// shouYeItem_1View_1.setTitle("体检预约");
		// shouYeItem_1View_2.setTitle("智能导检");
		// shouYeItem_1View_3.setTitle("报告查询");
		// shouYeItem_1View_4.setTitle("报告解读");
		//
		// shouYeItem_2View_1.setTitle("在线问诊");
		// shouYeItem_2View_2.setTitle("约名医");
		// shouYeItem_2View_3.setTitle("运动康复");
		// shouYeItem_2View_4.setTitle("家庭医生");
		// shouYeItem_2View_5.setTitle("私人医生");
		// // 设置各个描述信息
		// shouYeItem_2View_1.setDesc("医生在线快速解答");
		// shouYeItem_2View_2.setDesc("三甲名医免费约");
		// shouYeItem_2View_3.setDesc("是运动，更是医疗");
		// shouYeItem_2View_4.setDesc("解决全家健康问题");
		// shouYeItem_2View_5.setDesc("随时随地一对一服务");
		//
		// b1 = (RelativeLayout) v.findViewById(R.id.b1);
		// ==原来的==
	}

	private void initListener() {
		gallery.setMyOnItemClickListener(this);
		shouYeItem_1View_1.setOnClickListener(this);
		shouYeItem_1View_2.setOnClickListener(this);
		shouYeItem_1View_3.setOnClickListener(this);
		shouYeItem_1View_4.setOnClickListener(this);
		shouYeItem_2View_1.setOnClickListener(this);
		shouYeItem_2View_2.setOnClickListener(this);
		// iv_private_doc.setOnClickListener(this);
		// shouYeItem_2View_3.setOnClickListener(this);
		// shouYeItem_2View_4.setOnClickListener(this);
		// shouYeItem_2View_5.setOnClickListener(this);
		// b1.setOnClickListener(this);

		/*
		 * shouYeItem_1View_4.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Intent intent = new Intent();
		 * intent.setClass(getActivity(), GroupMessageListActivity.class);
		 * startActivity(intent); } });
		 */
		/*
		 * shouYeItem_2View_1.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub Intent intent = new Intent(); intent.setClass(getActivity(),
		 * ChooseReportActivity.class); startActivity(intent); } });
		 * shouYeItem_2View_2.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub Intent intent = new Intent(); intent.setClass(getActivity(),
		 * OnlineQuesActivity.class); startActivity(intent); } });
		 */
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.id_homeItem_2View_1:

			Log.i(TAG, "login");

			if (checkUser()) {
				Intent i = new Intent();
				i.setClass(getActivity(), WebViewActivity.class);
				i.putExtra("loadUrl", Constants.RESERVATION);
				startActivity(i);
			}

			break;
		case R.id.id_homeItem_2View_2:
			if (checkUser()) {
				MobclickAgent.onEvent(getActivity(), Constants.UMENG_EVENT_6);

				startActivity(new Intent(getActivity(),
						IAintelligentguidance.class));
			}

			break;
		case R.id.id_homeItem_2View_3:
			Log.i(TAG, "webview");
			if (checkUser()) {
				Intent i = new Intent();
				i.setClass(getActivity(), WebViewActivity.class);
				i.putExtra("loadUrl", Constants.REPORTQUERY);
				startActivity(i);

			}
			break;
		case R.id.id_homeItem_2View_4:

			// //打开自定义的Activity（帮忙医小助手）（该界面显示所有的推送消息，从sqlite中提取
			// Intent i = new Intent(getActivity(), LittleHelperActivity.class);
			// // i.putExtras(bundle);
			// //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
			// Intent.FLAG_ACTIVITY_CLEAR_TOP ); startActivity(i);

			// 报告列表
			if (checkUser()) {
				Intent i = new Intent();
				i.setClass(getActivity(), WebViewActivity.class);
				i.putExtra("loadUrl", Constants.REPORT_LIST);
				startActivity(i);
			}

			/*
			 * Intent intent = new Intent(); intent.setClass(getActivity(),
			 * GroupMessageListActivity.class); startActivity(intent);
			 */
			break;
		case R.id.id_homeItem_1View_1:

			if (checkUser()) {
				MobclickAgent.onEvent(getActivity(), Constants.UMENG_EVENT_23);
				// ----------V3调整为在线问诊
				Intent intent = new Intent();
				intent.setClass(getActivity(), ChooseReportActivity.class);
				// intent.setClass(getActivity(),
				// OnlineQuesActivityForV3.class);
				startActivity(intent);
			}

			// 在线问诊

			break;
		case R.id.id_homeItem_1View_2:
			if (checkUser()) {
				Intent i22 = new Intent();
				i22.setClass(getActivity(), WebViewActivity.class);
				i22.putExtra("loadUrl", Constants.FAMOUSDOCTOR);
				startActivity(i22);
			}
			break;

		// case R.id.id_iv_private_doc:
		// //私人医生
		// if (checkUser()) {
		// Intent i25 = new Intent();
		// i25.setClass(getActivity(), WebViewActivity.class);
		// i25.putExtra("loadUrl", Constants.PRIVATEDOCTOR);
		// startActivity(i25);
		// }
		// break;
		// case R.id.id_ShouYeItem_2View_3:
		// if (checkUser()) {
		// Intent i23 = new Intent();
		// i23.setClass(getActivity(), WebViewActivity.class);
		// i23.putExtra("loadUrl", Constants.SPORTS);
		// startActivity(i23);
		// }
		// break;
		// case R.id.id_ShouYeItem_2View_4:
		// if (checkUser()) {
		// Intent i24 = new Intent();
		// i24.setClass(getActivity(), WebViewActivity.class);
		// i24.putExtra("loadUrl", Constants.FAMILYDOCTOR);
		// startActivity(i24);
		// }
		// break;
		// case R.id.id_ShouYeItem_2View_5:
		// if (checkUser()) {
		// Intent i25 = new Intent();
		// i25.setClass(getActivity(), WebViewActivity.class);
		// i25.putExtra("loadUrl", Constants.PRIVATEDOCTOR);
		// startActivity(i25);
		// }
		// break;
		//
		// case R.id.b1:
		// // 瑞慈体检天猫商城
		// Log.i(TAG, "tmall");
		// Intent i_tmall = new Intent();
		// i_tmall.setClass(getActivity(), TmallWebViewActivity.class);
		// i_tmall.putExtra("loadUrl", Constants.RUICI_TMALL_URl);
		// startActivity(i_tmall);
		// break;

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

	// private void updateViewList() {
	// viewList.clear();
	// // 用代码的动态添加View
	// //添加对应的view进入集合（数据源）
	// for(int i=0;i<listData.size();i++){
	// ImageView imageView=new ImageView(getActivity());
	// imageView.setLayoutParams(new
	// LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
	// imageView.setScaleType(ScaleType.CENTER);//设置缩放样式
	// // imageView.setImageResource(images[i]);
	// ImageLoader.getInstance().displayImage((String)
	// listData.get(i).get("ImgUrl"), imageView,ImageLoaderUtils.getOptions());
	// viewList.add(imageView);
	// }
	// // viewpager.setAdapter(new ImageAdapter(viewList));
	// LogUtil.i("wpc", viewList.size()+"");
	// }

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

}
