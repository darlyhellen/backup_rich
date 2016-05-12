package com.ytdinfo.keephealth.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ytdinfo.keephealth.R;

public class HomeItem_2View extends LinearLayout {
	public ImageView iv_icon;
	public TextView tv_title;

	public HomeItem_2View(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);

	}

	public void initView(Context context) {
		View.inflate(context, R.layout.home_item_2view, this);
		iv_icon = (ImageView) findViewById(R.id.id_iv_icon);
		tv_title = (TextView) findViewById(R.id.id_tv_title);

	}

}
