package com.ytdinfo.keephealth.ui;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.listeners.Listeners.FetchListener;
import com.umeng.comm.core.nets.responses.FeedsResponse;
import com.umeng.comm.ui.adapters.FeedAdapter;
import com.umeng.common.ui.widgets.NestedListView;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.ui.clinic.ClinicWebView;

public class HotDiscussFragment extends BaseFragment{
	
	private FeedAdapter adapter;
	
	private TextView hotNewsView;
	
	private NestedListView listView;
	
	
	private List<FeedItem> mLists;
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView=inflater.inflate(R.layout.view_hot_news, container,
				false);// 关联布局文件
		initView(rootView);
		findTheHotinformation();
		return rootView;
	}
	
	
	public void sync()
	{
		findTheHotinformation();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	private  void initView(View rootView)
	{
		hotNewsView=(TextView)rootView.findViewById(R.id.id_hot_news);
		hotNewsView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_jiankangquan, 0,R.drawable.jiantou,0);
		hotNewsView.setText("健康圈");
		hotNewsView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(), ClinicWebView.class);
				intent.putExtra("loadUrl", Constants.NEWSLISTS);
				intent.putExtra("title", "最新资讯");
				startActivity(intent);
			}
		});
		
		listView =(NestedListView)rootView.findViewById(R.id.id_hot_news_list);
		adapter = new FeedAdapter(getActivity());		
		listView.setAdapter(adapter);
	}
 
	/**
	 * 上午11:41:08
	 * 
	 * @author zhangyh2 TODO 从公司服务器获取最新的热门咨询
	 */
	private void findTheHotinformation() {
		// TODO Auto-generated method stub
		CommunitySDK mCommSDK = CommunityFactory.getCommSDK(getActivity());
        mCommSDK.fetchRecommendedFeeds(new FetchListener<FeedsResponse>() {

            @Override
            public void onComplete(FeedsResponse response) {
            	if(response.errCode==FeedsResponse.NO_ERROR){
	               if(response!=null)
	               {
	            	   if(response.result.size()>3)
	            	   {
	            		   mLists=response.result.subList(0, 3);
	            	   }else {
	            		   mLists=response.result;
	            	   }
	            	   adapter.updateListViewData(mLists);
	               }
            	}else {
            		hotNewsView.setVisibility(View.GONE);
				}
            }

            @Override
            public void onStart() {

            }
        });
	}

	 
	
	
	 
}