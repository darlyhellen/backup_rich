package com.yuntongxun.kitsdk.utils;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * simple tools to handle text for apps.
 *
 * @author chao
 *
 */
public class TextUtil {
	private static MessageDigest md = null;
	public static final String PHONE_PREFIX = "+86";

	static {
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			LogUtil.e(e.toString());
		}
	}

	public static String getHashValue(final String c) {
		if (md != null) {
			md.update(c.getBytes());
			return byte2hex(md.digest());
		}
		return "";
	}

	public static String getHashValueWithServerAddress(final String ip,
													   final String tcpport, final String udpport) {
		return ip + ";" + tcpport + ";" + udpport;
	}

	public static String byte2hex(byte b[]) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0xff);
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
			if (n < b.length - 1)
				hs = (new StringBuffer(String.valueOf(hs))).toString();
		}

		return hs.toUpperCase();
	}

	public static String fillNumber(int length, int number) {
		String f = "%0" + length + "d";
		return String.format(f, number);
	}

	public static String connectString(String[] arrays, String rule) {
		return connectString(arrays, null, rule);
	}

	public static String connectString(String[] arrays, String suffix,
									   String rule) {
		if (arrays == null || arrays.length == 0) {
			return "";
		}
		StringBuffer buffer = new StringBuffer();
		for (String str : arrays) {
			if (suffix != null) {
				buffer.append(suffix);
			}
			buffer.append(str + rule);
		}
		if (buffer.toString().length() > 0) {
			buffer.deleteCharAt(buffer.length() - 1);
		}
		return buffer.toString();
	}

	public static String IPFormat(String ip) {
		try {
			String[] array = split(ip, ".");
			String newIP = "";
			for (int i = 0; i < array.length; i++) {
				newIP += Integer.parseInt(array[i]);
				if (i < array.length - 1) {
					newIP += ".";
				}
			}
			return newIP;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ip;
	}

	public static String readContentByFile(String path) {
		BufferedReader reader = null;
		String line = null;
		StringBuilder sb = new StringBuilder();
		try {
			File file = new File(path);
			reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				sb.append(line.trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString().trim();
	}

	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			is = null;
		}
		return sb.toString().trim();
	}

	public static long[] transfer_long_array(List<Long> list) {
		if (list != null) {
			int len = list.size();
			long[] array = new long[len];
			for (int i = 0; i < len; i++) {
				array[i] = list.get(i).longValue();
			}
			return array;
		}
		return null;
	}

	public static String[] transfer_string_array(List<Long> list) {
		if (list != null) {
			ArrayList<String> array = new ArrayList<String>();
			for (Long longs : list) {
				array.add("" + longs.longValue());
			}
			return array.toArray(new String[list.size()]);
		}

		return null;
	}

	public static String TrimSpace(String str) {
		if (str == null || str.equals(""))
			return null;

		char[] tempChar = null;
		try {
			char[] arrChar = new char[str.length()];
			int k = 0;
			for (int i = 0; i < str.length(); i++) {
				char c = str.charAt(i);
				if (c == ' ') {
					continue;
				} else {
					arrChar[k++] = c;
				}
			}
			tempChar = new char[k];
			System.arraycopy(arrChar, 0, tempChar, 0, k);
		} catch (Exception e) {
			return str;
		}
		return new String(tempChar);
	}

	public static String[] split(String original, String regex,
								 boolean isTogether) {
		int startIndex = original.indexOf(regex);
		int index = 0;
		if (startIndex < 0) {
			return new String[] { original };
		}
		ArrayList<String> v = new ArrayList<String>();
		while (startIndex < original.length() && startIndex != -1) {
			String temp = original.substring(index, startIndex);
			v.add(temp);
			index = startIndex + regex.length();
			startIndex = original.indexOf(regex, startIndex + regex.length());
		}
		if (original.indexOf(regex, original.length() - regex.length()) < 0) {
			String last = original.substring(index);
			if (isTogether) {
				last = regex + last;
			}
			v.add(last);
		}

		return v.toArray(new String[v.size()]);
	}

	public static String getLastStringBySplit(String original, String regex) {
		try {
			String[] array = split(original, regex);
			return array[array.length - 1].trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return original;
	}

	public static String[] split(String original, String regex) {
		return split(original, regex, false);
	}

	public static String getBetweenString(String original, String regex,
										  String regex2) {
		String str = "";
		try {
			int start = original.indexOf(regex);
			int end = original.lastIndexOf(regex2);
			if (start < 0) {
				start = 0;
			} else {
				start++;
			}
			if (end < 0) {
				end = original.length();
			}
			if (start < end) {
				str = original.substring(start, end);
			}
			LogUtil.d(original + " " + str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public static void resetArray(byte[] b) {
		if (b == null) {
			return;
		}
		int len = b.length;
		for (int i = 0; i < len; i++) {
			b[i] = 0;
		}
	}

	public static String replace(String str, String substr, String restr) {
		try {
			String[] tmp = split(str, substr);
			String returnstr = null;
			int len = tmp.length;
			if (tmp != null && len > 0) {
				returnstr = tmp[0];
				for (int i = 0; i < len - 1; i++)
					returnstr = returnstr + restr + tmp[i + 1];
			}
			return returnstr.trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	public static boolean isGraphic(CharSequence str) {
		final int len = str.length();
		for (int i=0; i<len; i++) {
			int gc = Character.getType(str.charAt(i));
			if (gc != Character.CONTROL
					&& gc != Character.FORMAT
					&& gc != Character.SURROGATE
					&& gc != Character.UNASSIGNED
					&& gc != Character.LINE_SEPARATOR
					&& gc != Character.PARAGRAPH_SEPARATOR
					&& gc != Character.SPACE_SEPARATOR) {
				return true;
			}
		}
		return false;
	}

	public static boolean isEmpty(CharSequence str) {
		if (str == null || str.length() == 0) {
			return true;
		}
		return false;
	}

	public static String formatPhone(String phoneNumber) {
		if (phoneNumber == null) {
			return "";
		}
		if (phoneNumber.startsWith(PHONE_PREFIX)) {
			return phoneNumber.substring(PHONE_PREFIX.length()).trim();
		}
		return phoneNumber.trim();
	}

	public static String formatPhoneAdd86(String phoneNumber) {
		if (phoneNumber == null) {
			return "";
		}
		if (!phoneNumber.startsWith(PHONE_PREFIX)) {
			return PHONE_PREFIX + phoneNumber.trim();
		}
		return phoneNumber.trim();
	}

	public static void releaseStringArray(String[] array) {
		if (array != null) {
			for (int i = 0; i < array.length; i++) {
				array[i] = null;
			}
			array = null;
		}
	}



	public static void releaseArrayList(ArrayList<?> dataList) {
		if (dataList != null) {
			dataList.clear();
		}
		dataList = null;
	}

	/*********************************************************************************/
	static void testFree() {
		
	}

	static final String[] firstName = { "赵", "钱", "孙", "李", "周", "吴", "郑", "王",
			"冯", "陈", "楮", "卫", "蒋", "沈", "韩", "杨", "孔 ", "曹", "严", "华", "金",
			"魏", "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水", "窦", "章", "朱", "秦",
			"尤", "许", "何", "吕", "施", "张" };

	static final String[] middleName = { "朱", "秦", "尤", "许", "何", "吕", "施", "张" };

	static final String[] lastName = { "朱", "秦", "尤", "许", "何", "吕", "施", "张" };

	public static final Random rnd = new Random();

	public static int creatRandom(int t) {
		return Math.abs(rnd.nextInt(t));
	}

	/*public static void createContact() {
		int firstLength = firstName.length;
		int middleLength = middleName.length;
		int lastLength = lastName.length;

		try {
			for (int i = 0; i < 1500; i++) {
				Contact c = new Contact();
				String dname = "";
				int phone = 10000000 + i;
				if (i % 3 == 0) {
					dname = firstName[creatRandom(firstLength)]
							+ middleName[creatRandom(middleLength)];
				} else {
					dname = firstName[creatRandom(firstLength)]
							+ middleName[creatRandom(middleLength)]
							+ lastName[creatRandom(lastLength)];
				}
				c.setDisplayName(dname);
				String mdn = "135" + phone;
				c.addPhone(new Phone(2, mdn));
				LogUtil.d("Create Contacts:" + dname + " ," + mdn);
				ContactsManager.getInstance().addContacts(c);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	public static String filterUnNumber(String str) {
		if (str == null) {
			return null;
		}
		if (str.startsWith("+86")) {
			str = str.substring(3, str.length());
		}

		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);

		if(str.startsWith("-")){
			return "-"+m.replaceAll("").trim();
		}else{
			return m.replaceAll("").trim();
		}

	}
	public static String extractsDiableNumber(String number){
		StringBuffer sb = new StringBuffer();
		if(TextUtil.isEmpty(number)){
			return null;
		}
		if (number.startsWith("+86")) {
			number = number.substring(3, number.length());
		}
		int len = number.length();
		int i = 0;
		while (i < len) {
			char temp = number.charAt(i);
			if (isReallyDialable(temp)) {
				sb.append(temp);
			}
			i++;
		}
		return sb.toString();
	}

	public final static boolean isReallyDialable(char c) {
		return (c >= '0' && c <= '9');
	}

	public static String getClientRandomId() {
		String clientid = DateUtil.getDefaultFormat();
		return DateUtil.getDefaultFormat() + clientid.substring(9);
	}

	public static boolean isStringEqual(String s1, String s2) {
		if (s1 == null || s2 == null) {
			if (s1 == null && s2 == null) {
				return true;
			}
		} else {
			if (s1.trim().equals(s2.trim())) {
				return true;
			}
		}
		return false;
	}

	/*public static boolean isListEqual(List<? extends DataColumns> list1,
			List<? extends DataColumns> list2) {
		if (list1 == null || list2 == null) {
			if (list1 == null && list2 == null) {
				return true;
			}
		} else {
			if (list1.size() == list2.size()) {
				if (list1.containsAll(list2)) {
					return true;
				}
			}
		}
		return false;
	}*/

	public static String MD5(String source) {
		return MD5(source.getBytes());
	}

	public static String MD5(byte[] source) {
		String s = null;
		char hexDigits[] = {
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
				'e', 'f' };
		try {
			java.security.MessageDigest md = java.security.MessageDigest
					.getInstance("MD5");
			md.update(source);
			byte tmp[] = md.digest();
			char str[] = new char[16 * 2];
			int k = 0;
			for (int i = 0; i < 16; i++) {
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			s = new String(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s;
	}

	public static String formatteOrganization(String company, String position) {
		StringBuffer sBuffer = new StringBuffer();
		String string = (company == null) ? " " : company;
		sBuffer.append(string + " ,");
		string = (position == null) ? " " : position;
		sBuffer.append(string);

		return sBuffer.toString();
	}

	/*public static String getCallsTypeString(int type) {
		if (type == CallsListItem.INCOMING_TYPE) {
			return "呼入";
		} else if (type == CallsListItem.OUTGOING_TYPE) {
			return "呼出";
		} else {
			return "未接";
		}
	}*/


	public static String getAreaCode(String phone) {
		String area = "000";
		phone = filterUnNumber(phone);
		if (phone == null) {
			return area;
		}

		if (phone.startsWith("0") && phone.length() > 1) {
			phone = phone.substring(1);
			if (phone.length() < 3) {
				return phone;
			}
			if (phone.startsWith("1") || phone.startsWith("2")) {
				area = phone.substring(0, 2);
			} else {
				area = phone.substring(0, 3);
			}
		}

		return area;
	}

	public static String PhoneClearArea(String phone) {
		phone = filterUnNumber(phone);
		if (phone == null||phone.length() < 4) {
			return phone;
		}
		if (phone.startsWith("0")) {
			phone = phone.substring(1);
			if (phone.startsWith("1") || phone.startsWith("2")) {
				phone = phone.substring(2);
			} else {
				phone = phone.substring(3);
			}
		}
		return phone;
	}

	public static boolean isChinese(char c) {

		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;

	}


	public static boolean hasFullSize(String inStr) {
		if (inStr.getBytes().length != inStr.length()) {
			return true;
		}
		return false;
	}

	public static String xmlTextDecode(String xmlText) {
		if(isEmpty(xmlText))
			return xmlText;
		xmlText = xmlText.replaceAll("&amp;", "&");
		xmlText = xmlText.replaceAll("&lt;", "<");
		xmlText = xmlText.replaceAll("&gt;", ">");
		xmlText = xmlText.replaceAll("&apos;", "\\");
		xmlText = xmlText.replaceAll("&quot;", "\"");
		return xmlText;
	}

	/**
	 * @param file
	 * @return
	 */
	public static int calculateVoiceTime(String file) {
		File _file = new File(file);
		if(!_file.exists()) {
			return 0;
		}

		int duration = (int) Math.ceil(_file.length() / 650) ;

		if(duration > 60) {
			return 60;
		}

		if(duration < 1) {
			return 1;
		}
		return duration;
	}

	/**
	 * @param recipients
	 * @return
	 */
	public static boolean isGroupContact(String recipients) {
		if(TextUtil.isEmpty(recipients) || !recipients.startsWith("g")) {
			return false;
		}
		return true;
	}
	
	/*public static boolean isSystem(String recipients) {
		if(TextUtil.isEmpty(recipients) || !recipients.startsWith(DeptEmploSQlManager.SYSTEM_CONTACT_USER_ID)) {
			return false;
		}
		return true;
	}*/




	/**
	 *
	 * @param c
	 * @return
	 */
	public static boolean characterChinese(char c) {
		Character.UnicodeBlock unicodeBlock = Character.UnicodeBlock
				.of(c);
		return ((unicodeBlock != Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS)
				&& (unicodeBlock != Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS)
				&& (unicodeBlock != Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A)
				&& (unicodeBlock != Character.UnicodeBlock.GENERAL_PUNCTUATION)
				&& (unicodeBlock != Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION)
				&& (unicodeBlock != Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS));
	}

	public static boolean frequentOperation(long mt) {
		if(System.currentTimeMillis() - mt > 3000) {
			return false;
		}
		return true;
	}


}
