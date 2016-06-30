/**
 * 2015年9月18日
 * IARecoverAcitvity.java
 * com.ytdinfo.keephealth.zhangyuhui
 * @auther Darly Fronch
 * 下午2:12:43
 * IARecoverAcitvity
 * TODO
 */
package com.ytdinfo.keephealth.zhangyuhui;

import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RelativeLayout;

import com.google.gson.reflect.TypeToken;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.view.MyProgressDialog;
import com.ytdinfo.keephealth.utils.NetworkReachabilityUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;
import com.ytdinfo.keephealth.zhangyuhui.common.IAFindOrganization;
import com.ytdinfo.keephealth.zhangyuhui.common.IALiteral;
import com.ytdinfo.keephealth.zhangyuhui.model.IAOrganizationHttp;
import com.ytdinfo.keephealth.zhangyuhui.model.OrgBase;
import com.ytdinfo.keephealth.zhangyuhui.poll.HttpTasker;
import com.ytdinfo.keephealth.zhangyuhui.poll.ThreadPoolManager;
import com.ytdinfo.keephealth.zhangyuhui.view.ichnography.IABabaibanDistrict;
import com.ytdinfo.keephealth.zhangyuhui.view.ichnography.IAJingAnDistrict;
import com.ytdinfo.keephealth.zhangyuhui.view.ichnography.IAPoisDataConfig;
import com.ytdinfo.keephealth.zhangyuhui.view.ichnography.IAXuhuiDistrict;

/**
 * 2015年9月18日 IARecoverAcitvity.java com.ytdinfo.keephealth.zhangyuhui
 * 
 * @auther Darly Fronch 下午2:12:43 IARecoverAcitvity TODO
 */
public class IARecoverAcitvity extends BaseActivity {
	private RelativeLayout relative;

	private TextView title;

	// 线程池
	protected ThreadPoolManager manager;
	protected static final int THREADCOUNT = 5;

	private Timer timer;

	private final int KEPPER = 10000;

	private IAOrganizationHttp organization;

