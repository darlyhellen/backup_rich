package com.ytdinfo.keephealth.ui.report;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.rayelink.eckit.SDKCoreHelper;
import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.adapter.PhotoGridViewAdapter;
import com.ytdinfo.keephealth.adapter.PhotoGridViewAdapter.Callback;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.app.MyApp;
import com.ytdinfo.keephealth.model.DocInfoBean;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.MainActivity;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;
import com.ytdinfo.keephealth.ui.view.MyPopWindow;
import com.ytdinfo.keephealth.ui.view.MyProgressDialog;
import com.ytdinfo.keephealth.utils.Chat_Dialog;
import com.ytdinfo.keephealth.utils.DBUtilsHelper;
import com.ytdinfo.keephealth.utils.ImageTools;
import com.ytdinfo.keephealth.utils.JsonUtil;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.NetworkReachabilityUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;
import com.yuntongxun.ecsdk.ECDevice.ECConnectState;
import com.yuntongxun.kitsdk.ECDeviceKit;
import com.yuntongxun.kitsdk.beans.ChatInfoBean;

public class AddPicturesActivity extends BaseActivity implements Callback {
	private String TAG = "AddPicturesActivity";
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x123) {
				if (croped_bitmap != null) {
					if (!NetworkReachabilityUtil.isNetworkConnected(MyApp
							.getInstance())) {
						ToastUtil.showMessage("网络未连接...");
						myProgressDialog2.dismiss();
					} else {
						requestImagesUrl();
					}

				}
			}
		};
	};

	private CommonActivityTopView commonActivityTopView;
	private GridView gridView;
	private List<String> listData = new ArrayList<String>();
	private PhotoGridViewAdapter photoGridViewAdapter;

	private MyPopWindow mypop;
	private PopupWindow pop;
	private LinearLayout ll_parent;

	private List<String> list_imagesUrl = new ArrayList<String>();
	private List<String> list_imagesPath = new ArrayList<String>();

	private Bitmap croped_bitmap;
	private String image_path;
	DbUtils db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_pictures);
		db = DBUtilsHelper.getInstance().getDb();
		initView();
		initListener();
		initPop();
	}

	private void initView() {
		commonActivityTopView = (CommonActivityTopView) findViewById(R.id.id_CommonActivityTopView);
		commonActivityTopView.tv_title.setText("选择体检报告");
		commonActivityTopView.bt_save.setText("问医生");
		commonActivityTopView.bt_save
				.setBackgroundResource(R.drawable.circle_white_selector);

		ll_parent = (LinearLayout) findViewById(R.id.id_ll_parent);

		gridView = (GridView) findViewById(R.id.aor_gridview);
		listData.add("file:///android_asset/add.png");

		photoGridViewAdapter = new PhotoGridViewAdapter(this, listData, this,
				"请上传体检报告");
		gridView.setAdapter(photoGridViewAdapter);

	}

	private void initListener() {
		commonActivityTopView.ibt_back
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();

					}
				});
		commonActivityTopView.bt_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (list_imagesUrl.size() > 0) {
					if (!DBUtilsHelper.getInstance().isOnline()) {
						requestAddReportPic();
					} else {
						ToastUtil.showMessage("您当前正在进行在线咨询，结束后才能进行报告解读哦");
						SharedPrefsUtil
								.putValue(Constants.CHECKEDID_RADIOBT, 1);
						Intent intent = new Intent(AddPicturesActivity.this,
								MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("news", "news");
						startActivity(intent);
						finish();
					}

				} else {
					ToastUtil.showMessage("请先添加体检照片");
				}

			}
		});
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position == (listData.size() - 1)) {
					// 点击最后一个，添加照片
					pop.showAtLocation(ll_parent, Gravity.BOTTOM, 0, 0);

					// Intent intent = new Intent(AddPicturesActivity.this,
					// MyPopWindow.class);
					// startActivityForResult(intent, 0x123);
				}

			}
		});
	}

	private void requestAddReportPic() {

		try {
			String userStr = SharedPrefsUtil.getValue(Constants.USERMODEL, "");
			UserModel userModel = new Gson().fromJson(userStr, UserModel.class);

			// 向服务器发送请求
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("SubjectType", 2);
			jsonParam.put("UserID", userModel.getID());
			jsonParam.put("UserSex", userModel.getUserSex());
			jsonParam.put("UserName", userModel.getUserName());
			jsonParam.put("Age", userModel.getAge());
			jsonParam.put("HeadPicture", userModel.getHeadPicture());
			jsonParam.put("RelationShip", "");
			jsonParam.put("StudyID", "1000");
			jsonParam.put("AttachPics",
					new JSONArray(list_imagesUrl).toString());
			// jsonParam.put("BodyContent", "");

			HttpClient.post(Constants.ONLINE_QUESTION_SUBMIT_URl,
					jsonParam.toString(), new RequestCallBack<String>() {

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
							parseJson(responseInfo.result.toString());

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

	private MyProgressDialog myProgressDialog;
	ChatInfoBean chatInfoBean;

	private void parseJson(String jsonStr) {
//		if (!Chat_Dialog.timeCurl()) {
//			final AlertDialog dialog = new AlertDialog.Builder(this).create();
//			dialog.show();
//			dialog.setCanceledOnTouchOutside(false);
//			Window window = dialog.getWindow();
//			window.setContentView(R.layout.chat_dialog);// 设置对话框的布局
//			TextView msg = (TextView) window.findViewById(R.id.chat_dialog_msg);
//			String desString = "亲，非常抱歉，我们的服务时间是工作日9：00－18：00，欢迎下次来咨询，祝您身体健康！";
//			msg.setText(desString);
//			Button sure = (Button) window.findViewById(R.id.chat_dialog_sure);
//			sure.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					dialog.dismiss();
//				}
//			});
//			return;
//		}

		try {
			JSONObject jsonObject = new JSONObject(jsonStr);
			JSONObject jsonData = jsonObject.getJSONObject("Data");
			final int subjectId = jsonData.getInt("SubjectID");
			// SharedPrefsUtil.putValue(Constants.SUBJECTID, subjectId + "");
			String docInfoBeanStr = jsonData.getString("responser");
			if (null == docInfoBeanStr || docInfoBeanStr.equals("")
					|| docInfoBeanStr.equals("null")) {
				final AlertDialog dialog = new AlertDialog.Builder(this)
						.create();
				dialog.show();
				dialog.setCanceledOnTouchOutside(false);
				Window window = dialog.getWindow();
				window.setContentView(R.layout.chat_dialog);// 设置对话框的布局
				TextView msg = (TextView) window
						.findViewById(R.id.chat_dialog_msg);
				String desString = null;
				if (!Chat_Dialog.timeCurl()) {
					desString = "亲，非常抱歉，我们的服务时间是工作日9：00－18：00，欢迎下次来咨询，祝您身体健康！";
				}else {
					 desString = "亲，我们的医生都在忙碌，请稍等~";
				}
				msg.setText(desString);
				Button sure = (Button) window
						.findViewById(R.id.chat_dialog_sure);
				sure.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				return;
			}
			// 开始计时
			// TimerService.count = 0;
			final DocInfoBean docInfoBean = new Gson().fromJson(docInfoBeanStr,
					DocInfoBean.class);
			saveDoc(docInfoBean);
			try {
				chatInfoBean = db.findFirst(Selector.from(ChatInfoBean.class)
						.where("docInfoBeanId", "=",
								docInfoBean.getVoipAccount()));
				if (null == chatInfoBean) {
					chatInfoBean = new ChatInfoBean();
					chatInfoBean.setSubjectID(subjectId + "");
					chatInfoBean.setDocInfoBeanId(docInfoBean.getVoipAccount());
					chatInfoBean.setComment(false);
				} else {
					chatInfoBean.setSubjectID(subjectId + "");
					chatInfoBean.setComment(false);
				}
				chatInfoBean.setTimeout(false);
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (SDKCoreHelper.getConnectState() != ECConnectState.CONNECT_SUCCESS) {
				myProgressDialog = new MyProgressDialog(
						AddPicturesActivity.this);
				myProgressDialog.setMessage("正在连接对话....");
				myProgressDialog.show();
				MyApp.ConnectYunTongXun();
			}
			goIntent(chatInfoBean);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void goIntent(ChatInfoBean chatInfoBean) {
		// TODO Auto-generated method stub
		chatInfoBean.setSubjectType("2");
		chatInfoBean.setStatus(false);
		DBUtilsHelper.getInstance().saveChatinfo(chatInfoBean);
		ECDeviceKit.getIMKitManager().startConversationActivity(chatInfoBean,
				list_imagesPath, null);
	}

	private void saveDoc(DocInfoBean docInfoBean) {
		// TODO Auto-generated method stub
		// DbUtils dbUtils = new DbUtils(null);

		try {
			db.createTableIfNotExist(DocInfoBean.class);
			db.save(docInfoBean);
			// List<DocInfoBean> docInfoBeans = db.findAll(DocInfoBean.class);
			// DocInfoBean docInfoBean2 =
			// db.findFirst(Selector.from(DocInfoBean.class).where("voipAccount","=","89077100000003"));
			// DocInfoBean docInfoBean2 = db.findById(DocInfoBean.class,
			// docInfoBean.getVoipAccount());
			// System.out.println("doc..........."+docInfoBean2.toString());
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void initPop() {
		mypop = new MyPopWindow(this);
		pop = mypop.getPop();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// if(resultCode==1001){
		// Bundle bundle = data.getExtras();
		// image_path = bundle.getString("imageUrl");
		// }
		LogUtil.i(TAG, "onActivityResult");

		image_path = mypop.INonActivityResult(requestCode, data, 0);
		if (image_path == null) {
			return;
		}

		myProgressDialog2 = new MyProgressDialog(AddPicturesActivity.this);
		myProgressDialog2.setMessage("上传照片....");
		myProgressDialog2.show();

		new Thread(new Runnable() {

			@Override
			public void run() {
				cropImage(image_path);
				Message msg = Message.obtain();
				msg.what = 0x123;
				handler.sendMessage(msg);
			}
		}).start();

	}

	@SuppressLint("NewApi")
	private void cropImage(String image_path) {

		croped_bitmap = ImageTools.cropBitmap(image_path);

	}

	// private void crop_upload(Bitmap bitmap) {
	// Bitmap cropedBitmap = ImageTools.cropBitmap(bitmap);
	// // 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
	// if (bitmap != null && !bitmap.isRecycled()) {
	//
	// bitmap.recycle();
	// bitmap = null;
	// }
	// System.gc();
	//
	// if (cropedBitmap != null) {
	//
	// requestImagesUrl(cropedBitmap);
	//
	// }
	// }

	private MyProgressDialog myProgressDialog2;

	/**
	 * 上传服务器
	 */
	public void requestImagesUrl() {

		try {
			// 向服务器发送请求
			JSONArray jsonArray = JsonUtil.bitmapTOjsonArray(croped_bitmap);
			LogUtil.i(TAG, jsonArray.toString());

			HttpClient.post(Constants.REPORT_IMAGES_URl, jsonArray.toString(),
					new RequestCallBack<String>() {

						@Override
						public void onStart() {
							Log.i("HttpUtil", "onStart");
							// myProgressDialog2 = new
							// MyProgressDialog(AddPicturesActivity.this);
							// myProgressDialog2.setMessage("上传照片....");
							// myProgressDialog2.show();
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

							try {
								JSONObject jsonObject = new JSONObject(
										responseInfo.result.toString());
								JSONArray jsonArray = jsonObject
										.getJSONArray("path");
								for (int i = 0; i < jsonArray.length(); i++) {
									list_imagesUrl.add(jsonArray.get(i)
											.toString());
								}
								list_imagesPath.add(image_path);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							try {
								myProgressDialog2.dismiss();
							} catch (Exception e) {
								// TODO: handle exception
							}

							updateAdapter();

						}

						@Override
						public void onFailure(HttpException error, String msg) {
							Log.i("HttpUtil", "onFailure===" + msg);
							LogUtil.i("===============", error.toString());

							myProgressDialog2.dismiss();
							ToastUtil.showMessage("照片上传失败");
						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 将newbitmap加入list中，并刷新adapter
	 * 
	 * @param newBitmap
	 */
	public void updateAdapter() {
		if (croped_bitmap != null) {
			listData.add(listData.size() - 1, image_path);
			// gridView.setAdapter(new PhotoGridViewAdapter(this, listData,
			// this));
			photoGridViewAdapter.notifyDataSetChanged();
			ImageTools.recycleBitmap(croped_bitmap);
		}
	}

	/**
	 * 接口方法，响应ListView按钮点击事件
	 */
	@Override
	public void click(View v, int position) {
		listData.remove(position);
		// gridView.setAdapter(new PhotoGridViewAdapter(this, listData, this));
		photoGridViewAdapter.notifyDataSetChanged();
		// 将list_imagesUrl中第position位置的图片URl删除
		list_imagesUrl.remove(position);
		list_imagesPath.remove(position);
	}

	@Override
	public void onResume() {
		super.onResume();

		MobclickAgent.onPageStart("AddPicturesActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		MobclickAgent.onPageEnd("AddPicturesActivity");
		MobclickAgent.onPause(this);
	}
}
