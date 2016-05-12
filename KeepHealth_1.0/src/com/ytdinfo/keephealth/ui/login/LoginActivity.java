package com.ytdinfo.keephealth.ui.login;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.rayelink.eckit.MainChatControllerListener;
import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.app.MyApp;
import com.ytdinfo.keephealth.model.TBNews;
import com.ytdinfo.keephealth.model.UserGroupBean;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.MainActivity;
import com.ytdinfo.keephealth.ui.forgetpass.FindPassActivity;
import com.ytdinfo.keephealth.ui.personaldata.PersonalDataActivity;
import com.ytdinfo.keephealth.ui.register.RegisterActivity;
import com.ytdinfo.keephealth.ui.view.CommonButton;
import com.ytdinfo.keephealth.ui.view.MyProgressDialog;
import com.ytdinfo.keephealth.utils.DBUtil;
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
import com.yuntongxun.kitsdk.ui.chatting.model.IMChattingHelper;

public class LoginActivity extends BaseActivity implements OnClickListener {
	public static final String TAG = "LoginActivity";

	private ImageButton ib_close1;
	private ImageButton ib_close2;
	private ImageView iv11;
	private ImageView back;
	private EditText user, password;
	private CommonButton login;
	private RelativeLayout register;
	private RelativeLayout rl_forget_pass;
	/** 用户Token标示 */
	String token;
	/** 用户登录状态==Fail */
	String loginStatus;
	/** 用户登录信息 */
	String message;

	String userString;
	String passwordString;
	private DbUtils dbUtil;

	Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		initView();
		initListener();

		handler = HandlerUtils.useHandler(new EditText[] { user, password },
				login.bt_common);
		HandlerUtils.startTimer(handler);

