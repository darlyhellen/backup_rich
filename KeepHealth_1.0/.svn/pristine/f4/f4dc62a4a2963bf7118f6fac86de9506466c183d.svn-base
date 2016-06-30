package com.ytdinfo.keephealth.zhangyuhui.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

/**
 * @author Administrator 裁剪屏幕。获取图片。
 */
public class ScreenShot {

	public static Bitmap takeScreenShot(Activity activity, View backview,
			int height) {
		// View是你需要截图的View
		View view = activity.getWindow().getDecorView();
		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap b1 = view.getDrawingCache();

		// 获取状态栏高度
		// Rect frame = new Rect();
		// activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		// int statusBarHeight = frame.top;
		int[] location = new int[2];
		backview.getLocationOnScreen(location);
		Log.e("backview距离底部", location[1] + "---" + height+"---"+IALiteral.height);
		// 去掉标题栏
		Bitmap b = Bitmap.createBitmap(b1, 0, location[1], IALiteral.width,
		/* IALiteral.height - location[1] */height);
		view.destroyDrawingCache();
		return b;
	}

	public static void savePic(Bitmap b, File filePath) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filePath);
			if (null != fos) {
				b.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
			}
		} catch (FileNotFoundException e) {
			// e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	public static void shoot(Activity a, File filePath, View backview,
			int height) {
		if (filePath == null) {
			return;
		}
		if (!filePath.getParentFile().exists()) {
			filePath.getParentFile().mkdirs();
		}
		ScreenShot.savePic(ScreenShot.takeScreenShot(a, backview, height),
				filePath);
	}
}