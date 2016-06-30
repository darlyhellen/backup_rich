package com.umeng.comm.custom;

public class AppAdd {
	
	private static AppAdd instance;
	
	private static AppInterface mAppInterface;
	
	public static AppAdd getInstance()
	{
		if(instance==null)
		{
			 instance=new AppAdd();
		}
		return instance;
	}
	
	
	public static void setAppInterface(AppInterface inteface)
	{
		mAppInterface=inteface;
	}

	
	public static AppInterface getAppInterface()
	{
		return mAppInterface;
	}
	

}
