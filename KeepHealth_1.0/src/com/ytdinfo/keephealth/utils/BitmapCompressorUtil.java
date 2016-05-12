package com.ytdinfo.keephealth.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import net.bither.util.NativeUtil;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import com.ytdinfo.keephealth.app.Constants;
 
/**
 * 图片压缩工具类
 */
@SuppressLint("NewApi")
public class BitmapCompressorUtil {
	private static String TAG = "BitmapCompressorUtil";
    /**
     * 质量压缩
     * @param image
     * @param maxkb
     * @return
     */
    public static Bitmap compressBitmap(Bitmap image,int maxkb) {
    	if(image==null){
    		return null;
    	}
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
//      Log.i(test,原始大小 + baos.toByteArray().length);
        while (baos.toByteArray().length / 1024 > maxkb && options>0) { // 循环判断如果压缩后图片是否大于(maxkb)50kb,大于继续压缩
          
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
            
            Log.i("paul","压缩一次!---" +options+"==="+baos.toByteArray().length / 1024);
        }
//      Log.i(test,压缩后大小 + baos.toByteArray().length);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
         image = BitmapFactory.decodeStream(isBm);// 把ByteArrayInputStream数据生成图片
        
        ImageTools.savePhotoToSDCard(baos, Constants.IMAGES_DIR, Constants.CROPED_IMAGE_NAME);
        
//        ImageTools.recycleBitmap(image);
        
        return image;
    }
    @SuppressLint("NewApi")
	public static String compressBitmap2(Bitmap image,int maxkb,String smallFileName) {
   
    	FileInputStream fileInputStream = null ;
    	int options = 100;
    	while (options>0) {
    		// 循环判断如果压缩后图片是否大于(maxkb)50kb,大于继续压缩
    		//bitmap.recycle();
    		//baos.reset();// 重置baos即清空baos
    		//image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
    		System.out.println("op..."+options);
    		NativeUtil.compressBitmap(image, options,smallFileName, true);
    		options -= 10;// 每次都减少10
    		//bitmap.recycle();
    		//bitmap = BitmapFactory.decodeFile(smallFileName);
    		//System.out.println("op2..."+getBitmapsize(bitmap));
    	 	try {
				 fileInputStream = new FileInputStream(smallFileName);
				//System.out.println("op2..."+fileInputStream.available());
				if (fileInputStream.available()/1024<maxkb) {
					fileInputStream.close();
					break;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
    	//	System.out.println("bit......."+bitmap.getByteCount());
    		//bitmap.recycle();
    		//Log.i("paul","压缩一次!---" +options+"==="+baos.toByteArray().length / 1024);
    	}
    //	FileInputStream
    	try {
			fileInputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    image.recycle();
    	return smallFileName;
//      Log.i(test,压缩后大小 + baos.toByteArray().length);
    	
    //	.return bitmap;
    }
    public static long getBitmapsize(Bitmap bitmap){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
return bitmap.getByteCount();
}
// Pre HC-MR1
return bitmap.getRowBytes() * bitmap.getHeight();

}
    /**
     * http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
     * 官网：获取压缩后的图片
     * 
     * @param res
     * @param resId
     * @param reqWidth
     *            所需图片压缩尺寸最小宽度
     * @param reqHeight
     *            所需图片压缩尺寸最小高度
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res,
            int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
         
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
 
    /**
     * 官网：获取压缩后的图片
     * 
     * @param res
     * @param resId
     * @param reqWidth
     *            所需图片压缩尺寸最小宽度
     * @param reqHeight
     *            所需图片压缩尺寸最小高度
     * @return
     */
    public static Bitmap decodeSampledBitmapFromFile(String filepath,
            int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, options);
 
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Config.RGB_565;
        options.inDither = true;
        return BitmapFactory.decodeFile(filepath, options);
    }
 /**
  * 官网：获取压缩后的图片
  * 
  * @param bitmap
  * @param reqWidth
  * @param reqHeight
  * @return
  */
    public static Bitmap decodeSampledBitmapFromBitmap(Bitmap bitmap,
            int reqWidth, int reqHeight) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
        byte[] data = baos.toByteArray();
         
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }
 
    /**
     * 计算压缩比例值(改进版 by touch_ping)
     * 
     * 原版2>4>8...倍压缩
     * 当前2>3>4...倍压缩
     * 
     * @param options
     *            解析图片的配置信息
     * @param reqWidth
     *            所需图片压缩尺寸最小宽度O
     * @param reqHeight
     *            所需图片压缩尺寸最小高度
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
         
        final int picheight = options.outHeight;
        final int picwidth = options.outWidth;
        Log.i(TAG, "原尺寸: "+  picwidth +" * "+picheight);
         
        int targetheight = picheight;
        int targetwidth = picwidth;
        int inSampleSize = 1;
         
        if (targetheight > reqHeight || targetwidth > reqWidth) {
            while (targetheight  >= reqHeight
                    && targetwidth>= reqWidth) {
                Log.i(TAG,"压缩: "+inSampleSize + "倍");
                inSampleSize += 1;
                targetheight = picheight/inSampleSize;
                targetwidth = picwidth/inSampleSize;
            }
        }
         
        Log.i(TAG,"最终压缩比例: "+inSampleSize + "倍");
        Log.i(TAG, "新尺寸: "+  targetwidth + " * " +targetheight);
        return inSampleSize;
    }
}