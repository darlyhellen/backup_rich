package com.ytdinfo.keephealth.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ytdinfo.keephealth.R;

public class CommonButton extends LinearLayout{
	public Button bt_common;

	public CommonButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		View.inflate(context, R.layout.common_button, this);
		bt_common = (Button) this.findViewById(R.id.id_bt_common);
	}
	
	/**
	 * 设置button上的文字
	 */
	public void setTitle(String title){
		bt_common.setText(title);
	}

}
