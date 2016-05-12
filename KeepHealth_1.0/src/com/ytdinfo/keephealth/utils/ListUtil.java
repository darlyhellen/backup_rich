package com.ytdinfo.keephealth.utils;

import java.util.List;

import com.ytdinfo.keephealth.model.OnlineQuestionUserModel;

public class ListUtil {
	/**
	 * 
	 * @param list
	 * @param key  是list中某个对象的一个属性
	 * @return		返回该对象在list中的位置
	 */
public static int getPosition(List<OnlineQuestionUserModel> list,String key){
	for(int i=0;i<list.size();i++){
		OnlineQuestionUserModel obj = list.get(i);
		if(key.equals(obj.getNamed())){
			return i;
		}
	}
	return -1;
}
}
