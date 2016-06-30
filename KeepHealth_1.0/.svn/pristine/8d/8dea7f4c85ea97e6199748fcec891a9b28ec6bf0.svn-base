/**上午11:33:49
 * @author zhangyh2
 * WholseAdapterV21.java
 * TODO
 */
package com.ytdinfo.keephealth.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.common.ui.util.FontUtils;
import com.ytdinfo.keephealth.R;

/**
 * @author zhangyh2 WholseAdapterV21 上午11:33:49 TODO
 */
public class WholseAdapterV21 extends ParentAdapter<WholseModel> {

	/**
	 * 上午11:34:09
	 * 
	 * @author zhangyh2
	 */
	public WholseAdapterV21(List<WholseModel> data, int resID, Context context) {
		super(data, resID, context);
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.darly.dlclent.adapter.ParentAdapter#HockView(int,
	 * android.view.View, android.view.ViewGroup, int, android.content.Context,
	 * java.lang.Object)
	 */
	@Override
	public View HockView(int position, View view, ViewGroup parent, int resID,
			Context context, WholseModel t) {
		// TODO Auto-generated method stub
		ViewHoker hoker = null;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(resID, null);
			hoker = new ViewHoker();
			hoker.icon = (ImageView) view.findViewById(R.id.v21_adapter_iv);
			// 获取屏幕宽高
			WindowManager wm = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			int width = wm.getDefaultDisplay().getWidth();
			int with = (int) (width / 4.5);
			LayoutParams lp = new LayoutParams(with, with);
			hoker.icon.setLayoutParams(lp);
			hoker.title = (TextView) view.findViewById(R.id.v21_adapter_name);
			hoker.descr = (TextView) view.findViewById(R.id.v21_adapter_desc);
			view.setTag(hoker);
		} else {
			hoker = (ViewHoker) view.getTag();
		}
		ImageLoader.getInstance().displayImage(t.getImg_url(), hoker.icon);
		hoker.title.setText(t.getTitle());
		hoker.descr.setText(t.getZhaiyao());
		FontUtils.changeTypeface(view);
		return view;
	}

	class ViewHoker {
		ImageView icon;
		TextView title;
		TextView descr;
	}

}
