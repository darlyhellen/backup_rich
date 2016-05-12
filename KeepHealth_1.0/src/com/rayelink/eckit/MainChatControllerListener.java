package com.rayelink.eckit;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.app.MyApp;
import com.ytdinfo.keephealth.jpush.LittleHelperActivity;
import com.ytdinfo.keephealth.model.DocInfoBean;
import com.ytdinfo.keephealth.model.DocOnline;
import com.ytdinfo.keephealth.model.UserGroupBean;
import com.ytdinfo.keephealth.ui.MainActivity;
import com.ytdinfo.keephealth.ui.report.ChatCommentActivity;
import com.ytdinfo.keephealth.utils.Chat_Dialog;
import com.ytdinfo.keephealth.utils.DBUtilsHelper;
import com.ytdinfo.keephealth.utils.NotificationUtils;
import com.ytdinfo.keephealth.utils.UserDataUtils;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.ECMessage.MessageStatus;
import com.yuntongxun.ecsdk.im.ECGroup;
import com.yuntongxun.ecsdk.im.ECGroup.Permission;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuntongxun.kitsdk.ChatControllerListener;
import com.yuntongxun.kitsdk.ECDeviceKit;
import com.yuntongxun.kitsdk.adapter.ConversationAdapter;
import com.yuntongxun.kitsdk.beans.ChatInfoBean;
import com.yuntongxun.kitsdk.db.ContactSqlManager;
import com.yuntongxun.kitsdk.db.ConversationSqlManager;
import com.yuntongxun.kitsdk.db.GroupSqlManager;
import com.yuntongxun.kitsdk.ui.ECChattingActivity;
import com.yuntongxun.kitsdk.ui.ECChattingActivity.RetryComplete;
import com.yuntongxun.kitsdk.ui.chatting.model.IMChattingHelper;
import com.yuntongxun.kitsdk.ui.group.model.ECContacts;
import com.yuntongxun.kitsdk.utils.ToastUtil;

public class MainChatControllerListener implements ChatControllerListener {

	public static MainChatControllerListener sInstance;

	public static MainChatControllerListener getInstance() {
		if (sInstance == null) {
			sInstance = new MainChatControllerListener();
		}
		return sInstance;
	}

	// 返回首页
	@Override
	public void backToMain(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("news", "news");
		context.startActivity(intent);
	}

	// 关闭计时
	@Override
	public void timeStop(String contactid) {
		if (contactid != null && !contactid.toUpperCase().contains("G")) {
			Intent intent = new Intent(MyApp.getInstance(),
					com.yuntongxun.kitsdk.TimerService.class);
			Bundle bundle = new Bundle();
			bundle.putInt("op", 3);
			bundle.putString("ContactId", contactid);
			intent.putExtras(bundle);
			MyApp.getInstance().startService(intent);
		}
	}

	// 暂停计时
	@Override
	public void timePause(String contactid) {
		if (contactid != null && !contactid.toUpperCase().contains("G")) {
			Intent intent = new Intent(MyApp.getInstance(),
					com.yuntongxun.kitsdk.TimerService.class);
			Bundle bundle = new Bundle();
			bundle.putInt("op", 2);
			bundle.putString("ContactId", contactid);
			intent.putExtras(bundle);
			MyApp.getInstance().startService(intent);
		}

	}

