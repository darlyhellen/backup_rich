package com.rayelink.eckit;

import android.app.AlertDialog;
 

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
