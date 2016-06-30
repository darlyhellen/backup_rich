package com.ytdinfo.keephealth.zhangyuhui.view.ichnography;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ytdinfo.keephealth.zhangyuhui.model.IAOrganization_Departments;

/**
 * @author Administrator 基础类。方便统一管理。
 */
public abstract class IABaseFrame extends FrameLayout {

	public IABaseFrame(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public IABaseFrame(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ImageLoader imageLoader = ImageLoader.getInstance();
	
	
	public static final int KEP = 1000;
	
	public abstract void setChange(
			ArrayList<IAOrganization_Departments> organization_departments);

	abstract class NoDoubleClickListener implements OnClickListener {

		public static final int MIN_CLICK_DELAY_TIME = 3000;
		private long lastClickTime = 0;

		@Override
		public void onClick(View v) {
			long currentTime = Calendar.getInstance().getTimeInMillis();
			if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
				lastClickTime = currentTime;
				onNoDoubleClick(v);
			}
		}
		
		public abstract void onNoDoubleClick(View v);
	}
}
