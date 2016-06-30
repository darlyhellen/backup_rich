package com.ytdinfo.keephealth.utils;

public class UserDataUtils {

	public static String getUserData(String subjectId) {
		// TODO Auto-generated method stub
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append('0');
		for (int i = 0; i < (32 - subjectId.length()); i++) {
			stringBuffer.append('~');
		}
		String usString = stringBuffer.append(subjectId).toString();
		return MD5Util.md5(usString) + usString;
	}

	public static String getUserDataForTrans(String subjectId,String doctorName) {
		// TODO Auto-generated method stub
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append('1');
		for (int i = 0; i < (32 - subjectId.length()); i++) {
			stringBuffer.append('~');
		}
		stringBuffer.append(subjectId).toString();
		for (int i = 0; i < (32 - doctorName.length()); i++) {
			stringBuffer.append('~');
		}
		String usString = stringBuffer.append(doctorName).toString();
		return MD5Util.md5(usString) + usString;
	}

	public static String getUserData2(String subjectId) {
		// TODO Auto-generated method stub
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append('0');
		for (int i = 0; i < (32 - subjectId.length()); i++) {
			stringBuffer.append('~');
		}
		String usString = stringBuffer.append(subjectId).toString();
		return "abcd" + MD5Util.md5(usString) + usString;
	}
}
