package com.ytdinfo.keephealth.jpush;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.model.TBNews;

public class LittleHelperAdapter extends BaseAdapter {
	private String TAG = "LittleHelperAdapter";

	private List<TBNews> mList;
	private LayoutInflater mInflater;
	private Context mContext;

//	private Bitmap icon_bitmap;

	public LittleHelperAdapter(Context context, List<TBNews> list) {
		mList = list;
		mInflater = LayoutInflater.from(context);
		mContext = context;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {

		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final TBNews tbNews = mList.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.adapter_little_helper,
					null);

			holder = new ViewHolder();
			holder.iv_icon = (ImageView) convertView
					.findViewById(R.id.id_iv_icon);
			holder.tv_title = (TextView) convertView
					.findViewById(R.id.id_tv_title);
			holder.tv_desc = (TextView) convertView
					.findViewById(R.id.id_tv_desc);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv_title.setText(tbNews.getTitle());
		holder.tv_desc.setText(tbNews.getDesc());
		holder.iv_icon.setImageBitmap(tbNews.getBitmap());
		
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(mContext, LittleHelperWebViewActivity.class);
				i.putExtra("loadUrl", tbNews.getUrl());
				mContext.startActivity(i);
			}
		});
		
		
		
		return convertView;
	}

	public class ViewHolder {
		public ImageView iv_icon;
		public TextView tv_title;
		public TextView tv_desc;
	}


}
