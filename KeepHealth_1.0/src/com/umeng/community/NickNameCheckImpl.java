package com.umeng.community;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.EditText;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.listeners.Listeners.CommListener;
import com.umeng.comm.core.nets.Response;
import com.umeng.comm.custom.AppInterface;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.FriendsCircleActivity;
import com.ytdinfo.keephealth.ui.MainActivity;
import com.ytdinfo.keephealth.ui.personaldata.CommonModifyInfoActivity;
import com.ytdinfo.keephealth.utils.DialogCustomInterface;
import com.ytdinfo.keephealth.utils.DialogUtils;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;

public class NickNameCheckImpl implements AppInterface {

	public static HomeJump instanceHomeJump;

	public interface HomeJump {
		public void goToHealthQuan();
	}
	
 
	

	// 0 用户未登录 1用户登录,昵称就是用户名 ，且确定用户名做为昵称 2用户没有昵称
	@Override
	public int appAddCheckNameIsUserName(Context mContext) {
		if (null != SharedPrefsUtil.getValue(Constants.TOKEN, null))// 登录
		{
			UserModel userModel =  new Gson().fromJson(
					SharedPrefsUtil.getValue(Constants.USERMODEL, ""),
					UserModel.class);
			//用户昵称为空,弹出修改昵称对话框
			if(userModel.getAddition1()==null||userModel.getAddition1()=="")
				return 1;
			//用户昵称不为空，用户昵称和用户名相同，并且用户名可作为昵称
//			else if (userModel.getAddition1().equals(userModel.getUserName())
//					&&!SharedPrefsUtil.getValue(Constants.IS_USER_NAME_AS_NICK_NAME, false))
//				return 1;
			
			else if(mContext instanceof MainActivity)
			{
				if(!((MainActivity)mContext).isCommunityCurrent())
					return 3;
				else 
					return 2;
			}
			else
				return 2;
		} else
			return 0;
	}

	@Override
	public void appAddShowModifyNickNameDialog(final Context mContext) {
	   if(DialogUtils.getInstance().dialog==null||!DialogUtils.getInstance().dialog.isShowing()){
			UserModel userModel =  new Gson().fromJson(
					SharedPrefsUtil.getValue(Constants.USERMODEL, ""),
					UserModel.class);
			String nameString=userModel.getUserName();
			int len = nameString.length();
			String message = "欢迎访问帮忙医社区，您正在使用的用户名 “"
					+ nameString
					+ "” 有可能是您的真实姓名，为保护您的隐私，您可以在下面设置昵称用于社区交流。";
			SpannableString spanString = new SpannableString(message);
			spanString.setSpan(new ForegroundColorSpan(mContext.getResources()
					.getColor(R.color.checked_text_name)), 21, 21 + len,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			DialogUtils.getInstance().setDialgoInterFace(
					new DialogCustomInterface() {
						@Override
						public void sure(final Dialog view) {
							// TODO Auto-generated method stub
							final String name = ((EditText) view.getWindow()
									.findViewById(R.id.extra)).getText().toString();
							if (name == null || !CommonModifyInfoActivity.isNiChengLen(name)) {
								ToastUtil.showMessage("昵称支持4-14位字母、汉字和数字");
								return;
							}
							final CommUser user = CommConfig.getConfig().loginedUser;
							user.name = name;
							CommunitySDKImpl.getInstance().updateUserProfile(user,
									new CommListener() {
										@Override
										public void onStart() {
											// TODO
											// Auto-generated
											// method stub
										}
	
										@Override
										public void onComplete(Response arg0) {
											// TODO
											if (Response.NO_ERROR == arg0.errCode){
												user.save();
												requestModifyInfo(mContext, name,view);
												}
											else {
												ToastUtil.showMessage("昵称已存在,请修改再提交");
											}
	
										}
									});
						}
	
						@Override
						public void cancel(Dialog view) {
							// TODO Auto-generated method stub
							view.dismiss();
							((MainActivity) mContext)
									.radioGroupCheckId(((MainActivity) mContext).oldCheckId);
							return;
						}
					});
			DialogUtils.getInstance().showDialog(mContext,
					R.layout.dialog_is_user_current, spanString);
			DialogUtils.getInstance().setSureText("进入社区");
			DialogUtils.getInstance().setCancelText("返回");
			DialogUtils.getInstance().setExpertText(nameString);
	   }
	}

	/**
	 * 将修改后的信息上传服务器
	 */
	public static void requestModifyInfo(final Context mContext,
			final String nickName,final Dialog view) {

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
							UserModel userModel = new Gson().fromJson(
									SharedPrefsUtil.getValue(
											Constants.USERMODEL, ""),
									UserModel.class);
							userModel.setAddition1(nickName);
							SharedPrefsUtil.putValue(Constants.USERMODEL,
									userModel.toString());
							if (userModel.getUserName()
									.equals(nickName)) {
								SharedPrefsUtil.putValue(
										Constants.IS_USER_NAME_AS_NICK_NAME,
										true);
							} else {
								SharedPrefsUtil.putValue(
										Constants.IS_USER_NAME_AS_NICK_NAME,
										false);
							}
							// 进入社区
//							((MainActivity) mContext)
//									.radioGroupCheckId(R.id.tab_rb_4);
							if (instanceHomeJump != null) {
								instanceHomeJump.goToHealthQuan();
								instanceHomeJump = null;
							}else {
								Intent mIntent2=new Intent(mContext,FriendsCircleActivity.class);
								mContext.startActivity(mIntent2);
							}
							view.dismiss();

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
