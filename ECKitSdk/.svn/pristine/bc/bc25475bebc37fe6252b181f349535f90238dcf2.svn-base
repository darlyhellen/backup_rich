/*
 *  Copyright (c) 2015 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */package com.yuntongxun.kitsdk.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yuntongxun.eckitsdk.R;
import com.yuntongxun.ecsdk.ECDevice.ECDeviceState;
import com.yuntongxun.ecsdk.platformtools.ECHandlerHelper;
import com.yuntongxun.kitsdk.adapter.CCPListAdapter.OnListAdapterCallBackListener;
import com.yuntongxun.kitsdk.adapter.ConversationAdapter;
import com.yuntongxun.kitsdk.core.ECKitConstant;
import com.yuntongxun.kitsdk.core.ECKitCustomProviderManager;
import com.yuntongxun.kitsdk.custom.provider.conversation.ECCustomConversationListActionProvider;
import com.yuntongxun.kitsdk.db.GroupNoticeSqlManager;
import com.yuntongxun.kitsdk.db.IMessageSqlManager;
import com.yuntongxun.kitsdk.ui.ECChattingActivity;
import com.yuntongxun.kitsdk.ui.ECGroupNoticeActivity;
import com.yuntongxun.kitsdk.ui.chatting.model.ECConversation;
import com.yuntongxun.kitsdk.ui.chatting.model.IMChattingHelper;
import com.yuntongxun.kitsdk.utils.LogUtil;
import com.yuntongxun.kitsdk.utils.ToastUtil;
import com.yuntongxun.kitsdk.view.BaseFragment;
import com.yuntongxun.kitsdk.view.ECProgressDialog;
import com.yuntongxun.kitsdk.view.NetWarnBannerView;

public class ConversationListFragment extends BaseFragment implements
		OnListAdapterCallBackListener{
	//,GroupService.Callback
 	

	private static final String TAG = "ConversationListFragment";

	/** 会话消息列表ListView */
	private ListView mListView;
	private NetWarnBannerView mBannerView;
	private ConversationAdapter mAdapter;
	private OnUpdateMsgUnreadCountsListener mAttachListener;
	private ECProgressDialog mPostingdialog;
//	private ImageView iv_nodata;
//	private TextView tv_data;
	
	
	
	final private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View visew,
				int position, long id) {

			if (mAdapter != null) {
				int headerViewsCount = mListView.getHeaderViewsCount();
				if (position < headerViewsCount) {
					return;
				}
				int _position = position - headerViewsCount;

				if (mAdapter == null || mAdapter.getItem(_position) == null) {
					return;
				}
				ECConversation conversation = mAdapter.getItem(_position);
				
				if(conversation!=null&&conversation.getSessionId().equals("10000"))
				{
					IMChattingHelper.getInstance();
					if(IMChattingHelper.chatControllerListener!=null) {
						IMChattingHelper.getInstance();
						IMChattingHelper.chatControllerListener.goToBMYHelpActivity(getActivity());
					}
					return;
				}

				ECCustomConversationListActionProvider obj = ECKitCustomProviderManager
						.getCustomConversationAction();
				
				if (obj != null) {

					boolean result = obj.onCustomConversationItemClick(
							getActivity(), conversation);

					if (result) {

						return;
					}
				}

				if (GroupNoticeSqlManager.CONTACT_ID.equals(conversation
						.getSessionId())) {
					Intent intent = new Intent(getActivity(),
							ECGroupNoticeActivity.class);
					startActivity(intent);
					return;
				}
				Intent intent = new Intent(getActivity(),
						ECChattingActivity.class);
				intent.putExtra(ECKitConstant.KIT_CONVERSATION_TARGET,
						conversation.getSessionId());
				intent.putExtra(ECChattingActivity.CONTACT_USER,
						conversation.getUsername());
				startActivity(intent);
			}
		}
	};

