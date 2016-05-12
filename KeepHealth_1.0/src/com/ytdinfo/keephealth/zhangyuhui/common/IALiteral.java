package com.ytdinfo.keephealth.zhangyuhui.common;

import java.util.ArrayList;

import org.apache.http.message.BasicNameValuePair;

import android.os.Environment;

import com.ytdinfo.keephealth.zhangyuhui.adapte.IAOrganz;
import com.ytdinfo.keephealth.zhangyuhui.model.IARoomName;
import com.ytdinfo.keephealth.zhangyuhui.model.IARoomNameHttp;
import com.ytdinfo.keephealth.zhangyuhui.model.IARoomPointModel;
import com.ytdinfo.keephealth.zhangyuhui.view.ichnography.IABaseFrame;

/**
 * @author zhangyuhui 静态数值类。
 */
public class IALiteral {
	// 屏幕资料
	public static int width = 0;

	public static int height = 0;

	public static float density = 0;
	public static int bitmapwidth = 0;

	public static int bitmapheight = 0;

	
	
	// 根据平面图的详细参数，和房间号的详细参数。返回需要绘制的 每个房间点的位置。
	public static ArrayList<IARoomPointModel> maps;
	public static ArrayList<IARoomName> roomName = new ArrayList<IARoomName>();

	public static final String IA_SCREEN_TEMP = getExternalStorePath()
			+ "/KeepHealth/ia_temp.png";

	/**
	 * 外置存储卡的路径
	 * 
	 * @return
	 */
	public static String getExternalStorePath() {
		if (isExistExternalStore()) {
			return Environment.getExternalStorageDirectory().getAbsolutePath();
		}
		return null;
	}

	/**
	 * 是否有外存卡
	 * 
	 * @return
	 */
	public static boolean isExistExternalStore() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}

	public static ArrayList<BasicNameValuePair> par;

	public static IABaseFrame layout;

	public static IAOrganz orginfo;

	public static IARoomNameHttp roomOrgpari;
}
