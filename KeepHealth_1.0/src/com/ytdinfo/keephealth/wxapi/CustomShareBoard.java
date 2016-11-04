package com.ytdinfo.keephealth.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.umeng.comm.core.beans.ShareContent;
import com.umeng.comm.core.share.Shareable;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.utils.ToastUtil;

/**
 * 
 */
public class CustomShareBoard extends PopupWindow implements OnClickListener {

	public WXCallBack wxCallBack = null;

	private Handler mHandler;

	public void setWXCallBack(WXCallBack mwxCallBack) {
		this.wxCallBack = mwxCallBack;
	}

	private Activity mActivity;
	// 分享四元素
	private String titleName;
	private String thumbUrl;
	private String url;
	private String siteDesc;

	public CustomShareBoard(Activity activity) {
		super(activity);
		this.mActivity = activity;
		initView(activity);
	}

	@SuppressWarnings("deprecation")
	private void initView(Context context) {
		View rootView = LayoutInflater.from(context).inflate(
				R.layout.custom_board, null);
		rootView.findViewById(R.id.wechat).setOnClickListener(this);
		rootView.findViewById(R.id.wechat_circle).setOnClickListener(this);
		rootView.findViewById(R.id.qq).setOnClickListener(this);
		rootView.findViewById(R.id.qzone).setOnClickListener(this);
		rootView.findViewById(R.id.consel).setOnClickListener(this);
		setContentView(rootView);
		mHandler = new Handler();
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		setBackgroundDrawable(new BitmapDrawable());
		setTouchable(true);
		this.setOutsideTouchable(true);
	}

	/**
	 * 下午5:05:59
	 * 
	 * @author zhangyh2 TODO 设置分享内容
	 */
	public void setShareContent(String titleName, String thumbUrl, String url,
			String siteDesc) {
		this.titleName = titleName;
		this.thumbUrl = thumbUrl;
		this.url = url;
		this.siteDesc = siteDesc;
	}

	
	public void setShareContent(ShareContent mContent)
	{
		
		this.titleName=mContent.mText;
		if(mContent.mImageItem!=null){
			this.thumbUrl=mContent.mImageItem.thumbnail;
		}
	    this.url=mContent.mTargetUrl;
		this.siteDesc=mContent.mText;
		 if(this.titleName==null||"".equalsIgnoreCase(this.titleName))
			{
				this.titleName="      ";
				this.siteDesc="     \n";
			}

	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		UMImage image = new UMImage(mActivity, thumbUrl);
		switch (id) {
		case R.id.wechat:
			new ShareAction(mActivity).setPlatform(SHARE_MEDIA.WEIXIN)
					.setCallback(umShareListener).withText(siteDesc)
					.withTitle(titleName).withMedia(image).withTargetUrl(url)
					.share();
			break;
		case R.id.wechat_circle:
			new ShareAction(mActivity).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
					.setCallback(umShareListener).withText(siteDesc)
					.withTitle(titleName).withMedia(image).withTargetUrl(url)
					.share();
			break;
		case R.id.qq:
			break;
		case R.id.qzone:
			break;
		case R.id.consel:
			dismiss();
			break;
		default:
			break;
		}
	}

	private UMShareListener umShareListener = new UMShareListener() {
		@Override
		public void onResult(SHARE_MEDIA platform) {
			Log.d("plat", "platform" + platform);
			if (platform.name().equals("WEIXIN_FAVORITE")) {
			} else {
				if (wxCallBack != null)
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							wxCallBack.shareComplete(true);
							Log.i("----", "wxCallBack----true");

						}
					});
			}
		}

		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			if (wxCallBack != null)
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						wxCallBack.shareComplete(false);
						Log.i("----", "wxCallBack----false");
					}
				});

		}

		@Override
		public void onCancel(SHARE_MEDIA platform) {
			ToastUtil.showMessage(" 分享取消了");
		}
	};

 

}
