package com.ytdinfo.keephealth.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.ytdinfo.keephealth.model.TBNews;

public class DBUtil {
	private Context mContext;
	private MyDBHelper dbHelper;
	private SQLiteDatabase db;
	// 数据库名字
	private String DBName = "keephealthV2.db";
	// 表的名字
	private String TBName = "tb_news";

	public DBUtil(Context context) {
		mContext = context;
		dbHelper = new MyDBHelper(mContext, DBName, null, 2);
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * 插入
	 */
	public void insert(TBNews tbNews) {
		String sql = "insert into tb_news(title,desc,icon,url,createDate,msg_id) values(?,?,?,?,?,?)";
		db.execSQL(sql, new Object[] { tbNews.getTitle(), tbNews.getDesc(),
				tbNews.getIcon(), tbNews.getUrl(),tbNews.getDateCreate(),tbNews.getMsg_id()});
	}

	/**
	 * 查询所有的
	 * 
	 * @return
	 */
	public List<TBNews> query() {
		List<TBNews> list = new ArrayList<TBNews>();

		String sql = "select * from tb_news";
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			TBNews tbNews = new TBNews();
			// 取数据
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String title = cursor.getString(cursor.getColumnIndex("title"));
			String desc = cursor.getString(cursor.getColumnIndex("desc"));
			String icon = cursor.getString(cursor.getColumnIndex("icon"));
			String url = cursor.getString(cursor.getColumnIndex("url"));
			String dateCreated=cursor.getString(cursor.getColumnIndex("createDate"));
			String msg_id=cursor.getString(cursor.getColumnIndex("msg_id"));
			
			// 存放到TBNews对象中
			tbNews.setId(id);
			tbNews.setTitle(title);
			tbNews.setDesc(desc);
			tbNews.setIcon(icon);
			tbNews.setUrl(url);
			tbNews.setDateCreate(dateCreated);
			tbNews.setMsg_id(msg_id);
			list.add(tbNews);
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}
	
	public TBNews queryFirst() {
		TBNews tbnews =null;

		String sql = "select * from tb_news order by id desc limit 0,1";
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			// 取数据
			tbnews=new TBNews();
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String title = cursor.getString(cursor.getColumnIndex("title"));
			String desc = cursor.getString(cursor.getColumnIndex("desc"));
			String icon = cursor.getString(cursor.getColumnIndex("icon"));
			String url = cursor.getString(cursor.getColumnIndex("url"));
			String dateCreated=cursor.getString(cursor.getColumnIndex("createDate"));
			String msg_id=cursor.getString(cursor.getColumnIndex("msg_id"));
			// 存放到TBNews对象中
			tbnews.setId(id);
			tbnews.setTitle(title==null?"":title);
			tbnews.setDesc(desc==null?"":desc);
			tbnews.setIcon(icon==null?"":icon);
			tbnews.setUrl(url==null?"":url);
			tbnews.setDateCreate(dateCreated==null?"":dateCreated);
			tbnews.setMsg_id(msg_id==null?"":msg_id);
		}
		if (cursor != null) {
			cursor.close();
		}
		return tbnews;
	}
	
	
	public TBNews queryFirst( String temPid) {
		TBNews tbnews =null;

		String sql = "select * from tb_news where msg_id="+temPid +" order by id desc limit 0,1";
		Cursor cursor = db.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			// 取数据
			tbnews=new TBNews();
			int id = cursor.getInt(cursor.getColumnIndex("id"));
			String title = cursor.getString(cursor.getColumnIndex("title"));
			String desc = cursor.getString(cursor.getColumnIndex("desc"));
			String icon = cursor.getString(cursor.getColumnIndex("icon"));
			String url = cursor.getString(cursor.getColumnIndex("url"));
			String dateCreated=cursor.getString(cursor.getColumnIndex("createDate"));
			String msg_id=cursor.getString(cursor.getColumnIndex("msg_id"));
			// 存放到TBNews对象中
			tbnews.setId(id);
			tbnews.setTitle(title==null?"":title);
			tbnews.setDesc(desc==null?"":desc);
			tbnews.setIcon(icon==null?"":icon);
			tbnews.setUrl(url==null?"":url);
			tbnews.setDateCreate(dateCreated==null?"":dateCreated);
			tbnews.setMsg_id(msg_id==null?"":msg_id);
		}
		if (cursor != null) {
			cursor.close();
		}
		return tbnews;
	}

}
