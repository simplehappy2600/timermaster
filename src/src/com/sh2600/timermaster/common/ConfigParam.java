package com.sh2600.timermaster.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ConfigParam {
	
	public static final String pref_key_quit_time 			= "pref_key_quit_time";
	public static final String pref_key_headset_enable		= "pref_key_headset_enable";
	public static final String pref_key_headset_way 		= "pref_key_headset_way";
	public static final String pref_key_interval_enable 	= "pref_key_interval_enable";
	public static final String pref_key_interval_interval 	= "pref_key_interval_interval";
	public static final String pref_key_interval_starttime	= "pref_key_interval_starttime";
	public static final String pref_key_interval_stoptime 	= "pref_key_interval_stoptime";	
	
	public String autoQuitTime;
	
	public String headset_way 			= "double";
	public boolean headset_enable 		= true;
	public boolean interval_enable 		= false;
	public int interval_interval 		= 10;
	public String interval_starttime	= "07:00";
	public String interval_stoptime 	= "08:00";	
	public String language 				= "cn";
	
	private Context context;
	
	public ConfigParam(Context context){
		this.context = context;
		
		init();
	}
	
	public void init(){
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
		
		this.headset_way = sharedPreferences.getString(pref_key_headset_way, "double");
		this.headset_enable = sharedPreferences.getBoolean(pref_key_headset_enable, false);
		this.interval_enable = sharedPreferences.getBoolean(this.pref_key_interval_enable, false);
		this.interval_interval= Integer.parseInt(sharedPreferences.getString(pref_key_interval_interval, "10"));
		this.interval_starttime = sharedPreferences.getString(this.pref_key_interval_starttime, null);
		this.interval_stoptime = sharedPreferences.getString(this.pref_key_interval_stoptime, null);
		this.autoQuitTime = readParamAutoQuitTime(sharedPreferences);	
					
	}
	
	public String readParamAutoQuitTime(SharedPreferences sharedPreferences){
		String r = sharedPreferences.getString(pref_key_quit_time, null);
		if ("00:00".equalsIgnoreCase(r)){
			r = null;
		}
		return r;
	}	
}
