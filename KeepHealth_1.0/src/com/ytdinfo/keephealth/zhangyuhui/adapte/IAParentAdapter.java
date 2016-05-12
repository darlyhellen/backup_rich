package com.ytdinfo.keephealth.zhangyuhui.adapte;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * @ClassName: ParentAdapter
 * @Description: TODO(总适配器，其他单适配器斗继承此类，缩减代码)
 * @author 张宇辉 zhangyuhui@octmami.com
 * @date 2014年12月30日 下午2:38:00
 *
 * @param <T>
 */
public abstract class IAParentAdapter<T> extends BaseAdapter {
	protected List<T> data;
	protected int resID;
	protected Context context;

	public IAParentAdapter(List<T> data, int resID, Context context) {
		super();
		this.data = data;
		this.resID = resID;
		this.context = context;
	}

	public void setLoadMore(List<T> ts) {
		data.addAll(ts);
		notifyDataSetChanged();
	}

	public void setData(List<T> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data == null ? 0 : data.size();
	}

	@Override
	public T getItem(int position) {
		// TODO Auto-generated method stub
		return data == null ? null : data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return HockView(position, convertView, parent, resID, context,
				getItem(position));
	}

	public abstract View HockView(int position, View view, ViewGroup parent,
			int resID, Context context, T t);

}
