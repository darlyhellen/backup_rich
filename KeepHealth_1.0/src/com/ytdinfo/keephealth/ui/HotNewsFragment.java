package com.ytdinfo.keephealth.ui;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.common.ui.widgets.NestedListView;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.adapter.WholseAdapterV21;
import com.ytdinfo.keephealth.adapter.WholseModel;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.ui.clinic.ClinicWebView;

public class HotNewsFragment extends BaseFragment {

	private WholseAdapterV21 adapter;

	private TextView hotNewsView;

	private NestedListView listView;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.view_hot_news, container,
				false);// 关联布局文件
		initView(rootView);
		findTheHotinformation();
		return rootView;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public void sync() {
		findTheHotinformation();
	}

	private void initView(View rootView) {
		hotNewsView = (TextView) rootView.findViewById(R.id.id_hot_news);
		hotNewsView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				MobclickAgent.onEvent(getActivity(), Constants.UMENG_EVENT_38,"最新资讯");
				Intent intent = new Intent(getActivity(), ClinicWebView.class);
				intent.putExtra("loadUrl", Constants.NEWSLISTS);
				intent.putExtra("title", "最新资讯");
				startActivity(intent);
			}
		});

		listView = (NestedListView) rootView
				.findViewById(R.id.id_hot_news_list);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				
				// 点击热门咨询，跳转热门咨询。后退跳入消息列表
				WholseModel model = (WholseModel) parent
						.getItemAtPosition(position);
				if (model!=null&&model.getTitle()!=null) {
					String ti = null;
					if (model.getTitle().length() > 32) {
						ti = model.getTitle().substring(0, 31);
					} else {
						ti = model.getTitle();
					}
					MobclickAgent.onEvent(getActivity(), Constants.UMENG_EVENT_38,ti);
				}else {
					MobclickAgent.onEvent(getActivity(), Constants.UMENG_EVENT_38,"其他");
				}
				synchronized (model) {
					if (model != null) {
						Intent intent = new Intent(getActivity(),
								ClinicWebView.class);
						intent.putExtra("loadUrl", Constants.NEWSPAGE + "?id="
								+ model.getId());
						// intent.putExtra("title", "最新资讯" /* model.getTitle()
						// */);
						startActivity(intent);
					}
				}

			}
		});
		adapter = new WholseAdapterV21(null, R.layout.item_v21_wholse_adapter,
				getActivity());
		listView.setAdapter(adapter);
	}

	/**
	 * 上午11:41:08
	 * 
	 * @author zhangyh2 TODO 从公司服务器获取最新的热门咨询
	 */
	private void findTheHotinformation() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.addQueryStringParameter("page_size", "3");
		params.addQueryStringParameter("currentIndex", "1");
		params.addQueryStringParameter("categoryId", "2");
		HttpClient.get(getActivity(), Constants.INFORMATIONS, params,
				new RequestCallBack<String>() {

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						LogUtils.i(arg0.result.toString());
						parserInfomaition(arg0.result.toString());
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// 获取失败,隐藏
						hotNewsView.setVisibility(View.GONE);
					}
				});
	}

	protected void parserInfomaition(String string) {
		// TODO Auto-generated method stub
		List<WholseModel> data = new ArrayList<WholseModel>();
		try {
			JSONArray jsonArray = new JSONArray(string);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);

				WholseModel model = new Gson().fromJson(jsonObject.toString(),
						WholseModel.class);
				data.add(model);

			}
			adapter.setData(data);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// 解析失败,隐藏
			hotNewsView.setVisibility(View.GONE);
		}
	}

}
