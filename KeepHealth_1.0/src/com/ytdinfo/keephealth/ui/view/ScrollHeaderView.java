/**上午10:17:58
 * @author zhangyh2
 * ScrollHeaderView.java
 * TODO
 */
package com.ytdinfo.keephealth.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * @author zhangyh2 ScrollHeaderView 上午10:17:58 TODO
 */
public class ScrollHeaderView extends ScrollView {

	private OnScrollChangedListener scrollViewListener = null;

	public ScrollHeaderView(Context context) {
		super(context);
	}

	public ScrollHeaderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ScrollHeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setOnScrollChangedListener(OnScrollChangedListener scrollViewListener) {
		this.scrollViewListener = scrollViewListener;
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		super.onScrollChanged(x, y, oldx, oldy);
		if (scrollViewListener != null) {
			scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
		}
	}

}
