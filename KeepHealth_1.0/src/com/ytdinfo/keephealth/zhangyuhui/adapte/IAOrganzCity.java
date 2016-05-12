package com.ytdinfo.keephealth.zhangyuhui.adapte;

import java.util.ArrayList;

/**
 * @author Administrator
 *	城市列表
 */
public class IAOrganzCity {
	public String city;
	public ArrayList<IAOrganz> organzs;

	public IAOrganzCity(String city, ArrayList<IAOrganz> organzs) {
		super();
		this.city = city;
		this.organzs = organzs;
	}

}
