package com.sh2600.timermaster.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sh2600.timermaster.ControlActivity;
import com.sh2600.timermaster.R;

public class TimerService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener{
	
	private static final String tag = TimerService.class.getSimpleName();
	
	private static int MOOD_NOTIFICATIONS = R.layout.activity_control;
	
	private AlarmManager alarmManager;
	
	private MediaButtonReceiver mediaButtonReceiver;
	
	private long lastPlayTime = 0;
	private int playInterval = 8*1000;
	
	private long lastHeadsetClickTime = 0;
	private int headsetClickInterval = 300;	
	private String autoQuitTime;
	
	private SharedPreferences sharedPreferences;
	
	private String headset_way = "double";
	private boolean headset_enable;
	private boolean interval_enable;
	private int interval_interval;
	private String interval_starttime;
	private String interval_stoptime;
	
	private final String pref_key_quit_time 		= "pref_key_quit_time";
	private final String pref_key_headset_enable	= "pref_key_headset_enable";
	private final String pref_key_headset_way 		= "pref_key_headset_way";
	private final String pref_key_interval_enable 	= "pref_key_interval_enable";
	private final String pref_key_interval_interval = "pref_key_interval_interval";
	private final String pref_key_interval_starttime= "pref_key_interval_starttime";
	private final String pref_key_interval_stoptime = "pref_key_interval_stoptime";	
	
	private TimerTask timerTask;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(tag, "onCreate");
		
		init();
		 
		mediaButtonReceiver = new MediaButtonReceiver(this.handler);	
		if (this.headset_enable){
			registerMediaButtonReceiver();
		}
		
		configAutoQuitTime(this.autoQuitTime);
		
		timerTask = new TimerTask(this);
		timerTask.initParam(this.interval_starttime, this.interval_stoptime, this.interval_interval);
		timerTask.onEnableChange(this.interval_enable);
		
		showNotification(R.drawable.ic_launcher, "time master");
		
		
	}
	
	@Override
	public void onDestroy() {	
		super.onDestroy();
		Log.d(tag, "onDestroy");
		if (this.headset_enable){
			unregisterMediaButtonReceiver();
		}
		
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(MOOD_NOTIFICATIONS);
	}
	
	private void init(){
		this.alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		
		this.headset_way = sharedPreferences.getString(pref_key_headset_way, "double");
		this.headset_enable = sharedPreferences.getBoolean(pref_key_headset_enable, false);
		this.interval_enable = sharedPreferences.getBoolean(this.pref_key_interval_enable, false);
		this.interval_interval= Integer.parseInt(sharedPreferences.getString(pref_key_interval_interval, "10"));
		this.interval_starttime = sharedPreferences.getString(this.pref_key_interval_starttime, null);
		this.interval_stoptime = sharedPreferences.getString(this.pref_key_interval_stoptime, null);
		this.autoQuitTime = readParamAutoQuitTime(this.sharedPreferences);	
		
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);			
	}
	
	private void registerMediaButtonReceiver(){
		Log.d(tag, "registerReceiver media button");
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_BUTTON);  
		intentFilter.setPriority(100);
		//intentFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY - 1);
		registerReceiver(mediaButtonReceiver, intentFilter);		
	}
	
	private void unregisterMediaButtonReceiver(){
		Log.d(tag, "unregisterReceiver media button");
		unregisterReceiver(this.mediaButtonReceiver);
	}
	
	private class AutoQuitTimeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			TimerService.this.stopSelf();
		}
		
	}
	
	private String readParamAutoQuitTime(SharedPreferences sharedPreferences){
		String r = sharedPreferences.getString(pref_key_quit_time, null);
		if ("00:00".equalsIgnoreCase(r)){
			r = null;
		}
		return r;
	}
	
	private void configAutoQuitTime(String time){
		if (time == null){
			return;
		}
		
		Date date = null;
		try{
			date = new SimpleDateFormat("hh:mm").parse(time);
		}
		catch (Exception e) {
			Log.e(tag, "解析时间异常, time=" + time , e);
		}
		if (date == null){
			return;
		}
		
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, date.getHours());
		c.set(Calendar.MINUTE, date.getMinutes());		
		
		Intent intent = new Intent(this, AutoQuitTimeReceiver.class);		
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
	    this.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), 24*3600*1000, pendingIntent);
	}	
	
	private void cancelAutoQuitTime(){
		Intent intent = new Intent(this, AutoQuitTimeReceiver.class);  
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
	    this.alarmManager.cancel(pendingIntent);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(tag, "onStartCommand");
		if (intent.getBooleanExtra("trackball", false)){
			new PlayTask(this.getAssets()).execute();
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	final Handler handler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {	
			
			long now = System.currentTimeMillis();
			
			boolean validClick = false;
			if (TimerService.this.headset_way.equalsIgnoreCase("double")){
				if (now - lastHeadsetClickTime > headsetClickInterval){
					lastHeadsetClickTime = now;
					validClick = true;			
				}
			}
			else{
				validClick = true;
			}
			
			if (validClick && (now - lastPlayTime > playInterval)){
				lastPlayTime = now;
				new PlayTask(TimerService.this.getAssets()).execute();			
			}
		}
	};
	
    private void showNotification(int moodId, String text) {

    	Log.d(tag, "showNotification");
    	
        Notification notification = new Notification(moodId, null, System.currentTimeMillis());

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ControlActivity.class), 0);

        notification.setLatestEventInfo(this, "test", text, contentIntent);

        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(MOOD_NOTIFICATIONS, notification);
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		
		if (pref_key_headset_enable.equalsIgnoreCase(key)){
			boolean new_headset_enable = sharedPreferences.getBoolean(pref_key_headset_enable, false);
			if (new_headset_enable && !this.headset_enable){
				this.headset_enable = true;
				registerMediaButtonReceiver();
			}
			if (!new_headset_enable && this.headset_enable){
				unregisterMediaButtonReceiver();
				this.headset_enable = false;
			}			
		}
		else if (pref_key_headset_way.equalsIgnoreCase(key)){
			this.headset_way = sharedPreferences.getString(pref_key_headset_way, "double");	
		}
		else if (pref_key_quit_time.equalsIgnoreCase(key)){			
			cancelAutoQuitTime();
			this.autoQuitTime = readParamAutoQuitTime(sharedPreferences);
			configAutoQuitTime(this.autoQuitTime);
		}
		else if (pref_key_interval_enable.equalsIgnoreCase(key)){
			this.timerTask.onEnableChange(sharedPreferences.getBoolean(pref_key_interval_enable, false));
		}
		else if (pref_key_interval_interval.equalsIgnoreCase(key)){
			this.timerTask.onIntervalChange(sharedPreferences.getString(pref_key_interval_interval, "0"));
		}
		else if (pref_key_interval_starttime.equalsIgnoreCase(key)){
			this.timerTask.onStartTimeChange(sharedPreferences.getString(pref_key_interval_starttime, null));
		}
		else if (pref_key_interval_stoptime.equalsIgnoreCase(key)){
			this.timerTask.onStopTimeChange(sharedPreferences.getString(pref_key_interval_stoptime, null));
		}
	}


}
