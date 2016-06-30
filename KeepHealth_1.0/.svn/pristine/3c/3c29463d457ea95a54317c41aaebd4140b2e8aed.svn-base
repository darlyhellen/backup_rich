package com.ytdinfo.keephealth.utils;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.ytdinfo.keephealth.app.MyApp;
import com.ytdinfo.keephealth.model.GroupUserInfoBean;
import com.yuntongxun.kitsdk.ECDeviceKit;
import com.yuntongxun.kitsdk.beans.ChatInfoBean;

public class DBUtilsHelper {
	 private 	static DbUtils db ;
	 public DbUtils getDb() {
		return db;
	}
	public void setDb(DbUtils db) {
		DBUtilsHelper.db = db;
	}
	public static DBUtilsHelper instance;

		/**
		 * 单例，返回一个实例
		 * 
		 * @return
		 */
		public static DBUtilsHelper getInstance() {
			
				instance = new DBUtilsHelper();
				if (null!=ECDeviceKit.getInstance().getUserId()) {
					db=DbUtils.create(MyApp.getInstance(), ECDeviceKit.getInstance().getUserId()+"_rayelink.db");
					//db = DbUtils.create(MyApp.getInstance(), SharedPrefsUtil.getValue(Constants.USERID, null));
				}	else {
					db = DbUtils.create(MyApp.getInstance(), "temp.db");
				}
			
			return instance;
		}
	 public   void saveChatinfo(ChatInfoBean chatInfoBean) {
		// TODO Auto-generated method stub
		
		 try {
				db.createTableIfNotExist(ChatInfoBean.class);
				db.saveOrUpdate(chatInfoBean);
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	 public   void saveGroupUserInfo(GroupUserInfoBean groupUserInfoBean) {
		 // TODO Auto-generated method stub
		
		 try {
			 db.createTableIfNotExist(GroupUserInfoBean.class);
			 db.save(groupUserInfoBean);
		 } catch (DbException e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
		 }
	 }
	    public   boolean isOnline() {
	    	try {
				ChatInfoBean chatInfoBean = db.findFirst(Selector.from(ChatInfoBean.class).where("isTimeout","=",false));
			if (null !=chatInfoBean) 
			return true;
	    	
	    	} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return false;
		}
	    
	    public   boolean isHaveOnline() {
	    	try {
				ChatInfoBean chatInfoBean = db.findFirst(Selector.from(ChatInfoBean.class).where("isTimeout","=",false));
			if (null !=chatInfoBean) 
				return true;
	    	} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	return false;
		}
	
}
