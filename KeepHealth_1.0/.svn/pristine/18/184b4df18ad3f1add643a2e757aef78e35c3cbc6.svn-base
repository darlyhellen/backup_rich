package com.ytdinfo.keephealth.zhangyuhui.common;

import java.util.ArrayList;

import com.ytdinfo.keephealth.utils.ToastUtil;
import com.ytdinfo.keephealth.zhangyuhui.adapte.IAOrganz;
import com.ytdinfo.keephealth.zhangyuhui.model.IAOrganization;
import com.ytdinfo.keephealth.zhangyuhui.model.IAOrganizationHttp;
import com.ytdinfo.keephealth.zhangyuhui.model.IAOrganization_Departments;
import com.ytdinfo.keephealth.zhangyuhui.model.IARoomName;
import com.ytdinfo.keephealth.zhangyuhui.model.OrgBase;

/**
 * @author Administrator 从服务器获取的数据。
 */
public class IAFindOrganization {
	private static IAOrganizationHttp http = new IAOrganizationHttp();
	private static int stata = 2;

	// 比如采用0未检查，1下一项，2已检查
	public static IAOrganizationHttp getOrganization(IAOrganz orginfo,
			OrgBase base, ArrayList<IARoomName> room) {

		http.setCode(200);
		http.setMsg("");
		ArrayList<IAOrganization> floo = new ArrayList<IAOrganization>();
		IAOrganization organization = new IAOrganization();
		organization.setOrganization_name(orginfo.name);
		organization.setOrganization_id(orginfo.id);
		organization.setOrganization_floor_num(orginfo.floor[0]);

		ArrayList<IAOrganization_Departments> data = new ArrayList<IAOrganization_Departments>();
		for (int j = 0, le = room.size(); j < le; j++) {
			data.add(new IAOrganization_Departments(room.get(j).RoomID, null,
					Integer.parseInt(room.get(j).DepartmentID), stata));
		}
		if (base == null) {
			ToastUtil.showMessage("网络请求失败");
			organization.setOrganization_hasstate(false);
			organization.setOrganization_departments(data);
			floo.add(organization);
			http.setData(floo);
			return http;
		} else {
			organization.setOrganization_hasstate(true);

			// 判断获取到的数据假如没有此字段，则展示原始页面。
			// 剔除不用体检的项目
			if (base.getModel().getAll() != null) {
				for (int j = 0, le = data.size(); j < le; j++) {
					for (int i = 0, lent = base.getModel().getAll().length; i < lent; i++) {
						if (data.get(j).departments_id == Integer.parseInt(base
								.getModel().getAll()[i] + "")) {
							data.get(j).setDepartments_state(0);
							break;
						}
					}
				}
			}
			// 剔除已经体检完成的项目
			if (base.getModel().getDone() != null) {
				for (int j = 0, le = data.size(); j < le; j++) {
					for (int i = 0, lent = base.getModel().getDone().size(); i < lent; i++) {
						if (data.get(j).departments_id == base.getModel()
								.getDone().get(i).getDepartmentID()) {
							data.get(j).setDepartments_state(2);
						}
					}
				}
			}
			if (base.getModel().getNext() != null) {

				for (int j = 0, le = data.size(); j < le; j++) {
					for (int i = 0, lent = base.getModel().getNext().size(); i < lent; i++) {
						String num = base.getModel().getNext().get(i)
								.getRoomID();
						if (num.contains("-")) {
							num = num.substring(0, num.indexOf("-"));
						}
						if (data.get(j).departments_roomnumber == Integer
								.parseInt(num)) {
							data.get(j).setDepartments_state(1);
						}
					}
				}
			}
			organization.setOrganization_departments(data);
			floo.add(organization);
			http.setData(floo);
			return http;

		}

	}

}
