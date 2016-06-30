package com.ytdinfo.keephealth.ui.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lidroid.xutils.BitmapUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.MyApp;
import com.ytdinfo.keephealth.utils.LogUtil;

/**
 * 无限滚动广告栏组件
 */
@SuppressWarnings("deprecation")
public class MyAdGallery extends Gallery implements OnItemClickListener,
		OnItemSelectedListener, OnTouchListener {
	/** 显示的Activity */
	private Context mContext;
	/** 条目单击事件接口 */
	private MyOnItemClickListener mMyOnItemClickListener;
	/** 图片切换时间 */
	private int mSwitchTime;
	/** 自动滚动的定时器 */
	private Timer mTimer;
	/** 圆点容器 */
	private LinearLayout mOvalLayout;
	/** 当前选中的数组索引 */
	private int curIndex = 0;
	/** 上次选中的数组索引 */
	private int oldIndex = 0;
	/** 圆点选中时的背景ID */
	private int mFocusedId;
	/** 圆点正常时的背景ID */
	private int mNormalId;
	/** 图片资源ID组 */
	private int[] mAdsId;
	private List<String> mListImagePath;
	/** 图片网络路径数组 */
	private List<String> mUrllist = new ArrayList<String>();
	/** ImageView组 */
	List<ImageView> listImgs;

	public BitmapUtils bitmapUtils;

	public MyAdGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyAdGallery(Context context) {
		super(context);
	}

	public MyAdGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 *            显示的Activity ,不能为null
	 * @param mris
	 *            图片的网络路径数组 ,为空时 加载 adsId
	 * @param adsId
	 *            图片组资源ID ,测试用
	 * @param switchTime
	 *            图片切换时间 写0 为不自动切换
	 * @param ovalLayout
	 *            圆点容器 ,可为空
	 * @param focusedId
	 *            圆点选中时的背景ID,圆点容器可为空写0
	 * @param normalId
	 *            圆点正常时的背景ID,圆点容器为空写0
	 */
	public void start(Context context, List<String> list,
			List<String> listImagePath, int switchTime,
			LinearLayout ovalLayout, int focusedId, int normalId) {
		if (context == null) {
			return;
		}
		this.mContext = context;

		this.mUrllist = list;
		this.mListImagePath = listImagePath;
		this.mSwitchTime = switchTime;
		this.mOvalLayout = ovalLayout;
		this.mFocusedId = focusedId;
		this.mNormalId = normalId;
		ininImages();// 初始化图片组
		setAdapter(new AdAdapter());
		this.setOnItemClickListener(this);
		this.setOnTouchListener(this);
		this.setOnItemSelectedListener(this);
		this.setSoundEffectsEnabled(false);
		this.setAnimationDuration(700); // 动画时间
		this.setUnselectedAlpha(1); // 未选中项目的透明度
		// 不包含spacing会导致onKeyDown()失效!!! 失效onKeyDown()前先调用onScroll(null,1,0)可处理
		setSpacing(0);
		// 取靠近中间 图片数组的整倍数
		try {
			setSelection((getCount() / 2 / listImgs.size()) * listImgs.size()); // 默认选中中间位置为起始位置
		} catch (Exception e) {
			// TODO: handle exception
		}
		setFocusableInTouchMode(true);
		initOvalLayout();// 初始化圆点
		startTimer();// 开始自动滚动任务
	}

	/** 初始化图片组 */
	private void ininImages() {
		listImgs = new ArrayList<ImageView>(); // 图片组
		if (mListImagePath == null || mListImagePath.size() == 0) {
			// 当传递过来的Banner为空时，使用默认参数。
			mListImagePath = new ArrayList<String>();
			String path = Constants.IMAGES_DIR;
			String photoName = "banner_default";
			mListImagePath.add(path + photoName + ".png");
		}
		int len = mUrllist.size() != 0 ? mUrllist.size() : mListImagePath
				.size();
		for (int i = 0; i < len; i++) {
			ImageView imageview = new ImageView(MyApp.getInstance()); // 实例化ImageView的对象
			imageview.setScaleType(ImageView.ScaleType.CENTER_CROP); // 设置缩放方式
			imageview.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.MATCH_PARENT));
			if (mUrllist.size() == 0) {// 本地加载图片
				LogUtil.i("wpc",
						"mListImagePath.size()==" + mListImagePath.size());
				LogUtil.i("wpc",
						"mListImagePath.get(i)==" + mListImagePath.get(i));
				// imageview.setImageResource(mAdsId[i]); // 为ImageView设置要显示的图片
				ImageLoader.getInstance().displayImage(
						"file://" + mListImagePath.get(i), imageview);
			} else { // 网络加载图片
				LogUtil.i("wpc", "mUrllist.size()==" + mUrllist.size());
				LogUtil.i("wpc", "mUrllist.get(i)==" + mUrllist.get(i));
				// BitmapHelp.getBitmapUtils(mContext).display(imageview,
				// mUrllist.get(i).getImageURL());
				ImageLoader.getInstance().displayImage(mUrllist.get(i),
						imageview);
			}
			listImgs.add(imageview);
		}
	}

	/** 初始化圆点 */
	private void initOvalLayout() {
		/*
		 * if (mOvalLayout != null && listImgs.size() < 2) {// 如果只有一第图时不显示圆点容器
		 * mOvalLayout.getLayoutParams().height = 0; } else
		 */
		if (mOvalLayout != null) {
			mOvalLayout.removeAllViews();
			// 圆点的大小是 圆点窗口的 70%;
			int Ovalheight = (int) (mOvalLayout.getLayoutParams().height * 0.5);
			// 圆点的左右外边距是 圆点窗口的 20%;
			int Ovalmargin = (int) (mOvalLayout.getLayoutParams().height * 0.2);
			android.widget.LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					Ovalheight, Ovalheight);
			layoutParams.setMargins(Ovalmargin, 0, Ovalmargin, 0);
			for (int i = 0; i < listImgs.size(); i++) {
				View v = new View(mContext); // 员点
				v.setLayoutParams(layoutParams);
				v.setBackgroundResource(mNormalId);
				mOvalLayout.addView(v);
			}
			// 选中第一个
			mOvalLayout.getChildAt(0).setBackgroundResource(mFocusedId);
		}
	}

	/** 无限循环适配器 */
	class AdAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			if (listImgs.size() < 2)// 如果只有一张图时不滚动
				return listImgs.size();
			return Integer.MAX_VALUE;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return listImgs.get(position % listImgs.size()); // 返回ImageView
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int kEvent;
		if (isScrollingLeft(e1, e2)) { // 检查是否往左滑动
			kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
		} else { // 检查是否往右滑动
			kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
		}
		onKeyDown(kEvent, null);
		return true;

	}

	/** 检查是否往左滑动 */
	private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
		return e2.getX() > (e1.getX() + 50);
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return super.onScroll(e1, e2, distanceX, distanceY);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (MotionEvent.ACTION_UP == event.getAction()
				|| MotionEvent.ACTION_CANCEL == event.getAction()) {
			startTimer();// 开始自动滚动任务
		} else {
			stopTimer();// 停止自动滚动任务
		}
		return false;
	}

	/** 图片切换事件 */
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		curIndex = position % listImgs.size();
		if (mOvalLayout != null && listImgs.size() > 1) { // 切换圆点
			mOvalLayout.getChildAt(oldIndex).setBackgroundResource(mNormalId); // 圆点取消
			mOvalLayout.getChildAt(curIndex).setBackgroundResource(mFocusedId);// 圆点选中
			oldIndex = curIndex;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	/** 项目点击事件 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		try{
			LogUtil.i("wpc", "onItemClick---1");
			//修改内容 2016/01/21 21:23
			if (mMyOnItemClickListener != null&&listImgs.size()!=0) {
				LogUtil.i("wpc", "onItemClick---2");
				mMyOnItemClickListener.onItemClick(curIndex);
			}
		}catch(Exception e)
		{
			
		}
	}

	/** 设置项目点击事件监听器 */
	public void setMyOnItemClickListener(MyOnItemClickListener listener) {
		mMyOnItemClickListener = listener;
	}

	/** 项目点击事件监听器接口 */
	public interface MyOnItemClickListener {
		/**
		 * @param curIndex
		 *            //当前条目在数组中的下标
		 */
		void onItemClick(int curIndex);
	}

	/** 停止自动滚动任务 */
	public void stopTimer() {
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	/** 开始自动滚动任务 图片大于1张才滚动 */
	public void startTimer() {
		if (mTimer == null && listImgs.size() > 1 && mSwitchTime > 0) {
			mTimer = new Timer();
			mTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					handler.sendMessage(handler.obtainMessage(1));
				}
			}, mSwitchTime, mSwitchTime);
		}
	}

	/** 处理定时滚动任务 */
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			// 不包含spacing会导致onKeyDown()失效!!!
			// 失效onKeyDown()前先调用onScroll(null,1,0)可处理
			onScroll(null, null, 1, 0);
			onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);
		}
	};
}
