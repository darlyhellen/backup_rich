package com.ytdinfo.keephealth.ui.OnlineVisits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.model.OnlineQuestionUserModel;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;
import com.ytdinfo.keephealth.utils.JsonUtil;
import com.ytdinfo.keephealth.utils.ListUtil;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;

public class HelpOthersActivity extends BaseActivity implements OnClickListener {
	// Handler handler = new Handler() {
	// public void handleMessage(Message msg) {
	// if (et_named.isShown()) {
	// if (et_named.getText().toString().equals("自己")) {
	// et_age.setHint("请输入您的年龄");
	// } else {
	// et_age.setHint("请输入" + et_named.getText().toString() + "年龄");
	// }
	//
	// } else {
	// if (tv_named.getText().toString().equals("自己")) {
	// et_age.setHint("请输入您的年龄");
	// } else {
	// et_age.setHint("请输入" + tv_named.getText().toString() + "年龄");
	// }
	//
	// }
	//
	// if (et_named.getText().toString().equals("自己")
	// || tv_named.getText().toString().equals("自己")) {
	// // 自己，显示个人资料信息
	// // 性别
	// if (me.getSex().equals("男")) {
	// showMan();
	// setManORWomanClickable(false);
	// } else if (me.getSex().equals("女")) {
	// showWoman();
	// setManORWomanClickable(false);
	// } else {
	// // 没有性别信息
	// setManORWomanClickable(true);
	// }
	// // 年龄
	// if (me.getAge() <= 0) {
	// // 没有年龄信息
	// et_age.setEnabled(true);
	// } else {
	// // 有年龄信息
	// et_age.setText(me.getAge() + "");
	// et_age.setEnabled(false);
	// }
	//
	// } else {
	// 设置性别，年龄可选
	// setManORWomanClickable(true);
	// et_age.setEnabled(true);
	// }
	// }
	// };
	private String TAG = "HelpOthersActivity";
	private CommonActivityTopView commonActivityTopView;
	private LinearLayout ll_named;

	private PopupWindow popupWindow;
	private TextView tv_named;
	private ImageButton man;
	private ImageButton woman;
	private EditText et_age, et_named;
	private ImageView iv_arrow;
	private RelativeLayout id_rl_1;

	// private Timer timer;

	private List<Map<String, Object>> listData;

	// private OnlineQuestionUserModel me = null;

