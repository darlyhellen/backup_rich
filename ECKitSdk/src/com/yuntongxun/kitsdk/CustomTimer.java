/**
 * Project Name:ECKitSdk
 * File Name:CustomTimer.java
 * Package Name:com.yuntongxun.kitsdk
 * Date:2015-11-3上午10:04:57
 * Copyright (c) 2015, chenzhou1025@126.com All Rights Reserved.
 *
 */

package com.yuntongxun.kitsdk;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.kitsdk.beans.ChatInfoBean;
import com.yuntongxun.kitsdk.ui.chatting.model.IMChattingHelper;

/**
 * ClassName:CustomTimer <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2015-11-3 上午10:04:57 <br/>
 * 
 * @author Think
 * @version
 * @since JDK 1.6
 * @see
 */
public class CustomTimer {

	private Timer timer;

	private int count;

	private TimerTask timerTask;

	public static InfoChangeObserver observer;

	public int controlFlag = 2;// 2:初始化 0:开始 1：停止 3是暂停

	private static int delay = 1000; // 1s
	private static int period = 1000; // 1s

	private String contactId;

	private int MAX_COUNT = 15*60;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x01) {
				try {
					IMChattingHelper.getInstance();
					ChatInfoBean chatinfoBean = IMChattingHelper.chatControllerListener
							.getChatInfoDb().findFirst(
									Selector.from(ChatInfoBean.class).where(
											"docInfoBeanId", "=", contactId));
					if (chatinfoBean != null) {
						chatinfoBean.setTimeout(true);
						chatinfoBean.setComment(false);
						chatinfoBean.setStatus(false);
						IMChattingHelper.getInstance();
						IMChattingHelper.chatControllerListener
								.getChatInfoDb().saveOrUpdate(chatinfoBean);
					}
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				IMChattingHelper.sendECMessage((ECMessage) msg.obj);
				Intent intnet = new Intent("com.rayelink.closesubject");
				intnet.putExtra("docId", contactId);
				ECDeviceKit.getmContext().sendBroadcast(intnet);
				IMChattingHelper.getInstance();
				IMChattingHelper.chatControllerListener
						.timeStop(contactId);
			}
		}
	};

	public CustomTimer(String mContactId) {
		contactId = mContactId;
		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				if (controlFlag == 0) {
					if (count <= MAX_COUNT) {
						Log.e("CustomTimer" + contactId, count + "");
						if (observer != null && count < MAX_COUNT)
							observer.onDataChanged(contactId, count);
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
						count++;
						if (count == MAX_COUNT) { 
							controlFlag=1;
							try {
								ChatInfoBean tempInfo = IMChattingHelper.chatControllerListener
										.getChatInfoDb()
										.findFirst(
												Selector.from(
														ChatInfoBean.class)
														.where("docInfoBeanId",
																"=", contactId));

								if (tempInfo != null
										&& tempInfo.getSubjectID() != null) {
									Log.e("CustomTimer-tempInfo",
											"closeSubject");
									IMChattingHelper.getInstance();
									IMChattingHelper.chatControllerListener
											.closeSubject(
													tempInfo.getSubjectID(),
													contactId, mHandler);
								}
							} catch (DbException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} 
				} else if (controlFlag == 3) {
					if (observer != null)
						observer.onDataChanged(contactId, count);
				}

			}
		};
	}

	public void start() {
		if (controlFlag == 2) {
			controlFlag = 0;
			timer.schedule(timerTask, delay, period);
		} else if (controlFlag == 3) {
			controlFlag = 0;
		}
	}

	public void stop() {
		Log.e("CustomTimer-stop", "stop");
		try {
			controlFlag = 1;
			if (timerTask != null)
				timerTask.cancel();
			if (timer != null)
				timer.cancel();
		} catch (Exception e) {
			Log.e("CustomTimer", e.getMessage());
		} finally {
			if (observer != null)
				observer.onDataChanged(contactId, 0);
		}
	}
	

	public void pause() {
		controlFlag = 3;
	}

}
