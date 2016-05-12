package com.ytdinfo.keephealth.zhangyuhui.view.loading;

import android.content.Context;

/**
 * @author zhangyuhui
 *	加载工具类，主要功能。请求服务器或耗时操作进行页面等待。
 */
public class ProgressUtil {

	private Context context;

	public ProgressUtil(Context context) {
		super();
		this.context = context;

	}

	private CustomProgressDialog progressDialog;

	public void startProgressDialog() {

		if (progressDialog == null) {
			progressDialog = CustomProgressDialog.createDialog(context);
			progressDialog.setCancelable(false);
			// progressDialog.setMessage("正在加载中...");
		}
		progressDialog.show();
	}

	public void isLock(boolean flag) {
		if (progressDialog != null) {
			// 点击其他地方，不能退出Dialog
			progressDialog.setCancelable(flag);
		}
	}

	public void stopProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}
}
