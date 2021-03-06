package com.ytdinfo.keephealth.ui;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.listeners.Listeners.CommListener;
import com.umeng.comm.core.nets.Response;
import com.umeng.common.ui.util.FontUtils;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DialogActivity extends BaseActivity implements OnClickListener{
	
	private Button sure;
	private Button cancel;
	private EditText extra;
	private TextView message;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_is_user_current);
		initView();
	}
	
	
	private void  initView()
	{
		sure=(Button)findViewById(R.id.sure);
		cancel=(Button)findViewById(R.id.cancel);
		extra=(EditText)findViewById(R.id.extra);
		message=(TextView)findViewById(R.id.message);
		int len=CommConfig.getConfig().loginedUser.name.length();
		String messageStr = "欢迎访问帮忙医社区，您正在使用的用户名 “"
				+ CommConfig.getConfig().loginedUser.name
				+ "” 有可能是您的真实姓名，为保护您的隐私，您可以在下面设置昵称用于社区交流。";
		SpannableString spanString = new SpannableString(messageStr); 		
	    spanString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.checked_text_name)), 
	    		20, 20+len+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); 
	    message.setText(spanString);
	    sure.setOnClickListener(this);
	    cancel.setOnClickListener(this);
	    cancel.setText("返回");
	    sure.setText("进入社区");
	    extra.setText(CommConfig.getConfig().loginedUser.name);
		FontUtils.changeTypeface(getWindow().getDecorView());
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId())
		{
		case R.id.sure:
			sure();
			break;
		case R.id.cancel:
			cancel();
			break;
		}
	}
	
	
	private void cancel()
	{
		//setResult(MainActivity.oldCheckId);
		this.finish();
	}
	
	private void sure()
	{
		// TODO Auto-generated method stub
		final String name = extra.getText().toString();
		if (CommConfig.getConfig().loginedUser.name
						.equals(name)) {// 用户名为空
			SharedPrefsUtil
					.putValue(
							Constants.IS_USER_NAME_AS_NICK_NAME,
							true);
			//setResult();
			DialogActivity.this.finish();
		} else {
			if (name.length() < 4
					|| name.length() > 14) {
				ToastUtil.showMessage(
						"名字长度不符合规则，长度大于4，小于15个。",
						1000);
				return;
			}
			CommUser user = CommConfig.getConfig().loginedUser;
			user.name = name;
			CommunitySDKImpl.getInstance()
					.updateUserProfile(user,
							new CommListener() {
								@Override
								public void onStart() {
								}

								@Override
								public void onComplete(
										Response arg0) {
									// TODO
									if (Response.NO_ERROR == arg0.errCode)
										 requestModifyInfo(name);

								}
							});
		}

	}

 
	
	/**
	 * 将修改后的信息上传服务器
	 */
	public  void requestModifyInfo(final String nickName) {

		try {
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("photo", "");
			jsonParam.put("fileName", "");
			String userJson = SharedPrefsUtil.getValue(Constants.USERMODEL, "");
			UserModel userModel = new Gson()
					.fromJson(userJson, UserModel.class);
			jsonParam.put("sex", userModel.getUserSex());
			jsonParam.put("Name", userModel.getUserName());
			jsonParam.put("Addition1", nickName);
			jsonParam.put("IDcard", userModel.getIDcard());
			jsonParam.put("UserAccount", userModel.getUserAccount());
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
							// 存本地
							UserModel userModel = new Gson().fromJson(
									SharedPrefsUtil.getValue(
											Constants.USERMODEL, ""),
									UserModel.class);
							userModel.setAddition1(nickName);
							SharedPrefsUtil.putValue(Constants.USERMODEL,
									userModel.toString());
							SharedPrefsUtil.putValue(
									Constants.IS_USER_NAME_AS_NICK_NAME, false);
							setResult(R.id.tab_rb_4);
							DialogActivity.this.finish();
						}

						@Override
						public void onFailure(HttpException error, String msg) {

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
	

}
