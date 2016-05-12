package com.ytdinfo.keephealth.zhangyuhui.view.ichnography;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.ytdinfo.keephealth.R;

/**
 * @author Administrator 绘图类。任何多边形。
 */
public class IADrawView extends TextView {

	public IADrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		init();
	}

	public IADrawView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		init();
	}

	public IADrawView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		this.context = context;
		init();
	}

	private ArrayList<Point> points;
	private Context context;
	private Paint paint;
//	private Paint room;
	private Path path;
//	private String text;
//	private float x;
//	private float y;

	private void init() {
		// TODO Auto-generated method stub
		paint = new Paint();
		/* 去锯齿 */
		paint.setAntiAlias(true);
		/* 设置paint的颜色 */
		paint.setColor(getResources().getColor(R.color.do_not_check));
		/* 设置paint的　style　为STROKE：空心 */
		paint.setStyle(Paint.Style.FILL);
		/* 设置paint的外框宽度 */
		paint.setStrokeWidth(3);
		path = new Path();
//		room = new Paint();
//		/* 去锯齿 */
//		room.setAntiAlias(true);
//		/* 设置paint的颜色 */
//		room.setColor(Color.BLACK);
//		/* 设置paint的　style　为STROKE：空心 */
//		room.setStyle(Paint.Style.FILL);
//		/* 设置paint的外框宽度 */
//		room.setStrokeWidth(3);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (points != null) {
			for (int i = 0; i < points.size(); i++) {
				if (i == 0) {
					path.moveTo(points.get(i).x, points.get(i).y);
				} else {
					path.lineTo(points.get(i).x, points.get(i).y);
				}
			}
			path.close();
		}
		canvas.drawPath(path, paint);
//		if (text != null) {
//			room.setTextSize(14);
//			room.setTextAlign(Paint.Align.CENTER);
//			canvas.drawText(text, x, y, room);
//		}

	}

	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}

	public ArrayList<Point> getPoints() {
		return points;
	}
	
	public void setName(String text){
		this.setText(text);
	};

	public void reDraw(int res, String text, int width, int height,boolean isTwinkle) {
		// TODO Auto-generated method stub
//		this.text = text;
		/* 去锯齿 */
		paint=new Paint();
		paint.setAntiAlias(true);
		/* 设置paint的颜色 */
		paint.setColor(res);
		/* 设置paint的　style　为STROKE：空心 */
		paint.setStyle(Paint.Style.FILL);
		/* 设置paint的外框宽度 */
		paint.setStrokeWidth(3);
		
		if (isTwinkle) {
			setAnimation(AnimationUtils.loadAnimation(context,
					R.anim.next_check));
		}else {
			clearAnimation();
		}
		
//		if (text != null) {
//			// 写文字
//			room.setTextSize(12);
//			FontMetrics fm = room.getFontMetrics();
//			// 文本的宽度
//			float textWidth = room.measureText(text);
//			float textCenterVerticalBaselineY = height / 2 + fm.descent
//					+ (fm.descent - fm.ascent) / 2;
//			x = (width + textWidth) / 2;
//			y = textCenterVerticalBaselineY;
//		}
		postInvalidate();
//		invalidate();
		Log.i("invalidate", "invalidate is run");
	}
}
