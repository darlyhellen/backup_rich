package com.ytdinfo.keephealth.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ytdinfo.keephealth.R;
/**
 * 我们自定义的组合控件,它里面有3个imageview，2个TextView
 *
 */
public class UserInfoItem_1View  extends RelativeLayout{
	public ImageView iv_touxiang;
	public TextView tv_name;
	public ImageView iv_sex;
	public TextView tv_phone;
	/**
	 * 带有2个参数的构造方法,布局文件的时候调用
	 * @param context
	 * @param attrs
	 */
	public UserInfoItem_1View(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}
	/**
	 * 初始化布局文件
	 * @param context
	 */
	private void initView(Context context) {
		//把一个布局文件---转换成一个View,并且加载在UserInfoItem_1View
		View.inflate(context, R.layout.userinfo_item_1, this);
		iv_touxiang = (ImageView) this.findViewById(R.id.id_iv_touxiang_1);
		tv_name = (TextView) this.findViewById(R.id.id_tv_name);
		iv_sex = (ImageView) this.findViewById(R.id.id_iv_sex);
		tv_phone = (TextView) this.findViewById(R.id.id_tv_phone);
	}

}
