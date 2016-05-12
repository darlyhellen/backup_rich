/**下午1:37:22
 * @author zhangyh2
 * OnlineQuesActivityForV3.java
 * TODO
 */
package com.ytdinfo.keephealth.ui.OnlineVisits;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableRow;
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
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.MainActivity;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;
import com.ytdinfo.keephealth.ui.view.MyPopWindow;
import com.ytdinfo.keephealth.ui.view.MyProgressDialog;
import com.ytdinfo.keephealth.utils.Chat_Dialog;
import com.ytdinfo.keephealth.utils.DBUtilsHelper;
import com.ytdinfo.keephealth.utils.ImageLoaderUtils;
import com.ytdinfo.keephealth.utils.ImageTools;
import com.ytdinfo.keephealth.utils.JsonUtil;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;
import com.yuntongxun.ecsdk.ECDevice.ECConnectState;
import com.yuntongxun.kitsdk.ECDeviceKit;
import com.yuntongxun.kitsdk.beans.ChatInfoBean;

/**
 * @author zhangyh2 OnlineQuesActivity 上午10:32:52
 *         TODO----------V3对整个在线问诊页面进行修改。当然以前的东西暂时不进行打动
 *         。只是进行隐藏即可，将第二个页面询问他人的资料全放到本页实现
 */
