package com.ytdinfo.keephealth.ui.opinionfeedback;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.adapter.PhotoGridViewAdapter;
import com.ytdinfo.keephealth.adapter.PhotoGridViewAdapter.Callback;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.view.MyPopWindow;
import com.ytdinfo.keephealth.ui.view.MyProgressDialog;
import com.ytdinfo.keephealth.utils.ImageTools;
import com.ytdinfo.keephealth.utils.JsonUtil;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;

public class OpinionFeedbackActivity extends BaseActivity implements Callback {
	private String TAG = "OpinionFeedbackActivity";
	private EditText et_content;

	private GridView gridView;
	private List<String> listData = new ArrayList<String>();
	private PhotoGridViewAdapter photoGridViewAdapter;
	private List<String> list_imagesUrl = new ArrayList<String>();

	private MyProgressDialog myProgressDialog;
	private Bitmap feedback;// 意见反馈Bitmap
	private String image_path;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0x111) {
				// 为空，保存按钮不可点击
				bt_save.setClickable(false);
				bt_save.setBackgroundResource(R.drawable.circle_l_white);
				bt_save.setTextColor(Color.parseColor("#77FFFFFF"));
			} else if (msg.what == 0x222) {
				// 不为空，保存按钮可点击
				bt_save.setClickable(true);
				bt_save.setBackgroundResource(R.drawable.circle_white_selector);
				bt_save.setTextColor(Color.parseColor("#FFFFFFFF"));
			} else if (msg.what == 0x123) {
				if (feedback != null) {
					
					requestImages();
				}
			}
		};
	};

	public Button bt_save;
	public EditText et;
	public ImageButton ibt_back, ibt_add;

	private MyPopWindow mypop;
	private PopupWindow pop;
	private LinearLayout ll_parent;

	private TimerTask timerTask;
	private Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opinion_feedback);
		initView();
		initListener();
		listenETisempty();
		initPop();
	}

	public void initPop() {
		mypop = new MyPopWindow(this);
		pop = mypop.getPop();
	}

	/**
	 * 监听edittext的内容是否为空， 为空：保存按钮不可点击 不为空：保存按钮可点击
	 */
	public void listenETisempty() {
		timerTask = new TimerTask() {

			@Override
			public void run() {
				if (et.getText().toString().equals("")
						|| et.getText().toString() == null) {
					// 为空，不可点击
					Message msg = Message.obtain();
					msg.what = 0x111;
					handler.sendMessage(msg);
				} else {
					// 非空，可点击
					Message msg = Message.obtain();
					msg.what = 0x222;
					handler.sendMessage(msg);
				}
			}
		};
		timer = new Timer();
		timer.schedule(timerTask, 0, 100);
	}

	private void initView() {

		bt_save = (Button) findViewById(R.id.id_bt_save);
		et = (EditText) findViewById(R.id.id_et);
		ibt_back = (ImageButton) findViewById(R.id.id_ibt_back);

		ll_parent = (LinearLayout) findViewById(R.id.id_ll_parent);

		et_content = (EditText) findViewById(R.id.id_et_content);

		LogUtil.i("paul", et_content + "---1");

		gridView = (GridView) findViewById(R.id.aor_gridview);
//		gridView.setSelection(gridView.getBottom());
		listData.add("file:///android_asset/add.png");
		photoGridViewAdapter = new PhotoGridViewAdapter(this, listData, this,"");

		gridView.setAdapter(photoGridViewAdapter);

	}

	private void initListener() {
		ibt_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		bt_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				LogUtil.i(TAG, "提交");
				// 上传到服务器
				requestOpinionFeedback();
			}

		});
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (position == (listData.size() - 1)) {
					// 点击最后一个，添加照片
					pop.showAtLocation(ll_parent, Gravity.BOTTOM, 0, 0);
					
//					Intent  intent = new Intent(OpinionFeedbackActivity.this, MyPopWindow.class);
//					startActivityForResult(intent, 0x123);
					
					// 隐藏输入法
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(
									OpinionFeedbackActivity.this
											.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);

				}
			}

		});
	}

	public void requestOpinionFeedback() {

		try {

			// 向服务器发送请求
			JSONObject jsonParam = new JSONObject();
			jsonParam.put("AttachPics", new JSONArray(list_imagesUrl));
			jsonParam.put("FeedbackBody", et.getText().toString());

			HttpClient.post(Constants.OPINION_FEEDBACK_URl,
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
							ToastUtil.showMessage("提交成功");
							finish();

						}

						@Override
						public void onFailure(HttpException error, String msg) {
							Log.i("HttpUtil", "onFailure===" + msg);
							LogUtil.i("===============", error.toString());
							ToastUtil.showMessage("提交失败");
						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void requestImages() {
		try {

			// 向服务器发送请求
			JSONArray jsonArray = JsonUtil.bitmapTOjsonArray(feedback);

			HttpClient.post(Constants.OPINION_FEEDBACK_IMAGES_URl,
					jsonArray.toString(), new RequestCallBack<String>() {

						@Override
						public void onStart() {
							Log.i("HttpUtil", "onStart");
							// myProgressDialog = new MyProgressDialog(
							// OpinionFeedbackActivity.this);
							// myProgressDialog.setMessage("上传照片....");
							// myProgressDialog.show();
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

							} catch (JSONException e) {
								e.printStackTrace();
							}

							myProgressDialog.dismiss();

							updateAdapter();

						}

						@Override
						public void onFailure(HttpException error, String msg) {
							Log.i("HttpUtil", "onFailure===" + msg);
							LogUtil.i("===============", error.toString());

							myProgressDialog.dismiss();
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
		if (feedback != null) {
			listData.add(listData.size() - 1, image_path);

//			gridView.setAdapter(new PhotoGridViewAdapter(this, listData, this));
			 photoGridViewAdapter.notifyDataSetChanged();
			ImageTools.recycleBitmap(feedback);

		}
	}

	// private JSONArray getJsonArray(List<Bitmap> list) {
	// JSONArray jsonArray = new JSONArray();
	//
	// LogUtil.i(TAG, list.size() + "");
	//
	// for (int i = 0; i < list.size(); i++) {
	// try {
	// // JSONObject jsonObject = new
	// // JSONObject(getImageStr(list.get(i)));
	//
	// String imgStr = ImageTools.getImageStr(list.get(i));
	// LogUtil.i(TAG, imgStr);
	//
	// jsonArray.put(imgStr);
	//
	// LogUtil.i(TAG, jsonArray.toString());
	//
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// return jsonArray;
	// }
	private JSONArray getJsonArray(Bitmap feedback) {
		JSONArray jsonArray = new JSONArray();

		try {
			String imgStr = ImageTools.getImageStr(feedback);
			jsonArray.put(imgStr);

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonArray;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
//		if(resultCode==1001){
//			Bundle bundle = data.getExtras();
//			image_path = bundle.getString("imageUrl");
//		}

		LogUtil.i(TAG, "onActivityResult");

		// Bitmap result_bitmap = mypop.INonActivityResult(requestCode, data);
		// crop_upload(result_bitmap);

		image_path = mypop.INonActivityResult(requestCode, data, 0);
		if(image_path==null){
			return;
		}

		myProgressDialog = new MyProgressDialog(OpinionFeedbackActivity.this);
		myProgressDialog.setMessage("上传照片....");
		myProgressDialog.show();

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

		feedback = ImageTools.cropBitmap(image_path);

		LogUtil.i("paul", feedback.getByteCount() / 1024 + "K");

	}

	// private void crop_upload(Bitmap bitmap) {
	// Bitmap cropedBitmap = ImageTools.cropBitmap(bitmap);
	// // 由于Bitmap内存占用较大，这里需要回收内存，否则会报out of memory异常
	// if (bitmap != null && !bitmap.isRecycled()) {
	//
	// bitmap.recycle();
	// bitmap = null;
	// System.gc();
	// }
	//
	// feedback = cropedBitmap;
	//
	// if (feedback != null) {
	// /**
	// * 上传服务器代码
	// */
	// // listData.add(feedback);
	//
	// // 上传一张图片
	// requestImages(feedback);
	// }
	// }

	/**
	 * 接口方法，响应ListView按钮点击事件
	 */
	@Override
	public void click(View v, int position) {
		listData.remove(position);
//		gridView.setAdapter(new PhotoGridViewAdapter(this, listData, this));
		photoGridViewAdapter.notifyDataSetChanged();
		// 将list_imagesUrl中第position位置的图片URl删除
		list_imagesUrl.remove(position);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		MobclickAgent.onPageStart("OpinionFeedbackActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		
		MobclickAgent.onPageEnd("OpinionFeedbackActivity");
		MobclickAgent.onPause(this);
	}

}
