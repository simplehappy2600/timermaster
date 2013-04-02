package com.sh2600.timermaster.common;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;

public class Utils {
	
	public static Intent buildIntent(Context packageContext, Class<?> cls, String action){
		
		Intent intent = new Intent(packageContext, cls);
		intent.setAction(action);
		
		return intent;
	}
	
	public static Intent buildCmdIntent(Context packageContext, Class<?> cls, String action, int cmd){
		
		Intent intent = new Intent(packageContext, cls);
		intent.setAction(action);
		intent.putExtra(CVal.Cmd.cmdtype, cmd);
		
		return intent;
	}
	
	public static String getDateString(Date date){		
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}
}
