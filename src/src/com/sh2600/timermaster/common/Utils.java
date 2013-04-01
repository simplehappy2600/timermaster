package com.sh2600.timermaster.common;

import android.content.Context;
import android.content.Intent;

public class Utils {
	
	public static Intent buildIntent(Context packageContext, Class<?> cls, String action){
		
		Intent intent = new Intent(packageContext, cls);
		intent.setAction(action);
		
		return intent;
	}
}
