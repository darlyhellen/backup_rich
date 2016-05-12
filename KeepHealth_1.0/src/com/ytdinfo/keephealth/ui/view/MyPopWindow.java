package com.ytdinfo.keephealth.ui.view;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.utils.ImageUtil;
import com.ytdinfo.keephealth.utils.LogUtil;

public class MyPopWindow {
	private String TAG = "MyPopWindow";
	private Context mContext;
	private PopupWindow pop;
	public Button bt1;
	public Button bt2;
	public Button bt3;

	private String take_photo_path;

	private Uri uri_temp_crop_picture;

	public Uri getUri_temp_crop_picture() {
		return uri_temp_crop_picture;
	}

	public MyPopWindow() {

	}

	public MyPopWindow(Context context) {
		mContext = context;
		initPop();
	}

	public PopupWindow getPop() {
		return pop;
	}

	File file;

	public void initPop() {

		pop = new PopupWindow(mContext);

		View view = ((Activity) mContext).getLayoutInflater().inflate(
				R.layout.item_popupwindows, null);

		pop.setWidth(LayoutParams.MATCH_PARENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);
		pop.setOutsideTouchable(true);
		pop.setContentView(view);

		RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
		bt1 = (Button) view.findViewById(R.id.item_popupwindows_camera);
		bt2 = (Button) view.findViewById(R.id.item_popupwindows_Photo);
		bt3 = (Button) view.findViewById(R.id.item_popupwindows_cancel);
		parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pop.dismiss();
			}
		});
		bt1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LogUtil.i(TAG, "照相");
				pop.dismiss();

				// 照相
				// 体检报告拍照的临时缓存
				File destDir = new File(Constants.IMAGES_DIR);
				if (!destDir.exists()) {
					destDir.mkdirs();
				}
				take_photo_path = Constants.IMAGES_DIR
						+ System.currentTimeMillis() + ".png";
				file = new File(take_photo_path);
				if (!file.exists()) {
					try {
						// 在指定的文件夹中创建文件
						file.createNewFile();
					} catch (Exception e) {
					}
				}

				Uri imageUri = Uri.fromFile(new File(take_photo_path));
				Intent openCameraIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				// 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
				if (imageUri != null) {
					openCameraIntent
							.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				}
				((Activity) mContext).startActivityForResult(openCameraIntent,
						Constants.CAMERA_WITH_DATA);
			}
		});
		bt2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pop.dismiss();
				// 相册
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_PICK);
				((Activity) mContext).startActivityForResult(intent,
						Constants.PHOTO_PICKED_WITH_DATA);
			}
		});
		bt3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 取消
				pop.dismiss();
			}
		});

	}

	/*
	 * public Bitmap INonActivityResult(int requestCode, Intent data) { if (data
	 * == null) { return null; }
	 * 
	 * LogUtil.i(TAG, "onActivityResult");
	 * 
	 * switch (requestCode) { case Constants.CAMERA_WITH_DATA: { // 照相机程序返回的
	 * Bitmap tempBitmap = BitmapFactory
	 * .decodeFile(Constants.HEAD_PICTURE_PATH);
	 * 
	 * uri_temp_crop_picture = Uri .fromFile(new File(Constants.IMAGES_DIR));
	 * 
	 * return tempBitmap;
	 * 
	 * // crop_upload(tempBitmap);
	 * 
	 * // break; }
	 * 
	 * case Constants.PHOTO_PICKED_WITH_DATA: { // 调用Gallery返回的 try { //
	 * 照片的原始资源地址 ContentResolver resolver = mContext.getContentResolver();
	 * uri_temp_crop_picture = data.getData(); // 使用ContentProvider通过URI获取原始图片
	 * Bitmap tempBitmap2 = MediaStore.Images.Media.getBitmap( resolver,
	 * uri_temp_crop_picture);
	 * 
	 * return tempBitmap2; // crop_upload(tempBitmap2);
	 * 
	 * } catch (FileNotFoundException e) { e.printStackTrace(); } catch
	 * (IOException e) { e.printStackTrace(); }
	 * 
	 * break; } case Constants.CROP_PICKED_WITH_DATA: { LogUtil.i(TAG, "裁剪"); if
	 * (data != null) { Bundle extras = data.getExtras(); Bitmap cropedBitmap =
	 * extras.getParcelable("data");
	 * 
	 * return cropedBitmap; }
	 * 
	 * break; }
	 * 
	 * 
	 * 
	 * case CHOOSE_PICTURE: ContentResolver resolver = getContentResolver();
	 * //照片的原始资源地址 Uri originalUri = data.getData(); try {
	 * //使用ContentProvider通过URI获取原始图片 Bitmap photo =
	 * MediaStore.Images.Media.getBitmap(resolver, originalUri); if (photo !=
	 * null) { //为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存 Bitmap smallBitmap
	 * = ImageTools.zoomBitmap(photo, photo.getWidth() / SCALE,
	 * photo.getHeight() / SCALE); //释放原始图片占用的内存，防止out of memory异常发生
	 * photo.recycle();
	 * 
	 * // iv_image.setImageBitmap(smallBitmap); } } catch (FileNotFoundException
	 * e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace();
	 * } break;
	 * 
	 * 
	 * case CROP: Uri uri = null; if (data != null) { uri = data.getData();
	 * System.out.println("Data"); }else { System.out.println("File"); String
	 * fileName = getSharedPreferences
	 * ("temp",Context.MODE_WORLD_WRITEABLE).getString("tempName", ""); uri =
	 * Uri.fromFile(new
	 * File(Environment.getExternalStorageDirectory(),fileName)); }
	 * cropImage(uri, 500, 500, CROP_PICTURE); break;
	 * 
	 * case CROP_PICTURE: Bitmap photo = null; Uri photoUri = data.getData(); if
	 * (photoUri != null) { photo =
	 * BitmapFactory.decodeFile(photoUri.getPath()); } if (photo == null) {
	 * Bundle extra = data.getExtras(); if (extra != null) { photo =
	 * (Bitmap)extra.get("data"); ByteArrayOutputStream stream = new
	 * ByteArrayOutputStream(); photo.compress(Bitmap.CompressFormat.JPEG, 100,
	 * stream); } } //iv_image.setImageBitmap(photo); break;
	 * 
	 * default: break; }
	 * 
	 * return null;
	 * 
	 * }
	 */

	public String INonActivityResult(int requestCode, Intent data, int tag) {

		LogUtil.i(TAG, "onActivityResult");

		switch (requestCode) {
		case Constants.CAMERA_WITH_DATA: {
			// 照相机程序返回的
			return ImageUtil.rotateImage(take_photo_path);

		}

		case Constants.PHOTO_PICKED_WITH_DATA: {
			if (data == null) {
				return null;
			}
			// 照片的原始资源地址
			uri_temp_crop_picture = data.getData();
			if (uri_temp_crop_picture == null) {
				return null;
			}
			return getImagePath(uri_temp_crop_picture);
		}

		default:
			break;
		}

		return null;

	}

	/**
	 * 得到相册中图片的路径
	 * 
	 * @return
	 */
	public String getImagePath(Uri originalUri) {
		if (originalUri.toString().contains("file://")) {
			return originalUri.toString().replace("file://", "");
		}
		String[] proj = { MediaColumns.DATA };
		// 好像是android多媒体数据库的封装接口，具体的看Android文档
		Cursor cursor = ((Activity) mContext).managedQuery(originalUri, proj,
				null, null, null);
		// 按我个人理解 这个是获得用户选择的图片的索引值
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		// 将光标移至开头 ，这个很重要，不小心很容易引起越界
		cursor.moveToFirst();
		// 最后根据索引值获取图片路径
		String path = cursor.getString(column_index);
		return path;
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
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		((Activity) mContext).startActivityForResult(intent,
				Constants.CROP_PICKED_WITH_DATA);
	}

}
