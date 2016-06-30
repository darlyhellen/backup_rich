package com.ytdinfo.keephealth.jpush;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import cn.jpush.android.api.JPushInterface;

import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.model.TBNews;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.login.LoginActivity;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;
import com.ytdinfo.keephealth.utils.DBUtil;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.yuntongxun.kitsdk.db.ConversationSqlManager;

public class LittleHelperActivity extends BaseActivity {
	private final String ACTION_NAME = "接受消息推送的广播";
	Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			listview.setAdapter(new LittleHelperAdapter(
					LittleHelperActivity.this, list));
			listview.setSelection(listview.getBottom());
		};
	};

	// private ImageView iv_icon;
	// private TextView tv_title;
	// private TextView tv_desc;

	// Bitmap icon_bitmap;

	private final String TAG = "LittleHelperActivity";
	private ListView listview;
	private CommonActivityTopView topView;
	private List<TBNews> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == SharedPrefsUtil.getValue(Constants.TOKEN, null)) 
		{
			Intent i = new Intent(this, LoginActivity.class);
			startActivityForResult(i, 1003);
			this.finish();
			return;
		}
		setContentView(R.layout.activity_little_helper);
		new Thread()
		{
			@Override
			public void run()
			{
				ConversationSqlManager.setChattingSessionRead(ConversationSqlManager.querySessionIdForBySessionId("10000"));
			}
		}.start();
		SharedPrefsUtil.putValue(Constants.CHECKEDID_RADIOBT, 1);
		SharedPrefsUtil.putValue(Constants.CHECKISUPDATE, true);
		LogUtil.i("wpc2", "LittleHelperActivity===true");
		
		//注册广播  
        registerBoradcastReceiver();  

		initView();
		initListener();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		JPushInterface.onResume(this);
		
		MobclickAgent.onPageStart("LittleHelperActivity"); 
		MobclickAgent.onResume(this); 
	}

	@Override
	protected void onPause() {
		super.onPause();

		JPushInterface.onPause(this);
		
		MobclickAgent.onPageEnd("LittleHelperActivity");
		MobclickAgent.onPause(this);
	}
@Override
protected void onStop() {
	// TODO Auto-generated method stub
	super.onStop();
	updateReadStatus();
}

	private void initView() {

		listview = (ListView) findViewById(R.id.id_lv_little_helper);
		topView = (CommonActivityTopView) findViewById(R.id.id_CommonActivityTopView);
		topView.tv_title.setText("帮忙医小助手");
		
		
		//先显示文字
		listview.setAdapter(new LittleHelperAdapter(
				LittleHelperActivity.this, new DBUtil(LittleHelperActivity.this)
				.query()));
		listview.setSelection(listview.getBottom());

		
		//后加载图片
		new Thread(new Runnable() {

			@Override
			public void run() {
				list = getListData(new DBUtil(LittleHelperActivity.this)
						.query());

				LogUtil.i(TAG, list.size() + "");

				handler.sendEmptyMessage(0x123);

			}
		}).start();

	}

	private void initListener() {
		topView.ibt_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	public List<TBNews> getListData(List<TBNews> db_tbNews) {
		List<TBNews> listData = new ArrayList<TBNews>();
		for (int i = 0; i < db_tbNews.size(); i++) {
			TBNews tbNews = db_tbNews.get(i);
			tbNews.setBitmap(getBitmap(tbNews.getIcon()));
			listData.add(tbNews);
		}

		return listData;
	}

	public Bitmap getBitmap(String imageUrl) {
		Bitmap mBitmap = null;
		try {
			URL url = new URL(imageUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStream is = conn.getInputStream();
			mBitmap = BitmapFactory.decodeStream(is);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return mBitmap;
	}
	
	
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){

		 @Override  
	        public void onReceive(Context context, Intent intent) {  
	            String action = intent.getAction();  
	            if(action.equals(ACTION_NAME)){  
	            	LogUtil.i(TAG, "收到消息推送");
	            	initView();
	            }  
	        }  
		
	};
	
	public void registerBoradcastReceiver(){  
        IntentFilter myIntentFilter = new IntentFilter();  
        myIntentFilter.addAction(ACTION_NAME);  
        //注册广播        
        registerReceiver(mBroadcastReceiver, myIntentFilter);  
    }  
	private void updateReadStatus() {
		try {
			//补充
			//CCPSqliteManager.getInstance().updateIMMessageUnreadStatusToReadBySessionId("帮忙医小助手",IMChatMessageDetail.STATE_READED);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
