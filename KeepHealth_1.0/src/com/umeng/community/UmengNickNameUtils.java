package com.umeng.community;

import android.content.Context;
import android.content.Intent;

import com.umeng.comm.custom.AppAdd;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.ui.FriendsCircleActivity;
import com.ytdinfo.keephealth.ui.MainActivity;
import com.ytdinfo.keephealth.ui.login.LoginActivity;

public class UmengNickNameUtils {
	
	
	
	public static  void handleUmengName(Context mContext)
	{
		int flag=UmengNickNameUtils.checkNameIsUserName(mContext);
		switch (flag) {
			case 0:
				Intent mIntent=new Intent(mContext,LoginActivity.class);
				mIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				mContext.startActivity(mIntent);
				break;
			case 1:
				UmengNickNameUtils.showModifyNickNameDialog(mContext);
				break;
			default:
				//((MainActivity)mContext).radioGroupCheckId(R.id.tab_rb_4);
				Intent mIntent2=new Intent(mContext,FriendsCircleActivity.class);
				mContext.startActivity(mIntent2);
				break;
		}	
	}

	
	//0 用户未登录     1用户登录,昵称就是用户名 ，且确定用户名做为昵称     2用户没有昵称 
	public static int  checkNameIsUserName(Context mContext)
	{
		return	AppAdd.getAppInterface().appAddCheckNameIsUserName(mContext);
	}
	
	
	
	public static void showModifyNickNameDialog(final Context mContext)
	{
		AppAdd.getAppInterface().appAddShowModifyNickNameDialog(mContext);
	}
	
	
	

	
	
}
