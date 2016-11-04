package com.ytdinfo.keephealth.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ytdinfo.keephealth.R;

public class HomeV21Title extends RelativeLayout {
	public TextView tv_title;
	public ImageButton ibt_back;
	public RelativeLayout backGround;

	public HomeV21Title(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	private void initView(Context context) {
		View.inflate(context, R.layout.home_title_v21, this);
		ibt_back = (ImageButton) this.findViewById(R.id.id_ibt_back);
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

	public void showBack(boolean show) {
		if (show) {
			ibt_back.setVisibility(View.VISIBLE);
		} else {
			ibt_back.setVisibility(View.INVISIBLE);
		}
	}
}
