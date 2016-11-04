package com.ytdinfo.keephealth.ui.personaldata;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.comm.core.beans.CommConfig;
import com.umeng.comm.core.beans.CommUser;
import com.umeng.comm.core.impl.CommunitySDKImpl;
import com.umeng.comm.core.listeners.Listeners.CommListener;
import com.umeng.comm.core.listeners.Listeners.SimpleFetchListener;
import com.umeng.comm.core.nets.Response;
import com.umeng.comm.core.nets.responses.PortraitUploadResponse;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.MainActivity;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;
import com.ytdinfo.keephealth.ui.view.CommonModifyView;
import com.ytdinfo.keephealth.ui.view.MyPopWindow;
import com.ytdinfo.keephealth.ui.view.MyProgressDialog;
import com.ytdinfo.keephealth.utils.ImageLoaderUtils;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;

@SuppressLint("NewApi")
public class PersonalDataActivity extends BaseActivity implements
		OnClickListener {
	private String TAG = "PersonalDataActivity";
	private Bitmap head;// 头像Bitmap

	private CommonActivityTopView commonActivityTopView;
	private PersonalDataItem_2View personalDataItem_2View;

	private CommonModifyView personalDataItem_1View_2;
	private CommonModifyView personalDataItem_1View_3;
	private CommonModifyView personalDataItem_1View_4;
	private CommonModifyView personalDataItem_1View_5;
	private CommonModifyView addtion;

	private AlertDialog alertDialog;
	private LinearLayout ll_parent;
	private PopupWindow popwindow;

	private MyPopWindow mypop;
	private PopupWindow pop;

	String[] sex_china = { "男", "女" };
	String[] sex_english = { "Man", "Woman" };
	int sex_position;

	private String IDcard;
	private String mobilePhone;

	// private static UserModel userModel;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_personal_data);
		SharedPrefsUtil.putValue(Constants.CHECKEDID_RADIOBT, 2);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		LogUtil.i("wpc", "onSaveInstanceState");
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
		LogUtil.i("wpc", "onRestoreInstanceState");
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("PersonalDataActivity");
		MobclickAgent.onResume(this);
		initView();
		initData();
		initListener();
		initPop();
	}

	private void initView() {
		commonActivityTopView = (CommonActivityTopView) findViewById(R.id.id_CommonActivityTopView);
		commonActivityTopView.setTitle("个人资料");
		personalDataItem_2View = (PersonalDataItem_2View) findViewById(R.id.id_PersonalDataItem_2View);
		personalDataItem_2View.tv_title.setText("头像");
		personalDataItem_1View_2 = (CommonModifyView) findViewById(R.id.id_PersonalDataItem_1View_2);
		personalDataItem_1View_2.tv_title.setText("姓名");
		// V21 调整用户昵称
		addtion = (CommonModifyView) findViewById(R.id.id_PersonalDataItem_V21_Addition1);
		addtion.tv_title.setText("昵称");
		personalDataItem_1View_3 = (CommonModifyView) findViewById(R.id.id_PersonalDataItem_1View_3);
		personalDataItem_1View_3.tv_title.setText("手机号码");
		personalDataItem_1View_4 = (CommonModifyView) findViewById(R.id.id_PersonalDataItem_1View_4);
		personalDataItem_1View_4.tv_title.setText("性别");
		personalDataItem_1View_5 = (CommonModifyView) findViewById(R.id.id_PersonalDataItem_1View_5);
		personalDataItem_1View_5.tv_title.setText("身份证号");
		ll_parent = (LinearLayout) findViewById(R.id.id_ll_parent);
	}

	public void initData() {
		String jsonUserModel = SharedPrefsUtil
				.getValue(Constants.USERMODEL, "");
		UserModel userModel = new Gson().fromJson(jsonUserModel,
				UserModel.class);
		LogUtil.i("paul", userModel.toString());

		LogUtil.i("paul", userModel.getHeadPicture());

		if (userModel.getUserSex().equals("Man")) {
			// 显示默认男头像
			imageLoader.displayImage(userModel.getHeadPicture(),
					personalDataItem_2View.iv_touxiang,
					ImageLoaderUtils.getOptions3());
		} else if (userModel.getUserSex().equals("Woman")) {
			// 显示默认女头像
			imageLoader.displayImage(userModel.getHeadPicture(),
					personalDataItem_2View.iv_touxiang,
					ImageLoaderUtils.getOptions());
		} else {
			// 显示默认头像
			imageLoader.displayImage(userModel.getHeadPicture(),
					personalDataItem_2View.iv_touxiang,
					ImageLoaderUtils.getOptions2());
		}
		personalDataItem_1View_2.tv_desc.setText(userModel.getUserName());
		addtion.tv_desc.setText(userModel.getAddition1());
		personalDataItem_1View_3.tv_desc.setText(transformMobile(userModel
				.getMobilephone()));
		mobilePhone = userModel.getMobilephone();
		if (userModel.getUserSex().equals("Man")) {
			personalDataItem_1View_4.tv_desc.setText("男");
		} else if (userModel.getUserSex().equals("Woman")) {
			personalDataItem_1View_4.tv_desc.setText("女");
		} else {
			personalDataItem_1View_4.tv_desc.setText("");
		}

		personalDataItem_1View_5.tv_desc.setText(transformIDcard(userModel
				.getIDcard()));
		IDcard = userModel.getIDcard();

	}

	private CharSequence transformIDcard(String iDcard) {
		if (iDcard.length() != 18) {
			return iDcard;
		}
		String str1 = iDcard.substring(0, 6);
		String str2 = "********";
		String str3 = iDcard.substring(14);
		return str1 + str2 + str3;
	}

	private String transformMobile(String mobilephone) {
		if (mobilephone.length() != 11) {
			return mobilephone;
		}
		String str1 = mobilephone.substring(0, 3);
		String str2 = "****";
		String str3 = mobilephone.substring(7);
		return str1 + str2 + str3;
	}

	public void initPop() {
		mypop = new MyPopWindow(this);
		pop = mypop.getPop();
	}

	private void initListener() {
		// 返回
		commonActivityTopView.ibt_back.setOnClickListener(this);
		// 头像
		personalDataItem_2View.setOnClickListener(this);
		// 姓名
		personalDataItem_1View_2.setOnClickListener(this);
		// 昵称
		addtion.setOnClickListener(this);
		// 手机号码不可修改
		personalDataItem_1View_3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ToastUtil.showMessage("手机号不能修改");
			}
		});
		// 性别
		personalDataItem_1View_4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSinChosDia();
			}
		});
		// 身份证号
		personalDataItem_1View_5.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ibt_back:
			// 返回
			Intent i = new Intent(PersonalDataActivity.this, MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtra("flag", "r2");
			startActivity(i);
			finish();
			break;
		case R.id.id_PersonalDataItem_2View:
			// 头像
			pop.showAtLocation(ll_parent, Gravity.BOTTOM, 0, 0);
			break;
		case R.id.id_PersonalDataItem_1View_2:
			// 姓名
			Intent i12 = new Intent();
			i12.setClass(PersonalDataActivity.this,
					CommonModifyInfoActivity.class);
			// 将“姓名”传过去
			i12.putExtra("title", personalDataItem_1View_2.tv_title.getText()
					.toString());
			i12.putExtra("desc", personalDataItem_1View_2.tv_desc.getText()
					.toString());
			startActivity(i12);

			break;

		case R.id.id_PersonalDataItem_V21_Addition1:
			// 昵称
			Intent addtions = new Intent();
			addtions.setClass(PersonalDataActivity.this,
					CommonModifyInfoActivity.class);
			// 将“姓名”传过去
			addtions.putExtra("title", addtion.tv_title.getText().toString());
			addtions.putExtra("desc", addtion.tv_desc.getText().toString());
			startActivity(addtions);

			break;
		case R.id.id_PersonalDataItem_1View_5:
			// 身份证号
			Intent i15 = new Intent();
			i15.setClass(PersonalDataActivity.this,
					CommonModifyInfoActivity.class);
			// 将“身份证号”传过去
			i15.putExtra("title", personalDataItem_1View_5.tv_title.getText()
					.toString());
			i15.putExtra("desc", IDcard);
			startActivity(i15);

			break;

		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent i = new Intent(PersonalDataActivity.this, MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			i.putExtra("flag", "r2");
			startActivity(i);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		LogUtil.i("wpc", "onDestroy");
		if (head != null) {
			head.recycle();
			head = null;
			// System.gc();
		}
	}

	/**
	 * 将图片转换成字符串（用Base64jar包）
	 * 
	 * @param imagepath
	 * @return
	 * @throws Exception
	 */
	public static String getImageStr(Bitmap bm) throws Exception {

		if (bm != null) {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bm.compress(CompressFormat.PNG, 100, bos);
			
			String uploadBuffer = new String(Base64.encodeBase64(bos
					.toByteArray()));
			bos.close();
			return uploadBuffer;
		}
		return "";
	}

	/**
	 * 单项选择对话框
	 */
	private void showSinChosDia() {
		int chosed = personalDataItem_1View_4.tv_desc.getText().toString()
				.equals("女") ? 1 : 0;
		AlertDialog.Builder sinChosDia = new AlertDialog.Builder(
				PersonalDataActivity.this);
		sinChosDia.setSingleChoiceItems(sex_china, chosed,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						alertDialog.dismiss();
						sex_position = which;
						// 上传到服务器
						requestModifyInfo();

					}
				});
		alertDialog = sinChosDia.create();
		alertDialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtil.i(TAG, "onActivityResult");
		if (requestCode == Constants.CROP_PICKED_WITH_DATA) {
			// 裁剪
			if (data != null) {
				Bundle extras = data.getExtras();
				head = extras.getParcelable("data");
				if (head != null) {
					/**
					 * 上传服务器代码
					 */
					
					//保存带
					modifyCommunityHeadPic(head);
				

				}
			}
		} else {
			// 拍照或相册
			String head_path = mypop.INonActivityResult(requestCode, data, 0);
			if (head_path == null) {
				return;
			}
			File temp = new File(head_path);
			cropPhoto(Uri.fromFile(temp));// 裁剪图片
		}
	}

	private MyProgressDialog progressDialog;

	/**
	 * 将修改后的信息上传服务器
	 */
	public void requestModifyInfo() {

		try {

			JSONObject jsonParam = new JSONObject();
			jsonParam.put("photo", getImageStr(head));
			jsonParam.put("fileName", Constants.HEAD_PICTURE);
			String userJson = SharedPrefsUtil.getValue(Constants.USERMODEL, "");
			UserModel userModel = new Gson()
					.fromJson(userJson, UserModel.class);
			jsonParam.put("sex", sex_english[sex_position]);
			jsonParam.put("Name", userModel.getUserName());
			jsonParam.put("UserAccount", userModel.getUserAccount());
			jsonParam.put("IDcard", userModel.getIDcard());

			Log.i(TAG, head + "==head==");
			HttpClient.post(Constants.MODIFYINFO_URl, jsonParam.toString(),
					new RequestCallBack<String>() {

						@Override
						public void onStart() {
							Log.i("HttpUtil", "onStart");

							progressDialog = new MyProgressDialog(
									PersonalDataActivity.this);
							progressDialog.setMessage("保存中...");
							progressDialog.show();
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

							progressDialog.dismiss();
							ToastUtil.showMessage("修改成功");

							String jsonUserModel = SharedPrefsUtil.getValue(
									Constants.USERMODEL, "");
							UserModel userModel = new Gson().fromJson(
									jsonUserModel, UserModel.class);

							try {
								JSONObject jsonObject = new JSONObject(
										responseInfo.result);
								if (jsonObject.has("photopath")) {
									userModel.setHeadPicture(jsonObject
											.getString("photopath"));
									

								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

							// 存入修改后的性别(英文)
							userModel.setUserSex(sex_english[sex_position]);

							SharedPrefsUtil.putValue(Constants.USERMODEL,
									userModel.toString());

							LogUtil.i(TAG, "存入成功");

							initData();

						}

						@Override
						public void onFailure(HttpException error, String msg) {
							Log.i("HttpUtil", "onFailure===" + msg);
							LogUtil.i("===============", error.toString());
							LogUtil.i("=================",
									Constants.MODIFYINFO_URl);

							ToastUtil.showMessage("网络获取失败");
							progressDialog.dismiss();
						}
					});

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private  void modifyCommunityHeadPic(final Bitmap pic)
	{
	 
		CommunitySDKImpl.getInstance().updateUserProtrait(pic,
				new SimpleFetchListener<PortraitUploadResponse>() {
			
			@Override
			public void onComplete(PortraitUploadResponse response) {
				// TODO Auto-generated method stub
			 if(response.errCode==Response.NO_ERROR)
			 {
				 CommUser usr=CommConfig.getConfig().loginedUser;
				 usr.iconUrl=response.mIconUrl;
				 usr.save();
				 requestModifyInfo();
			 }
				
			}
		});
	}
	
	/**
	 * 调用系统的裁剪
	 * 
	 * @param uri
	 */
	public void cropPhoto(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 75);
		intent.putExtra("outputY", 75);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, Constants.CROP_PICKED_WITH_DATA);
	}

	@Override
	public void onPause() {
		super.onPause();

		MobclickAgent.onPageEnd("PersonalDataActivity");
		MobclickAgent.onPause(this);
	}

}
