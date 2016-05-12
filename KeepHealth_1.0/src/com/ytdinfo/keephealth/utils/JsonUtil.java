package com.ytdinfo.keephealth.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.ytdinfo.keephealth.model.OnlineQuestionUserModel;
import com.ytdinfo.keephealth.model.TBNews;

public class JsonUtil {
	/**
	 * 将bitmap转换成JSONArray
	 * @param feedback
	 * @return
	 */
	public static JSONArray bitmapTOjsonArray(Bitmap feedback) {
		JSONArray jsonArray = new JSONArray();

			try {
				String imgStr = ImageTools.getImageStr(feedback);
				jsonArray.put(imgStr);

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		return jsonArray;
	}

	/**
	 * 将json转换为TBNews对象
	 * 
	 * @param json_str
	 * @return
	 */
	public static TBNews jsonTOtbnews(String json_str) {
		TBNews tbNews = new TBNews();
		try {
			JSONObject jsonObject = new JSONObject(json_str);
			tbNews.setIcon(jsonObject.getString("icon"));
			tbNews.setTitle(jsonObject.getString("title"));
			tbNews.setUrl(jsonObject.getString("url"));
			tbNews.setDateCreate(System.currentTimeMillis()+"");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return tbNews;
	}

	/**
	 * 将json转换为List<OnlineQuestionUserModel>
	 * 
	 * @param json_str
	 * @return
	 */
	public static List<OnlineQuestionUserModel> jsonTOonlinequesusermodel(
			String json_str) {
		List<OnlineQuestionUserModel> list = new ArrayList<OnlineQuestionUserModel>();
		try {
			JSONArray jsonArray  = new JSONArray(json_str);
			for(int i=0;i<jsonArray.length();i++){
				JSONObject jsonObj = (JSONObject) jsonArray.get(i);
				OnlineQuestionUserModel model = new Gson().fromJson(jsonObj.toString(), OnlineQuestionUserModel.class);
				list.add(model);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return list;

	}
	/**
	 * 将json转换为List<Map<>>
	 * 
	 * @param json_str
	 * @return
	 */
	public static List<Map<String, Object>> jsonTOmap_onlinequesusermodel(
			String json_str) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			JSONArray jsonArray  = new JSONArray(json_str);
			for(int i=0;i<jsonArray.length();i++){
				JSONObject jsonObj = (JSONObject) jsonArray.get(i);
				OnlineQuestionUserModel model = new Gson().fromJson(jsonObj.toString(), OnlineQuestionUserModel.class);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("named", model.getNamed());
				map.put("sex", model.getSex());
				map.put("age", model.getAge());
				list.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return list;

	}
	
public static List<String> parseJson_images(String jsonStr_images) {
	List<String> list = new ArrayList<String>();
		try {
			JSONArray jsonArray = new JSONArray(jsonStr_images);
			for(int i=0;i<jsonArray.length();i++){
				list.add(jsonArray.get(i).toString());
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
}
