package com.ytdinfo.keephealth.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ytdinfo.keephealth.R;

/**
 * 我们自定义的组合控件,它里面有1个imageview，1个TextView
 *
 */
@SuppressLint("NewApi")
public class ShouYeItem_1View extends LinearLayout {
	private ImageView iv;
	private TextView tv;
	/**
	 * 初始化布局文件
	 * @param context
	 */
	private void iniView(Context context) {
		//把一个布局文件---转换成一个View,并且加载在ShouYeItem_1View
		View.inflate(context,R.layout.shouye_item_1,this);
		iv = (ImageView) this.findViewById(R.id.id_iv_icon);
		tv = (TextView) this.findViewById(R.id.id_tv_title);
	}
	public ShouYeItem_1View(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		iniView(context);
	}
	/**
	 * 带有2个参数的构造方法,布局文件的时候调用
	 * @param context
	 * @param attrs
	 */
	public ShouYeItem_1View(Context context, AttributeSet attrs) {
		super(context, attrs);
		iniView(context);
//		String title = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.qzd.mobilesafe","title");
//		desc_on = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.qzd.mobilesafe","desc_on");
//		desc_off = attrs.getAttributeValue("http://schemas.android.com/apk/res/com.qzd.mobilesafe","desc_off");
//		tv_title.setText(title);
	}
	public ShouYeItem_1View(Context context) {
		super(context);
		iniView(context);
	}
	/**
	 * 设置图标
	 */
	public void setIcon(Drawable drawable){
		iv.setBackground(drawable);
	}
	/**
	 * 设置标题信息
	 */
	public void setTitle(String text){
		tv.setText(text);
	}
	
}
