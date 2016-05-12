package com.ytdinfo.keephealth.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ytdinfo.keephealth.R;

public class HomeItem_1View extends RelativeLayout {
	public ImageView iv_icon;
	public TextView tv_title;
	public Button bt_mark;
	public TextView tv_desc;

	public HomeItem_1View(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);

	}

	public void initView(Context context) {
		View.inflate(context, R.layout.home_item_1view, this);
		iv_icon = (ImageView) findViewById(R.id.id_iv_icon);
		tv_title = (TextView) findViewById(R.id.id_tv_title);
		bt_mark = (Button) findViewById(R.id.id_bt_mark);
		tv_desc  = (TextView) findViewById(R.id.id_tv_desc);

	}

}
