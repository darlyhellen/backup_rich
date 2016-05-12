package com.ytdinfo.keephealth.ui.setting;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.MainActivity;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;
import com.ytdinfo.keephealth.ui.view.CommonButton;
import com.ytdinfo.keephealth.utils.HandlerUtils;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;

public class ModifyPassActivity extends BaseActivity {
	private CommonActivityTopView commonActivityTopView;
	private EditText et_original_pass;
	private EditText et_new_pass;
	private EditText et_sure_pass;
	private CommonButton ok;

	String telephone;
	
	Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_pass);
		initView();
		initListener();

		telephone = getIntent().getStringExtra("telephone");
		
		handler = HandlerUtils.useHandler(new EditText[]{et_original_pass, et_new_pass,et_sure_pass}, ok.bt_common);
		HandlerUtils.startTimer(handler);
	}

	private void initView() {
		commonActivityTopView = (CommonActivityTopView) findViewById(R.id.id_CommonActivityTopView);
		commonActivityTopView.setTitle("修改密码");
		et_original_pass = (EditText) findViewById(R.id.id_et_original_pass);
		et_new_pass = (EditText) findViewById(R.id.id_et_new_pass);
		et_sure_pass = (EditText) findViewById(R.id.id_et_sure_pass);
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
				String original_pass = et_original_pass.getText().toString();
				String new_pass = et_new_pass.getText().toString();
				String sure_pass = et_sure_pass.getText().toString();
				if (new_pass.contains(" ")) {
					ToastUtil.showMessage("密码不能包含空格，请重新输入！");
					return;
				}
				if (original_pass.equals("")) {
					ToastUtil.showMessage("请输入原始密码");
				} else if (new_pass.length() < 6 ||	new_pass.length() >20) {
					ToastUtil.showMessage("请输入6-20位新密码");
				} else if (sure_pass.equals("")) {
					ToastUtil.showMessage("请输入确认密码");
				} else if (!sure_pass.equals(new_pass)) {
					ToastUtil.showMessage("两次输入的新密码不一致");
				} else {
					requestPassModify();
				}
			}
		});
	}

	/**
	 * 请求服务器进行密码修改
	 */
	private void requestPassModify() {

		try {
			// 向服务器发送请求
			// RequestParams params = new RequestParams();
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("UserAccount", telephone);
			jsonParam.put("Password", et_new_pass.getText().toString().trim());
			jsonParam.put("OldPassword", et_original_pass.getText().toString()
					.trim());

			// params.addBodyParameter("param", jsonParam.toString());

			HttpClient.post(Constants.MODIFYPASS_URl, jsonParam.toString(),
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
							parseJson(responseInfo.result.toString());
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
	 * 返回的json
	 * 
	 * @param json
	 */
	private void parseJson(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			boolean isSuccess = jsonObject.getBoolean("Success");
			String msg = jsonObject.getString("Message");
			if (isSuccess) {
				ToastUtil.showMessage("密码修改成功");
				SharedPrefsUtil.putValue(Constants.CHECKEDID_RADIOBT, 0);
				// 跳到首页
				Intent i = new Intent(this, MainActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
			} else {
				ToastUtil.showMessage(msg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		
		MobclickAgent.onPageStart("ModifyPassActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		
		MobclickAgent.onPageEnd("ModifyPassActivity");
		MobclickAgent.onPause(this);
	}
}
