package com.ytdinfo.keephealth.utils;

import com.umeng.common.ui.util.FontUtils;
import com.ytdinfo.keephealth.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.SpannableString;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class DialogUtils {
	
	private  DialogCustomInterface sinterface;
	
	
	private   static DialogUtils instance;
	
	public Dialog dialog=null;
	
	public static   DialogUtils getInstance()
	{
		if(instance==null)
		{
			instance=new DialogUtils();
		}
		return instance;
	}
 
	public void setDialgoInterFace(DialogCustomInterface mInterface)
	{
		sinterface=mInterface;
	}
	
	 
	
	public void setSureText(String sureText)
	{
		((Button)dialog.getWindow().findViewById(R.id.sure)).setText(sureText);
	}

	public void setCancelText(String cancelText)
	{
		((Button)dialog.getWindow().findViewById(R.id.cancel)).setText(cancelText);
	}

	public void setExpertText(String expertText)
	{
		((EditText)dialog.getWindow().findViewById(R.id.extra)).setText(expertText);
	}
	
 
	public void showDialog(Context mContext,int layoutId,SpannableString message)
	{
		dialog = new AlertDialog.Builder(mContext)
		.create();
		dialog.show();
		dialog.setCanceledOnTouchOutside(false);
		dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
		Window window = dialog.getWindow();
		window.setContentView(layoutId);// 设置对话框的布局
		FontUtils.changeTypeface(window.getDecorView());
		TextView msg = (TextView) window
				.findViewById(R.id.message); 
		msg.setText(message);
		Button sure = (Button) window
				.findViewById(R.id.sure);
		sure.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sinterface.sure(dialog);
			}
		});
		Button cancel = (Button) window
				.findViewById(R.id.cancel);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sinterface.cancel(dialog);
			}
		});	
		return;
	}
	
	 
	
	
	
}
