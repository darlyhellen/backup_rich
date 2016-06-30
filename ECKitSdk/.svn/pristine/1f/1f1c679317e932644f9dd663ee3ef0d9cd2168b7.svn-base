package com.yuntongxun.kitsdk.ui.voip;

import java.util.Arrays;




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
import com.yuntongxun.ecsdk.CameraCapability;
import com.yuntongxun.ecsdk.CameraInfo;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.voip.video.ECCaptureView;
import com.yuntongxun.kitsdk.utils.LogUtil;
import com.yuntongxun.kitsdk.utils.TextUtil;


public class VideoActivity extends ECVoIPBaseActivity implements OnClickListener{
	
    private static final String TAG = "VideoActivity";
    private static long lastClickTime;


    private SurfaceView mVideoView;
    private Chronometer mChronometer;

    private View mCameraSwitch;
    boolean isConnect = false;
    CameraInfo[] cameraInfos;
    public ECCaptureView mLoaclVideoViewLate;
    int numberOfCameras;
    private ImageButton video_switch;
    private int mWidth;
    private int mHeight;

    public int defaultCameraId;
    public int cameraCurrentlyLocked;
    public int mCameraCapbilityIndex;
    
    private FrameLayout mVedioBefore;
    private FrameLayout mVedioLate;
	private TextView mName;
	private TextView mPhone;
	private ImageView mVideoIcon;
	private TextView mVideoTopTips;
	private ImageButton handUpBefore;
	private RelativeLayout mLoaclVideoViewBefore;
	
	private ImageButton handUpLate;
	private ImageButton mMute;
	private long duration = 0;
	private ImageButton cameraSwitchBefore;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
        mCallName = getIntent().getStringExtra(EXTRA_CALL_NAME);
        mCallNumber = getIntent().getStringExtra(EXTRA_CALL_NUMBER);
        mCallFrom = getIntent().getStringExtra(EXTRA_CALL_FROM);
        
        initResourceRefs();
        if(!TextUtil.isEmpty(mCallName)){
    		mName.setText(mCallName);
        }
        if(!TextUtil.isEmpty(mCallNumber)){
        	mPhone.setText(mCallNumber);
        }
        

