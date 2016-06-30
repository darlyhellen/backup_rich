package com.yuntongxun.kitsdk;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.yuntongxun.kitsdk.beans.ChatInfoBean;
import com.yuntongxun.kitsdk.core.CCPAppManager;
import com.yuntongxun.kitsdk.core.ECKitConstant;
import com.yuntongxun.kitsdk.ui.ECChattingActivity;
import com.yuntongxun.kitsdk.ui.ECConversationListActivity;
import com.yuntongxun.kitsdk.ui.chatting.model.IMChattingHelper;
import com.yuntongxun.kitsdk.ui.group.ECGroupListActivity;

public class IMKitManager {

	protected static IMKitManager sInstance;

	protected static IMKitManager getInstance() {
		if (sInstance == null) {
			synchronized (IMKitManager.class) {
				sInstance = new IMKitManager();
			}
		}

		return sInstance;
	}

	public void startConversationActivity(Context context,String target) {

		Intent intent = new Intent(CCPAppManager.getContext(),
				ECChattingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(ECKitConstant.KIT_CONVERSATION_TARGET, target);
		context.startActivity(intent);
	}
	
	
	public void startConversationActivity(String target) {

		Intent intent = new Intent(CCPAppManager.getContext(),
				ECChattingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(ECKitConstant.KIT_CONVERSATION_TARGET, target);
		CCPAppManager.getContext().startActivity(intent);

	}
	

	public void startConversationActivity(ChatInfoBean chatInfoBean,List<String> list_imagesUrl,String bodyContenet) {

		Intent intent = new Intent(CCPAppManager.getContext(),
				ECChattingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Bundle bundle=new Bundle();
		bundle.putSerializable("ChatInfoBean", chatInfoBean);
		intent.putStringArrayListExtra("ImageList", (ArrayList<String>) list_imagesUrl);
		intent.putExtra("Content", bodyContenet);
		intent.putExtra("FromQues", true);
		intent.putExtras(bundle);
		CCPAppManager.getContext().startActivity(intent);

	}
	
	public void startConversationListActivity() {

		Intent intent = new Intent(CCPAppManager.getContext(),
				ECConversationListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		CCPAppManager.getContext().startActivity(intent);

	}

	public static void setAutoReceiverOfflineMsg(boolean isAuto) {

		IMChattingHelper.isAutoGetOfflineMsg = isAuto;
	}
	
	public void startGroupListActivity() {

		Intent intent = new Intent(CCPAppManager.getContext(),
				ECGroupListActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		CCPAppManager.getContext().startActivity(intent);

	}

}
