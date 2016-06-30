package com.ytdinfo.keephealth.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ytdinfo.keephealth.R;

public class HomeV21Title extends RelativeLayout {
	public TextView tv_title;

	private RelativeLayout backGround;

	public HomeV21Title(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		View.inflate(context, R.layout.home_title_v21, this);
		tv_title = (TextView) this.findViewById(R.id.id_hometitle_title);
		backGround = (RelativeLayout) this
				.findViewById(R.id.id_hometitle_backgroud);
	}

	/**
	 * 设置标题
	 */
	public void setTitle(String title) {
		tv_title.setText(title);
	}

	public void setback(int alpha) {
		backGround.getBackground().setAlpha(alpha);
	}

}
