package com.ytdinfo.keephealth.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.ytdinfo.keephealth.app.MyApp;

public class SharedPrefsUtil {  
    public final static String SETTING = "ytdinfo_preference";  
    private static final Context context = MyApp.getInstance().getApplicationContext();
    public static void putValue(String key, int value) {  
         Editor sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();  
         sp.putInt(key, value);  
         sp.commit();  
    }  
  
    public static void putValue(String key,Long value) {  
        Editor sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();  
        sp.putLong(key, value);
       
        sp.commit();  
   }  
    public static void putValue(String key, boolean value) {  
         Editor sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();  
         sp.putBoolean(key, value);  
         sp.commit();  
    }  
    public static void putValue(String key, String value) {  
         Editor sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();  
         sp.putString(key, value);  
         sp.commit();  
    }  
    public static int getValue(String key, int defValue) {  
        SharedPreferences sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);  
        int value = sp.getInt(key, defValue);  
        return value;  
    }  
    public static long getValue(String key, long defValue) {  
        SharedPreferences sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);  
        long value = sp.getLong(key, defValue);  
        return value;  
    } 
    public static boolean getValue(String key, boolean defValue) {  
        SharedPreferences sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);  
        boolean value = sp.getBoolean(key, defValue);  
        return value;  
    }  
    public static String getValue(String key, String defValue) {  
        SharedPreferences sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);  
        String value = sp.getString(key, defValue);  
        return value;  
    }  
    public static void remove(String key){
    	 SharedPreferences sp =  context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
    	 Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }
} 
