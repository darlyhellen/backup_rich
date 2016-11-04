package com.ytdinfo.keephealth.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

public class MyWebView extends WebView {

	public MyWebView(Context context) {
		super(context);
	}

	public MyWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	long last_time = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			long current_time = System.currentTimeMillis();
			long d_time = current_time - last_time;
			System.out.println(d_time);
			;
			if (d_time < 300) {
				last_time = current_time;
				return true;
			} else {
				last_time = current_time;
			}
			break;
		}
		return super.onTouchEvent(event);
	}

	private OnScrollChangedCallback mOnScrollChangedCallback;

	public MyWebView(final Context context, final AttributeSet attrs,
			final int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onScrollChanged(final int l, final int t, final int oldl,
			final int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);

		if (mOnScrollChangedCallback != null) {
			mOnScrollChangedCallback.onScroll(l, t, oldl, oldt);
		}
	}

	public OnScrollChangedCallback getOnScrollChangedCallback() {
		return mOnScrollChangedCallback;
	}

	public void setOnScrollChangedCallback(
			final OnScrollChangedCallback onScrollChangedCallback) {
		mOnScrollChangedCallback = onScrollChangedCallback;
	}

	/**
	 * Impliment in the activity/fragment/view that you want to listen to the
	 * webview
	 */
	public static interface OnScrollChangedCallback {
		public void onScroll(int x, int y, int olx, int oly);
	}
}