package com.sh2600.timermaster.service;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
	
	private PendingIntent mAlarmSender;
	
	@Override
    public void onReceive(Context context, Intent intent) {

		//是否开始启动
		
//        mAlarmSender = PendingIntent.getService(context, 0, new Intent(context,
//                RefreshDataService.class), 0);
//        
//        long firstTime = SystemClock.elapsedRealtime();
//        AlarmManager am = (AlarmManager) context
//                .getSystemService(Activity.ALARM_SERVICE);
//        am.cancel(mAlarmSender);
//        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
//                30 * 60 * 1000, mAlarmSender);
    }

}
