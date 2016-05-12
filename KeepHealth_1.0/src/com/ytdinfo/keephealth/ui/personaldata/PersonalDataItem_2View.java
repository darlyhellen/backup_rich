package com.ytdinfo.keephealth.ui.personaldata;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ytdinfo.keephealth.R;

public class PersonalDataItem_2View extends RelativeLayout {
	public TextView  tv_title;
	public ImageView iv_touxiang;

	public PersonalDataItem_2View(Context context, AttributeSet attrs) {
		super(context, attrs);
		setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
		initView(context);
	}

	private void initView(Context context) {
		View.inflate(context, R.layout.personaldata_item_2, this);
		tv_title = (TextView) this.findViewById(R.id.id_tv_title);
		iv_touxiang =  (ImageView) this.findViewById(R.id.id_iv_touxiang);
	}

}
