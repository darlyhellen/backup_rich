package com.ytdinfo.keephealth.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Color;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;

/**
 * 利用handler和timer实时监听多个edittext，以赋予Button不同的状态
 * 
 * @author jasongao
 *
 */
public class HandlerUtils {

	private static boolean islogining;

	public static Handler useHandler(final EditText ets[], final Button bt) {

		Handler handler = new Handler() {
			@Override
			public void handleMessage(android.os.Message msg) {

				if (msg.what == 0x342) {
						islogining = true;
					bt.setClickable(false);
					bt.setTextColor(Color.parseColor("#66ffffff"));
				} else if (msg.what == 0x341) {
					islogining = false;
				}
				if (!islogining) {
					listenET(ets, bt);
				}
			};
		};
		return handler;
	}

	public static void startTimer(final Handler handler) {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {

			@Override
			public void run() {
				handler.sendEmptyMessage(0x321);
			}
		};
		timer.schedule(task, 0, 100);
	}

	/**
	 * 当有一个edittext为空时，Button为不可点击状态； 当所有的edittext都不为空时，Button为可点击状态
	 * 
	 * @param ets
	 * @param bt
	 */
	public static void listenET(final EditText ets[], final Button bt) {
		for (int i = 0; i < ets.length; i++) {
			if (ets[i].getText().toString().equals("")) {
				bt.setClickable(false);
				bt.setTextColor(Color.parseColor("#66ffffff"));
				break;
			} else {
				bt.setClickable(true);
				bt.setTextColor(Color.parseColor("#ffffffff"));
			}
		}
	}

}
