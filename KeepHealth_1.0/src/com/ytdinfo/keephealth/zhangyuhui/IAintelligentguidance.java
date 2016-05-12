package com.ytdinfo.keephealth.zhangyuhui;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.WebViewActivity;
import com.ytdinfo.keephealth.ui.report.ChooseReportActivity;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;
import com.ytdinfo.keephealth.ui.view.MyProgressDialog;
import com.ytdinfo.keephealth.utils.NetworkReachabilityUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;
import com.ytdinfo.keephealth.zhangyuhui.adapte.IALocalAdapter;
import com.ytdinfo.keephealth.zhangyuhui.adapte.IAOrganz;
import com.ytdinfo.keephealth.zhangyuhui.adapte.IAOrganzHttp;
import com.ytdinfo.keephealth.zhangyuhui.common.IAFindOrganization;
import com.ytdinfo.keephealth.zhangyuhui.common.IALiteral;
import com.ytdinfo.keephealth.zhangyuhui.common.LogFileHelper;
import com.ytdinfo.keephealth.zhangyuhui.common.PreferencesJsonCach;
import com.ytdinfo.keephealth.zhangyuhui.model.IABundleOrganiza;
import com.ytdinfo.keephealth.zhangyuhui.model.IAOrganizationHttp;
import com.ytdinfo.keephealth.zhangyuhui.model.IARoomNameHttp;
import com.ytdinfo.keephealth.zhangyuhui.model.OrgBase;
import com.ytdinfo.keephealth.zhangyuhui.poll.HttpTasker;
import com.ytdinfo.keephealth.zhangyuhui.poll.ThreadPoolManager;
import com.ytdinfo.keephealth.zhangyuhui.view.ichnography.IABabaibanDistrict;
import com.ytdinfo.keephealth.zhangyuhui.view.ichnography.IAJingAnDistrict;
import com.ytdinfo.keephealth.zhangyuhui.view.ichnography.IAPoisDataConfig;
import com.ytdinfo.keephealth.zhangyuhui.view.ichnography.IAXuhuiDistrict;

/**
 * @author Administrator 程序UI接入口。一个整体的功能模块，通过首页的按钮点击智能导检，调用到本Activity然后完成一系列功能。
 */
public class IAintelligentguidance extends BaseActivity {

	private String useID;

	// 线程池
	protected ThreadPoolManager manager;
	protected static final int THREADCOUNT = 5;

	private Spinner city;
	private TextView city_text;
	private Spinner local;
	private TextView name;
	// 将获取到的平面图进行展示。
	private TableRow ia_guide_table_row;
	private TextView ia_guide_first;
	private TextView ia_guide_secend;
	private RelativeLayout relative_fir;
	private RelativeLayout relative_sec;

	private IAOrganizationHttp organization;
	// 機構信息。
	private IAOrganz orginfo;
	private UserModel model;

	private Timer timer;// 计时器，进行间隔10S请求数据。
	private static final int KEPPER = 10000;

	/**
	 * 獲取頂部列表。
	 */
	private IAOrganzHttp citys;

	/**
	 * 测试URL链接。传送参数。肯定会失败（解析失败。）
	 */
	// private String url = /* "http://192.168.0.242:8080/IntelgentGuid/test"
	// */"http://121.43.229.49:88//APIQueuingSystem/GetData";
	// private String imagesd = /*
	// "http://192.168.0.242:8080/IntelgentGuid/image"
	// */"http://121.43.229.49:88//APIAccount/GetOrganizationInfo";
	// http://121.43.229.49:88
	// 获取本地保存的机构ID和明确的对应类进行绑定。（机构ID是已知状态。后台返回后才进行加载对应界面。）
	private ArrayList<IABundleOrganiza> tcList = new ArrayList<IABundleOrganiza>();

	private MyProgressDialog loading = null;