//	private final AdapterView.OnItemLongClickListener mOnLongClickListener = new AdapterView.OnItemLongClickListener() {
//		@Override
//		public boolean onItemLongClick(AdapterView<?> parent, View view,
//				int position, long id) {
//			if (mAdapter != null) {
//				int headerViewsCount = mListView.getHeaderViewsCount();
//				if (position < headerViewsCount) {
//					return false;
//				}
//				int _position = position - headerViewsCount;
//
//				if (mAdapter == null || mAdapter.getItem(_position) == null) {
//					return false;
//				}
//				ECConversation conversation = mAdapter.getItem(_position);
//
//				ECCustomConversationListActionProvider obj = ECKitCustomProviderManager
//						.getCustomConversationAction();
//
//				if (obj != null
//						) {
//
//					boolean result= obj.onCustonConversationLongClick(getActivity(),
//							conversation);
//					
//					if(result){
//					
//					return false;
//					}
//				}
//
//				List<String> list = new ArrayList<String>();
//				list.add(getString(R.string.main_delete));
//
//				ECCustomConversationListUIProvider uiObj = ECKitCustomProviderManager
//						.getCustomConversationListUIProvider();
//
//				if (uiObj != null) {
//					List<String> uiList = uiObj
//							.getCustomConversationItemLongClickMenu(
//									ConversationListFragment.this, conversation);
//					if (uiList != null && uiList.size() > 0) {
//
//						for (String item : uiList) {
//
//							list.add(item);
//
//						}
//
//					}
//
//				}
//
//				final int itemPosition = position;
//				ECListDialog dialog = new ECListDialog(getActivity(),
//						list.toArray(new String[list.size()]));
//
//				dialog.setOnDialogItemClickListener(new ECListDialog.OnDialogItemClickListener() {
//					@Override
//					public void onDialogItemClick(Dialog d, int position) {
//						handleContentMenuClick(itemPosition, position);
//					}
//				});
//				dialog.setTitle(conversation.getUsername());
//				dialog.show();
//				return true;
//			}
//			return false;
//		}
//	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		//   registerReceiver(new String[]{getActivity().getPackageName() + ".inited" ,IMessageSqlManager.ACTION_GROUP_DEL,GroupService.ACTION_SYNC_GROUP});
		//registerReceiver(new String[] { GroupService.ACTION_SYNC_GROUP,IMessageSqlManager.ACTION_GROUP_DEL });
		// registerReceiver(new String[]{getActivity().getPackageName() + ".inited" ,IMessageSqlManager.ACTION_GROUP_DEL});
	}

	@Override
	public void onResume() {
		super.onResume();
		updateConnectState();
		//GroupService.syncGroup(this);
		IMessageSqlManager.registerMsgObserver(mAdapter);
		mAdapter.notifyChange();
	   // GroupService.syncGroup(this);
  
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mAttachListener = (OnUpdateMsgUnreadCountsListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnUpdateMsgUnreadCountsListener");
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			IMessageSqlManager.unregisterMsgObserver(mAdapter);
		} catch (Exception e) {
			LogUtil.e("Exception" + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	
	private OnGetConversationInfoListener onGetPublicInfoInterface;
	
	public interface OnGetConversationInfoListener{
		public void getConversationInfo(ECConversation conversation,TextView textView,ImageView imageView,ImageView isonline);
		public void getBMYXZSConversationInfo(ECConversation conversation,TextView msgView,TextView timeView);
	};
	
	public void setOnGetConversationInfoListener(OnGetConversationInfoListener infoInterface)
	{
		onGetPublicInfoInterface=infoInterface;
	}
	
	/**
     *
     */
	private void initView() {
		if (mListView != null) {
			mListView.setAdapter(null);

			if (mBannerView != null) {
				mListView.removeHeaderView(mBannerView);
			}
		}

		mListView = (ListView) findViewById(R.id.main_chatting_lv);
		
		//iv_nodata=(ImageView)findViewById(R.id.iv_nodata);
		//tv_data=(TextView)findViewById(R.id.tv_data);
		//View mEmptyView = findViewById(R.id.empty_conversation_tv);
		
		View group_list_empty=findViewById(R.id.group_list_empty);
		mListView.setEmptyView(group_list_empty);
		
		mListView.setDrawingCacheEnabled(false);
		mListView.setScrollingCacheEnabled(false);

		//mListView.setOnItemLongClickListener(mOnLongClickListener);
		mListView.setOnItemClickListener(mItemClickListener);
		mBannerView = new NetWarnBannerView(getActivity());
		mListView.addHeaderView(mBannerView);
		mAdapter = new ConversationAdapter(getActivity(), this,onGetPublicInfoInterface);
		mListView.setAdapter(mAdapter);

		registerForContextMenu(mListView);

	}

	public void updateConnectState() {
		if (!isAdded()) {
			return;
		}
		ECDeviceState connect = ECDeviceState.ONLINE;
		if (connect == ECDeviceState.OFFLINE) {
			mBannerView
					.setNetWarnText(getString(R.string.connect_server_error));
			mBannerView.reconnect(false);
		} else if (connect == ECDeviceState.OFFLINE) {
			mBannerView
					.setNetWarnText(getString(R.string.connect_server_error));
			mBannerView.reconnect(false);
		} else if (connect == ECDeviceState.ONLINE) {
			mBannerView.hideWarnBannerView();
		}
	}

	private Boolean handleContentMenuClick(int convresion, int position) {
		if (mAdapter != null) {
			int headerViewsCount = mListView.getHeaderViewsCount();
			if (convresion < headerViewsCount) {
				return false;
			}
			int _position = convresion - headerViewsCount;

			if (mAdapter == null || mAdapter.getItem(_position) == null) {
				return false;
			}
			final ECConversation conversation = mAdapter.getItem(_position);
			switch (position) {
			case 0:
				mPostingdialog = new ECProgressDialog(
						ConversationListFragment.this.getActivity(),
						R.string.clear_chat);
				mPostingdialog.show();
				ECHandlerHelper handlerHelper = new ECHandlerHelper();
				handlerHelper.postRunnOnThead(new Runnable() {
					@Override
					public void run() {
						IMessageSqlManager.deleteChattingMessage(conversation
								.getSessionId());
						ToastUtil.showMessage(R.string.clear_msg_success);
						ConversationListFragment.this.getActivity()
								.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										dismissPostingDialog();
										mAdapter.notifyChange();
									}
								});
					}
				});

				break;

			default:
				ECCustomConversationListActionProvider obj = ECKitCustomProviderManager
						.getCustomConversationAction();

				if (obj != null
						)
					

				 obj.onCustomConversationMenuItemClick(getActivity(),
						conversation, position);

				break;
			}
		}
		return null;
	}

	/**
	 * 关闭对话框
	 */
	private void dismissPostingDialog() {
		if (mPostingdialog == null || !mPostingdialog.isShowing()) {
			return;
		}
		mPostingdialog.dismiss();
		mPostingdialog = null;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.ytx_conversation;
	}
	
	
	/**
	 * @return the mAdapter
	 */
	public ConversationAdapter getmAdapter() {
		return mAdapter;
	}

	@Override
	public void OnListAdapterCallBack() {
		if (mAttachListener != null) {
			mAttachListener.OnUpdateMsgUnreadCounts();
		}
	}

	public interface OnUpdateMsgUnreadCountsListener {
		void OnUpdateMsgUnreadCounts();
	}

	@Override
	protected void handleReceiver(Context context, Intent intent) {
		super.handleReceiver(context, intent);
//		 if(intent.getAction().equals(new String[]{getActivity().getPackageName()+".inited"})){
//	            GroupService.syncGroup(this);
//	        } else if (IMessageSqlManager.ACTION_GROUP_DEL.equals(intent.getAction())) {
//	            onSyncGroup();
//	        }else if(intent.getAction().equals(GroupService.ACTION_SYNC_GROUP))
//	        {
//	        	Log.e("GroupService-handleReceiver","ACTION_SYNC_GROUP");
//	        }
	}
	
	
	


//	 	@Override
//	    public void onSyncGroup() {
//	 		Log.e("ConversationListFragment","onSyncGroup");
//	        //mAdapter.notifyChange();
//	    }
//
//	    @Override
//	    public void onSyncGroupInfo(String groupId) {
//	 		Log.e("ConversationListFragment","onSyncGroupInfo");
//	    }
//
//	    @Override
//	    public void onGroupDel(String groupId) {
//	    	Log.e("ConversationListFragment","onGroupDel("+groupId+")");
//	        //onSyncGroup();
//	    }
//
//	    @Override
//	    public void onError(ECError error) {
//	    	Log.e("ConversationListFragment",error.errorMsg);
//	    }

}
