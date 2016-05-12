package com.ytdinfo.keephealth.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ytdinfo.keephealth.R;

public class CommonActivityTopView extends RelativeLayout {
	public ImageButton ibt_back;
	public TextView tv_title;
	public Button bt_save;

	public CommonActivityTopView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		View.inflate(context, R.layout.common_activity_top, this);
		ibt_back = (ImageButton) this.findViewById(R.id.id_ibt_back);
		tv_title = (TextView) this.findViewById(R.id.id_tv_title);
		bt_save = (Button) findViewById(R.id.id_bt_save);
	}

	/**
	 * 设置标题
	 */
	public void setTitle(String title) {
		tv_title.setText(title);
	}

}
