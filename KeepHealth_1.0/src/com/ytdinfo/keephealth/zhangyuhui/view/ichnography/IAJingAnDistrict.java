package com.ytdinfo.keephealth.zhangyuhui.view.ichnography;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap.Config;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.zhangyuhui.common.IALiteral;
import com.ytdinfo.keephealth.zhangyuhui.model.IAOrganization_Departments;
import com.ytdinfo.keephealth.zhangyuhui.model.IARoomPointModel;

/**
 * @author Administrator 八佰半體檢機構。
 */
public class IAJingAnDistrict extends IABaseFrame {

	public IAJingAnDistrict(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public IAJingAnDistrict(Context context, Activity activity, String url) {
		super(context);
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.url = url;
		init(context);
	}

	private String url;

	private Activity activity;
	// 眼科
	public OrgView yk1;
	// 眼科
	public OrgView yk2;
	// 视力
	public OrgView sl1;
	// 备用
	public OrgView by1;
	// 外科
	public OrgView wk1;
	// 外科
	public OrgView wk2;
	// 备用
	public OrgView by2;
	// 采血处
	public OrgView cxc1;
	// B超
	public OrgView bc1;
	// B超
	public OrgView bc2;
	// B超
	public OrgView bc3;
	// 内科
	public OrgView nk1;
	// 内科
	public OrgView nk2;
	// 心电图
	public OrgView xdt1;
	// 内科
	public OrgView nk3;
	// 内科
	public OrgView nk4;
	// 心电图
	public OrgView xdt2;
	// 心电图
	public OrgView xdt3;
	// 耳鼻喉
	public OrgView ebh1;
	// 耳鼻喉
	public OrgView ebh2;
	// 一般检查
	public OrgView ybjc1;
	// 外科
	public OrgView wk3;
	// B超
	public OrgView bc4;
	// 妇科
	public OrgView fk1;
	// 妇科
	public OrgView fk2;
	// 内科
	public OrgView nk5;

	// 口腔科
	public OrgView kqk1;
	// 中医科
	public OrgView zyk1;
	// 钼靶室
	public OrgView mbs1;
	// DR
	public OrgView dr1;
	// CT室
	public OrgView cts1;

	// 动脉硬化
	public OrgView dmyh1;
	// 备用
	public OrgView by3;
	// 骨密度
	public OrgView gmd1;
	// 多普勒
	public OrgView dpl1;
	// 肺
	public OrgView fgn1;
	public OrgView fgn2;
	public OrgView fgn3;
	public OrgView fgn4;
	private OrgView beiyong;

	private ImageView nextCheck;
	public LayoutParams next;

	private void init(final Context context) {
		// TODO Auto-generated method stub
		for (int i = 0, len = IALiteral.maps.size(); i < len; i++) {
			IARoomPointModel model = IALiteral.maps.get(i);
			switch (model.room) {
			case 221:
				OrgView r = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(r);
				break;
			case 268:
				beiyong = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(beiyong);
				break;
			case 201:// 眼科
				yk1 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(yk1);
				break;
			case 203:
				// 眼科
				yk2 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(yk2);
				break;
			case 205:
				// 视力
				sl1 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(sl1);
				break;

			case 206:// 备用
				by1 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(by1);
				break;
			case 207:
				// 外科
				wk1 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(wk1);
				break;
			case 208:
				// 外科
				wk2 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(wk2);
				break;
			case 209:// 备用
				by2 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(by2);
				break;
			case 210:
				// 采血处
				cxc1 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(cxc1);
				break;

			case 211:// B超
				bc1 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(bc1);
				break;
			case 212:
				// B超
				bc2 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(bc2);
				break;
			case 213:
				// B超
				bc3 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(bc3);
				break;

			case 215:// 内科
				nk1 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(nk1);
				break;
			case 216:
				// 内科
				nk2 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(nk2);
				break;
			case 217:
				// 心电图
				xdt1 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(xdt1);
				break;
			case 218:// 内科
				nk3 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(nk3);
				break;
			case 219:
				// 内科
				nk4 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(nk4);
				break;
			case 256:
				// 心电图
				xdt2 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(xdt2);
				break;
			case 257:
				// 心电图
				xdt3 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(xdt3);
				break;
			case 222:
				// 耳鼻喉
				ebh1 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(ebh1);
				break;
			case 223:
				// 耳鼻喉
				ebh2 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(ebh2);
				break;
			case 225:
				// 一般检查
				ybjc1 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(ybjc1);
				break;
			case 226:
				// 外科
				wk3 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(wk3);
				break;

			case 227:
				// B超
				bc4 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(bc4);
				break;
			case 228:
				// 妇科
				fk1 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(fk1);
				break;
			case 229:
				// 妇科
				fk2 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(fk2);
				break;
			case 230:
				// 内科
				nk5 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(nk5);
				break;

			case 258:
				// 口腔科
				kqk1 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(kqk1);
				break;
			case 260:
				// 中医科
				zyk1 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(zyk1);
				break;
			case 261:
				// 钼靶室
				mbs1 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(mbs1);
				break;
			case 262:
				// DR
				dr1 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(dr1);
				break;
			case 263:
				// CT室
				cts1 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(cts1);
				break;

			case 265:
				// 动脉硬化
				dmyh1 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(dmyh1);
				break;
			case 266:
				// 备用
				by3 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(by3);
				break;
			case 267:
				// 骨密度
				gmd1 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(gmd1);
				break;
			case 250:
				// 多普勒
				dpl1 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(dpl1);
				break;
			case 251:
				// 肺
				fgn1 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(fgn1);
				break;
			case 252:
				// 肺
				fgn2 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(fgn2);
				break;
			case 253:
				// 肺
				fgn3 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(fgn3);
				break;
			case 255:
				// 肺
				fgn4 = new OrgView(context, model.list, model.width,
						model.height, model.degree);
				addView(fgn4);
				break;
			default:
				break;
			}

		}

		// 以ImageView为背景。进行全部适配，顶部的背景图片。覆盖到色彩之上。
		LayoutParams lp = new LayoutParams(IALiteral.width, IALiteral.width
				* IAPoisDataConfig.babaibanh / IAPoisDataConfig.babaibanw);
		final ImageView bake = new ImageView(context);
		bake.setLayoutParams(lp);
		if (url != null) {
			DisplayImageOptions options = new DisplayImageOptions.Builder()
//					.showStubImage(R.drawable.jingan) // 设置图片下载期间显示的图片
//					.showImageForEmptyUri(R.drawable.jingan) // 设置图片Uri为空或是错误的时候显示的图片
//					.showImageOnFail(R.drawable.jingan) // 设置图片加载或解码过程中发生错误显示的图片
					.bitmapConfig(Config.ARGB_8888).cacheInMemory(true) // 设置下载的图片是否缓存在内存中
					.cacheOnDisc(true) // 设置下载的图片是否缓存在SD卡中
					.build(); // 创建配置过得DisplayImageOption对象 ;
			imageLoader.displayImage(url, bake, options);
		} else {
//			bake.setImageResource(R.drawable.jingan);
		}
		
		IALiteral.bitmapwidth = IALiteral.width;
		IALiteral.bitmapheight = IALiteral.width * IAPoisDataConfig.babaibanh
				/ IAPoisDataConfig.babaibanw;
		addView(bake);
		/*bake.setOnClickListener(new IABaseFrame.NoDoubleClickListener() {

			@Override
			public void onNoDoubleClick(View v) {
				// TODO Auto-generated method stub
				IAJingAnDistrict.this.setDrawingCacheEnabled(true);
				Bitmap bitmap = IAJingAnDistrict.this.getDrawingCache();
				ScreenShot.savePic(bitmap, new File(IALiteral.IA_SCREEN_TEMP));
				// ScreenShot.shoot(activity, new
				// File(IALiteral.IA_SCREEN_TEMP),
				// bake, IALiteral.width * IAPoisDataConfig.babaibanh
				// / IAPoisDataConfig.babaibanw);
				IAJingAnDistrict.this.destroyDrawingCache();
				Intent intent = new Intent(activity, IAShowActivitya.class);
				activity.startActivity(intent);
			}
		});*/

		nextCheck = new ImageView(context);
		next = new LayoutParams(96, 44);
		nextCheck.setLayoutParams(next);
		nextCheck.setImageResource(R.drawable.next_check);
		nextCheck.setVisibility(View.INVISIBLE);
		addView(nextCheck);
	}

	private boolean hasNext;
	@Override
	public void setChange(
			ArrayList<IAOrganization_Departments> organization_departments) {
		// TODO Auto-generated method stub
		// 根据传进来的参数。改变对应设置。
		for (IAOrganization_Departments entry : organization_departments) {
			if (entry.getDepartments_state() == 1) {
				hasNext = true;
				break;
			}else {
				hasNext = false;
			}
		}
		for (IAOrganization_Departments entry : organization_departments) {
			setChangeByid(entry);
		}
	};

	private void setChangeByid(IAOrganization_Departments entry) {
		switch (entry.departments_roomnumber) {
		case 201:// 眼科
			setColorByid(yk1, entry);
			break;
		case 203:
			// 眼科
			setColorByid(yk2, entry);
			break;
		case 205:
			// 视力
			setColorByid(sl1, entry);
			break;

		case 206:// 备用
			setColorByid(by1, entry);
			break;
		case 207:
			// 外科
			setColorByid(wk1, entry);
			break;
		case 208:
			// 外科
			setColorByid(wk2, entry);
			break;
		case 209:// 备用
			setColorByid(by2, entry);
			break;
		case 210:
			// 采血处
			setColorByid(cxc1, entry);
			break;

		case 211:// B超
			setColorByid(bc1, entry);
			break;
		case 212:
			// B超
			setColorByid(bc2, entry);
			break;
		case 213:
			// B超
			setColorByid(bc3, entry);
			break;

		case 215:// 内科
			setColorByid(nk1, entry);
			break;
		case 216:
			// 内科
			setColorByid(nk2, entry);
			break;
		case 217:
			// 心电图
			setColorByid(xdt1, entry);
			break;
		case 218:// 内科
			setColorByid(nk3, entry);
			break;
		case 219:
			// 内科
			setColorByid(nk4, entry);
			break;
		case 256:
			// 心电图
			setColorByid(xdt2, entry);
			break;
		case 257:
			// 心电图
			setColorByid(xdt3, entry);
			break;
		case 222:
			// 耳鼻喉
			setColorByid(ebh1, entry);
			break;
		case 223:
			// 耳鼻喉
			setColorByid(ebh2, entry);
			break;
		case 225:
			// 一般检查
			setColorByid(ybjc1, entry);
			break;
		case 226:
			// 外科
			setColorByid(wk3, entry);
			break;

		case 227:
			// B超
			setColorByid(bc4, entry);
			break;
		case 228:
			// 妇科
			setColorByid(fk1, entry);
			break;
		case 229:
			// 妇科
			setColorByid(fk2, entry);
			break;
		case 230:
			// 内科
			setColorByid(nk5, entry);
			break;

		case 258:
			// 口腔科
			setColorByid(kqk1, entry);
			break;
		case 260:
			// 中医科
			setColorByid(zyk1, entry);
			break;
		case 261:
			// 钼靶室
			setColorByid(mbs1, entry);
			break;
		case 262:
			// DR
			setColorByid(dr1, entry);
			break;
		case 263:
			// CT室
			setColorByid(cts1, entry);
			break;

		case 265:
			// 动脉硬化
			setColorByid(dmyh1, entry);
			break;
		case 266:
			// 备用
			setColorByid(by3, entry);
			break;
		case 267:
			// 骨密度
			setColorByid(gmd1, entry);
			break;
		case 250:
			// 多普勒
			setColorByid(dpl1, entry);
			break;
		case 251:
			// 肺
			setColorByid(fgn1, entry);
			break;
		case 252:
			// 肺
			setColorByid(fgn2, entry);
			break;
		case 253:
			// 肺
			setColorByid(fgn3, entry);
			break;
		case 255:
			// 肺
			setColorByid(fgn4, entry);
			break;

		case 268:
			setColorByid(beiyong, entry);
			break;
		default:
			break;
		}
	}

	
	private void setColorByid(OrgView tv, IAOrganization_Departments dept) {
		switch (dept.departments_state) {
		case 0:
			// 未体检
			tv.getDraw().reDraw(
					getResources().getColor(R.color.has_not_checked), null, 0,
					0, false);
			// tv.getName().setTextSize(6);
			// tv.setText("未体检的科室");
			break;
		case 1:
			// 下一项

			int X = 0;
			int Y = 0;
			int lenth = tv.getPoint().size();
			for (int i = 0; i < lenth; i++) {
				// 无论多少个点。现在获取所有的X轴和与Y轴和。
				Point p = tv.getPoint().get(i);
				X += p.x;
				Y += p.y;
			}
			next.leftMargin = X / lenth - 48;
			next.topMargin = Y / lenth - 44;
			nextCheck.setVisibility(View.VISIBLE);
			nextCheck.setLayoutParams(next);

			tv.getDraw().reDraw(getResources().getColor(R.color.next_check),
					null, 0, 0, true);
			// tv.getName().setTextSize(6);
			// tv.setText("下一项体检的科室");
			break;
		case 2:
			// 已经体检
			tv.getDraw().reDraw(
					getResources().getColor(R.color.has_been_checked), null, 0,
					0, false);
			// tv.getName().setTextSize(6);
			// tv.setText("已体检的科室");
			if (!hasNext) {
				nextCheck.setVisibility(View.GONE);
			}
			break;
		case 3:
			// 不用体检
			tv.getDraw().reDraw(getResources().getColor(R.color.do_not_check),
					null, 0, 0, false);
			// tv.getName().setTextSize(6);
			// tv.setText("不用体检的科室");
			break;
		case 4:
			// 已经体检但同科的其他房间。
			tv.getDraw().reDraw(
					getResources().getColor(R.color.checked_other_room), null,
					0, 0, false);
			// tv.getName().setTextSize(6);
			// tv.setText("其他体检的科室");
			break;
		default:
			break;
		}
	}

}