	// 开始计时
	@Override
	public void timeStart(String contactid) {
		if (contactid != null && !contactid.toUpperCase().contains("G")) {
			try {
				ChatInfoBean chatInfoBean = DBUtilsHelper
						.getInstance()
						.getDb()
						.findFirst(
								Selector.from(ChatInfoBean.class).where(
										"docInfoBeanId", "=", contactid));
				if (chatInfoBean != null && !chatInfoBean.isTimeout()) {
					Intent intent = new Intent(MyApp.getInstance(),
							com.yuntongxun.kitsdk.TimerService.class);
					Bundle bundle = new Bundle();
					bundle.putInt("op", 1);
					bundle.putString("ContactId", contactid);
					intent.putExtras(bundle);
					MyApp.getInstance().startService(intent);
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// 再次聊天发起
	@Override
	public void retryChat(final Context context, String subjectID,
			String contactid, final RetryComplete retryCompleteInstance) {
		MobclickAgent.onEvent(context, Constants.UMENG_EVENT_14);
		if (contactid != null && !contactid.toUpperCase().contains("G")) {
			// TODO Auto-generated method stub
			RequestParams requestParams = new RequestParams();
			requestParams.addQueryStringParameter("subjectID", subjectID);
			requestParams.addQueryStringParameter("voipAccount", contactid);
			HttpClient.delete(MyApp.getInstance(), Constants.CHATAGAIN_URL,
					requestParams, new RequestCallBack<String>() {
						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							super.onStart();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							// TODO Auto-generated method stub
							Log.i("再次咨询", arg0.result);

							try {
								JSONObject jsonObject = new JSONObject(
										arg0.result);
								JSONObject data = jsonObject
										.getJSONObject("Data");
								String subjectID = data.getString("SubjectID");
								if (!subjectID.equals("-1")) {
									String docInfoBeanStr = data
											.getString("responser");
									if (null == docInfoBeanStr
											|| docInfoBeanStr.equals("")
											|| docInfoBeanStr.equals("null")) {
										if (!Chat_Dialog.timeCurl()) {
											final AlertDialog dialog = new AlertDialog.Builder(
													context).create();
											dialog.show();
											dialog.setCanceledOnTouchOutside(false);
											Window window = dialog.getWindow();
											window.setContentView(R.layout.chat_dialog);// 设置对话框的布局
											TextView msg = (TextView) window
													.findViewById(R.id.chat_dialog_msg);
											String desString = "亲，非常抱歉，我们的服务时间是工作日9：00－18：00，欢迎下次来咨询，祝您身体健康！";
											msg.setText(desString);
											Button sure = (Button) window
													.findViewById(R.id.chat_dialog_sure);
											sure.setOnClickListener(new OnClickListener() {
												@Override
												public void onClick(View v) {
													// TODO Auto-generated
													// method stub
													dialog.dismiss();
												}
											});
											return;
										} else {
											final AlertDialog dialog = new AlertDialog.Builder(
													context).create();
											dialog.show();
											dialog.setCanceledOnTouchOutside(false);
											Window window = dialog.getWindow();
											window.setContentView(R.layout.chat_dialog);// 设置对话框的布局
											TextView msg = (TextView) window
													.findViewById(R.id.chat_dialog_msg);
											String desString = "亲，该医生在忙碌，点击确定后返回首页，点击报告解读，为您安排其它医生~";
											msg.setText(desString);
											Button sure = (Button) window
													.findViewById(R.id.chat_dialog_sure);
											sure.setOnClickListener(new OnClickListener() {

												@Override
												public void onClick(View v) {
													// TODO Auto-generated
													// method
													Intent i = new Intent(
															context,
															MainActivity.class);
													i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
													context.startActivity(i);
													dialog.dismiss();
												}
											});
											return;
										}

									}
									DocInfoBean docInfoBean = new Gson()
											.fromJson(docInfoBeanStr,
													DocInfoBean.class);
									try {
										// 查看是否有这个医生的会话信息
										ChatInfoBean chatInfoBean = DBUtilsHelper
												.getInstance()
												.getDb()
												.findFirst(
														Selector.from(
																ChatInfoBean.class)
																.where("docInfoBeanId",
																		"=",
																		docInfoBean
																				.getVoipAccount()));
										if (null == chatInfoBean) {
											chatInfoBean = new ChatInfoBean();
										}
										chatInfoBean
												.setDocInfoBeanId(docInfoBean
														.getVoipAccount());
										chatInfoBean.setComment(false);
										chatInfoBean.setTimeout(false);
										chatInfoBean.setSubjectID(subjectID);
										chatInfoBean.setStatus(false);
										DBUtilsHelper.getInstance()
												.saveChatinfo(chatInfoBean);
										// ((Activity) context).finish();

										((ECChattingActivity) context)
												.restart();
										// ECDeviceKit
										// .getIMKitManager()
										// .startConversationActivity(context,
										// chatInfoBean
										// .getDocInfoBeanId());
										retryCompleteInstance.onComplete();
									} catch (DbException e) {
										// TODO Auto-generated
										// catch
										// block
										e.printStackTrace();
									}
								} else {
									final AlertDialog dialog = new AlertDialog.Builder(
											context).create();
									dialog.show();
									dialog.setCanceledOnTouchOutside(false);
									Window window = dialog.getWindow();
									window.setContentView(R.layout.chat_dialog);// 设置对话框的布局
									TextView msg = (TextView) window
											.findViewById(R.id.chat_dialog_msg);
									msg.setText(/* data.getString("Message") */"亲，该医生不在线哦，点击确定后返回首页，点击报告解读，为您安排其它医生~");
									Button sure = (Button) window
											.findViewById(R.id.chat_dialog_sure);
									sure.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											Intent i = new Intent(context,
													MainActivity.class);
											i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
											context.startActivity(i);
											dialog.dismiss();
										}
									});
								}
							} catch (Exception e) {

							}
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub
							ToastUtil.showMessage("网络获取失败");
						}
					});
		}

	}

