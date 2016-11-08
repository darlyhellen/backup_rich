package com.ytdinfo.keephealth.ui;

import com.umeng.comm.ui.fragments.CommunityMainFragment;
import com.ytdinfo.keephealth.R;

import android.os.Bundle;

/**
 * @author zhangyh2 FriendsCircleActivity 上午10:32:32 TODO
 */
public class FriendsCircleActivity extends BaseActivity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ytdinfo.keephealth.ui.BaseActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_circle);
		getSupportFragmentManager().beginTransaction()
				.add(R.id.friend_content, new CommunityMainFragment()).commit();
	}

}
