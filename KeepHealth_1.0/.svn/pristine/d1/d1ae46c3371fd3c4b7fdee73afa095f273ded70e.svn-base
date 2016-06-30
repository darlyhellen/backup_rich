package com.ytdinfo.keephealth.ui.forgetpass;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

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

public class FindPassActivity extends BaseActivity {
	private ImageButton back;
	private EditText et_phone;
	private CommonButton bt_yanzhengma;
	private CommonActivityTopView  commonActivityTopView; 
	Handler handler;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_pass);
		initView();
		initListener();
		
		handler = HandlerUtils.useHandler(new EditText[]{et_phone}, bt_yanzhengma.bt_common);
		HandlerUtils.startTimer(handler);
	}

	private void initView() {
		commonActivityTopView = (CommonActivityTopView) findViewById(R.id.id_CommonActivityTopView);
		commonActivityTopView.setTitle("找回密码");
		back = (ImageButton) commonActivityTopView.findViewById(R.id.id_ibt_back);
		bt_yanzhengma = (CommonButton) findViewById(R.id.id_bt_yanzhengma);
		bt_yanzhengma.setTitle("获取验证码");
		et_phone = (EditText) findViewById(R.id.id_et_telephone);
	}

	private void initListener() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		bt_yanzhengma.bt_common.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String phone = et_phone.getText().toString().trim();
				if(phone.equals("")){
					ToastUtil.showMessage("请输入手机号");
				}else if (phone.length()!=11) {
					ToastUtil.showMessage("请输入正确的手机号");
				}else {
					bt_yanzhengma.bt_common.setEnabled(false);
					requestVerificationCode();
					
//					Intent intent = new Intent();
//					intent.setClass(FindPassActivity.this, YanZhengMaActivity.class);
//					intent.putExtra("telephone", et_phone.getText().toString().trim());
//					startActivity(intent);
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
			jsonParam.put("UserAccount", et_phone.getText().toString().trim());
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
							parseJson_VerificationCode(responseInfo.result.toString());
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
	
	private void parseJson_VerificationCode(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONObject jsonData = jsonObject.getJSONObject("Data");
			boolean MobileExist = jsonData.getBoolean("MobileExist");
			
			if(MobileExist){
				ToastUtil.showMessage("验证码发送成功，请注意查收。");
//				String verificationcode = jsonData.getString("verificationcode");
				
				Intent intent = new Intent();
				intent.setClass(FindPassActivity.this, YanZhengMaActivity.class);
//				intent.putExtra("verificationcode", verificationcode);
				intent.putExtra("telephone", et_phone.getText().toString().trim());
				startActivity(intent);
				
			}else {
				ToastUtil.showMessage("该手机号未注册，请先注册");
				bt_yanzhengma.bt_common.setEnabled(true);
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		bt_yanzhengma.bt_common.setEnabled(true);
		
		MobclickAgent.onPageStart("FindPassActivity"); 
		MobclickAgent.onResume(this); 
	}

	@Override
	protected void onPause() {
		super.onPause();

		MobclickAgent.onPageEnd("FindPassActivity");
		MobclickAgent.onPause(this);
	}
}
