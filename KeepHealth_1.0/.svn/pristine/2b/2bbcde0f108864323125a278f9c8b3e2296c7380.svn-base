package com.ytdinfo.keephealth.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.jpush.android.api.JPushInterface;

import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.adapter.ImageAdapter;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;

public class GuideActivity extends BaseActivity implements OnPageChangeListener,
		OnClickListener {

	private ViewPager viewPager;
	private List<View> viewList;
	// private int
	// images[]={R.drawable.v1,R.drawable.v2,R.drawable.v3,R.drawable.v4};//导航图片资源
	private View view1, view2, view3;
	private ImageView points[];// 存放小圆圈数组

	private int currentIndex = 0;// 当前页面,默认首页

	private Button startButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPrefsUtil.putValue(Constants.CHECKISUPDATE, true);
		LogUtil.i("wpc2", "GuideActivity===true");
		
		String isFirstCome = SharedPrefsUtil.getValue(Constants.ISFIRSTCOME, "YES");
		if(isFirstCome.equals("YES")){
			//第一次使用
			setContentView(R.layout.activity_guide);
			initViewPager();// 初始化ViewPager对象
			initPoint();// 初始化导航小圆点
			
			//设置非第一次使用
			SharedPrefsUtil.putValue(Constants.ISFIRSTCOME, "NO");
			
		}else {
			//直接进入MainActivity
			intoMainActivity();
		}
		
		
	}
	
	
	
	
	@Override
	protected void onResume() {
		super.onResume();

		JPushInterface.onResume(this);
		
		MobclickAgent.onPageStart("GuideActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();

		JPushInterface.onPause(this);
		
		MobclickAgent.onPageEnd("GuideActivity");
		MobclickAgent.onPause(this);
	}

	private void initPoint() {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);
		points = new ImageView[3];
		for (int i = 0; i < points.length; i++) {
			points[i] = (ImageView) linearLayout.getChildAt(i);// 遍历LinearLayout下的所有ImageView子节点
			points[i].setEnabled(true);// 设置当前状态为允许（可点，灰色）
			// 设置点击监听
			points[i].setOnClickListener(this);

			// 额外设置一个标识符，以便点击小圆点时跳转对应页面
			points[i].setTag(i);// 标识符与圆点顺序一致
		}

		currentIndex = 0;
		points[currentIndex].setEnabled(false);// 设置首页为当前页(小点着色,蓝色)

	}

	private void initViewPager() {
		viewPager = (ViewPager) findViewById(R.id.viewpager);// 取得ViewPager实例
		viewList = new ArrayList<View>();// 实例化list集合

		/*
		 * 用代码的动态添加View //添加对应的view进入集合（数据源） for(int i=0;i<images.length;i++){
		 * ImageView imageView=new ImageView(MainActivity.this);
		 * imageView.setLayoutParams(new
		 * LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		 * imageView.setScaleType(ScaleType.FIT_XY);//设置缩放样式
		 * imageView.setImageResource(images[i]); viewList.add(imageView); }
		 */

		// 用xml静态添加view
		view1 = View.inflate(GuideActivity.this, R.layout.view1, null);
		view2 = View.inflate(GuideActivity.this, R.layout.view2, null);
		view3 = View.inflate(GuideActivity.this, R.layout.view3, null);
		viewList.add(view1);
		viewList.add(view2);
		viewList.add(view3);

		// 设置适配器
		ImageAdapter adapter = new ImageAdapter(viewList);

		// 绑定适配器
		viewPager.setAdapter(adapter);

		// 设置页卡切换监听
		viewPager.setOnPageChangeListener(this);

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int position) {// 当前页卡被选择时,position为当前页数

		if (position == 2) {// 由于进入微信这个按钮在第4个页面（view）才会出现，如果一开始就加载这个按钮监听，就导致空指针异常
			startButton = (Button) findViewById(R.id.startbutton);
			startButton.setOnClickListener(new OnClickListener() {// 匿名内部类，区分小圆圈的点击事件

						@Override
						public void onClick(View v) {
							// 跳到首页
							intoMainActivity();
						}
					});
		}
		points[position].setEnabled(false);// 不可点
		points[currentIndex].setEnabled(true);// 恢复之前页面状态
		currentIndex = position;

	}

	

	@Override
	public void onClick(View v) {
		// 利用刚设置的标识符跳转页面
		// Log.i("tuzi",v.getTag()+"");
		viewPager.setCurrentItem((Integer) v.getTag());

	}
	
	public void intoMainActivity() {
		Intent intent = new Intent(GuideActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		 startActivity(intent);
		 
		 finish();
		
	}

}