	// 结束咨询
	@Override
	public void closeWindow(final Context mContext, final String contactid)
			throws DbException {
		// 结束咨询
		// TODO Auto-generated method stub
		if (contactid != null && !contactid.toUpperCase().contains("G")) {
			RequestParams params = new RequestParams();
			final ChatInfoBean mchatInfoBean = DBUtilsHelper
					.getInstance()
					.getDb()
					.findFirst(
							Selector.from(ChatInfoBean.class).where(
									"docInfoBeanId", "=", contactid));
			if (mchatInfoBean != null) {
				final String subjectId = mchatInfoBean.getSubjectID();
				params.addQueryStringParameter("subjectId", subjectId);
				HttpClient.get(MyApp.getInstance(), Constants.CLOSESUBJECT_URL,
						params, new RequestCallBack<String>() {
							@Override
							public void onStart() {
								// TODO Auto-generated method
								// stub
								super.onStart();
							}

							@Override
							public void onSuccess(ResponseInfo<String> arg0) {
								// TODO Auto-generated method
								// stub
								try {
									timeStop(contactid);
									JSONObject jsonObject = new JSONObject(
											arg0.result);
									String data = jsonObject.getString("Data");
									if (data.equals("true")) {
										// 组建一个待发送的ECMessage
										ECMessage msg = ECMessage
												.createECMessage(ECMessage.Type.TXT);
										// 设置消息的属性：发出者，接受者，发送时间等
										msg.setForm(ECDeviceKit.getInstance()
												.getUserId());
										msg.setMsgTime(System
												.currentTimeMillis());
										// 设置消息接收者
										msg.setTo(contactid);
										msg.setSessionId(contactid);
										// 设置消息发送类型（发送或者接收）
										msg.setDirection(ECMessage.Direction.SEND);
										// 创建一个文本消息体，并添加到消息对象中
										ECTextMessageBody msgBody = new ECTextMessageBody(
												Constants.CLOSESUBJECT);
										msg.setBody(msgBody);
										IMChattingHelper.sendECMessage(msg);
										Intent i = new Intent();
										i.setClass(mContext,
												ChatCommentActivity.class);
										i.putExtra("subjectId", subjectId);
										i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										// i.putExtra("voipAccount",
										// chatInfoBean.getDocInfoBeanId());
										mContext.startActivity(i);
										mchatInfoBean.setStatus(false);
										mchatInfoBean.setTimeout(true);
										DBUtilsHelper.getInstance()
												.saveChatinfo(mchatInfoBean);
									}
								} catch (JSONException e) {
									// TODO Auto-generated catch
									// block
									e.printStackTrace();
								}
							}

							@Override
							public void onFailure(HttpException arg0,
									String arg1) {
							}
						});
			}
		}
	}

