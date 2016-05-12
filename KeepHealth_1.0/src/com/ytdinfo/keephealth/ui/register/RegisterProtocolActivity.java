package com.ytdinfo.keephealth.ui.register;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;

public class RegisterProtocolActivity extends BaseActivity {
	private  ImageButton back;
	private CommonActivityTopView  commonActivityTopView; 
	private TextView tv_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_protocol);
		initView();
		initListener();
	}

	

	private void initView() {
		commonActivityTopView = (CommonActivityTopView) findViewById(R.id.id_CommonActivityTopView);
		commonActivityTopView.setTitle("用户注册协议");
		back = (ImageButton) commonActivityTopView.findViewById(R.id.id_ibt_back);
		tv_content = (TextView) findViewById(R.id.id_tv_content);
	}
	private void initListener() {
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		MobclickAgent.onPageStart("RegisterProtocolActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		
		MobclickAgent.onPageEnd("RegisterProtocolActivity");
		MobclickAgent.onPause(this);
	}

}
