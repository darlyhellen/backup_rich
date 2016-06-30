package com.ytdinfo.keephealth.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.MyApp;
import com.ytdinfo.keephealth.ui.MainActivity;

public class NotificationUtils {
	private static NotificationManager nm;
	
	public static NotificationManager getNm() {
		return nm;
	}

	public static void setNm(NotificationManager nm) {
		NotificationUtils.nm = nm;
	}

	/**
	 * 旧方法
	 */
	public static void send(Context context) {
		// 1 得到通知管理器
		NotificationManager nm = (NotificationManager) MyApp.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);

		// 2构建通知
		Notification notification = new Notification(
				R.drawable.ic_launcher, "你收到新的消息",
				System.currentTimeMillis());

		// 3设置通知的点击事件
		Intent intent = new Intent(MyApp.getInstance(), MainActivity.class);
		intent.putExtra("news", "news");
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(MyApp.getInstance(), 100,
				intent, 0);
		notification.setLatestEventInfo(MyApp.getInstance(), "帮忙医消息提醒", "你收到新的消息", contentIntent);

		notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击通知之后自动消失
		if(SharedPrefsUtil.getValue(Constants.ALERT, false))
		{
	     notification.defaults = Notification.DEFAULT_VIBRATE;// 震动提醒</pre><br>  
		}
		// 4发送通知
		nm.notify(100, notification);
	//	return nm;
	}

}
