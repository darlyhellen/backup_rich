/**
 * 上午9:23:07
 * @author zhangyh2
 * $
 * Chat_Dialog.java
 * TODO
 */
package com.ytdinfo.keephealth.utils;

import java.util.Calendar;

/**
 * @author zhangyh2 Chat_Dialog $ 上午9:23:07 TODO
 */
public class Chat_Dialog {

	public static boolean timeCurl() {
		Calendar cal = Calendar.getInstance();//  当前日期
		int hour = cal.get(Calendar.HOUR_OF_DAY);//  获取小时
		int minute = cal.get(Calendar.MINUTE);//  获取分钟
		int minuteOfDay = hour * 60 + minute;//  从0:00分开是到目前为止的分钟数
		final int start = 9 * 60;//  起始时间 17:20的分钟数
		final int end = 18 * 60;//  结束时间 19:00的分钟数
		if (minuteOfDay >= start && minuteOfDay <= end) {
			return true;
		} else {
			return false;
		}
	}
}
