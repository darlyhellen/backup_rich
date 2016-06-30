package com.ytdinfo.keephealth.ui.register;

import java.util.Timer;
import java.util.TimerTask;

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

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.app.MyApp;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;
import com.ytdinfo.keephealth.ui.view.CommonButton;
import com.ytdinfo.keephealth.utils.HandlerUtils;
import com.ytdinfo.keephealth.utils.NetworkReachabilityUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;

public class RegisterActivity extends BaseActivity implements OnClickListener {
	private final String TAG = "RegisterActivity";
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				ibt_yanzhengma.setText(--time_now + "s");
			}
			if (time_now <= 0) {
				resetTimer();
			}
			if (msg.what == 0x321) {
				HandlerUtils.listenET(new EditText[] { et_telephone,
						et_yanzhengma }, bt_register.bt_common);
			}
		};
	};
	private TimerTask timerTask;
	private Timer timer;
	private int time_now = 61;// 倒计时按钮上当前的时间

	private CommonButton bt_register;
	private ImageButton back, ibt_clearPass, ibt_clearPhone;
	private Button ibt_yanzhengma;
	private TextView tv_tongyixieyi;
	private EditText et_yanzhengma, et_telephone;
	private CommonActivityTopView commonActivityTopView;

	private void resetTimer() {
		timer.cancel();
		time_now = 61;
		ibt_yanzhengma.setEnabled(true);
		ibt_yanzhengma.setBackgroundResource(R.drawable.cicle_green);
		ibt_yanzhengma.setTextColor(Color.parseColor("#FF00abb0"));
		ibt_yanzhengma.setText("重新获取");

	}

	/**
	 * 验证码
	 */
	// String verificationcode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		initView();
		initListener();

		HandlerUtils.startTimer(handler);
	}

	private void initView() {
		commonActivityTopView = (CommonActivityTopView) findViewById(R.id.id_CommonActivityTopView);
		commonActivityTopView.setTitle("注册");
		back = (ImageButton) commonActivityTopView
				.findViewById(R.id.id_ibt_back);

		bt_register = (CommonButton) findViewById(R.id.id_bt_register);
		bt_register.setTitle("注 册");

		tv_tongyixieyi = (TextView) findViewById(R.id.id_tv_tongyixieyi);
		et_telephone = (EditText) findViewById(R.id.id_et_telephone);
		et_yanzhengma = (EditText) findViewById(R.id.id_et_yanzhengma);
		ibt_clearPass = (ImageButton) findViewById(R.id.id_ibt_clearPass);
		ibt_clearPhone = (ImageButton) findViewById(R.id.id_ibt_clearPhone);
		ibt_yanzhengma = (Button) findViewById(R.id.id_ibt_yanzhengma);

	}

	private void initListener() {
		// 获取验证码
		ibt_yanzhengma.setOnClickListener(this);
		// 注册
		bt_register.bt_common.setOnClickListener(this);
		// 协议
		tv_tongyixieyi.setOnClickListener(this);

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		ibt_clearPass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				et_yanzhengma.setText("");
			}
		});
		ibt_clearPhone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				et_telephone.setText("");
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ibt_yanzhengma:
			// 获取验证码
			if (et_telephone.getText().toString().trim().equals("")) {
				ToastUtil.showMessage("请输入手机号");
			} else if (et_telephone.getText().toString().trim().length() != 11) {
				ToastUtil.showMessage("请输入正确的手机号");
			} else {

				// timeDown();

				ibt_yanzhengma.setEnabled(false);

				requestVerificationCode();
			}

			break;
		case R.id.id_bt_common:
			// 注册
			if (et_telephone.getText().toString().equals("")) {
				ToastUtil.showMessage("请输入手机号");
			} else if (et_telephone.getText().toString().trim().length() != 11) {
				ToastUtil.showMessage("请输入正确的手机号");
			} else {
				if (et_yanzhengma.getText().toString().trim().equals("")) {
					ToastUtil.showMessage("请输入验证码");
				} else {
					// if(verificationcode.equals(et_yanzhengma.getText().toString().trim())){
					// // 注册成功，跳到设置密码界面
					// ToastUtil.showMessage("注册成功");
					//
					// Intent intent = new Intent();
					// intent.setClass(RegisterActivity.this,
					// SettingPassActivity.class);
					// intent.putExtra("telephone",
					// et_telephone.getText().toString()
					// .trim());
					// startActivity(intent);
					// }else {
					// ToastUtil.showMessage("验证码错误");
					// }

					requestVerificationCodeOK();
				}
			}

			// requestRegister();

			break;
		case R.id.id_tv_tongyixieyi:
			// 进入用户注册协议页面
			Intent intent = new Intent();
			intent.setClass(RegisterActivity.this,
					RegisterProtocolActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	/**
	 * 判断验证码是否正确
	 */
	private void requestVerificationCodeOK() {
		try {
			// 向服务器发送请求
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("Mobilephone", et_telephone.getText().toString()
					.trim());
			jsonParam.put("code", et_yanzhengma.getText().toString().trim());
			HttpClient.post(Constants.CHECK_VERIFICATION_URl,
					jsonParam.toString(), new RequestCallBack<String>() {

						@Override
						public void onStart() {
						}

						@Override
						public void onLoading(long total, long current,
								boolean isUploading) {
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {

							Log.i("HttpUtil", "onSuccess==="
									+ responseInfo.result.toString());
							parseJson_VerificationCodeOK(responseInfo.result
									.toString());

						}

						@Override
						public void onFailure(HttpException error, String msg) {
							Log.i("HttpUtil", "onFailure===" + msg);
							ToastUtil.showMessage("网络获取失败");
							// testTextView.setText(error.getExceptionCode() +
							// ":" +
							// msg);
						}
					});
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void parseJson_VerificationCodeOK(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONObject jsonData = jsonObject.getJSONObject("Data");
			String verificationstatus = jsonData
					.getString("verificationstatus");

			if (verificationstatus.equals("Success")) {
				// 验证码正确
				// 注册成功，跳到设置密码界面
				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this,
						SettingPassActivity.class);
				intent.putExtra("telephone", et_telephone.getText().toString()
						.trim());
				MobclickAgent.onEvent(this, Constants.UMENG_EVENT_1);
				startActivity(intent);
			} else {
				ToastUtil.showMessage("验证码错误");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 请求获取验证码
	 */
	private void requestVerificationCode() {
		try {
			// 向服务器发送请求
			if (!NetworkReachabilityUtil
					.isNetworkConnected(MyApp.getInstance())) {
				ibt_yanzhengma.setEnabled(true);
			}
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("UserAccount", et_telephone.getText().toString()
					.trim());
			HttpClient.post(Constants.VERIFICATION_URl, jsonParam.toString(),
					new RequestCallBack<String>() {
						@Override
						public void onStart() {
						}

						@Override
						public void onLoading(long total, long current,
								boolean isUploading) {
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							Log.i("HttpUtil", "onSuccess==="
									+ responseInfo.result.toString());
							parseJson_VerificationCode(responseInfo.result
									.toString());
						}

						@Override
						public void onFailure(HttpException error, String msg) {
							Log.i("HttpUtil", "onFailure===" + msg);
							ToastUtil.showMessage("网络获取失败");
							// testTextView.setText(error.getExceptionCode() +
							// ":" +
							// msg);
						}
					});
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	private void parseJson_VerificationCode(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONObject jsonData = jsonObject.getJSONObject("Data");
			boolean MobileExist = jsonData.getBoolean("MobileExist");

			if (MobileExist) {

				ToastUtil.showMessage("该用户已存在,请直接登录或选择其他手机号码进行注册。");
				ibt_yanzhengma.setEnabled(true);
			} else {
				// ibt_yanzhengma.setEnabled(false);
				// ibt_yanzhengma.setBackgroundResource(R.drawable.bt_cicle_gray);
				// ibt_yanzhengma.setTextColor(Color.parseColor("#ffffffff"));
				timeDown();
				ToastUtil.showMessage("验证码发送成功，请注意查收。");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 1分钟倒计时
	 */
	public void timeDown() {

		ibt_yanzhengma.setEnabled(false);
		ibt_yanzhengma.setBackgroundResource(R.drawable.bt_cicle_gray);
		ibt_yanzhengma.setTextColor(Color.parseColor("#ffffffff"));

		timerTask = new TimerTask() {

			@Override
			public void run() {
				Message msg = Message.obtain();
				msg.what = 0x123;
				handler.sendMessage(msg);

			}
		};
		timer = new Timer();
		timer.schedule(timerTask, 0, 1 * 1000);

	}

	@Override
	public void onResume() {
		super.onResume();

		MobclickAgent.onPageStart("RegisterActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		MobclickAgent.onPageEnd("RegisterActivity");
		MobclickAgent.onPause(this);
	}
}
