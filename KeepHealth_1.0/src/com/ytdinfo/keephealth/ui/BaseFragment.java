package com.ytdinfo.keephealth.ui;

import com.yuntongxun.kitsdk.utils.FontMainUtils;

import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		FontMainUtils.changeTypeface(getView());
	}

}
