package com.ytdinfo.keephealth.ui;

import java.util.Calendar;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.umeng.comm.core.CommunitySDK;
import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.constants.Constants;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.impl.CommunityFactory;
import com.umeng.comm.core.listeners.Listeners.FetchListener;
import com.umeng.comm.core.listeners.Listeners.OnItemViewClickListener;
import com.umeng.comm.core.nets.Response;
import com.umeng.comm.core.nets.responses.FeedsResponse;
import com.umeng.comm.ui.activities.FeedDetailActivity;
import com.umeng.comm.ui.adapters.FeedAdapter;
import com.umeng.common.ui.util.BroadcastUtils;
import com.umeng.common.ui.widgets.NestedListView;
import com.umeng.community.UmengNickNameUtils;
import com.ytdinfo.keephealth.R;

public class HotDiscussFragment extends BaseFragment {

	private FeedAdapter adapter;

	private TextView hotNewsView;

	private NestedListView listView;

	private List<FeedItem> mLists;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.view_hot_discuss, container,
				false);// 关联布局文件
		initView(rootView);
		findTheHotinformation();
		registerBroadcast();
		return rootView;
	}

	public void sync() {
		findTheHotinformation();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		registerBroadcast();
	}

	private void initView(View rootView) {

		hotNewsView = (TextView) rootView.findViewById(R.id.id_hot_news);
		hotNewsView.setCompoundDrawablesWithIntrinsicBounds(
				R.drawable.ic_jiankangquan, 0, R.drawable.jiantou, 0);
		hotNewsView.setText("健康圈");
		hotNewsView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				MobclickAgent.onEvent(getActivity(),
						com.ytdinfo.keephealth.app.Constants.UMENG_EVENT_39,
						"健康圈");
				UmengNickNameUtils.handleUmengName(getActivity());
			}
		});

		listView = (NestedListView) rootView
				.findViewById(R.id.id_hot_news_list);
		adapter = new FeedAdapter(getActivity());
		adapter.setCommentClickListener(new OnItemViewClickListener<FeedItem>() {

			@Override
			public void onItemClick(int position, FeedItem item) {
				// 先进入feed详情页面，再弹出评论编辑键盘
				if (item.text != null) {
					String ti = null;
					if (item.text.length() > 32) {
						ti = item.text.substring(0, 31);
					} else {
						ti = item.text;
					}
					MobclickAgent
							.onEvent(
									getActivity(),
									com.ytdinfo.keephealth.app.Constants.UMENG_EVENT_39,
									ti);
				} else {
					MobclickAgent
							.onEvent(
									getActivity(),
									com.ytdinfo.keephealth.app.Constants.UMENG_EVENT_39,
									"其他");
				}
				Intent intent = new Intent(getActivity(),
						FeedDetailActivity.class);
				intent.putExtra(Constants.TAG_FEED, item);
				intent.putExtra(Constants.TAG_IS_COMMENT, true);
				intent.putExtra(Constants.TAG_IS_SCROLL, true);
				getActivity().startActivity(intent);
			}
		});
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
		mCommSDK.fetchTopFeeds(new FetchListener<FeedsResponse>() {
			@Override
			public void onComplete(FeedsResponse response) {
				if (response.errCode == Response.NO_ERROR) {
					if (response != null) {
						if (response.result.size() > 2) {
							mLists = response.result.subList(0, 2);
						} else {
							mLists = response.result;
						}
						for (int i = 0; i < mLists.size(); i++) {
							mLists.get(i).isTop = 1;
						}
						adapter.updateListViewData(mLists);
					}
				} else {
					hotNewsView.setVisibility(View.GONE);
				}
			}

			@Override
			public void onStart() {

			}
		});
	}

	protected void registerBroadcast() {
		// 注册广播接收器
		BroadcastUtils.registerFeedBroadcast(getActivity(), mReceiver);
		BroadcastUtils.registerFeedUpdateBroadcast(getActivity(), mReceiver);
	}

	@Override
	public void onDestroy() {
		BroadcastUtils.unRegisterBroadcast(getActivity(), mReceiver);
		super.onDestroy();
	}

	/**
	 * 数据同步处理
	 */
	protected BroadcastUtils.DefalutReceiver mReceiver = new BroadcastUtils.DefalutReceiver() {
		@Override
		public void onReceiveUser(Intent intent) {

		}

		@Override
		public void onReceiveFeed(Intent intent) {// 发送or删除时
			findTheHotinformation();
		}

		// 更新Feed的相关数据。包括like、comment、forward数量修改
		@Override
		public void onReceiveUpdateFeed(Intent intent) {
			if (adapter != null) {
				FeedItem item = getFeed(intent);
				List<FeedItem> items = adapter.getDataSource();
				for (FeedItem feed : items) {
					if (feed.id.equals(item.id)) {
						feed.isLiked = item.isLiked;
						feed.likeCount = item.likeCount;
						feed.likes = item.likes;
						feed.commentCount = item.commentCount;
						feed.comments = item.comments;
						feed.forwardCount = item.forwardCount;
						feed.isCollected = item.isCollected;
						feed.category = item.category;
						break;
					}
				}
				// 此处不可直接调用adapter.notifyDataSetChanged，其他地方在notifyDataSetChanged（）方法中又逻辑处理
				adapter.notifyDataSetChanged();
			}
		}
	};

}
