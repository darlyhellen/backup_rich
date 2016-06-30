package com.ytdinfo.keephealth.ui.register;

import java.util.List;

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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.app.MyApp;
import com.ytdinfo.keephealth.model.UserGroupBean;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.MainActivity;
import com.ytdinfo.keephealth.ui.login.LoginActivity;
import com.ytdinfo.keephealth.ui.personaldata.PersonalDataActivity;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;
import com.ytdinfo.keephealth.ui.view.CommonButton;
import com.ytdinfo.keephealth.ui.view.MyProgressDialog;
import com.ytdinfo.keephealth.utils.DBUtilsHelper;
import com.ytdinfo.keephealth.utils.HandlerUtils;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.ECMessage.MessageStatus;
import com.yuntongxun.ecsdk.im.ECGroup;
import com.yuntongxun.ecsdk.im.ECGroup.Permission;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuntongxun.kitsdk.ECDeviceKit;
import com.yuntongxun.kitsdk.db.ConversationSqlManager;
import com.yuntongxun.kitsdk.db.GroupSqlManager;

public class SettingPassActivity extends BaseActivity implements
		OnClickListener {
	private ImageButton back;
	private ImageButton clearPass;
	private EditText et_pass1;
	private EditText et_pass2;
	private ImageButton ibt_clearPass1;
	private ImageButton ibt_clearPass2;
	private CommonActivityTopView commonActivityTopView;
	private CommonButton ok;

	private String telephone;

	Handler handler;

	private MyProgressDialog myProgressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_pass);

		Intent intent = getIntent();
		telephone = intent.getStringExtra("telephone");

		initView();
		initListener();

		handler = HandlerUtils.useHandler(
				new EditText[] { et_pass1, et_pass2 }, ok.bt_common);
		HandlerUtils.startTimer(handler);
	}

	private void initView() {
		commonActivityTopView = (CommonActivityTopView) findViewById(R.id.id_CommonActivityTopView);
		commonActivityTopView.setTitle("设置密码");
		back = (ImageButton) commonActivityTopView
				.findViewById(R.id.id_ibt_back);
		et_pass1 = (EditText) findViewById(R.id.id_et_pass1);
		et_pass2 = (EditText) findViewById(R.id.id_et_pass2);
		ibt_clearPass1 = (ImageButton) findViewById(R.id.id_ib_clearPass1);
		ibt_clearPass2 = (ImageButton) findViewById(R.id.id_ib_clearPass2);
		ok = (CommonButton) findViewById(R.id.id_bt_ok);
		ok.bt_common.setText("确 认");
	}

	private void initListener() {
		// 确认
		ok.bt_common.setOnClickListener(this);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		ibt_clearPass1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				et_pass1.setText("");
			}
		});
		ibt_clearPass2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				et_pass2.setText("");
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_bt_common:
			// 确认
			String pass1 = et_pass1.getText().toString().trim();
			String pass2 = et_pass2.getText().toString().trim();
			if (pass1.length() < 6 || pass1.length() > 20) {
				ToastUtil.showMessage("请输入6-20位密码");
			} else {
				if (pass2.equals("")) {
					ToastUtil.showMessage("请输入确认密码");
				} else if (!pass1.equals(pass2)) {
					ToastUtil.showMessage("两次密码不一致");
				} else {
					requestPassSetting();
				}
			}

			break;

		default:
			break;
		}
	}

	/**
	 * 请求服务器进行密码设置
	 */
	private void requestPassSetting() {
		myProgressDialog = new MyProgressDialog(SettingPassActivity.this);
		myProgressDialog.setMessage("正在登录...");
		try {
			// 向服务器发送请求
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("UserAccount", telephone);
			jsonParam.put("Password", et_pass1.getText().toString().trim());
			HttpClient.post(Constants.SETPASS_URl, jsonParam.toString(),
					new RequestCallBack<String>() {
						@Override
						public void onStart() {
							Log.i("HttpUtil", "onStart");
							myProgressDialog.show();
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
							myProgressDialog.dismiss();
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
			String msg = jsonObject.getString("Message");
			if (isSuccess) {
				ToastUtil.showMessage("密码设置成功");
				// 按照需求，进行直接登录。
				requestLogin(telephone, et_pass1.getText().toString().trim());
				// // 跳到登录界面
				// Intent i = new Intent(this, LoginActivity.class);
				// startActivity(i);
			}else {
				ToastUtil.showMessage(msg);
				myProgressDialog.dismiss();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			myProgressDialog.dismiss();
		}
	}

	/** 用户Token标示 */
	String token;
	/** 用户登录状态==Fail */
	String loginStatus;
	/** 用户登录信息 */
	String message;
	private DbUtils dbUtil;

	/**
	 * @param userString
	 * @param passwordString
	 *            下午4:44:57
	 * @author zhangyh2 SettingPassActivity.java TODO 直接调用登录接口
	 */
	private void requestLogin(String userString, String passwordString) {

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("UserAccount", userString);
			jsonObject.put("Password", passwordString);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		HttpClient.post(Constants.LOGIN_URl, jsonObject.toString(),
				new RequestCallBack<String>() {
					@Override
					public void onStart() {
						handler.sendEmptyMessage(0x342);
					}

					@Override
					public void onSuccess(final ResponseInfo<String> arg0) {
						myProgressDialog.dismiss();
						LogUtil.i("login", "onsuccess-----" + arg0.result);
						analyzeJson(arg0.result);
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						myProgressDialog.dismiss();
						LogUtil.e("login", arg1.toString());
						ToastUtil.showMessage("网络请求失败");
						handler.sendEmptyMessage(0x341);
					}
				});
	}

	/**
	 * 解析json
	 * 
	 * @param json
	 */
	private void analyzeJson(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			loginStatus = jsonObject.getString("Loginstatus");
			message = jsonObject.getString("Message");
			if (!loginStatus.equals("Fail")) {
				String token = jsonObject.getString("Token");
				String usermodel = jsonObject.getString("UserModel");
				String userId = jsonObject.getJSONObject("UserModel")
						.getString("ID");
				SharedPrefsUtil.putValue(Constants.TOKEN, token);
				SharedPrefsUtil.putValue(Constants.USERID, userId);
				SharedPrefsUtil.putValue(Constants.USERMODEL, usermodel);
				String s = SharedPrefsUtil.getValue(Constants.TOKEN, null);
				//连接云通讯
			    MyApp.ConnectYunTongXun();
			 //   requestGetUserGroup();
				// 跳到首页
				UserModel userModel = new Gson().fromJson(usermodel,
						UserModel.class);
				dbUtil = DBUtilsHelper.getInstance().getDb();

				Intent i;
				if (null == userModel.getIDcard()
						|| "".equals(userModel.getIDcard())) {
					i = new Intent(this, PersonalDataActivity.class);
				} else {
					i = new Intent(this, MainActivity.class);
					SharedPrefsUtil.putValue(Constants.CHECKEDID_RADIOBT, 0);  
				}
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
			
			} else {
				handler.sendEmptyMessage(0x341);
				Intent intent = new Intent(this, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(0x341);
		}
	}
	 

	private void requestGetUserGroup() {
		// TODO Auto-generated method stub
		HttpClient.get(SettingPassActivity.this, Constants.GETUSERGROUPS_URL,
				new RequestParams(), new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							JSONObject data = jsonObject.getJSONObject("Data");
							String statusCode = data.getString("statusCode");
							if (statusCode.equals("000000")) {
								List<UserGroupBean> list = new Gson().fromJson(
										data.getString("groups"),
										new TypeToken<List<UserGroupBean>>() {
										}.getType());
								for (int i = 0; i < list.size(); i++) {
									UserGroupBean userGroupBean = list.get(i);
									if(!GroupSqlManager.isExitGroup(userGroupBean.getGroupId()))
									{
										ECGroup group =new ECGroup();
										group.setGroupId(userGroupBean.getGroupId());
										group.setName(userGroupBean.getName());
										group.setDateCreated(userGroupBean.getDateCreated());
										group.setGroupType(Integer.parseInt(userGroupBean.getType()));
										group.setCount(Integer.parseInt(userGroupBean.getCount()));
										group.setPermission(Permission.PRIVATE);
									 
										GroupSqlManager.insertGroup(group, true, false);
										  ECMessage msg = ECMessage.createECMessage(ECMessage.Type.TXT);
									        //设置消息的属性：发出者，接受者，发送时间等
									        msg.setForm(ECDeviceKit.getInstance().getUserId());
									        msg.setMsgTime(System.currentTimeMillis());
									        // 设置消息接收者
									        msg.setTo(userGroupBean.getGroupId());
									        msg.setSessionId(userGroupBean.getGroupId());
									        // 设置消息发送类型（发送或者接收）
									        msg.setDirection(ECMessage.Direction.RECEIVE);
									        // 创建一个文本消息体，并添加到消息对象中
									        ECTextMessageBody msgBody = new ECTextMessageBody("");
									    	// 调用接口发送IM消息
											msg.setMsgTime(System.currentTimeMillis());
										  msg.setBody(msgBody);
										  msg.setMsgStatus(MessageStatus.SUCCESS);
										 // IMChattingHelper.sendECMessage(msg);
 									  ConversationSqlManager.insertSessionRecord(msg); 
										
									}
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub

					}
				});
	} 
 

	/**
	 * @param userGroupBean
	 *            下午4:51:44
	 * @author zhangyh2 SettingPassActivity.java TODO 保存用户族群信息
	 */
	private void saveGroupBean(UserGroupBean userGroupBean) {
		// TODO Auto-generated method stub

		try {
			dbUtil.createTableIfNotExist(UserGroupBean.class);
			dbUtil.save(userGroupBean);
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

	@Override
	public void onResume() {
		super.onResume();

		MobclickAgent.onPageStart("SettingPassActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		MobclickAgent.onPageEnd("SettingPassActivity");
		MobclickAgent.onPause(this);
	}
}
