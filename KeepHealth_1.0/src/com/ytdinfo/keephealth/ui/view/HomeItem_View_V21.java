package com.ytdinfo.keephealth.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ytdinfo.keephealth.R;

public class HomeItem_View_V21 extends RelativeLayout {
	public ImageView iv_icon;
	public ImageView iv_header;
	public LinearLayout liner;
	public TextView tv_title;
	public Button bt_mark;
	public TextView tv_desc;

	public HomeItem_View_V21(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);

	}

	public void initView(Context context) {
		View.inflate(context, R.layout.home_v21_items_view, this);
		iv_icon = (ImageView) findViewById(R.id.id_v21_image);
		iv_header = (ImageView) findViewById(R.id.id_v21_header);
		liner = (LinearLayout) findViewById(R.id.id_v21_liner);
		tv_title = (TextView) findViewById(R.id.id_v21_title);
		bt_mark = (Button) findViewById(R.id.id_v21_mark);
		tv_desc = (TextView) findViewById(R.id.id_v21_des);
		hideIcon();
	}


	/**
	 * 下午2:48:28
	 * 
	 * @author zhangyh2 TODO 隐藏后来图片
	 */
	public void hideIcon() {
		iv_header.setVisibility(View.GONE);
	}

}
