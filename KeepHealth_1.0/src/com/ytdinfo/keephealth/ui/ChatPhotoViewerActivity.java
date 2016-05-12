package com.ytdinfo.keephealth.ui;

import uk.co.senab.photoview.PhotoView;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ytdinfo.keephealth.R;

@SuppressLint("ResourceAsColor")
public class ChatPhotoViewerActivity extends BaseActivity {
	private PhotoView imageView;
	private String fileUrl;
	private ImageView id_iv_close;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_imageview);
		id_iv_close = (ImageView) findViewById(R.id.id_iv_close);
		imageView = (PhotoView) findViewById(R.id.chat_iv);
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageView.setAdjustViewBounds(true);
		// imageView.setBackgroundColor(R.color.transparent);
		imageView.setBackgroundColor(getResources().getColor(
				R.color.transparent2));
		/*
		 * int screenWidth = getScreenWidth(this); ViewGroup.LayoutParams lp =
		 * imageView.getLayoutParams(); lp.width = screenWidth; lp.height =
		 * LayoutParams.WRAP_CONTENT; imageView.setLayoutParams(lp);
		 * imageView.setMaxWidth(screenWidth)
		 */;
		if (null != getIntent()) {
			fileUrl = getIntent().getStringExtra("fileUrl");
			ImageLoader.getInstance().displayImage(fileUrl, imageView);
		}

		id_iv_close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
}
