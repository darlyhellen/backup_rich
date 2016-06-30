package com.ytdinfo.keephealth.ui.forgetpass;

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

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;
import com.ytdinfo.keephealth.ui.view.CommonButton;
import com.ytdinfo.keephealth.utils.HandlerUtils;
import com.ytdinfo.keephealth.utils.ToastUtil;

public class YanZhengMaActivity extends BaseActivity {
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				chongxinhuoqu.setText(--time_now + "s");
			}
			if(time_now<=0){
				timer.cancel();
				//将重新获取设为原样
				chongxinhuoqu.setClickable(true);
				chongxinhuoqu.setBackgroundResource(R.drawable.cicle_green);
				chongxinhuoqu.setTextColor(Color.parseColor("#FF00abb0"));
				chongxinhuoqu.setText("重新获取");
			}
			if(msg.what==0x321){
				HandlerUtils.listenET(new EditText[]{et_yanzhengma}, ok.bt_common);
			}
		}

		
	};
	private TimerTask timerTask;
	private Timer timer;
	private int time_now = 61;// 倒计时按钮上当前的时间
	private Button chongxinhuoqu;
	private CommonActivityTopView commonActivityTopView;
	private CommonButton ok;
	private EditText et_yanzhengma;
	
	/**
	 * 验证码
	 */
//	String verificationcode;
	String telephone ;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yan_zheng_ma);

		initView();
		initListener();
		timeDown();
		
//		verificationcode = getIntent().getStringExtra("verificationcode");
		telephone= getIntent().getStringExtra("telephone");
		
		HandlerUtils.startTimer(handler);
		
	}

	private void initView() {
		commonActivityTopView = (CommonActivityTopView) findViewById(R.id.id_CommonActivityTopView);
		commonActivityTopView.setTitle("验证码");
//		time_down = (Button) findViewById(R.id.id_bt_timeDown);
		chongxinhuoqu = (Button) findViewById(R.id.id_bt_chongxinhuoqu);
		ok = (CommonButton) findViewById(R.id.id_bt_ok);
		ok.bt_common.setText("确 认");
		et_yanzhengma = (EditText) findViewById(R.id.id_et_yanzhengma);
		
	}

	private void initListener() {
		commonActivityTopView.ibt_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		chongxinhuoqu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				time_now = 61;
				timer.cancel();
				timeDown();
				
				requestVerificationCode();
			}
		});
		ok.bt_common.setOnClickListener(new OnClickListener() {
			//确认
			@Override
			public void onClick(View v) {
				Log.i(getClass().getName(), "确认");
				if(et_yanzhengma.getText().toString().trim().equals("")){
					ToastUtil.showMessage("请输入验证码");
				}else {
//					if(verificationcode.equals(et_yanzhengma.getText().toString().trim())){
//						//验证码正确
//						Intent intent = new Intent(YanZhengMaActivity.this, ResetPassActivity.class);
//						intent.putExtra("telephone", telephone);
//						startActivity(intent);
//					}else {
//						ToastUtil.showMessage("验证码错误");
//					}
					
					requestVerificationCodeOK();
					
				}
				
			}
		});
	}
	/**
	 * 请求获取验证码
	 */
	private void requestVerificationCode() {
		try {
			// 向服务器发送请求
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("UserAccount", telephone);
			HttpClient.post(Constants.FORGETPASS_VERIFICATION_URl, jsonParam.toString(),new RequestCallBack<String>() {

						@Override
						public void onStart() {
						}

						@Override
						public void onLoading(long total, long current,
								boolean isUploading) {
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							
							Log.i("HttpUtil", "onSuccess==="+ responseInfo.result.toString());
//							parseJson_VerificationCode(responseInfo.result.toString());
							ToastUtil.showMessage("验证码发送成功，请注意查收。");
						}

						

						@Override
						public void onFailure(HttpException error, String msg) {
							Log.i("HttpUtil", "onFailure==="+msg);
							ToastUtil.showMessage("网络获取失败");
							// testTextView.setText(error.getExceptionCode() + ":" +
							// msg);
						}
					});
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 1分钟倒计时
	 */
	public void timeDown() {
		//设置重新获取为不可用
		chongxinhuoqu.setClickable(false);
		chongxinhuoqu.setBackgroundResource(R.drawable.gray_fillet);
		chongxinhuoqu.setTextColor(Color.parseColor("#FFffffff"));
		
		

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
	
	/**
	 * 判断验证码是否正确
	 */
	private void requestVerificationCodeOK() {
		try {
			// 向服务器发送请求
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("Mobilephone", telephone);
			jsonParam.put("code", et_yanzhengma.getText().toString().trim());
			HttpClient.post(Constants.CHECK_VERIFICATION_URl, jsonParam.toString(),new RequestCallBack<String>() {

						@Override
						public void onStart() {
						}

						@Override
						public void onLoading(long total, long current,
								boolean isUploading) {
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							
							Log.i("HttpUtil", "onSuccess==="+ responseInfo.result.toString());
							parseJson_VerificationCodeOK(responseInfo.result.toString());
							
						}

						

						@Override
						public void onFailure(HttpException error, String msg) {
							Log.i("HttpUtil", "onFailure==="+msg);
							ToastUtil.showMessage("网络获取失败");
							// testTextView.setText(error.getExceptionCode() + ":" +
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
			String verificationstatus = jsonData.getString("verificationstatus");
			
			if(verificationstatus.equals("Success")){
				//验证码正确
				Intent intent = new Intent(YanZhengMaActivity.this, ResetPassActivity.class);
				intent.putExtra("telephone", telephone);
				startActivity(intent);
			}else {
				ToastUtil.showMessage("验证码错误");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		MobclickAgent.onPageStart("YanZhengMaActivity"); 
		MobclickAgent.onResume(this); 
	}

	@Override
	protected void onPause() {
		super.onPause();

		MobclickAgent.onPageEnd("YanZhengMaActivity");
		MobclickAgent.onPause(this);
	}
	
}