	// 开始聊天
	@Override
	public void chatStart(String contactid) {
		if (contactid != null && !contactid.toUpperCase().contains("G")) {
			// timeStart(contactid);
			try {
				ChatInfoBean chatInfoBean = DBUtilsHelper
						.getInstance()
						.getDb()
						.findFirst(
								Selector.from(ChatInfoBean.class).where(
										"docInfoBeanId", "=", contactid));
				if (chatInfoBean != null) {
					ECChattingActivity.USER_DATA = UserDataUtils
							.getUserData(chatInfoBean.getSubjectID());
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// 初始化医生信息
	@Override
	public void initDocInfo(String contactid, final TextView tvUserName) {
		if (contactid != null && !contactid.toUpperCase().contains("G")) {
			// TODO Auto-generated method stub
			RequestParams requestParams = new RequestParams();
			requestParams.addQueryStringParameter("voipAccount", contactid);
			HttpClient.get(MyApp.getInstance(), Constants.GETDOC_URL,
					requestParams, new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							JSONObject jsonObject;
							JSONObject responser = null;
							try {
								jsonObject = new JSONObject(arg0.result);
								responser = jsonObject.getJSONObject("Data");
							} catch (JSONException e) {
								e.printStackTrace();
							}
							if (responser != null && !responser.equals("null")) {
								DocInfoBean docInfoBean = new Gson().fromJson(
										responser.toString(), DocInfoBean.class);
								tvUserName.setText(docInfoBean.getUserName());
								ECContacts contacts = new ECContacts();
								contacts.setContactid(docInfoBean
										.getVoipAccount());
								contacts.setRemark(docInfoBean.getHeadPicture());
								contacts.setNickname(docInfoBean.getUserName());
								ContactSqlManager.insertContact(contacts, 1,
										true);
							}
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub
						}
					});
		}
	}

	// 打开评论框
	@Override
	public void openComment(Context mContext, String contactId) {
		// TODO Auto-generated method stub
		if (contactId != null && !contactId.toUpperCase().contains("G")) {
			ChatInfoBean chatInfoBean;
			try {
				chatInfoBean = DBUtilsHelper
						.getInstance()
						.getDb()
						.findFirst(
								Selector.from(ChatInfoBean.class).where(
										"docInfoBeanId", "=", contactId));
				if (chatInfoBean != null) {
					Intent i = new Intent();
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					i.putExtra("subjectId", chatInfoBean.getSubjectID());
					i.setClass(mContext, ChatCommentActivity.class);
					mContext.startActivity(i);
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// 转诊
	@Override
	public void transferTreat(final String from, final String contactId,
			String subjectId) {
		// 转诊到其他聊天页面，当前聊天窗口需要关闭
		try {
			// 会话信息处理
			ChatInfoBean toChatInfoBean = DBUtilsHelper
					.getInstance()
					.getDb()
					.findFirst(
							Selector.from(ChatInfoBean.class).where(
									"docInfoBeanId", "=", contactId));
			ChatInfoBean fromChatInfoBean = DBUtilsHelper
					.getInstance()
					.getDb()
					.findFirst(
							Selector.from(ChatInfoBean.class).where(
									"docInfoBeanId", "=", from));

			if (toChatInfoBean == null) {
				toChatInfoBean = new ChatInfoBean();
			}
			toChatInfoBean.setDocInfoBeanId(contactId);
			toChatInfoBean.setSubjectID(fromChatInfoBean.getSubjectID());
			toChatInfoBean.setSubjectType(fromChatInfoBean.getSubjectType());
			toChatInfoBean.setStatus(false);
			toChatInfoBean.setComment(false);
			toChatInfoBean.setTimeout(false);
			DBUtilsHelper.getInstance().saveChatinfo(toChatInfoBean);
			if (fromChatInfoBean != null) {
				fromChatInfoBean.setTimeout(true);
				fromChatInfoBean.setComment(true);
				toChatInfoBean.setStatus(false);
				fromChatInfoBean.setSubjectID("0");
				DBUtilsHelper.getInstance().saveChatinfo(fromChatInfoBean);
			}

			// 消息发送处理
			ECContacts docContacts = ContactSqlManager.getContact(contactId);
			if (docContacts != null
					&& !docContacts.getContactid().equals(
							docContacts.getNickname())) {
				// 组建一个待发送的ECMessage
				ECMessage msg = ECMessage.createECMessage(ECMessage.Type.TXT);
				// 设置消息的属性：发出者，接受者，发送时间等
				msg.setForm(from);
				msg.setMsgTime(System.currentTimeMillis());
				// 设置消息接收者
				msg.setTo(contactId);
				msg.setSessionId(contactId);
				// 设置消息发送类型（发送或者接收）
				msg.setDirection(ECMessage.Direction.SEND);
				// 创建一个文本消息体，并添加到消息对象中
				ECTextMessageBody msgBody = new ECTextMessageBody("您已成功转诊至 "
						+ docContacts.getNickname() + " 医生");
				msg.setBody(msgBody);
				ECChattingActivity.USER_DATA = UserDataUtils
						.getUserDataForTrans(
								toChatInfoBean.getSubjectID() + "", from);
				// IMChattingHelper.sendECMessage(msg);
				IMChattingHelper.sendECMessage(msg);
				Intent intnet = new Intent("com.rayelink.transfertreat");
				intnet.putExtra("From", from);
				intnet.putExtra("ContactId", contactId);
				MyApp.getInstance().sendBroadcast(intnet);
			} else {
				if (contactId != null && !contactId.toUpperCase().contains("G")) {
					// TODO Auto-generated method stub
					RequestParams requestParams = new RequestParams();
					requestParams.addQueryStringParameter("voipAccount",
							contactId);
					HttpClient.get(MyApp.getInstance(), Constants.GETDOC_URL,
							requestParams, new RequestCallBack<String>() {
								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
									JSONObject jsonObject;
									JSONObject responser = null;
									try {
										jsonObject = new JSONObject(arg0.result);
										responser = jsonObject
												.getJSONObject("Data");
									} catch (JSONException e) {
										e.printStackTrace();
									}
									DocInfoBean docInfoBean = new Gson()
											.fromJson(responser.toString(),
													DocInfoBean.class);
									ECMessage msg = ECMessage
											.createECMessage(ECMessage.Type.TXT);
									// 设置消息的属性：发出者，接受者，发送时间等
									msg.setForm(from);
									msg.setMsgTime(System.currentTimeMillis());
									// 设置消息接收者
									msg.setTo(contactId);
									msg.setSessionId(contactId);
									// 设置消息发送类型（发送或者接收）
									msg.setDirection(ECMessage.Direction.SEND);
									// 创建一个文本消息体，并添加到消息对象中
									ECTextMessageBody msgBody = new ECTextMessageBody(
											"您已成功转诊至 "
													+ docInfoBean.getUserName()
													+ " 医生");
									msg.setBody(msgBody);
									ChatInfoBean tempChatInfoBean = null;
									try {
										tempChatInfoBean = DBUtilsHelper
												.getInstance()
												.getDb()
												.findFirst(
														Selector.from(
																ChatInfoBean.class)
																.where("docInfoBeanId",
																		"=",
																		contactId));
									} catch (DbException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									if (tempChatInfoBean != null) {
										ECChattingActivity.USER_DATA = UserDataUtils
												.getUserDataForTrans(
														tempChatInfoBean
																.getSubjectID()
																+ "", from);
									}
									IMChattingHelper.sendECMessage(msg);
									ECContacts contacts = new ECContacts();
									contacts.setContactid(docInfoBean
											.getVoipAccount());
									contacts.setRemark(docInfoBean
											.getHeadPicture());
									contacts.setNickname(docInfoBean
											.getUserName());
									ContactSqlManager.insertContact(contacts,
											1, true);
									Intent intnet = new Intent(
											"com.rayelink.transfertreat");
									intnet.putExtra("From", from);
									intnet.putExtra("ContactId", contactId);
									MyApp.getInstance().sendBroadcast(intnet);
								}

								@Override
								public void onFailure(HttpException arg0,
										String arg1) {

									ECMessage msg = ECMessage
											.createECMessage(ECMessage.Type.TXT);
									// 设置消息的属性：发出者，接受者，发送时间等
									msg.setForm(from);
									msg.setMsgTime(System.currentTimeMillis());
									// 设置消息接收者
									msg.setTo(contactId);
									msg.setSessionId(contactId);
									// 设置消息发送类型（发送或者接收）
									msg.setDirection(ECMessage.Direction.SEND);
									// 创建一个文本消息体，并添加到消息对象中
									ECTextMessageBody msgBody = new ECTextMessageBody(
											"您已成功转诊至 " + contactId + " 医生");
									msg.setBody(msgBody);
									ChatInfoBean tempChatInfoBean = null;
									try {
										tempChatInfoBean = DBUtilsHelper
												.getInstance()
												.getDb()
												.findFirst(
														Selector.from(
																ChatInfoBean.class)
																.where("docInfoBeanId",
																		"=",
																		contactId));
									} catch (DbException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									if (tempChatInfoBean != null) {
										ECChattingActivity.USER_DATA = UserDataUtils
												.getUserDataForTrans(
														tempChatInfoBean
																.getSubjectID()
																+ "", from);
									}
									ECChattingActivity.USER_DATA = UserDataUtils
											.getUserDataForTrans(
													tempChatInfoBean
															.getSubjectID()
															+ "", from);
									IMChattingHelper.sendECMessage(msg);
									// TODO Auto-generated method stub
									Intent intnet = new Intent(
											"com.rayelink.transfertreat");
									intnet.putExtra("From", from);
									intnet.putExtra("ContactId", contactId);
									MyApp.getInstance().sendBroadcast(intnet);
								}
							});
				}

			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 一般消息处理
	@Override
	public void handleMessage(String subjectId, String from) {
		try {
			final DbUtils dbUtils = DBUtilsHelper.getInstance().getDb();
			ChatInfoBean chatinfoBean = dbUtils.findFirst(Selector.from(
					ChatInfoBean.class).where("docInfoBeanId", "=", from));
			if (chatinfoBean != null)// 有此医生
			{
				if (!chatinfoBean.isTimeout()) {
					chatinfoBean.setStatus(true);
					dbUtils.saveOrUpdate(chatinfoBean);
				}
			} else {// 无此医生
				createChatInfoBeanFalse(from, subjectId, null);
			}

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// getSubjectInfo(subjectId, from);

	}

	// 跳转至帮忙医小助手界面
	@Override
	public void goToBMYHelpActivity(Context context) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(context, LittleHelperActivity.class);
		context.startActivity(intent);
	}

	// 同步群组列表数据
	@Override
	public void syncUserGroups(ConversationAdapter adapter) {
		// TODO Auto-generated method stub
		requestGetUserGroup(adapter);
	}

	// 发送消息提醒
	@Override
	public void notifycationSend(Context mContext, String messsage) {
		// TODO Auto-generated method stub
		NotificationUtils.send(MyApp.getInstance());
	}

	// 获取
	private void requestGetUserGroup(final ConversationAdapter adapter) {
		// TODO Auto-generated method stub
		HttpClient.get(MyApp.getInstance(), Constants.GETUSERGROUPS_URL,
				new RequestParams(), new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							JSONObject data = jsonObject.getJSONObject("Data");
							String statusCode = data.getString("statusCode");
							if (statusCode.equals("000000")) {
								if (!data.getString("groups").equals("null")) {
									List<UserGroupBean> list = new Gson().fromJson(
											data.getString("groups"),
											new TypeToken<List<UserGroupBean>>() {
											}.getType());
									for (int i = 0; i < list.size(); i++) {
										UserGroupBean userGroupBean = list
												.get(i);
										if (!GroupSqlManager
												.isExitGroup(userGroupBean
														.getGroupId())) {
											ECGroup group = new ECGroup();
											group.setGroupId(userGroupBean
													.getGroupId());
											group.setName(userGroupBean
													.getName());
											group.setDateCreated(userGroupBean
													.getDateCreated());
											group.setGroupType(Integer
													.parseInt(userGroupBean
															.getType()));
											group.setCount(Integer
													.parseInt(userGroupBean
															.getCount()));
											group.setPermission(Permission.PRIVATE);

											GroupSqlManager.insertGroup(group,
													true, false);
											ConversationSqlManager
													.getInstance();
											if (ConversationSqlManager
													.querySessionIdForBySessionId(userGroupBean
															.getGroupId()) == 0) {
												ECMessage msg = ECMessage
														.createECMessage(ECMessage.Type.TXT);
												// 设置消息的属性：发出者，接受者，发送时间等
												msg.setForm(ECDeviceKit
														.getInstance()
														.getUserId());
												msg.setMsgTime(System
														.currentTimeMillis());
												// 设置消息接收者
												msg.setTo(userGroupBean
														.getGroupId());
												msg.setSessionId(userGroupBean
														.getGroupId());
												// 设置消息发送类型（发送或者接收）
												msg.setDirection(ECMessage.Direction.RECEIVE);
												// 创建一个文本消息体，并添加到消息对象中
												ECTextMessageBody msgBody = new ECTextMessageBody(
														"");
												// 调用接口发送IM消息
												msg.setMsgTime(System
														.currentTimeMillis());
												msg.setBody(msgBody);
												msg.setMsgStatus(MessageStatus.SUCCESS);
												ConversationSqlManager
														.insertSessionRecord(msg);
											}
										}
									}
									adapter.notifyChange();
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub

					}
				});
	}

	// 获取会话信息
	private void getSubjectInfo(String subjectId, final String from) {
		// TODO Auto-generated method stub
		try {
			final DbUtils dbUtils = DBUtilsHelper.getInstance().getDb();
			RequestParams requestParams = new RequestParams();
			requestParams.addQueryStringParameter("id", subjectId);
			HttpClient.get(MyApp.getInstance(), Constants.SUBJECTINFO,
					requestParams, new RequestCallBack<String>() {
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
											.getJSONObject("Sub  ject");
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
										chatinfoBean.setStatus("1"
												.equals(subject
														.getString("Status")));
										chatinfoBean.setComment(false);
										chatinfoBean.setTimeout(false);
										JSONObject userJsonObject = data
												.getJSONObject("User");
										if (userJsonObject != null) {
											chatinfoBean.setDocInfoBeanId(from);
										}
										Log.e("getSubjectInfo-subject", "4");
										dbUtils.saveOrUpdate(chatinfoBean);

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
					});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private ChatInfoBean getChatInfo(String voipId) {
		try {
			return DBUtilsHelper
					.getInstance()
					.getDb()
					.findFirst(
							Selector.from(ChatInfoBean.class).where(
									"docInfoBeanId", "=", voipId));
		} catch (DbException e) {
			// TODO Auto-generated catch block
			return null;
		}
	}

	// 创建关闭的会话
	private void createChatInfoBeanFalse(String docId, String subjectId,
			String subjectType) {
		try {
			ChatInfoBean chatInfoBean = new ChatInfoBean();
			chatInfoBean.setComment(true);
			chatInfoBean.setDocInfoBeanId(docId);
			chatInfoBean.setStatus(false);
			chatInfoBean.setSubjectID(subjectId);
			chatInfoBean.setSubjectType(subjectType);
			chatInfoBean.setTimeout(true);
			DBUtilsHelper.getInstance().getDb().saveOrUpdate(chatInfoBean);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 是否是当前账号医生在线
	private boolean isChatInfoOnLine(String docId) {
		try {
			return (DBUtilsHelper
					.getInstance()
					.getDb()
					.findFirst(
							Selector.from(ChatInfoBean.class)
									.where("docInfoBeanId", "=", docId)
									.and("isTimeout", "=", false)) != null);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			return false;
		}

	}

	// 创建新开启的会话
	private void createChatInfoBeanTrue(String docId, String subjectId,
			String subjectType) {
		try {
			ChatInfoBean chatInfoBean = new ChatInfoBean();
			chatInfoBean.setComment(false);
			chatInfoBean.setDocInfoBeanId(docId);
			chatInfoBean.setStatus(true);
			chatInfoBean.setSubjectID(subjectId);
			if (subjectType != null)
				chatInfoBean.setSubjectType(subjectType);
			chatInfoBean.setTimeout(false);
			DBUtilsHelper.getInstance().getDb().saveOrUpdate(chatInfoBean);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeService() {
		Intent intent = new Intent(MyApp.getInstance(),
				com.yuntongxun.kitsdk.TimerService.class);
		Bundle bundle = new Bundle();
		bundle.putInt("op", 4);
		bundle.putString("ContactId", "0");
		intent.putExtras(bundle);
		MyApp.getInstance().startService(intent);

	}

	public static void closeAllSubject(boolean isQT) {
		RequestParams params = new RequestParams();
		try {
			final ChatInfoBean mchatInfoBean = DBUtilsHelper
					.getInstance()
					.getDb()
					.findFirst(
							Selector.from(ChatInfoBean.class).where(
									"isTimeout", "=", false));
			if (isQT) {
				if (mchatInfoBean != null) {
					// mchatInfoBean.setStatus(false);
					// mchatInfoBean.setTimeout(true);
					// DBUtilsHelper.getInstance()
					// .saveChatinfo(mchatInfoBean);
					// MainChatControllerListener.getInstance().timeStop(mchatInfoBean.getDocInfoBeanId());
					mchatInfoBean.setStatus(false);
					mchatInfoBean.setTimeout(true);
					DBUtilsHelper.getInstance().saveChatinfo(mchatInfoBean);
				}
				if(SDKCoreHelper.getInstance().mConnect== ECDevice.ECConnectState.CONNECT_SUCCESS){
					ECDeviceKit.unInitial();
				}
				DBUtilsHelper.instance = null;
				MainChatControllerListener.getInstance().closeService();
			} else {
				if (mchatInfoBean != null) {
					final String subjectId = mchatInfoBean.getSubjectID();
					params.addQueryStringParameter("subjectId", subjectId);
					HttpClient.get(MyApp.getInstance(),
							Constants.CLOSESUBJECT_URL, params,
							new RequestCallBack<String>() {
								@Override
								public void onStart() {
									// TODO Auto-generated method
									// stub
									super.onStart();
								}

								@Override
								public void onSuccess(ResponseInfo<String> arg0) {
									// TODO Auto-generated method
									// stub
									try {
										MainChatControllerListener
												.getInstance()
												.timeStop(
														mchatInfoBean
																.getDocInfoBeanId());
										JSONObject jsonObject = new JSONObject(
												arg0.result);
										String data = jsonObject
												.getString("Data");
										if (data.equals("true")) {
											// 组建一个待发送的ECMessage
											ECMessage msg = ECMessage
													.createECMessage(ECMessage.Type.TXT);
											// 设置消息的属性：发出者，接受者，发送时间等
											msg.setForm(ECDeviceKit
													.getInstance().getUserId());
											msg.setMsgTime(System
													.currentTimeMillis());
											// 设置消息接收者
											msg.setTo(mchatInfoBean
													.getDocInfoBeanId());
											msg.setSessionId(mchatInfoBean
													.getDocInfoBeanId());
											// 设置消息发送类型（发送或者接收）
											msg.setDirection(ECMessage.Direction.SEND);
											// 创建一个文本消息体，并添加到消息对象中
											ECTextMessageBody msgBody = new ECTextMessageBody(
													Constants.CLOSESUBJECT);
											msg.setBody(msgBody);
											IMChattingHelper.sendECMessage(msg);
											mchatInfoBean.setStatus(false);
											mchatInfoBean.setTimeout(true);
											DBUtilsHelper
													.getInstance()
													.saveChatinfo(mchatInfoBean);
											MainChatControllerListener
													.getInstance()
													.timeStop(
															mchatInfoBean
																	.getDocInfoBeanId());
											if(SDKCoreHelper.getInstance().mConnect== ECDevice.ECConnectState.CONNECT_SUCCESS){
												ECDeviceKit.unInitial();
											}
											DBUtilsHelper.instance = null;
											MainChatControllerListener
													.getInstance()
													.closeService();
										}
									} catch (JSONException e) {
										// TODO Auto-generated catch
										// block
										e.printStackTrace();
									}
								}

								@Override
								public void onFailure(HttpException arg0,
										String arg1) {
									MainChatControllerListener
											.getInstance()
											.timeStop(
													mchatInfoBean
															.getDocInfoBeanId());
									mchatInfoBean.setStatus(false);
									mchatInfoBean.setTimeout(true);
									DBUtilsHelper.getInstance().saveChatinfo(
											mchatInfoBean);
									if(SDKCoreHelper.getInstance().mConnect== ECDevice.ECConnectState.CONNECT_SUCCESS){
										ECDeviceKit.unInitial();
									}
									DBUtilsHelper.instance = null;
									MainChatControllerListener.getInstance()
											.closeService();
								}
							});
				} else {
					if(SDKCoreHelper.getInstance().mConnect== ECDevice.ECConnectState.CONNECT_SUCCESS){
						ECDeviceKit.unInitial();
					}
					DBUtilsHelper.instance = null;
					MainChatControllerListener.getInstance().closeService();
				}
			}
		} catch (DbException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	@Override
	public DbUtils getChatInfoDb() {
		return DBUtilsHelper.getInstance().getDb();
	}

	@Override
	public void closeSubject(final String subjectId, final String docVip,
			final Handler mHandler) {
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("subjectId", subjectId);
		HttpClient.get(MyApp.getInstance(), Constants.CLOSESUBJECT_URL, params,
				new RequestCallBack<String>() {
					@Override
					public void onStart() {
						// TODO Auto-generated method
						// stub
						super.onStart();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method
						// stub
						try {
							JSONObject jsonObject = new JSONObject(arg0.result);
							String data = jsonObject.getString("Data");
							if (data != null && data.equals("true")) {
								// 组建一个待发送的ECMessage
								ECMessage msg = ECMessage
										.createECMessage(ECMessage.Type.TXT);
								// 设置消息的属性：发出者，接受者，发送时间等
								msg.setForm(ECDeviceKit.getInstance()
										.getUserId());
								msg.setMsgTime(System.currentTimeMillis());
								msg.setUserData(UserDataUtils
										.getUserData(subjectId));
								// 设置消息接收者
								msg.setTo(docVip);
								msg.setSessionId(docVip);
								// 设置消息发送类型（发送或者接收）
								msg.setDirection(ECMessage.Direction.SEND);
								// 创建一个文本消息体，并添加到消息对象中
								ECTextMessageBody msgBody = new ECTextMessageBody(
										Constants.CLOSESUBJECT);
								msg.setBody(msgBody);
								Message message = Message.obtain();
								message.what = 0x01;
								message.obj = msg;
								mHandler.sendMessage(message);
								// try{
								// IMChattingHelper.sendECMessage(msg);
								// }catch(Exception e)
								// {
								// Log.e("sendECMessage", e.toString());
								// }
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch
							// block
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						ToastUtil.showMessage("网络获取失败");
					}
				});
	}

	@Override
	public void isChattingDocOnline(final ChatInfoBean info,
			final String docid, final Button retry) {
		// 在这里请求医生是否在线的问题
		// 根据mRecipients获取医生在线状态，获取完成后进行再次咨询修改状态
		Cursor cursor = ConversationSqlManager.getConversationCursor();
		List<String> param = new ArrayList<String>();
		if (cursor != null) {
			while (cursor.moveToNext()) {
				int num = cursor.getColumnIndex("sessionId");
				String s = cursor.getString(num);
				param.add(s);
			}
		}
		if (param.size() == 0) {
			// 这是用户第一次使用。没有医生数据库。
			param.add(docid);
		} else {
			// 不是第一次登陆。但无法保证数据库中含有本条医生记录。所以进行判断
			boolean has = false;
			for (String st : param) {
				if (st.equals(docid)) {
					has = true;
					break;
				}
			}
			if (!has) {
				param.add(docid);
			}

		}

		RequestParams params = new RequestParams();
		params.addQueryStringParameter(new BasicNameValuePair("voipAccounts",
				param.toString()));
		params.addQueryStringParameter(new BasicNameValuePair("rType",
				"Android"));
		HttpClient.get(MyApp.getInstance(), Constants.DOCISONLINE, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						LogUtils.i(arg0.result);
						// 解析JSON
						boolean isonline = false;
						MainActivity.onlines = new ArrayList<DocOnline>();
						try {
							JSONObject json = new JSONObject(arg0.result);
							JSONObject data = json.getJSONObject("Data");
							JSONArray doctorInfos = data
									.getJSONArray("DoctorInfos");
							if (null != doctorInfos) {
								for (int i = 0; i < doctorInfos.length(); i++) {
									JSONObject jsonObject = doctorInfos
											.getJSONObject(i);
									String info = jsonObject
											.getString("VoipAccount");
									if (docid.equals(info)) {
										isonline = jsonObject
												.getBoolean("IsOnline");
									}
									MainActivity.onlines.add(new DocOnline(
											info, jsonObject
													.getBoolean("IsOnline")));
								}
							}
						} catch (Exception e) {
							// TODO: handle exception
							MainActivity.onlines = new ArrayList<DocOnline>();
							isonline = false;
						}

						if (info != null && info.isTimeout()) {
							if (info.isComment()) {
								// 用户已经评论
								if (!isonline) {
									retry.setClickable(false);
									retry.setTextColor(MyApp.getInstance()
											.getResources()
											.getColor(R.color.w_white_unclick));
									retry.setBackgroundResource(R.drawable.login_bt_bg_glay);
								} else {
									retry.setClickable(true);
									retry.setTextColor(MyApp.getInstance()
											.getResources()
											.getColor(R.color.w_white));
									retry.setBackgroundResource(R.drawable.login_bt_bg);
								}
							}
						} else {
							if (!isonline) {
								retry.setClickable(false);
								retry.setTextColor(MyApp.getInstance()
										.getResources()
										.getColor(R.color.w_white_unclick));
								retry.setBackgroundResource(R.drawable.login_bt_bg_glay);
							} else {
								retry.setClickable(true);
								retry.setTextColor(MyApp.getInstance()
										.getResources()
										.getColor(R.color.w_white));
								retry.setBackgroundResource(R.drawable.login_bt_bg);
							}
						}

						if (MainActivity.onlines != null
								&& MainActivity.onlines.size() > 0) {
							for (DocOnline doc : MainActivity.onlines) {
								if (docid.equals(doc.getDoc())) {
									doc.setOnline(isonline);
								}
							}
						} else {
							MainActivity.onlines = new ArrayList<DocOnline>();
							MainActivity.onlines.add(new DocOnline(docid,
									isonline));
						}

					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						MainActivity.onlines = new ArrayList<DocOnline>();
						LogUtils.i(arg1);
						boolean isonline = false;
						if (info != null && info.isTimeout()) {
							if (info.isComment()) {
								// 用户已经评论
								if (!isonline) {
									retry.setClickable(false);
									retry.setTextColor(MyApp.getInstance()
											.getResources()
											.getColor(R.color.w_white_unclick));
									retry.setBackgroundResource(R.drawable.login_bt_bg_glay);
								} else {
									retry.setClickable(true);
									retry.setTextColor(MyApp.getInstance()
											.getResources()
											.getColor(R.color.w_white));
									retry.setBackgroundResource(R.drawable.login_bt_bg);
								}
							}
						} else {
							if (!isonline) {
								retry.setClickable(false);
								retry.setTextColor(MyApp.getInstance()
										.getResources()
										.getColor(R.color.w_white_unclick));
								retry.setBackgroundResource(R.drawable.login_bt_bg_glay);
							} else {
								retry.setClickable(true);
								retry.setTextColor(MyApp.getInstance()
										.getResources()
										.getColor(R.color.w_white));
								retry.setBackgroundResource(R.drawable.login_bt_bg);
							}
						}
						if (MainActivity.onlines != null
								&& MainActivity.onlines.size() > 0) {
							for (DocOnline doc : MainActivity.onlines) {
								if (docid.equals(doc.getDoc())) {
									doc.setOnline(isonline);
								}
							}
						} else {
							MainActivity.onlines = new ArrayList<DocOnline>();
							MainActivity.onlines.add(new DocOnline(docid,
									isonline));
						}
					}
				});
	};

}
