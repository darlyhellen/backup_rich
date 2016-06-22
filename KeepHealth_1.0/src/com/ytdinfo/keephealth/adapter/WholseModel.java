/**上午10:38:17
 * @author zhangyh2
 * WholseModel.java
 * TODO
 */
package com.ytdinfo.keephealth.adapter;

import java.io.Serializable;

/**
 * @author zhangyh2 WholseModel 上午10:38:17 TODO
 */
public class WholseModel implements Serializable {

	/**
	 * 上午11:08:58 TODO
	 */
	private static final long serialVersionUID = 1L;

	private String id;
	private String img_url;
	private String title;
	private String zhaiyao;
	private String update_time;
	private String add_time;

	/**
	 * 下午2:09:35
	 * 
	 * @author zhangyh2
	 */
	public WholseModel() {
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getZhaiyao() {
		return zhaiyao;
	}

	public void setZhaiyao(String zhaiyao) {
		this.zhaiyao = zhaiyao;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
