package com.umeng.common.ui.presenter.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.umeng.comm.core.beans.FeedItem;
import com.umeng.comm.core.beans.FeedItem.CATEGORY;
import com.umeng.comm.core.constants.ErrorCode;
import com.umeng.comm.core.db.ctrl.impl.DatabaseAPI;
import com.umeng.comm.core.listeners.Listeners;
import com.umeng.comm.core.nets.responses.FeedsResponse;
import com.umeng.common.ui.mvpview.MvpFeedView;

public class HotDiscussFeedPresenter extends FeedListPresenter {

    public HotDiscussFeedPresenter(MvpFeedView feedViewInterface) {
        super(feedViewInterface, true);
    }

    @Override
    public void loadDataFromServer() {
        mCommunitySDK.fetchTopFeeds(new Listeners.FetchListener<FeedsResponse>() {
            @Override
            public void onStart() {
                if(mTopFeeds != null){
                    mTopFeeds.clear();
                }
            }

            @Override
            public void onComplete(FeedsResponse response) {
                if (response.errCode == ErrorCode.NO_ERROR) {
                    mTopFeeds = response.result;
                    for (int i = 0; i < mTopFeeds.size(); i++){
                        mTopFeeds.get(i).isTop = 1;
                    }
                }
                mCommunitySDK.fetchRecommendedFeeds(mRefreshListener);
            }
        });
    }

    @Override
    protected void beforeDeliveryFeeds(FeedsResponse response) {
        for (FeedItem item : response.result) {
            item.category = CATEGORY.RECOMMEND;
        }

        DatabaseAPI.getInstance().getFeedDBAPI().clearRecommendFeed();
    }

    @Override
    public void loadDataFromDB() {
        mDatabaseAPI.getFeedDBAPI().loadRecommendFeedsFromDB(mDbFetchListener);
    }
    
    @Override
    public void sortFeedItems(List<FeedItem> items) {
        Comparator<FeedItem> comparator = getFeedCompartator();
        if (comparator != null) {
            Collections.sort(items, comparator);
        }
        items=items.subList(0, 2);
    }

    @Override
    public void loadMoreData() {
        fetchNextPageData();
    }

    @Override
    protected void saveDataToDB(List<FeedItem> newFeedItems) {
 
        mDatabaseAPI.getFeedDBAPI().saveRecommendFeedToDB(newFeedItems);
    }

    @Override
    protected void fetchDataFromServerByLogin() {
//        mCommunitySDK.fetchRecommendedFeeds(mLoginRefreshListener);
//        loadDataFromServer();
    }

    @Override
    protected Comparator<FeedItem> getFeedCompartator() {
        return null;
    }
}
