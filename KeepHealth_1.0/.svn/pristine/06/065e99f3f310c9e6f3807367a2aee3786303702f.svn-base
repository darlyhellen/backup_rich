/**
 * 上午11:20:37
 * @author Zhangyuhui
 * $
 * LogFileHelper.java
 * TODO
 */
package com.ytdinfo.keephealth.zhangyuhui.common;

import java.io.File;
import java.io.FileWriter;

import android.text.format.Time;
import android.util.Log;

import com.ytdinfo.keephealth.app.Constants;

/**
 * @author Zhangyuhui LogFileHelper $ 上午11:20:37 TODO 工具类。理想工作效果为，将日志信息输出到文件中。
 */
public class LogFileHelper {

	private static LogFileHelper instance;
	private static boolean isDebug = false;// 是否需要打印bug，可以在application的onCreate函数里面初始化
	private static final String TAG = "测试";
	private static final String CRASH_REPORTER_EXTENSION = ".log";

	/**
	 * 
	 * 上午11:21:13
	 * 
	 * @author Zhangyuhui LogFileHelper.java TODO
	 */
	private LogFileHelper() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the instance
	 */
	public static LogFileHelper getInstance() {
		if (instance == null) {
			instance = new LogFileHelper();
		}
		return instance;
	}

	// 下面四个是默认tag的函数
	public void i(String msg) {
		if (isDebug) {
			Log.i(TAG, msg);
		} else {
			saveInfoFile(TAG, msg);
		}

	}

	public void d(String msg) {
		if (isDebug) {
			Log.d(TAG, msg);
		} else {
			saveInfoFile(TAG, msg);
		}
	}

	public void e(String msg) {
		if (isDebug) {
			Log.e(TAG, msg);
		} else {
			saveInfoFile(TAG, msg);
		}
	}

	public void v(String msg) {
		if (isDebug) {
			Log.v(TAG, msg);
		} else {
			saveInfoFile(TAG, msg);
		}
	}

	// 下面是传入自定义tag的函数
	public void i(String tag, String msg) {
		if (isDebug) {
			Log.i(tag, msg);
		} else {
			saveInfoFile(tag, msg);
		}
	}

	public void d(String tag, String msg) {
		if (isDebug) {
			Log.i(tag, msg);
		} else {
			saveInfoFile(tag, msg);
		}
	}

	public void e(String tag, String msg) {
		if (isDebug) {
			Log.i(tag, msg);
		} else {
			saveInfoFile(tag, msg);
		}
	}

	public void v(String tag, String msg) {
		if (isDebug) {
			Log.i(tag, msg);
		} else {
			saveInfoFile(tag, msg);
		}
	}

	/**
	 * @param msg
	 *            上午11:26:41
	 * @author Zhangyuhui LogFileHelper.java TODO 将日志输入到文件中
	 */
	private void saveInfoFile(String tag, String msg) {
		// TODO Auto-generated method stub
		Time t = new Time("GMT+8");
		t.setToNow(); // 取得系统时间
		int date = t.year * 10000 + t.month * 100 + t.monthDay;
		String fileName = "LOG" + date + CRASH_REPORTER_EXTENSION;
		File act = new File(Constants.STORAGE_ROOT_DIR);
		if (!act.exists()) {
			act.mkdir();
		}
		File file = new File(Constants.STORAGE_ROOT_DIR + fileName);
		try {
			// long timestamp = System.currentTimeMillis();
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, true);
			String time = t.year + "-" + (t.month + 1) + "-" + t.monthDay + " "
					+ t.hour + ":" + t.minute + ":" + t.second + " ";
			fw.write(time);
			fw.write(tag);
			fw.append("\t");
			fw.append(msg + "\r\n");
			fw.flush();
			fw.close();
		} catch (Exception e) {
		}
	}
}
