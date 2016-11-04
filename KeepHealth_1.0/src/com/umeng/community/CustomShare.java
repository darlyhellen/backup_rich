package com.umeng.community;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;

import com.umeng.comm.core.beans.ShareContent;
import com.umeng.comm.core.share.Shareable;
import com.umeng.socialize.UMShareAPI;
import com.ytdinfo.keephealth.ui.WebViewActivity;
import com.ytdinfo.keephealth.wxapi.CustomShareBoard;
import com.ytdinfo.keephealth.wxapi.WXCallBack;

public class CustomShare implements Shareable,WXCallBack {

	@Override
	public void onActivityResult(Activity arg0, int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		UMShareAPI.get(arg0).onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void share(Activity arg0, ShareContent arg1) {
		// TODO Auto-generated method stub
		Log.e("Share",arg1.mText);
		CustomShareBoard shareBoard = new CustomShareBoard(arg0);
		shareBoard.setShareContent(arg1);
		shareBoard.setWXCallBack(this);
		shareBoard.showAtLocation(arg0.getWindow().getDecorView(), Gravity.BOTTOM,
					0, 0);
		
	}

	@Override
	public void shareComplete(boolean flag) {
		// TODO Auto-generated method stub
		Log.e("Share","shareComplete"+flag);
	}

}
