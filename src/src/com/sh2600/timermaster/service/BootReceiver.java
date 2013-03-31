package com.sh2600.timermaster.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	
	@Override
    public void onReceive(Context context, Intent intent) {
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean boot = sharedPreferences.getBoolean("pref_key_boot_enable", false);		
		if (boot){
			Intent myIntent = new Intent(context, TimerService.class);
			myIntent.setAction("com.sh2600.timermaster.service.TimerService");
			context.startService(myIntent);
		}
		else{
			Log.i("BootReceiver", "not start service");
		}		
    }

}
