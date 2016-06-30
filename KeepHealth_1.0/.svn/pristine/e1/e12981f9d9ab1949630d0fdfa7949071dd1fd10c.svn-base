package com.ytdinfo.keephealth.adapter;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.rayelink.eckit.SDKCoreHelper;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.app.HttpClient;
import com.ytdinfo.keephealth.app.MyApp;
import com.ytdinfo.keephealth.model.DocInfoBean;
import com.ytdinfo.keephealth.model.ReportBean;
import com.ytdinfo.keephealth.model.UserModel;
import com.ytdinfo.keephealth.ui.MainActivity;
import com.ytdinfo.keephealth.ui.view.MyProgressDialog;
import com.ytdinfo.keephealth.utils.Chat_Dialog;
import com.ytdinfo.keephealth.utils.DBUtilsHelper;
import com.ytdinfo.keephealth.utils.LogUtil;
import com.ytdinfo.keephealth.utils.NetworkReachabilityUtil;
import com.ytdinfo.keephealth.utils.SharedPrefsUtil;
import com.ytdinfo.keephealth.utils.ToastUtil;
import com.yuntongxun.kitsdk.ECDeviceKit;
import com.yuntongxun.kitsdk.beans.ChatInfoBean;

public class ReportAdapter extends BaseAdapter {
	private static final String TAG = ReportAdapter.class.getName();
	private LayoutInflater inflater;
	private List<ReportBean> list;
	// private BitmapUtils bitmapUtils;
	private ReportBean ReportBean;
	private Context context;
	private UserModel userModel;
	private String SubjectID;
	private DocInfoBean docInfoBean;
	DbUtils db;

