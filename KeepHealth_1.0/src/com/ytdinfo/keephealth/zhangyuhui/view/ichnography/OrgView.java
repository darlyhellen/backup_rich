package com.ytdinfo.keephealth.zhangyuhui.view.ichnography;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class OrgView extends FrameLayout {

	public OrgView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public OrgView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private IADrawView draw;
//	private TextView name;
	private ArrayList<Point> point;
	private int width;
	private int height;
	private float degree;

	public OrgView(Context context, ArrayList<Point> point, int width,
			int height, float degree) {
		super(context);
		this.point = point;
		this.width = width;
		this.height = height;
		this.degree = degree;
		init(context);
	}

	private void init(Context context) {
		// TODO Auto-generated method stub
		draw = new IADrawView(context);
		draw.setPoints(point);
		addView(draw);
		/*name = new TextView(context);
		if (point == null || point.size() == 0) {
			return;
		}
		LayoutParams lsp = new LayoutParams(width, height);
		if (point.get(1).y != point.get(0).y) {
			if (point.get(1).y > point.get(0).y) {
				// 右下偏移
				if (point.size() == 4) {
					lsp.setMargins(point.get(0).x - point.get(3).x,
							point.get(3).y - point.get(0).y, 0, 0);
				} else {
					lsp.setMargins(point.get(0).x, point.get(0).y, 0, 0);
				}
			} else {
				// 右上偏移
				if (point.size() == 4) {
					lsp.setMargins(point.get(1).x - point.get(0).x,
							point.get(0).y - point.get(1).y, 0, 0);
				} else {
					lsp.setMargins(point.get(0).x, point.get(0).y, 0, 0);
				}
			}
		} else {
			lsp.setMargins(point.get(0).x, point.get(0).y, 0, 0);
		}

		name.setLayoutParams(lsp);
		name.setGravity(Gravity.CENTER);
		name.setTextColor(Color.WHITE);
		// 旋转
		RotateAnimation animation = new RotateAnimation(0f, degree,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation.setFillAfter(true);
		name.setAnimation(animation);
		addView(name);*/
	}

	public IADrawView getDraw() {
		return draw;
	}

//	public TextView getName() {
//		return name;
//	}
//
//	public void setText(String title) {
//		if (name != null) {
//			name.setText(title);
//		}
//	}

	public ArrayList<Point> getPoint() {
		return point;
	}

}
