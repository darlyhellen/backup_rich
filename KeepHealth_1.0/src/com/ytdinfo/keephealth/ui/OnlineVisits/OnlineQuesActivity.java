package com.ytdinfo.keephealth.ui.OnlineVisits;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.rayelink.eckit.SDKCoreHelper;
import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.adapter.PhotoGridViewAdapter;
import com.ytdinfo.keephealth.adapter.PhotoGridViewAdapter.Callback;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.app.MyApp;
import com.ytdinfo.keephealth.model.DocInfoBean;
import com.ytdinfo.keephealth.model.OnlineQuestionUserModel;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.MainActivity;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;
import com.ytdinfo.keephealth.ui.view.MyPopWindow;
import com.ytdinfo.keephealth.ui.view.MyProgressDialog;
import com.ytdinfo.keephealth.utils.Chat_Dialog;
import com.ytdinfo.keephealth.utils.DBUtilsHelper;
import com.ytdinfo.keephealth.utils.ImageTools;
import com.ytdinfo.keephealth.utils.JsonUtil;
import com.ytdinfo.keephealth.utils.ListUtil;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;
import com.yuntongxun.ecsdk.ECDevice.ECConnectState;
import com.yuntongxun.kitsdk.ECDeviceKit;
import com.yuntongxun.kitsdk.beans.ChatInfoBean;

