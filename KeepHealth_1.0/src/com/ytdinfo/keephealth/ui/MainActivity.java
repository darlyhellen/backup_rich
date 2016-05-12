package com.ytdinfo.keephealth.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rayelink.eckit.SDKCoreHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.app.MyApp;
import com.ytdinfo.keephealth.jpush.ExampleUtil;
import com.ytdinfo.keephealth.model.DocInfoBean;
import com.ytdinfo.keephealth.model.DocOnline;
import com.ytdinfo.keephealth.model.TBNews;
import com.ytdinfo.keephealth.service.UpdateService;
import com.ytdinfo.keephealth.ui.login.LoginActivity;
import com.ytdinfo.keephealth.utils.DBUtil;
import com.ytdinfo.keephealth.utils.ImageLoaderUtils;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.kitsdk.ECDeviceKit;
import com.yuntongxun.kitsdk.db.ContactSqlManager;
import com.yuntongxun.kitsdk.db.ConversationSqlManager;
import com.yuntongxun.kitsdk.fragment.ConversationListFragment;
import com.yuntongxun.kitsdk.fragment.ConversationListFragment.OnGetConversationInfoListener;
import com.yuntongxun.kitsdk.fragment.ConversationListFragment.OnUpdateMsgUnreadCountsListener;
import com.yuntongxun.kitsdk.ui.chatting.model.ECConversation;
import com.yuntongxun.kitsdk.ui.group.model.ECContacts;
import com.yuntongxun.kitsdk.utils.DateUtil;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends Base2Activity implements
		OnUpdateMsgUnreadCountsListener {
	private String TAG = "MainActivity";
	private AlertDialog alertDialog;
	private RadioGroup radioGroup;
	private FragmentManager fragmentManager;
	private HomeFragment homeFragment;
	private ConversationListFragment newsFragment;
	private UserInfoFragment userInfoFragment;
	private RadioButton radioButton0, radioButton1, radioButton2;

	private int oldBtn = 0;
	private boolean flag = true;

	public static boolean isForeground = false;

	public RadioButton getRadioButton0() {
		return radioButton0;
	}

	public void setRadioButton0(RadioButton radioButton0) {
		this.radioButton0 = radioButton0;
	}

	private String fromPersonData;
	private ImageView newsPoint;

	public static List<DocOnline> onlines;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 检查更新
		// if (SharedPrefsUtil.getValue(Constants.CHECKISUPDATE, false)
		// && SharedPrefsUtil.getValue(Constants.NOTUPDATE, 0) <= Calendar
		// .getInstance().get(Calendar.DAY_OF_YEAR)) {
		//
		// checkISupdate();
		//
		// // showUpdateDialog();
		// }
		startCheck();

		// 统计活跃用户
		statisticActiveUser();

		// ysj
		initView();

		int checkedid_radiobt = SharedPrefsUtil.getValue(
				Constants.CHECKEDID_RADIOBT, 0);
		((RadioButton) radioGroup.getChildAt(checkedid_radiobt))
				.setTextColor(getResources().getColor(R.color.w_RadioButton));

		if (getIntent().hasExtra("flag")) {
			changeRadioButtonTextColor();
			initHome2();
			radioButton2.setChecked(true);
			radioButton2.setTextColor(getResources().getColor(
					R.color.w_RadioButton));
		} else if (getIntent().hasExtra("news")) {
			changeRadioButtonTextColor();
			initHome1();
			radioButton1.setChecked(true);
			radioButton1.setTextColor(getResources().getColor(
					R.color.w_RadioButton));
		} else {
			changeRadioButtonTextColor();
			initHome();
			radioButton0.setChecked(true);
			radioButton0.setTextColor(getResources().getColor(
					R.color.w_RadioButton));
		}
		initListener();
		registerMessageReceiver(); // used for receive msg
		init();
	}

	/**
	 * 下午3:01:16
	 * 
	 * @author zhangyh2 TODO 版本更新
	 */
	protected void startCheck() {
		// TODO Auto-generated method stub
		// 考虑到用户流量的限制，目前我们默认在Wi-Fi接入情况下才进行自动提醒。如需要在任意网络环境下都进行更新自动提醒，
		// 则请在update调用之前添加以下代码：UmengUpdateAgent.setUpdateOnlyWifi(false)。
		// 特别提示：针对机顶盒等可能不支持或者没有无线网络的设备， 请同样添加上述代码。
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		String upgrade_mode = MobclickAgent.getConfigParams(this, "updata");
		// OnlineConfigAgent.getInstance().updateOnlineConfig(this);
		// OnlineConfigAgent.getInstance().setDebugMode(true);
		// String upgrade_mode =
		// OnlineConfigAgent.getInstance().getConfigParams(
		// this, "updata");
		LogUtils.i("获取的参数" + upgrade_mode);
		LogUtils.i("获取的参数是否为空" + TextUtils.isEmpty(upgrade_mode));
		if (TextUtils.isEmpty(upgrade_mode)) {
			return;
		}
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.update(this);
		UmengUpdateAgent.forceUpdate(this);// 这行如果是强制更新就一定加上
		int versionName = MyApp.getInstance().getVersionCode();
		LogUtils.i("进行参数的对比" + upgrade_mode + "--" + versionName);
		int serv = 0;
		try {
			serv = Integer.parseInt(upgrade_mode);
		} catch (Exception e) {
			// TODO: handle exception
			LogUtil.i(upgrade_mode + "不能转换成int类型");
			return;
		}
		if (serv > versionName) {
			// 进入强制更新
			UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

				@Override
				public void onUpdateReturned(int updateStatus,
						UpdateResponse updateResponse) {
				}
			});
			UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {
				@Override
				public void onClick(int status) {

					switch (status) {
					case UpdateStatus.Update:
					default:
						// 退出应用
						Toast.makeText(MainActivity.this, "请等待升级完成后再次使用，谢谢合作！",
								0).show();
						MainActivity.this.finish();
					}
				}
			});

		} else {
			UmengUpdateAgent.update(this);
		}
	}

	public void showUpdateDialog(final String lastestUrl) {
		if (alertDialog != null && alertDialog.isShowing()) {
			return;
		}
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (isQZupdate) {
						SharedPrefsUtil
								.putValue(Constants.CHECKEDID_RADIOBT, 0);
						SharedPrefsUtil.putValue(Constants.CHECKISUPDATE, true);
						System.exit(0);
					} else {
						// 计算间隔几天进行重新检验更新。
						Calendar calendar = Calendar.getInstance();
						calendar.set(Calendar.DAY_OF_YEAR,
								calendar.get(Calendar.DAY_OF_YEAR) + 3);
						SharedPrefsUtil.putValue(Constants.NOTUPDATE,
								calendar.get(Calendar.DAY_OF_YEAR));
					}
				}
				return false;
			}
		});
		alertDialog.show();
		alertDialog.setCanceledOnTouchOutside(false);
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.update);// 设置对话框的布局

		TextView descriptiontv = (TextView) window
				.findViewById(R.id.description);
		if (description != null) {
			descriptiontv.setText(description);
		}
		Button sure = (Button) window.findViewById(R.id.sure);
		sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				// checkISupdate();
				// update(lastestUrl);
				Intent intent = new Intent(MainActivity.this,
						UpdateService.class);
				intent.putExtra("url", lastestUrl);
				MainActivity.this.startService(intent);
				if (isQZupdate) {
					finish();
				}
			}
		});

		Button notsure = (Button) window.findViewById(R.id.notsure);
		if (isQZupdate) {
			alertDialog.setCancelable(false);
			notsure.setVisibility(View.GONE);
		} else {
			alertDialog.setCancelable(true);
			notsure.setVisibility(View.VISIBLE);
			notsure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					alertDialog.dismiss();
					// 计算间隔几天进行重新检验更新。
					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.DAY_OF_YEAR,
							calendar.get(Calendar.DAY_OF_YEAR) + 3);
					SharedPrefsUtil.putValue(Constants.NOTUPDATE,
							calendar.get(Calendar.DAY_OF_YEAR));
				}
			});
		}
	}

	/**
	 * 检查更新
	 */
	private void checkISupdate() {
		int versionCode = getVersionCode();

		String channel = "";
		try {
			ApplicationInfo appInfo = this.getPackageManager()
					.getApplicationInfo(getPackageName(),
							PackageManager.GET_META_DATA);
			channel = appInfo.metaData.getString("UMENG_CHANNEL");
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String url = Constants.ROOT_URl + "/api/SoftwareUpdate/List?version="
				+ versionCode + "&type=0&channel=" + channel;
		LogUtil.i("paul", url);

		RequestParams params = new RequestParams();

		HttpClient.get(this, url, params, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				LogUtil.i("paul", arg0.result.toString());

				parseJson(arg0.result.toString());
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {

			}
		});

	}

	/**
	 * Retrieves application's version code from the manifest
	 * 
	 * @return versionCode
	 */
	public int getVersionCode() {
		int code = 1;
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			code = packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return code;
	}

	private boolean isQZupdate;
	private String description;

	private void parseJson(String jsonStr) {
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			JSONObject jsonData = jsonObject.getJSONObject("Data");
			String lastestUrl = jsonData.getString("LastestUrl");
			isQZupdate = jsonData.getBoolean("Force");
			description = jsonData.getString("Description");
			LogUtil.i("paul", lastestUrl);

			if (!lastestUrl.equals("null") && lastestUrl != null
					&& !lastestUrl.equals("")) {
				int lastIndex_Backslash = lastestUrl.lastIndexOf("/");
				int index_apk = lastestUrl.lastIndexOf(".apk");
				String lastesVersion = lastestUrl.substring(
						lastIndex_Backslash + 1, index_apk);

				// String versionName = getAppVersionName(this);
				int versionCode = getVersionCode();

				LogUtil.i("paul", "服务器上版本： " + lastesVersion + "====="
						+ "本地版本： " + versionCode);
				if (Integer.parseInt(lastesVersion) - versionCode > 0) {
					// 需要更新
					showUpdateDialog(lastestUrl);
					// update(lastestUrl);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void statisticActiveUser() {
		List<String> listDate;
		Calendar calendar = Calendar.getInstance();
		// 日期输出格式（MM代表月，E代表星期，HH代表24小时制，hh代表12小时制，mm代表分）
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String currentDate = simpleDateFormat.format(calendar.getTime());
		LogUtil.i("wpc", "currentDate== " + currentDate); // 20150925

		String dateJson = SharedPrefsUtil.getValue(Constants.DATE, "");
		LogUtil.i("wpc", "dateJson== " + dateJson);

		if (dateJson.equals("")) {
			listDate = new ArrayList<String>();
			listDate.add(currentDate);
		} else {
			listDate = new Gson().fromJson(dateJson,
					new TypeToken<List<String>>() {
					}.getType());
			if (!listDate.contains(currentDate)) {

				listDate.add(currentDate);
			}
			// 判断统计
			if (listDate.size() >= 3) {
				if ((Integer.parseInt(listDate.get(listDate.size() - 1)) - Integer
						.parseInt(listDate.get(listDate.size() - 3))) <= 30) {
					// 统计
					LogUtil.i("wpc", "统计");
					MobclickAgent.onEvent(this, Constants.UMENG_EVENT_24);
				}
			}
		}
		SharedPrefsUtil.putValue(Constants.DATE, new Gson().toJson(listDate));

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// super.onSaveInstanceState(outState);
	}

	private void initView() {
		// TODO Auto-generated method stub
		radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
		radioButton0 = (RadioButton) findViewById(R.id.tab_rb_1);
		radioButton1 = (RadioButton) findViewById(R.id.tab_rb_2);
		radioButton2 = (RadioButton) findViewById(R.id.tab_rb_3);
		newsPoint = (ImageView) findViewById(R.id.news_point);
	}

	private void initHome() {
		// TODO Auto-generated method stub
		fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		homeFragment = new HomeFragment();
		transaction.add(R.id.framelayout, homeFragment);
		transaction.show(homeFragment);
		transaction.commit();
	}

	private void initHome1() {
		// TODO Auto-generated method stub
		fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		newsFragment = new ConversationListFragment();
		newsFragment.setOnGetConversationInfoListener(infoInterface);
		transaction.add(R.id.framelayout, newsFragment);
		transaction.show(newsFragment);
		transaction.commit();
	}

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	private OnGetConversationInfoListener infoInterface = new OnGetConversationInfoListener() {

		@Override
		public void getConversationInfo(final ECConversation conversation,
				final TextView textView, final ImageView imageView,
				final ImageView isonline) {
			if (conversation.getSessionId().startsWith("g")) {
				imageView.setImageResource(R.drawable.group_head);
				textView.setText(conversation.getUsername() == null ? conversation
						.getSessionId() : conversation.getUsername());
				return;
			}

			LogUtil.i("医生是否在线判断数组" + onlines);
			// 根据医生是否在线。来判断如何展示。医生状态都在此类中。可以直接判断获取。
			ECContacts contact = ContactSqlManager.getContact(conversation
					.getSessionId());
			if (contact == null
					|| contact.getNickname() == contact.getContactid()) {
				RequestParams requestParams = new RequestParams();
				requestParams.addQueryStringParameter("voipAccount",
						conversation.getSessionId());
				HttpClient.get(MyApp.getInstance(), Constants.GETDOC_URL,
						requestParams, new RequestCallBack<String>() {
							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								JSONObject jsonObject;
								JSONObject responser = null;
								try {
									jsonObject = new JSONObject(arg0.result);
									responser = jsonObject
											.getJSONObject("Data");
								} catch (JSONException e) {
									e.printStackTrace();
								}
								DocInfoBean docInfoBean = new Gson().fromJson(
										responser.toString(), DocInfoBean.class);
								boolean isOnline = false;
								if (onlines != null) {
									for (DocOnline docOnline : onlines) {
										if (conversation.getSessionId().equals(
												docOnline.getDoc())) {
											isOnline = docOnline.isOnline();
											break;
										}
									}
								}
								if (isOnline) {
									// 医生在线
									textView.setText(docInfoBean.getUserName());
//									BitmapUtils bitmapUtils = new BitmapUtils(
//											MyApp.getInstance());
//									bitmapUtils.display(imageView,
//											docInfoBean.getHeadPicture());
									
									imageLoader.displayImage(docInfoBean.getHeadPicture(),
											imageView,
											ImageLoaderUtils.getOptions2());
									imageView.setAlpha(1.0f);
									isonline.setImageResource(R.drawable.choices_icon_selected);
								} else {
									// 医生离线
									textView.setText(docInfoBean.getUserName());
//									BitmapUtils bitmapUtils = new BitmapUtils(
//											MyApp.getInstance());
//									bitmapUtils.display(imageView,
//											docInfoBean.getHeadPicture());
									imageLoader.displayImage(docInfoBean.getHeadPicture(),
											imageView,
											ImageLoaderUtils.getOptions2());
									imageView.setAlpha(0.5f);
									isonline.setImageResource(R.drawable.close_icon_selected);
								}

								ECContacts contacts = new ECContacts();
								contacts.setContactid(conversation
										.getSessionId());
								contacts.setRemark(docInfoBean.getHeadPicture());
								contacts.setNickname(docInfoBean.getUserName());
								ContactSqlManager.insertContact(contacts, 1,
										true);
							}

							@Override
							public void onFailure(HttpException arg0,
									String arg1) {
								// TODO Auto-generated method stub
							}
						});

			} else {
				boolean isOnline = false;
				if (onlines != null) {
					for (DocOnline docOnline : onlines) {
						if (conversation.getSessionId().equals(
								docOnline.getDoc())) {
							isOnline = docOnline.isOnline();
							break;
						}
					}
				}
				if (isOnline) {
					// 医生在线
					textView.setText(contact.getNickname());
//					BitmapUtils bitmapUtils = new BitmapUtils(
//							MyApp.getInstance());
//					bitmapUtils.display(imageView, contact.getRemark());
//					imageView.setAlpha(1.0f);
					imageView.setAlpha(1.0f);
					imageLoader.displayImage(contact.getRemark(),
							imageView,
							ImageLoaderUtils.getOptions2());
//					imageView.setImageResource(R.drawable.ic_launcher);
//					imageView.setAlpha(1.0f);
					isonline.setImageResource(R.drawable.choices_icon_selected);
				} else {
					// 医生离线
					textView.setText(contact.getNickname());
					imageLoader.displayImage(contact.getRemark(),
							imageView,
							ImageLoaderUtils.getOptions2());
					imageView.setAlpha(0.5f);
					isonline.setImageResource(R.drawable.close_icon_selected);
				}
			}
		}

		@Override
		public void getBMYXZSConversationInfo(ECConversation arg0,
				TextView arg1, TextView arg2) {
			DBUtil dbUtil = new DBUtil(MyApp.getInstance());
			TBNews news = dbUtil.queryFirst();
			if (news != null) {
				Log.e("mGetPersonInfoListener-news", news.getDesc());
				Log.e("mGetPersonInfoListener-news", news.getTitle());
				arg1.setText(news.getTitle());
				arg2.setText(DateUtil.getDateString(
						Long.parseLong(news.getDateCreate()),
						DateUtil.SHOW_TYPE_CALL_LOG).trim());
			} else {
				Log.e("mGetPersonInfoListener-news", "110-kong");
				arg1.setText("");
				arg2.setText("");
			}
		}

	};

	private void initHome2() {
		// TODO Auto-generated method stub
		fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		userInfoFragment = new UserInfoFragment();
		transaction.add(R.id.framelayout, userInfoFragment);
		transaction.show(userInfoFragment);
		transaction.commit();
	}

	/**
	 * 初始化监听
	 */
	private void initListener() {
		// TODO Auto-generated method stub
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				// int position = radioButtonPosition(checkedId);
				// flag = position > oldBtn ? true : false;//
				// 根据点击顺序判断fragment的进入方�?
				// oldBtn = position;
				// SharedPrefsUtil.putValue(Constants.CHECKEDID_RADIOBT,
				// position);
				clickRadioButton(checkedId);
			}
		});
	}

	@SuppressLint("ResourceAsColor")
	public void clickRadioButton(int checkedId) {
		// TODO Auto-generated method stub
		int position = radioButtonPosition(checkedId);
		flag = position > oldBtn ? true : false;// 根据点击顺序判断fragment的进入方�?
		oldBtn = position;
		SharedPrefsUtil.putValue(Constants.CHECKEDID_RADIOBT, position);

		changeRadioButtonTextColor();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		if (flag) {
			transaction.setCustomAnimations(R.anim.anim_push_from_right,
					R.anim.anim_pop_from_right);
		} else {
			transaction.setCustomAnimations(R.anim.anim_push_from_left,
					R.anim.anim_pop_from_left);
		}

		hideFragments(transaction);
		switch (checkedId) {
		case R.id.tab_rb_1:
			radioButton0.setTextColor(getResources().getColor(
					R.color.w_RadioButton));
			if (homeFragment == null) {
				homeFragment = new HomeFragment();
				transaction.add(R.id.framelayout, homeFragment);
			} else {
				if (homeFragment.isVisible())
					return;
				transaction.show(homeFragment);
			}
			transaction.commitAllowingStateLoss();
			break;
		case R.id.tab_rb_2:
			radioButton1.setTextColor(getResources().getColor(
					R.color.w_RadioButton));
			if (null != SharedPrefsUtil.getValue(Constants.TOKEN, null)) {
				if (newsFragment == null) {
					newsFragment = new ConversationListFragment();
					newsFragment
							.setOnGetConversationInfoListener(infoInterface);
					transaction.add(R.id.framelayout, newsFragment);
				} else {
					if (newsFragment.isVisible())
						return;
					transaction.show(newsFragment);
				}
				transaction.commitAllowingStateLoss();
				// 每次打开此页面。或者刷新此页面都进行请求服务端，医生在线列表。<b>需要在这里查询数据库获取DoctorID</b>
				Cursor cursor = ConversationSqlManager.getConversationCursor();
				if (cursor!=null) {
					List<String> param = new ArrayList<String>();
					while (cursor.moveToNext()) {
						int num = cursor.getColumnIndex("sessionId");
						String s = cursor.getString(num);
						param.add(s);
					}
					RequestParams params = new RequestParams();
					params.addQueryStringParameter(new BasicNameValuePair(
							"voipAccounts", param.toString()));
					params.addQueryStringParameter(new BasicNameValuePair("rType",
							"Android"));
					HttpClient.get(this, Constants.DOCISONLINE, params,
							new RequestCallBack<String>() {

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
									// TODO Auto-generated method stub
									LogUtils.i(arg0.result);
									onlines = new ArrayList<DocOnline>();
									try {
										JSONObject json = new JSONObject(
												arg0.result);
										JSONObject data = json
												.getJSONObject("Data");
										JSONArray doctorInfos = data
												.getJSONArray("DoctorInfos");
										if (null != doctorInfos) {
											for (int i = 0; i < doctorInfos
													.length(); i++) {
												JSONObject jsonObject = doctorInfos
														.getJSONObject(i);
												String info = jsonObject
														.getString("VoipAccount");
												onlines.add(new DocOnline(
														info,
														jsonObject
																.getBoolean("IsOnline")));
											}
										}
									} catch (Exception e) {
										// TODO: handle exception
										onlines = new ArrayList<DocOnline>();
									}
									newsFragment.getmAdapter().notifyChange();
								}

								@Override
								public void onFailure(HttpException arg0,
										String arg1) {
									// TODO Auto-generated method stub
									LogUtils.i(arg1);
									onlines = new ArrayList<DocOnline>();
									newsFragment.getmAdapter().notifyChange();
								}
							});
				}
				/*
				 * System.out.println("device///"+CCPHelper.getInstance().getDevice
				 * ()); if(null==CCPHelper.getInstance().getDevice()||CCPHelper.
				 * getInstance().getDevice().isOnline()!=State.ONLINE){
				 * CCPHelper.getInstance().registerCCP( new
				 * CCPHelper.RegistCallBack() {
				 * 
				 * @Override public void onRegistResult(int reason, String msg)
				 * { // Log.i("XXX", String.format("%d, %s", // reason, msg));
				 * if (reason==8192) { Intent intent = new Intent();
				 * intent.setClass(MainActivity.this,
				 * GroupMessageListActivity.class); if (flag) {
				 * startActivityForResult(intent,1001); }else {
				 * startActivityForResult(intent,1002); } } else {
				 * 
				 * } } });}else { Intent intent = new Intent();
				 * intent.setClass(MainActivity.this,
				 * GroupMessageListActivity.class); if (flag) {
				 * startActivityForResult(intent,1001); }else {
				 * startActivityForResult(intent,1002); } }
				 */} else {
				Intent i = new Intent(MainActivity.this, LoginActivity.class);
				startActivityForResult(i, 1003);
			}

			/*
			 * if (newsFragment == null) { newsFragment = new NewsFragment();
			 * transaction.add(R.id.framelayout, newsFragment); } else { if
			 * (newsFragment.isVisible()) return;
			 * transaction.show(newsFragment); }
			 */
			break;
		case R.id.tab_rb_3:
			radioButton2.setTextColor(getResources().getColor(
					R.color.w_RadioButton));

			if (null != SharedPrefsUtil.getValue(Constants.TOKEN, null)) {
				if (userInfoFragment == null) {
					userInfoFragment = new UserInfoFragment();
					transaction.add(R.id.framelayout, userInfoFragment);
				} else {
					if (userInfoFragment.isVisible())
						return;
					transaction.show(userInfoFragment);
				}
				transaction.commitAllowingStateLoss();
			} else {
				Intent i = new Intent(MainActivity.this, LoginActivity.class);
				startActivityForResult(i, 1003);
			}
			break;
		}

	}

	@SuppressLint("ResourceAsColor")
	public void clickRadioButton2(int checkedId) {
		// TODO Auto-generated method stub
		int position = radioButtonPosition(checkedId);
		flag = position > oldBtn ? true : false;// 根据点击顺序判断fragment的进入方�?
		oldBtn = position;
		SharedPrefsUtil.putValue(Constants.CHECKEDID_RADIOBT, position);

		radioButton1.setChecked(true);

		changeRadioButtonTextColor();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// if (flag) {
		// transaction.setCustomAnimations(R.anim.anim_push_from_right,
		// R.anim.anim_pop_from_right);
		// } else {
		// transaction.setCustomAnimations(R.anim.anim_push_from_left,
		// R.anim.anim_pop_from_left);
		// }

		hideFragments(transaction);

		// radioButton1.setChecked(true);
		radioButton1.setTextColor(getResources()
				.getColor(R.color.w_RadioButton));
		if (null != SharedPrefsUtil.getValue(Constants.TOKEN, null)) {
			if (newsFragment == null) {
				newsFragment = new ConversationListFragment();
				newsFragment.setOnGetConversationInfoListener(infoInterface);
				transaction.add(R.id.framelayout, newsFragment);
			} else {
				if (newsFragment.isVisible())
					return;
				transaction.show(newsFragment);
			}
			transaction.commitAllowingStateLoss();

		} else {
			Intent i = new Intent(MainActivity.this, LoginActivity.class);
			startActivityForResult(i, 1003);
		}

	}

	private void hideFragments(FragmentTransaction transaction) {

		if (homeFragment != null) {
			transaction.hide(homeFragment);
		}
		if (newsFragment != null) {
			transaction.hide(newsFragment);
		}
		if (userInfoFragment != null) {
			transaction.hide(userInfoFragment);
		}

	}

	private void changeRadioButtonTextColor() {
		radioButton0.setTextColor(getResources().getColor(R.color.w_gray));
		radioButton1.setTextColor(getResources().getColor(R.color.w_gray));
		radioButton2.setTextColor(getResources().getColor(R.color.w_gray));
	}

	private int radioButtonPosition(int checkedId) {
		// TODO Auto-generated method stub
		switch (checkedId) {
		case R.id.tab_rb_1:
			return 0;
		case R.id.tab_rb_2:
			return 1;
		case R.id.tab_rb_3:
			return 2;
		}

		return 0;
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		if (arg2 == null)
			return;

		if (arg2.hasExtra("flag")) {
			radioButton2.setChecked(true);
		}
		switch (arg0) {
		case 1001:
			radioButton0.setChecked(true);
			break;
		case 1002:
			radioButton2.setChecked(true);
			break;
		case 1003:
			radioButton0.setChecked(true);
			break;

		default:
			break;
		}
		super.onActivityResult(arg0, arg1, arg2);
	}

	/**
	 * 程序退出
	 * */
	private long firstime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			long secondtime = System.currentTimeMillis();
			if (secondtime - firstime > 3000) {
				Toast.makeText(MainActivity.this, "再按一次,退出程序",
						Toast.LENGTH_SHORT).show();
				firstime = System.currentTimeMillis();
				return true;
			} else {
				/*
				 * Intent intent = new Intent();
				 * intent.setClass(MainActivity.this, MainActivity.class);
				 * intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				 * //注意本行的FLAG设置 startActivity(intent);
				 */
				SharedPrefsUtil.putValue(Constants.CHECKEDID_RADIOBT, 0);
				SharedPrefsUtil.putValue(Constants.CHECKISUPDATE, true);
				LogUtil.i("wpc2", "onKeyDown===true");
				finish();
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	// 初始化 JPush。如果已经初始化，但没有登录成功，则执行重新登录。
	private void init() {
		JPushInterface.init(getApplicationContext());
	}

	@Override
	protected void onResume() {
		isForeground = true;
		super.onResume();
		try {
			if (null != SharedPrefsUtil.getValue(Constants.TOKEN, null)
					&& newsFragment == null&&SDKCoreHelper.getInstance().mConnect== ECDevice.ECConnectState.CONNECT_SUCCESS ) {
				fragmentManager = getSupportFragmentManager();
				FragmentTransaction transaction = fragmentManager
						.beginTransaction();
				newsFragment = new ConversationListFragment();
				newsFragment.setOnGetConversationInfoListener(infoInterface);
				transaction.add(R.id.framelayout, newsFragment);
				transaction.hide(newsFragment);
				transaction.commit();
			}
		} catch (Exception e) {

		}
		JPushInterface.onResume(this);

		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		isForeground = false;
		super.onPause();

		JPushInterface.onPause(this);

		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mMessageReceiver);
		fragmentManager = getSupportFragmentManager();
		List<Fragment> frgList = fragmentManager.getFragments();
		frgList.clear();

	}

	// for receive customer msg from jpush server
	private MessageReceiver mMessageReceiver;
	public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";

	public void registerMessageReceiver() {
		mMessageReceiver = new MessageReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(MESSAGE_RECEIVED_ACTION);
		registerReceiver(mMessageReceiver, filter);
	}

	public class MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
				String messge = intent.getStringExtra(KEY_MESSAGE);
				String extras = intent.getStringExtra(KEY_EXTRAS);
				StringBuilder showMsg = new StringBuilder();
				showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
				if (!ExampleUtil.isEmpty(extras)) {
					showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
				}
				setCostomMsg(showMsg.toString());
			}
		}
	}

	private void setCostomMsg(String msg) {
		LogUtil.i("===", msg);
	}

	public MainActivity() {

	}

	public Context getContext() {
		return this;
	}

	@Override
	public void OnUpdateMsgUnreadCounts() {
		new Thread() {
			@Override
			public void run() {
				if (ECDeviceKit.userId != null) {
					if (ConversationSqlManager.getInstance()
							.qureyAllSessionUnreadCount() == 0) {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								newsPoint.setVisibility(View.GONE);
							}
						});

					} else {
						runOnUiThread(new Runnable() {

							@Override
							public void run() {
								newsPoint.setVisibility(View.VISIBLE);
							}
						});
					}
				}
			}
		}.start();

		// try{
		// if(ConversationSqlManager.getInstance().qureyAllSessionUnreadCount()==0)
		// {
		// newsPoint.setVisibility(View.GONE);
		// }else {
		// newsPoint.setVisibility(View.VISIBLE);
		// }
		// }catch(Exception e )
		// {
		//
		// }

	}

}
