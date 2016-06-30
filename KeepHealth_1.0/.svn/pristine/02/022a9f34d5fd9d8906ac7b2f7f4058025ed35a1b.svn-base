package com.ytdinfo.keephealth.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ytdinfo.keephealth.R;

public class CommonModifyView extends RelativeLayout {
	public TextView  tv_title;
	public TextView  tv_desc;

	public CommonModifyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		View.inflate(context, R.layout.personaldata_item_1, this);
		tv_title = (TextView) this.findViewById(R.id.id_tv_title);
		tv_desc = (TextView) this.findViewById(R.id.id_tv_desc);
	}

}
