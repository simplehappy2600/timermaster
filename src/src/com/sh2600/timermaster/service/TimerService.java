package com.sh2600.timermaster.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;

import com.sh2600.timermaster.ControlActivity;
import com.sh2600.timermaster.R;

public class TimerService extends Service implements OnPreferenceChangeListener{
	
	static final String tag = TimerService.class.getSimpleName();
	
	private static int MOOD_NOTIFICATIONS = R.layout.activity_control;
	
	MediaButtonReceiver mediaButtonReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(tag, "onCreate");
		//初始化Handler对象
		MyHandler handler = new MyHandler();  
		//初始化媒体(耳机)广播对象.  
		mediaButtonReceiver = new MediaButtonReceiver(handler);		
		
		//注册媒体(耳机)广播对象  
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);  
		intentFilter.setPriority(100);
//		intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);

		registerReceiver(mediaButtonReceiver, intentFilter);
		
		showNotification(R.drawable.ic_launcher, "time master");
	}
	
	@Override
	public void onDestroy() {	
		super.onDestroy();
		Log.d(tag, "onDestroy");
		unregisterReceiver(this.mediaButtonReceiver);
		
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(MOOD_NOTIFICATIONS);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(tag, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}
	
	public class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {		
			super.handleMessage(msg);
		}
	}
	
    private void showNotification(int moodId, String text) {

    	Log.d(tag, "showNotification");
    	
        Notification notification = new Notification(moodId, null, System.currentTimeMillis());

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ControlActivity.class), 0);

        notification.setLatestEventInfo(this, "test", text, contentIntent);

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(MOOD_NOTIFICATIONS, notification);
    }

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {		
		
		String s = preference.getSharedPreferences().getString("preferences_stop_time", "");
		Log.v(tag, s);
		
		return false;
	}	

}
