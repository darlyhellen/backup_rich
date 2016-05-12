package com.ytdinfo.keephealth.zhangyuhui.model;

import java.util.ArrayList;

public class IAOrganizationHttp {
	public int code;
	public String msg;
	public ArrayList<IAOrganization> data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public ArrayList<IAOrganization> getData() {
		return data;
	}

	public void setData(ArrayList<IAOrganization> data) {
		this.data = data;
	}



}