        ECDevice.getECVoIPSetupManager().setVideoView(mVideoView, mLoaclVideoViewLate);
        mCallId = VoIPCallHelper.makeCall(mCallType ,  mCallNumber);
    }
    
    private void initResourceRefs() {
    	
    	mVedioBefore = (FrameLayout) findViewById(R.id.Video_layout_before);
		mName = (TextView) findViewById(R.id.tv_name);
		mPhone = (TextView) findViewById(R.id.tv_phone);
		mVideoIcon = (ImageView) findViewById(R.id.video_icon);
		mVideoTopTips = (TextView) findViewById(R.id.notice_tips);
		handUpBefore = (ImageButton) findViewById(R.id.hand_up_before);
		handUpBefore.setOnClickListener(this);
		mLoaclVideoViewBefore = (RelativeLayout) findViewById(R.id.localvideo_view_before);
		cameraSwitchBefore = (ImageButton) findViewById(R.id.camera_switch_before);
		
		mVedioLate = (FrameLayout) findViewById(R.id.Video_layout_late);
		mVideoView = (SurfaceView) findViewById(R.id.video_view);
        mLoaclVideoViewLate = (ECCaptureView) findViewById(R.id.localvideo_view_late);
        mLoaclVideoViewLate.setZOrderMediaOverlay(true);
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
    protected void onResume() {
    	super.onResume();
    }

	@Override
    protected void onDestroy() {
    	super.onDestroy();
    	VoIPCallHelper.mHandlerVideoCall = false;
    }

	@Override
	public void onCallProceeding(String callId) {
		
        if (callId != null && callId.equals(mCallId)) {
            mVideoTopTips.setText(getString(R.string.ec_voip_call_connect));
        }
	}

	@Override
	public void onCallAlerting(String callId) {
        if (callId != null && callId.equals(mCallId)) {// 等待对方接受邀请...
            mVideoTopTips.setText(getString(R.string.str_tips_wait_invited));
        }
	}

	@Override
	public void onCallAnswered(String callId) {
        if (callId != null && callId.equals(mCallId) && !isConnect) {
            initResVideoSuccess();
        }
	}

	private void initResVideoSuccess() {	
		
		isConnect = true;
		mVedioBefore.setVisibility(View.GONE);
		mVedioLate.setVisibility(View.VISIBLE);
		
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.setVisibility(View.VISIBLE);
        mChronometer.start();
        mLoaclVideoViewLate.setVisibility(View.VISIBLE);

        

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
	
    private void finishCalling() {
    	mVedioBefore.setVisibility(View.VISIBLE);
    	cameraSwitchBefore.setVisibility(View.GONE);
    	handUpBefore.setVisibility(View.GONE);
    	mVideoTopTips.setText(R.string.ec_voip_calling_finish);
    	if(isConnect){
    		duration = SystemClock.elapsedRealtime() - mChronometer.getBase();
    		mChronometer.stop();
    		mVedioLate.setVisibility(View.GONE);
            mLoaclVideoViewLate.setVisibility(View.GONE);
    	}
    	finish();
    	isConnect = false;
    }
    
    private void finishCalling(int reason) {
    	mVedioBefore.setVisibility(View.VISIBLE);
    	handUpBefore.setVisibility(View.GONE);
    	mVideoTopTips.setText(CallFailReason.getCallFailReason(reason));
        mLoaclVideoViewLate.setVisibility(View.GONE);
        cameraSwitchBefore.setVisibility(View.GONE);
        if (isConnect) {
        	duration = SystemClock.elapsedRealtime() - mChronometer.getBase();
            mChronometer.stop();
            mVedioLate.setVisibility(View.GONE);
        }
        isConnect = false;
        VoIPCallHelper.releaseCall(mCallId);
        if(reason == 175603){
        	return;
        }
        finish();
    }
    


	@Override
	public void onClick(View v) {
		
		if(v.getId()==R.id.hand_up_before){
			doHandUpReleaseCall();
			
		}else if(v.getId()==R.id.hand_up_late){
			doHandUpReleaseCall();
			
		}else if(v.getId()==R.id.camera_switch){
			if (numberOfCameras == 1) {
                return;
            }
            mCameraSwitch.setEnabled(false);
            mLoaclVideoViewLate.switchCamera();
            mCameraSwitch.setEnabled(true);
			
		}else if(v.getId()==R.id.mute){
			
			VoIPCallHelper.setMute();
            boolean mute = VoIPCallHelper.getMute();
            mMute.setImageResource(mute ? R.drawable.mute_icon_on : R.drawable.mute_selector);
		}
		
		
	}
	
    protected void doHandUpReleaseCall() {

        LogUtil.d(TAG, "[VideoActivity] onClick: Voip talk hand up, CurrentCallId " + mCallId);
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
	protected int getLayoutId() {
		return R.layout.ec_video_call_out;
	}
	
    public void comportCapbilityIndex(CameraCapability[] caps) {

        if(caps == null ) {
            return;
        }
        int pixel[] = new int[caps.length];
        int _pixel[] = new int[caps.length];
        for(CameraCapability cap : caps) {
            if(cap.index >= pixel.length) {
                continue;
            }
            pixel[cap.index] = cap.width * cap.height;
        }
        System.arraycopy(pixel, 0, _pixel, 0, caps.length);
        Arrays.sort(_pixel);
        for(int i = 0 ; i < caps.length ; i++) {
            if(pixel[i] == /*_pixel[0]*/ 352*288) {
                mCameraCapbilityIndex = i;
                return;
            }
        }
    }

	@Override
	public void onMakeCallback(ECError arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}
}
