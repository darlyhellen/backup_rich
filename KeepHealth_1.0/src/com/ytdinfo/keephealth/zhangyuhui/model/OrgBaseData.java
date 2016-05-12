package com.ytdinfo.keephealth.zhangyuhui.model;

import java.util.ArrayList;

public class OrgBaseData {

	private int[] All;
	private ArrayList<OrgBaseDone> Done;
	private ArrayList<OrgBaseDone> Next;
	private String msg;

	public int[] getAll() {
		return All;
	}

	public void setAll(int[] all) {
		All = all;
	}

	public ArrayList<OrgBaseDone> getDone() {
		return Done;
	}

	public void setDone(ArrayList<OrgBaseDone> done) {
		Done = done;
	}

	public ArrayList<OrgBaseDone> getNext() {
		return Next;
	}

	public void setNext(ArrayList<OrgBaseDone> next) {
		Next = next;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
