package com.ytdinfo.keephealth.zhangyuhui.model;

public class IAOrganization_Departments {
	public String departments_name;
	public int departments_id;
	/**
	 * 0-未体检，1-下一项，2-已经体检，3-不用体检，4-已经体检但同科的其他房间，5-其他。
	 */
	public int departments_state;
	public int departments_roomnumber;

	public IAOrganization_Departments(int departments_roomnumber,
			String departments_name, int departments_id, int departments_state) {
		super();
		this.departments_roomnumber = departments_roomnumber;
		this.departments_name = departments_name;
		this.departments_id = departments_id;
		this.departments_state = departments_state;
	}

	public String getDepartments_name() {
		return departments_name;
	}

	public void setDepartments_name(String departments_name) {
		this.departments_name = departments_name;
	}

	public int getDepartments_id() {
		return departments_id;
	}

	public void setDepartments_id(int departments_id) {
		this.departments_id = departments_id;
	}

	public int getDepartments_state() {
		return departments_state;
	}

	public void setDepartments_state(int departments_state) {
		this.departments_state = departments_state;
	}

	public int getDepartments_roomnumber() {
		return departments_roomnumber;
	}

	public void setDepartments_roomnumber(int departments_roomnumber) {
		this.departments_roomnumber = departments_roomnumber;
	}

}
