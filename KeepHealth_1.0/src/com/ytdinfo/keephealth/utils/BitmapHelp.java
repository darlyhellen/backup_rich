package com.ytdinfo.keephealth.utils;

import android.content.Context;

import com.lidroid.xutils.BitmapUtils;
import com.ytdinfo.keephealth.R;


public class BitmapHelp {
    private BitmapHelp() {
    }

    private static BitmapUtils bitmapUtils;

    /**
     * BitmapUtils不是单例的 根据需要重载多个获取实例的方法
     *
     * @param appContext application context
     * @return
     */
    public static BitmapUtils getBitmapUtils(Context appContext) {
        if (bitmapUtils == null) {
            bitmapUtils = new BitmapUtils(appContext);
          //  bitmapUtils.configDefaultCacheExpiry(1000 * 10);
        }
     bitmapUtils.configDefaultLoadingImage(R.drawable.userinfo_touxiang);
         bitmapUtils.configDefaultLoadFailedImage(R.drawable.userinfo_touxiang);
	     //bitmapUtils.configDefaultBitmapConfig(Bitmap.Config.RGB_565);
	    // bitmapUtils.configDefaultAutoRotation(true);
        return bitmapUtils;
    }
}
