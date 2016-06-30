package com.yuntongxun.kitsdk.ui.voip;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yuntongxun.eckitsdk.R;
import com.yuntongxun.ecsdk.CameraInfo;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.voip.video.ECCaptureView;
import com.yuntongxun.kitsdk.utils.LogUtil;

public class VideoCallInActivity extends ECVoIPBaseActivity implements
		OnClickListener {

	private static final String TAG = "VideoCallInActivity";
	private RelativeLayout mInfoLl;
	private FrameLayout mVedioGoing;
	private TextView mName;
	private TextView mPhone;
	private ImageView mVideoIcon;
	private TextView mVideoTopTips;
	private ImageButton answer;
	private ImageButton handUpBefore;
	private Chronometer mChronometer;
	public ECCaptureView mLoaclVideoView;
	private SurfaceView mVideoView;
	private View mCameraSwitch;
	private ImageButton video_switch;
	private ImageButton handUpLate;
	private ImageButton mMute;

	private long duration = 0;
	boolean isConnect = false;

	public int defaultCameraId;
	public int mCameraCapbilityIndex;
	public int cameraCurrentlyLocked;
	CameraInfo[] cameraInfos;
	int numberOfCameras;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mCallId = getIntent().getStringExtra(ECDevice.CALLID);
		mCallNumber = getIntent().getStringExtra(ECDevice.CALLER);
		mCallName = mCallNumber;
		initResourceRefs();
		mName.setText(mCallName);
		mPhone.setText(mCallNumber);
		mVideoTopTips.setText(getString(R.string.str_vedio_call_in, mCallName));

		ECDevice.getECVoIPSetupManager().setVideoView(mVideoView, mLoaclVideoView);
		mLoaclVideoView.setVisibility(View.VISIBLE);
	}

	private void initResourceRefs() {

		mInfoLl = (RelativeLayout) findViewById(R.id.vedio_prepare);
		mName = (TextView) findViewById(R.id.tv_name);
		mPhone = (TextView) findViewById(R.id.tv_phone);
		mVideoIcon = (ImageView) findViewById(R.id.video_icon);
		mVideoTopTips = (TextView) findViewById(R.id.notice_tips);
		answer = (ImageButton) findViewById(R.id.answer);
		handUpBefore = (ImageButton) findViewById(R.id.hand_up_before);

		answer.setOnClickListener(this);
		handUpBefore.setOnClickListener(this);

		mVedioGoing = (FrameLayout) findViewById(R.id.vedio_going);
		mVideoView = (SurfaceView) findViewById(R.id.video_view);
		mLoaclVideoView = (ECCaptureView) findViewById(R.id.localvideo_view);
		mLoaclVideoView.setZOrderMediaOverlay(true);
		mCameraSwitch = findViewById(R.id.camera_switch);
		mCameraSwitch.setOnClickListener(this);
		video_switch = (ImageButton) findViewById(R.id.video_switch);
		video_switch.setOnClickListener(this);
		handUpLate = (ImageButton) findViewById(R.id.hand_up_late);
		handUpLate.setOnClickListener(this);
		mMute = (ImageButton) findViewById(R.id.mute);
		mMute.setOnClickListener(this);
		mVideoView.getHolder().setFixedSize(240, 320);
	}



	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.answer) {
			VoIPCallHelper.acceptCall(mCallId);

		} else if (v.getId() == R.id.hand_up_before) {
			doHandUpReleaseCall();

		} else if (v.getId() == R.id.hand_up_late) {
			doHandUpReleaseCall();

		} else if (v.getId() == R.id.mute) {
			VoIPCallHelper.setMute();
			boolean mute = VoIPCallHelper.getMute();
			mMute.setImageResource(mute ? R.drawable.mute_icon_on
					: R.drawable.mute_selector);

		} else if (v.getId() == R.id.camera_switch) {
			if (numberOfCameras == 1) {
				return;
			}
			mCameraSwitch.setEnabled(false);
			mLoaclVideoView.switchCamera();
			mCameraSwitch.setEnabled(true);
		}
	}

	@Override
	public void onCallProceeding(String callId) {

	}

	@Override
	public void onCallAlerting(String callId) {

	}

	@Override
	public void onCallAnswered(String callId) {
		if (callId != null && callId.equals(mCallId) && !isConnect) {
			initResVideoSuccess();
		}
	}

	private void initResVideoSuccess() {
		isConnect = true;
		mInfoLl.setVisibility(View.GONE);
		mVedioGoing.setVisibility(View.VISIBLE);
		mLoaclVideoView.setVisibility(View.VISIBLE);

		mChronometer = (Chronometer) findViewById(R.id.chronometer);
		mChronometer.setBase(SystemClock.elapsedRealtime());
		mChronometer.setVisibility(View.VISIBLE);
		mChronometer.start();

		// 默认设为前置摄像头

	}

	@Override
	public void onMakeCallFailed(String callId, int reason) {
		if (callId != null && callId.equals(mCallId)) {
			finishCalling(reason);
		}
	}

	@Override
	public void onCallReleased(String callId) {
		if (callId != null && callId.equals(mCallId)) {
			finishCalling();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		VoIPCallHelper.mHandlerVideoCall = false;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.ec_video_call_in;
	}

	private void finishCalling() {
		mInfoLl.setVisibility(View.VISIBLE);
		answer.setVisibility(View.GONE);
		handUpBefore.setVisibility(View.GONE);
		mLoaclVideoView.setVisibility(View.GONE);
		mVideoTopTips.setText(R.string.ec_voip_calling_finish);
		if (isConnect) {
			duration = SystemClock.elapsedRealtime() - mChronometer.getBase();
			mChronometer.stop();
			mVedioGoing.setVisibility(View.GONE);
			mLoaclVideoView.setVisibility(View.GONE);
		}
		// insertCallLog();
		finish();
		isConnect = false;
	}

	private void finishCalling(int reason) {
		mInfoLl.setVisibility(View.VISIBLE);
		answer.setVisibility(View.GONE);
		handUpBefore.setVisibility(View.GONE);
		mVideoTopTips.setText(CallFailReason.getCallFailReason(reason));
		mLoaclVideoView.setVisibility(View.GONE);
		if (isConnect) {
			duration = SystemClock.elapsedRealtime() - mChronometer.getBase();
			mChronometer.stop();
			mVedioGoing.setVisibility(View.GONE);
		}
		isConnect = false;
		VoIPCallHelper.releaseCall(mCallId);
		if (reason == 175603) {
			return;
		}
		// insertCallLog();
		finish();
	}



	protected void doHandUpReleaseCall() {

		// Hang up the video call...
		LogUtil.d(TAG,
				"[VideoActivity] onClick: Voip talk hand up, CurrentCallId "
						+ mCallId);
		try {
			if (mCallId != null) {
				VoIPCallHelper.releaseCall(mCallId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!isConnect) {
			finish();
		}
	}



	@Override
	public void onMakeCallback(ECError arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub

	}
}
