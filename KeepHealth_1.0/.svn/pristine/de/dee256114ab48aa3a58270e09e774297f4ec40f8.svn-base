package com.ytdinfo.keephealth.ui.personaldata;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.utils.IDCardUtils;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.MathUtils;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;

public class CommonModifyInfoActivity extends BaseActivity {
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
				bt_save.setClickable(true);
				bt_save.setBackgroundResource(R.drawable.circle_white_selector);
				bt_save.setTextColor(Color.parseColor("#FFFFFFFF"));
			}
		};
	};
	private Intent intent;

	private String title;
	private String desc;

	public TextView tv_title;
	public Button bt_save;
	public EditText et;
	public ImageButton ibt_clear, ibt_back;

	private TimerTask timerTask;
	private Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_modify_info);
		initView();
		initListener();
		listenETisempty();
	}

	/**
	 * 监听edittext的内容是否为空， 为空：保存按钮不可点击 不为空：保存按钮可点击
	 */
	public void listenETisempty() {
		timerTask = new TimerTask() {

			@Override
			public void run() {
				if (et.getText().toString().equals("")
						|| et.getText().toString() == null) {
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

	private void initView() {
		intent = getIntent();

		title = intent.getStringExtra("title");
		desc = intent.getStringExtra("desc");

		tv_title = (TextView) findViewById(R.id.id_tv_title);
		tv_title.setText(title);
		bt_save = (Button) findViewById(R.id.id_bt_save);
		et = (EditText) findViewById(R.id.id_et);
		et.setHint(title);
		et.setText(desc);

		ibt_clear = (ImageButton) findViewById(R.id.id_ibt_clear);
		ibt_back = (ImageButton) findViewById(R.id.id_ibt_back);

	}

	private void initListener() {
		ibt_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 保存
		bt_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (title.equals("身份证号")) {
					// if(et.getText().toString().length()!=18){
					if (!IDCardUtils.IDCardValidate(
							et.getText().toString().trim()).equals("")) {
						ToastUtil.showMessage("请输入正确的身份证号");
					} else {
						// 上传到服务器
						requestModifyInfo();
					}
				} else if (title.equals("手机号码")) {
					if (et.getText().toString().length() != 11) {
						ToastUtil.showMessage("请输入正确的手机号");
					} else {
						// 上传到服务器
						requestModifyInfo();
					}
				} else {
					Pattern pattern = Pattern
							.compile("([^\\._\\w\\u4e00-\\u9fa5])*");
					Matcher matcher = pattern.matcher(et.getText().toString());
					String newName = matcher.replaceAll("");
					et.setText(newName);
					if (newName.length() > 6 || newName.length() == 0) {
						ToastUtil.showMessage("请输入6位以下的姓名");
					} else {
						// 上传到服务器
						requestModifyInfo();
					}
				}

			}
		});
		ibt_clear.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				et.setText("");
			}
		});
	}

	/**
	 * 将修改后的信息上传服务器
	 */
	public void requestModifyInfo() {

		try {

			// 向服务器发送请求
			// RequestParams params = new RequestParams();
			// params.addHeader(Constants.TOKEN,
			// SharedPrefsUtil.getValue(Constants.TOKEN, ""));

			JSONObject jsonParam = new JSONObject();
			jsonParam.put("photo", "");
			jsonParam.put("fileName", "");
			String userJson = SharedPrefsUtil.getValue(Constants.USERMODEL, "");
			UserModel userModel = new Gson()
					.fromJson(userJson, UserModel.class);
			// String userSex = "";
			// if(userModel.getUserSex().equals("男")){
			// userSex = "Man";
			// }else if(userModel.getUserSex().equals("女")){
			// userSex ="Woman";
			// }
			jsonParam.put("sex", userModel.getUserSex());

			if (title.equals("姓名")) {

				jsonParam.put("Name", et.getText().toString().trim());

				jsonParam.put("IDcard", userModel.getIDcard());

				jsonParam.put("UserAccount", userModel.getUserAccount());

			} else if (title.equals("身份证号")) {
				jsonParam.put("Name", userModel.getUserName());

				jsonParam.put("IDcard", et.getText().toString().trim());

				jsonParam.put("UserAccount", userModel.getUserAccount());

			} else {
				jsonParam.put("Name", userModel.getUserName());

				jsonParam.put("IDcard", userModel.getIDcard());

				jsonParam.put("UserAccount", et.getText().toString().trim());
			}

			// params.addBodyParameter("param", jsonParam.toString());

			LogUtil.i("=====", "===" + userModel.getUserSex() + "===");
			LogUtil.i("=====", jsonParam.toString());

			// params.setBodyEntity(new StringEntity(jsonParam.toString()));
			// HttpUtils httpUtils = new HttpUtils();
			HttpClient.post(Constants.MODIFYINFO_URl, jsonParam.toString(),
					new RequestCallBack<String>() {

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
							ToastUtil.showMessage("修改成功");

							// 存本地
							UserModel userModel = new Gson().fromJson(
									SharedPrefsUtil.getValue(
											Constants.USERMODEL, ""),
									UserModel.class);
							if (title.equals("姓名")) {
								// 存入修改后的姓名
								userModel.setUserName(et.getText().toString()
										.trim());

							} else if (title.equals("身份证号")) {
								// 存入修改后的身份证号
								userModel.setIDcard(et.getText().toString()
										.trim());

								// 存入年龄
								userModel.setAge(MathUtils.calculateAge(et
										.getText().toString().trim()));

								// SharedPrefsUtil.putValue(Constants.USERMODEL,
								// userModel.toString());
							} else {
								// 存入修改后的手机号
								// userModel.setMobilephone(et.getText().toString()
								// .trim());

							}

							SharedPrefsUtil.putValue(Constants.USERMODEL,
									userModel.toString());

							Log.i("HttpUtil", "存入手机");

							finish();

						}

						@Override
						public void onFailure(HttpException error, String msg) {
							Log.i("HttpUtil", "onFailure===" + msg);
							LogUtil.i("===============", error.toString());
							ToastUtil.showMessage("网络获取失败");
							LogUtil.i("=================",
									Constants.MODIFYINFO_URl);
							// testTextView.setText(error.getExceptionCode() +
							// ":" +
							// msg);
						}
					});

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onResume() {
		super.onResume();

		MobclickAgent.onPageStart("CommonModifyInfoActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		MobclickAgent.onPageEnd("CommonModifyInfoActivity");
		MobclickAgent.onPause(this);
	}
}