	public ReportAdapter(Context context, List<ReportBean> list) {
		super();
		inflater = LayoutInflater.from(context);
		this.list = list;
		this.context = context;
		// bitmapUtils =BitmapHelp.getBitmapUtils(context);
		userModel = new Gson().fromJson(
				SharedPrefsUtil.getValue(Constants.USERMODEL, null),
				UserModel.class);
		db = DBUtilsHelper.getInstance().getDb();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public ReportBean getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		MyListener myListener = null;
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.kh_choose_report_listview_item, null);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView
					.findViewById(R.id.crli_title);
			viewHolder.date = (TextView) convertView
					.findViewById(R.id.crli_date);
			viewHolder.button = (Button) convertView.findViewById(R.id.crli_bt);
			convertView.setTag(viewHolder);
			/*
			 * viewHolder.button.setTag(position);
			 * viewHolder.button.setOnClickListener(new OnClickListener() {
			 * 
			 * @Override public void onClick(View v) { // TODO Auto-generated
			 * method stub
			 * System.out.println("re........."+ReportBean.getStudyId()); Intent
			 * i = new Intent(); i.setClass(context, GroupChatActivity.class);
			 * // i.putExtra(GroupBaseActivity.KEY_GROUP_ID, "88249600000048");
			 * Bundle bundle = new Bundle();
			 * bundle.putSerializable("ReportBean", ReportBean);
			 * i.putExtras(bundle); context.startActivity(i); } });
			 */
		} else {
			//
			viewHolder = (ViewHolder) convertView.getTag();
		}
		myListener = new MyListener(position, viewHolder.button);
		// viewHolder.button.setFocusable(true);
		// viewHolder.button.requestFocus();
		ReportBean = list.get(position);
		viewHolder.button.setOnClickListener(myListener);
		viewHolder.title.setText(ReportBean.getName() + " "
				+ ReportBean.getTitle());
		viewHolder.date.setText(ReportBean.getMedicalDate());
		return convertView;

	}

	private class MyListener implements OnClickListener {
		int mPosition;
		private Button buttons;

		public MyListener(int inPosition, Button button) {
			mPosition = inPosition;
			buttons = button;
		}

		@Override
		public void onClick(View v) {
			// requestDoctor(list.get(mPosition).getStudyId());
			if (!DBUtilsHelper.getInstance().isHaveOnline()) {

				if (!NetworkReachabilityUtil.isNetworkConnected(MyApp
						.getInstance())) {
					ToastUtil.showMessage("网络未连接...");
					buttons.setClickable(true);
				} else {
					buttons.setClickable(false);
					requestDoctor(list.get(mPosition).getStudyId(), buttons);
				}
			} else {
				ToastUtil.showMessage("您当前正在进行在线咨询，结束后才能进行报告解读哦");
				Intent intent = new Intent(ReportAdapter.this.context,
						MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("news", "news");
				ReportAdapter.this.context.startActivity(intent);
				SharedPrefsUtil.putValue(Constants.CHECKEDID_RADIOBT, 1);
			}
		}

	}

	class ViewHolder {
		public TextView title;
		public TextView date;
		public Button button;
	}

	private MyProgressDialog myProgressDialog;

	private void requestDoctor(String studyId, final Button buttons) {
		// TODO Auto-generated method stub
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("SubjectType", "1");
			jsonObject.put("UserID", userModel.getID());
			jsonObject.put("UserName", userModel.getUserName());
			jsonObject.put("UserSex", userModel.getUserSex());
			jsonObject.put("Age", userModel.getAge());
			jsonObject.put("HeadPicture", userModel.getHeadPicture());
			jsonObject.put("RelationShip", null);
			jsonObject.put("StudyID", studyId);
			jsonObject.put("AttachPics", null);
			jsonObject.put("BodyContent", null);
			HttpClient.post(Constants.STARTCHAT_URl, jsonObject.toString(),
					new RequestCallBack<String>() {
						@Override
						public void onStart() {
							// TODO Auto-generated method stub
							super.onStart();
							myProgressDialog = new MyProgressDialog(context);
							myProgressDialog.setMessage("正在请求...");
							myProgressDialog.show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> arg0) {
							// TODO Auto-generated method stub
							LogUtil.i(TAG, arg0.result);
							myProgressDialog.dismiss();
							analyzeJson(arg0.result, buttons);

						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							// TODO Auto-generated method stub
							myProgressDialog.dismiss();
							buttons.setClickable(true);
						}
					});

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	ChatInfoBean chatInfoBean;

	private void analyzeJson(String json, Button buttons) {
		// TODO Auto-generated method stub
//		if (!Chat_Dialog.timeCurl()) {
//			final AlertDialog dialog = new AlertDialog.Builder(context)
//					.create();
//			dialog.show();
//			dialog.setCanceledOnTouchOutside(false);
//			Window window = dialog.getWindow();
//			window.setContentView(R.layout.chat_dialog);// 设置对话框的布局
//			TextView msg = (TextView) window.findViewById(R.id.chat_dialog_msg);
//			String desString = "亲，非常抱歉，我们的服务时间是工作日9：00－18：00，欢迎下次来咨询，祝您身体健康！";
//			msg.setText(desString);
//			Button sure = (Button) window.findViewById(R.id.chat_dialog_sure);
//			sure.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//					// TODO Auto-generated method stub
//					dialog.dismiss();
//				}
//			});
//			buttons.setClickable(true);
//			return;
//		}
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONObject data = jsonObject.getJSONObject("Data");
			SubjectID = data.getString("SubjectID");
			// SharedPrefsUtil.putValue(Constants.SUBJECTID, SubjectID);
			String responser = data.getString("responser");
			if (null == responser || responser.equals("")
					|| responser.equals("null")) {
				final AlertDialog dialog = new AlertDialog.Builder(context)
						.create();
				dialog.show();
				dialog.setCanceledOnTouchOutside(false);
				Window window = dialog.getWindow();
				window.setContentView(R.layout.chat_dialog);// 设置对话框的布局
				TextView msg = (TextView) window
						.findViewById(R.id.chat_dialog_msg);
				String desString = "亲，我们的医生都在忙碌，请稍等~";
				msg.setText(desString);
				Button sure = (Button) window
						.findViewById(R.id.chat_dialog_sure);
				sure.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				buttons.setClickable(true);
				return;
			}
			// 开启计时
			// ......
			docInfoBean = new Gson().fromJson(responser, DocInfoBean.class);
			try {
				chatInfoBean = db.findFirst(Selector.from(ChatInfoBean.class)
						.where("docInfoBeanId", "=",
								docInfoBean.getVoipAccount()));
				if (null == chatInfoBean) {
					chatInfoBean = new ChatInfoBean();
					chatInfoBean.setSubjectID(SubjectID);
					chatInfoBean.setDocInfoBeanId(docInfoBean.getVoipAccount());
					chatInfoBean.setComment(false);
				} else {
					chatInfoBean.setSubjectID(SubjectID);
					chatInfoBean.setComment(false);
				}
				chatInfoBean.setTimeout(false);
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!SDKCoreHelper.isOnLine()) {
				MyApp.ConnectYunTongXun();
			}
			goIntent(chatInfoBean);
			buttons.setClickable(true);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void goIntent(ChatInfoBean chatInfoBean) {
		// TODO Auto-generated method stub
		chatInfoBean.setSubjectType("1");
		chatInfoBean.setStatus(false);
		DBUtilsHelper.getInstance().saveChatinfo(chatInfoBean);
		ECDeviceKit.getIMKitManager().startConversationActivity(chatInfoBean,
				null, null);
		/*
		 * Intent i = new Intent(); Bundle bundle = new Bundle();
		 * bundle.putSerializable("chatInfoBean", chatInfoBean);
		 * i.putExtras(bundle); context.startActivity(i);
		 */
	}

}
