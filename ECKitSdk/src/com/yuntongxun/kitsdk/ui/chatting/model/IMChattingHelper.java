/*
 *  Copyright (c) 2013 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.yuntongxun.kitsdk.ui.chatting.model;

import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.yuntongxun.ecsdk.ECChatManager;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.ECMessage.MessageStatus;
import com.yuntongxun.ecsdk.OnChatReceiveListener;
import com.yuntongxun.ecsdk.PersonInfo;
import com.yuntongxun.ecsdk.im.ECFileMessageBody;
import com.yuntongxun.ecsdk.im.ECImageMessageBody;
import com.yuntongxun.ecsdk.im.ECMessageNotify;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuntongxun.ecsdk.im.ECVoiceMessageBody;
import com.yuntongxun.ecsdk.im.group.ECGroupNoticeMessage;
import com.yuntongxun.kitsdk.ChatControllerListener;
import com.yuntongxun.kitsdk.ECDeviceKit;
import com.yuntongxun.kitsdk.beans.ChatInfoBean;
import com.yuntongxun.kitsdk.beans.ClientUser;
import com.yuntongxun.kitsdk.beans.DemoGroupNotice;
import com.yuntongxun.kitsdk.core.CCPAppManager;
import com.yuntongxun.kitsdk.db.GroupNoticeSqlManager;
import com.yuntongxun.kitsdk.db.IMessageSqlManager;
import com.yuntongxun.kitsdk.db.ImgInfoSqlManager;
import com.yuntongxun.kitsdk.group.GroupNoticeHelper;
import com.yuntongxun.kitsdk.setting.ECPreferenceSettings;
import com.yuntongxun.kitsdk.setting.ECPreferences;
import com.yuntongxun.kitsdk.ui.ECChattingActivity;
import com.yuntongxun.kitsdk.utils.DateUtil;
import com.yuntongxun.kitsdk.utils.DemoUtils;
import com.yuntongxun.kitsdk.utils.ECNotificationManager;
import com.yuntongxun.kitsdk.utils.FileAccessor;
import com.yuntongxun.kitsdk.utils.LogUtil;
import com.yuntongxun.kitsdk.utils.MD5Util;

public class IMChattingHelper implements OnChatReceiveListener,
		ECChatManager.OnDownloadMessageListener {

	private static final String TAG = "ECSDK_Kit.IMChattingHelper";
	public static final String INTENT_ACTION_SYNC_MESSAGE = "com.yuntongxun.kitsdk_sync_message";
	public static final String GROUP_PRIVATE_TAG = "@priategroup.com";
	public static final int MAX_OFFINE_COUNT = 100;
	private static HashMap<String, SyncMsgEntry> syncMessage = new HashMap<String, SyncMsgEntry>();

	public static boolean isAutoGetOfflineMsg = true;
	private static IMChattingHelper sInstance;
	private boolean isSyncOffline = false;

	public static IMChattingHelper getInstance() {
		if (sInstance == null) {
			sInstance = new IMChattingHelper();
		}
		return sInstance;
	}

	/** 云通讯SDK聊天功能接口 */
	private ECChatManager mChatManager;
	/** 全局处理所有的IM消息发送回调 */
	private ChatManagerListener mListener;
	/** 处理成员获取对象回调 */
	public UserFaceManagerListener mUserListener;
	/* 处理聊天窗口的对象回调 */
	public static ChatControllerListener chatControllerListener;

	public void setChatControllerListener(ChatControllerListener listener) {
		chatControllerListener = listener;
	}

	public void setUserFaceManagerListener(UserFaceManagerListener mListener) {
		this.mUserListener = mListener;
	}

	public interface UserFaceManagerListener {
		public void getUserFace(ECMessage message, TextView nameView,
				ImageView headView);
	}

	/** 是否是同步消息 */
	private boolean isFirstSync = false;

	private IMChattingHelper() {
		mChatManager = ECDevice.getECChatManager();
		mListener = new ChatManagerListener();
	}

	private void checkChatManager() {
		mChatManager = ECDevice.getECChatManager();
	}

	/**
	 * 消息发送报告
	 */
	private OnMessageReportCallback mOnMessageReportCallback;

	/**
	 * 发送ECMessage 消息
	 * 
	 * @param msg
	 */
	public static long sendECMessage(ECMessage msg) {
		getInstance().checkChatManager();
		// 获取一个聊天管理器
		ECChatManager manager = getInstance().mChatManager;
		if (manager != null) {
			// 调用接口发送IM消息
			msg.setMsgTime(System.currentTimeMillis());
			if (msg.getUserData() == null
					|| msg.getType() == ECMessage.Type.VOICE)
				msg.setUserData(ECChattingActivity.USER_DATA);
			manager.sendMessage(msg, getInstance().mListener);
			// 保存发送的消息到数据库
		} else {
			msg.setMsgStatus(ECMessage.MessageStatus.FAILED);
		}
		if (msg.getType().equals(ECMessage.Type.TXT)
				&& "BMY://CloseSubject"
						.equalsIgnoreCase(((ECTextMessageBody) (msg.getBody()))
								.getMessage())) {
			return 0;
		}
		return IMessageSqlManager.insertIMessage(msg,
				ECMessage.Direction.SEND.ordinal());
	}
	
	public static long sendECMessageNotSave(ECMessage msg) {
		getInstance().checkChatManager();
		// 获取一个聊天管理器
		ECChatManager manager = getInstance().mChatManager;
		if (manager != null) {
			// 调用接口发送IM消息
			msg.setMsgTime(System.currentTimeMillis());
			if (msg.getUserData() == null
					|| msg.getType() == ECMessage.Type.VOICE)
				msg.setUserData(ECChattingActivity.USER_DATA);
			manager.sendMessage(msg, getInstance().mListener);
			// 保存发送的消息到数据库
		} else {
			msg.setMsgStatus(ECMessage.MessageStatus.FAILED);
		}
		 return 0;
	}

	/**
	 * 消息重发
	 * 
	 * @param msg
	 * @return
	 */
	public static long reSendECMessage(ECMessage msg) {
		ECChatManager manager = getInstance().mChatManager;
		if (manager != null) {
			// 调用接口发送IM消息
			String oldMsgId = msg.getMsgId();
			if (msg.getUserData() == null
					|| msg.getType() == ECMessage.Type.VOICE)
				msg.setUserData(ECChattingActivity.USER_DATA);
			manager.sendMessage(msg, getInstance().mListener);
			if (msg.getType() == ECMessage.Type.IMAGE) {
				ImgInfo imgInfo = ImgInfoSqlManager.getInstance().getImgInfo(
						oldMsgId);
				if (imgInfo == null
						|| TextUtils.isEmpty(imgInfo.getBigImgPath())) {
					return -1;
				}
				String bigImagePath = new File(FileAccessor.getImagePathName(),
						imgInfo.getBigImgPath()).getAbsolutePath();
				imgInfo.setMsglocalid(msg.getMsgId());
				ECFileMessageBody body = (ECFileMessageBody) msg.getBody();
				body.setLocalUrl(bigImagePath);
				BitmapFactory.Options options = DemoUtils
						.getBitmapOptions(new File(FileAccessor.IMESSAGE_IMAGE,
								imgInfo.getThumbImgPath()).getAbsolutePath());
				msg.setUserData("outWidth://" + options.outWidth
						+ ",outHeight://" + options.outHeight + ",THUMBNAIL://"
						+ msg.getMsgId());
				ImgInfoSqlManager.getInstance().updateImageInfo(imgInfo);
			}
			// 保存发送的消息到数据库
			return IMessageSqlManager.changeResendMsg(msg.getId(), msg);
		}
		return -1;
	}

	public static long sendImageMessage(ImgInfo imgInfo, ECMessage message,
			boolean sendFlag) {

		ECChatManager manager = getInstance().mChatManager;
		if (manager != null) {
			// 调用接口发送IM消息
			message.setUserData(ECChattingActivity.USER_DATA);
			if (sendFlag) {
				manager.sendMessage(message, getInstance().mListener);

				if (TextUtils.isEmpty(message.getMsgId())) {
					return -1;
				}
			} else {
				message.setMsgId(UUID.randomUUID().toString());
				// imgInfo.setMsglocalid();
				message.setMsgStatus(MessageStatus.SUCCESS);
			}
			imgInfo.setMsglocalid(message.getMsgId());
			BitmapFactory.Options options = DemoUtils
					.getBitmapOptions(new File(FileAccessor.IMESSAGE_IMAGE,
							imgInfo.getThumbImgPath()).getAbsolutePath());
			message.setUserData("outWidth://" + options.outWidth
					+ ",outHeight://" + options.outHeight + ",THUMBNAIL://"
					+ message.getMsgId());
			long row = IMessageSqlManager.insertIMessage(message,
					ECMessage.Direction.SEND.ordinal());
			if (row != -1) {
				return ImgInfoSqlManager.getInstance().insertImageInfo(imgInfo);
			}
		}
		return -1;

	}

	public void getPersonInfo() {
		LogUtil.d(TAG, "[getPersonInfo] currentVersion :");
		final ClientUser clientUser = CCPAppManager.getClientUser();
		if (clientUser == null) {
			return;
		}
		LogUtil.d(TAG,
				"[getPersonInfo] currentVersion :" + clientUser.getpVersion()
						+ " ,ServerVersion: " + mServicePersonVersion);
		if (clientUser.getpVersion() < mServicePersonVersion) {
			ECDevice.getECChatManager().getPersonInfo(
					new ECChatManager.OnGetPersonInfoListener() {
						@Override
						public void onGetPersonInfoComplete(ECError e,
								PersonInfo p) {
							clientUser.setpVersion(p.getVersion());
							clientUser.setSex(p.getSex().ordinal() + 1);
							clientUser.setUserName(p.getNickName());
							if (!TextUtils.isEmpty(p.getBirth())) {
								clientUser.setBirth(DateUtil
										.getActiveTimelong(p.getBirth()));
							}
							String newVersion = clientUser.toString();
							LogUtil.d(TAG,
									"[getPersonInfo -result] ClientUser :"
											+ newVersion);
							try {
								ECPreferences
										.savePreference(
												ECPreferenceSettings.SETTINGS_REGIST_AUTO,
												newVersion, true);
							} catch (InvalidClassException e1) {
								e1.printStackTrace();
							}

						}
					});
		}

	}

	private class ChatManagerListener implements
			ECChatManager.OnSendMessageListener {

		public void onComplete(ECError error) {

		}

		@Override
		public void onSendMessageComplete(ECError error, ECMessage message) {
			if (message == null) {
				return;
			}
			// 处理ECMessage的发送状态
			if (message != null) {
				if (message.getType() == ECMessage.Type.VOICE) {
					try {
						DemoUtils.playNotifycationMusic(
								CCPAppManager.getContext(),
								"sound/voice_message_sent.mp3");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				IMessageSqlManager.setIMessageSendStatus(message.getMsgId(),
						message.getMsgStatus().ordinal());
				if (!message.getTo().contains("g")) {
					if (chatControllerListener != null) {
						chatControllerListener.timePause(message.getTo());
					}
				}
				IMessageSqlManager.notifyMsgChanged(message.getSessionId());
				if (mOnMessageReportCallback != null) {
					mOnMessageReportCallback.onMessageReport(error, message);
				}
				Intent intnet = new Intent("com.rayelink.refreshchat");
				intnet.putExtra("docId",
						message.getTo());
				ECDeviceKit.getmContext().sendBroadcast(intnet);				
				return;
			}
		}

		@Override
		public void onProgress(String msgId, int total, int progress) {
			// 处理发送文件IM消息的时候进度回调
			LogUtil.d(TAG, "[IMChattingHelper - onProgress] msgId：" + msgId
					+ " ,total：" + total + " ,progress:" + progress);
		}

	}

	public static void setOnMessageReportCallback(
			OnMessageReportCallback callback) {
		getInstance().mOnMessageReportCallback = callback;
	}
	
	public static OnMessageReportCallback getOnMessageReportCallback()
	{
		return getInstance().mOnMessageReportCallback;
	}

	public interface OnMessageReportCallback {
		void onMessageReport(ECError error, ECMessage message);

		void onPushMessage(String sessionId, List<ECMessage> msgs);
	}

	private int getMaxVersion() {
		int maxVersion = IMessageSqlManager.getMaxVersion();
		int maxVersion1 = GroupNoticeSqlManager.getMaxVersion();
		return maxVersion > maxVersion1 ? maxVersion : maxVersion1;
	}

	/**
	 * 收到新的IM文本和附件消息
	 */
	@Override
	public void OnReceivedMessage(ECMessage msg) {
		if (msg == null) {
			return;
		}


		postReceiveMessage(msg, true);

	}

	/**
	 * 处理接收消息
	 * 
	 * @param msg
	 * @param showNotice
	 */
	private synchronized void postReceiveMessage(ECMessage msg,
			boolean showNotice) {
		
		if (!msg.getForm().contains("g")) {
			if (chatControllerListener != null) {
				chatControllerListener.timeStart(msg.getForm());
			}
			String userData = msg.getUserData();
			String subjectId;
			if (userData != null && !TextUtils.isEmpty(userData)
					&& msg.getUserData().charAt(32) != '1') {
				String userinfo = userData.substring(32);
				String userinfo2 = userData.substring(33);
				String md5String = userData.substring(0, 32);
				String mds5 = MD5Util.md5(userinfo);
				if (!mds5.equalsIgnoreCase(md5String))
					return;
				subjectId = userinfo2.substring(0, 32).replace("~", "");
				try {

					ChatInfoBean chatInfoBean = chatControllerListener
							.getChatInfoDb().findFirst(
									Selector.from(ChatInfoBean.class).where(
											"SubjectID", "=", subjectId));

					if (chatInfoBean == null) {
						IMChattingHelper.chatControllerListener
								.handleMessage(subjectId, msg.getForm());
					} else {
						if (!chatInfoBean.isStatus()) {
							chatInfoBean.setStatus(true);
							chatControllerListener.getChatInfoDb()
									.saveOrUpdate(chatInfoBean);
							Intent intnet = new Intent("com.rayelink.subtitle");
							intnet.putExtra("docId",
									chatInfoBean.getDocInfoBeanId());
							intnet.putExtra("docStatus",
									chatInfoBean.isStatus());
							ECDeviceKit.getmContext().sendBroadcast(intnet);
						}
					}
				} catch (DbException e) {
					e.printStackTrace();
				}

			}

		}
		// 接收到的IM消息，根据IM消息类型做不同的处理
		// IM消息类型：ECMessage.Type
		if (msg.getType() != ECMessage.Type.TXT) {
			ECFileMessageBody body = (ECFileMessageBody) msg.getBody();

			if (!TextUtils.isEmpty(body.getRemoteUrl())) {
				boolean thumbnail = false;
				String fileExt = DemoUtils
						.getExtensionName(body.getRemoteUrl());
//				// 尝试当传递过来的是文件格式的图片内容。则进行文件和图片类型转换。
				if (msg.getType() == ECMessage.Type.FILE) {
					if (fileExt.equalsIgnoreCase("png")
							|| fileExt.equalsIgnoreCase("jpeg")
							|| fileExt.equalsIgnoreCase("bmp")) {
						// 假如文件后缀名为这些类型的图片。则进行转换。
						msg.setType(ECMessage.Type.IMAGE);
						ECImageMessageBody messageBody = new ECImageMessageBody();
						messageBody.setThumbnailFileUrl(body.getRemoteUrl()+"_thum");
						messageBody.setRemoteUrl(body.getRemoteUrl());
						msg.setBody(messageBody);
					}
				}
//				// 尝试当传递过来的是文件格式的图片内容。则进行文件和图片类型转换。

				if (msg.getType() == ECMessage.Type.VOICE) {
					body.setLocalUrl(new File(FileAccessor.getVoicePathName(),
							DemoUtils.md5(String.valueOf(System
									.currentTimeMillis())) + ".amr")
							.getAbsolutePath());
				} else if (msg.getType() == ECMessage.Type.IMAGE) {
					ECImageMessageBody imageBody = (ECImageMessageBody) msg.getBody();
					thumbnail = TextUtils.isEmpty(imageBody
							.getThumbnailFileUrl());
					imageBody.setLocalUrl(new File(FileAccessor 
							.getImagePathName(), DemoUtils
							.md5(thumbnail ? imageBody.getThumbnailFileUrl()
									: imageBody.getRemoteUrl())
							+ "." + fileExt).getAbsolutePath());
				} else {
					body.setLocalUrl(new File(FileAccessor.getFilePathName(),
							DemoUtils.md5(String.valueOf(System
									.currentTimeMillis())) + "." + fileExt)
							.getAbsolutePath());

				}
				if (syncMessage != null) {
					syncMessage.put(msg.getMsgId(), new SyncMsgEntry(
							showNotice, msg));
				}
				if (mChatManager != null) {
					if (!thumbnail) {
						mChatManager.downloadThumbnailMessage(msg, this);
					} else {
						mChatManager.downloadMediaMessage(msg, this);
					}
				}
				if (TextUtils.isEmpty(body.getFileName())
						&& !TextUtils.isEmpty(body.getRemoteUrl())) {
					body.setFileName(FileAccessor.getFileName(body
							.getRemoteUrl()));
				}
				msg.setUserData("fileName=" + body.getFileName());
				// msg.setUserData(ECChattingActivity.USER_DATA);
				/*
				 * if (IMessageSqlManager.insertIMessage(msg, msg.getDirection()
				 * .ordinal()) > 0) { return; }
				 */
			} else {
				LogUtil.e(TAG, "ECMessage fileUrl: null");
			}
		}
		try {

			if (msg.getType() == ECMessage.Type.TXT) {
				if ("BMY://CloseSubject".equals(((ECTextMessageBody) msg
						.getBody()).getMessage()))// 医生端主动结束咨询
				{
					String userData = msg.getUserData();
					String userinfo2 = userData.substring(33);
				 
					String subjectId = userinfo2.substring(0, 32).replace("~", "");
					
					try {
						ChatInfoBean chatInfoBean = chatControllerListener
								.getChatInfoDb().findFirst(
										Selector.from(ChatInfoBean.class)
												.where("docInfoBeanId", "=",
														msg.getForm()));
						chatInfoBean.setTimeout(true);
						chatInfoBean.setStatus(false);
						chatControllerListener.getChatInfoDb().saveOrUpdate(
								chatInfoBean);
						IMChattingHelper.getInstance();
						IMChattingHelper.chatControllerListener.closeSubject(subjectId, msg.getForm(), mHandler);
						// 关闭当前聊天窗口变化
					} catch (DbException e) {

						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (msg.getUserData() != null
						&& !msg.getUserData().equals("(null)")
						&& msg.getUserData().charAt(32) == '1') {
					// 计时
					String s = msg.getUserData();
					String userinfo = s.substring(32);
					String userinfo2 = s.substring(33);
					String md5String = s.substring(0, 32);
					String mds5 = MD5Util.md5(userinfo);
					if (!mds5.equalsIgnoreCase(md5String))
						return;

					String subjectId = userinfo2.substring(0, 32).replace("~",
							"");

					String docVoip = userinfo2.substring(32, 64).replace("~",
							"");
					if (chatControllerListener != null)
						chatControllerListener.timeStop(msg.getForm());
					IMChattingHelper.getInstance();
					// 转诊
					IMChattingHelper.chatControllerListener
							.transferTreat(msg.getForm(), docVoip, subjectId);
				}
			}
		} catch (Exception e) {

		}
		boolean flag=false;
		if(msg.getType()==ECMessage.Type.TXT)
		{
			boolean flag1=((ECTextMessageBody) msg.getBody()).getMessage().toString().contains("该病人是由");
			boolean flag2=((ECTextMessageBody) msg.getBody()).getMessage().toString().contains("医生转诊过来的咨询用户。");
			flag=flag1&&flag2;
		}
		 
		if(flag)
			return;
		
		if (!(msg.getType() == ECMessage.Type.TXT && "BMY://CloseSubject"
				.equals(((ECTextMessageBody) msg.getBody()).getMessage()))) {
			if(!IMessageSqlManager.isHaveSameMessage(msg)){
				if (IMessageSqlManager.insertIMessage(msg, msg.getDirection()
						.ordinal()) <= 0) {
					return;
				}
			}
		}
		
		if (mOnMessageReportCallback != null) {
			ArrayList<ECMessage> msgs = new ArrayList<ECMessage>();
			msgs.add(msg);
			mOnMessageReportCallback.onPushMessage(msg.getSessionId(), msgs);
		}

		// 是否状态栏提示
		if (isApplicationBroughtToBackground(ECDeviceKit.getmContext()
				.getApplicationContext()))
			showNotification(msg);
		
		if(IMChattingHelper.getOnMessageReportCallback()==null){
			Intent intnet = new Intent("com.rayelink.refreshchat");
			intnet.putExtra("docId",
					msg.getForm());
			ECDeviceKit.getmContext().sendBroadcast(intnet);
		}
		
	}
	
	
	private synchronized void postReceiveOfflineMessage(ECMessage msg,
			boolean showNotice) {
		
		if (!msg.getForm().contains("g")) {
			if (chatControllerListener != null) {
				chatControllerListener.timeStart(msg.getForm());
			}
			String userData = msg.getUserData();
			String subjectId;
			if (userData != null && !TextUtils.isEmpty(userData)
					&& msg.getUserData().charAt(32) != '1') {
				String userinfo = userData.substring(32);
				String userinfo2 = userData.substring(33);
				String md5String = userData.substring(0, 32);
				String mds5 = MD5Util.md5(userinfo);
				if (!mds5.equalsIgnoreCase(md5String))
					return;
				subjectId = userinfo2.substring(0, 32).replace("~", "");
				Log.e("userinfo",userinfo);
				Log.e("toId",msg.getTo()+"");
				Log.e("fromId",msg.getForm()+"");
				if(msg.getType() == ECMessage.Type.TXT)
					Log.e("message-content",((ECTextMessageBody)msg.getBody()).getMessage()+"");
				Log.e("subjectId",subjectId);
				try {
					ChatInfoBean chatInfoBean = chatControllerListener
							.getChatInfoDb().findFirst(
									Selector.from(ChatInfoBean.class).where(
											"SubjectID", "=", subjectId));

					if (chatInfoBean == null) {
						IMChattingHelper.chatControllerListener
								.handleMessage(subjectId, msg.getForm());
					} else {
						if (!chatInfoBean.isStatus()) {
							chatInfoBean.setStatus(true);
							chatControllerListener.getChatInfoDb()
									.saveOrUpdate(chatInfoBean);
							Intent intnet = new Intent("com.rayelink.subtitle");
							intnet.putExtra("docId",
									chatInfoBean.getDocInfoBeanId());
							intnet.putExtra("docStatus",
									chatInfoBean.isStatus());
							ECDeviceKit.getmContext().sendBroadcast(intnet);
						}
					}
				} catch (DbException e) {
					e.printStackTrace();
				}

			}

		}
		// 接收到的IM消息，根据IM消息类型做不同的处理
		// IM消息类型：ECMessage.Type
		if (msg.getType() != ECMessage.Type.TXT) {
			ECFileMessageBody body = (ECFileMessageBody) msg.getBody();

			if (!TextUtils.isEmpty(body.getRemoteUrl())) {
				boolean thumbnail = false;
				String fileExt = DemoUtils
						.getExtensionName(body.getRemoteUrl());
//				// 尝试当传递过来的是文件格式的图片内容。则进行文件和图片类型转换。
				if (msg.getType() == ECMessage.Type.FILE) {
					if (fileExt.equalsIgnoreCase("png")
							|| fileExt.equalsIgnoreCase("jpeg")
							|| fileExt.equalsIgnoreCase("bmp")) {
						// 假如文件后缀名为这些类型的图片。则进行转换。
						msg.setType(ECMessage.Type.IMAGE);
						ECImageMessageBody messageBody = new ECImageMessageBody();
						messageBody.setThumbnailFileUrl(body.getRemoteUrl()+"_thum");
						messageBody.setRemoteUrl(body.getRemoteUrl());
						msg.setBody(messageBody);
					}
				}
//				// 尝试当传递过来的是文件格式的图片内容。则进行文件和图片类型转换。

				if (msg.getType() == ECMessage.Type.VOICE) {
					body.setLocalUrl(new File(FileAccessor.getVoicePathName(),
							DemoUtils.md5(String.valueOf(System
									.currentTimeMillis())) + ".amr")
							.getAbsolutePath());
				} else if (msg.getType() == ECMessage.Type.IMAGE) {
					ECImageMessageBody imageBody = (ECImageMessageBody) msg.getBody();
					thumbnail = TextUtils.isEmpty(imageBody
							.getThumbnailFileUrl());
					imageBody.setLocalUrl(new File(FileAccessor 
							.getImagePathName(), DemoUtils
							.md5(thumbnail ? imageBody.getThumbnailFileUrl()
									: imageBody.getRemoteUrl())
							+ "." + fileExt).getAbsolutePath());
				} else {
					body.setLocalUrl(new File(FileAccessor.getFilePathName(),
							DemoUtils.md5(String.valueOf(System
									.currentTimeMillis())) + "." + fileExt)
							.getAbsolutePath());

				}
				if (syncMessage != null) {
					syncMessage.put(msg.getMsgId(), new SyncMsgEntry(
							showNotice, msg));
				}
				if (mChatManager != null) {
					if (!thumbnail) {
						mChatManager.downloadThumbnailMessage(msg, this);
					} else {
						mChatManager.downloadMediaMessage(msg, this);
					}
				}
				if (TextUtils.isEmpty(body.getFileName())
						&& !TextUtils.isEmpty(body.getRemoteUrl())) {
					body.setFileName(FileAccessor.getFileName(body
							.getRemoteUrl()));
				}
				msg.setUserData("fileName=" + body.getFileName());
				// msg.setUserData(ECChattingActivity.USER_DATA);
				/*
				 * if (IMessageSqlManager.insertIMessage(msg, msg.getDirection()
				 * .ordinal()) > 0) { return; }
				 */
			} else {
				LogUtil.e(TAG, "ECMessage fileUrl: null");
			}
		}
		try {

			if (msg.getType() == ECMessage.Type.TXT) {
				if ("BMY://CloseSubject".equals(((ECTextMessageBody) msg
						.getBody()).getMessage()))// 医生端主动结束咨询
				{
					String userData = msg.getUserData();
					String userinfo2 = userData.substring(33);
				 
					String subjectId = userinfo2.substring(0, 32).replace("~", "");
					
					try {
						ChatInfoBean chatInfoBean = chatControllerListener
								.getChatInfoDb().findFirst(
										Selector.from(ChatInfoBean.class)
												.where("docInfoBeanId", "=",
														msg.getForm()));
						chatInfoBean.setTimeout(true);
						chatInfoBean.setStatus(false);
						chatControllerListener.getChatInfoDb().saveOrUpdate(
								chatInfoBean);
						IMChattingHelper.getInstance();
						IMChattingHelper.chatControllerListener.closeSubject(subjectId, msg.getForm(), mHandler);
						// 关闭当前聊天窗口变化
					} catch (DbException e) {

						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (msg.getUserData() != null
						&& !msg.getUserData().equals("(null)")
						&& msg.getUserData().charAt(32) == '1') {
					// 计时
					String s = msg.getUserData();
					String userinfo = s.substring(32);
					String userinfo2 = s.substring(33);
					String md5String = s.substring(0, 32);
					String mds5 = MD5Util.md5(userinfo);
					if (!mds5.equalsIgnoreCase(md5String))
						return;

					String subjectId = userinfo2.substring(0, 32).replace("~",
							"");

					String docVoip = userinfo2.substring(32, 64).replace("~",
							"");
					if (chatControllerListener != null)
						chatControllerListener.timeStop(msg.getForm());
					CustomCallBack callBack=new CustomCallBack();
					callBack.from=msg.getForm();
					callBack.docVoip=docVoip;
					callBack.subjectId=subjectId;
					IMChattingHelper.chatControllerListener.getSubjectInfo(subjectId, msg.getForm(), callBack);
				}
			}
		} catch (Exception e) {

		}
		boolean flag=false;
		if(msg.getType()==ECMessage.Type.TXT)
		{
			boolean flag1=((ECTextMessageBody) msg.getBody()).getMessage().toString().contains("该病人是由");
			boolean flag2=((ECTextMessageBody) msg.getBody()).getMessage().toString().contains("医生转诊过来的咨询用户。");
			flag=flag1&&flag2;
		}
		 
		if(flag)
			return;
		
		if (!(msg.getType() == ECMessage.Type.TXT && "BMY://CloseSubject"
				.equals(((ECTextMessageBody) msg.getBody()).getMessage()))) {
			if(!IMessageSqlManager.isHaveSameMessage(msg)){
				if (IMessageSqlManager.insertIMessage(msg, msg.getDirection()
						.ordinal()) <= 0) {
					return;
				}
			}
		}
		
		if (mOnMessageReportCallback != null) {
			ArrayList<ECMessage> msgs = new ArrayList<ECMessage>();
			msgs.add(msg);
			mOnMessageReportCallback.onPushMessage(msg.getSessionId(), msgs);
		}

		// 是否状态栏提示
		if (isApplicationBroughtToBackground(ECDeviceKit.getmContext()
				.getApplicationContext()))
			showNotification(msg);
		
		if(IMChattingHelper.getOnMessageReportCallback()==null){
			Intent intnet = new Intent("com.rayelink.refreshchat");
			intnet.putExtra("docId",
					msg.getForm());
			ECDeviceKit.getmContext().sendBroadcast(intnet);
		}
		
	}
	
	
	class  CustomCallBack extends RequestCallBack<String>
	{

		public DbUtils dbUtils=IMChattingHelper.getInstance().chatControllerListener
				.getChatInfoDb();
		
		public String from;
		public String docVoip;
		public String subjectId;
		
		
		@Override
		public void onSuccess(ResponseInfo<String> arg0) {
			try {
				JSONObject jsonObject = new JSONObject(
						arg0.result);
				JSONObject data = jsonObject
						.getJSONObject("Data");
				if (data != null) {
					Log.e("getSubjectInfo", data.toString());
					JSONObject subject = data
							.getJSONObject("Subject");
					if (subject != null) {
						 
						Log.e("getSubjectInfo-subject",
								subject.toString());
						// 是否有此医生的会话
					
						ChatInfoBean chatinfoBean = dbUtils
								.findFirst(Selector.from(
										ChatInfoBean.class)
										.where("docInfoBeanId",
												"=", from));
						// 如果有医生在线
						if (chatinfoBean == null) {
							chatinfoBean = new ChatInfoBean();
						}
						chatinfoBean.setSubjectType(subject
								.getInt("SubjectType") + "");
						chatinfoBean.setSubjectID(subject
								.getInt("ID") + "");
						chatinfoBean.setStatus("1".equals(subject.getString("Status")));
						chatinfoBean.setComment(false);
						chatinfoBean.setTimeout(false);
						dbUtils.saveOrUpdate(chatinfoBean);
						//0 ：结束会话   1：未结束会话  3：已经结束，医生把这个会话关闭掉
						if("1".equals(subject.getString("Status")))
						{
							// 转诊
							IMChattingHelper.chatControllerListener
									.transferTreat(from, docVoip, subjectId);
						}else 
						{
							chatinfoBean.setComment(true);
							chatinfoBean.setTimeout(true);
							dbUtils.saveOrUpdate(chatinfoBean);
						}
					}
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		@Override
		public void onFailure(HttpException arg0, String arg1) {
			// TODO Auto-generated method stub

		}
		
	}
	
 
	
	
	private Handler mHandler=new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			if(msg.what==0x01)
			{
				ECMessage message=(ECMessage)msg.obj;
				try {
					IMChattingHelper.getInstance();
					ChatInfoBean	chatinfoBean = IMChattingHelper.chatControllerListener
							.getChatInfoDb().findFirst(Selector
							.from(ChatInfoBean.class).where("docInfoBeanId", "=", message.getTo()));
					if(chatinfoBean!=null){
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
				Intent intnet = new Intent("com.rayelink.closesubject");
				intnet.putExtra("docId",
						message.getTo());
				ECDeviceKit.getmContext().sendBroadcast(intnet);
				IMChattingHelper.getInstance();
				IMChattingHelper.chatControllerListener.timeStop(message.getTo());
			}
		}
	};

	public static boolean isApplicationBroughtToBackground(final Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;

	}

	private static void showNotification(ECMessage msg) {
		if (checkNeedNotification(msg.getSessionId())) {
			ECNotificationManager.getInstance().forceCancelNotification();
			String lastMsg = "";
			if (msg.getType() == ECMessage.Type.TXT) {
				lastMsg = ((ECTextMessageBody) msg.getBody()).getMessage();
			}
			if (chatControllerListener != null) {
				chatControllerListener.notifycationSend(
						CCPAppManager.getContext(), "你有新的消息来！");
			}

		}
	}

	public static void checkDownFailMsg() {
		getInstance().postCheckDownFailMsg();
	}

	private void postCheckDownFailMsg() {
		List<ECMessage> downdFailMsg = IMessageSqlManager.getDowndFailMsg();
		if (downdFailMsg == null || downdFailMsg.isEmpty()) {
			return;
		}
		for (ECMessage msg : downdFailMsg) {
			ECImageMessageBody body = (ECImageMessageBody) msg.getBody();
			body.setThumbnailFileUrl(body.getRemoteUrl() + "_thum");
			if (syncMessage != null) {
				syncMessage.put(msg.getMsgId(), new SyncMsgEntry(false, msg));
			}
			if (mChatManager != null) {
				mChatManager.downloadThumbnailMessage(msg, this);
			}
		}

	}

	/**
	 * 是否需要状态栏通知
	 * 
	 * @param contactId
	 */
	public static boolean checkNeedNotification(String contactId) {
		String currentChattingContactId = ECPreferences
				.getSharedPreferences()
				.getString(
						ECPreferenceSettings.SETTING_CHATTING_CONTACTID.getId(),
						(String) ECPreferenceSettings.SETTING_CHATTING_CONTACTID
								.getDefaultValue());
		if (contactId == null) {
			return true;
		}
		if (contactId.equals(currentChattingContactId)) {
			return false;
		}
		return true;
	}

	@Override
	public void OnReceiveGroupNoticeMessage(ECGroupNoticeMessage notice) {
		if (notice == null) {
			return;
		}

		// 接收到的群组消息，根据群组消息类型做不同处理
		// 群组消息类型：ECGroupMessageType
		GroupNoticeHelper.insertNoticeMessage(notice,
				new GroupNoticeHelper.OnPushGroupNoticeMessageListener() {

					@Override
					public void onPushGroupNoticeMessage(DemoGroupNotice system) {
						IMessageSqlManager
								.notifyMsgChanged(GroupNoticeSqlManager.CONTACT_ID);

						ECMessage msg = ECMessage
								.createECMessage(ECMessage.Type.TXT);
						msg.setSessionId(system.getSender());
						msg.setForm(system.getSender());
						ECTextMessageBody tx = new ECTextMessageBody(system
								.getContent());
						msg.setBody(tx);
						if (isApplicationBroughtToBackground(ECDeviceKit
								.getmContext().getApplicationContext()))
							// 是否状态栏提示
							showNotification(msg);
					}
				});
		/*
		 * // 群组被解散 if (notice.getType() ==
		 * ECGroupNoticeMessage.ECGroupMessageType.DISMISS) { ECDismissGroupMsg
		 * dismissGroupMsg = (ECDismissGroupMsg) notice; // 处理群组被解散通知 //
		 * 将群组从本地缓存中删除并通知UI刷新 } // 有人被移除出群组 if (notice.getType() ==
		 * ECGroupNoticeMessage.ECGroupMessageType.REMOVE_MEMBER) {
		 * ECRemoveMemberMsg removeMemberMsg = (ECRemoveMemberMsg) notice; //
		 * 处理群组移除成员通知 if ("$Smith账号".equals(removeMemberMsg.getMember())) { //
		 * 如果是自己则将从本地群组关联关系中移除 // 通知UI处理刷新 } }
		 * 
		 * // 有人退出群组通知（群组成员） if(notice.getType() ==
		 * ECGroupNoticeMessage.ECGroupMessageType.QUIT) { ECQuitGroupMsg
		 * quitGroupMsg = (ECQuitGroupMsg) notice; // 处理某人退出群组通知 }
		 * 
		 * // 有人加入群组通知（群组成员） if (notice.getType() ==
		 * ECGroupNoticeMessage.ECGroupMessageType.JOIN) { ECJoinGroupMsg
		 * joinGroupMsg = (ECJoinGroupMsg) notice; // 处理某人加入群组通知 }
		 * 
		 * // 有人申请加入群组（仅限于管理员） if(notice.getType() ==
		 * ECGroupNoticeMessage.ECGroupMessageType.PROPOSE) { ECProposerMsg
		 * proposerMsg = (ECProposerMsg) notice; // 处理申请加入群组请求通知 }
		 * 
		 * // 群组管理员邀请加入群组通知（群组成员） if (notice.getType() ==
		 * ECGroupNoticeMessage.ECGroupMessageType.INVITE) { ECInviterMsg
		 * inviterMsg = (ECInviterMsg) notice; // 处理群组管理员邀请加入群组通知 String groupId
		 * = inviterMsg.getGroupId(); }
		 */

	}

	private int mHistoryMsgCount = 0;

	@Override
	public void onOfflineMessageCount(int count) {
		mHistoryMsgCount = count;
	}

	/*
	 * @Override public void onHistoryMessageCount(int count) { //
	 * 注册SDK的参数需要设置如下才能收到该回调 // ECInitParams.setOnChatReceiveListener(new
	 * OnChatReceiveListener()); // count参数标识当前账号的离线消息数 }
	 */

	/*
	 * @Override public int onCountHistoryMessageToGet() { //
	 * 注册SDK的参数需要设置如下才能收到该回调 // ECInitParams.setOnChatReceiveListener(new
	 * OnChatReceiveListener()); // 建议根据onHistoryMessageCount(int
	 * count)设置接收的离线消息数 return count; }
	 */

	@Override
	public int onGetOfflineMessage() {
		// 获取全部的离线历史消息

		return isAutoGetOfflineMsg ? ECDevice.SYNC_OFFLINE_MSG_ALL : 0;
	}

	@Override
	public void onReceiveOfflineMessage(List<ECMessage> msgs) {
		// 离线消息的处理可以参考 void OnReceivedMessage(ECMessage msg)方法
		// 处理逻辑完全一样
		// 参考 IMChattingHelper.java
		if (msgs != null && !msgs.isEmpty() && !isFirstSync)
			isFirstSync = true;
		for (ECMessage msg : msgs) {
			postReceiveOfflineMessage(msg, false);
		}
	}

	@Override
	public void onReceiveOfflineMessageCompletion() {
		// SDK离线消息拉取完成之后会通过该接口通知应用
		// 应用可以在此做类似于Loading框的关闭，Notification通知等等
		try {
			// 如果已经没有需要同步消息的请求时候，则状态栏开始提醒
			ECMessage lastECMessage = IMessageSqlManager.getLastECMessage();
			if (lastECMessage != null && mHistoryMsgCount > 0 && isFirstSync) {
				if (isApplicationBroughtToBackground(ECDeviceKit.getmContext()
						.getApplicationContext()))
					showNotification(lastECMessage);
				// lastECMessage.setSessionId(lastECMessage.getTo().startsWith("G")?lastECMessage.getTo():lastECMessage.getForm());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		isFirstSync = isSyncOffline = false;
		// 无需要同步的消息
		CCPAppManager.getContext().sendBroadcast(
				new Intent(INTENT_ACTION_SYNC_MESSAGE));
	}

	public int mServicePersonVersion = 0;

	@Override
	public void onServicePersonVersion(int version) {
		mServicePersonVersion = version;
	}

	/**
	 * 客服消息
	 * 
	 * @param msg
	 */
	@Override
	public void onReceiveDeskMessage(ECMessage msg) {

	}

	public static boolean isSyncOffline() {
		return getInstance().isSyncOffline;
	}

	/**
	 * 下载
	 */
	@Override
	public void onDownloadMessageComplete(ECError e, ECMessage message) {
		if (message == null)
			return;
		// 处理发送文件IM消息的时候进度回调
		LogUtil.d(TAG, "[IMChattingHelper - onDownloadMessageComplete] msgId："
				+ message.getMsgId());
		postDowloadMessageResult(message);
	}

	public void onComplete(ECError error) {

	}

	@Override
	public void onProgress(String msgId, int totalByte, int progressByte) {
		// 处理发送文件IM消息的时候进度回调
		LogUtil.d(TAG, "[IMChattingHelper - onProgress] msgId: " + msgId
				+ " , totalByte: " + totalByte + " , progressByte:"
				+ progressByte);
	}

	private synchronized void postDowloadMessageResult(ECMessage message) {
		if (message == null) {
			return;
		}
		if (message.getType() == ECMessage.Type.VOICE) {
			ECVoiceMessageBody voiceBody = (ECVoiceMessageBody) message
					.getBody();
			voiceBody.setDuration(DemoUtils.calculateVoiceTime(voiceBody
					.getLocalUrl()));
		} else if (message.getType() == ECMessage.Type.IMAGE) {
			ImgInfo thumbImgInfo = ImgInfoSqlManager.getInstance()
					.getThumbImgInfo(message);
			if (thumbImgInfo == null) {
				return;
			}
			ImgInfoSqlManager.getInstance().insertImageInfo(thumbImgInfo);
			BitmapFactory.Options options = DemoUtils
					.getBitmapOptions(new File(FileAccessor.getImagePathName(),
							thumbImgInfo.getThumbImgPath()).getAbsolutePath());
			message.setUserData("outWidth://" + options.outWidth
					+ ",outHeight://" + options.outHeight + ",THUMBNAIL://"
					+ message.getMsgId());
		}
		if (IMessageSqlManager.updateIMessageDownload(message) <= 0) {
			return;
		}
		if (mOnMessageReportCallback != null) {
			mOnMessageReportCallback.onMessageReport(null, message);
		}
		boolean showNotice = true;
		SyncMsgEntry remove = syncMessage.remove(message.getMsgId());
		if (remove != null) {
			showNotice = remove.showNotice;
			if (mOnMessageReportCallback != null && remove.msg != null) {
				ArrayList<ECMessage> msgs = new ArrayList<ECMessage>();
				msgs.add(remove.msg);
				// 出现图片双重显示效果。故而进行调整
				// mOnMessageReportCallback.onPushMessage(
				// remove.msg.getSessionId(), msgs);
			}
		}
		if (showNotice)
			showNotification(message);
	}

	public class SyncMsgEntry {
		// 是否是第一次初始化同步消息
		boolean showNotice = false;
		ECMessage msg;

		public SyncMsgEntry(boolean showNotice, ECMessage message) {
			this.showNotice = showNotice;
			this.msg = message;
		}
	}

	@Override
	public void onSoftVersion(String arg0, int arg1) {

	}

	public void destory() {
		if (syncMessage != null) {
			syncMessage.clear();
		}
		mListener = null;
		mChatManager = null;
		isFirstSync = false;
		isAutoGetOfflineMsg = true;
		sInstance = null;
	}

	@Override
	public void onReceiveMessageNotify(ECMessageNotify arg0) {
		// TODO Auto-generated method stub
		
	}
}