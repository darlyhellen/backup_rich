package com.ytdinfo.keephealth.ui.report;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.utils.DBUtilsHelper;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;
import com.yuntongxun.kitsdk.beans.ChatInfoBean;

public class ChatCommentActivity extends BaseActivity {
	Handler handler = new Handler(){
		@Override
		public void handleMessage(android.os.Message msg) {
			tv_textLength.setText(et.length()+"/100");
		};
	};
	private RelativeLayout parent;
	private RadioGroup radioGroup;
	private RadioButton red;
	private RadioButton yelllow;
	private RadioButton gray;
	private EditText et;
	private Button bt_ok;
	private int star = 3;
	private String subjectID;
	DbUtils dbUtils;
	private ChatInfoBean chatInfoBean;
	private String subjectType;
	private TextView tv_textLength;
	
	private Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_comment);
		dbUtils = DBUtilsHelper.getInstance().getDb();
		Intent i = getIntent();
		if (null != i) {
			subjectID = i.getExtras().getString("subjectId");
			try {
				chatInfoBean = dbUtils.findFirst(Selector.from(
						ChatInfoBean.class).where("SubjectID", "=", subjectID));

				if (null != chatInfoBean) {
					subjectType = chatInfoBean.getSubjectType();
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		initView();
		initListener();
		listenET();

	}

	private void listenET() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				
				handler.sendEmptyMessage(0x123);
			}
		}, 0, 100);
	}

	private void initView() {
		tv_textLength = (TextView) findViewById(R.id.tv_textLength);
		parent = (RelativeLayout) findViewById(R.id.id_parent);
		radioGroup = (RadioGroup) findViewById(R.id.id_radioGroup);
		red = (RadioButton) findViewById(R.id.id_red);
		yelllow = (RadioButton) findViewById(R.id.id_yellow);
		gray = (RadioButton) findViewById(R.id.id_gray);
		et = (EditText) findViewById(R.id.id_edittext);
		bt_ok = (Button) findViewById(R.id.id_ok);
	}

	private void initListener() {
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.id_red:
					setRBColor(red, yelllow, gray);
					star = 3;
					break;
				case R.id.id_yellow:
					setRBColor(yelllow, gray, red);
					star = 2;
					break;
				case R.id.id_gray:
					setRBColor(gray, red, yelllow);
					star = 1;
					break;

				default:
					break;
				}
			}
		});
		bt_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				requestEvaluation();
			}
		});
	}

	public void setRBColor(RadioButton white, RadioButton gray1,
			RadioButton gray2) {
		white.setTextColor(Color.parseColor("#FFffffff"));
		gray1.setTextColor(Color.parseColor("#FF666666"));
		gray2.setTextColor(Color.parseColor("#FF666666"));
	}

	private void requestEvaluation() {

		try {
			// 向服务器发送请求
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("SubjectID", subjectID);
			jsonParam.put("Star", star);
			jsonParam.put("Comment", et.getText().toString());

			HttpClient.post(Constants.EVALUATION_URL, jsonParam.toString(),
					new RequestCallBack<String>() {

						@Override
						public void onStart() {
							Log.i("HttpUtil", "onStart");
						}

						@Override
						public void onLoading(long total, long current,
								boolean isUploading) {
							Log.i("HttpUtil", "onLoading");
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							Log.i("HttpUtil", "onSuccess");
							Log.i("HttpUtil", "onSuccess==="
									+ responseInfo.result.toString());
							if (!et.getText().toString().equals("")) {
								if ("3".equals(subjectType)) {
									// 在线问诊-评价
									MobclickAgent.onEvent(
											ChatCommentActivity.this,
											Constants.UMENG_EVENT_12);
								} else {
									// 报告解读-评价
									MobclickAgent.onEvent(
											ChatCommentActivity.this,
											Constants.UMENG_EVENT_9);
								}
							}

							try {
								List<ChatInfoBean> list = dbUtils
										.findAll(Selector.from(
												ChatInfoBean.class).where(
												"SubjectID", "=", subjectID));//"docInfoBeanId", "=", doctorID));

								for (int i = 0; i < list.size(); i++) {
									ChatInfoBean chatInfoBean = list.get(i);
									chatInfoBean.setComment(true);
									dbUtils.saveOrUpdate(chatInfoBean);
								}

							} catch (DbException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							ToastUtil.showMessage("评价成功");
							finish();
							/*
							 * Intent i = new Intent();
							 * i.setClass(ChatCommentActivity.this,
							 * MainActivity.class);
							 * i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							 * startActivity(i);
							 */

						}

						@Override
						public void onFailure(HttpException error, String msg) {
							Log.i("HttpUtil", "onFailure===" + msg);
							LogUtil.i("===============", error.toString());
							ToastUtil.showMessage("网络获取失败");
						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onResume() {
		super.onResume();

		MobclickAgent.onPageStart("ChatCommentActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		MobclickAgent.onPageEnd("ChatCommentActivity");
		MobclickAgent.onPause(this);
	}
}
