package com.rayelink.eckit;

import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.MyApp;
import com.ytdinfo.keephealth.utils.Chat_Dialog;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
 

public class RetryConnectDialog   {
	
	
	private static  RetryConnectDialog instance;
	public static RetryConnectDialog getInstance()
	{
		if(instance==null)
		{
			instance=new RetryConnectDialog();
		}
		return instance;
	}
	
	private AlertDialog dialog;
	
	public RetryConnectDialog()
	{
		 
		
	}
	
	
	
	

}
