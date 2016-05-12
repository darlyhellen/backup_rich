package com.ytdinfo.keephealth.model;

import java.io.Serializable;

import com.google.gson.Gson;


public class GroupUserInfoBean  implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	/* {
         "UserID": 119,
         "Face": "http://test.rayelink.com/Content/UploadFile/Admin/photoImageUrl/84d53439-0429-47b2-8808-d5fc7fde7c34.png",
         "UserName": "姓名",
         "voipAccount": "89077100000013"
     }*/
	private int UserID;
	private String Face;
	private String UserName;
	private String voipAccount;
	private boolean IsDoctor;
	
	public boolean isIsDoctor() {
		return IsDoctor;
	}

	public void setIsDoctor(boolean isDoctor) {
		IsDoctor = isDoctor;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return new Gson().toJson(this);
	}
	public int getUserID() {
		return UserID;
	}

	public void setUserID(int userID) {
		UserID = userID;
	}

	public String getFace() {
		return Face;
	}

	public void setFace(String face) {
		Face = face;
	}


	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getVoipAccount() {
		return voipAccount;
	}

	public void setVoipAccount(String voipAccount) {
		this.voipAccount = voipAccount;
	}
	


	
	
	
}
