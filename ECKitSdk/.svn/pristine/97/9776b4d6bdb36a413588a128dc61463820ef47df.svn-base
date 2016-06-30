/**
 * Project Name:ECKitSdk
 * File Name:TimerService.java
 * Package Name:com.yuntongxun.kitsdk
 * Date:2015-11-2下午4:37:04
 * Copyright (c) 2015, chenzhou1025@126.com All Rights Reserved.
 *
 */

package com.yuntongxun.kitsdk;

import java.util.HashMap;
import java.util.Iterator;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * ClassName:TimerService <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015-11-2 下午4:37:04 <br/>
 * 
 * @author Think
 * @version
 * @since JDK 1.6
 * @see
 */
public class TimerService extends Service {
	
	private HashMap<String, CustomTimer>  mTimers=new HashMap<String,CustomTimer>();
 
	public static  InfoChangeObserver observer;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				int op = bundle.getInt("op");
				String contactId=bundle.getString("ContactId");
				Log.e("op-contacid", op+"-"+contactId);
				switch (op) {
					case 1://开始计时
						startTimer(contactId);
						break;
					case 2://暂停计时
						pauseTimer(contactId);
						break;
					case 3://停止计时
						stopTimer(contactId);
						break;
					case 4:
						exit();
						break;
				}

			}
		}
	}

	private void startTimer(String contactid) {
		if(!mTimers.containsKey(contactid))
		{
		   mTimers.put(contactid, new CustomTimer(contactid));
		   mTimers.get(contactid).start();
		}  
		else {
			  mTimers.get(contactid).start();
		}
	}

	private void stopTimer(String contactid) {
		if(mTimers.containsKey(contactid))
		{
			mTimers.get(contactid).stop();
			mTimers.remove(contactid);
		} 
	}
	
	private void pauseTimer(String contactid)
	{
		if(mTimers.containsKey(contactid))
		{
			mTimers.get(contactid).pause();
		}
	}
	
	
	public void exit()
	{
		Iterator it=mTimers.keySet().iterator();      
        while(it.hasNext())
        {
            String str=(String)it.next();               
            mTimers.get(str).stop();
            mTimers.remove(str);      
        }   
		
	}

	@Override
	public void onDestroy() {
		@SuppressWarnings("rawtypes")
		Iterator it=mTimers.keySet().iterator();      
        while(it.hasNext())
        {
            String str=(String)it.next();               
            mTimers.get(str).stop();
            mTimers.remove(str);      
        }   
		super.onDestroy();
	}

}
