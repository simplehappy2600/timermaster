package com.sh2600.timermaster.service;

import java.util.Calendar;

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
import com.sh2600.timermaster.common.CVal;
import com.sh2600.timermaster.common.ConfigParam;
import com.sh2600.timermaster.common.Utils;

public class TimerService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
	
	private static final String tag = TimerService.class.getSimpleName();
	
	private static int MOOD_NOTIFICATIONS = R.layout.activity_control;
	
	private AlarmManager alarmManager;
	
	private MediaButtonReceiver mediaButtonReceiver;
	
	private long lastPlayTime = 0;
	private int playInterval = 8*1000;
	
	private long lastHeadsetClickTime = 0;
	private int headsetClickInterval = 300;	
	
	private SharedPreferences sharedPreferences;
	
	private ConfigParam configParam;
	private TimerTask timerTask;	

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(tag, "onCreate");
		this.alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);		
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);		
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
		
		configParam = new ConfigParam(this);		
		timerTask = new TimerTask(this);
		 
		mediaButtonReceiver = new MediaButtonReceiver(this.handler);	
		if (configParam.headset_enable){
			registerMediaButtonReceiver();
		}
		
		configAutoQuitTime(configParam.autoQuitTime);		
		
		timerTask.initParam(configParam.interval_starttime, configParam.interval_stoptime, configParam.interval_interval);
		timerTask.onEnableChange(configParam.interval_enable);
		
		showNotification(R.drawable.ic_launcher, "time master");
	
	}
	
	@Override
	public void onDestroy() {	
		super.onDestroy();
		Log.d(tag, "onDestroy");
		if (configParam.headset_enable){
			unregisterMediaButtonReceiver();
		}
		
		cancelAutoQuitTime();
		timerTask.stop();
		
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(MOOD_NOTIFICATIONS);
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

	private void configAutoQuitTime(String time){
		
		Calendar c = this.timerTask.getAlarmTimeByDay(time);
		if (c == null){
			return;
		}
		
		Log.i(tag, "service will quit at " + c.getTime().toString());
		
		Intent intent = Utils.buildIntent(this, TimerService.class, CVal.Action.TimeAutoQuit);
		intent.putExtra(CVal.Cmd.cmdtype, CVal.Cmd.CMD_Quit);
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);		
	    this.alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), CVal.DayMs, pendingIntent);
	}	
	
	private void cancelAutoQuitTime(){
		Intent intent = Utils.buildIntent(this, TimerService.class, CVal.Action.TimeAutoQuit);		
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
	    this.alarmManager.cancel(pendingIntent);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(tag, "onStartCommand");
		
		int cmdType = intent.getIntExtra(CVal.Cmd.cmdtype, CVal.Cmd.CMD_None);
		if (cmdType != CVal.Cmd.CMD_None){
			handler.sendEmptyMessage(cmdType);
		}

		return super.onStartCommand(intent, flags, startId);
	}
	
	final Handler handler = new Handler(){
		
		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case CVal.Cmd.CMD_Quit:
				stopSelf();
				break;
			case CVal.Cmd.CMD_Play:
				new PlayTask(TimerService.this.getAssets()).execute();
				break;
			case CVal.Cmd.CMD_HeadsetClick:
				handle_heahset();
				break;
			case CVal.Cmd.CMD_StartInterval:
				timerTask.configInterval();
				break;
			case CVal.Cmd.CMD_StopInterval:
				timerTask.cancelInterval();
				break;
			case CVal.Cmd.CMD_IntervalInterval:
				handle_interval();
				break;
			default:
				break;
			}		

		}
	};
	
	private void handle_heahset(){
		long now = System.currentTimeMillis();
		
		boolean validClick = false;
		if (this.configParam.headset_way.equalsIgnoreCase("double")){
			if (now - lastHeadsetClickTime < headsetClickInterval){				
				validClick = true;
				lastHeadsetClickTime = 0;
			}
			else{
				lastHeadsetClickTime = now;
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
	
	private void handle_interval(){
		
		if (!this.configParam.interval_enable){
			this.timerTask.cancelInterval();
			return;
		}

		Calendar now = Calendar.getInstance();
		Calendar startTime = timerTask.getTime(configParam.interval_starttime);
		Calendar stopTime = timerTask.getTime(configParam.interval_stoptime);		
		
		boolean ok = false;
		if (startTime.before(stopTime)){
			ok = startTime.before(now) && now.before(stopTime);
		}
		else{
			ok = now.before(stopTime) || now.after(startTime);
		}
		
		if (ok){
			//play
			Log.d(tag, "timer task do " + now.getTime().toString());
			new PlayTask(TimerService.this.getAssets()).execute();
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
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		
		if (this.configParam.pref_key_headset_enable.equalsIgnoreCase(key)){
			boolean new_headset_enable = sharedPreferences.getBoolean(this.configParam.pref_key_headset_enable, false);
			if (new_headset_enable && !this.configParam.headset_enable){
				this.configParam.headset_enable = true;
				registerMediaButtonReceiver();
			}
			if (!new_headset_enable && this.configParam.headset_enable){
				unregisterMediaButtonReceiver();
				this.configParam.headset_enable = false;
			}			
		}
		else if (this.configParam.pref_key_headset_way.equalsIgnoreCase(key)){
			this.configParam.headset_way = sharedPreferences.getString(this.configParam.pref_key_headset_way, "double");	
		}
		else if (this.configParam.pref_key_quit_time.equalsIgnoreCase(key)){			
			cancelAutoQuitTime();
			configParam.autoQuitTime = configParam.readParamAutoQuitTime(sharedPreferences);
			configAutoQuitTime(configParam.autoQuitTime);
		}
		else if (this.configParam.pref_key_interval_enable.equalsIgnoreCase(key)){
			this.timerTask.onEnableChange(sharedPreferences.getBoolean(
					this.configParam.pref_key_interval_enable, false)
			);
		}
		else if (this.configParam.pref_key_interval_interval.equalsIgnoreCase(key)){
			this.timerTask.onIntervalChange(sharedPreferences.getString(
					this.configParam.pref_key_interval_interval, "0")
			);
		}
		else if (this.configParam.pref_key_interval_starttime.equalsIgnoreCase(key)){
			this.timerTask.onStartTimeChange(sharedPreferences.getString(this.configParam.pref_key_interval_starttime, null));
		}
		else if (this.configParam.pref_key_interval_stoptime.equalsIgnoreCase(key)){
			this.timerTask.onStopTimeChange(sharedPreferences.getString(this.configParam.pref_key_interval_stoptime, null));
		}
	}	


}