public class OnlineQuesActivity extends BaseActivity implements Callback,
		OnClickListener {
	private static final String TAG = "OnlineQuesActivity";

	/**
	 * 上午10:54:29 TODO true 为可以点击，false 为不可以点击。
	 */
	private boolean isClick = true;
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x111) {
				// 为空，保存按钮不可点击
				bt_save.setClickable(false);
				bt_save.setBackgroundResource(R.drawable.circle_l_white);
				bt_save.setTextColor(Color.parseColor("#77FFFFFF"));
			} else if (msg.what == 0x222) {
				// 不为空，保存按钮可点击
				if (isClick) {
					bt_save.setClickable(true);
					bt_save.setBackgroundResource(R.drawable.circle_white_selector);
					bt_save.setTextColor(Color.parseColor("#FFFFFFFF"));
				} else {
					bt_save.setClickable(false);
					bt_save.setBackgroundResource(R.drawable.circle_l_white);
					bt_save.setTextColor(Color.parseColor("#77FFFFFF"));
				}

			} else if (msg.what == 0x123) {
				if (croped_bitmap != null) {

					requestImagesUrl();
				}
			}
		};
	};

	private OnlineQuestionUserModel onlineQuestionUserModel;
	private List<String> list_imagesUrl = new ArrayList<String>();
	private List<String> list_imagesPath = new ArrayList<String>();
	private AlertDialog builder;

	private CommonActivityTopView commonActivityTopView;
	private ListView listview;
	private List<Map<String, Object>> listMapData;
	private RelativeLayout rl, rl_helpOthers;
	private TextView tv_info;
	private ImageView iv_arrow;
	private EditText et_content;
	private Button bt_save;

	private boolean isExtended;

	private ImageButton back;
	private GridView gridView;
	private List<String> listData = new ArrayList<String>();
	private PhotoGridViewAdapter photoGridViewAdapter;

	private MyPopWindow mypop;
	private PopupWindow pop;
	private LinearLayout ll_parent;

	private Timer timer;

	private Bitmap croped_bitmap;
	private String image_path;

	private MyProgressDialog myProgressDialog2;

	DbUtils db;

	private LinearLayout ll;
	private RadioGroup rg;
	private RadioButton rb_man;
	private RadioButton rb_woman;

	private int gender_tag = 1;
	private EditText et_age;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online_report);

		LogUtil.i("wpc", "onCreate");

		SharedPrefsUtil.putValue("show_edit", true);

		// 判断 activity被销毁后 有没有数据被保存下来
		if (savedInstanceState != null) {

			image_path = savedInstanceState.getString("ImageFilePath");

			Log.i("wpc", "savedInstanceState===image_path===" + image_path);

			myProgressDialog2 = new MyProgressDialog(OnlineQuesActivity.this);
			myProgressDialog2.setMessage("上传照片....");
			myProgressDialog2.show();

			new Thread(new Runnable() {

				@Override
				public void run() {
					cropImage(image_path);
					Message msg = Message.obtain();
					msg.what = 0x123;
					handler.sendMessage(msg);
				}
			}).start();
		}

		initView1();

		onlineQuestionUserModel = new OnlineQuestionUserModel();
		db = DBUtilsHelper.getInstance().getDb();
		// // 拿到个人资料信息进行显示
		// // 先拿到本地保存的数据
		// String jsonStr = SharedPrefsUtil.getValue(
		// Constants.ONLINE_QUES_USERMODEL, "");
		// List<OnlineQuestionUserModel> listData = JsonUtil
		// .jsonTOonlinequesusermodel(jsonStr);

		// setShowing(listData);

		// if (!isExistMe(listData)) {
		// "自己"的信息不存在

		// // 把自己的信息替换为个人资料中的信息
		// saveInfo_me(listData);
		// }

		// initData();
		//
		// initListView();

		// initView();

		initListener();
		initPop();

		listenETisempty();
		// requestRrport();
	}

	@Override
	protected void onResume() {
		super.onResume();

		MobclickAgent.onPageStart("OnlineQuesActivity");
		MobclickAgent.onResume(this);

		if (SharedPrefsUtil.getValue("show_edit", false)) {
			setShowing();
		}

		initData();

		// LogUtil.i(TAG, listData.size()+"");
		//
		// if(listData.size()!=0){
		// Map<String, Object > map = listMapData.get(listMapData.size()-1);
		//
		// String infoStr = map.get("named") + " (" + map.get("sex") + ","
		// + map.get("age") + ")";
		// tv_info.setText(infoStr);
		// onlineQuestionUserModel.setNamed(map.get("named") + "");
		// onlineQuestionUserModel.setSex(map.get("sex") + "");
		// onlineQuestionUserModel.setAge((Integer) map.get("age"));
		// }
		// // 先拿到本地保存的数据
		// String jsonStr = SharedPrefsUtil.getValue(
		// Constants.ONLINE_QUES_USERMODEL, "");
		// List<OnlineQuestionUserModel> list = JsonUtil
		// .jsonTOonlinequesusermodel(jsonStr);
		// if (list.size() != 0) {
		// OnlineQuestionUserModel me = list.get(list.size() - 1);
		//
		// onlineQuestionUserModel = me;
		//
		// String infoStr = me.getNamed() + " (" + me.getSex() + ","
		// + me.getAge() + ")";
		// tv_info.setText(infoStr);
		// } else {
		// tv_info.setText("自己");
		// }

		// initView();

		if (!SharedPrefsUtil.getValue("show_edit", false)) {
			ll.setVisibility(View.GONE);
		}

		if (!(ll.getVisibility() == View.VISIBLE)) {
			showInfoData();
		}

		initListView();

		// listMapData.clear();
		// listview.setAdapter(new SimpleAdapter(this, listMapData,
		// R.layout.item_listview, new String[] { "named", "age" },
		// new int[] { R.id.id_tv_name, R.id.id_tv_age }));

	}

	private void setShowing() {
		// 拿到个人资料信息进行显示
		// 先拿到本地保存的数据
		String jsonStr = SharedPrefsUtil.getValue(
				Constants.ONLINE_QUES_USERMODEL, "");
		List<OnlineQuestionUserModel> listData = JsonUtil
				.jsonTOonlinequesusermodel(jsonStr);

		OnlineQuestionUserModel me = null;
		if (isExistMe(listData)) {
			// 先拿到本地保存的数据
			me = listData.get(ListUtil.getPosition(listData, "自己"));
		}

		// 拿到个人信息
		String jsonUser = SharedPrefsUtil.getValue(Constants.USERMODEL, "");
		UserModel userModel = new Gson().fromJson(jsonUser, UserModel.class);
		if (userModel == null) {
			return;
		}
		if (!isFullofME(me) && !isFullofUSER(userModel)) {
			// 显示完善信息的布局
			tv_info.setText("自己");
			ll.setVisibility(View.VISIBLE);
			iv_arrow.setVisibility(View.GONE);
		} else {
			ll.setVisibility(View.GONE);
			iv_arrow.setVisibility(View.VISIBLE);
			// 把自己的信息替换为个人资料中的信息
			saveInfo_me(listData, userModel, me);

		}

	}

	public void showInfoData() {
		// 显示最后一个
		// 先拿到本地保存的数据
		String jsonStr = SharedPrefsUtil.getValue(
				Constants.ONLINE_QUES_USERMODEL, "");
		List<OnlineQuestionUserModel> list = JsonUtil
				.jsonTOonlinequesusermodel(jsonStr);
		if (list.size() != 0) {
			OnlineQuestionUserModel last = list.get(list.size() - 1);

			onlineQuestionUserModel = last;

			String infoStr = last.getNamed() + " (" + last.getSex() + ","
					+ last.getAge() + ")";
			tv_info.setText(infoStr);
		} else {
			tv_info.setText("自己");
		}
	}

	private boolean isFullofUSER(UserModel userModel) {
		if (userModel.getUserSex() == null || userModel.getUserSex().equals("")
				|| userModel.getUserSex().equals("Secret")
				|| userModel.getAge() <= 0) {
			return false;
		}
		return true;
	}

	private boolean isFullofME(OnlineQuestionUserModel me) {
		if (me == null) {
			return false;
		}
		if (me.getSex() == null || me.getSex().equals("") || me.getAge() <= 0) {
			return false;
		}
		return true;
	}

	private void initView1() {
		listview = (ListView) findViewById(R.id.id_listView);
		ll_parent = (LinearLayout) findViewById(R.id.id_ll_parent);
		commonActivityTopView = (CommonActivityTopView) findViewById(R.id.id_CommonActivityTopView);
		commonActivityTopView.tv_title.setText("提问");
		commonActivityTopView.bt_save.setText("提交");
		commonActivityTopView.bt_save
				.setBackgroundResource(R.drawable.circle_white_selector);
		bt_save = commonActivityTopView.bt_save;

		tv_info = (TextView) findViewById(R.id.id_tv_info);
		iv_arrow = (ImageView) findViewById(R.id.id_iv_arrow);
		ll = (LinearLayout) findViewById(R.id.id_ll);

		et_content = (EditText) findViewById(R.id.id_et_content);

		rg = (RadioGroup) findViewById(R.id.id_rg);
		rb_man = (RadioButton) findViewById(R.id.id_rb_man);
		rb_woman = (RadioButton) findViewById(R.id.id_rb_woman);

		et_age = (EditText) findViewById(R.id.id_et_age);

		rl = (RelativeLayout) findViewById(R.id.id_rl);
		rl_helpOthers = (RelativeLayout) findViewById(R.id.id_rl_helpOthers);

		gridView = (GridView) findViewById(R.id.aor_gridview);
		listData.add("file:///android_asset/add.png");
		photoGridViewAdapter = new PhotoGridViewAdapter(this, listData, this,
				"上传疾病照片");

		gridView.setAdapter(photoGridViewAdapter);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		LogUtil.i("wpc", "onSaveInstanceState");
		outState.putString("ImageFilePath", image_path);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);

		LogUtil.i("wpc", "onRestoreInstanceState");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		LogUtil.i("wpc", "onDestroy");
	}

	/**
	 * 监听edittext的内容是否为空， 为空：保存按钮不可点击 不为空：保存按钮可点击
	 */
	public void listenETisempty() {
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				if (et_content.getText().toString().trim().equals("")
						|| et_content.getText().toString() == null) {
					// 为空，不可点击
					Message msg = Message.obtain();
					msg.what = 0x111;
					handler.sendMessage(msg);
				} else {
					// 非空，可点击
					Message msg = Message.obtain();
					msg.what = 0x222;
					handler.sendMessage(msg);
				}
			}
		};
		timer = new Timer();
		timer.schedule(timerTask, 0, 100);
	}

	public void initPop() {
		mypop = new MyPopWindow(this);
		pop = mypop.getPop();

	}

	private void initData() {

		// 从本地取出数据
		listMapData = new ArrayList<Map<String, Object>>();
		String jsonModel_list = SharedPrefsUtil.getValue(
				Constants.ONLINE_QUES_USERMODEL, "");

		listMapData = JsonUtil.jsonTOmap_onlinequesusermodel(jsonModel_list);

	}

	private boolean isExistMe(List<OnlineQuestionUserModel> listData) {
		if (listData.size() != 0) {
			if (ListUtil.getPosition(listData, "自己") != -1)
				return true;
		}
		return false;
	}

	private void saveInfo_me(List<OnlineQuestionUserModel> listData,
			UserModel userModel, OnlineQuestionUserModel me) {
		String sex = "";
		int age = 0;

		// OnlineQuestionUserModel me = null;
		// if (isExistMe(listData)) {
		// // 先拿到本地保存的数据
		// me = listData.get(ListUtil.getPosition(listData, "自己"));
		//
		// }

		// // 拿到个人信息
		// String jsonUser = SharedPrefsUtil.getValue(Constants.USERMODEL, "");
		// UserModel userModel = new Gson().fromJson(jsonUser, UserModel.class);
		// String IdCard = userModel.getIDcard();

		// sex = userModel.getUserSex();
		// age = userModel.getAge();
		//
		// if (sex.equals("Man")) {
		// sex = "男";
		// } else if (sex.equals("Woman")) {
		// sex = "女";
		// } else {
		// // 个人资料不存在性别信息，使用本地保存的信息
		// if (me != null) {
		// sex = me.getSex();
		// } else {
		// sex = "";
		// }
		// }
		//
		// // 个人资料不存在年龄信息
		// if (age <= 0) {
		// // 使用身份证进行年龄计算
		// if (!IdCard.equals("") && IdCard != null) {
		// age = MathUtils.calculateAge(IdCard);
		// } else {
		// // 身份证不存在,实用本地保存的信息
		// if (me != null) {
		// age = me.getAge();
		// } else {
		// age = 0;
		// }
		// }
		//
		// }

		if (isFullofUSER(userModel)) {
			if (userModel.getUserSex().equals("Man")) {
				sex = "男";
			} else if (userModel.getUserSex().equals("Woman")) {
				sex = "女";
			}
			age = userModel.getAge();
		} else {
			sex = me.getSex();
			age = me.getAge();
		}

		OnlineQuestionUserModel model = new OnlineQuestionUserModel();
		model.setNamed("自己");
		model.setSex(sex);
		model.setAge(age);
		LogUtil.i(TAG, model.toString());

		// 先删除
		if (listData.size() != 0) {
			if (ListUtil.getPosition(listData, "自己") != -1)
				listData.remove(ListUtil.getPosition(listData, "自己"));

		}
		// 再添加新的信息
		listData.add(model);
		// 再次保存
		SharedPrefsUtil.putValue(Constants.ONLINE_QUES_USERMODEL,
				listData.toString());
		LogUtil.i(TAG, listData.toString());
		// if (sex.equals("") || sex.equals("Secret") || sex == null) {
		// // 从服务器获取的性别不存在
		// }
		// String jsonModel_me = SharedPrefsUtil.getValue(
		// Constants.ONLINE_QUES_USERMODEL_ME, "");
		// OnlineQuestionUserModel model_me = new Gson().fromJson(
		// jsonModel_me, OnlineQuestionUserModel.class);
		// if (model_me.getSex().equals("") || model_me.getSex() == null
		// || model_me.getAge() == 0) {
		//
		// }

	}

	private void showORgoneListView() {
		if (isExtended) {
			// listMapData.clear();
			listview.setVisibility(View.GONE);
			iv_arrow.setImageResource(R.drawable.arrow_gray_down);
			isExtended = false;
		} else {
			// 加载数据
			// String jsonModel_list =
			// SharedPrefsUtil.getValue(Constants.ONLINE_QUES_USERMODEL, "");
			//
			// listMapData =
			// JsonUtil.jsonTOmap_onlinequesusermodel(jsonModel_list);

			listview.setVisibility(View.VISIBLE);
			iv_arrow.setImageResource(R.drawable.arrow_gray_top);
			isExtended = true;
		}

	}

	private void initView() {
		// ll_parent = (LinearLayout) findViewById(R.id.id_ll_parent);
		// commonActivityTopView = (CommonActivityTopView)
		// findViewById(R.id.id_CommonActivityTopView);
		// commonActivityTopView.tv_title.setText("提问");
		// commonActivityTopView.bt_save.setText("提交");
		// commonActivityTopView.bt_save
		// .setBackgroundResource(R.drawable.circle_white_selector);
		// bt_save = commonActivityTopView.bt_save;
		//
		// tv_info = (TextView) findViewById(R.id.id_tv_info);
		// iv_arrow = (ImageView) findViewById(R.id.id_iv_arrow);
		//
		// et_content = (EditText) findViewById(R.id.id_et_content);

		// // 先拿到本地保存的数据
		// String jsonStr = SharedPrefsUtil.getValue(
		// Constants.ONLINE_QUES_USERMODEL, "");
		// List<OnlineQuestionUserModel> list = JsonUtil
		// .jsonTOonlinequesusermodel(jsonStr);
		// if (list.size() != 0) {
		// int position_me = ListUtil.getPosition(list, "自己");
		// if (position_me != -1) {
		// OnlineQuestionUserModel me = list.get(position_me);
		//
		// onlineQuestionUserModel = me;
		//
		// String infoStr = me.getNamed() + " (" + me.getSex() + ","
		// + me.getAge() + ")";
		// tv_info.setText(infoStr);
		// } else {
		// tv_info.setText("自己");
		// }
		// } else {
		// tv_info.setText("自己");
		// }

		// initListView();

		// rl = (RelativeLayout) findViewById(R.id.id_rl);
		// rl_helpOthers = (RelativeLayout) findViewById(R.id.id_rl_helpOthers);
		//
		// gridView = (GridView) findViewById(R.id.aor_gridview);
		// listData.add("file:///android_asset/add.png");
		// photoGridViewAdapter = new PhotoGridViewAdapter(this, listData, this,
		// "上传疾病照片");
		//
		// gridView.setAdapter(photoGridViewAdapter);

	}

	private void initListView() {
		// listview = (ListView) findViewById(R.id.id_listView);
		listview.setAdapter(new SimpleAdapter(this, listMapData,
				R.layout.item_listview, new String[] { "named", "age" },
				new int[] { R.id.id_tv_name, R.id.id_tv_age }));
		listview.setVisibility(View.GONE);
		isExtended = false;

	}

	private void initListener() {
		commonActivityTopView.ibt_back.setOnClickListener(this);
		rl.setOnClickListener(this);
		rl_helpOthers.setOnClickListener(this);

		commonActivityTopView.bt_save.setOnClickListener(this);

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Map<String, Object> map = listMapData.get(arg2);
				String infoStr = map.get("named") + " (" + map.get("sex") + ","
						+ map.get("age") + ")";
				tv_info.setText(infoStr);

				onlineQuestionUserModel.setNamed(map.get("named") + "");
				onlineQuestionUserModel.setSex(map.get("sex") + "");
				onlineQuestionUserModel.setAge((Integer) map.get("age"));

				listview.setVisibility(View.GONE);
				isExtended = false;
				iv_arrow.setImageResource(R.drawable.arrow_gray_down);
			}
		});

		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position == (listData.size() - 1)) {
					// 点击最后一个，添加照片
					pop.showAtLocation(ll_parent, Gravity.BOTTOM, 0, 0);

					// Intent intent = new Intent(OnlineQuesActivity.this,
					// MyPopWindow.class);
					// startActivityForResult(intent, 0x123);

					// 隐藏输入法
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(OnlineQuesActivity.this
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
				}

			}
		});

		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.id_rb_man:
					gender_tag = 1;
					saveMe();
					break;
				case R.id.id_rb_woman:
					gender_tag = 0;
					saveMe();

					break;
				default:
					break;
				}
			}
		});
		et_age.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					saveMe();
				}
			}
		});

	}

	public void saveMe() {
		// 先拿到本地保存的数据
		String jsonStr = SharedPrefsUtil.getValue(
				Constants.ONLINE_QUES_USERMODEL, "");
		List<OnlineQuestionUserModel> listData = JsonUtil
				.jsonTOonlinequesusermodel(jsonStr);

		if (!et_age.getText().toString().trim().equals("")) {
			OnlineQuestionUserModel model = new OnlineQuestionUserModel();
			model.setNamed("自己");
			if (gender_tag == 1) {
				model.setSex("男");
			} else if (gender_tag == 0) {
				model.setSex("女");
			}
			if (et_age.getText().toString().trim().equals("")) {
				model.setAge(0);
			} else {
				model.setAge(Integer.parseInt(et_age.getText().toString()
						.trim()));
			}
			LogUtil.i(TAG, model.toString());

			// 先删除
			if (listData.size() != 0) {
				if (ListUtil.getPosition(listData, "自己") != -1)
					listData.remove(ListUtil.getPosition(listData, "自己"));

			}
			// 再添加新的信息
			listData.add(model);
			// 再次保存
			SharedPrefsUtil.putValue(Constants.ONLINE_QUES_USERMODEL,
					listData.toString());
			LogUtil.i(TAG, listData.toString());
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ibt_back:
			finish();
			break;

		case R.id.id_bt_save:
			// 提交
			if (ll.getVisibility() == View.VISIBLE) {
				onlineQuestionUserModel.setNamed("自己");
				if (gender_tag == 0) {
					onlineQuestionUserModel.setSex("女");
				} else {
					onlineQuestionUserModel.setSex("男");
				}
				if (et_age.getText().toString().equals("")) {
					onlineQuestionUserModel.setAge(0);
				} else {
					onlineQuestionUserModel.setAge(Integer.parseInt(et_age
							.getText().toString()));
				}

			}
			if (onlineQuestionUserModel.getNamed() == null
					|| onlineQuestionUserModel.getNamed().equals("")
					|| onlineQuestionUserModel.getSex() == null
					|| onlineQuestionUserModel.getSex().equals("")
					|| onlineQuestionUserModel.getAge() <= 0
					|| onlineQuestionUserModel.getAge() > 120) {
				ToastUtil.showMessage("请先完善问诊人的信息");
			} else {
				if (!DBUtilsHelper.getInstance().isOnline()) {
					isClick = false;
					requestOnlineQuestion();
				} else {
					ToastUtil.showMessage("您当前正在进行在线咨询，结束后才能进行在线问医生哦");
					SharedPrefsUtil.putValue(Constants.CHECKEDID_RADIOBT, 1);
					Intent intent = new Intent(OnlineQuesActivity.this,
							MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("news", "news");
					startActivity(intent);
					finish();
				}
				// requestOnlineQuestion();
			}

			break;

		case R.id.id_rl:
			showORgoneListView();
			// listview.setAdapter(new SimpleAdapter(this, listMapData,
			// R.layout.item_listview, new String[] { "named", "age" },
			// new int[] { R.id.id_tv_name, R.id.id_tv_age }));

			break;
		case R.id.id_rl_helpOthers:
			// 帮别人问诊
			MobclickAgent.onEvent(OnlineQuesActivity.this,
					Constants.UMENG_EVENT_13);
			// 让et_age失去焦点
			// et_age.setFocusable(false);
			startActivity(new Intent(this, HelpOthersActivity.class));
			break;

		default:
			break;
		}
	}

	private String bodyContenet;

	private void requestOnlineQuestion() {
		bodyContenet = et_content.getText().toString().trim();
		try {
			String userStr = SharedPrefsUtil.getValue(Constants.USERMODEL, "");
			UserModel userModel = new Gson().fromJson(userStr, UserModel.class);

			// String jsonStr = SharedPrefsUtil.getValue(
			// Constants.ONLINE_QUES_USERMODEL, "");
			// List<OnlineQuestionUserModel> listData = JsonUtil
			// .jsonTOonlinequesusermodel(jsonStr);

			// 向服务器发送请求
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("SubjectType", 3);
			jsonParam.put("UserID", userModel.getID());
			jsonParam.put("UserName", userModel.getUserName());
			jsonParam.put("UserSex", onlineQuestionUserModel.getSex());
			jsonParam.put("Age", onlineQuestionUserModel.getAge());
			jsonParam.put("HeadPicture", userModel.getHeadPicture());
			jsonParam.put("RelationShip", onlineQuestionUserModel.getNamed());
			jsonParam.put("StudyID", "1000");
			jsonParam.put("AttachPics",
					new JSONArray(list_imagesUrl).toString());
			jsonParam.put("BodyContent", bodyContenet);

			LogUtil.i(TAG, onlineQuestionUserModel.getNamed() + "==="
					+ onlineQuestionUserModel.getSex() + "==="
					+ onlineQuestionUserModel.getAge());

			// JSONArray jsonArray = getJsonArray(listData);
			// // params.addBodyParameter("param", jsonArray.toString());
			// LogUtil.i(TAG, jsonArray.toString());

			HttpClient.post(Constants.ONLINE_QUESTION_SUBMIT_URl,
					jsonParam.toString(), new RequestCallBack<String>() {

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
							parseJson(responseInfo.result.toString());
							isClick = true;
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							Log.i("HttpUtil", "onFailure===" + msg);
							LogUtil.i("===============", error.toString());
							ToastUtil.showMessage("网络获取失败");
							isClick = true;
						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private MyProgressDialog myProgressDialog;
	private ChatInfoBean chatInfoBean;
	private DocInfoBean docInfoBean;

	private void parseJson(String jsonStr) {
//		if (!Chat_Dialog.timeCurl()) {
//			final AlertDialog dialog = new AlertDialog.Builder(this).create();
//			dialog.show();
//			dialog.setCanceledOnTouchOutside(false);
//			Window window = dialog.getWindow();
//			window.setContentView(R.layout.chat_dialog);// 设置对话框的布局
//			TextView msg = (TextView) window.findViewById(R.id.chat_dialog_msg);
//			String desString = "亲，非常抱歉，我们的服务时间是工作日9：00－18：00，欢迎下次来咨询，祝您身体健康！";
//			msg.setText(desString);
//			Button sure = (Button) window.findViewById(R.id.chat_dialog_sure);
//			sure.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					dialog.dismiss();
//				}
//			});
//			return;
//		}

		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			JSONObject jsonData = jsonObject.getJSONObject("Data");
			final int subjectId = jsonData.getInt("SubjectID");
			// SharedPrefsUtil.putValue(Constants.SUBJECTID, subjectId + "");
			String docInfoBeanStr = jsonData.getString("responser");
			if (null == docInfoBeanStr || docInfoBeanStr.equals("")
					|| docInfoBeanStr.equals("null")) {
				final AlertDialog dialog = new AlertDialog.Builder(this)
						.create();
				dialog.show();
				dialog.setCanceledOnTouchOutside(false);
				Window window = dialog.getWindow();
				window.setContentView(R.layout.chat_dialog);// 设置对话框的布局
				TextView msg = (TextView) window
						.findViewById(R.id.chat_dialog_msg);
				String desString = null;
				if (!Chat_Dialog.timeCurl()) {
					desString = "亲，非常抱歉，我们的服务时间是工作日9：00－18：00，欢迎下次来咨询，祝您身体健康！";
				}else {
					 desString = "亲，我们的医生都在忙碌，请稍等~";
				}
				msg.setText(desString);
				Button sure = (Button) window
						.findViewById(R.id.chat_dialog_sure);
				sure.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				return;
			}
			// 开始计时
			// TimerService.count = 0;
			docInfoBean = new Gson()
					.fromJson(docInfoBeanStr, DocInfoBean.class);
			saveDoc(docInfoBean);
			try {
				chatInfoBean = db.findFirst(Selector.from(ChatInfoBean.class)
						.where("docInfoBeanId", "=",
								docInfoBean.getVoipAccount()));
				if (null == chatInfoBean) {
					chatInfoBean = new ChatInfoBean();
					chatInfoBean.setSubjectID(subjectId + "");
					chatInfoBean.setDocInfoBeanId(docInfoBean.getVoipAccount());
					chatInfoBean.setComment(false);
				} else {
					chatInfoBean.setSubjectID(subjectId + "");
					chatInfoBean.setComment(false);
				}
				chatInfoBean.setTimeout(false);
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/*
			 * chatInfoBean = new ChatInfoBean();
			 * chatInfoBean.setSubjectID(subjectId + "");
			 * chatInfoBean.setDocInfoBeanId(docInfoBean.getVoipAccount());
			 */
			// SharedPrefsUtil.putValue(Constants.CHATINFO,
			// chatInfoBean.toString());
			if (SDKCoreHelper.getConnectState() != ECConnectState.CONNECT_SUCCESS) {
				myProgressDialog = new MyProgressDialog(OnlineQuesActivity.this);
				myProgressDialog.setMessage("正在连接对话....");
				myProgressDialog.show();
				MyApp.ConnectYunTongXun();
			}
			goIntent(chatInfoBean);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void goIntent(ChatInfoBean chatInfoBean) {
		// TODO Auto-generated method stub
		chatInfoBean.setSubjectType("3");
		chatInfoBean.setStatus(false);
		DBUtilsHelper.getInstance().saveChatinfo(chatInfoBean);
		ECDeviceKit.getIMKitManager().startConversationActivity(chatInfoBean,
				list_imagesPath, bodyContenet);
	}

	private void saveDoc(DocInfoBean docInfoBean) {
		// TODO Auto-generated method stub
		// DbUtils dbUtils = new DbUtils(null);

		try {
			db.createTableIfNotExist(DocInfoBean.class);
			db.save(docInfoBean);
			// List<DocInfoBean> docInfoBeans = db.findAll(DocInfoBean.class);
			// DocInfoBean docInfoBean2 =
			// db.findFirst(Selector.from(DocInfoBean.class).where("voipAccount","=","89077100000003"));
			// DocInfoBean docInfoBean2 = db.findById(DocInfoBean.class,
			// docInfoBean.getVoipAccount());
			// System.out.println("doc..........."+docInfoBean2.toString());
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 上传服务器
	 */
	public void requestImagesUrl() {

		try {
			// 向服务器发送请求
			JSONArray jsonArray = JsonUtil.bitmapTOjsonArray(croped_bitmap);

			HttpClient.post(Constants.ONLINE_QUESTION_IMAGES_URl,
					jsonArray.toString(), new RequestCallBack<String>() {

						@Override
						public void onStart() {
							Log.i("HttpUtil", "onStart");
							// myProgressDialog2 = new MyProgressDialog(
							// OnlineQuesActivity.this);
							// myProgressDialog2.setMessage("上传照片....");
							// myProgressDialog2.show();

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
								for (int i = 0; i < jsonArray.length(); i++) {
									list_imagesUrl.add(jsonArray.get(i)
											.toString());
								}
								list_imagesPath.add(image_path);

							} catch (JSONException e) {
								e.printStackTrace();
							}

							myProgressDialog2.dismiss();

							updateAdapter();

						}

						@Override
						public void onFailure(HttpException error, String msg) {
							Log.i("HttpUtil", "onFailure===" + msg);
							LogUtil.i("===============", error.toString());

							myProgressDialog2.dismiss();
							ToastUtil.showMessage("照片上传失败");
						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 将newbitmap加入list中，并刷新adapter
	 * 
	 * @param newBitmap
	 */
	public void updateAdapter() {
		if (croped_bitmap != null) {
			listData.add(listData.size() - 1, image_path);
			// gridView.setAdapter(new PhotoGridViewAdapter(this, listData,
			// this));
			photoGridViewAdapter.notifyDataSetChanged();
			ImageTools.recycleBitmap(croped_bitmap);

		}
	}

	private static JSONArray getJsonArray(List<Bitmap> list) {
		JSONArray jsonArray = new JSONArray();

		LogUtil.i(TAG, list.size() + "");

		for (int i = 0; i < list.size() - 1; i++) {
			try {
				// JSONObject jsonObject = new
				// JSONObject(getImageStr(list.get(i)));

				LogUtil.i(TAG, getImageStr(list.get(i)));

				jsonArray.put(getImageStr(list.get(i)));

				LogUtil.i(TAG, jsonArray.toString());

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return jsonArray;
	}

	/**
	 * 将图片转换成字符串（用Base64jar包）
	 * 
	 * @param imagepath
	 * @return
	 * @throws Exception
	 */
	public static String getImageStr(Bitmap bm) throws Exception {
		// if(new File(imagepath).exists()){
		// FileInputStream fs = new FileInputStream(imagepath);
		// ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// byte[] buffer = new byte[1024];
		// int count = 0;
		// while ((count = fs.read(buffer)) >= 0) {
		// outStream.write(buffer, 0, count);
		// }
		// String uploadBuffer = new String(Base64.encodeBase64(outStream
		// .toByteArray()));
		// outStream.close();
		// fs.close();
		// return uploadBuffer;
		// }
		// return "";

		if (bm != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bm.compress(CompressFormat.PNG, 100, bos);

			String uploadBuffer = new String(Base64.encodeBase64(bos
					.toByteArray()));
			bos.close();
			return uploadBuffer;
		}
		return "";

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// if (resultCode == 1001) {
		// Bundle bundle = data.getExtras();
		// image_path = bundle.getString("imageUrl");
		// }

		LogUtil.i("wpc", "onActivityResult");

		image_path = mypop.INonActivityResult(requestCode, data, 0);
		if (image_path == null) {
			return;
		}

		LogUtil.i("wpc", image_path);

		myProgressDialog2 = new MyProgressDialog(OnlineQuesActivity.this);
		myProgressDialog2.setMessage("上传照片....");
		myProgressDialog2.show();

		new Thread(new Runnable() {

			@Override
			public void run() {
				cropImage(image_path);
				Message msg = Message.obtain();
				msg.what = 0x123;
				handler.sendMessage(msg);
			}
		}).start();

	}

	@SuppressLint("NewApi")
	private void cropImage(String image_path) {

		croped_bitmap = ImageTools.cropBitmap(image_path);

		// feedback = cropedBitmap;

	}

	// private void crop_upload(Bitmap bitmap) {
	// Bitmap cropedBitmap = ImageTools.cropBitmap(bitmap);
	// // 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
	// if (bitmap != null && !bitmap.isRecycled()) {
	//
	// bitmap.recycle();
	// bitmap = null;
	// }
	// System.gc();
	//
	// if (cropedBitmap != null) {
	//
	// requestImagesUrl(cropedBitmap);
	// //
	// FileUploadDemo/libs/ksoap2-android-assembly-2.5.8-jar-with-dependencies.jar
	// }
	// }

	/**
	 * 接口方法，响应ListView按钮点击事件
	 */
	@Override
	public void click(View v, int position) {
		listData.remove(position);
		// gridView.setAdapter(new PhotoGridViewAdapter(this, listData, this));
		photoGridViewAdapter.notifyDataSetChanged();
		// 将list_imagesUrl中第position位置的图片URl删除
		list_imagesUrl.remove(position);
		list_imagesPath.remove(position);
	}

	@Override
	public void onPause() {
		super.onPause();

		MobclickAgent.onPageEnd("OnlineQuesActivity");
		MobclickAgent.onPause(this);
	}
}