	private IARoomNameHttp roomOrgpari;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0x0001:
				// 先获取到的下拉列表信息。进行下拉列表绑定控件。
				bundleCityOrganz();
				// mLocationClient.start();// 启动定位模块。当然此页面关闭时。关闭定位模块。第一期不用定位。
				break;
			case 0x0002:
				String json = (String) msg.obj;
				PreferencesJsonCach.putValue(orginfo.id + "", json,
						IAintelligentguidance.this);
				LogFileHelper.getInstance().i("智能导检开始连接，数据返回" + json);
				try {
					roomOrgpari = new Gson().fromJson(json,
							IARoomNameHttp.class);
				} catch (Exception e) {
				}
				LogFileHelper.getInstance().i("智能导检开始连接，数据返回,进行解析");
				if (roomOrgpari != null && roomOrgpari.model != null) {
					getOrgAndPoint();
					getDataFHttp();
				} else {
					loading.dismiss();
					ToastUtil.showMessage("网络请求失败");
				}
				break;
			case 100:
				// 选取路线
				OrgBase sBase = (OrgBase) msg.obj;
//				if (sBase != null) {
					locOrChoseToFindOrganization(sBase);
//				} else {
//					ToastUtil.showMessage("网络请求失败");
//					if (loading != null) {
//						loading.dismiss();
//					}
//				}
				break;
			case 101:
				// 定位成功线路------------------------2
				setSpinnerByLocal((OrgBase) msg.obj);
				break;
			case 200:
				organization = (IAOrganizationHttp) msg.obj;
				LogFileHelper.getInstance().i("智能导检开始连接，实时数据返回");
				for (IABundleOrganiza cmodel : tcList) {
					for (int i = 0, len = organization.data.size(); i < len; i++) {
						switch (organization.data.get(i).organization_floor_num) {
						case 1:
							if (cmodel.id == organization.data.get(i).organization_id
									&& cmodel.floor == 1) {
								relative_fir.removeAllViews();
								if (organization.data.get(i).organization_hasstate) {
									// 给定一个参数。每个科室都有三种状态。加以标注。比如采用0未检查，1下一项，2已检查。即可。需要制定规则。传递参数。
									cmodel.layout.setChange(organization.data
											.get(i).organization_departments);
								}
								relative_fir.addView(cmodel.layout);
							}
							break;
						case 2:
							if (cmodel.id == organization.data.get(i).organization_id
									&& cmodel.floor == 2) {
								if (organization.data.get(i).organization_hasstate) {
									// 给定一个参数。每个科室都有三种状态。加以标注。比如采用0未检查，1下一项，2已检查。即可。需要制定规则。传递参数。
									cmodel.layout.setChange(organization.data
											.get(i).organization_departments);
								}

							}
							break;

						default:
							break;
						}

					}
				}
				if (loading != null) {
					loading.dismiss();
				}
				break;
			default:
				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		manager = ThreadPoolManager.getInstance(ThreadPoolManager.TYPE_FIFO,
				THREADCOUNT);
		manager.start();
		// 获取屏幕的宽高。这几个参数应该在程序第一个页面就获取到。
		if (IALiteral.width == 0 || IALiteral.height == 0) {
			DisplayMetrics dm = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(dm);
			IALiteral.width = dm.widthPixels;
			IALiteral.height = dm.heightPixels;
			IALiteral.density = dm.density;
		}
		setContentView(R.layout.activity_intelligentguidance);
		loading = new MyProgressDialog(this);
		loading.setMessage("加载中");
		loading.show();
		// UI操作
		findView();
		getCityLocal();// 无论如何先获取服务器下拉列表信息。
	}

	// 当前界面的UI操作
	private void findView() {
		// TODO Auto-generated method stub
		// ia_conmmontopview
		CommonActivityTopView activityTopView = (CommonActivityTopView) findViewById(R.id.ia_conmmontopview);
		activityTopView.setTitle("智能导检");
		ImageButton button = (ImageButton) activityTopView
				.findViewById(R.id.id_ibt_back);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		// （对应情况，在初期就应该有MAP集合来保存多个静态平面图与机构相互对应。否则无法正常提取））
		relative_fir = (RelativeLayout) findViewById(R.id.ia_guide_relative_first);

		relative_sec = (RelativeLayout) findViewById(R.id.ia_guide_relative_sec);
		relative_fir.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(IAintelligentguidance.this,
						IARecoverAcitvity.class);
				IALiteral.roomOrgpari = roomOrgpari;
				JSONObject object = new JSONObject();
				try {
					// 测试数据
					object.put("UserMobile", /* "15002174183" */
							model.getMobilephone());
					object.put("OrganizationID", /* "1" */orginfo.id);
				} catch (Exception e) {
					// TODO: handle exception
				}
				ArrayList<BasicNameValuePair> par = new ArrayList<BasicNameValuePair>();
				par.add(new BasicNameValuePair("param", object.toString()));
				IALiteral.par = par;

				IALiteral.orginfo = orginfo;
				startActivity(intent);
			}
		});
		city = (Spinner) findViewById(R.id.ia_guide_city);
		city_text = (TextView) findViewById(R.id.ia_guide_city_text);
		local = (Spinner) findViewById(R.id.ia_guide_local);
		name = (TextView) findViewById(R.id.ia_guide_name);
		ia_guide_table_row = (TableRow) findViewById(R.id.ia_guide_table_row);
		ia_guide_first = (TextView) findViewById(R.id.ia_guide_first);
		ia_guide_first.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				relative_fir.setVisibility(View.VISIBLE);
				relative_sec.setVisibility(View.GONE);
			}
		});
		ia_guide_secend = (TextView) findViewById(R.id.ia_guide_secend);
		ia_guide_secend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				relative_fir.setVisibility(View.GONE);
				relative_sec.setVisibility(View.VISIBLE);
			}
		});

		LinearLayout ia_intel_tijianyuyue = (LinearLayout) findViewById(R.id.ia_intel_tijianyuyue);
		ia_intel_tijianyuyue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent();
				i.setClass(IAintelligentguidance.this, WebViewActivity.class);
				i.putExtra("loadUrl", Constants.RESERVATION);
				startActivity(i);
				finish();
			}
		});
		LinearLayout ia_intel_baogaochaxun = (LinearLayout) findViewById(R.id.ia_intel_baogaochaxun);
		ia_intel_baogaochaxun.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent();
				i.setClass(IAintelligentguidance.this, WebViewActivity.class);
				i.putExtra("loadUrl", Constants.REPORTQUERY);
				startActivity(i);
				finish();
			}
		});
		LinearLayout ia_intel_baogaoxiangqing = (LinearLayout) findViewById(R.id.ia_intel_baogaoxiangqing);
		ia_intel_baogaoxiangqing.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(IAintelligentguidance.this,
						ChooseReportActivity.class);
				startActivity(intent);
				finish();
			}
		});

		// 获取用户手机号
		String use = SharedPrefsUtil.getValue(Constants.USERMODEL, null);
		model = new Gson().fromJson(use, UserModel.class);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("IAintelligentguidance");
		MobclickAgent.onResume(this);
	}

	/**
	 * 定位成功后根据获取的参数和已知集合citys进行匹配。当机构ID相同时，进行变更首选项。
	 * 定位成功线路------------------------2
	 */
	protected void setSpinnerByLocal(OrgBase base) {
		// TODO Auto-generated method stub
		// 定位后数据返回的位置信息。
		// if (organization == null) {
		// organization = IAFindOrganization.getOrganization(orginfo, base);
		// }
		lop: for (int i = 0; i < citys.data.size(); i++) {
			for (int j = 0; j < citys.data.get(i).organzs.size(); j++) {
				if (organization.data.get(0).organization_id == citys.data
						.get(i).organzs.get(j).id) {
					city.setSelection(i);
					local.setSelection(j);
					break lop;
				}
			}
		}
	}

	/**
	 * 获取下拉列表页面数据。(直接回到HANDler中进行操作。0x0001；)
	 */
	private void getCityLocal() {
		// TODO Auto-generated method stub
		// 假如次流程是写死状态。则进行获取绑定即可
		// 给服务器传递机构ID，返回机构信息。房间号和科室对应关系。
		citys = IAPoisDataConfig.getOrg();
		Message message = new Message();
		message.what = 0x0001;
		message.obj = citys;
		handler.sendMessage(message);
	}

	/**
	 * 绑定下拉列表页面。
	 */
	private void bundleCityOrganz() {
		// city.setAdapter(new IACityAdapter(citys.data,
		// R.layout.ia_guide_item_city, this));
		city_text.setText(citys.data.get(0).city);
		// city.setOnItemSelectedListener(new OnItemSelectedListener() {
		// @Override
		// public void onItemSelected(AdapterView<?> parent, View view,
		// int position, long id) {
		// // TODO Auto-generated method stub
		// IAOrganzCity organz = (IAOrganzCity) parent
		// .getItemAtPosition(position);
		local.setAdapter(new IALocalAdapter(citys.data.get(0).organzs,
				R.layout.ia_guide_item_city, IAintelligentguidance.this));
		local.setOnItemSelectedListener(new OnItemSelectedListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see android.widget.AdapterView.OnItemSelectedListener#
			 * onItemSelected(android.widget.AdapterView, android.view.View,
			 * int, long) 定位绑定。或用户选择完成后。进行数据请求。获取机构科室信息。
			 */
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				orginfo = (IAOrganz) parent.getItemAtPosition(position);
				LogFileHelper.getInstance().i("智能导检开始连接");
				if (!NetworkReachabilityUtil
						.isNetworkConnected(IAintelligentguidance.this)) {
					ToastUtil.showMessage("网络请求失败");
					LogFileHelper.getInstance().i("智能导检开始连接，网络链接失败，请检查网络");
					if (loading != null) {
						loading.dismiss();
					}
					name.setText(orginfo.name);
					String json = PreferencesJsonCach.getInfo(orginfo.id + "",
							IAintelligentguidance.this);
					if (json != null) {
						roomOrgpari = new Gson().fromJson(json,
								IARoomNameHttp.class);
					}
					getOrgAndPoint();
					// // 以ImageView为背景。进行全部适配，顶部的背景图片。覆盖到色彩之上。
					// LayoutParams lp = new LayoutParams(IALiteral.width,
					// IALiteral.width * IAPoisDataConfig.babaibanh
					// / IAPoisDataConfig.babaibanw);
					// ImageView bake = new
					// ImageView(IAintelligentguidance.this);
					// bake.setLayoutParams(lp);
					// bake.setBackgroundColor(getResources().getColor(
					// R.color.do_not_check));
					// IALiteral.bitmapwidth = IALiteral.width;
					// IALiteral.bitmapheight = IALiteral.width
					// * IAPoisDataConfig.babaibanh
					// / IAPoisDataConfig.babaibanw;
					// switch (orginfo.id) {
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
					// relative_fir.removeAllViews();
					// relative_fir.addView(bake);
				} else {
					LogFileHelper.getInstance().i("智能导检开始连接，网络正常，开始请求平面数据");
					if (loading != null) {
						loading.show();
					}
					// 请求服务器平面图数据。
					JSONObject object = new JSONObject();
					try {
						object.put("OrganizationID", orginfo.id);
					} catch (Exception e) {
						// TODO: handle exception
					}
					ArrayList<BasicNameValuePair> par = new ArrayList<BasicNameValuePair>();
					par.add(new BasicNameValuePair("param", object.toString()));
					manager.addAsyncTask(new HttpTasker(
							IAintelligentguidance.this, par,
							Constants.IAINTELORGINFO, new TypeToken<String>() {
							}, handler, true, 0x0002, true));
					// 请求服务器平面图数据。
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		// }
		//
		// @Override
		// public void onNothingSelected(AdapterView<?> parent) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
	}

	private void getOrgAndPoint() {
		IABundleOrganiza baseView = null;
		LogFileHelper.getInstance().i("智能导检开始绘制机构图");
		if (roomOrgpari == null) {
			return;
		}
		relative_fir.removeAllViews();

		if (orginfo.floor.length > 1) {
			ia_guide_table_row.setVisibility(View.VISIBLE);
		} else {
			ia_guide_table_row.setVisibility(View.GONE);
		}
		tcList.clear();
		for (int i = 0; i < orginfo.floor.length; i++) {
			name.setText(orginfo.name);
			switch (orginfo.id) {
			case 31:
				// 八百伴机构进行绑定。
				switch (orginfo.floor[i]) {
				case 1:
					// 获取对应机构的点阵
					IALiteral.roomName = roomOrgpari.model;
					IALiteral.maps = IAPoisDataConfig.getData(
							IAPoisDataConfig.babaiban, getResources()
									.getIntArray(R.array.babaiban_roomnub));
					// 进行机构绑定
					tcList.add(new IABundleOrganiza(31, 1,
							new IABabaibanDistrict(IAintelligentguidance.this,
									IAintelligentguidance.this,
									roomOrgpari.Organizationplan)));
					baseView = new IABundleOrganiza(31, 1,
							new IABabaibanDistrict(IAintelligentguidance.this,
									IAintelligentguidance.this,
									roomOrgpari.Organizationplan));
					break;
				case 2:
					// 获取对应机构的点阵
					IALiteral.roomName = roomOrgpari.model;
					IALiteral.maps = IAPoisDataConfig.getData(
							IAPoisDataConfig.babaiban, getResources()
									.getIntArray(R.array.babaiban_roomnub));
					// 进行机构绑定
					tcList.add(new IABundleOrganiza(31, 2,
							new IABabaibanDistrict(IAintelligentguidance.this,
									IAintelligentguidance.this,
									roomOrgpari.Organizationplan)));
					baseView = new IABundleOrganiza(31, 2,
							new IABabaibanDistrict(IAintelligentguidance.this,
									IAintelligentguidance.this,
									roomOrgpari.Organizationplan));
					break;
				case 3:

					break;
				default:
					break;
				}

				break;

			case 12:
				// 静安体检机构
				// 获取对应机构的点阵
				IALiteral.roomName = roomOrgpari.model;
				IALiteral.maps = IAPoisDataConfig.getData(
						IAPoisDataConfig.jingan,
						getResources().getIntArray(R.array.jingan_roomnub));
				// 进行机构绑定
				tcList.add(new IABundleOrganiza(12, 1, new IAJingAnDistrict(
						IAintelligentguidance.this, IAintelligentguidance.this,
						roomOrgpari.Organizationplan)));
				baseView = new IABundleOrganiza(12, 1, new IAJingAnDistrict(
						IAintelligentguidance.this, IAintelligentguidance.this,
						roomOrgpari.Organizationplan));
				break;
			case 24:
				// 获取对应机构的点阵
				switch (orginfo.floor[i]) {
				case 1:
					IALiteral.roomName = roomOrgpari.model;
					IALiteral.maps = IAPoisDataConfig.getData(
							IAPoisDataConfig.xuhui,
							getResources().getIntArray(R.array.xuhui_roomnub));
					// 进行机构绑定
					tcList.add(new IABundleOrganiza(24, 1, new IAXuhuiDistrict(
							IAintelligentguidance.this,
							IAintelligentguidance.this,
							roomOrgpari.Organizationplan)));
					baseView = new IABundleOrganiza(24, 1, new IAXuhuiDistrict(
							IAintelligentguidance.this,
							IAintelligentguidance.this,
							roomOrgpari.Organizationplan));
					break;
				case 2:
					IALiteral.roomName = roomOrgpari.model;
					IALiteral.maps = IAPoisDataConfig.getData(
							IAPoisDataConfig.xuhui,
							getResources().getIntArray(R.array.xuhui_roomnub));
					// 进行机构绑定
					tcList.add(new IABundleOrganiza(100003, 2,
							new IAXuhuiDistrict(IAintelligentguidance.this,
									IAintelligentguidance.this,
									roomOrgpari.Organizationplan)));
					baseView = new IABundleOrganiza(100003, 2,
							new IAXuhuiDistrict(IAintelligentguidance.this,
									IAintelligentguidance.this,
									roomOrgpari.Organizationplan));
					break;
				case 3:

				default:
					break;
				}
				break;
			default:
				break;
			}

		}
		relative_fir.addView(baseView.layout);
	}

	/**
	 * 通过定位或用户选择，获取对应的机构信息。 传递参数分为两种情况，一种未定位获取的位置信息。另一种为用户选取的位置信息。两种都需要传递用户信息。
	 * 返回数据后进行如下操作。第一通过机构ID得到，已经静态写入程序的机构平面图。 第二，通过获取到的机构信息。绑定平面图。
	 */
	private boolean isfinish = false;

	private void locOrChoseToFindOrganization(OrgBase base) {

		organization = null;
		organization = IAFindOrganization.getOrganization(orginfo, base,
				IALiteral.roomName);
		// 通过传递过来的机构ID取得平面图。
		Message msg = new Message();
		msg.what = 200;
		msg.obj = organization;
		handler.sendMessage(msg);
		if (timer == null && !isfinish) {
			// 启动请求。
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					// （第二--无穷）间隔10S进行一次数据请求。将请求的结果进行(发送organization---ID给服务器，还有用户信息给服务器)
					getDataFHttp();
				}
			}, KEPPER, KEPPER);
		}
	}

	private void getDataFHttp() {
		JSONObject object = new JSONObject();
		try {
			// 测试数据
			object.put("UserMobile", /* "15002174183" */model.getMobilephone());
			object.put("OrganizationID", /* "1" */orginfo.id);
		} catch (Exception e) {
			// TODO: handle exception
		}
		LogFileHelper.getInstance().i("智能导检开始连接，开始请求实时数据");
		ArrayList<BasicNameValuePair> par = new ArrayList<BasicNameValuePair>();
		par.add(new BasicNameValuePair("param", object.toString()));
		manager.addAsyncTask(new HttpTasker(IAintelligentguidance.this, par,
				Constants.IAINTELGETDATA, new TypeToken<OrgBase>() {
				}, handler, true, 100, false));

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		isfinish = true;
		if (timer != null) {
			timer.cancel();
		}
		super.finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		isfinish = true;
		if (timer != null) {
			timer.cancel();
		}
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();

		MobclickAgent.onPageEnd("IAintelligentguidance");
		MobclickAgent.onPause(this);
	}
}
