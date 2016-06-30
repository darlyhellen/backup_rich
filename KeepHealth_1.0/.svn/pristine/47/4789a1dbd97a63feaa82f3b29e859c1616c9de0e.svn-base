package com.ytdinfo.keephealth.utils;

import java.io.File;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class ImageUtil {
	/**
	 * 读取图片的旋转的角度
	 *
	 * @param path
	 *            图片绝对路径
	 * @return 图片的旋转角度
	 */
	public static int getBitmapDegree(String path) {
		int degree = 0;
		try {
			// 从指定路径下读取图片，并获取其EXIF信息
			ExifInterface exifInterface = new ExifInterface(path);
			// 获取图片的旋转信息
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 将图片按照某个角度进行旋转
	 *
	 * @param bm
	 *            需要旋转的图片
	 * @param degree
	 *            旋转角度
	 * @return 旋转后的图片
	 */
	public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
		Bitmap returnBm = null;

		// 根据旋转角度，生成旋转矩阵
		Matrix matrix = new Matrix();
		matrix.postRotate(degree);
		try {
			// 将原始图片按照旋转矩阵进行旋转，并得到新的图片
			returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
					bm.getHeight(), matrix, true);
		} catch (OutOfMemoryError e) {
		}
		if (returnBm == null) {
			returnBm = bm;
		}
		if (bm != returnBm) {
			bm.recycle();
		}
		return returnBm;
	}

	public static String rotateImage(String image_path) {
		try{
			if(image_path==null){
				return null;
			}
			
			File file = new File(image_path);
			LogUtil.i("wpc", "-----"+file.length());
			if(file==null||!file.exists()||file.length()==0){
				return null;
			}
			int degree = getBitmapDegree(image_path);
			if(degree == 0){
				return image_path;
			}
			Bitmap bm = ImageTools.cropBitmap(image_path);
			
			Bitmap rotated_bm = rotateBitmapByDegree(bm, degree);
			ImageTools.savePhotoToSDCard(rotated_bm, file.getParent(), "ROTATED_"+file
					.getName().replace(".png", ""));
			return file.getParent()+"/ROTATED_"+file.getName();
		}catch(Exception ex){
			return image_path;
		}

	}
/////////////////////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 读取图片属性：获取旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/*
	 * 旋转图片
	 * 
	 * @param angle 选择角度
	 * 
	 * @param bitmap
	 * 
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		System.out.println("angle2=" + angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 旋转图片
	 * 
	 * @param path
	 *            图片绝对路径
	 * @param bitmap
	 * @return
	 */
	/*
	 * public static void rotaingImageView(String path, Bitmap bitmap) { int
	 * degree = readPictureDegree(path); Bitmap bitmap2 =
	 * rotaingImageView(degree, bitmap); ImageTools.savePhotoToSDCard(bitmap2,
	 * path, photoName); }
	 */

	public static Bitmap rotaingImageView(String path, Bitmap bitmap) {
		int degree = readPictureDegree(path);
		return rotaingImageView(degree, bitmap);
	}

	/**
	 * 旋转图片
	 * 
	 * @param file
	 * @param bitmap
	 * @return
	 */
	public static String rotaingImageView(File file) {
		int degree = readPictureDegree(file.getAbsolutePath());
		Bitmap bitmap2 = rotaingImageView(degree,
				BitmapFactory.decodeFile(file.getAbsolutePath()));
		ImageTools.savePhotoToSDCard(bitmap2, file.getParent(), file.getName()
				.replace(".png", ""));
		bitmap2.recycle();
		return file.getAbsolutePath();
	}

	public static Bitmap rotaingImageView(File file, Bitmap bitmap) {
		int degree = readPictureDegree(file.getAbsolutePath());
		return rotaingImageView(degree, bitmap);
	}
}
