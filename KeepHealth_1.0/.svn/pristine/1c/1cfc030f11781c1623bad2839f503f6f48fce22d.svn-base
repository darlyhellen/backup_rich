package com.ytdinfo.keephealth.utils;

import java.util.Calendar;

public class MathUtils {
	/**
	 * 根据身份证号计算出年龄
	 * 
	 * @param idCard
	 * @return
	 */
	public static int calculateAge(String id) {

		Calendar ca = Calendar.getInstance();
		int nowYear = ca.get(Calendar.YEAR);
		int nowMonth = ca.get(Calendar.MONTH) + 1;
		int len = id.length();
		if (len == 18) {
			int IDYear = Integer.parseInt(id.substring(6, 10));
			int IDMonth = Integer.parseInt(id.substring(10, 12));
			if ((IDMonth - nowMonth) > 0) {
				return nowYear - IDYear - 1;
			} else
				return nowYear - IDYear;
		} else
			System.out.println("错误的身份证号");
		return 0;
	}
}
