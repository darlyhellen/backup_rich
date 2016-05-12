package com.ytdinfo.keephealth.model;

import com.google.gson.Gson;

public class OnlineQuestionUserModel {
	private String named;
	private String sex;
	private int age;
	
	@Override
	public String toString(){
		return new Gson().toJson(this);
	}

	public String getNamed() {
		return named;
	}

	public void setNamed(String named) {
		this.named = named;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
