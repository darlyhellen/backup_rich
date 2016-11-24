package com.ytdinfo.keephealth.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHelper extends SQLiteOpenHelper {
	private final String CREATE_TABLE_SQL = "create table tb_news("
			+ "id integer primary key," + "title varchar(50),"
			+ "desc varchar(200)," + "icon varchar(100),"
			+ "createDate varchar(100)," + "url varchar(100),"
			+ "msg_id varchar(100))";

	public MyDBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		if (oldVersion == 1 && newVersion == 2) {
			// 从版本1到版本2时，增加了一个字段
			String sql = "alter table tb_news add msg_id varchar(100) NULL";
			db.execSQL(sql);
		}
	}
}
