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

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.login.LoginActivity;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;
import com.ytdinfo.keephealth.ui.view.CommonButton;
import com.ytdinfo.keephealth.utils.HandlerUtils;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;

public class ResetPassActivity extends BaseActivity {
	private CommonActivityTopView commonActivityTopView;
	private EditText et_pass;
	private CommonButton ok;

	String telephone;
	
	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_pass);
		initView();
		initListener();

		telephone = getIntent().getStringExtra("telephone");
		
		handler = HandlerUtils.useHandler(new EditText[]{et_pass}, ok.bt_common);
		HandlerUtils.startTimer(handler);
	}

	private void initView() {
		commonActivityTopView = (CommonActivityTopView) findViewById(R.id.id_CommonActivityTopView);
		commonActivityTopView.setTitle("重置密码");
		et_pass = (EditText) findViewById(R.id.id_et_pass);
		ok = (CommonButton) findViewById(R.id.id_bt_ok);
		ok.bt_common.setText("确 认");
	}

	private void initListener() {
		commonActivityTopView.ibt_back
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});
		ok.bt_common.setOnClickListener(new OnClickListener() {
			// 确认
			@Override
			public void onClick(View v) {
				if(et_pass.getText().toString().length()<6||et_pass.getText().toString().length()>20){
					ToastUtil.showMessage("请输入6-20位的密码");
				}else {
					requestPassSetting();
				}
				
			}
		});
	}

	/**
	 * 请求服务器进行密码设置
	 */
	private void requestPassSetting() {

		try {
			// 向服务器发送请求
//			RequestParams params = new RequestParams();
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("UserAccount", telephone);
			jsonParam.put("Password", et_pass.getText().toString().trim());
//			params.addBodyParameter("param", jsonParam.toString());

			HttpUtils http = new HttpUtils();
			HttpClient.post(Constants.FORGETPASS_RESETPASS_URl,
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
							parseJson_setPass(responseInfo.result.toString());
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

	/**
	 * 设置密码返回的json
	 * 
	 * @param json
	 */
	private void parseJson_setPass(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			boolean isSuccess = jsonObject.getBoolean("Success");
			LogUtil.i("wpc", "isSuccess==="+isSuccess);
			if (isSuccess) {
				ToastUtil.showMessage("密码设置成功");
				// 跳到登录界面
				Intent i = new Intent(this, LoginActivity.class);
				startActivity(i);
			}else {
				ToastUtil.showMessage("密码设置失败");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		MobclickAgent.onPageStart("ResetPassActivity"); 
		MobclickAgent.onResume(this); 
	}

	@Override
	protected void onPause() {
		super.onPause();

		MobclickAgent.onPageEnd("ResetPassActivity");
		MobclickAgent.onPause(this);
	}
}
