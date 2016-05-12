package com.ytdinfo.keephealth.ui.report;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.adapter.ReportAdapter;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.model.ReportBean;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.MainActivity;
import com.ytdinfo.keephealth.ui.WebViewActivity;
import com.ytdinfo.keephealth.ui.OnlineVisits.OnlineQuesActivityForV3;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;
import com.ytdinfo.keephealth.ui.view.MyProgressDialog;
import com.ytdinfo.keephealth.utils.ACache;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.NetworkReachabilityUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;

public class ChooseReportActivity extends BaseActivity {
	private static final String TAG = ChooseReportActivity.class.getName();
	private CommonActivityTopView commonActivityTopView;
	private ImageButton back;
	private ListView listView;
	private List<ReportBean> list = new ArrayList<ReportBean>();
	private ReportAdapter reportAdapter;
	private Button bt_add, bt_search;
	private Button nodata;
	private RelativeLayout parent;
	private ACache aCache;
	private UserModel userModel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_report);
		userModel = new Gson().fromJson(
				SharedPrefsUtil.getValue(Constants.USERMODEL, null),
				UserModel.class);
		aCache = ACache.get(this);
		initView();
		initListener();
		commonActivityTopView.setTitle("选择体检报告");
		reportAdapter = new ReportAdapter(ChooseReportActivity.this, list);
		listView.setAdapter(reportAdapter);
		if (NetworkReachabilityUtil
				.isNetworkConnected(ChooseReportActivity.this)) {
			requestRrport();
		} else {
			// String s =
			// aCache.getAsString(userModel.getID()+Constants.CHOICE_REPORT_URl);
			// System.out.println(s);
			if (aCache.getAsString(userModel.getID()
					+ Constants.CHOICE_REPORT_URl) != null) {
				analyzeJson(aCache.getAsString(userModel.getID()
						+ Constants.CHOICE_REPORT_URl));
			} else {
				showWhich();
			}
		}
	}

	private void initView() {
		commonActivityTopView = (CommonActivityTopView) findViewById(R.id.id_CommonActivityTopView);
		back = (ImageButton) commonActivityTopView
				.findViewById(R.id.id_ibt_back);
		listView = (ListView) findViewById(R.id.cr_listView);
		bt_add = (Button) findViewById(R.id.id_bt_add);
		bt_search = (Button) findViewById(R.id.id_bt_search);
		nodata = (Button) findViewById(R.id.id_iv_nodata);
		parent = (RelativeLayout) findViewById(R.id.id_rl_parent);

	}

	private void initListener() {
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		bt_add.setOnClickListener(new OnClickListener() {
			// 添加体检报告照片
			@Override
			public void onClick(View v) {
				MobclickAgent.onEvent(ChooseReportActivity.this,
						Constants.UMENG_EVENT_8);

				if (null == SharedPrefsUtil.getValue(Constants.SUBJECTID, null)) {
					Intent intent = new Intent();
					intent.setClass(ChooseReportActivity.this,
							OnlineQuesActivityForV3.class);
					startActivity(intent);
					// new Intent(ChooseReportActivity.this,
					// AddPicturesActivity.class)
				} else {
					ToastUtil.showMessage("您当前正在进行在线咨询，结束后才能进行报告解读哦");
					SharedPrefsUtil.putValue(Constants.CHECKEDID_RADIOBT, 1);
					Intent intent = new Intent(ChooseReportActivity.this,
							MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("news", "news");
					startActivity(intent);
					finish();
				}

			}
		});
		bt_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent();
				i.setClass(ChooseReportActivity.this, WebViewActivity.class);
				i.putExtra("loadUrl", Constants.REPORTQUERY);
				startActivity(i);

			}
		});

	}

	private MyProgressDialog progressDialog;

	private void requestRrport() {
		/*
		 * RequestParams params = new RequestParams();
		 * params.addQueryStringParameter("parameter1", null);
		 * params.addQueryStringParameter("parameter2", null);
		 * params.addQueryStringParameter("type", null);
		 * params.addQueryStringParameter("userId", "5775311");
		 * params.addQueryStringParameter("isSearch", "false");
		 */

		HttpClient.options(Constants.CHOICE_REPORT_URl, null,
				new RequestCallBack<String>() {
					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
						progressDialog = new MyProgressDialog(
								ChooseReportActivity.this);
						progressDialog.setMessage("正在请求...");
						progressDialog.show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						progressDialog.dismiss();
						LogUtil.i(TAG, arg0.result);
						analyzeJson(arg0.result);
						aCache.put(userModel.getID()
								+ Constants.CHOICE_REPORT_URl, arg0.result);
					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						progressDialog.dismiss();
						LogUtils.d(arg1);
						ToastUtil.showMessage("网络请求失败...");

					}
				});

	}

	private void analyzeJson(String json) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(json);
			String data = jsonObject.getString("Data");
			Gson gson = new Gson();
			List<ReportBean> list2 = gson.fromJson(data,
					new TypeToken<List<ReportBean>>() {
					}.getType());
			for (int i = 0; i < list2.size(); i++) {
				ReportBean reportBean = list2.get(i);
				if (!reportBean.getStatus().equals("报告检验中")) {
					list.add(reportBean);
				}
			}
			// list.addAll(list2);
			reportAdapter.notifyDataSetChanged();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		showWhich();
	}

	private void showWhich() {
		if (list.isEmpty()) {
			nodata.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			parent.setBackgroundColor(Color.parseColor("#FFffffff"));
		} else {
			nodata.setVisibility(View.GONE);
			listView.setVisibility(View.VISIBLE);
			parent.setBackgroundColor(Color.parseColor("#FFEBEBEB"));
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		MobclickAgent.onPageStart("ChooseReportActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		MobclickAgent.onPageEnd("ChooseReportActivity");
		MobclickAgent.onPause(this);
	}
}