public class OnlineQuesActivityForV3 extends BaseActivity implements
		OnClickListener, Callback {

	private CommonActivityTopView commonActivityTopView;

	private MyPopWindow mypop;
	private PopupWindow pop;
	private LinearLayout ll_parent;
	private GridView gridView;
	private List<String> listData = new ArrayList<String>();
	private PhotoGridViewAdapter photoGridViewAdapter;

	private List<String> list_imagesUrl = new ArrayList<String>();
	private List<String> list_imagesPath = new ArrayList<String>();

	private MyProgressDialog util;
	private Bitmap croped_bitmap;
	private String image_path;

	private ImageButton man;
	private ImageButton woman;
	private EditText et_age, et_named;
	private EditText et_content;
	private String bodyContenet;
	private TextView sex;

	private Button bt_save;
	private boolean isName;
	private boolean isAge;
	private boolean isContent;

	private TableRow choseSex;
	private RelativeLayout choseSex_check;

	private UserModel userModel;

	private DbUtils db;
	private Handler handler = new Handler() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 0x123) {
				if (croped_bitmap != null) {
					requestImagesUrl();
				}
			}
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytdinfo.keephealth.ui.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_online_report_new);
		SharedPrefsUtil.putValue("show_edit", true);
		// 判断 activity被销毁后 有没有数据被保存下来
		if (savedInstanceState != null) {
			image_path = savedInstanceState.getString("ImageFilePath");
			Log.i("wpc", "savedInstanceState===image_path===" + image_path);
			util = new MyProgressDialog(OnlineQuesActivityForV3.this);
			util.setMessage("上传照片....");
			util.show();
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
		initTitle();
		initView();
		db = DBUtilsHelper.getInstance().getDb();
		initPop();
		String jsonUserModel = SharedPrefsUtil
				.getValue(Constants.USERMODEL, "");
		userModel = new Gson().fromJson(jsonUserModel, UserModel.class);
		if (userModel != null) {
			if (!TextUtils.isEmpty(userModel.getUserName())) {
				et_named.setText(userModel.getUserName());
			}
			if (!TextUtils.isEmpty(userModel.getIDcard())) {
				String age = userModel.getIDcard().substring(6, 10);
				int year = Calendar.getInstance().get(Calendar.YEAR);
				et_age.setText(year - Integer.parseInt(age) + "");
			}
			if (!TextUtils.isEmpty(userModel.getUserSex())) {
				if (userModel.getUserSex().equals("Man")) {
					// 显示默认男
					showMan();
				} else if (userModel.getUserSex().equals("Woman")) {
					// 显示默认女51170219720510684X
					showWoman();
				} else {
				}
			}
		}
	}

	public void initPop() {
		mypop = new MyPopWindow(this);
		pop = mypop.getPop();
	}

	/**
	 * 下午1:39:55
	 * 
	 * @author zhangyh2 TODO 初始化标题
	 */
	private void initTitle() {
		// TODO Auto-generated method stub
		commonActivityTopView = (CommonActivityTopView) findViewById(R.id.report_CommonActivityTopView);
		commonActivityTopView.tv_title.setText("填写问诊信息");
		commonActivityTopView.bt_save.setText("提交");
		commonActivityTopView.bt_save
				.setBackgroundResource(R.drawable.circle_white_selector);
		commonActivityTopView.ibt_back.setOnClickListener(this);
		commonActivityTopView.bt_save.setOnClickListener(this);
		bt_save = commonActivityTopView.bt_save;
		unclickButton();
	}

	private void clickButton() {
		bt_save.setClickable(true);
		bt_save.setBackgroundResource(R.drawable.circle_white_selector);
		bt_save.setTextColor(Color.parseColor("#FFFFFFFF"));
	}

	private void unclickButton() {
		bt_save.setClickable(false);
		bt_save.setBackgroundResource(R.drawable.circle_l_white);
		bt_save.setTextColor(Color.parseColor("#77FFFFFF"));
	}

	private int editStart;
	private int editEnd;
	private int maxLen = 200; // the max byte

	/**
	 * 下午1:44:37
	 * 
	 * @author zhangyh2 TODO 初始化组件
	 */
	private void initView() {
		// TODO Auto-generated method stub
		// ------------V3进行修改
		ll_parent = (LinearLayout) findViewById(R.id.report_ll_parent);
		man = (ImageButton) findViewById(R.id.online_ibt_man);
		woman = (ImageButton) findViewById(R.id.online_ibt_woman);
		choseSex = (TableRow) findViewById(R.id.online_report_sex);
		choseSex.setOnClickListener(this);
		choseSex_check = (RelativeLayout) findViewById(R.id.online_report_sex_chose);
		sex = (TextView) findViewById(R.id.online_sex);
		et_age = (EditText) findViewById(R.id.online_et_age);
		et_age.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (s != null && !"".equals(s.toString())) {
					isAge = true;
					if (isAge && isContent && isName) {
						clickButton();
					}
				} else {
					isAge = false;
					unclickButton();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
		et_named = (EditText) findViewById(R.id.online_et_named);
		et_named.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (s != null && !"".equals(s.toString())) {
					isName = true;
					if (isAge && isContent && isName) {
						clickButton();
					}
				} else {
					isName = false;
					unclickButton();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		et_content = (EditText) findViewById(R.id.report_et_content);
		// et_content.setFilters(new InputFilter[]{new
		// InputFilter.LengthFilter(10)});
		et_content.addTextChangedListener(textWatcher);
		man.setOnClickListener(this);
		woman.setOnClickListener(this);
		// ------------V3进行修改
		gridView = (GridView) findViewById(R.id.report_aor_gridview);
		listData.add("file:///android_asset/add.png");
		photoGridViewAdapter = new PhotoGridViewAdapter(this, listData, this,
				"上传疾病照片");
		gridView.setAdapter(photoGridViewAdapter);
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
							.hideSoftInputFromWindow(
									OnlineQuesActivityForV3.this
											.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
				}

			}
		});

	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			if (s != null && !"".equals(s.toString())) {
				isContent = true;
				if (isAge && isContent && isName) {
					clickButton();
				}
			} else {
				isContent = false;
				unclickButton();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			editStart = et_content.getSelectionStart();
			editEnd = et_content.getSelectionEnd();

			// 先去掉监听器，否则会出现栈溢出
			et_content.removeTextChangedListener(textWatcher);
			if (!TextUtils.isEmpty(et_content.getText())) {
				while (s.toString().length() > maxLen + 1) {
					ToastUtil.showMessage("字数不能超过" + maxLen);
					s.delete(editStart - 1, editEnd);
					editStart--;
					editEnd--;
				}
			}
			et_content.setText(s);
			et_content.setSelection(editStart);
			// 恢复监听器
			et_content.addTextChangedListener(textWatcher);
		}

		private int calculateLength(String etstring) {
			char[] ch = etstring.toCharArray();

			int varlength = 0;
			for (int i = 0; i < ch.length; i++) {
				// changed by zyf 0825 , bug 6918，加入中文标点范围 ， TODO 标点范围有待具体化
				if ((ch[i] >= 0x2E80 && ch[i] <= 0xFE4F)
						|| (ch[i] >= 0xA13F && ch[i] <= 0xAA40)
						|| ch[i] >= 0x80) { // 中文字符范围0x4e00 0x9fbb
					varlength = varlength + 2;
				} else {
					varlength++;
				}
			}
			// 这里也可以使用getBytes,更准确嘛
			// varlength = etstring.getBytes(CharSet.forName(GBK)).lenght;//
			// 编码根据自己的需求，注意u8中文占3个字节...
			return varlength;
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("OnlineQuesActivity");
		MobclickAgent.onResume(this);
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

	@SuppressLint("NewApi")
	private void cropImage(String image_path) {
		croped_bitmap = ImageTools.cropBitmap(image_path);
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
							util.dismiss();
							updateAdapter();
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							Log.i("HttpUtil", "onFailure===" + msg);
							LogUtil.i("===============", error.toString());
							util.dismiss();
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
			photoGridViewAdapter.notifyDataSetChanged();
			ImageTools.recycleBitmap(croped_bitmap);
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
		case R.id.id_ibt_back:
			finish();
			break;
		case R.id.id_bt_save:
			// 提交
			String name = et_named.getText().toString().trim();
			String ages = et_age.getText().toString().trim();
			String mansex = sex.getText().toString().trim();
			if (name == null || "".equals(name) || mansex == null
					|| "".equals(mansex) || ages == null || "".equals(ages)) {
				ToastUtil.showMessage("请先完善问诊人的信息");
				return;
			}
			int age = Integer.parseInt(ages);
			if (age <= 0 || age > 120) {
				ToastUtil.showMessage("年龄不符合要求");
			} else {
				if (!DBUtilsHelper.getInstance().isOnline()) {
					unclickButton();
					requestOnlineQuestion(name, age, mansex);
				} else {
					ToastUtil.showMessage("您当前正在进行在线咨询，结束后才能进行在线问医生哦");
					SharedPrefsUtil.putValue(Constants.CHECKEDID_RADIOBT, 1);
					Intent intent = new Intent(OnlineQuesActivityForV3.this,
							MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("news", "news");
					startActivity(intent);
					finish();
				}
				// requestOnlineQuestion();
			}

			break;
		case R.id.online_ibt_man:
			// 男
			showMan();
			break;
		case R.id.online_ibt_woman:
			// 女
			showWoman();
			break;

		case R.id.online_report_sex:
			if (choseSex_check.isShown()) {
				choseSex_check.setVisibility(View.GONE);
			} else {
				choseSex_check.setVisibility(View.VISIBLE);
			}
			break;
		default:
			break;
		}
	}

	public void showMan() {
		sex.setText("男");
		choseSex_check.setVisibility(View.GONE);
		man.setBackgroundResource(R.drawable.boy_blue);
		woman.setBackgroundResource(R.drawable.girl_gray);
	}

	public void showWoman() {
		sex.setText("女");
		choseSex_check.setVisibility(View.GONE);
		man.setBackgroundResource(R.drawable.boy_gray);
		woman.setBackgroundResource(R.drawable.girl_red);
	}

	private void requestOnlineQuestion(String name, int age, String mansex) {
		bodyContenet = et_content.getText().toString().trim();
		try {

			// 向服务器发送请求
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("SubjectType", 3);
			jsonParam.put("UserID", "");
			jsonParam.put("UserName", name);
			jsonParam.put("UserSex", mansex);
			jsonParam.put("Age", age);
			jsonParam.put("HeadPicture", "");
			jsonParam.put("RelationShip", "自己");
			jsonParam.put("StudyID", "1000");
			jsonParam.put("AttachPics",
					new JSONArray(list_imagesUrl).toString());
			jsonParam.put("BodyContent", bodyContenet);
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
							clickButton();
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							Log.i("HttpUtil", "onFailure===" + msg);
							LogUtil.i("===============", error.toString());
							ToastUtil.showMessage("网络获取失败");
							clickButton();
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

		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			JSONObject jsonData = jsonObject.getJSONObject("Data");
			final int subjectId = jsonData.getInt("SubjectID");
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
			if (SDKCoreHelper.getConnectState() != ECConnectState.CONNECT_SUCCESS) {
				myProgressDialog = new MyProgressDialog(
						OnlineQuesActivityForV3.this);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ytdinfo.keephealth.adapter.PhotoGridViewAdapter.Callback#click(android
	 * .view.View, int) 这个方法是点击添加照片信息
	 */
	@Override
	public void click(View v, int position) {
		// TODO Auto-generated method stub
		listData.remove(position);
		// gridView.setAdapter(new PhotoGridViewAdapter(this, listData, this));
		photoGridViewAdapter.notifyDataSetChanged();
		// 将list_imagesUrl中第position位置的图片URl删除
		list_imagesUrl.remove(position);
		list_imagesPath.remove(position);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtil.i("wpc", "onActivityResult");
		image_path = mypop.INonActivityResult(requestCode, data, 0);
		if (image_path == null) {
			return;
		}

		LogUtil.i("wpc", image_path);

		util = new MyProgressDialog(OnlineQuesActivityForV3.this);
		util.setMessage("上传照片....");
		util.show();

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

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("OnlineQuesActivity");
		MobclickAgent.onPause(this);
	}
}
