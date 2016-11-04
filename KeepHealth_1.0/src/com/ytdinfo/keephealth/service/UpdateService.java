package com.ytdinfo.keephealth.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.lidroid.xutils.util.LogUtils;
import com.ytdinfo.keephealth.R;
import com.ytdinfo.keephealth.app.Constants;
import com.ytdinfo.keephealth.ui.MainActivity;
import com.ytdinfo.keephealth.utils.DigestUtils;

/**
 * @author zhangyh2 a $ 下午2:12:42 TODO
 */
public class UpdateService extends Service {
	private NotificationManager nm;
	private Notification notification;
	private File tempFile = null;
	private boolean cancelUpdate = false;
	private MyHandler myHandler;
	private int download_precent = 0;
	private RemoteViews views;
	private int notificationId = 1234;
	private boolean isfirstDown;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) {
		// to check wifi.
		if (intent != null) {
			initService(intent);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 下午4:28:08
	 * 
	 * @author zhangyh2 TODO 初始化通知，初始化
	 */
	@SuppressWarnings("deprecation")
	private void initService(Intent intent) {
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notification = new Notification();
		notification.icon = android.R.drawable.stat_sys_download;
		// notification.icon=android.R.drawable.stat_sys_download_done;
		notification.tickerText = getString(R.string.app_name) + "更新";
		notification.when = System.currentTimeMillis();
		notification.defaults = Notification.DEFAULT_LIGHTS;

		// 设置任务栏中下载进程显示的views
		views = new RemoteViews(getPackageName(), R.layout.update_service);
		notification.contentView = views;

		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, MainActivity.class), 0);
		notification.setLatestEventInfo(this, "", "", contentIntent);

		// 将下载任务添加到任务栏中
		nm.notify(notificationId, notification);

		myHandler = new MyHandler(Looper.myLooper(), this);

		// 初始化下载任务内容views
		Message message = myHandler.obtainMessage(3, 0);
		myHandler.sendMessage(message);

		// 启动线程开始执行下载任务
		if (!isfirstDown) {
			isfirstDown = true;
			LogUtils.i(intent.getStringExtra("url") + "启动Service进行下载");

			downFile(intent.getStringExtra("url"),
					intent.getIntExtra("version", 0),
					intent.getStringExtra("desMd5"));
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// 下载更新文件
	private void downFile(final String url, final int version,
			final String desMd5) {
		if (url == null) {
			return;
		}

		new Thread() {
			@Override
			public void run() {
				// 先获取文件长度然后在获取文件流,文件长度获取失败，则不进行下载
				try {
					URL urls = new URL(url);
					HttpURLConnection connection = (HttpURLConnection) urls
							.openConnection();
					connection.setRequestMethod("GET");
					if (HttpStatus.SC_OK == connection.getResponseCode()) {
						int fileSize = connection.getContentLength();
						connection.disconnect();
						download(url, version, fileSize, desMd5);
					} else {
						Message message = myHandler
								.obtainMessage(4, "下载更新文件失败");
						myHandler.sendMessage(message);
					}
				} catch (Exception e) {
					Message message = myHandler.obtainMessage(4, "下载更新文件失败");
					myHandler.sendMessage(message);
				}
			}
		}.start();
	}

	/**
	 * 上午9:45:16
	 * 
	 * @author zhangyh2 TODO
	 */
	private void download(String url, int version, int length, String desMd5) {
		HttpURLConnection connection = null;
		try {
			LogUtils.i("新版本文件长度" + length);
			URL connetURL = new URL(url);
			connection = (HttpURLConnection) connetURL.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Connection", "Keep-Alive");
			File rootFile = new File(Constants.STORAGE_ROOT_DIR, "download");
			if (!rootFile.exists() && !rootFile.isDirectory())
				rootFile.mkdir();
			tempFile = new File(Constants.STORAGE_ROOT_DIR, "/download/"
					+ version + "comytdinfokeephealth.apk");
			long count = 0;
			int precent = 0;

			if (!tempFile.exists()) {
				deleteFolderFile(rootFile.getAbsolutePath(), false);
				tempFile.createNewFile();
			}
			count = tempFile.length();
			if (count > length) {
				deleteFolderFile(rootFile.getAbsolutePath(), false);
				tempFile.createNewFile();
				count = 0;
			} else if (count == length) {
				// 判断本地生成的MD5是否和服务器一致。一致则进行安装，否则提示下载失败？还是重新下载？
				String md5 = DigestUtils.getFileMD5(tempFile);
				LogUtils.i(md5);
				if (md5.equalsIgnoreCase(desMd5)) {
					Message message = myHandler.obtainMessage(2, tempFile);
					myHandler.sendMessage(message);
				} else {
					Message message = myHandler.obtainMessage(5, "MD5匹配失败");
					myHandler.sendMessage(message);
					tempFile.delete();
				}
				return;
			}
			// 设置范围，格式为Range：bytes x-y;
			connection.setRequestProperty("Range", "bytes=" + count + "-"
					+ length);
			InputStream is = connection.getInputStream();
			if (is != null) {

				RandomAccessFile raf = new RandomAccessFile(tempFile, "rw");
				LogUtils.i(count + "--" + length);
				raf.seek(count);
				int temp = 0;
				byte[] buffer = new byte[2048];
				while ((temp = is.read(buffer)) != -1) {
					raf.write(buffer, 0, temp);
					count += temp;
					precent = (int) (((double) count / length) * 100);

					// 每下载完成5%就通知任务栏进行修改下载进度
					if (precent - download_precent >= 1) {
						download_precent = precent;
						Message message = myHandler.obtainMessage(3, precent);
						myHandler.sendMessage(message);
					}
				}
				raf.close();
				is.close();
			}

			// 判断本地生成的MD5是否和服务器一致。一致则进行安装，否则提示下载失败？还是重新下载？
			String md5 = DigestUtils.getFileMD5(tempFile);
			LogUtils.i(md5);
			if (md5.equalsIgnoreCase(desMd5)) {
				Message message = myHandler.obtainMessage(2, tempFile);
				myHandler.sendMessage(message);
			} else {
				Message message = myHandler.obtainMessage(5, "MD5匹配失败");
				myHandler.sendMessage(message);
				tempFile.delete();
			}
		} catch (ClientProtocolException e) {
			Message message = myHandler.obtainMessage(4, "下载更新文件失败");
			myHandler.sendMessage(message);
		} catch (IOException e) {
			Message message = myHandler.obtainMessage(4, "下载更新文件失败");
			myHandler.sendMessage(message);
		} catch (Exception e) {
			Message message = myHandler.obtainMessage(4, "下载更新文件失败");
			myHandler.sendMessage(message);
		}
	}

	/**
	 * 删除指定目录下文件及目录
	 * 
	 * @param deleteThisPath
	 * @param filepath
	 * @return
	 */
	public void deleteFolderFile(String filePath, boolean deleteThisPath) {
		if (!TextUtils.isEmpty(filePath)) {
			try {
				File file = new File(filePath);
				if (file.isDirectory()) {// 处理目录
					File files[] = file.listFiles();
					for (int i = 0; i < files.length; i++) {
						deleteFolderFile(files[i].getAbsolutePath(), true);
					}
				}
				if (deleteThisPath) {
					if (!file.isDirectory()) {// 如果是文件，删除
						file.delete();
					} else {// 目录
						if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
							file.delete();
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// 安装下载后的apk文件
	private void Instanll(File file, Context context) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	class MyHandler extends Handler {
		private Context context;

		public MyHandler(Looper looper, Context c) {
			super(looper);
			this.context = c;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg != null) {
				switch (msg.what) {
				case 0:
					Toast.makeText(context, msg.obj.toString(),
							Toast.LENGTH_SHORT).show();
					break;
				case 1:
					break;
				case 2:
					// 下载完成后清除所有下载信息，执行安装提示
					download_precent = 0;
					nm.cancel(notificationId);
					Instanll((File) msg.obj, context);
					// 停止掉当前的服务
					stopSelf();
					break;
				case 3:

					// 更新状态栏上的下载进度信息
					views.setTextViewText(R.id.tvProcess, "已下载"
							+ download_precent + "%");
					views.setProgressBar(R.id.pbDownload, 100,
							download_precent, false);
					notification.contentView = views;
					nm.notify(notificationId, notification);
					break;
				case 4:
					isfirstDown = false;
					nm.cancel(notificationId);
					Toast.makeText(context, "版本更新失败，请检查网络。", Toast.LENGTH_SHORT)
							.show();
					stopSelf();
					break;
				case 5:
					isfirstDown = false;
					nm.cancel(notificationId);
					Toast.makeText(context, "安装包完整性检验失败，请重新下载。",
							Toast.LENGTH_SHORT).show();
					stopSelf();
					break;
				}
			}
		}
	}

}