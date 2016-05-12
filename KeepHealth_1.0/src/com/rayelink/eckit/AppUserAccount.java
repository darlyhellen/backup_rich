/**
 * Project Name:KeepHealth_1.0
 * File Name:AppUserAccount.java
 * Package Name:com.rayelink.eckit
 * Date:2015-10-28上午9:28:45
 * Copyright (c) 2015, chenzhou1025@126.com All Rights Reserved.
 *
 */

package com.rayelink.eckit;

import android.content.Context;
import android.content.Intent;
import android.opengl.ETC1;

import com.google.gson.Gson;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.login.LoginActivity;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;

/**
 * ClassName:AppUserAccount <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015-10-28 上午9:28:45 <br/>
 * 
 * @author Think
 * @version 你好
 * @since JDK 1.6
 * @see
 */
public class AppUserAccount {

	public static UserModel curUser;
	//专有开发环境
	//public  static String APP_KEY = "f89a4c4b533b948401533b9c16f40001";
//	public  static String APP_TOKEN = "11a063c3bd4042e88991bcec60f50e10";
	
	// 开发环境
//	public  static String APP_KEY = "8a48b5514ff923b4015002a45a471721";
//	public  static String APP_TOKEN = "c5623c1ef078946cdd569b6e6cf3f48c";

	// 测试环境
// 	 public  static String APP_KEY="8a48b55150e162370150f03d89ec68af";
//     public  static String APP_TOKEN="5dfaec5d2bc8188c11800966f7ba6656";
//	 
	//正式环境
	 public final static String APP_KEY="aaf98f894efcded4014f05f582360cd0";
	 public final static String APP_TOKEN="be793d6e7d08a7ac2c095530647cc1df";
  
	 //true 专有环境  flase 测试环境
//	  public static void changeToZYAppConfig()
//	  {
//		  APP_KEY="f89a4c4b533b948401533b9c16f40001"; 
//		  APP_TOKEN="11a063c3bd4042e88991bcec60f50e10";
//	  }
	   
   

	// 获取当前登录用户
	public static UserModel getCurUserAccount() {
		if (curUser == null) {
			return new Gson().fromJson(
					SharedPrefsUtil.getValue(Constants.USERMODEL, null),
					UserModel.class); 	
		} else {
			return curUser;
		}
	}

	// 检测用户是否登录
	public static boolean checkUserIsLogin(Context mContext) {
		if (getCurUserAccount() == null) {
			Intent i11 = new Intent();
			i11.setClass(mContext, LoginActivity.class);
			mContext.startActivity(i11);
			return false;
		} else
			return true;
	}

}
