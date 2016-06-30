package com.ytdinfo.keephealth.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.utils.LogUtil;

public class PhotoGridViewAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<String> mList;
	private Callback mCallback;
	private String mDesc;

	/**
	 * 自定义接口，用于回调按钮点击事件到Activity，也可以仅用来传递参数
	 * 
	 */
	public interface Callback {
		public void click(View v, int position);
	}

	public PhotoGridViewAdapter(Context context, List<String> list,
			Callback callback, String desc) {
		super();
		mInflater = LayoutInflater.from(context);
		mList = list;
		mCallback = callback;
		mDesc = desc;
	}

	@Override
	public int getCount() {
		return mList.size() > 4 ? 4 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final int po = position;

		ViewHolder viewHolder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(
					R.layout.kh_online_report_gridview_item, null);
			viewHolder = new ViewHolder();
			viewHolder.photo = (ImageView) convertView
					.findViewById(R.id.id_iv_photo);
			viewHolder.close = (ImageView) convertView
					.findViewById(R.id.id_iv_close);
			viewHolder.desc = (TextView) convertView
					.findViewById(R.id.id_tv_desc);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		LogUtil.i("paul123", position + "");

		if (parent.getChildCount() == position) { // 里面就是正常的position

			if (position == (mList.size() - 1)) {
				viewHolder.photo.setImageResource(R.drawable.add);
				viewHolder.photo.setScaleType(ScaleType.CENTER_CROP);
				viewHolder.close.setVisibility(View.GONE);
				viewHolder.desc.setText(mDesc);
			} else {
				viewHolder.close.setVisibility(View.VISIBLE);
				viewHolder.desc.setText("");
				ImageLoader.getInstance().displayImage(
						"file://" + mList.get(position), viewHolder.photo);

				LogUtil.i("paul", position + "===" + mList.get(position));
			}

			// 给删除绑定点击监听器，点击触发OnClickListener的onClick()方法
			// 响应按钮点击事件,调用子定义接口，并传入View
			viewHolder.close.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mCallback.click(v, po);
				}
			});

		} else { // 临时的position=0
			LogUtil.i("paul", position + "");
		}

		return convertView;
	}

	class ViewHolder {
		ImageView photo;
		ImageView close;
		TextView desc;
	}

}
