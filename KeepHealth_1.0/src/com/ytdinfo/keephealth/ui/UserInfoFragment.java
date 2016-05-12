package com.ytdinfo.keephealth.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.lidroid.xutils.util.LogUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.login.LoginActivity;
import com.ytdinfo.keephealth.ui.opinionfeedback.OpinionFeedbackActivity;
import com.ytdinfo.keephealth.ui.personaldata.PersonalDataActivity;
import com.ytdinfo.keephealth.ui.setting.SettingActivity;
import com.ytdinfo.keephealth.utils.ImageLoaderUtils;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;

public class UserInfoFragment extends Fragment {
	private String TAG = "UserInfoFragment";

	private UserInfoItem_2View userInfoItem_2View_1;
	private UserInfoItem_2View userInfoItem_2View_2;
	private UserInfoItem_2View userInfoItem_2View_3;
	private UserInfoItem_2View userInfoItem_2View_4;
	// ----------V3添加关于我们
	private UserInfoItem_2View userInfoItem_2View_5;

	private UserInfoItem_1View userInfoItem_1View;

	private DisplayImageOptions options;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.webview_fragment, container,
				false);// 关联布局文件
		// BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
		// BitmapHelp.getBitmapUtils(getActivity()).display(ysj,
		// "http://img4.duitang.com/uploads/item/201506/05/20150605140911_uNhQY.jpeg");
		initView(rootView);
		initListener();
		return rootView;

	}

	private void initView(View rootView) {
		userInfoItem_2View_1 = (UserInfoItem_2View) rootView
				.findViewById(R.id.id_UserInfoItem_2View_1);
		userInfoItem_2View_2 = (UserInfoItem_2View) rootView
				.findViewById(R.id.id_UserInfoItem_2View_2);
		userInfoItem_2View_3 = (UserInfoItem_2View) rootView
				.findViewById(R.id.id_UserInfoItem_2View_3);
		userInfoItem_2View_4 = (UserInfoItem_2View) rootView
				.findViewById(R.id.id_UserInfoItem_2View_4);
		userInfoItem_2View_5 = (UserInfoItem_2View) rootView
				.findViewById(R.id.id_UserInfoItem_2View_5);

		userInfoItem_1View = (UserInfoItem_1View) rootView
				.findViewById(R.id.id_UserInfoItem_1View);

		// 设置各个图标
		userInfoItem_2View_1.setIcon(getResources().getDrawable(
				R.drawable.ic_bmy_declare));
		userInfoItem_2View_2.setIcon(getResources().getDrawable(
				R.drawable.my_reservation));
		userInfoItem_2View_3.setIcon(getResources().getDrawable(
				R.drawable.opinion_feedback));
		userInfoItem_2View_4.setIcon(getResources().getDrawable(
				R.drawable.setting));
		userInfoItem_2View_5.setIcon(getResources().getDrawable(
				R.drawable.about_us_icon));
		// 设置各个标题
		userInfoItem_2View_1.setTitle("体检报告解读");
		userInfoItem_2View_2.setTitle("我的预约");
		userInfoItem_2View_3.setTitle("意见反馈");
		userInfoItem_2View_4.setTitle("设置");
		userInfoItem_2View_5.setTitle("关于我们");

	}

	private void initListener() {

		// 头像，个人资料
		userInfoItem_1View.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isLogined()) {
					Intent intent = new Intent();
					intent.setClass(getActivity(), PersonalDataActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setClass(getActivity(), LoginActivity.class);
					startActivity(intent);
				}
			}
		});
		// 健康档案
		userInfoItem_2View_1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ----------V3调整为体检报告解读页面
				// Intent intent = new Intent();
				// intent.setClass(getActivity(), ChooseReportActivity.class);
				// startActivity(intent);
				Log.i(TAG, "webview");
				Intent ii = new Intent();
				ii.setClass(getActivity(), WebViewActivity.class);
				ii.putExtra("loadUrl", Constants.HEALTHARCHIVE);
				startActivity(ii);
			}
		});

		// 我的预约
		userInfoItem_2View_2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "webview");
				Intent i = new Intent();
				i.setClass(getActivity(), WebViewActivity.class);
				i.putExtra("loadUrl", Constants.RESERVATIONS);
				startActivity(i);

			}
		});
		// 意见反馈
		userInfoItem_2View_3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isLogined()) {
					Intent intent = new Intent();
					intent.setClass(getActivity(),
							OpinionFeedbackActivity.class);
					startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setClass(getActivity(), LoginActivity.class);
					startActivity(intent);
				}
			}
		});
		userInfoItem_2View_4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), SettingActivity.class);
				startActivity(intent);
			}
		});

		userInfoItem_2View_5.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.i(TAG, "webview");
				Intent i = new Intent();
				i.setClass(getActivity(), WebViewActivity.class);
				i.putExtra("loadUrl", Constants.ABOUTUS);
				startActivity(i);
			}
		});

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		MobclickAgent.onPageStart("UserInfoFragment"); // 统计页面

		LogUtil.i(TAG, "onresume");

		String jsonUserModel = SharedPrefsUtil
				.getValue(Constants.USERMODEL, "");
		UserModel userModel = new Gson().fromJson(jsonUserModel,
				UserModel.class);
		if (userModel == null) {
			return;
		}
		// 已登录，显示信息
		// 头像
		// BitmapUtils bitmapUtils = new BitmapUtils(getActivity());
		// bitmapUtils.display(ysj,
		// "http://img4.duitang.com/uploads/item/201506/05/20150605140911_uNhQY.jpeg");
		// BitmapHelp.getBitmapUtils(getActivity()).display(ysj,
		// "http://img4.duitang.com/uploads/item/201506/05/20150605140911_uNhQY.jpeg");
		// userInfoItem_1View.iv_touxiang.setImageBitmap(BitmapFactory.decodeFile(Constants.HEAD_PICTURE_PATH));

		if (userModel.getUserSex().equals("Man")) {
			// 显示默认男头像
			imageLoader.displayImage(userModel.getHeadPicture(),
					userInfoItem_1View.iv_touxiang,
					ImageLoaderUtils.getOptions3());
		} else if (userModel.getUserSex().equals("Woman")) {
			// 显示默认女头像
			imageLoader.displayImage(userModel.getHeadPicture(),
					userInfoItem_1View.iv_touxiang,
					ImageLoaderUtils.getOptions());
		} else {
			// 显示默认头像
			imageLoader.displayImage(userModel.getHeadPicture(),
					userInfoItem_1View.iv_touxiang,
					ImageLoaderUtils.getOptions2());
		}

		LogUtil.i(TAG, userModel.getHeadPicture());

		// System.out.println("photo......"+userModel.getHeadPicture());
		/*
		 * new Thread(new Runnable() {
		 * 
		 * @Override public void run() { // TODO Auto-generated method stub
		 * BitmapHelp .getBitmapUtils(getActivity()).display(userInfoItem_1View
		 * .iv_touxiang, userModel.getHeadPicture()); } }).start();
		 */
		/*
		 * ImageView avatar = new ImageView(getActivity());
		 * BitmapHelp.getBitmapUtils(getActivity()).display(avatar,
		 * userModel.getHeadPicture(), new BitmapLoadCallBack<View>() {
		 * 
		 * @Override public void onLoadCompleted(View container, String uri,
		 * Bitmap bitmap, BitmapDisplayConfig config, BitmapLoadFrom from) {
		 * userInfoItem_1View.iv_touxiang.setImageBitmap(bitmap); }
		 * 
		 * @Override public void onLoadFailed(View container, String uri,
		 * Drawable drawable) { // TODO Auto-generated // method stub
		 * userInfoItem_1View
		 * .iv_touxiang.setImageResource(R.drawable.ic_launcher); } });
		 */
		// 名字
		userInfoItem_1View.tv_name.setText(userModel.getUserName());
		// 性别
		if (userModel.getUserSex().equals("Man")) {
			// 男
			userInfoItem_1View.iv_sex.setImageResource(R.drawable.boy);
		} else if (userModel.getUserSex().equals("Woman")) {
			// 女
			userInfoItem_1View.iv_sex.setImageResource(R.drawable.girl);
		}

		// 手机号
		userInfoItem_1View.tv_phone.setText(userModel.getMobilephone());

		// 信息修改可点击
		// userInfoItem_1View.setClickable(true);

	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("UserInfoFragment");
	}

	/**
	 * 用户是否登录
	 * 
	 * @return
	 */
	public boolean isLogined() {
		return !SharedPrefsUtil.getValue(Constants.TOKEN, "").equals("");
	}
}
