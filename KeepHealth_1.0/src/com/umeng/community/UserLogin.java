package com.umeng.community;

 

import android.content.Context;
import android.content.Intent;

import com.umeng.comm.core.login.AbsLoginImpl;
import com.umeng.comm.core.login.LoginListener;
import com.ytdinfo.keephealth.ui.login.LoginActivity;

public class UserLogin extends AbsLoginImpl{

	@Override
	protected void onLogin(Context arg0, LoginListener arg1) {
		// TODO Auto-generated method stub
		
		Intent intent=new Intent(arg0,LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		arg0.startActivity(intent);
 
		
	}

}