		MobclickAgent.setDebugMode(true);
	}

	private void initView() {
		ib_close1 = (ImageButton) findViewById(R.id.id_ib_close1);
		ib_close2 = (ImageButton) findViewById(R.id.id_ib_close2);
		iv11 = (ImageView) findViewById(R.id.iv11);
		back = (ImageView) findViewById(R.id.login_back);
		user = (EditText) findViewById(R.id.et_user);
		password = (EditText) findViewById(R.id.et_password);
		login = (CommonButton) findViewById(R.id.bt_login);
		login.bt_common.setText("登 录");
		register = (RelativeLayout) findViewById(R.id.id_rl_register);
		rl_forget_pass = (RelativeLayout) findViewById(R.id.id_rl_forget_pass);
	}

	private void initListener() {
		login.bt_common.setOnClickListener(this);
		register.setOnClickListener(this);
		ib_close1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				user.setText("");
			}
		});
		ib_close2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				password.setText("");
			}
		});

		iv11.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {// 隐藏输入法
				try {
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(LoginActivity.this
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
				} catch (Exception e) {
				}
			}
		});

		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
		rl_forget_pass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, FindPassActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_bt_common:
			// 登录
			userString = user.getText().toString().trim();
			passwordString = password.getText().toString().trim();
			if (userString.equals("")) {
				ToastUtil.showMessage("请输入手机号");
			} else if (userString.length() != 11) {
				ToastUtil.showMessage("请输入正确的手机号");
			} else if (passwordString.equals("")) {
				ToastUtil.showMessage("请输入密码");
			} else {
				requestLogin(userString, passwordString);
			}
			break;
		case R.id.id_rl_register:
			// 注册
			Intent intent = new Intent();
			intent.setClass(LoginActivity.this, RegisterActivity.class);
			startActivity(intent);

			break;

		default:
			break;
		}
	}

	private MyProgressDialog myProgressDialog;

	private void requestLogin(String userString, String passwordString) {
		myProgressDialog = new MyProgressDialog(LoginActivity.this);
		myProgressDialog.setMessage("正在登录...");

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
						super.onStart();
						myProgressDialog.show();
						handler.sendEmptyMessage(0x342);
					}

					@Override
					public void onSuccess(final ResponseInfo<String> arg0) {
						myProgressDialog.dismiss();
						LogUtil.i(TAG, "onsuccess-----" + arg0.result);
						analyzeJson(arg0.result);
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						myProgressDialog.dismiss();
						LogUtil.e(TAG, arg1.toString());
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
//				String userId = jsonObject.getJSONObject("UserModel")
//						.getString("ID");
				String voip=jsonObject.getJSONObject("UserModel")
						.getString("voipAccount");
				SharedPrefsUtil.putValue(Constants.TOKEN, token);
				SharedPrefsUtil.putValue(Constants.USERID, voip);
				SharedPrefsUtil.putValue(Constants.USERMODEL, usermodel);
				String s = SharedPrefsUtil.getValue(Constants.TOKEN, null);
				//连接云通讯
			    MyApp.ConnectYunTongXun();
			   // requestGetUserGroup();
				// 跳到首页
				UserModel userModel = new Gson().fromJson(usermodel,
						UserModel.class);
				dbUtil = DBUtilsHelper.getInstance().getDb();
				
				//初始化帮忙医小助手
				InitBMY();
				//初始化会话信息
				InitConservationClosed();
				
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
				ToastUtil.showMessage(message);
				handler.sendEmptyMessage(0x341);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			handler.sendEmptyMessage(0x341);
		}
	}
	Handler mHandler;
	
	private void InitConservationClosed()
	{
		mHandler=new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				if(msg.what==0x01)
				{
					IMChattingHelper.sendECMessage((ECMessage)msg.obj);
				}
			}
		};
		
		HttpClient.get(LoginActivity.this, Constants.LOGIN_CLOSE_SUBJECT,
				new RequestParams(), new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Log.e("String", arg0.result);
						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							JSONObject data=jsonObject.getJSONObject("Data");
							if(data!=null)
							{
								if(data.getBoolean("HasValue"))
								{
									String subjectId=data.getJSONObject("SubjectInfo").getString("Id");
									String docVoip=data.getString("VoipAccount");
									MainChatControllerListener.getInstance().closeSubject(subjectId, docVoip,mHandler);								}
								}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub

					}
				});
	}
	
	
	
	
	private void InitBMY()
	{
				//判断是否有帮忙医的消息
			  	DBUtil dbUtil = new DBUtil(MyApp.getInstance());
			  	TBNews mObject =dbUtil.queryFirst();
				if(mObject!=null )
				{
					 ECMessage msg = ECMessage.createECMessage(ECMessage.Type.TXT);
				        //设置消息的属性：发出者，接受者，发送时间等
				        msg.setForm("10000");
				        msg.setMsgTime(System.currentTimeMillis());
				        // 设置消息接收者
				        msg.setTo("10000");
				        msg.setSessionId("10000");
				        // 设置消息发送类型（发送或者接收）
				        msg.setDirection(ECMessage.Direction.RECEIVE);
				        // 创建一个文本消息体，并添加到消息对象中
				        ECTextMessageBody msgBody = new ECTextMessageBody(mObject.getTitle());
				    	// 调用接口发送IM消息
						msg.setMsgTime(System.currentTimeMillis());
						msg.setBody(msgBody);
					    msg.setMsgStatus(MessageStatus.SUCCESS);
					    if(ECDeviceKit.getInstance().getUserId()!=null){
					    long salId= ConversationSqlManager.querySessionIdForBySessionId("10000");
					    if(salId==0)//没有
						  ConversationSqlManager.insertSessionRecordV2(msg, 1);
					    }
				}
		
	}
	
	
	private void requestGetUserGroup() {
		// TODO Auto-generated method stub
		HttpClient.get(LoginActivity.this, Constants.GETUSERGROUPS_URL,
				new RequestParams(), new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						LogUtil.i(TAG, arg0.result);
						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							JSONObject data = jsonObject.getJSONObject("Data");
							String statusCode = data.getString("statusCode");
							if (statusCode.equals("000000")) {
								if(data.has("groups")&&data.getString("groups")!=null&&!data.getString("groups").equals("null")){
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
 
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}


 
	@Override
	public void onResume() {
		super.onResume();

		MobclickAgent.onPageStart("LoginActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		MobclickAgent.onPageEnd("LoginActivity");
		MobclickAgent.onPause(this);
	}
}
