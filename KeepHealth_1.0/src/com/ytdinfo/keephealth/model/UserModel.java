package com.ytdinfo.keephealth.model;

import java.io.Serializable;

import com.google.gson.Gson;

public class UserModel implements Serializable {

	private int Age;
	private boolean PhotoIsEdit;
	private String[] SYS_DepartmentDoctor;
	private String Mobilephone;
	private String Password;
	private String Online;
	private String UserAccount;
	private String UserName;
	private String CreateDate;
	private String subToken;
	private String statusCode;
	private String CreateUser;
	private String[] SYS_DoctorAssistantid;
	private String subAccountSid;
	private String[] SYS_RoleUser;
	private int ID;
	private int Pid;
	private String UpdateUser;
	private String UserType;
	private String HeadPicture;
	private String[] SYS_DoctorAssistantid1;
	private String voipAccount;
	private String Marriage;
	private String statusMsg;
	private String Addition2;
	private String Status;
	private String Addition1;
	private String UserSex;
	private String Email;
	private String dateCreated;
	private String UpdateDate;
	private String voipPwd;
	private String IDcard;

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
	public int getAge() {
		return Age;
	}

	public void setAge(int age) {
		Age = age;
	}
	
	public boolean isPhotoIsEdit() {
		return PhotoIsEdit;
	}

	public void setPhotoIsEdit(boolean photoIsEdit) {
		PhotoIsEdit = photoIsEdit;
	}
	
	public String[] getSYS_DepartmentDoctor() {
		return SYS_DepartmentDoctor;
	}

	public void setSYS_DepartmentDoctor(String[] sYS_DepartmentDoctor) {
		SYS_DepartmentDoctor = sYS_DepartmentDoctor;
	}

	public String getMobilephone() {
		return Mobilephone;
	}

	public void setMobilephone(String mobilephone) {
		Mobilephone = mobilephone;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public String getOnline() {
		return Online;
	}

	public void setOnline(String online) {
		Online = online;
	}

	public String getUserAccount() {
		return UserAccount;
	}

	public void setUserAccount(String userAccount) {
		UserAccount = userAccount;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getCreateDate() {
		return CreateDate;
	}

	public void setCreateDate(String createDate) {
		CreateDate = createDate;
	}

	public String getSubToken() {
		return subToken;
	}

	public void setSubToken(String subToken) {
		this.subToken = subToken;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getCreateUser() {
		return CreateUser;
	}

	public void setCreateUser(String createUser) {
		CreateUser = createUser;
	}

	public String[] getSYS_DoctorAssistantid() {
		return SYS_DoctorAssistantid;
	}

	public void setSYS_DoctorAssistantid(String[] sYS_DoctorAssistantid) {
		SYS_DoctorAssistantid = sYS_DoctorAssistantid;
	}

	public String getSubAccountSid() {
		return subAccountSid;
	}

	public void setSubAccountSid(String subAccountSid) {
		this.subAccountSid = subAccountSid;
	}

	public String[] getSYS_RoleUser() {
		return SYS_RoleUser;
	}

	public void setSYS_RoleUser(String[] sYS_RoleUser) {
		SYS_RoleUser = sYS_RoleUser;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public int getPid() {
		return Pid;
	}

	public void setPid(int pid) {
		Pid = pid;
	}

	public String getUpdateUser() {
		return UpdateUser;
	}

	public void setUpdateUser(String updateUser) {
		UpdateUser = updateUser;
	}

	public String getUserType() {
		return UserType;
	}

	public void setUserType(String userType) {
		UserType = userType;
	}

	public String getHeadPicture() {
		return HeadPicture;
	}

	public void setHeadPicture(String headPicture) {
		HeadPicture = headPicture;
	}

	public String[] getSYS_DoctorAssistantid1() {
		return SYS_DoctorAssistantid1;
	}

	public void setSYS_DoctorAssistantid1(String[] sYS_DoctorAssistantid1) {
		SYS_DoctorAssistantid1 = sYS_DoctorAssistantid1;
	}

	public String getVoipAccount() {
		return voipAccount;
	}

	public void setVoipAccount(String voipAccount) {
		this.voipAccount = voipAccount;
	}

	public String getMarriage() {
		return Marriage;
	}

	public void setMarriage(String marriage) {
		Marriage = marriage;
	}

	public String getStatusMsg() {
		return statusMsg;
	}

	public void setStatusMsg(String statusMsg) {
		this.statusMsg = statusMsg;
	}

	public String getAddition2() {
		return Addition2;
	}

	public void setAddition2(String addition2) {
		Addition2 = addition2;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getAddition1() {
		return Addition1;
	}

	public void setAddition1(String addition1) {
		Addition1 = addition1;
	}

	public String getUserSex() {
		return UserSex;
	}

	public void setUserSex(String userSex) {
		UserSex = userSex;
	}

	public String getEmail() {
		return Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getUpdateDate() {
		return UpdateDate;
	}

	public void setUpdateDate(String updateDate) {
		UpdateDate = updateDate;
	}

	public String getVoipPwd() {
		return voipPwd;
	}

	public void setVoipPwd(String voipPwd) {
		this.voipPwd = voipPwd;
	}

	public String getIDcard() {
		return IDcard;
	}

	public void setIDcard(String iDcard) {
		IDcard = iDcard;
	}

}
