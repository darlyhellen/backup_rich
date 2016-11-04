package com.ytdinfo.keephealth.ui;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;

import com.rayelink.eckit.BroadCastAction;
import com.rayelink.eckit.MainChatControllerListener;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.common.ui.util.FontUtils;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.MyApp;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;

public class BaseActivity extends SwipeBackActivity {
	InternalReceiver internalReceiver = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window window = getWindow();
//            // Translucent status bar
//            window.setFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
		registerReceiver(new String[] { BroadCastAction.ACTION_LOGOUT});
	}
	
 
	protected final void registerReceiver(String[] actionArray) {
		if (actionArray == null) {
			return;
		}
		IntentFilter intentfilter = new IntentFilter();
		for (String action : actionArray) {
			intentfilter.addAction(action);
		}

		if (internalReceiver == null) {
			internalReceiver = new InternalReceiver();
		}
		registerReceiver(internalReceiver, intentfilter);
	}


	public static final String INTETN_ACTION_EXIT_CCP_DEMO = "exit_demo";

	class InternalReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent == null) {
				return;
			}
			if(BroadCastAction.ACTION_LOGOUT.equals(intent.getAction()))
			{
				cancelUser();
				return;
			}
				onReceiveBroadcast(intent);
			}
	}

	protected void onReceiveBroadcast(Intent intent) {
 
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		JPushInterface.clearAllNotifications(MyApp.getInstance());
		NotificationManager nm = (NotificationManager) MyApp.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
		if(nm!=null)
			nm.cancel(100);
		if(CommConfig.getConfig()==null)
		{
			Typeface face = Typeface.createFromAsset(getAssets(),
					"fonts/lantinghei-font.TTF");
			CommunitySDK mCommSDK = CommunityFactory.getCommSDK(this);
			mCommSDK.initSDK(this.getApplicationContext());
			CommConfig.getConfig().setTypeface(face);
		}
		FontUtils.changeTypeface(getWindow().getDecorView());
	}

	private void cancelUser() {

		SharedPrefsUtil.remove(Constants.USERID);
		SharedPrefsUtil.remove(Constants.USERMODEL);
		SharedPrefsUtil.remove(Constants.ONLINE_QUES_USERMODEL);
		MainChatControllerListener.closeAllSubject(true);
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		SharedPrefsUtil.remove(Constants.TOKEN);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LogUtil.i("wpc", "BaseActivity===onDestroy");
 	 	unregisterReceiver(internalReceiver);
	}

}
