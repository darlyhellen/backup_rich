package com.rayelink.eckit;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.provider.Contacts;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.app.MyApp;
import com.ytdinfo.keephealth.model.GroupUserInfoBean;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.utils.ImageLoaderUtils;
import com.ytdinfo.keephealth.utils.ToastUtil;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECDevice.ECConnectState;
import com.yuntongxun.ecsdk.ECDevice.ECDeviceState;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.kitsdk.ECDeviceKit;
import com.yuntongxun.kitsdk.beans.ECAuthParameters;
import com.yuntongxun.kitsdk.db.ContactSqlManager;
import com.yuntongxun.kitsdk.listener.OnConnectSDKListener;
import com.yuntongxun.kitsdk.listener.OnInitSDKListener;
import com.yuntongxun.kitsdk.listener.OnLogoutSDKListener;
import com.yuntongxun.kitsdk.ui.chatting.model.IMChattingHelper;
import com.yuntongxun.kitsdk.ui.chatting.model.IMChattingHelper.UserFaceManagerListener;
import com.yuntongxun.kitsdk.ui.group.model.ECContacts;
import com.yuntongxun.kitsdk.utils.LogUtil;

public class SDKCoreHelper implements OnInitSDKListener, OnConnectSDKListener,
		OnLogoutSDKListener {

	private static final String TAG = "SDKCoreHelper";

	private static SDKCoreHelper sInstance;
	public ECDevice.ECConnectState mConnect = ECDevice.ECConnectState.CONNECT_FAILED;
	private ECInitParams mInitParams;
	private boolean mKickOff = false;

	private SDKCoreHelper() {
		
	}

	public static SDKCoreHelper getInstance() {
		if (sInstance == null) {
			sInstance = new SDKCoreHelper();
		}
		return sInstance;
	}

	//判断云通讯是否开启
	public static boolean isKickOff() {
		return getInstance().mKickOff;
	}

	@Override
	public void onInitialized() {
		LogUtil.d(TAG, "ECSDK is ready");
		//云通讯SDK登录
		ECAuthParameters parameters = new ECAuthParameters();
		UserModel curUser=AppUserAccount.getCurUserAccount();
		if(curUser!=null){
			parameters.setUserId(curUser.getVoipAccount());
			parameters.setPwd(curUser.getVoipPwd());
			parameters.setAppKey(AppUserAccount.APP_KEY);
			parameters.setAppToken(AppUserAccount.APP_TOKEN);
			parameters.setLoginType(ECInitParams.LoginAuthType.PASSWORD_AUTH);
			parameters.setLoginMode(ECInitParams.LoginMode.FORCE_LOGIN);
			ECDeviceKit.login(parameters, getInstance());
		}
	}

	/**
	 * 当前SDK注册状态
	 * 
	 * @return
	 */
	public static ECDevice.ECConnectState getConnectState() {
		return getInstance().mConnect;
	}
	
	
	public static boolean isOnLine()
	{
		return  ECDevice.getECDeviceOnlineState()==ECDeviceState.ONLINE;
	}
	

	//云通讯登出
	@Override
	public void onLogout() {
		getInstance().mConnect = ECDevice.ECConnectState.CONNECT_FAILED;
		if (mInitParams != null && mInitParams.getInitParams() != null) {
			mInitParams.getInitParams().clear();
		}
		mInitParams = null;
		MyApp.getInstance().sendBroadcast(new Intent(BroadCastAction.ACTION_LOGOUT));
	}

	//云通讯连接错误接口
	@Override
	public void onError(Exception exception) {
		LogUtil.e(TAG,
				"ECSDK couldn't start: " + exception.getLocalizedMessage());
		
		Log.e("SDKCoreHelper_onError", exception.getLocalizedMessage());
		 ECDevice.unInitial();
	}

	@Override
	public void onConnect() {
		// TODO Auto-generated method stub
		Log.e("SDKCoreHelper_onConnect", "链接云云通讯");
	}

	@Override
	public void onDisconnect(ECError error) {
		// TODO Auto-generated method stub
		if(error.errorCode==175004)
		{
			ToastUtil.showMessage("您的账号在其他地方已经登录！");
			SDKCoreHelper.getInstance().onLogout();
		}
		Log.e("SDKCoreHelper_onDisconnect", error.errorCode+"");
		Log.e("SDKCoreHelper_onDisconnect", error.errorMsg);
	}

	@Override
	public void onConnectState(ECConnectState state, ECError error) {
		// TODO Auto-generated method stub
		Log.e("SDKCoreHelper_onConnectState", state.name());
		Log.e("SDKCoreHelper_onConnectState", error.errorMsg);
		mConnect=state;
		if(state==ECConnectState.CONNECT_SUCCESS)
		{
			MyApp.getInstance().InitChatController();
			IMChattingHelper.getInstance().setUserFaceManagerListener(new UserFaceManagerListener() {

				@Override
				public void getUserFace(ECMessage message, TextView nameView,
						ImageView headView) {
					if(!ContactSqlManager.hasContact(message.getForm())){
						requestGetUserface(message.getForm(),nameView,headView);
					}else {
						ECContacts mContact=ContactSqlManager.getContact(message.getForm());
						nameView.setText(mContact.getNickname());
//						BitmapUtils bitmapUtils=new BitmapUtils(MyApp.getInstance());
//						bitmapUtils.display(headView, mContact.getRemark());
						ImageLoader.getInstance().displayImage(
								mContact.getRemark(),headView,
								ImageLoaderUtils.getOptions2());
					}
				}
			});
		
		}
	}
	
	
	private void requestGetUserface(String s,final TextView nameView ,final ImageView headView) {
		HttpClient.put(Constants.GETUSERFACE_URL, "[\"" + s + "\"]",
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						LogUtil.i(TAG, arg0.result);
						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							String data = jsonObject.getString("Data");
							List<GroupUserInfoBean> l = new Gson().fromJson(
									data,
									new TypeToken<List<GroupUserInfoBean>>() {
									}.getType());
							for (int i = 0; i < l.size(); i++) {
								GroupUserInfoBean userInfoBean = l.get(i);
								String headPic = userInfoBean.getFace();
								String userName = userInfoBean.getUserName();
								ECContacts contact=new ECContacts();
								contact.setContactid(userInfoBean.getVoipAccount());
								contact.setNickname(userInfoBean.getUserName());
								contact.setRemark(userInfoBean.getFace());
								contact.setType(Contacts.KIND_PHONE);
								ContactSqlManager.insertContact(contact,1,true);
								nameView.setText(userName);
								ImageLoader.getInstance().displayImage(
										headPic,headView,
										ImageLoaderUtils.getOptions2());
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						LogUtil.i(TAG, arg1.toString());
					}
				});
	}
	

}
