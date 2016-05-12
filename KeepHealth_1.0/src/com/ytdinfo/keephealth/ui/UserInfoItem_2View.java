package com.ytdinfo.keephealth.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ytdinfo.keephealth.R;

/**
 * 我们自定义的组合控件,它里面有2个imageview，1个TextView
 *
 */
@SuppressLint("NewApi")
public class UserInfoItem_2View extends RelativeLayout {
	private ImageView iv_icon;
	private TextView tv_title;

	/**
	 * 带有2个参数的构造方法,布局文件的时候调用
	 * 
	 * @param context
	 * @param attrs
	 */
	public UserInfoItem_2View(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	/**
	 * 初始化布局文件
	 */
	private void initView(Context context) {
		View.inflate(context, R.layout.userinfo_item_2, this);
		iv_icon = (ImageView) this.findViewById(R.id.id_iv_icon);
		tv_title = (TextView) this.findViewById(R.id.id_tv_title);
	}

	/**
	 * 设置图标
	 * 
	 * @param drawable
	 */
	public void setIcon(Drawable drawable) {
		try{
			iv_icon.setBackground(drawable);
		}catch(Exception e){
			iv_icon.setBackgroundDrawable(drawable);
		}
	}

	/**
	 * 设置标题
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		tv_title.setText(title);
	}

}
