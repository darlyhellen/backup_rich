package com.ytdinfo.keephealth.ui.setting;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.rayelink.eckit.MainChatControllerListener;
import com.umeng.analytics.MobclickAgent;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.BaseActivity;
import com.ytdinfo.keephealth.ui.MainActivity;
import com.ytdinfo.keephealth.ui.view.CommonActivityTopView;
import com.ytdinfo.keephealth.ui.view.CommonModifyView;
import com.ytdinfo.keephealth.utils.DBUtilsHelper;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;

public class SettingActivity extends BaseActivity implements OnClickListener {
	private CommonActivityTopView commonActivityTopView;
	private CommonModifyView commonModifyView;
	private ImageButton ibt_switch;
	// 开关
	int swh = 1;

	private Button bt_cancelUser;
	private AlertDialog alertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		initView();
		initListener();
		if (SharedPrefsUtil.getValue(Constants.ALERT, true)) {
			ibt_switch.setBackgroundResource(R.drawable.on_switch);
			swh = 1;
		} else {
			ibt_switch.setBackgroundResource(R.drawable.off_switch);
			swh = 0;
		}
	}

	private void initView() {
		commonActivityTopView = (CommonActivityTopView) findViewById(R.id.id_commonActivityTopView);
		commonActivityTopView.tv_title.setText("设置");

		commonModifyView = (CommonModifyView) findViewById(R.id.id_modify_pass);
		commonModifyView.tv_title.setText("修改密码");

		ibt_switch = (ImageButton) findViewById(R.id.id_ibt_switch);

		bt_cancelUser = (Button) findViewById(R.id.id_bt_cancelUser);

	}

	private void initListener() {
		commonActivityTopView.ibt_back.setOnClickListener(this);
		commonModifyView.setOnClickListener(this);
		ibt_switch.setOnClickListener(this);
		bt_cancelUser.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.id_ibt_back:
			finish();
			break;
		case R.id.id_modify_pass:
			// 修改密码
			UserModel userModel = new Gson().fromJson(
					SharedPrefsUtil.getValue(Constants.USERMODEL, ""),
					UserModel.class);
			String telephone = userModel.getMobilephone();
			Intent i = new Intent(SettingActivity.this,
					ModifyPassActivity.class);
			i.putExtra("telephone", telephone);
			startActivity(i);
			break;
		case R.id.id_ibt_switch:
			// 开关
			if (swh == 1) {
				SharedPrefsUtil.putValue(Constants.ALERT, false);
				ibt_switch.setBackgroundResource(R.drawable.off_switch);
				swh = 0;
			} else {
				SharedPrefsUtil.putValue(Constants.ALERT, true);
				ibt_switch.setBackgroundResource(R.drawable.on_switch);
				swh = 1;
			}
			break;
		case R.id.id_bt_cancelUser:

			showDialog();

			break;
		default:
			break;
		}
	}

	public void showDialog() {
		if(alertDialog != null && alertDialog.isShowing()){
			return;
		}
		alertDialog = new AlertDialog.Builder(SettingActivity.this).create();
		alertDialog.show();
		alertDialog.setCanceledOnTouchOutside(false);
		Window window = alertDialog.getWindow();
		window.setContentView(R.layout.exit_app);
//		ImageView close = (ImageView) window.findViewById(R.id.cto_close);
//		close.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				alertDialog.dismiss();
//			}
//		});
		Button cancel = (Button) window.findViewById(R.id.cto_cancle);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				alertDialog.dismiss();

			}
		});
		Button sure = (Button) window.findViewById(R.id.cto_sure);
		sure.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				cancelUser();
			}
		});
	}

	private void cancelUser() {
		SharedPrefsUtil.putValue(Constants.CHECKEDID_RADIOBT, 0);
		SharedPrefsUtil.remove(Constants.TOKEN);
		SharedPrefsUtil.remove(Constants.USERID);
		SharedPrefsUtil.remove(Constants.USERMODEL);
		SharedPrefsUtil.remove(Constants.ONLINE_QUES_USERMODEL);
		MainChatControllerListener.closeAllSubject(false);
		DBUtilsHelper.instance=null;
		ToastUtil.showMessage("已退出");
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
	
	
	

	@Override
	public void onResume() {
		super.onResume();

		MobclickAgent.onPageStart("SettingActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();

		MobclickAgent.onPageEnd("SettingActivity");
		MobclickAgent.onPause(this);
	}
}