	String sex = "男";
	boolean isVisible_ll_named = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_others);

		// // 先拿到本地保存的数据
		// String jsonStr = SharedPrefsUtil.getValue(
		// Constants.ONLINE_QUES_USERMODEL, "");
		// List<OnlineQuestionUserModel> listData = JsonUtil
		// .jsonTOonlinequesusermodel(jsonStr);
		// me = listData.get(ListUtil.getPosition(listData, "自己"));

		// listenET();

		initData();
		initView();
		initListener();

	}

	// private void listenET() {
	// timer = new Timer();
	// timer.schedule(new TimerTask() {
	//
	// @Override
	// public void run() {
	// handler.sendEmptyMessage(0x123);
	// }
	// }, 0, 100);
	//
	// }

	private void initData() {
		listData = new ArrayList<Map<String, Object>>();
		// if(me.getSex()==null||me.getSex().equals("")||me.getAge()<=0){
		// //自己的信息不完善
		// Map<String, Object> map0 = new HashMap<String, Object>();
		// map0.put("named", "自己");
		// listData.add(map0);
		// }
		Map<String, Object> map1 = new HashMap<String, Object>();
		map1.put("named", "妈妈");
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("named", "爸爸");
		Map<String, Object> map3 = new HashMap<String, Object>();
		map3.put("named", "宝宝");
		Map<String, Object> map4 = new HashMap<String, Object>();
		map4.put("named", "朋友");
		Map<String, Object> map5 = new HashMap<String, Object>();
		map5.put("named", "自定义");
		listData.add(map1);
		listData.add(map2);
		listData.add(map3);
		listData.add(map4);
		listData.add(map5);
	}

	private void initView() {
		commonActivityTopView = (CommonActivityTopView) findViewById(R.id.id_CommonActivityTopView);
		commonActivityTopView.tv_title.setText("添加问诊人员");
		commonActivityTopView.bt_save.setText("确定");
		commonActivityTopView.bt_save
				.setBackgroundResource(R.drawable.circle_white_selector);

		ll_named = (LinearLayout) findViewById(R.id.id_ll_named);
		tv_named = (TextView) findViewById(R.id.id_tv_named);
		man = (ImageButton) findViewById(R.id.id_ibt_man);
		woman = (ImageButton) findViewById(R.id.id_ibt_woman);
		et_age = (EditText) findViewById(R.id.id_et_age);
		et_named = (EditText) findViewById(R.id.id_et_named);
		iv_arrow = (ImageView) findViewById(R.id.id_arrow);

		id_rl_1 = (RelativeLayout) findViewById(R.id.id_rl_1);

	}

	private void initListener() {
		commonActivityTopView.ibt_back.setOnClickListener(this);
		commonActivityTopView.bt_save.setOnClickListener(this);
		ll_named.setOnClickListener(this);
		man.setOnClickListener(this);
		woman.setOnClickListener(this);
		id_rl_1.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ibt_back:
			finish();
			break;
		case R.id.id_bt_save:
			// 确定
			String named = "";
			if (isVisible_ll_named) {
				// 选择布局可见
				named = tv_named.getText().toString();

			} else {
				Pattern pattern = Pattern
						.compile("([^\\._\\w\\u4e00-\\u9fa5])*");
				Matcher matcher = pattern.matcher(et_named.getText().toString());
				String newName = matcher.replaceAll("");
				et_named.setText(newName);
				named = et_named.getText().toString().trim();
				if (newName.equals("")) {
					ToastUtil.showMessage("请先完善信息");
					break;
				}
			}
			// 先判断信息全否？
			if (et_age.getText().toString().trim().equals("")) {
				ToastUtil.showMessage("请先完善信息");
			} else if (et_age.getText().toString().trim().length() >= 3
					|| Integer.parseInt(et_age.getText().toString().trim()) <= 0
					|| Integer.parseInt(et_age.getText().toString().trim()) > 120) {
				ToastUtil.showMessage("请输入正确的年龄");
			} else {
				// 先拿到本地保存的数据
				String jsonStr = SharedPrefsUtil.getValue(
						Constants.ONLINE_QUES_USERMODEL, "");
				List<OnlineQuestionUserModel> listData = JsonUtil
						.jsonTOonlinequesusermodel(jsonStr);

				OnlineQuestionUserModel model = new OnlineQuestionUserModel();
				model.setNamed(named);
				model.setSex(sex);
				model.setAge(Integer.parseInt(et_age.getText().toString()
						.trim()));
				LogUtil.i(TAG, model.toString());

				// 先删除
				if (listData.size() != 0) {
					if (ListUtil.getPosition(listData, named) != -1)
						listData.remove(ListUtil.getPosition(listData, named));

				}
				// 再添加新的信息
				listData.add(model);
				// 再次保存
				SharedPrefsUtil.putValue(Constants.ONLINE_QUES_USERMODEL,
						listData.toString());
				LogUtil.i(TAG, listData.toString());

				SharedPrefsUtil.putValue("show_edit", false);
				finish();
				// }
			}

			break;
		case R.id.id_rl_1:
			LogUtil.i(TAG, "点击");
			if (ll_named.isShown()) {
				showPopWindow();
			}
			break;
		case R.id.id_ll_named:
			LogUtil.i(TAG, "点击");
			if (ll_named.isShown()) {
				showPopWindow();
			}
			break;
		case R.id.id_ibt_man:
			// 男
			showMan();
			break;
		case R.id.id_ibt_woman:
			// 女
			showWoman();
			break;

		default:
			break;
		}
	}

	public void showMan() {
		sex = "男";
		man.setBackgroundResource(R.drawable.boy_blue);
		woman.setBackgroundResource(R.drawable.girl_gray);
	}

	public void showWoman() {
		sex = "女";
		man.setBackgroundResource(R.drawable.boy_gray);
		woman.setBackgroundResource(R.drawable.girl_red);
	}

	public void setManORWomanClickable(boolean isClickable) {
		man.setClickable(isClickable);
		woman.setClickable(isClickable);
	}

	private void showPopWindow() {
		iv_arrow.setImageResource(R.drawable.arrow_gray_top);
		LogUtil.i(TAG, "showPopWindow()");
		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		int screenHeigh = dm.heightPixels;

		// 获取自定义布局文件activity_popupwindow.xml的视图
		View popupWindow_view = getLayoutInflater().inflate(
				R.layout.activity_popupwindow, null, false);

		// 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
		popupWindow = new PopupWindow(popupWindow_view, screenWidth / 4,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, true);

		ListView lv_popwindow = (ListView) popupWindow_view
				.findViewById(R.id.id_lv_popwindow);

		lv_popwindow.setAdapter(new SimpleAdapter(this, listData,
				R.layout.item_popwindow, new String[] { "named" },
				new int[] { R.id.id_tv_popwindow }));
		lv_popwindow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position == listData.size() - 1) {
					// 点击左后一个：自定义
					// 显示输入框
					et_named.setVisibility(View.VISIBLE);
					// 隐藏选择布局
					ll_named.setVisibility(View.GONE);
					isVisible_ll_named = false;

					et_named.setFocusable(true);
					et_named.setFocusableInTouchMode(true);
					et_named.requestFocus();

					Timer timer = new Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							InputMethodManager inputManager = (InputMethodManager) et_named
									.getContext().getSystemService(
											Context.INPUT_METHOD_SERVICE);
							inputManager.showSoftInput(et_named, 0);
						}
					}, 998);

				} else {
					tv_named.setText(listData.get(position).get("named") + "");
				}

				dismissPopWindow();
			}
		});

		// 点击其他地方消失
		popupWindow_view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				dismissPopWindow();
				return false;
			}
		});

		popupWindow.showAsDropDown(ll_named);

	}

	public void dismissPopWindow() {
		iv_arrow.setImageResource(R.drawable.arrow_gray_down);
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		MobclickAgent.onPageStart("HelpOthersActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		MobclickAgent.onPageEnd("HelpOthersActivity");
		MobclickAgent.onPause(this);
	}

}
