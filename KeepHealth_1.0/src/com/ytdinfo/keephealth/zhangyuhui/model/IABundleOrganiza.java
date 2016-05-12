package com.ytdinfo.keephealth.zhangyuhui.model;

import com.ytdinfo.keephealth.zhangyuhui.view.ichnography.IABaseFrame;


/**
 * @author Administrator 绑定的机构平面图
 */
public class IABundleOrganiza {
	public int id;
	public int floor;
	public IABaseFrame layout;

	public IABundleOrganiza(int id,int floor, IABaseFrame layout) {
		super();
		this.id = id;
		this.floor = floor;
		this.layout = layout;
	}

}