	private MyProgressDialog loading = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
		setContentView(R.layout.ia_show_activity);
		relative = (RelativeLayout) findViewById(R.id.ia_show_relative);
		relative.removeAllViews();
		getView();
		if (!NetworkReachabilityUtil.isNetworkConnected(IARecoverAcitvity.this)) {
			ToastUtil.showMessage("网络请求失败");
			if (loading != null) {
				loading.dismiss();
			}
			// relative.setBackgroundColor(getResources().getColor(
			// R.color.do_not_check));
			// // 以ImageView为背景。进行全部适配，顶部的背景图片。覆盖到色彩之上。
			// LayoutParams lp = new LayoutParams(IALiteral.height,
			// IALiteral.width * IAPoisDataConfig.babaibanw
			// / IAPoisDataConfig.babaibanh);
			// ImageView bake = new ImageView(IARecoverAcitvity.this);
			// bake.setLayoutParams(lp);
			// bake.setBackgroundColor(getResources().getColor(
			// R.color.do_not_check));
			// bake.setScaleType(ScaleType.FIT_CENTER);
			// IALiteral.bitmapwidth = IALiteral.width;
			// IALiteral.bitmapheight = IALiteral.width
			// * IAPoisDataConfig.babaibanh / IAPoisDataConfig.babaibanw;
			// switch (IALiteral.orginfo.id) {
			// case 31:
			// bake.setImageResource(R.drawable.babaiban);
			// break;
			// case 12:
			// bake.setImageResource(R.drawable.jingan);
			// break;
			// case 24:
			// bake.setImageResource(R.drawable.xuhui);
			// break;
			// default:
			// break;
			// }
			relative.addView(IALiteral.layout);
		} else {

			relative.addView(IALiteral.layout);

			manager = ThreadPoolManager.getInstance(
					ThreadPoolManager.TYPE_FIFO, THREADCOUNT);
			manager.start();
			loading = new MyProgressDialog(this);
			loading.setMessage("加载中");
			loading.show();
			if (timer == null) {
				// 启动请求。
				timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						// （第二--无穷）间隔10S进行一次数据请求。将请求的结果进行(发送organization---ID给服务器，还有用户信息给服务器)
						getDataFHttp();
					}
				}, 0, KEPPER);
			}
		}
		setViewFullScreen();

		title = (TextView) findViewById(R.id.ia_show_title);
		title.setText(IALiteral.orginfo.name);
		ImageView consel = (ImageView) findViewById(R.id.ia_show_image_consel);
		consel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IARecoverAcitvity.this.finish();
			}
		});

	}

	/**
	 * @auther Darly Fronch 2015 上午9:22:51 TODO起始进来后放大到全屏状态。
	 */
	private void setViewFullScreen() {
		ObjectAnimator aStart = null;
		ObjectAnimator aStop = null;
		// 横屏：width<height

		double screen = (double) IALiteral.height / (double) IALiteral.width;
		// 图片 if(width>height)
		double image = (double) IAPoisDataConfig.babaibanw
				/ (double) IAPoisDataConfig.babaibanh;
		AnimatorSet animSet = new AnimatorSet();// 定义一个AnimatorSet对象
		if (screen > image) {
			// 按照手机高度放大
			double a = (double) IALiteral.bitmapheight
					/ (double) IALiteral.width;
			if (a < 1) {
				a = 1 / a;
			}
			aStart = ObjectAnimator.ofFloat(IALiteral.layout, "scaleX", 1f,
					(float) (a));
			aStop = ObjectAnimator.ofFloat(IALiteral.layout, "scaleY", 1f,
					(float) (a));
		} else if (screen < image) {
			// 按照手机宽度放大
			double a = (double) IALiteral.bitmapwidth
					/ (double) IALiteral.height;
			if (a < 1) {
				a = 1 / a;
			}
			aStart = ObjectAnimator.ofFloat(IALiteral.layout, "scaleX", 1f,
					(float) (a));
			aStop = ObjectAnimator.ofFloat(IALiteral.layout, "scaleY", 1f,
					(float) (a));
		} else {
			// 等比放大
			double a = (double) IALiteral.bitmapwidth
					/ (double) IALiteral.width;
			if (a < 1) {
				a = 1 / a;
			}
			aStart = ObjectAnimator.ofFloat(IALiteral.layout, "scaleX", 1f,
					(float) (a));
			aStop = ObjectAnimator.ofFloat(IALiteral.layout, "scaleY", 1f,
					(float) (a));
		}

		animSet.play(aStart).with(aStop);
		animSet.setDuration(0);
		animSet.start();
	};

	private void getView() {
		switch (IALiteral.orginfo.id) {
		case 31:
			// 八百伴机构进行绑定。
			IALiteral.layout = new IABabaibanDistrict(this, this,
					IALiteral.roomOrgpari.Organizationplan);
			break;

		case 12:
			// 静安体检机构
			IALiteral.layout = new IAJingAnDistrict(this, this,
					IALiteral.roomOrgpari.Organizationplan);
			break;
		case 24:
			// 获取对应机构的点阵
			IALiteral.layout = new IAXuhuiDistrict(this, this,
					IALiteral.roomOrgpari.Organizationplan);
			break;
		default:
			break;
		}
	}

	/**
	 * @auther Darly Fronch 2015 下午2:40:35 TODO
	 */
	protected void getDataFHttp() {
		// TODO Auto-generated method stub
		if (!NetworkReachabilityUtil.isNetworkConnected(IARecoverAcitvity.this)) {
			ToastUtil.showMessage("网络请求失败");
			if (loading != null) {
				loading.dismiss();
			}
		} else {
			manager.addAsyncTask(new HttpTasker(this, IALiteral.par,
					Constants.IAINTELGETDATA, new TypeToken<OrgBase>() {
					}, handler, true, 100, false));
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 100) {
				organization = null;
				organization = IAFindOrganization.getOrganization(
						IALiteral.orginfo, (OrgBase) msg.obj,
						IALiteral.roomName);
				for (int i = 0, len = organization.data.size(); i < len; i++) {
					((RelativeLayout) IALiteral.layout.getParent())
							.removeAllViews();
					relative.removeAllViews();
					if (organization.data.get(i).organization_hasstate) {
						// 给定一个参数。每个科室都有三种状态。加以标注。比如采用0未检查，1下一项，2已检查。即可。需要制定规则。传递参数。
						IALiteral.layout
								.setChange(organization.data.get(i).organization_departments);
					}
					relative.addView(IALiteral.layout);
				}
			}
			if (loading != null) {
				loading.dismiss();
			}
		}
	};

	private final class TouchListener implements OnTouchListener {

		/** 记录是拖拉照片模式还是放大缩小照片模式 */
		private int mode = 0;// 初始状态
		/** 拖拉照片模式 */
		private static final int MODE_DRAG = 1;
		/** 放大缩小照片模式 */
		private static final int MODE_ZOOM = 2;

		/** 用于记录开始时候的坐标位置 */
		private PointF startPoint = new PointF();
		/** 用于记录拖拉图片移动的坐标位置 */
		private Matrix matrix = new Matrix();
		/** 用于记录图片要进行拖拉时候的坐标位置 */
		private Matrix currentMatrix = new Matrix();

		/** 两个手指的开始距离 */
		private float startDis;
		/** 两个手指的中间点 */
		private PointF midPoint;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			/** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			// 手指压下屏幕
			case MotionEvent.ACTION_DOWN:
				mode = MODE_DRAG;
				// 记录ImageView当前的移动位置
				// currentMatrix.set(IALiteral.layout.getImageMatrix());
				startPoint.set(event.getX(), event.getY());
				break;
			// 手指在屏幕上移动，改事件会被不断触发
			case MotionEvent.ACTION_MOVE:
				// 拖拉图片
				// imageView.setScaleType(ScaleType.MATRIX);
				if (mode == MODE_DRAG) {
					float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
					float dy = event.getY() - startPoint.y; // 得到x轴的移动距离
					// 在没有移动之前的位置上进行移动
					matrix.set(currentMatrix);
					matrix.postTranslate(dx, dy);
				}
				// 放大缩小图片
				else if (mode == MODE_ZOOM) {
					float endDis = distance(event);// 结束距离
					if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
						float scale = endDis / startDis;// 得到缩放倍数
						matrix.set(currentMatrix);
						matrix.postScale(scale, scale, midPoint.x, midPoint.y);
					}
				}
				break;
			// 手指离开屏幕
			case MotionEvent.ACTION_UP:
				// 当触点离开屏幕，但是屏幕上还有触点(手指)
			case MotionEvent.ACTION_POINTER_UP:
				mode = 0;
				break;
			// 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
			case MotionEvent.ACTION_POINTER_DOWN:
				mode = MODE_ZOOM;
				/** 计算两个手指间的距离 */
				startDis = distance(event);
				/** 计算两个手指间的中间点 */
				if (startDis > 10f) { // 两个手指并拢在一起的时候像素大于10
					midPoint = mid(event);
					// 记录当前ImageView的缩放倍数
					// currentMatrix.set(imageView.getImageMatrix());
				}
				break;
			}
			// imageView.setImageMatrix(matrix);
			return true;
		}

		/** 计算两个手指间的距离 */
		private float distance(MotionEvent event) {
			float dx = event.getX(1) - event.getX(0);
			float dy = event.getY(1) - event.getY(0);
			/** 使用勾股定理返回两点之间的距离 */
			return FloatMath.sqrt(dx * dx + dy * dy);
		}

		/** 计算两个手指间的中间点 */
		private PointF mid(MotionEvent event) {
			float midX = (event.getX(1) + event.getX(0)) / 2;
			float midY = (event.getY(1) + event.getY(0)) / 2;
			return new PointF(midX, midY);
		}

	}

	@Override
	public void onResume() {
		super.onResume();

		MobclickAgent.onPageStart("IARecoverAcitvity");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		MobclickAgent.onPageEnd("IARecoverAcitvity");
		MobclickAgent.onPause(this);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		if (timer != null) {
			timer.cancel();
		}
		super.finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if (timer != null) {
			timer.cancel();
		}
		super.onDestroy();
	}
}