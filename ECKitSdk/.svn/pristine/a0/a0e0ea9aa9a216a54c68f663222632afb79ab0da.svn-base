package com.yuntongxun.kitsdk.utils;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umeng.comm.core.beans.CommConfig;

public class FontMainUtils {
	 /**
     * 为所有textView类型和其子类型修改字体
     */
    public static void changeTypeface(View rootView) {
        Typeface typeface = CommConfig.getConfig().getTypeface();
        if (typeface == null || !(rootView instanceof ViewGroup)) {
            return;
        }

        ViewGroup rootViewGroup = (ViewGroup) rootView;
        // 迭代所有View, 如果是TextView的子类就修改字体
        int childCount = rootViewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = rootViewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                changeTypeface(view);
            } else if (view instanceof TextView) {
                ((TextView) view).setTypeface(typeface);
            }
        }

    }
}
