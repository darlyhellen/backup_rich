package com.ytdinfo.keephealth.model;

import java.io.Serializable;

import com.google.gson.Gson;


public class UserGroupBean implements Serializable {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*{
        "count": "11",
        "dateCreated": "1440725472615",
        "groupId": "g8907712",
        "name": "风湿患者群",
        "permission": "0",
        "type": "2"
      }*/
	private int id;
public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
private String count;
private String dateCreated;
private String groupId;
private String name;
private String permission;
private String type;
@Override
public String toString() {
	// TODO Auto-generated method stub
	return new Gson().toJson(this);
}
public String getCount() {
	return count;
}
public void setCount(String count) {
	this.count = count;
}
public String getDateCreated() {
	return dateCreated;
}
public void setDateCreated(String dateCreated) {
	this.dateCreated = dateCreated;
}
public String getGroupId() {
	return groupId;
}
public void setGroupId(String groupId) {
	this.groupId = groupId;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getPermission() {
	return permission;
}
public void setPermission(String permission) {
	this.permission = permission;
}
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}

	
}
