package com.ytdinfo.keephealth.ui;


public class NewsFragment
{
	}
//public class NewsFragment extends Fragment implements View.OnClickListener,
//		OnItemClickListener {
//	private static final String TAG = NewsFragment.class.getName();
//	private TextView mContactNum;
//	private TextView mGroupNum;
//
//	private LinearLayout mGroupTopContentLy;
//	private ListView mGroupListLv;
//	private LinearLayout mGroupListEmpty;
//
//	private IMConvAdapter mIMAdapter;
//	private ChatInfoBean chatInfoBean;
//	private TextView data;
//	private ImageView nodata;
//	private UserModel userModel;
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		userModel = new Gson().fromJson(
//				SharedPrefsUtil.getValue(Constants.USERMODEL, null),
//				UserModel.class);
//		EmoticonUtil.initEmoji();
//		// requestGetUserGroup();
//		// regist .
//		registerReceiver(new String[] { CCPIntentUtils.INTENT_IM_RECIVE,
//				CCPIntentUtils.INTENT_DELETE_GROUP_MESSAGE,
//				CCPIntentUtils.INTENT_REMOVE_FROM_GROUP,
//				CCPIntentUtils.INTENT_RECEIVE_SYSTEM_MESSAGE,
//				CCPIntentUtils.INTENT_JOIN_GROUP_SUCCESS });
//
//	}
//
//	@Override
//	public void setUserVisibleHint(boolean isVisibleToUser) {
//		super.setUserVisibleHint(isVisibleToUser);
//		if (isVisibleToUser) {
//			// 相当于Fragment的onResume
//
//		} else {
//			// 相当于Fragment的onPause
//		}
//	}
//	DbUtils dbUtils ;
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		View rootView = inflater.inflate(R.layout.layout_im_group_activity,
//				container, false);// 关联布局文件
//		initListView(rootView);
//		dbUtils = DBUtilsHelper.getInstance().getDb();
//		return rootView;
//
//	}
//
//	private void initListView(View v) {
//		nodata = (ImageView) v.findViewById(R.id.iv_nodata);
//		data = (TextView) v.findViewById(R.id.tv_data);
//		mGroupListLv = (ListView) v.findViewById(R.id.group_list_content);
//		mGroupListLv.setOnItemClickListener(this);
//		mGroupListEmpty = (LinearLayout) v.findViewById(R.id.group_list_empty);
//		mGroupListLv.setEmptyView(mGroupListEmpty);
//	}
//
//	protected void handleTaskBackGround(ITask iTask) {
//		int key = iTask.getKey();
//		if (key == TaskKey.TASK_KEY_DEL_MESSAGE) {
//			// delete all IM message and del local file also.
//			try {
//				CCPSqliteManager.getInstance().deleteAllIMMessage();
//				CCPUtil.delAllFile(MyApp.getInstance().getVoiceStore()
//						.getAbsolutePath());
//				CCPSqliteManager.getInstance().deleteAllNoticeMessage();
//				getActivity().sendBroadcast(
//						new Intent(CCPIntentUtils.INTENT_IM_RECIVE));
//				// ysj closeConnectionProgress();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	@Override
//	public void onResume() {
//		// TODO Auto-generated method stub
//		super.onResume();
//		
//		MobclickAgent.onPageStart("NewsFragment"); //统计页面
//		
//		initConversation();
//	}
//	
//	@Override
//	public void onPause() {
//	    super.onPause();
//	    
//	    MobclickAgent.onPageEnd("NewsFragment"); 
//	}
//
//	@Override
//	public void onDestroy() {
//		// TODO Auto-generated method stub
//		super.onDestroy();
//		EmoticonUtil.getInstace().release();
//	}
//
//	/*
//	 * @Override protected void handleTitleAction(int direction) {
//	 * 
//	 * if(direction == TITLE_RIGHT_ACTION) {
//	 * showConnectionProgress(getString(R.string.str_dialog_message_default));
//	 * ITask iTask = new ITask(TaskKey.TASK_KEY_DEL_MESSAGE); addTask(iTask); }
//	 * else { super.handleTitleAction(direction); } }
//	 */
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		/*
//		 * ysj case R.drawable.im_contact_icon:
//		 * 
//		 * //startActivity(new Intent(getActivity(), IMChatActivity.class));
//		 * 
//		 * Intent intent = new Intent(getActivity(),
//		 * InviteInterPhoneActivity.class); intent.putExtra("create_to",
//		 * InviteInterPhoneActivity.CREATE_TO_IM_TALK); startActivity(intent);
//		 * 
//		 * break;
//		 */
//
//		case R.drawable.group_icon:
//
//			startActivity(new Intent(getActivity(), GroupListActivity.class));
//
//			break;
//		default:
//			break;
//		}
//	}
//
//	/*
//	 * @Override protected void onDestroy() { super.onDestroy();
//	 * 
//	 * EmoticonUtil.getInstace().release();
//	 * 
//	 * }
//	 */
//	private MyProgressDialog myProgressDialog;
//
//	@Override
//	public void onItemClick(AdapterView<?> parent, View view, int position,
//			long id) {
//		/*Intent intent2 = new Intent(CCPIntentUtils.INTENT_IM_RECIVE);
//		getActivity().sendBroadcast(intent2);*/
//		final IMConversation vSession = mIMAdapter.getItem(position);
//		// System.out.println("vseession........"+vSession.toString());
//		if (vSession != null) {
//			Intent intent = null;
//			if (vSession.getType() == IMConversation.CONVER_TYPE_SYSTEM) {
//				intent = new Intent(getActivity(), SystemMsgActivity.class);
//
//			} else if (vSession.getId().equals("帮忙医小助手")) {
//				Intent i = new Intent(getActivity(), LittleHelperActivity.class);
//				getActivity().startActivity(i);
//			} else {
//			
//				try {
//					docInfoBean = dbUtils.findFirst(Selector.from(
//							DocInfoBean.class).where("voipAccount", "=",
//							vSession.getContact()));
//				} catch (DbException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				if(docInfoBean == null && !vSession.getContact().startsWith("g")){
//					ToastUtil.showMessage("未获取到会话信息，可能存在非法数据。");
//					return;
//				}
//				if (CCPHelper.getInstance().getDevice() == null) {
//					myProgressDialog = new MyProgressDialog(getActivity());
//					myProgressDialog.setMessage("正在连接对话....");
//					myProgressDialog.show();
//					CCPHelper.getInstance().registerCCP(
//							new CCPHelper.RegistCallBack() {
//
//								@Override
//								public void onRegistResult(int reason,
//										String msg) {
//									// Log.i("XXX", String.format("%d, %s",
//									// reason, msg));
//									if (reason == 8192) {
//										myProgressDialog.dismiss();
//										Intent intent = new Intent(
//												getActivity(),
//												GroupChatActivity.class);
//										Bundle bundle = new Bundle();
//										if (vSession.getContact().startsWith(
//												"g")) {
//											bundle.putSerializable("groupId",
//													vSession.getContact());
//										} else {
//											bundle.putSerializable(
//													"docInfoBean", docInfoBean);
//										}
//										intent.putExtras(bundle);
//										startActivity(intent);
//										// LogUtil.i(TAG, "通讯云登录成功");
//									} else {
//										myProgressDialog.dismiss();
//										ToastUtil.showMessage("对话连接失败....");
//										// LogUtil.i(TAG, "通讯云登录失败");
//									}
//								}
//							});
//				} else {
//					Intent intent2 = new Intent(getActivity(),
//							GroupChatActivity.class);
//					Bundle bundle = new Bundle();
//					if (vSession.getContact().startsWith("g")) {
//						bundle.putSerializable("groupId", vSession.getContact());
//					} else {
//						bundle.putSerializable("docInfoBean", docInfoBean);
//					}
//					intent2.putExtras(bundle);
//					startActivity(intent2);
//				}
//
//			}
//
//		}
//
//	}
//
//	private DocInfoBean docInfoBean;
//	private UserGroupBean userGroupBean;
//
//	class IMConvAdapter extends ArrayAdapter<IMConversation> {
//		private DbUtils dbUtils;
//		LayoutInflater mInflater;
//
//		public IMConvAdapter(Context context, List<IMConversation> iMList) {
//			super(context, 0, iMList);
//			dbUtils = DBUtilsHelper.getInstance().getDb();
//			mInflater = getActivity().getLayoutInflater();
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//
//			IMConvHolder holder;
//			if (convertView == null || convertView.getTag() == null) {
//				convertView = mInflater.inflate(R.layout.im_chat_list_item,
//						null);
//				holder = new IMConvHolder();
//
//				holder.avatar = (RoundImageView) convertView
//						.findViewById(R.id.avatar);
//				holder.name = (TextView) convertView.findViewById(R.id.name);
//				holder.updateTime = (TextView) convertView
//						.findViewById(R.id.update_time);
//				holder.iLastMessage = (CCPTextView) convertView
//						.findViewById(R.id.im_last_msg);
//				holder.newCount = (TextView) convertView
//						.findViewById(R.id.im_unread_count);
//				// holder.newCountLy = (LinearLayout)
//				// convertView.findViewById(R.id.unread_count_ly);
//				holder.unReadBg = (RelativeLayout) convertView
//						.findViewById(R.id.im_unread_bg);
//			} else {
//				holder = (IMConvHolder) convertView.getTag();
//			}
//
//			IMConversation iSession = getItem(position);
//			if (iSession != null) {
//				try {
//					docInfoBean = dbUtils.findFirst(Selector.from(
//							DocInfoBean.class).where("voipAccount", "=",
//							iSession.getContact()));
//				} catch (DbException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				if (iSession.getType() == IMConversation.CONVER_TYPE_SYSTEM) {
//
//					// holder.avatar.setImageResource(R.drawable.system_messages_icon);
//				} else if (iSession.getType() == IMConversation.CONVER_TYPE_MESSAGE) {
//					if (iSession.getId().startsWith("g")) {
//						// holder.name.setText(iSession.get);
//						try {
//							userGroupBean = dbUtils.findFirst(Selector.from(
//									UserGroupBean.class).where("groupId", "=",
//									iSession.getId()));
//							if (null != userGroupBean) {
//								holder.name.setText(userGroupBean.getName());
//							}else {
//								//requestGetUserGroup(holder.name);
//							}
//						} catch (DbException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						holder.avatar.setImageResource(R.drawable.icon_group);
//					} else if (iSession.getId().equals("帮忙医小助手")) {
//						holder.avatar.setImageResource(R.drawable.icon_bang);
//						if (null != docInfoBean) {
//							holder.name.setText(docInfoBean.getUserName());
//						} else {
//							holder.name.setText(iSession.getContact());
//						}
//					} else {
//						//TODO: if the docInfoBean is null, request the api from the server
//						if (null != docInfoBean) {
//
//							ImageLoader.getInstance().displayImage(
//									docInfoBean.getHeadPicture(),
//									holder.avatar,
//									ImageLoaderUtils.getOptions(R.drawable.doc_deafault_photo));
//						}
//						if (null != docInfoBean) {
//							holder.name.setText(docInfoBean.getUserName());
//						} else {
//							holder.name.setText(iSession.getContact());
//						}
//					}
//				}
//
//				holder.updateTime.setText(iSession.getDateCreated().substring(
//						5, 11));
//
//				if (!TextUtils.isEmpty(iSession.getUnReadNum())
//						&& !"0".equals(iSession.getUnReadNum())) {
//					holder.newCount.setText(iSession.getUnReadNum());
//					holder.newCount.setVisibility(View.VISIBLE);
//					holder.unReadBg.setVisibility(View.VISIBLE);
//				} else {
//					holder.newCount.setVisibility(View.GONE);
//					holder.unReadBg.setVisibility(View.GONE);
//				}
//
//				holder.iLastMessage.setEmojiText(iSession.getRecentMessage());
//			}
//
//			return convertView;
//		}
//
//		class IMConvHolder {
//
//			RoundImageView avatar;
//			TextView name;
//			TextView updateTime;
//
//			CCPTextView iLastMessage;
//			TextView newCount;
//			LinearLayout newCountLy;
//			RelativeLayout unReadBg;
//		}
//	}
//
//	class IMMsgAsyncTask extends
//			AsyncTask<Void, Void, ArrayList<IMConversation>> {
//
//		@Override
//		protected ArrayList<IMConversation> doInBackground(Void... params) {
//			try {
//				return CCPSqliteManager.getInstance().queryIMConversation();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(ArrayList<IMConversation> result) {
//			super.onPostExecute(result);
//			if (result != null && !result.isEmpty()) {
//				
//			/*	for (int i = 0; i < result.size(); i++) {
//					if (!result.get(i).getUnReadNum().equals("0")) {
//						NotificationUtils.send(getActivity());
//					}
//				}*/
//				if( getActivity() == null)
//					return;
//				// getActivity().getClass();
//				mIMAdapter = new IMConvAdapter(getActivity(),
//						result);
//				mGroupListLv.setAdapter(mIMAdapter);
//				mGroupListEmpty.setVisibility(View.GONE);
//			} else {
//				//
//				mGroupListLv.setAdapter(null);
//				mGroupListEmpty.setVisibility(View.VISIBLE);
//				//nodata.setVisibility(View.VISIBLE);
//				//data.setText("暂无数据");
//				if (SharedPrefsUtil.getValue("loadingSuccess", false)) {
//					nodata.setVisibility(View.VISIBLE);
//					data.setText("暂无数据");
//				}
//			}
//		}
//	}
//
//	private boolean loadingSuccess;
//	protected void onReceiveBroadcast(Intent intent) {
//		if (intent != null
//				&& (CCPIntentUtils.INTENT_IM_RECIVE.equals(intent.getAction())
//						|| CCPIntentUtils.INTENT_DELETE_GROUP_MESSAGE
//								.equals(intent.getAction()) || CCPIntentUtils.INTENT_REMOVE_FROM_GROUP
//							.equals(intent.getAction()))
//				|| CCPIntentUtils.INTENT_RECEIVE_SYSTEM_MESSAGE.equals(intent
//						.getAction())
//				|| CCPIntentUtils.INTENT_JOIN_GROUP_SUCCESS.equals(intent
//						.getAction())) {
//			if (intent.hasExtra(GroupBaseActivity.KEY_GROUP_ID)) {
//				loadingSuccess=true;
//			}
//			// update UI...
//			initConversation();
//		}
//
//	}
//
//	/*
//	 * @Override public boolean onKeyDown(int keyCode, KeyEvent event) { if
//	 * (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//	 * Intent intent = new Intent(); setResult(Activity.RESULT_OK,intent);
//	 * finish(); } return super.onKeyDown(keyCode, event); } protected int
//	 * getLayoutId() { return R.layout.layout_im_group_activity; }
//	 */
//
//	private void initConversation() {
//		new IMMsgAsyncTask().execute();
//	}
//
//	private InternalReceiver internalReceiver;
//
//	protected final void registerReceiver(String[] actionArray) {
//		if (actionArray == null) {
//			return;
//		}
//		IntentFilter intentfilter = new IntentFilter(
//				INTETN_ACTION_EXIT_CCP_DEMO);
//		intentfilter.addAction(CCPIntentUtils.INTENT_CONNECT_CCP);
//		intentfilter.addAction(CCPIntentUtils.INTENT_DISCONNECT_CCP);
//		for (String action : actionArray) {
//			intentfilter.addAction(action);
//		}
//
//		if (internalReceiver == null) {
//			internalReceiver = new InternalReceiver();
//		}
//		getActivity().getApplicationContext().registerReceiver(
//				internalReceiver, intentfilter);
//	}
//
//	public static final String INTETN_ACTION_EXIT_CCP_DEMO = "exit_demo";
//
//	class InternalReceiver extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//
//			if (intent == null) {
//				return;
//			}
//
//			if (CCPIntentUtils.INTENT_KICKEDOFF.equals(intent.getAction())
//					|| CCPIntentUtils.INTENT_INVALIDPROXY.equals(intent
//							.getAction())) {
//
//				String message = "您的账号在其他地方已经登录";
//				if (CCPIntentUtils.INTENT_INVALIDPROXY.equals(intent
//						.getAction())) {
//					message = "无效的代理,与云通讯服务器断开";
//				}
//				Dialog dialog = new AlertDialog.Builder(getActivity())
//						.setTitle(R.string.account_offline_notify)
//						.setIcon(R.drawable.navigation_bar_help_icon)
//						.setMessage(message)
//						.setPositiveButton(R.string.dialog_btn,
//								new DialogInterface.OnClickListener() {
//									@Override
//									public void onClick(DialogInterface dialog,
//											int whichButton) {
//										dialog.dismiss();
//
//										CCPUtil.exitCCPDemo();
//										// launchCCP();
//									}
//
//								}).create();
//				dialog.show();
//
//			} else if (intent != null
//					&& INTETN_ACTION_EXIT_CCP_DEMO.equals(intent.getAction())) {
//				Log4Util.d(CCPHelper.DEMO_TAG, "Launcher destory.");
//				CCPUtil.exitCCPDemo();
//				// finish();
//			} else {
//				if (intent == null || TextUtils.isEmpty(intent.getAction())) {
//					return;
//				}
//
//				/**
//				 * version 3.5 for listener SDcard status
//				 */
//				if (Intent.ACTION_MEDIA_REMOVED.equalsIgnoreCase(intent
//						.getAction())
//						|| Intent.ACTION_MEDIA_MOUNTED.equalsIgnoreCase(intent
//								.getAction())) {
//
//					// updateExternalStorageState();
//					return;
//				}
//
//				onReceiveBroadcast(intent);
//			}
//		}
//
//	}
//	private void requestGetUserGroup() {
//		// TODO Auto-generated method stub
//HttpClient.get(MyApp.getInstance(),Constants.GETUSERGROUPS_URL,  new RequestParams(),new RequestCallBack<String>() {
//	@Override
//	public void onSuccess(ResponseInfo<String> arg0) {
//		// TODO Auto-generated method stub
//	//	LogUtil.i(TAG, arg0.result);
//		try {
//			JSONObject jsonObject = new JSONObject(arg0.result);
//			JSONObject data = jsonObject.getJSONObject("Data");
//			String statusCode = data.getString("statusCode");
//			if (statusCode.equals("000000")) {
//			List<UserGroupBean> list = new Gson().fromJson(data.getString("groups"), new TypeToken<List<UserGroupBean>>() {
//			}.getType());
//			for (int i = 0; i < list.size(); i++) {
//				UserGroupBean userGroupBean = list.get(i); 
//				try {
//					UserGroupBean userGroupBean2 = dbUtils.findFirst(Selector.from(UserGroupBean.class).where("groupId","=",userGroupBean.getGroupId()));
//					if (null!=userGroupBean2) {
//						return;
//					}
//				} catch (DbException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				saveGroupBean(userGroupBean);
//				IMChatMessageDetail chatMessageDetail = IMChatMessageDetail.getGroupItemMessageReceived(userGroupBean.getGroupId(),IMChatMessageDetail.TYPE_MSG_TEXT
//						, userGroupBean.getGroupId(),userGroupBean.getGroupId() );
//				// chatMessageDetail.setMessageContent(message);
//				chatMessageDetail.setDateCreated(System.currentTimeMillis()+"");
//				chatMessageDetail.setIsRead(IMChatMessageDetail.STATE_READED);
//				chatMessageDetail.setGroupName(userGroupBean.getName());
//			//	chatMessageDetail.setUserData(aMsg.getUserData());
//				try {
//					CCPSqliteManager.getInstance().insertIMMessage(chatMessageDetail);
//				} catch (SQLException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				Intent intent = new Intent(CCPIntentUtils.INTENT_IM_RECIVE);
//				intent.putExtra(GroupBaseActivity.KEY_GROUP_ID, userGroupBean.getGroupId());
//				//intent.putExtra("groupName", userGroupBean.getName());
//				MyApp.getInstance().sendBroadcast(intent);
//			}
//			}
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	@Override
//	public void onFailure(HttpException arg0, String arg1) {
//		// TODO Auto-generated method stub
//		
//	}
//});
//	}
//
//	private void saveGroupBean(UserGroupBean userGroupBean) {
//		// TODO Auto-generated method stub
//		
//		try {
//			dbUtils.createTableIfNotExist(UserGroupBean.class);
//			dbUtils.save(userGroupBean);
//			// List<DocInfoBean> docInfoBeans = db.findAll(DocInfoBean.class);
//			// DocInfoBean docInfoBean2 =
//			// db.findFirst(Selector.from(DocInfoBean.class).where("voipAccount","=","89077100000003"));
//			// DocInfoBean docInfoBean2 = db.findById(DocInfoBean.class,
//			// docInfoBean.getVoipAccount());
//			// System.out.println("doc..........."+docInfoBean2.toString());
//		} catch (DbException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//}
